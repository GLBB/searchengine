package cn.gl.analysis.extract.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
public class DuplicateCheck {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    final Simhash simhash = new Simhash();

    final CopyOnWriteArrayList<BigInteger> list = new CopyOnWriteArrayList<>();

    /**
     * 重复返回false
     * @param content
     * @return
     */
    public boolean check(String content){
        BigInteger one = this.simhash.getSimhash(content);
        for (int i = 0; i < list.size(); i++) {
            int distance = simhash.hammingDistance(one, list.get(i));
            if (distance < 3) {
                if (content.length() < 20) {
                    logger.info("重复 {} : {}", i, content);
                }else {
                    logger.info("重复 {} : {}", i, content.substring(0, 20));
                }
                return false;
            }
        }
        list.add(one);
        return true;
    }

}
