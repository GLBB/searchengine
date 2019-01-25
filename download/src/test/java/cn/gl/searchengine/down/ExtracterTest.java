package cn.gl.searchengine.down;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

public class ExtracterTest {
    Extracter extracter = Extracter.getInstance();

    @Test
    public void test0(){
//        extracter.extract("https://www.ibm.com/developerworks/cn/", )
    }


    @Test
    public void test1() throws IOException {
        String base = "https://www.ibm.com/developerworks/cn/";
        String site = getsite(base);
        String[] split = base.split(":");
        String protocal = split[0];

        int i = base.lastIndexOf("/");
        String parrent = base.substring(0, i + 1);

        try(FileInputStream fis = new FileInputStream("html_repo/2018_12_20/1db6c035-ca2d-4521-b12f-abc3351b8536")){
            byte[] bytes = fis.readAllBytes();
            String html = new String(bytes, "UTF-8");
            Document document = Jsoup.parse(html);
            Elements elements = document.select("a");
            for (Element alink : elements) {
//                System.out.println(alink.tagName() +" :" + alink.attr("href") + " "+ alink.attr("rel"));
                String href = alink.attr("href");
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
        }

    }

    private String getsite(String base){
        String[] split = base.split("/");
        String site = split[0] + "//" + split[2];
        return site;
    }

    @Test
    public void test2(){
        String base = "http://www.runoob.com/";
        String[] split = base.split("/");
        System.out.println(Arrays.toString(split));
        String site = split[0] + "//" + split[2];
        System.out.println(site);
    }

    @Test
    public void test3(){
        String base = "http://www.runoob.com/";
        int i = base.lastIndexOf("/");
        String substring = base.substring(0, i + 1);
        System.out.println(substring);
    }

}