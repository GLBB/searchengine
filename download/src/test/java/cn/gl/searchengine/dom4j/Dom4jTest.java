package cn.gl.searchengine.dom4j;

import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Dom4jTest {

    /**
     * 测试从文件系统加载
     * @throws DocumentException
     */
    @Test
    public void test1() throws DocumentException {
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read("src/main/resources/seed.xml");

        List<Node> list = document.selectNodes("/seeds/seed");
        for (Node seed : list) {
            String site = seed.selectSingleNode("site").getStringValue();

            System.out.println(site);

            Node sitemap = seed.selectSingleNode("sitemap");
            if (sitemap != null) {

                System.out.println(sitemap.getStringValue());
            }
        }
    }

    String xml = "<urlset><url><loc>https://blog.csdn.net</loc><lastmod>2018-12-15</lastmod><changefreq>weekly</changefreq><priority>0.8</priority></url><url><loc>https://blog.csdn.net/mobile/newarticle.html</loc><lastmod>2018-12-15</lastmod><changefreq>weekly</changefreq><priority>0.8</priority></url><url><loc>https://blog.csdn.net/web/newarticle.html</loc><lastmod>2018-12-15</lastmod><changefreq>weekly</changefreq><priority>0.8</priority></url><url><loc>https://blog.csdn.net/enterprise/newarticle.html</loc><lastmod>2018-12-15</lastmod><changefreq>weekly</changefreq><priority>0.8</priority></url><url><loc>https://blog.csdn.net/code/newarticle.html</loc><lastmod>2018-12-15</lastmod><changefreq>weekly</changefreq><priority>0.8</priority></url><url><loc>https://blog.csdn.net/www/newarticle.html</loc><lastmod>2018-12-15</lastmod><changefreq>weekly</changefreq><priority>0.8</priority></url><url><loc>https://blog.csdn.net/database/newarticle.html</loc><lastmod>2018-12-15</lastmod><changefreq>weekly</changefreq><priority>0.8</priority></url><url><loc>https://blog.csdn.net/system/newarticle.html</loc><lastmod>2018-12-15</lastmod><changefreq>weekly</changefreq><priority>0.8</priority></url><url><loc>https://blog.csdn.net/cloud/newarticle.html</loc><lastmod>2018-12-15</lastmod><changefreq>weekly</changefreq><priority>0.8</priority></url><url><loc>https://blog.csdn.net/software/newarticle.html</loc><lastmod>2018-12-15</lastmod><changefreq>weekly</changefreq><priority>0.8</priority></url><url><loc>https://blog.csdn.net/other/newarticle.html</loc><lastmod>2018-12-15</lastmod><changefreq>weekly</changefreq><priority>0.8</priority></url><url><loc>https://blog.csdn.net/mobile/hotarticle.html</loc><lastmod>2018-12-15</lastmod><changefreq>weekly</changefreq><priority>0.8</priority></url><url><loc>https://blog.csdn.net/web/hotarticle.html</loc><lastmod>2018-12-15</lastmod><changefreq>weekly</changefreq><priority>0.8</priority></url><url><loc>https://blog.csdn.net/enterprise/hotarticle.html</loc><lastmod>2018-12-15</lastmod><changefreq>weekly</changefreq><priority>0.8</priority></url><url><loc>https://blog.csdn.net/code/hotarticle.html</loc><lastmod>2018-12-15</lastmod><changefreq>weekly</changefreq><priority>0.8</priority></url><url><loc>https://blog.csdn.net/www/hotarticle.html</loc><lastmod>2018-12-15</lastmod><changefreq>weekly</changefreq><priority>0.8</priority></url><url><loc>https://blog.csdn.net/database/hotarticle.html</loc><lastmod>2018-12-15</lastmod><changefreq>weekly</changefreq><priority>0.8</priority></url><url><loc>https://blog.csdn.net/system/hotarticle.html</loc><lastmod>2018-12-15</lastmod><changefreq>weekly</changefreq><priority>0.8</priority></url><url><loc>https://blog.csdn.net/cloud/hotarticle.html</loc><lastmod>2018-12-15</lastmod><changefreq>weekly</changefreq><priority>0.8</priority></url><url><loc>https://blog.csdn.net/software/hotarticle.html</loc><lastmod>2018-12-15</lastmod><changefreq>weekly</changefreq><priority>0.8</priority></url><url><loc>https://blog.csdn.net/other/hotarticle.html</loc><lastmod>2018-12-15</lastmod><changefreq>weekly</changefreq><priority>0.8</priority></url><url><loc>https://blog.csdn.net/column.html</loc><lastmod>2018-12-15</lastmod><changefreq>weekly</changefreq><priority>0.8</priority></url><url><loc>https://blog.csdn.net/mobile/column.html</loc><lastmod>2018-12-15</lastmod><changefreq>weekly</changefreq><priority>0.8</priority></url><url><loc>https://blog.csdn.net/web/column.html</loc><lastmod>2018-12-15</lastmod><changefreq>weekly</changefreq><priority>0.8</priority></url><url><loc>https://blog.csdn.net/enterprise/column.html</loc><lastmod>2018-12-15</lastmod><changefreq>weekly</changefreq><priority>0.8</priority></url><url><loc>https://blog.csdn.net/code/column.html</loc><lastmod>2018-12-15</lastmod><changefreq>weekly</changefreq><priority>0.8</priority></url><url><loc>https://blog.csdn.net/www/column.html</loc><lastmod>2018-12-15</lastmod><changefreq>weekly</changefreq><priority>0.8</priority></url><url><loc>https://blog.csdn.net/database/column.html</loc><lastmod>2018-12-15</lastmod><changefreq>weekly</changefreq><priority>0.8</priority></url><url><loc>https://blog.csdn.net/system/column.html</loc><lastmod>2018-12-15</lastmod><changefreq>weekly</changefreq><priority>0.8</priority></url><url><loc>https://blog.csdn.net/cloud/column.html</loc><lastmod>2018-12-15</lastmod><changefreq>weekly</changefreq><priority>0.8</priority></url><url><loc>https://blog.csdn.net/software/column.html</loc><lastmod>2018-12-15</lastmod><changefreq>weekly</changefreq><priority>0.8</priority></url><url><loc>https://blog.csdn.net/other/column.html</loc><lastmod>2018-12-15</lastmod><changefreq>weekly</changefreq><priority>0.8</priority></url><url><loc>https://blog.csdn.net/PK.html</loc><lastmod>2018-12-15</lastmod><changefreq>weekly</changefreq><priority>0.8</priority></url><url><loc>https://blog.csdn.net/pk/list.html</loc><lastmod>2018-12-15</lastmod><changefreq>weekly</changefreq><priority>0.8</priority></url></urlset>";

    /**
     * 从字符串获得document 对象
     */
    @Test
    public void test2() throws DocumentException {

        Document document = DocumentHelper.parseText(xml);
        List<Node> locNodeList = document.selectNodes("//loc");
        for (Node locNode : locNodeList) {
            System.out.println(locNode.getStringValue());
        }

    }

    @Test
    public void test3(){
        String url = "http://www.w3school.com.cn/";
        String s = url.replaceAll("[\\.|/|:]", "");
        System.out.println(s);
    }

    @Test
    public void test4(){
        File file = new File("pony/jack/kk/");
        file.mkdir();

    }

    
    @Test
    public void test5() throws DocumentException, IOException {
        FileReader fr = new FileReader("sitemap.xml");
        BufferedReader br = new BufferedReader(fr);
        List<String> strList = br.lines().collect(Collectors.toList());
        String xml2 = "";
        for (String s : strList) {
            xml2 += s + "\n";
        }
        br.close();
        Document document = DocumentHelper.parseText(xml2);
        List<Node> locNodeList = document.selectNodes("./urlset/url");
        for (Node locNode : locNodeList) {
            System.out.println(locNode.getStringValue());
        }
    }




    @Test
    public void test6(){
        try(FileInputStream fis = new FileInputStream("sitemap.xml")) {
            byte[] bytes = fis.readAllBytes();
            String content = new String(bytes, "UTF-8");
            Document document = DocumentHelper.parseText(content);
            List<Node> locNodeList = document.selectNodes("//loc");
            for (Node locNode : locNodeList) {
                System.out.println(locNode.getStringValue());
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test7() throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read("sitemap.xml");
        Element rootElement = document.getRootElement();
        Iterator iterator = rootElement.nodeIterator();
        while (iterator.hasNext()) {
            Node node = (Node) iterator.next();
            if (node instanceof Element) {
                Element element = (Element) node;
                Element loc = element.element("loc");
                System.out.println(loc.getStringValue());
            }
        }
    }

    @Test
    public void test8() throws IOException, DocumentException {
        FileReader fr = new FileReader("sitemap.xml");
        BufferedReader br = new BufferedReader(fr);
        List<String> strList = br.lines().collect(Collectors.toList());
        String xml2 = "";
        for (String s : strList) {
            xml2 += s + "\n";
        }
        br.close();
        Document document = DocumentHelper.parseText(xml2);

        Element rootElement = document.getRootElement();
        Iterator iterator = rootElement.nodeIterator();
        while (iterator.hasNext()) {
            Node node = (Node) iterator.next();
            if (node instanceof Element) {
                Element element = (Element) node;
                Element loc = element.element("loc");
                System.out.println(loc.getStringValue());
            }
        }
    }
}
