package cn.gl.searchengine;

import org.junit.Test;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RobotsTxt {
    @Test
    public void test1(){
        String robotstxt = "User-agent: *\n" +
                "Disallow: /wp-admin/\n" +
                "Disallow: /wp-content/\n" +
                "Disallow: /wp-includes/\n" +
                "Disallow: /wp-\n" +
                "Disallow: /xmlrpc.php\n" +
                "Disallow: /trackback\n" +
                "Disallow: /*/trackback\n" +
                "Disallow: /feed\n" +
                "Disallow: /*/feed\n" +
                "Disallow: /comments/feed\n" +
                "Disallow: /?s=*\n" +
                "Disallow: /*/?s=*\n" +
                "Disallow: /search/*\n" +
                "Disallow: /*/search/*\n" +
                "Disallow: /?r=*\n" +
                "Disallow: /*.jpg$\n" +
                "Disallow: /*.jpeg$\n" +
                "Disallow: /*.gif$\n" +
                "Disallow: /*.png$\n" +
                "Disallow: /*.bmp$\n" +
                "Disallow: /?p=*\n" +
                "Disallow: /*/comment-page-*\n" +
                "Disallow: /*?replytocom*\n" +
                "Disallow: /a/date/\n" +
                "Disallow: /a/author/\n" +
                "Disallow: /a/category/\n" +
                "Disallow: /?p=*&amp;preview=true\n" +
                "Disallow: /?page_id=*&amp;preview=true\n" +
                "Disallow: /wp-login.php\n" +
                "Sitemap: http://www.importnew.com/sitemap.xml.gz";
        robotstxt = robotstxt.toLowerCase();
        String sitemap = robotstxt.lines().filter(line -> line.contains("sitemap")).findFirst().get();
        Pattern pattern = Pattern.compile("http[\\s|\\S]+[txt|gz|xml]");
        Matcher matcher = pattern.matcher(sitemap);
        while (matcher.find()) {
            System.out.println(matcher.group());
        }
    }

    @Test
    public void test2(){
        String robotstxt = "# See http://www.robotstxt.org/wc/norobots.html for documentation on how to use the robots.txt file\n" +
                "#\n" +
                "# To ban all spiders from the entire site uncomment the next two lines:\n" +
                "# User-Agent: *\n" +
                "# Disallow: /";
        robotstxt = robotstxt.toLowerCase();
        String sitemap = robotstxt.lines().filter(line -> line.contains("sitemap")).findAny().get();
        Pattern pattern = Pattern.compile("http[\\s|\\S]+[txt|gz|xml]");
        Matcher matcher = pattern.matcher(sitemap);
        while (matcher.find()) {
            System.out.println(matcher.group());
        }
    }

    @Test
    public void test3(){
        URI uri = URI.create("https://start.firefoxchina.cn/");
        System.out.println(uri.toString());
    }
}
