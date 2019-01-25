package cn.gl.searchengine.down;

import org.dom4j.DocumentException;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SeedLoaderTest {

    @Test
    public void test1() throws DocumentException {
        SeedsLoader seedsLoader = new SeedsLoader();
        seedsLoader.xml2Seeds();
        seedsLoader.getSitemapURL();
        seedsLoader.loadSitemap();
        seedsLoader.insertToDispather();
        System.out.println();
    }

    /**
     * https://www.csdn.net/
     * http://www.importnew.com/
     * http://ifeve.com/
     * http://www.w3school.com.cn/
     */
    @Test
    public void test2(){
        String url = "https://www.csdn.net/";
        Pattern pattern = Pattern.compile("(//(\\w+\\.\\w+)(\\.)?)");
        Matcher matcher = pattern.matcher(url);
        while (matcher.find()) {
            System.out.println(matcher.group(2));
        }
    }
}
