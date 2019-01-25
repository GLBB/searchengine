package cn.gl.searchengine.dom4j;

import org.dom4j.*;
import org.junit.Test;

import java.util.List;

public class Dom4jTest2 {

    String demo = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
            "\n" +
            "<bookstore>\n" +
            "\n" +
            "<book>\n" +
            "  <title lang=\"eng\">Harry Potter</title>\n" +
            "  <price>29.99</price>\n" +
            "</book>\n" +
            "\n" +
            "<book>\n" +
            "  <title lang=\"eng\">Learning XML</title>\n" +
            "  <price>39.95</price>\n" +
            "</book>\n" +
            "\n" +
            "</bookstore>";

    @Test
    public void test1() throws DocumentException {
        Document document = DocumentHelper.parseText(demo);
        List<Node> bookNode = document.selectNodes("bookstore//title");
        bookNode.stream().forEach(e->{
            System.out.println(e.getStringValue());
        });
    }

}
