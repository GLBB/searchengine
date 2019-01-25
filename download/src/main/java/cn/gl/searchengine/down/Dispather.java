package cn.gl.searchengine.down;

import cn.gl.searchengine.dto.RowCol;
import org.dom4j.DocumentException;

import java.math.BigInteger;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.*;

public class Dispather implements Runnable{

    /**
     * 单例模式
     */
    private static Dispather dispather;
    static {
        try {
            dispather = new Dispather();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * 未抓取的网页链接
     */
    static ConcurrentHashMap<String, Set<String>> ungrasp = new ConcurrentHashMap<>();

    Random rand = new Random();

    /**
     * 保存未成功下载的链接
     */
    static Set<String> unsuccess = new HashSet<>();

    /**
     * 保存成功下载的链接计算hash值
     */
    int[] hash = new int[1024*1024];

    /**
     * 下载池
     */
    ExecutorService executorService;
    {
        executorService = Executors.newFixedThreadPool(3);
    }

    /**
     * md5 算法
     */
    MessageDigest md5 = MessageDigest.getInstance("MD5");

    private Dispather() throws NoSuchAlgorithmException {}

    public static Dispather getDispather(){
        return dispather;
    }

    // 分配 url集合 给 dispather
    // dispather 下载url集合
    // 返回下载结果
    // 将未成功下载的加入一个未成功集合，并且记录未成功次数，如果大于3次就放弃该url
    // 将成功下载的链接记录已经下载，并保存下载时间，URL，对应的网页在磁盘上的名字
    // dispather 将网页交给 析取者，
    // 提取出网页中的链接，去重，判断是否下载过，加入对应的未抓取链接队列中
    @Override
    public void run() {
        // 初始化从种子站点加载URL
        SeedsLoader seedsLoader = new SeedsLoader();
        try {
            seedsLoader.xml2Seeds();
            seedsLoader.getSitemapURL();
            seedsLoader.loadSitemap();
            seedsLoader.insertToDispather();
            ungrasp.put("other", new HashSet<>());
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        while (true) {
            try{
                // 得到返回的urimap 下载情况统计,
                // 得到提取到的uri, 判断该链接是否下载过，若未下载过，就插入
                Future<HashMap<String, Integer>>[] futures = new Future[3];
                Gather[] gathers = new Gather[3];
                for (int i = 0; i < 3; i++) {
                    List<String> allocateURL1 = allocate();
                    List<URI> uris1 = str2URL(allocateURL1);
                    if (allocateURL1.size() == 0) {
                        System.out.println("待下载链接为空");
                        try {
                            TimeUnit.MINUTES.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Gather gather1 = new Gather();
                        gathers[i] = gather1;
                        gather1.setNeeddown(uris1);
                        Future<HashMap<String, Integer>> future1 = executorService.submit(gather1);
                        futures[i] = future1;
                    }
                }

                for (int i = 0; i < 3; i++) {
                    Future<HashMap<String, Integer>> future = futures[i];
                    try {
                        // 成功或失败的链接
                        HashMap<String, Integer> uriMap = future.get(10, TimeUnit.SECONDS);
                        if (uriMap == null) {
                            continue;
                        }
                        processURIMap(uriMap);
                        // 解析出来的链接
                        Set<String> extractURI = gathers[i].getExtractURI();
                        processExtractURI(extractURI);
                    } catch (InterruptedException e) {
                        Set<String> extractURI = gathers[i].getExtractURI();
                        if (extractURI != null) {
                            processExtractURI(extractURI);
                        }

                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        Set<String> extractURI = gathers[i].getExtractURI();
                        if (extractURI != null) {
                            processExtractURI(extractURI);
                        }

                        e.printStackTrace();
                    }
                }
            }catch (Exception e) {
                System.out.println(e);
            }
            try {
                // 沉睡 30 - 60 秒
                int i = 0;
                while (i<10){
                    i = rand.nextInt(20);
                }
                System.out.println("dispather 沉睡 " + i + " 秒");
                TimeUnit.SECONDS.sleep(i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 将新的url 加入ungrasp 队列中
     * @param uris
     */
    private void processExtractURI(Set<String> uris){
        Iterator<String> iterator = uris.iterator();
        while (iterator.hasNext()) {
            String uri = iterator.next();
            RowCol rowAndCol = getRowAndCol(uri);
            int result = getbit(rowAndCol.getRow(), rowAndCol.getCol());
            if (result == 1) {
                // 说明已经下载过
                continue;
            }
            else if (result == 0) {
                // 说明还没下载过
                // 添加到 ungrasp 集合中
                // 设置 hash 数组的对应的值为 1
                Iterator<String> queue = ungrasp.keys().asIterator();
                boolean flag = true;
                while (queue.hasNext()) {
                    String queueKey = queue.next();
                    if(queueKey.equals("other")){
                        continue;
                    }
                    String queueSite = Extracter.getsite(queueKey);
                    String uriSite = Extracter.getsite(uri);
                    if (queueSite.equals(uriSite)){
                        ungrasp.get(queueKey).add(uri);
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    ungrasp.get("other").add(uri);
                }

                setbit(rowAndCol.getRow(), rowAndCol.getCol());
            }
        }
    }



    private void processURIMap(HashMap<String, Integer> uriMap){
        Set<String> keys = uriMap.keySet();
        for (String key : keys) {
            Integer flag = uriMap.get(key);
            if (flag == 0) {
                unsuccess.add(key);
            }
            // 计算md5值，标记到去重集合中
            RowCol rowAndCol = getRowAndCol(key);
            setbit(rowAndCol.getRow(), rowAndCol.getCol());
        }
    }


    private RowCol getRowAndCol(String url){
        md5.update(url.getBytes());
        byte[] digest = md5.digest();
        BigInteger bigInteger = new BigInteger(digest);
        if (bigInteger.compareTo(BigInteger.ZERO) < 0) {
            // 转化为正数
            BigInteger two128 = new BigInteger("2").pow(128);
            bigInteger = bigInteger.add(two128);
        }
        // 计算 row
        BigInteger two20 = new BigInteger("2").pow(20);
        int row = bigInteger.divide(new BigInteger("32")).mod(two20).intValue();
        // 计算collumn
        int col = bigInteger.mod(new BigInteger("32")).intValue();
        RowCol rowCol = new RowCol(row, col);
        return rowCol;
    }

    private void setbit(int row, int col){
        int temp = 1<<col;
        hash[row] = hash[row] | temp;
    }

    private int getbit(int row, int col){
        int temp = hash[row];
        temp = temp >> col;
        int result = temp & 1;
        return result;
    }



    private List<String> allocate(){
        ArrayList<String> list = new ArrayList<>();
        Enumeration<String> keys = ungrasp.keys();
        Iterator<String> keyIterater = keys.asIterator();
        while (keyIterater.hasNext()) {
            String key = keyIterater.next();
            Set<String> set = ungrasp.get(key);
            Iterator<String> setIterator = set.iterator();
            String url =  null;
            if (setIterator.hasNext()) {
                url = setIterator.next();
                if(url.strip().equals("")){

                }else {
                    list.add(url);
                }
            }
            if (url != null) {
                set.remove(url);
            }
        }
        return list;
    }

    /**
     * 将一组字符串url 转化未url对象
     */
    private List<URI> str2URL(List<String> strURLs){
        List<URI> uris = new ArrayList<>();
        for (String str : strURLs) {
            str = str.replaceAll(" ", "%20");
            URI uri = URI.create(str);
            uris.add(uri);
        }
        return uris;
    }


}
