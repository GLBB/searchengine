package cn.gl.searchengine.down;

import cn.gl.searchengine.bean.IP;
import cn.gl.searchengine.config.SpringContext;
import cn.gl.searchengine.dto.GraspPage;
import cn.gl.searchengine.mapper.FilePageMapper;
import cn.gl.searchengine.proxymanager.ProxyManager;
import cn.gl.searchengine.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
@Service
public class Gather implements Callable<HashMap<String, Integer>> {

    /**
     * 依赖代理
     */
    private static ProxyManager proxyManager = ProxyManager.getInstance();

    Random rand = new Random();

    /**
     * 默认检验proxyIP 是否可用的URI,单个容易报连接错误，所以用多个试下
     */
//    private static final String DEFAULTCHECKPROXYURI = "https://www.baidu.com/";
    private static final List<String> DEFAULTCHECKPROXYURIS = new ArrayList<>();
    static {
        DEFAULTCHECKPROXYURIS.add("https://www.sogou.com/");
        DEFAULTCHECKPROXYURIS.add("https://www.baidu.com/");
        DEFAULTCHECKPROXYURIS.add("https://www.so.com/");
        DEFAULTCHECKPROXYURIS.add("https://cn.bing.com/");
        DEFAULTCHECKPROXYURIS.add("http://www.youdao.com/");
        DEFAULTCHECKPROXYURIS.add("http://www.chinaso.com/");
        DEFAULTCHECKPROXYURIS.add("http://search.sina.com.cn/");
        DEFAULTCHECKPROXYURIS.add("http://www.searchcraft.cn/");
        DEFAULTCHECKPROXYURIS.add("https://www.swkong.com/");
    }

    private List<URI> needdown;

    /**
     * urimap 作用，标识这次提取出来的网页，key uri,
     * value 0 表示下载为成功
     * value 1 表示下载成功
     *
     * dispather 使用
     */
    private HashMap<String, Integer> uriMap = new HashMap<>();

    /**
     * 保存从下载后的网页抽取出的 uri, dispather 使用
     */
    private volatile Set<String> extractURI = new CopyOnWriteArraySet<>();

    /**
     * 用于代理的ip, 回收的时候插入 proxymanager 中
     * ip1 保存 http 类型 代理 ip
     * ip2 保存 https 类型 代理 ip
     * flag1 用于标记 ip1 是否可用
     * flag2 用于标记 ip2 是否可用
     */
    private IP ip1;
    private IP ip2;
    boolean flag1 = true;
    boolean flag2 = true;

    private static final String FILESYSTEMROOT = "html_repo";

    private static FilePageMapper filePageMapper = SpringContext.getContext().getBean(FilePageMapper.class);

    private static Extracter extracter = Extracter.getInstance();

    /**
     * 写文件， 添加到文件系统中,并把URL保存到数据库, 写编码
     * 保存数据库 下载日期，对应的文件系统路径文件名，对应的url
     * 抽取链接, 方便dispather 来读取
     * 如果用了代理，回收代理
     * @return
     * @throws Exception
     */
    @Override
    public HashMap<String, Integer> call() throws Exception {
        if (needdown== null) {
            System.out.println("needdown 为空");
            return null;
        }
        HttpClient httpClient = getHttpClient(0);
        HttpClient httpsClient = getHttpClient(1);

        List<HttpRequest> requests = needdown.stream().map(uri ->{
                    if (!uri.toString().strip().equals("")){
                        System.out.println("uri: "+ uri);
                        return HttpRequest.newBuilder(uri)
                                .timeout(Duration.ofSeconds(3))
                                .setHeader("User-Agent", ProxyManager.userAgent.get(ProxyManager.rand.nextInt(ProxyManager.userAgent.size())))
                                .setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                                .setHeader("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2")
                                .setHeader("If-None-Match", "W/\"a8b7eee808803ac203acb351b10cd050\"")
                                .setHeader("Cache-Control", "")
                                .build();
                    }else {
                        return null;
                    }
                }
        ).collect(Collectors.toList());

        CompletableFuture.allOf(requests.stream().map(req->{
            String strUri = req.uri().toString();
            CompletableFuture<Void> completableFuture = null;
            if (strUri.startsWith("https")){
                completableFuture = httpsClient.sendAsync(req, HttpResponse.BodyHandlers.ofString()).thenAccept(res -> {
                    if (res.statusCode() == 200) {
                        // 写文件， 添加到文件系统中,并把URL保存到数据库, 写编码
                        String filePath = writeFile(res);
                        // 保存数据库 下载日期，对应的文件系统路径文件名，对应的url
                        Integer insert = save2Database(filePath, req.uri().toString());
                        if (insert == 0) {
                            System.out.println("错误， 没有插入成功");
                        }
                        // 抽取链接, 插入 extractURI中, 方便dispather 来读取
                        Set<String> extractPartialURI = extracter.extract(res.uri().toString(), res.body());
                        insert2ExtractURI(extractPartialURI);
                    }else {
                        System.out.println("下载不成功: "+ req.toString());
                        uriMap.put(res.uri().toString(), 0);
                    }
                }).exceptionally(e -> {
                    uriMap.put(req.uri().toString(), 0);
                    // 如果 抛出的式连接异常，就放弃该ip
                    if (e instanceof ConnectException) {
                        flag2 = false;
                    }
                    System.out.println("出现异常");
                    e.printStackTrace();
                    return null;
                });
                return completableFuture;
            }else if (strUri.startsWith("http")){
                completableFuture = httpClient.sendAsync(req, HttpResponse.BodyHandlers.ofString()).thenAccept(res->{
                    if (res.statusCode() == 200){
                        // 写文件， 添加到文件系统中,并把URL保存到数据库, 写编码
                        String filePath = writeFile(res);
                        // 保存数据库 下载日期，对应的文件系统路径文件名，对应的url
                        Integer insert = save2Database(filePath, req.uri().toString());
                        if (insert == 0) {
                            System.out.println("错误， 没有插入成功");
                        }
                        // 抽取链接, 方便dispather 来读取
                        Set<String> extractPartialURI = extracter.extract(res.uri().toString(), res.body());
                        insert2ExtractURI(extractPartialURI);
                    }else {
                        uriMap.put(res.uri().toString(), 0);
                    }
                }).exceptionally(e->{
                    System.out.println(e);
                    uriMap.put(req.uri().toString(), 0);
                    if (e instanceof ConnectException) {
                        flag1 = false;
                    }
                    return null;
                });
                return completableFuture;
            }else {
                System.out.println("异常uri: " + req.uri());
                return null;
            }
        }).toArray(CompletableFuture<?>[]::new)).join();
        recoveryProxy();
        return uriMap;

    }

    private synchronized void insert2ExtractURI(Set<String> extractPartialURI){
        Iterator<String> iterator = extractPartialURI.iterator();
        while (iterator.hasNext()) {
            String uri = iterator.next();
            extractURI.add(uri);
        }
    }

    private void recoveryProxy() {
        if (ip1 != null && flag1) {
            ProxyManager.getInstance().repoHTTP.put(ip1.getIp(), ip1);
        }
        if (ip2 != null && flag2) {
            ProxyManager.getInstance().repoHTTPS.put(ip2.getIp(), ip2);
        }

    }

    private Integer save2Database(String filepath, String url){
        GraspPage graspPage = new GraspPage(filepath, url, new Date());
        System.out.println("filePageMapper: " + filePageMapper);
        Integer insert = filePageMapper.insert(graspPage);
        return insert;
    }

    private String writeFile(HttpResponse<String> res){
        String filePath = FILESYSTEMROOT + "/" + DateUtil.getDateString();
        File file = new File(filePath);
        UUID uuid = UUID.randomUUID();
        if (!file.exists()) file.mkdirs();
        String body = res.body();
        filePath = filePath + "/" + uuid;
        try (FileWriter fw = new FileWriter(filePath)) {
            fw.write(body);
        } catch (IOException e) {
            e.printStackTrace();
        }
        uriMap.put(res.uri().toString(), 1);
        return filePath;
    }

    /**
     * 得到一个可用的httpClient
     * @param i 标记获得http类型的client 还是 https 的client。 0 代表获得 http 类型， 1 代表获得 https 类型
     * @return
     */
    private HttpClient getHttpClient(int i){
        IP proxyIP = null;
        if (i==0) {
            proxyIP = getProxyHttpIp();
        }else if (i==1){
            proxyIP = getProxyHttpsIp();
        }

        if (proxyIP == null) {
            return HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(3))
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();
        } else {
            System.out.println("gather 使用的ip 代理："+proxyIP);
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(3))
                    .proxy(ProxySelector.of(new InetSocketAddress(proxyIP.getIp(), proxyIP.getPort())))
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();
            boolean useable = checkHttpClient(client);
            if (useable) {
                return client;
            }else {
                return getHttpClient(i);
            }

        }
    }


    /**
     * 从代理池中获得一个 https ip
     * @return
     */
    private IP getProxyHttpsIp(){
        IP proxyIP = null;
        synchronized (this) {
            Enumeration<String> keys = proxyManager.repoHTTPS.keys();
            Iterator<String> iterator = keys.asIterator();
            if (iterator.hasNext()) {
                proxyIP = proxyManager.repoHTTPS.remove(iterator.next());
                ip2 = proxyIP;
            }
        }
        return proxyIP;
    }

    /**
     * 从代理池中获得一个http Ip
     * @return
     */
    private IP getProxyHttpIp(){
        IP proxyIP = null;
        synchronized (this) {
            Enumeration<String> keys = proxyManager.repoHTTP.keys();
            Iterator<String> iterator = keys.asIterator();
            if (iterator.hasNext()) {
                proxyIP = proxyManager.repoHTTP.remove(iterator.next());
                ip1 = proxyIP;
            }
        }
        return proxyIP;
    }

    /**
     * 检测 httpclient 是否可用
     * @param client
     * @return
     */
    private boolean checkHttpClient(HttpClient client){
        String randCheckProxyURI = DEFAULTCHECKPROXYURIS.get(rand.nextInt(DEFAULTCHECKPROXYURIS.size()));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(randCheckProxyURI))
                .timeout(Duration.ofSeconds(3))
                .setHeader("User-Agent", ProxyManager.userAgent.get(ProxyManager.rand.nextInt(ProxyManager.userAgent.size())))
//                .setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; …) Gecko/20100101 Firefox/64.0")
                .setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .setHeader("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2")
                .setHeader("Upgrade-Insecure-Requests", "1")
                .setHeader("If-None-Match", "W/\"a8b7eee808803ac203acb351b10cd050\"")
                .setHeader("Cache-Control", "")
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200 || response.statusCode() == 301 || response.statusCode()==302) {
                return true;
            }else {
                System.out.println("Gather: "+response);
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 下载单个页面
     * @param uri
     * @return
     */
    public String down(String uri) throws IOException, InterruptedException {
        HttpClient client = null;
        if (uri.startsWith("https")){
            client = getHttpClient(1);
        }else if (uri.startsWith("http")) {
            client = getHttpClient(0);
        }else {
            System.out.println("uri错误: "+ uri);
            return null;
        }
        String html = down(uri, client);
        return html;
    }

    /**
     * 下载单个网页, 为run 方法准备切换 client
     * @param uri
     * @return
     */
    private String down(String uri, HttpClient client) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .timeout(Duration.ofSeconds(3))
                .setHeader("User-Agent", ProxyManager.userAgent.get(ProxyManager.rand.nextInt(ProxyManager.userAgent.size())))
                .setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .setHeader("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2")
                .setHeader("Upgrade-Insecure-Requests", "1")
                .setHeader("If-None-Match", "W/\"a8b7eee808803ac203acb351b10cd050\"")
                .setHeader("Cache-Control", "")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return response.body();
        }else {
            System.out.println("down:"+ response);
            return null;
        }
    }

    public List<URI> getNeeddown() {
        return needdown;
    }

    public void setNeeddown(List<URI> needdown) {
        this.needdown = needdown;
    }

    public HashMap<String, Integer> getUriMap() {
        return uriMap;
    }

    public void setUriMap(HashMap<String, Integer> uriMap) {
        this.uriMap = uriMap;
    }

    public Set<String> getExtractURI() {
        return extractURI;
    }

    public void setExtractURI(Set<String> extractURI) {
        this.extractURI = extractURI;
    }
}
