package cn.gl.analysis.simhash;

import java.math.BigInteger;
import java.util.StringTokenizer;
@SuppressWarnings("ALL")
public class SimHash
{
    private String tokens;
    private BigInteger strSimHash;
    private int hashbits = 128;
    public SimHash(String tokens)
    {
        this.tokens = tokens;
        this.strSimHash = this.simHash();
    }
    public SimHash(String tokens, int hashbits)
    {
        this.tokens = tokens;
        this.hashbits = hashbits;
        this.strSimHash = this.simHash();
    }
    public BigInteger simHash() {
        int[] v = new int[this.hashbits];
        StringTokenizer stringTokens = new StringTokenizer(this.tokens);
        while (stringTokens.hasMoreTokens())
        {
            String temp = stringTokens.nextToken();
            BigInteger t = this.hash(temp);
            for (int i = 0; i < this.hashbits; i++)
            {
                BigInteger bitmask = new BigInteger("1").shiftLeft(i);
                if (t.and(bitmask).signum() != 0)
                {
                    v[i] += 1;
                }
                else
                {
                    v[i] -= 1;
                }
            }
        }
        BigInteger fingerprint = new BigInteger("0");
        for (int i = 0; i < this.hashbits; i++)
        {
            if (v[i] >= 0)
            {
                fingerprint = fingerprint.add(new BigInteger("1").shiftLeft(i));
            }
        }
        return fingerprint;
    }
    private BigInteger hash(String source)
    {
        if (source == null || source.length() == 0)
        {
            return new BigInteger("0");
        }
        else
        {
            char[] sourceArray = source.toCharArray();
            BigInteger x = BigInteger.valueOf(((long) sourceArray[0]) << 7);
            BigInteger m = new BigInteger("1000003");
            BigInteger mask = new BigInteger("2").pow(this.hashbits).subtract(
                new BigInteger("1"));
            for (char item : sourceArray)
            {
                BigInteger temp = BigInteger.valueOf((long) item);
                x = x.multiply(m).xor(temp).and(mask);
            }
            x = x.xor(new BigInteger(String.valueOf(source.length())));
            if (x.equals(new BigInteger("-1")))
            {
                x = new BigInteger("-2");
            }
            return x;
        }
    }
    public int hammingDistance(SimHash other)
    {
        BigInteger m = new BigInteger("1").shiftLeft(this.hashbits).subtract(
            new BigInteger("1"));
        BigInteger x = this.strSimHash.xor(other.strSimHash).and(m);
        int tot = 0;
        while (x.signum() != 0)
        {
            tot += 1;
            x = x.and(x.subtract(new BigInteger("1")));
        }
        return tot;
    }
    public static void main(String[] args)
    {
        String s = "浅谈solr Solr是一个独立的企业级搜索应用服务器，它对外提供类似于Web-service的API接口。用户可以通过http请求，向搜索引擎服务器提交一定格式的XML文件，生成索引；也可以通过Http Get操作提出查找请求，并得到XML格式的返回结果。 Solr是一个高性能，采用Java5开发， Solr 基于Lucene的全文搜索服务器。同时对其进行了扩展，提供了比Lucene更为丰富的查询语言，同时实现了可配置、可扩展并对查询性能进行了优化。浅谈solr Solr是一个独立的企业级搜索应用服务器，它对外提供类似于Web-service的API接口。用户可以通过http请求，向搜索引擎服务器提交一定格式的XML文件，生成索引；也可以通过Http Get操作提出查找请求，并得到XML格式的返回结果。 Solr是一个高性能，采用Java5开发， Solr 基于Lucene的全文搜索服务器。同时对其进行了扩展，提供了比Lucene更为丰富的查询语言，同时实现了可配置、可扩展并对查询性能进行了优化。浅谈solr Solr是一个独立的企业级搜索应用服务器，它对外提供类似于Web-service的API接口。用户可以通过http请求，向搜索引擎服务器提交一定格式的XML文件，生成索引；也可以通过Http Get操作提出查找请求，并得到XML格式的返回结果。 Solr是一个高性能，采用Java5开发， Solr 基于Lucene的全文搜索服务器。同时对其进行了扩展，提供了比Lucene更为丰富的查询语言，同时实现了可配置、可扩展并对查询性能进行了优化。浅谈solr Solr是一个独立的企业级搜索应用服务器，它对外提供类似于Web-service的API接口。用户可以通过http请求，向搜索引擎服务器提交一定格式的XML文件，生成索引；也可以通过Http Get操作提出查找请求，并得到XML格式的返回结果。 Solr是一个高性能，采用Java5开发， Solr 基于Lucene的全文搜索服务器。同时对其进行了扩展，提供了比Lucene更为丰富的查询语言，同时实现了可配置、可扩展并对查询性能进行了优化。";
        SimHash hash1 = new SimHash(s, 128);
        System.out.println(hash1.strSimHash);
        s = "浅谈solr Solr是一个独立的企业级搜索应用服务器，它对外提供类似于Web-service的API接口。用户可以通过http请求，向搜索引擎服务器提交一定格式的XML文件，生成索引；也可以通过Http Get操作提出查找请求，并得到XML格式的返回结果。 Solr是一个高性能，采用Java5开发， Solr 基于Lucene的全文搜索服务器。同时对其进行了扩展，提供了比Lucene更为丰富的查询语言，同时实现了可配置、可修改、可扩展并对查询性能进行了优化。浅谈solr Solr是一个独立的企业级搜索应用服务器，它对外提供类似于Web-service的API接口。用户可以通过http请求，向搜索引擎服务器提交一定格式的XML文件，生成索引；也可以通过Http Get操作提出查找请求，并得到XML格式的返回结果。 Solr是一个高性能，采用Java5开发， Solr 基于Lucene的全文搜索服务器。同时对其进行了扩展，提供了比Lucene更为丰富的查询语言，同时实现了可配置、可扩展并对查询性能进行了优化。浅谈solr Solr是一个独立的企业级搜索应用服务器，它对外提供类似于Web-service的API接口。用户可以通过http请求，向搜索引擎服务器提交一定格式的XML文件，生成索引；也可以通过Http Get操作提出查找请求，并得到XML格式的返回结果。 Solr是一个高性能，采用Java5开发， Solr 基于Lucene的全文搜索服务器。同时对其进行了扩展，提供了比Lucene更为丰富的查询语言，同时实现了可配置、可扩展并对查询性能进行了优化。浅谈solr Solr是一个独立的企业级搜索应用服务器，它对外提供类似于Web-service的API接口。用户可以通过http请求，向搜索引擎服务器提交一定格式的XML文件，生成索引；也可以通过Http Get操作提出查找请求，并得到XML格式的返回结果。 Solr是一个高性能，采用Java5开发， Solr 基于Lucene的全文搜索服务器。同时对其进行了扩展，提供了比Lucene更为丰富的查询语言，同时实现了可配置、可扩展并对查询性能进行了优化。";
        SimHash hash2 = new SimHash(s, 128);
        System.out.println(hash2.strSimHash);
        s = "This is a test string for testing als";
//        SimHash hash3 = new SimHash(s, 128);
//        System.out.println(hash3.strSimHash + "  " + hash3.strSimHash.bitCount());
        System.out.println("============================");
        System.out.println(hash1.hammingDistance(hash2));
//        System.out.println(hash1.hammingDistance(hash3));
    }
}
