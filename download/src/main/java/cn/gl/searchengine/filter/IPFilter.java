package cn.gl.searchengine.filter;

import cn.gl.searchengine.bean.IP;
import cn.gl.searchengine.proxymanager.ProxyManager;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;

public class IPFilter {

    /**
     * 筛选可用ip所用到的线程数
     */
    private static final int THREADNUM = 3;

    /**
     * 筛选出可用Ip
     * @param proxyIPs
     * @return
     */
    public List<IP> ipFilter(List<IP> proxyIPs){
        // 并发添加
        CopyOnWriteArrayList<IP> usableIP = new CopyOnWriteArrayList();
        ExecutorService executorService = Executors.newFixedThreadPool(THREADNUM);

        int i = 0;
        while (i < proxyIPs.size()) {
            Runnable[] tasks = new Runnable[THREADNUM];
            for (int j = 0; j < THREADNUM; j++) {
                int temp = i;
                tasks[j] = ()->{
                    checkIP(proxyIPs.get(temp), usableIP);
                };
                System.out.println(i);
                i++;
            }
            for (int j = 0; j < THREADNUM; j++) {
                executorService.execute(tasks[j]);
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        executorService.shutdown();
        return usableIP;

    }

    /**
     * 将ip发送给百度检验 代理是否可用
     * @param ip
     * @param usableIP
     */
    private void checkIP(IP ip, List<IP> usableIP){
        System.out.println("check---" + ip);
        HttpClient client = HttpClient.newBuilder()
                .proxy(ProxySelector.of(new InetSocketAddress(ip.getIp(), ip.getPort())))
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .header("User-Agent", ProxyManager.userAgent.get(ProxyManager.rand.nextInt(ProxyManager.userAgent.size())))
                .setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .setHeader("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2")
                .setHeader("Accept-Encoding", "gzip, deflate, br")
//                            .setHeader("Referer", "https://www.xicidaili.com/nn/")
//                            .setHeader("Connection", "keep-alive")
                .setHeader("Upgrade-Insecure-Requests", "1")
                .setHeader("If-None-Match", "W/\"a8b7eee808803ac203acb351b10cd050\"")
                .setHeader("Cache-Control", "")
                .uri(URI.create("https://www.baidu.com/"))
                .timeout(Duration.ofSeconds(5))
                .build();
        try {
            System.out.println("检测ip " + ip);
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // 加上301 302 是为了测试 http 型代理访问 https 服务
            if (response.statusCode() == 200 || response.statusCode() == 301 || response.statusCode() == 302) {
                usableIP.add(ip);
            }

            // 为了测试响应的ip 是否可用，一下代码为测试代码
            System.out.println(response);
        } catch (IOException e) {
            boolean flag = true;
            if(e instanceof HttpConnectTimeoutException){
                flag = false;
            }else if (e instanceof ConnectException){
                flag = false;
            }
            if (flag) {
                e.printStackTrace();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
