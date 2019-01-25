package cn.gl.analysis.schedule;

import cn.gl.analysis.extract.Extracter;
import cn.gl.analysis.extract.impl.ExtracterImpl;
import cn.gl.analysis.load.Loader;
import cn.gl.analysis.load.impl.LoaderImpl;
import cn.gl.analysis.write.Writer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ExtracterSchedule implements Runnable {

    @Autowired
    LoaderImpl loader;

    @Autowired
    ExtracterImpl extracter;

    @Autowired
    Writer writer;

    static ExecutorService executorService;
    static {
        executorService = Executors.newFixedThreadPool(3);
    }

    @Override
    public void run() {
        loader.setDirectoryPath("html_repo/2018_12_22");
        executorService.execute(loader);
        executorService.execute(extracter);
        executorService.execute(writer);
    }
}
