package cn.gl.analysis.extract.impl;

import cn.gl.analysis.config.SpringContext;
import cn.gl.analysis.dto.ExtractContent;
import cn.gl.analysis.holder.FilepathURIHolder;
import cn.gl.analysis.load.Reader;
import cn.gl.analysis.load.impl.ReaderImpl;
import cn.gl.analysis.mapper.FilePageMapper;
import cn.gl.analysis.vo.FilepathURL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;


public class ExtractHandler implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(ExtractHandler.class);
    private ReaderImpl reader;

//    @Autowired
//    private FilePageMapper filePageMapper;

//    private static HashMap<String, String> filepathURLMap;

    /**
     * 需要判断是否是初始化过的对象..
     */
    private FilepathURIHolder holder = SpringContext.getContext().getBean(FilepathURIHolder.class);

    private ArrayList<ExtractContent> extractContents = new ArrayList<>();

    /**
     * 标志是否处理结束
     */
    private volatile boolean handlerOver = false;

    /**
     * 提取正文
     */
    ExtractText extractText = SpringContext.getContext().getBean(ExtractText.class);

    /**
     * 计算是否重复
     */
    DuplicateCheck duplicateCheck = SpringContext.getContext().getBean(DuplicateCheck.class);

//    static {
//        Set<FilepathURL> allFilepathURL = filePageMapper.getAllFilepathURL();
//        Iterator<FilepathURL> iterator = allFilepathURL.iterator();
//        while (iterator.hasNext()) {
//            FilepathURL filepathURL = iterator.next();
//            String filepath = filepathURL.getFilepath();
//            String url = filepathURL.getUrl();
//            if (filepath != null && url != null) {
//                filepathURLMap.put(filepath, url);
//            }else {
//                logger.warn("filepath: {}, url: {}", filepath, url);
//            }
//        }
//    }

//    private HashMap<String, String> anchorTextContent = new HashMap<>();
//
//    private HashMap<String, String>

    @Override
    public void run() {
        // 检测reader 是否读取完毕
        // 若读取完毕，就开始处理，否则等待
        logger.info("等待reader读文件完毕");
        while (!reader.isOver()) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        logger.info("hanlder 开始处理");
        // 从pathContentMap 中提取信息
        HashMap<String, String> pathContentMap = reader.getPathContentMap();
        extractPathsContentMap(pathContentMap);
        handlerOver = true;
        logger.info("handler 处理完毕");
    }

    private void extractPathsContentMap(Map<String, String> pathContentMap){
        logger.info("holder: {}", holder);

        boolean init = holder.isInit();
        if (! init) {
            holder.init();
        }
        Iterator<String> iterator = pathContentMap.keySet().iterator();
        while (iterator.hasNext()) {
            String path = iterator.next();
            String html = pathContentMap.get(path);
            ExtractContent extractContent = extractPathContentMap(path, html);
//            boolean check = duplicateCheck.check(extractContent.getText());
//            if (check) {
//                extractContents.add(extractContent);
//            }else {
//                logger.info("重复 url: {}", extractContent.getUri());
//            }

            if (extractContent != null) {
                extractContents.add(extractContent);
            }
        }
    }

    private ExtractContent extractPathContentMap(String path, String html){
        ExtractContent extractContent = new ExtractContent();
        Document doc = Jsoup.parse(html);
        boolean check = duplicateCheck.check(doc.text());
        if (check) {
            // 得到uri
            String uri = holder.getFilepathURLMap().get(path);

            extractContent.setUri(uri);
            // 设置文件路径
            extractContent.setFileName(path);
            // 设置 title
            String title = getTitle(doc);
            extractContent.setTitle(title);
            // 设置anchorTextContent
            Map<String, String> anchorTextContent = getAnchorTextContent(uri, doc);
            extractContent.setAnchorTextContent(anchorTextContent);
            // 设置 anchar
            Set<String> anchors = anchorTextContent.keySet();
            extractContent.setAnchor(new HashSet<>(anchors));
            // 设置 description
            String discription = getDiscription(doc);
            extractContent.setDescription(discription);
            // 设置 keywords
            String keywords = getKeywords(doc);
            extractContent.setKeywors(keywords);
            // 设置 text 正文
//        String text = getText(doc);
            String text = extractText.getText(doc, title, discription, keywords);
            extractContent.setText(text);
            return extractContent;
        }else {
            return null;
        }


    }

    /**
     * 提取 html 中 titlt 标签中的内容
     * @param doc
     * @return
     */
    private String getTitle(Document doc){
        Elements titleTag = doc.select("title");
        String title = titleTag.text();
        return title;
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
     * 得到正文
     * @return
     */
    private String getText(Document doc){
        return doc.text();
    }

    /**
     * 得到keywords
     * @return
     */
    private String getKeywords(Document doc){
        return doc.select("meta[name=keywords]").attr("content");
    }

    /**
     * 得到description
     * @return
     */
    private String getDiscription(Document doc){
        return doc.select("meta[name=description]").attr("content");
    }


    public Reader getReader() {
        return reader;
    }

    public void setReader(ReaderImpl reader) {
        this.reader = reader;
    }

    public boolean isHandlerOver() {
        return handlerOver;
    }

    public ArrayList<ExtractContent> getExtractContents() {
        return extractContents;
    }
}
