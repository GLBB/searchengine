package com.nix.searchengine.controller;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import top.coolzhang.query.QueryDirectory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.FileSystems;

/**
 * @author coolzhang
 * @date 18-12-18 下午4:28
 */
@Controller
public class HelloWorld extends HttpServlet {

    @RequestMapping("/search")
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("utf8");

        String keyword = request.getParameter("keyword");
        System.out.println(keyword);
        String html_prefix = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>搜索结果</title>\n" +
                "    <link rel=\"stylesheet\" href=\"../css/search.css\">\n" +
                "    <script src=\"../js/index.js\"></script>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"wrapper\">\n" +
                "        <div class=\"inputwrapper\">\n" +
                "            <div class=\"input\">\n" +
                "                <form id=\"form\" action=\"/search\", method=\"get\">\n" +
                "                    <span class=\"i\">\n" +
                "                        <input id=\"input\" name=\"keyword\" type=\"text\" autocomplete='off' value="+ keyword +">\n" +
                "                    </span>\n" +
                "                    <span class=\"b\">\n" +
                "                        <input id=\"submit\" type=\"submit\" value=\"搜索一下\" onclick=\"test()\">\n" +
                "                    </span>\n" +
                "                </form>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        <div class=\"result\">";
        String html_suffix = "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";


        // 查询结果
        Directory directory = FSDirectory.open(FileSystems.getDefault().getPath("target/classes/static/index"));
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        String result = "";
        QueryDirectory queryDirectory = new QueryDirectory();
        if (keyword != null) {
            String[] strings = keyword.split(" ");
            BooleanQuery.Builder builder = new BooleanQuery.Builder();
            for (String s : strings) {
                builder.add(new TermQuery(new Term("fileContent", s)), BooleanClause.Occur.SHOULD);
            }
            BooleanQuery query = builder.build();
            try {
                result = queryDirectory.returnResult(indexSearcher, query);
            } catch (InvalidTokenOffsetsException e) {
                e.printStackTrace();
            }
            indexSearcher.getIndexReader().close();
        }

        response.getWriter().write(html_prefix + result + html_suffix);
    }
}
