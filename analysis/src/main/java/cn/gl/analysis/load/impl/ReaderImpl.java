package cn.gl.analysis.load.impl;

import cn.gl.analysis.load.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Callable;

public class ReaderImpl implements Reader, Runnable {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private HashMap<String, String> pathContentMap = new HashMap<>();

    Set<String> paths;

    /**
     * 标志所有文件是否读完
     */
    private volatile boolean over;

    /**
     * 根据 path 读取文件
     * content 可能为空
     */
    @Override
    public void read() {
        logger.info("开始读文件: " + paths);

        Iterator<String> iterator = paths.iterator();
        while (iterator.hasNext()) {
            String pathStr = iterator.next();
            Path path = Path.of(pathStr);
            String content = null;
            try {
                content = Files.readString(path);
            } catch (IOException e) {
                logger.warn("读文件: {} 碰到异常", pathStr);
                e.printStackTrace();
            }
            pathContentMap.put(pathStr, content);
        }
        over = true;
        logger.info("读文件完毕");
    }

    @Override
    public void run() {
        read();
    }

    public Set<String> getPaths() {
        return paths;
    }

    public void setPaths(Set<String> paths) {
        this.paths = paths;
    }

    public boolean isOver() {
        return over;
    }


    public HashMap<String, String> getPathContentMap() {
        return pathContentMap;
    }

    public void setPathContentMap(HashMap<String, String> pathContentMap) {
        this.pathContentMap = pathContentMap;
    }
}
