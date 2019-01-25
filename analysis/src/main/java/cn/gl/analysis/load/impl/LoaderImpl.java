package cn.gl.analysis.load.impl;

import cn.gl.analysis.load.Loader;
import cn.gl.analysis.load.Reader;
import cn.gl.analysis.mapper.FilePageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 10 个线程，
 * 每个线程读一百个文件
 */
@Service
public class LoaderImpl implements Loader, Runnable {

    Logger logger = LoggerFactory.getLogger(LoaderImpl.class);

    @Autowired
    FilePageMapper filePageMapper;

    /**
     * 文件所有路径集合
     * boolean 为 false 代表未读
     * 为 true 代表已读
     */
    private Map<String, Boolean> filesNameMap = new HashMap<>();

    private volatile boolean allRead = false;

    public BlockingQueue<ReaderImpl> readers = new ArrayBlockingQueue<>(1);

    private String directoryPath;

    /**
     * 加载文件内容到内存所使用的线程池
     */
    private static ExecutorService executorService;

    static {
        executorService = Executors.newFixedThreadPool(1);
    }

    /**
     * 从数据库加载文件名
     * @return
     */
    @Override
    public boolean load() {
        logger.info("开始加载全部文件名");
        Set<String> allFilename = filePageMapper.getAllFilename();
        allFilename.stream().forEach(fileName->{
            filesNameMap.put(fileName, false);
        });
        logger.info("加载结束");
        logger.info("加载文件名的数量 {}", filesNameMap.keySet().size());
        return true;
    }

    @Override
    public void run() {
        load();
        while (!allRead) {
            ReaderImpl reader = new ReaderImpl();
            // 分配 还没有分配过的 path 给 Reader 加载
            Set<String> partialPaths = allocatePaths();
            reader.setPaths(partialPaths);
            // 将reader 提交到线程池
            executorService.execute(reader);
            // 将 reader 放入阻塞队列
            try {
                logger.info("准备将reader 放入阻塞队列");
                readers.put(reader);
                logger.info("reader 放入成功");
            } catch (InterruptedException e) {
                e.printStackTrace();
                logger.warn("将reader 放入阻塞队列的过程中被打断");
            }
            // 判断所有的文件路径是否都已经加载过，
            // 若加载过就将标志变量 allRead 置为 true
            // 并且关闭线程池
            boolean isAllRead = isAllRead();
            logger.info("是否全部文件读完{}", isAllRead);
            if (isAllRead) {
                allRead = true;
                executorService.shutdown();
            }
        }
    }


//    public String getDirectoryPath() {
//        return directoryPath;
//    }

    public void setDirectoryPath(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    /**
     * 分配文件路径给 reader
     * @return
     */
    private Set<String> allocatePaths(){
        Set<String> paths = new HashSet<>();

        Set<String> keys = filesNameMap.keySet();
        Iterator<String> iterator = keys.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            if (count >= 100) {
                break;
            }
            String path = iterator.next();
            Boolean read = filesNameMap.get(path);
            if (!read) {
                count++;
                paths.add(path);
                filesNameMap.put(path, true);
            }
        }
        return paths;
    }

    private boolean isAllRead(){
        Iterator<String> iterator = filesNameMap.keySet().iterator();
        while (iterator.hasNext()) {
            Boolean read = filesNameMap.get(iterator.next());
            if (!read) {
                return false;
            }
        }
        return true;
    }

    public BlockingQueue<ReaderImpl> getReaders() {
        return readers;
    }

    public boolean getAllRead(){
        return allRead;
    }


}
