package cn.gl.analysis.extract.impl;

import cn.gl.analysis.extract.Extracter;
import cn.gl.analysis.load.Loader;
import cn.gl.analysis.load.Reader;
import cn.gl.analysis.load.impl.LoaderImpl;
import cn.gl.analysis.load.impl.ReaderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ExtracterImpl implements Extracter,Runnable {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    LoaderImpl loader;

    static ExecutorService executorService;
    static {
        executorService = Executors.newFixedThreadPool(1);
    }

    private BlockingQueue<ExtractHandler> handlers = new ArrayBlockingQueue<>(1);

    /**
     * 是否全部都以提交， 不代表运行完
     */
    private volatile boolean allOver = false;

    @Override
    public void run() {
        extract();
    }

    @Override
    public void extract() {
        // 如果 allread 是 false 或者 readers size 大于 0,
        // 就从 loader 中获得 reader
        BlockingQueue<ReaderImpl> readers = loader.getReaders();
        while (true) {
            if (!loader.getAllRead() || readers.size() > 0) {
                logger.info("准备获取 reader");
                ReaderImpl reader = null;
                try {
                    reader = readers.take();
                    logger.info("获取成功");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 提交 reader 给 extractHanlder, 并将handler 保存， 等到 writer 来消费
                ExtractHandler handler = new ExtractHandler();
                handler.setReader(reader);
                executorService.execute(handler);
                try {
                    logger.info("准备将 handler 放入阻塞队列");
                    handlers.put(handler);
                    logger.info("放入成功");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else {
                logger.info("已提交全部 handler");
                allOver = true;
                executorService.shutdown();
                break;
            }
        }
    }

    public BlockingQueue<ExtractHandler> getHandlers() {
        return handlers;
    }

    public boolean isAllOver() {
        return allOver;
    }
}
