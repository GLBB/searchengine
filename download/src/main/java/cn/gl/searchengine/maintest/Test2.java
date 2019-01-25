package cn.gl.searchengine.maintest;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Test2 {

    public static void main(String[] args) {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        var i = 1;
        var temp = i;
        Runnable r = ()->{
            try {

                System.out.println("hello");
                throw new RuntimeException();
            }catch (Exception e) {

            }

        };

        scheduledExecutorService.scheduleAtFixedRate(r, 1000, 1000,TimeUnit.MILLISECONDS);

    }
}
