package cn.gl.analysis;

import cn.gl.analysis.config.SpringContext;
import cn.gl.analysis.extract.Extracter;
import cn.gl.analysis.schedule.ExtracterSchedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class AnalysisApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(AnalysisApplication.class, args);

        ExtracterSchedule extracterSchedule = SpringContext.getContext().getBean(ExtracterSchedule.class);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(extracterSchedule);
    }

}

