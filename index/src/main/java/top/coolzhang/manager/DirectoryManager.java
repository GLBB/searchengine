package top.coolzhang.manager;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;
import top.coolzhang.factory.DirectoryFactory;

import java.io.File;
import java.io.IOException;

/**
 * @author coolzhang
 * @date 18-12-17 下午4:28
 */
public class DirectoryManager {
    private Directory directory;

    public DirectoryManager() throws IOException {
        this.directory = DirectoryFactory.getDirectoryFactory().getDirectory();
    }

    public IndexWriter getIndexWriter() throws IOException {
        Analyzer analyzer = new IKAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        return new IndexWriter(this.directory, config);
    }

    /**
     * 向索引库中添加索引
     * @param pathname 传入文件路径，可以是具体的某个文件，也可以是包含文件的文件夹路径
     * @throws IOException
     */
    public void add(String pathname) throws IOException {
        IndexWriter indexWriter = getIndexWriter();

        File f = new File(pathname);
        File[] files = f.listFiles();
        if (files != null) {
            for (File file : files) {
                Document document = new Document();
                // 文件名字
                String file_name = file.getName();
                Field fileNameField = new TextField("fileName", file_name, Field.Store.YES);
                // 文件内容
                String file_content = FileUtils.readFileToString(file, "utf-8");
                Field fileContentField = new TextField("fileContent", file_content, Field.Store.YES);

                document.add(fileNameField);
                document.add(fileContentField);

                indexWriter.addDocument(document);
            }
        }else {
            File file = new File(pathname);
            Document document = new Document();
            // 文件名字
            String file_name = file.getName();
            Field fileNameField = new TextField("fileName", file_name, Field.Store.YES);
            // 文件内容
            String file_content = FileUtils.readFileToString(file, "utf-8");
            Field fileContentField = new TextField("fileContent", file_content, Field.Store.YES);

            document.add(fileNameField);
            document.add(fileContentField);

            indexWriter.addDocument(document);
        }
        indexWriter.close();
    }

    /**
     * 删除索引库中全部索引
     * @throws IOException
     */
    public void deleteAll() throws IOException {
        IndexWriter indexWriter = getIndexWriter();
        indexWriter.deleteAll();
        indexWriter.close();
    }


    /**
     * 删除指定的索引
     * @param query
     * @throws IOException
     */
    public void delete(Query query) throws IOException {
        IndexWriter indexWriter = getIndexWriter();
        indexWriter.deleteDocuments(query);
        indexWriter.close();
    }

    @Test
    public void testAdd() throws IOException {
        String pathname = "../src/main/resources/static/files";
        System.out.println("添加索引中...");
        long start = System.currentTimeMillis();
        add(pathname);
        long time = System.currentTimeMillis() - start;
        System.out.println("添加成功！用时" + time + "ms");
    }

    @Test
    public void testDelete() throws IOException {
        deleteAll();
    }
}
