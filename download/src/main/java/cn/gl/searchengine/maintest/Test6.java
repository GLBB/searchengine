package cn.gl.searchengine.maintest;

import java.io.File;

public class Test6 {

    public static void main(String[] args) {
        File file = new File("pony/jack");
        boolean b = file.mkdirs();
        System.out.println(b);
    }
}
