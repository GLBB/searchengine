package cn.gl.searchengine.down;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

public class URITest {
//    http://www.csdn.net/tag/sharepoint 2013
//    http://www.csdn.net/tag/尹华山
//    http://www.csdn.net/tag/中国互联网安全大会
    @Test
    public static void main(String[] args) throws UnsupportedEncodingException {
        var str = "http://www.csdn.net/tag/中国互联网安全大会";
        String s = str.replaceAll(" ", "%20");
        System.out.println("remove blank :" + s);
        URI uri = URI.create(s);
        System.out.println(uri);

    }
}
