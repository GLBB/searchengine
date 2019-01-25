package cn.gl.searchengine.down;

import cn.gl.searchengine.dto.Seed;
import cn.gl.searchengine.util.DateUtil;
import org.dom4j.*;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import static cn.gl.searchengine.dto.Seed.Mutiple.NO;

public class SeedsLoader {

    /**
     * 种子站点路径
     */
    private static final String XMLLOCATION = "src/main/resources/seed.xml";

    /**
     * 种子站点
     */
    private List<Seed> seeds = new ArrayList<>();

    /**
     * key site , value sitemap 对应的url
     */
    private HashMap<String, String> seedMap = new HashMap<>();

    /**
     * 以 sitemap 为 key, 以 包含的 html 结尾的为 value
     */
    private HashMap<String, List<String>> seedsitemapURLs = new HashMap<>();

    /**
     * 依赖gather 下载xml
     */
    Gather gather = new Gather();

    /**
     * 保存sitemap 文件到 文件系统
     */
    private String rootDirec = "sitemap";

    /**
     * 从 xml 加载 seed 节点
     * @throws DocumentException
     */
    public void xml2Seeds() throws DocumentException {
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read("src/main/resources/seed.xml");
        List<Node> list = document.selectNodes("/seeds/seed");
        for (Node seed : list){
            String site = seed.selectSingleNode("site").getStringValue();
            String sitemap = null;
            Node sitemapNode = seed.selectSingleNode("sitemap");
            if (sitemapNode != null) {
                sitemap = sitemapNode.getStringValue();
            }

            Seed temp = new Seed(site, sitemap);

            Node includeNode = seed.selectSingleNode("include");
            if (includeNode != null) {
                String include = includeNode.getStringValue();
                if (include.equals("yes")){
                    temp.setInclude(Seed.Mutiple.YES);
                }
            }

            seeds.add(temp);
        }
    }


    /**
     * 将种子站点的的sitemap 加载到 成员变量 seedMap 中
     */
    public void getSitemapURL(){
        for (Seed seed : seeds) {
            String base = seed.getUrl();
            try {
                String robotstxt = gather.down(base + "robots.txt");
                if (robotstxt == null) {
                    continue;
                }
                // 提取sitemap 链接, sitemap 可能有多个 , 暂时就找一个
                robotstxt = robotstxt.toLowerCase();
                String sitemap = robotstxt.lines().filter(line -> line.contains("sitemap")).findFirst().get();
                Pattern pattern = Pattern.compile("http[\\s|\\S]+[txt|gz|xml]");
                Matcher matcher = pattern.matcher(sitemap);
                String sitemapURL = null;
                if (matcher.find()) {
                    sitemapURL = matcher.group();
                }
                if (sitemapURL == null) {
                    continue;
                }
                seedMap.put(base, sitemapURL);
            }catch (NoSuchElementException e){
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 加载sitemap 中的 url 到成员变量 seedsitemapURLs 中
     */
    public void loadSitemap(){
        Set<String> keys = seedMap.keySet();
        for (String key : keys) {
            String siteMapURL = seedMap.get(key);
            String sitemapContent = null;
            // 不能直接下载
            if (!siteMapURL.endsWith("gz")){
                try {
                    sitemapContent = gather.down(siteMapURL);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else {
                try {
                    sitemapContent = getGZcontent(siteMapURL);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (sitemapContent == null) {
                continue;
            }
            resolveSitemap(siteMapURL, siteMapURL, sitemapContent);
            System.out.println();
        }
    }

    /**
     * 为 loadSitemap 服务
     * 如果是 txt 文件, 那么每一行是一个URL
     * 如果是 xml ， 那么 提取全部loc 标签, 判断loc 标签中的是以 html 结尾，还是txt结尾, 还是 xml 结尾， 还是 gz 结尾, 重复这个过程
     * 如果是 gz 结尾，判断 gz 的前缀， 如 1.xml.gz, 如果是xml 做第二步， 如果是 txt 做第一步
     */
    private void resolveSitemap(String seedsitemapURL,String sitemapURL ,String sitemapContent) {
        if (sitemapURL.endsWith("txt")){
            ArrayList<String> urls = new ArrayList<>();
            sitemapContent.lines().forEach(line -> urls.add(line));
            seedsitemapURLs.put(seedsitemapURL, urls);
        }else if (sitemapURL.endsWith("xml") || sitemapURL.endsWith("gz")) {
            Document document = null;
            try {
                document = DocumentHelper.parseText(sitemapContent);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
            if (document == null) {
                return;
            }

            Element rootElement = document.getRootElement();
            Iterator iterator = rootElement.nodeIterator();
            while (iterator.hasNext()) {
                Node node = (Node) iterator.next();
                if (node instanceof Element) {
                    Element element = (Element) node;
                    Element locElement = element.element("loc");
                    String locValue = locElement.getStringValue();
                    if (!locValue.endsWith("xml") || !locValue.endsWith("gz")) {
                        List<String> list = seedsitemapURLs.get(seedsitemapURL);
                        if (list == null) {
                            list = new ArrayList<>();
                            seedsitemapURLs.put(seedsitemapURL, list);
                        }
                        list.add(locValue);
                    }else {
                        String content = null;
                        if (!locValue.endsWith("gz")){
                            try {
                                content = gather.down(locValue);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }else {
                            try {
                                content = getGZcontent(locValue);
                                String[] split = locValue.split(".gz");
                                locValue = split[0];
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        if (content == null) {
                            return;
                        }
                        resolveSitemap(seedsitemapURL, locValue, content);
                    }
                }
            }
        }
    }

    /**
     * 插入 dispather 中
     */
    public void insertToDispather(){
//        Set<String> keys = seedsitemapURLs.keySet();
//        for (String key : keys) {
//            List<String> list = seedsitemapURLs.get(key);
//
//
//        }
        for (Seed seed: seeds) {
            HashSet<String> set = new HashSet<>();
            set.add(seed.getUrl());
            Dispather.ungrasp.put(seed.getUrl(), set);
        }
        Set<String> keys = seedsitemapURLs.keySet();
        for (String key : keys) {
            List<String> list = seedsitemapURLs.get(key);
            String keyName = seedGetName(key);
            // 去重
            Set<String> set = new HashSet<>(list);
            List<String> noReapt = new ArrayList<>(set);
            for (Seed seed: seeds) {
                String seedUrl = seed.getUrl();
                String seedName = seedGetName(seedUrl);
                if (seedName.equals(keyName)) {
                    Dispather.ungrasp.get(seed.getUrl()).addAll(set);
                }
            }

        }
    }

    /**
     * 下载 gz 压缩包并解析内容
     * @param url
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    private String getGZcontent(String url) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
        InputStream in = new GZIPInputStream(response.body());
        byte[] bytes = in.readAllBytes();
        String content = new String(bytes, "UTF-8");
        return content;
    }



    private void write2file(String xml, Seed seed){
//        String dateString = DateUtil.getDateString();
//        String siteName = seedGetName(seed);
//        File file = new File(rootDirec+"/" + dateString + "/" + siteName);
//        file.mkdirs();
    }

    /**
     * 得到种子站点的 一个 url 标识符
     * @param sitemapURL
     * @return
     */
    private String seedGetName(String sitemapURL){
        Pattern pattern = Pattern.compile("(//(\\w+\\.\\w+)(\\.)?)");
        Matcher matcher = pattern.matcher(sitemapURL);
        while (matcher.find()) {
            return matcher.group(2);
        }
        System.out.println("seedGetName: " + "正则表达式为成功匹配");
        return null;
    }



}
