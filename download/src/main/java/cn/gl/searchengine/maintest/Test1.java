package cn.gl.searchengine.maintest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test1 {

    public static void main(String[] args) {
        Runnable[] tasks = new Runnable[4];
        for (int i = 0; i < tasks.length; i++) {
            int temp = i;
            tasks[i] = ()->{
                System.out.println(temp);
            };
        }
        ExecutorService executorService = Executors.newFixedThreadPool(tasks.length);
        for (int i = 0; i < tasks.length; i++) {
            executorService.submit(tasks[i]);
        }
        executorService.shutdown();


    }

}
