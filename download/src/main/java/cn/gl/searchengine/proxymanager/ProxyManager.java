package cn.gl.searchengine.proxymanager;

import cn.gl.searchengine.bean.IP;
import cn.gl.searchengine.dto.FilenameHTML;
import cn.gl.searchengine.filter.IPFilter;
import cn.gl.searchengine.parser.XiciParser;
import cn.gl.searchengine.util.DateUtil;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ProxyManager implements Runnable {

    /**
     * 每天下载页数
     */
    public static final int PAGE = 30;

    /**
     * 一天的所有重ip代理分为多次访问达到目标页数
     */
    private static final int STEP = 10;

    /**
     * 存储 userAgent
     * 会被并发访问，只读
     */
    public static final List<String> userAgent = new ArrayList<>();

    // 存储今天的返回。
    List<HttpResponse> responses = new ArrayList<>();

    // 存储今天未成功的下载的URI
    List<URI> notSuccessUris = new ArrayList<>();

    /**
     * 生成随机数
     */
    public static Random rand = new Random();

    /**
     * 从html 中提取出ip
     */
    private XiciParser parser = new XiciParser();

    /**
     * 过滤ip, 得到可用ip
     */
    private IPFilter filter = new IPFilter();

    /**
     * 保存可用 ip HTTP类型的
     */
    public ConcurrentHashMap<String, IP> repoHTTP = new ConcurrentHashMap<>();

    /**
     * 保存可用Ip 类型为HTTPS类型的Ip
     */
    public ConcurrentHashMap<String, IP> repoHTTPS = new ConcurrentHashMap<>();

    private static ProxyManager manager = new ProxyManager();

    private ProxyManager(){}

    public static ProxyManager getInstance(){
        return manager;
    }


    /**
     * 在ScheduleExcutorService 中调用，防止抛出异常，线程不再被调用，要用try-catch包起来
     */
    @Override
    public void run() {
        try {
            // 从配置文件中获得所有的userAgert
            generateUserAgent("src/main/resources/proxy/user_agents.txt");
            // 得到今天所有的响应，保存到成员变量 responses 中
            downTodayResponse();
            // 判断没有成功响应的URI，重访机制，现略

            // 根据响应得到响应体,html
            List<FilenameHTML> todayHtml = getTodayHtml();
            // 将响应体保存到文件系统中
            writeHtmlToFile(todayHtml);
            // 根据响应体获得提取代理ip
            ArrayList<IP> allIP = parser.getAllIP(todayHtml);
            // 检测可用 ip
            List<IP> usableIP = filter.ipFilter(allIP);
            // 将ip 放入一个地方放着
            saveIP(usableIP);
            // 将某些成员变量刷新，准备明天访问
            responses = new ArrayList<>();
            notSuccessUris = new ArrayList<>();
        }catch (Exception e){
            System.out.println(e);
        }
    }

    /**
     * 将可用ip 保存到成员变量 repoIP 中
     * @param usableIP
     */
    private void saveIP(List<IP> usableIP){
        for (IP ip : usableIP) {
            if (ip.getProtocal() == IP.Protocal.HTTP){
                repoHTTP.put(ip.getIp(), ip);
            }else if (ip.getProtocal() == IP.Protocal.HTTPS){
                repoHTTPS.put(ip.getIp(), ip);
            }
        }
    }

    /**
     * 将今天的所有Html 保存到文件
     * @param todayHtml
     */
    private void writeHtmlToFile(List<FilenameHTML> todayHtml){
        String dateString = DateUtil.getDateString();
        // 创建 2018_12_17 如这样一样的目录
        File dateFile = new File("xici/" + dateString);
        dateFile.mkdir();
        String parentDirec = "xici/"+dateString;
        // 写文件
        for (FilenameHTML filenameHTML : todayHtml) {
            writeFile(filenameHTML, parentDirec);
        }
    }

    /**
     * 写一个文件
     * @param filenameHTML
     * @param parentFileName
     */
    private void writeFile(FilenameHTML filenameHTML, String parentFileName){

        try(
                FileOutputStream fos = new FileOutputStream(parentFileName + "/" + filenameHTML.getFileName());
                OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8")
        ) {

            osw.write(filenameHTML.getHtml());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    /**
     * 根据成员变量 responses ，提取出 html, 并把html 对应的Uri 最后一个数字提取出来
     * @return
     */
    private List<FilenameHTML> getTodayHtml(){
        List<FilenameHTML> list = new ArrayList<>();
        for (HttpResponse response: responses) {
            if (response.body() instanceof  String) {
                String body = (String) response.body();
                // 根据uri得到文件名
                String path = response.uri().getPath();
                String[] segment = path.split("/");
                if (segment.length == 0) {
                    System.out.println("异常片段, uri 切片后长度为0");
                }else {
                    String fileName = segment[segment.length-1];
                    FilenameHTML filenameHTML = new FilenameHTML(fileName, body);
                    list.add(filenameHTML);
                }
            }else {
                System.out.println(response.body());
                System.out.println("不知道body什么类型");
            }
        }

        return list;
    }

    /**
     * 读取resources/proxy/user_agents.txt ， 加载到成员变量 userAgent 中
     * @param userAgentFilePath
     */
    private void generateUserAgent(String userAgentFilePath){
        Path path = Paths.get(userAgentFilePath);
        try {
            BufferedReader bufferedReader = Files.newBufferedReader(path);
            bufferedReader.lines().forEach(line -> {
                userAgent.add(line);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 根据成员变量 1<=x<=PAGE 得到所有的响应
     *
     *
     * i = 1
     * getResponse(1, 11)
     * i = 11
     * getResponse(11, 21)
     * i = 21
     * getResponse(21, 31)
     */
    private void downTodayResponse(){
        int i = 1;
        while (i < PAGE + 1) {
            getResponse(i, i+STEP);
            i +=STEP;
            int sleepTime = 0;
            while (sleepTime < 2) {
                sleepTime = rand.nextInt(5);
            }
            try {
                System.out.println("SleepTime: "+ sleepTime);
                TimeUnit.SECONDS.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 得到 start<=x<end 的页数
     * @param start
     * @param end
     */
    private void getResponse(int start, int end){
        HttpClient client = HttpClient.newBuilder()
//                .connectTimeout(Duration.ofSeconds(3))
                .build();

//        发送多了可能会返回503 或者连接异常
//        所以需要判断状态码和异常
        List<HttpRequest> requests = generateURIs(start, end).stream()
                .map(uri -> {
                    int userAgentIndex = rand.nextInt(userAgent.size());
                    System.out.println(userAgentIndex);
                    // 需要设置代理
                    HttpRequest request = HttpRequest.newBuilder(uri)
//                            .timeout(Duration.ofSeconds(3))
//                            .setHeader("Host", "www.xicidaili.com")
                            .setHeader("User-Agent", userAgent.get(userAgentIndex))
                            .setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                            .setHeader("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2")
//                            .setHeader("Accept-Encoding", "gzip, deflate, br")
//                            .setHeader("Referer", "https://www.xicidaili.com/nn/")
//                            .setHeader("Connection", "keep-alive")
                            .setHeader("Upgrade-Insecure-Requests", "1")
                            .setHeader("If-None-Match", "W/\"a8b7eee808803ac203acb351b10cd050\"")
                            .setHeader("Cache-Control", "")
//                            .setHeader("Referer", "max-age=0")
                            .build();
//                    System.out.println(request.headers());
                    return request;
                }).collect(Collectors.toList());


        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(
                requests.stream()
                        .map(req ->
                                client.sendAsync(req, HttpResponse.BodyHandlers.ofString()).thenAccept(res -> {
                                    // 判断返回状态码
                                    if (res.statusCode() == 200) {
                                        responses.add(res);
                                    } else {
                                        notSuccessUris.add(res.uri());
                                        System.out.println(res.headers());
                                    }
                                    System.out.println(res);
                                }).exceptionally(e -> {
                                    System.out.println("e: " + e);
                                    return null;
                                })
                        )
                        .toArray(CompletableFuture<?>[]::new)
        );
        System.out.println("1---");
        voidCompletableFuture.join();

    }

    /**
     * 生成URI，从 start <= x < end
     * @param start
     * @param end
     * @return
     */
    private List<URI> generateURIs(Integer start, Integer end) {
        List<URI> uris = new ArrayList<>();
        for (int i = start; i < end; i++) {
            uris.add(URI.create("https://www.xicidaili.com/nn/" + i));
        }
        return uris;
    }
}
