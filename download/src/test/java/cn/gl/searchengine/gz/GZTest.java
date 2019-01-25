package cn.gl.searchengine.gz;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.zip.GZIPInputStream;

public class GZTest {

    /**
     * 解析gz 文件
     */
    @Test
    public void test1() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://www.importnew.com/sitemap.xml.gz"))
                .build();

        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
        InputStream in = new GZIPInputStream(response.body());
        byte[] bytes = in.readAllBytes();
        String str = new String(bytes, "UTF-8");
        System.out.println(str);


    }

    /**
     * 从
     */
    @Test
    public void test2() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://www.importnew.com/sitemap.xml.gz"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpHeaders headers = response.headers();
        System.out.println(headers);
        String body = response.body();
        System.out.println(body);
        ByteArrayInputStream bais = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));

        GZIPInputStream gzipInputStream = new GZIPInputStream(bais);
        byte[] bytes = gzipInputStream.readAllBytes();
        String s = new String(bytes, "UTF-8");
        System.out.println(s);
    }
}
