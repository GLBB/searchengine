package cn.gl.searchengine;

import cn.gl.searchengine.down.Dispather;
import cn.gl.searchengine.proxymanager.ProxyManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class SearchengineApplication {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(SearchengineApplication.class, args);
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(ProxyManager.getInstance(), 0, 1, TimeUnit.DAYS);

        TimeUnit.SECONDS.sleep(10);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(Dispather.getDispather());

    }

}

