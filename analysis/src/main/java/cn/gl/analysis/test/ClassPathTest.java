package cn.gl.analysis.test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClassPathTest {

    static String path = "html_repo/2018_12_22/0a0fc5d5-ae2f-4d5a-af7c-0ff4ae0f44a9";

    public static void main(String[] args) throws IOException {
        String html = Files.readString(Path.of(path));
        Document doc = Jsoup.parse(html);
        Elements links = doc.select("a");
        for (Element link : links) {
            var anchorText = link.text();
            var anchorLink = link.attr("href");
            System.out.println(anchorText + ":" + anchorLink);
        }
    }

}
