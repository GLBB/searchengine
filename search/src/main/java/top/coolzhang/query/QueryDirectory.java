package top.coolzhang.query;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;
import top.coolzhang.factory.DirectoryFactory;

import java.io.IOException;

/**
 * @author coolzhang
 * @date 18-12-17 下午11:31
 */
public class QueryDirectory {

    /**
     * 获取 IndexSearcher 对象
     * @return IndexSearcher
     * @throws IOException
     */
    public IndexSearcher getIndexSearcher() throws IOException {
        Directory directory = DirectoryFactory.getDirectoryFactory().getDirectory();
        IndexReader indexReader = DirectoryReader.open(directory);
        return new IndexSearcher(indexReader);
    }

    /**
     * 打印查询结果
     * @param indexSearcher
     * @param query
     * @throws IOException
     */
    public void printResult(IndexSearcher indexSearcher, Query query) throws IOException {
        long start = System.currentTimeMillis();
        TopDocs topDocs = indexSearcher.search(query, 10);
        long time = System.currentTimeMillis() - start;
        System.out.println("共查询到" + topDocs.totalHits + "篇文档， 用时" + time + "ms");
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            int doc = scoreDoc.doc;
            Document document = indexSearcher.doc(doc);
            // 文件名称
            String fileName = document.get("fileName");
            System.out.println(fileName);
            // 文件内容
            String fileContent = document.get("fileContent");
            System.out.println(fileContent);

            System.out.println("*****************************************************************");
        }
    }

    public String returnResult(IndexSearcher indexSearcher, Query query) throws IOException, InvalidTokenOffsetsException {
        long start = System.currentTimeMillis();
        TopDocs topDocs = indexSearcher.search(query, 10);
        long time = System.currentTimeMillis() - start;
//        System.out.println("共查询到" + topDocs.totalHits + "篇文档， 用时" + time + "ms");
        String queryTime = "<div class='queryinfo'>为您找到相关结果" + topDocs.totalHits + "个， 用时" + time + "ms</div>";
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;

        QueryScorer scorer = new QueryScorer(query);
        Fragmenter fragmenter = new SimpleSpanFragmenter(scorer, 100);
        Formatter formatter = new SimpleHTMLFormatter("<span style='color:red'>", "</span>");
        Highlighter highlighter = new Highlighter(formatter, scorer);
        highlighter.setTextFragmenter(fragmenter);

        StringBuffer stringBuffer = new StringBuffer(queryTime);
        for (ScoreDoc scoreDoc : scoreDocs) {
            int doc = scoreDoc.doc;
            Document document = indexSearcher.doc(doc);
            String title ="<div class='iteam'><a class='iteam-title' href='http://47.95.205.92:8080/html/index.html' target='view_window'>" + document.get("fileName") + "</a>";
            String content = "<p>" + highlighter.getBestFragment(new IKAnalyzer(), "fileContent", document.get("fileContent")) + "...</p>";
//            System.out.println(content);
//            System.out.println("---------------------");
            String link = "<a class='iteam-link' href='http://47.95.205.92:8080/html/index.html' target='view_window'>http://47.95.205.92:8080/html/index.html</a></div>";
            stringBuffer.append(title + content + link);
        }
        return stringBuffer.toString();
    }

    /**
     * 查询所有结果
     * @throws IOException
     */
    public void queryAll() throws IOException {
        IndexSearcher indexSearcher = getIndexSearcher();
        Query query = new MatchAllDocsQuery();
        printResult(indexSearcher, query);
        indexSearcher.getIndexReader().close();
    }

    /**
     * 传入一个特定的 query 并查询结果
     * @param query
     * @throws IOException
     */
    public void query(Query query) throws IOException {
        IndexSearcher indexSearcher = getIndexSearcher();
        printResult(indexSearcher, query);
        indexSearcher.getIndexReader().close();
    }

    /**
     *
     * @throws IOException
     */
    public void queryPaser() throws IOException {
        IndexSearcher indexSearcher = getIndexSearcher();
        Query query1 = new TermQuery(new Term("fileContent", "南京大屠杀"));
        Query query2 = new TermQuery(new Term("fileContent", "日本"));
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        builder.add(query1, BooleanClause.Occur.SHOULD);
        builder.add(query2, BooleanClause.Occur.SHOULD);
        BooleanQuery query = builder.build();
        printResult(indexSearcher, query);
        indexSearcher.getIndexReader().close();
    }

    /**
     * 根据用户输入的关键字字符串进行查询
     * @param text
     * @throws IOException
     */
    public void query(String text) throws IOException, InvalidTokenOffsetsException {
        if (text != null) {
            String[] strings = text.split(" ");
            IndexSearcher indexSearcher = getIndexSearcher();
            BooleanQuery.Builder builder = new BooleanQuery.Builder();
            for (String s : strings) {
                builder.add(new TermQuery(new Term("fileContent", s)), BooleanClause.Occur.SHOULD);
            }
            BooleanQuery query = builder.build();
            returnResult(indexSearcher, query);
            indexSearcher.getIndexReader().close();
        }
    }

    @Test
    public void testMyQuery() throws IOException, InvalidTokenOffsetsException {
        String s = "中国 制造 创造";
        query(s);
    }

    @Test
    public void testQueryAll() throws IOException {
        queryAll();
    }

    @Test
    public void testQuery() throws IOException {
        Query query = new TermQuery(new Term("fileContent", "南京大屠杀"));
        query(query);
    }

    @Test
    public void testPaserQuery() throws IOException {
        queryPaser();
    }

    @Test
    public void testHighlight() throws IOException, InvalidTokenOffsetsException {
        String text = "搜索引擎包含了各个学科的概念和知识，这些学科包含了计算科学，数学，心理学等，特别是数学几乎在搜索引擎的各个系统都大量使用，例如布尔代数，概率论，数理统计等，这些数学知识的应用为搜索引擎解决了一个个的难题，最终使得搜索技术走向成熟。";
        TermQuery query = new TermQuery(new Term("f", "数学"));
        QueryScorer scorer = new QueryScorer(query);
        Fragmenter fragmenter = new SimpleSpanFragmenter(scorer, 60);
        Formatter formatter = new SimpleHTMLFormatter("<span style='color:red'>", "</span>");
        Highlighter highlighter = new Highlighter(formatter, scorer);
        highlighter.setTextFragmenter(fragmenter);
        String str = highlighter.getBestFragment(new IKAnalyzer(), "f", text);
        System.out.println(str);
    }
}
