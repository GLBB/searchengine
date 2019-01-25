package cn.gl.searchengine.down;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Extracter {

    private static Extracter extracter = new Extracter();

    private Extracter(){}

    public static Extracter getInstance(){
        return extracter;
    }

    /**
     * 从html 中提取链接
     * @param html
     */
    public Set<String> extract(String uri, String html){
        String site = getsite(uri);
        String[] split = uri.split(":");
        String protocal = split[0];

        int i = uri.lastIndexOf("/");
        String parrent = uri.substring(0, i + 1);

        Document document = Jsoup.parse(html);
        Elements elements = document.select("a");

        HashSet<String> extractUris = new HashSet<>();

        for (Element alink : elements) {
            String href = alink.attr("href");
            if (href.startsWith("//")) {
                href = protocal + ":" + href;
                extractUris.add(href);
            } else if (href.startsWith("javascript:")) {

            } else if (href.startsWith("/")) {
                href = site + href;
                extractUris.add(href);
            } else if (!href.startsWith("http")) {
                href = parrent + href;
                extractUris.add(href);
            } else {
                if (href.startsWith("http")) {
                    extractUris.add(href);
                }
            }
        }
        return extractUris;
    }

    /**
     * 得到site
     * @param base
     * @return
     */
    public static String getsite(String base){
        String[] split = base.split("/");
        String site = split[0] + "//" + split[2];
        return site;
    }

}
