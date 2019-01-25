package cn.gl.searchengine.down;

import org.junit.Test;
import org.junit.runner.Request;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GatherTest {

    /**
     * https://baijiahao.baidu.com/s?id=1600690207117771729&wfr=spider&for=pc
     */
    @Test
    public void test1(){
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://www.w3school.com.cn/"))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(res->{
            System.out.println(res.statusCode());
            System.out.println(res.headers());
            String s = res.headers().firstValue("content-type").get();
            String[] split = s.split(";");
            String charset = split[1];
            String[] split1 = charset.split("=");
            charset = split1[1];
            System.out.println(charset);

        }).join();
    }
}
