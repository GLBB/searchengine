package cn.gl.searchengine.maintest;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class Test5 {

    public static void main(String[] args) {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

        map.put("pony", 20);
        map.put("jack", 30);

        Enumeration<String> keys = map.keys();
        Iterator<String> iterator = keys.asIterator();
        if (iterator.hasNext()) {
            Integer r = map.remove(iterator.next());
            System.out.println(r);
        }
        System.out.println(map);
    }
}
