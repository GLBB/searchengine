package cn.gl.analysis.extract;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
public class ExtractHanlderTest {



    /**
     * 提取锚文本和正向链接
     */
    @Test
    public void test1() throws IOException {
        String path = "../html_repo/2018_12_22/0a0fc5d5-ae2f-4d5a-af7c-0ff4ae0f44a9";

        String html = Files.readString(Path.of(path));
        Document doc = Jsoup.parse(html);
        Elements links = doc.select("a");
        for (Element link : links) {
            var anchorText = link.text();
            var anchorLink = link.attr("href");
            System.out.println(anchorText + ":" + anchorLink);
        }
    }

    private void processLink(String base, String href){
        String site = getsite(base);
        String protocal = getProtocal(base);
        String parrent = getParent(base);

        if (href.startsWith("//")){
            href = protocal + ":" + href;
            System.out.println(href + "---");
        }else if (href.startsWith("javascript:")){

        }else if (href.startsWith("/")){
            href = site+href;
            System.out.println(href + "---");
        } else if (!href.startsWith("http")){
            href = parrent + href;
            System.out.println(href + "***");
        }else {
            if (!href.isEmpty()){
                System.out.println(href);
            }
        }
    }

    private String getsite(String base){
        String[] split = base.split("/");
        String site = split[0] + "//" + split[2];
        return site;
    }

    private String getProtocal(String base){
        String[] split = base.split(":");
        String protocal = split[0];
        return protocal;
    }

    private String getParent(String base){
        int i = base.lastIndexOf("/");
        String parrent = base.substring(0, i + 1);
        return parrent;
    }

    /**
     * 提取title
     */
    @Test
    public void test2() throws IOException {
        String path = "../html_repo/2018_12_22/d3a787e0-3449-437d-ad7c-19514b44ab07";
        String html = Files.readString(Path.of(path));
        Document doc = Jsoup.parse(html);
        Elements titleTag = doc.select("title");
        String title = titleTag.text();
        System.out.println(title);
    }

    /**
     * 正文提取
     */
    @Test
    public void test3() throws IOException {
        String path = "../html_repo/2018_12_22/0a1dd6bb-7404-405f-a67e-950b4b11e18e";
        String html = Files.readString(Path.of(path));
        Document doc = Jsoup.parse(html);

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

        StringBuilder sb = new StringBuilder();

        String[] lines = content.split("\n");
        for (int i = 0; i < lines.length; i++) {
            sb.append(lines[i] + ":" + charsLengthList.get(i) + "\n");
        }

        Files.writeString(Path.of("../test/0a1dd6bb-7404-405f-a67e-950b4b11e18e"), sb);
    }

    /**
     * 测试获得间断行
     */
    @Test
    public void test4() throws IOException {
        String path = "../html_repo/2018_12_22/0a1dd390-7b74-422d-99dd-0a96082d0c1c";
        String html = Files.readString(Path.of(path));
        Document doc = Jsoup.parse(html);

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

        StringBuilder sb = new StringBuilder();

        String[] lines = content.split("\n");
        for (int i = 0; i < lines.length; i++) {
            sb.append(lines[i] + ":" + charsLengthList.get(i) + "\n");
        }

        Files.writeString(Path.of("../test/0a0fc5d5-ae2f-4d5a-af7c-0ff4ae0f44a9"), sb);

        // 如果 keywords 和 description 存在，title那么就当作正文， 正文需要去掉 [image]
        List<AjacentZeroLine> ajacentZeroLineList = getAjacentZeroLineList(charsLengthList, 5);
        // ajacentZeroLineList 需要判断size 是否为0, 为0 就全文，或 keywords+desciption
        // 不为零才进行下面的计算
        if (ajacentZeroLineList.size() == 0) {
            System.out.println("无正文");
        }else {
            List<BreakPoint> breakPoint = getBreakPoint(ajacentZeroLineList, charsLengthList);
            BreakPoint maxScore = getMaxScore(breakPoint);
            String text = getText(maxScore, content);
            text = text.replaceAll("\n+", "\n");
            System.out.println(text);
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

/**
 * 5 行以上连续的0 才算 AjacentZeroLine
 */
class AjacentZeroLine{
    int start;
    int end;
    int interval;
}

class BreakPoint{
    AjacentZeroLine pre;
    AjacentZeroLine end;
    int fontN;
    int score;

    public BreakPoint(AjacentZeroLine pre, AjacentZeroLine end, int fontN, int score) {
        this.pre = pre;
        this.end = end;
        this.fontN = fontN;
        this.score = score;
    }
}
