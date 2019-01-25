package cn.gl.analysis.extract.impl;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;


public class Simhash {

    private final int hashbits = 128;

    private static final JiebaSegmenter segmenter = new JiebaSegmenter();

    public BigInteger getSimhash(String str){
        List<String> words = participle(str);
        BigInteger bigInteger = simHash(words);
        return bigInteger;
    }


    private List<String> participle(String str){
        ArrayList<String> list = new ArrayList<>();
//        Iterator<SegToken> segTokenIterator = null;
//        synchronized (this) {
//            segTokenIterator = segmenter.process(str, JiebaSegmenter.SegMode.SEARCH).iterator();
//        }
//        while (segTokenIterator.hasNext()) {
//            SegToken segToken = segTokenIterator.next();
//            String word = segToken.word;
//            list.add(word);
//        }

        StringTokenizer tokenizer = new StringTokenizer(str);
        Iterator<Object> iterator = tokenizer.asIterator();
        while (iterator.hasNext()) {
            String word = (String) iterator.next();
            list.add(word);
        }
        return list;
    }



    private BigInteger simHash(List<String> words) {
        int[] v = new int[this.hashbits];
        for (String word : words) {
            BigInteger hash = hash(word);
            for (int i = 0; i < hashbits; i++) {
                BigInteger bitmask = new BigInteger("1").shiftLeft(i);
                if (hash.and(bitmask).signum() != 0) {
                    v[i] += 1;
                }
                else {
                    v[i] -= 1;
                }
            }
        }
        BigInteger fingerprint = new BigInteger("0");
        for (int i = 0; i < this.hashbits; i++) {
            if (v[i] >= 0) {
                fingerprint = fingerprint.add(new BigInteger("1").shiftLeft(i));
            }
        }
        return fingerprint;
    }

    private BigInteger hash(String source) {
        if (source == null || source.length() == 0) {
            return new BigInteger("0");
        }
        else{
            char[] sourceArray = source.toCharArray();
            BigInteger x = BigInteger.valueOf(((long) sourceArray[0]) << 7);
            BigInteger m = new BigInteger("1000003");
            BigInteger mask = new BigInteger("2").pow(this.hashbits).subtract(
                    new BigInteger("1"));
            for (char item : sourceArray) {
                BigInteger temp = BigInteger.valueOf((long) item);
                x = x.multiply(m).xor(temp).and(mask);
            }
            x = x.xor(new BigInteger(String.valueOf(source.length())));
            if (x.equals(new BigInteger("-1"))) {
                x = new BigInteger("-2");
            }
            return x;
        }
    }

    public int hammingDistance(BigInteger one, BigInteger two) {
        BigInteger m = new BigInteger("1").shiftLeft(this.hashbits).subtract(
                new BigInteger("1"));
        BigInteger x = one.xor(two).and(m);
        int tot = 0;
        while (x.signum() != 0)
        {
            tot += 1;
            x = x.and(x.subtract(new BigInteger("1")));
        }
        return tot;
    }
}
