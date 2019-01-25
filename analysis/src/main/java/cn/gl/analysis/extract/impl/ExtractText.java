package cn.gl.analysis.extract.impl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.TextNode;
import org.jsoup.safety.Whitelist;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExtractText {

    /**
     * 得到正文, 会修改Document 对象
     * @return
     */
    public String getText(Document doc, String title, String description, String keywords){
        doc.select("script").remove();
        doc.select("head").remove();
        doc.select("link").remove();
        doc.select("style").remove();
        doc.select("img").forEach(imgTag->{
            imgTag.replaceWith(new TextNode("[image]"));
        });
        String noComment = doc.toString().replaceAll("<!--(.+)-->", "");
        String content = Jsoup.clean(noComment, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));

        List<Integer> charsLengthList = content.lines()
                .map(line -> {
                    return line.trim().length();
                })
                .collect(Collectors.toList());

        // 如果 keywords 和 description 存在，title那么就当作正文， 正文需要去掉 [image], 去掉空行
        List<AjacentZeroLine> ajacentZeroLineList = getAjacentZeroLineList(charsLengthList, 5);
        // ajacentZeroLineList 需要判断size 是否为0, 为0 就全文，或 keywords+desciption
        // 不为零才进行下面的计算
        if (ajacentZeroLineList.size() == 0) {
            StringBuilder sb = new StringBuilder();
            if (title != null){
                sb.append(title.trim());
            }
            if (description != null) {
                sb.append(description.trim());
            }
            if (keywords != null) {
                sb.append(keywords.trim());
            }
            return sb.toString();
        }else {
            List<BreakPoint> breakPoint = getBreakPoint(ajacentZeroLineList, charsLengthList);
            BreakPoint maxScore = getMaxScore(breakPoint);
            String text = getText(maxScore, content);
            text = text.replaceAll("\n", " ");
            text = text.replaceAll("\\[image\\]", " ");
            text = text.replaceAll(" +", " ");
            return text;
        }
    }

    /**
     * 得到相邻的空行
     * @param charsLine
     * @param adacent
     * @return
     */
    private List<AjacentZeroLine> getAjacentZeroLineList(List<Integer> charsLine, int adacent){
        List<AjacentZeroLine> ajctZeroLineList = new ArrayList<>();
        int i=0;
        while (i < charsLine.size() - adacent) {
            // 判断当前行是否是 0，若是执行下面
            if (charsLine.get(i)  == 0) {
                // 判断是否有10 行连续的 0
                if (condition(i, charsLine, adacent)){
                    // 若有
                    // 创建一个AjacentZeroLine 对象
                    AjacentZeroLine ajacentZeroLine = new AjacentZeroLine();
                    // start = 当前i 的值， end = 得到相邻最下面的一个索引
                    ajacentZeroLine.start = i;
                    ajacentZeroLine.end = end(i+ adacent, charsLine);
                    ajacentZeroLine.interval = ajacentZeroLine.end - ajacentZeroLine.start + 1;
                    ajctZeroLineList.add(ajacentZeroLine);
                    // 将 i 值 赋值为 end+1
                    i = ajacentZeroLine.end + 1;
                }else {
                    // 若没有
                    // i=i+1
                    i++;
                }
            }else {
                i++;
            }
        }
        return ajctZeroLineList;
    }

    private boolean condition(int i, List<Integer> charsLine, int ajacent){
        int end = i + ajacent;
        while (i < end) {
            if (charsLine.get(i) != 0) {
                return false;
            }
            i++;
        }
        return true;
    }

    private int end(int i, List<Integer> charsLine){
        for (int j = i; j < charsLine.size(); j++) {
            if (charsLine.get(j) != 0) {
                return j-1;
            }
        }
        return charsLine.size();
    }

    /**
     * 得到间断点
     */
    private List<BreakPoint> getBreakPoint(List<AjacentZeroLine> ajctZeroLineList, List<Integer> charsLine){
        List<BreakPoint> list = new ArrayList<>();
        // 统计开始到一个间断点的情况
        AjacentZeroLine ajacentZeroLineStart = ajctZeroLineList.get(0);
        int startFontN = getFontN(0, ajacentZeroLineStart.start, charsLine);
        int startScore = getScore(1, ajacentZeroLineStart.interval, startFontN);
        BreakPoint startBreakPoint = new BreakPoint(null, ajacentZeroLineStart, startFontN, startScore);
        list.add(startBreakPoint);

        // 统计间断点之间的情况
        for (int i = 0; i < ajctZeroLineList.size() - 1; i++) {
            AjacentZeroLine ajacentZeroLine0 = ajctZeroLineList.get(i);
            AjacentZeroLine ajacentZeroLine1 = ajctZeroLineList.get(i + 1);
            int fontN = getFontN(ajacentZeroLine0.end, ajacentZeroLine1.start, charsLine);
            int score = getScore(ajacentZeroLine0.interval, ajacentZeroLine1.interval, fontN);
            BreakPoint breakPoint = new BreakPoint(ajacentZeroLine0, ajacentZeroLine1, fontN, score);
            list.add(breakPoint);
        }

        // 统计最后一个间断点到结束的情况
        AjacentZeroLine ajacentZeroLineEnd = ajctZeroLineList.get(ajctZeroLineList.size() -1);
        int endFontN = getFontN(ajacentZeroLineEnd.end, charsLine.size(), charsLine);
        int endScore = getScore(ajacentZeroLineEnd.interval, 1, endFontN);
        BreakPoint endBreakPoint = new BreakPoint(ajacentZeroLineEnd, null, endFontN, endScore);
        list.add(endBreakPoint);
        return list;
    }

    private int getFontN(int start, int end, List<Integer> charsLine){
        int count = 0;
        for (int i = start; i < end; i++) {
            count += charsLine.get(i);
        }
        return count;
    }

    // 计算score 可以用占用行数，而不用间隔数
    private int getScore(int preInterval, int endInterval, int fontN){
        return preInterval*endInterval + fontN;
    }

    // 得到最大的breakpoint
    private BreakPoint getMaxScore(List<BreakPoint> list){
        BreakPoint max = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i).score > max.score) {
                max = list.get(i);
            }
        }
        return max;
    }

    // 得到间断点之间的正文
    private String getText(BreakPoint breakPoint, String content){
        StringBuilder sb = new StringBuilder();
        String[] lines = content.split("\n");
        if (breakPoint.pre == null) {
            for (int i = 0; i < breakPoint.end.start; i++) {
                sb.append(lines[i] + "\n");
            }
        }else if (breakPoint.end == null) {
            for (int i = breakPoint.pre.end; i < lines.length; i++) {
                sb.append(lines[i] + "\n");
            }

        }else {
            for (int i = breakPoint.pre.end; i < breakPoint.end.start; i++) {
                sb.append(lines[i] + "\n");
            }
        }
        return sb.toString();
    }

}
