package cn.gl.analysis.write;

import cn.gl.analysis.dto.ExtractContent;
import cn.gl.analysis.extract.impl.ExtractHandler;
import cn.gl.analysis.extract.impl.ExtracterImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

@Service
public class Writer implements Runnable {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ExtracterImpl extracter;

    private static final String DIREC = "analysis_repo";

    @Override
    public void run() {
        BlockingQueue<ExtractHandler> handlers = extracter.getHandlers();
        while (true) {
            if (!extracter.isAllOver() || handlers.size() > 0) {
                logger.info("准备获取 handler");
                ExtractHandler handler = null;
                try {
                    handler = handlers.take();
                    logger.info("获取成功");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.info("等待handler 处理完毕");
                while (!handler.isHandlerOver()) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                logger.info("handler 处理完毕");
                logger.info("准备写文件");
                ArrayList<ExtractContent> extractContents = handler.getExtractContents();

                for (ExtractContent extractContent : extractContents) {

                    writeExtractContent(extractContent);
                }
                logger.info("写文件完毕");
            }else {
                break;
            }

        }

    }


    private void writeExtractContent(ExtractContent extractContent){
//        html_repo/2018_12_22/0a0fc5d5-ae2f-4d5a-af7c-0ff4ae0f44a9
        String fileName = extractContent.getFileName();
        String[] split = fileName.split("/");
        String date = split[1];
        String parentDirec = DIREC + "/" + date;
        File file = new File(parentDirec);
        file.mkdirs();

        String writeFilePath = parentDirec + "/" + split[2];
        // 写文件
        try(
                FileOutputStream fos = new FileOutputStream(writeFilePath);
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(extractContent);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
