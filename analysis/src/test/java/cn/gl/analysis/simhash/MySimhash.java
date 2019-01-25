package cn.gl.analysis.simhash;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

@SuppressWarnings("ALL")
public class MySimhash {

    private final int hashbits = 128;

    static JiebaSegmenter segmenter = new JiebaSegmenter();

    public static void main(String[] args) {
        String str1 = "浅谈solr Solr是一个独立的企业级搜索应用服务器，它对外提供类似于Web-service的API接口。用户可以通过http请求，向搜索引擎服务器提交一定格式的XML文件，生成索引；也可以通过Http Get操作提出查找请求，并得到XML格式的返回结果。 Solr是一个高性能，采用Java5开发， Solr 基于Lucene的全文搜索服务器。同时对其进行了扩展，提供了比Lucene更为丰富的查询语言，同时实现了可配置、可扩展并对查询性能进行了优化。";
        String str2 = "消费者(Consumer)是一个想从Ring Buffer里读取数据的线程，它可以访问ConsumerBarrier对象——这个对象由RingBuffer创建并且代表消费者与RingBuffer进行交互。就像Ring Buffer显然需要一个序号才能找到下一个可用节点一样，消费者也需要知道它将要处理的序号——每个消费者都需要找到下一个它要访问的序号。在上面的例子中，消费者处理完了Ring Buffer里序号8之前（包括8）的所有数据，那么它期待访问的下一个序号是9。";
        MySimhash mySimhash = new MySimhash();
        BigInteger one = mySimhash.getSimhash(str1);
        BigInteger two = mySimhash.getSimhash(str2);
        int i = mySimhash.hammingDistance(one, two);
        System.out.println(i);
    }

    public BigInteger getSimhash(String str){
        List<String> words = participle(str);
        BigInteger bigInteger = simHash(words);
        return bigInteger;


    }


    private static List<String> participle(String str){
//        ArrayList<String> list = new ArrayList<>();
//        Iterator<SegToken> segTokenIterator = segmenter.process(str, JiebaSegmenter.SegMode.SEARCH).iterator();
//        while (segTokenIterator.hasNext()) {
//            SegToken segToken = segTokenIterator.next();
//            String word = segToken.word;
//            list.add(word);
//        }

        StringTokenizer tokenizer = new StringTokenizer(str);
        Iterator<Object> iterator = tokenizer.asIterator();
        List<String> list = new ArrayList<>();
        while (iterator.hasNext()) {
            String word = (String) iterator.next();
            list.add(word);
        }
        return list;
    }



    private BigInteger simHash(List<String> words) {
        int[] v = new int[hashbits];
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
            BigInteger mask = new BigInteger("2").pow(hashbits).subtract(
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
        BigInteger m = new BigInteger("1").shiftLeft(hashbits).subtract(
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
