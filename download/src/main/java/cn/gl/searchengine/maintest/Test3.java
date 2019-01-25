package cn.gl.searchengine.maintest;

import java.util.concurrent.CopyOnWriteArraySet;

public class Test3 {

    public static void main(String[] args) {
        CopyOnWriteArraySet<String> set = new CopyOnWriteArraySet<>();
        set.add("111");
        set.add("222");
        set.add("333");
        set.add("222");

        System.out.println(set);
    }
}
