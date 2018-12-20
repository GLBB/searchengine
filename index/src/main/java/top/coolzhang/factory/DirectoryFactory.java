package top.coolzhang.factory;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.FileSystems;

/**
 * @author coolzhang
 * @date 18-12-17 下午4:49
 *
 * 索引库工厂类(单例模式)， 用于获取索引库
 *
 */
public class DirectoryFactory {
    private static DirectoryFactory instance = null;
    private static Directory directory;

    private DirectoryFactory() throws IOException {
        directory = FSDirectory.open(FileSystems.getDefault().getPath("../src/main/resources/static/index"));
    }

    public static DirectoryFactory getDirectoryFactory() throws IOException {
        if (instance == null) {
            return new DirectoryFactory();
        }else {
            return instance;
        }
    }

    public Directory getDirectory() {
        return directory;
    }
}
