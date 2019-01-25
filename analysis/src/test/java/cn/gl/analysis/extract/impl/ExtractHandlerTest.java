package cn.gl.analysis.extract.impl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;


public class ExtractHandlerTest {


    @Test
    public void test1() throws IOException {
        String uri = "https://ask.csdn.net/questions/676198";
        String path = "../html_repo/2018_12_22/0a0fc5d5-ae2f-4d5a-af7c-0ff4ae0f44a9";
        String html = Files.readString(Path.of(path));
        Document doc = Jsoup.parse(html);

        Map<String, String> anchorTextContentMap = getAnchorTextContent(uri, doc);
        System.out.println();
    }



    /**
     * 提取其中的锚文本和链接,
     * 链接作为 key
     * 锚文本作为value
     *
     * baseURI 该html 对应的uri
     * @param doc
     * @return
     */
    public Map<String, String> getAnchorTextContent(String baseURI, Document doc) {
        Map<String, String> anchorTextMap = new HashMap<>();

        String site = getsite(baseURI);
        String protocal = getProtocal(baseURI);
        String parrent = getParent(baseURI);
        Elements links = doc.select("a");
        for (Element link : links) {
            String href = link.attr("href");
            String realHref = getRealHref(href, site, protocal, parrent);
            String anchorText = link.text();
            String anchorTitle = link.attr("title");

            if (realHref !=null && !realHref.trim().equals("")){
                String description = "(" + anchorText+ ")-*-(" + anchorTitle + ")";
                anchorTextMap.put(realHref, description);
            }
        }
        return anchorTextMap;
    }

    private String getRealHref(String href, String site, String protocal, String parrent){
//        logger.info("origin: {}", href);
        if (href.startsWith("//")){
            href = protocal + ":" + href;
//            logger.info("real: {}", href);
            return href;
        }else if (href.startsWith("javascript:")){

        }else if (href.startsWith("/")){
            href = site + href;
            return href;
        } else if (!href.startsWith("http")){
            href = parrent + href;
            return href;
        }else {
            if (!href.isEmpty()){
                // 如果http 或https开头
                return href;
            }
        }
        return null;
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
     * 可以帮助正文提取的
     * title 标签
     * meta name = description 标签
     * meta name = keywords 标签
     * h1 标签
     * h2 标签
     * h3 标签
     *
     * 得到正文算法：
     * 从上到下判断每一行，如果一行字符数大于100，就算正文起始，加上前面5行，之后5行如果有字符数大于150个字符，就又开始
     * 如果没有字符数大于200的行，就取 description 和 keywords 作为正文
     *
     * @throws IOException
     */
    @Test
    public void test2() throws IOException {
        String path = "../html_repo/2018_12_22/0a1dd390-7b74-422d-99dd-0a96082d0c1c";
        String html = Files.readString(Path.of(path));
        Document doc = Jsoup.parse(html);

        String text = doc.text();

        System.out.println(text);

        Files.writeString(Path.of("../test/0a0fc5d5-ae2f-4d5a-af7c-0ff4ae0f44a9"), text);
    }

    /**
     * 将 html 去掉标签
     */
    @Test
    public void test3() throws IOException {
        String path = "../html_repo/2018_12_22/0a1dd390-7b74-422d-99dd-0a96082d0c1c";
        String html = Files.readString(Path.of(path));
        Document doc = Jsoup.parse(html);

        String title = doc.select("title").text();
        String description = doc.select("meta[name=description]").attr("content");
        String keywords = doc.select("meta[name=keywords]").attr("content");

        System.out.println("title: "+ title);
        System.out.println("description: "+description);
        System.out.println("keywords: " + keywords);


        doc.select("script").remove();
        doc.select("head").remove();
        doc.select("link").remove();
        doc.select("style").remove();
        doc.select("img").forEach(imgTag->{
            imgTag.replaceWith(new TextNode("[image]"));
        });
        String noComment = doc.toString().replaceAll("<!--(.+)-->", "");
        String clean = Jsoup.clean(noComment, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
        // 去除连续多行都少与20个字的

        List<Integer> charsLengthList = clean.lines()
                .map(line -> {
                    return line.trim().length();
                })
                .collect(Collectors.toList());

//        String denoising = processText(clean, charsLengthList);

        System.out.println(charsLengthList);
//        System.out.println("----------------------------------");
//        System.out.println(clean);
//        System.out.println("--------------------------------");
//        System.out.println(denoising);
//        getContentText(clean, charsLengthList);

        System.out.println(charsLengthList.get(100));

        StringBuilder sb = new StringBuilder();



        Files.writeString(Path.of("../test/0a0fc5d5-ae2f-4d5a-af7c-0ff4ae0f44a9"), clean);



        String text = getContentText(clean, charsLengthList);
        System.out.println("*****************");
        System.out.println(text);

    }
    
    private String processText(String content, List<Integer> charsLengthList){
        StringBuilder sb = new StringBuilder();
        String[] split = content.split("\n");
        for (int i = 0; i < split.length; i++) {
            Integer num = charsLengthList.get(i);
            if (num > 20) {
                sb.append(split[i]);
            }else {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private String getContentText(String content, List<Integer> charsLengthList){
        String[] split = content.split("\n");
        StringBuilder sb = new StringBuilder();
        boolean flag = false;
        for (int i = 0; i < split.length; i++) {
            // 得到包含当前行及下面 4 行的字符数 如果字符数大于180， 就认为是正文部分，将当前行加入结果集，继续判断加入结果集
            int count = getNext5RowN(i, charsLengthList);
            if (count > 180) {
                sb.append(split[i]);
                flag = true;
            }else {
                if (flag == true) {
                    break;
                }
            }
        }
        return sb.toString();
    }

    private int getNext5RowN(int rowIdx, List<Integer> charsLengthList){
        int n = 0;
        if (rowIdx < charsLengthList.size()) {
            for (int j = rowIdx; j < rowIdx+5; j++) {
                n += charsLengthList.get(j);
                if (j >= charsLengthList.size()) {
                    break;
                }
            }

        }
        return n;
    }

    private void decContent(String content, List<Integer> charsLengthList){
        String[] split = content.split("\n");
        for (int i = 0; i < split.length; i++) {

        }
    }

    /**
     * 可以帮助正文提取的
     * title 标签
     * meta name = description 标签
     * meta name = keywords 标签
     * h1 标签
     * h2 标签
     * h3 标签
     *
     * 得到正文算法：
     * 从上到下判断每一行，如果一行字符数大于100，就算正文起始，加上前面5行，之后5行如果有字符数大于150个字符，就又开始
     * 如果没有字符数大于200的行，就取 description 和 keywords 作为正文
     *
     * @throws IOException
     */
    @Test
    public void test4() throws IOException {
        String path = "../html_repo/2018_12_22/0a1dd390-7b74-422d-99dd-0a96082d0c1c";
        String html = Files.readString(Path.of(path));
        Document doc = Jsoup.parse(html);

        String title = doc.select("title").text();
        String description = doc.select("meta[name=description]").attr("content");
        String keywords = doc.select("meta[name=keywords]").attr("content");

        System.out.println("title: "+ title);
        System.out.println("description: "+description);
        System.out.println("keywords: " + keywords);


        doc.select("script").remove();
        doc.select("head").remove();
        doc.select("link").remove();
        doc.select("style").remove();
        doc.select("img").forEach(imgTag->{
            imgTag.replaceWith(new TextNode("[image]"));
        });
        String noComment = doc.toString().replaceAll("<!--(.+)-->", "");
        String clean = Jsoup.clean(noComment, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
        // 去除连续多行都少与20个字的

        List<Integer> charsLengthList = clean.lines()
                .map(line -> {
                    return line.trim().length();
                })
                .collect(Collectors.toList());


    }

    private void getText(String content, List<Integer> charsLengthList){
        String[] rows = content.split("\n");
        StringBuilder sb = new StringBuilder();
        boolean flag = false;
        for (int i = 0; i < charsLengthList.size(); i++) {
            Integer chars = charsLengthList.get(i);
            if (chars > 200) {
                flag = true;
                sb.append(rows[i]);
                // 判断后边5行有没有大于 20 字的行
                // 若有，得到大于 20 字符行的索引，将中间几行加入 stringbuider, 置i 值
//                int nextIdx = getIdxGreaterThan20(i+1, charsLengthList);
//                if (nextIdx > 0) {
//
//                }
            }

        }
    }

    private Integer getIdxGreaterThan20(int idx, List<Integer> charsLengthList){
        for (int i = idx; i < idx + 5; i++) {
            if (charsLengthList.get(i) > 20) {
                return i;
            }
        }
        return -1;
    }

}