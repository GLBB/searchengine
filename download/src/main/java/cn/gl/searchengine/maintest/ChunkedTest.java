package cn.gl.searchengine.maintest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;

public class ChunkedTest {
//    uri: https://blog.csdn.net/qq_34845121
//    uri: http://www.importnew.com/20339.html
//    uri: http://ifeve.com/java8-stream-%e4%b8%adspliterator%e7%9a%84%e4%bd%bf%e7%94%a8%e4%ba%8c/
//    uri: https://developer.ibm.com/recipes/tutorials/realtime-anomaly-detection-on-the-iot-edge-using-nodered-and-moving-zscore/?cm_sp=dw-dwtv-_-data-science-_-code-anomaly-detection-algorithm
//    uri: http://www.runoob.com/jeasyui/jeasyui-layout-tabs3.html
//    uri: http://www.csdn.net/tag/profiling
//    uri: http://www.w3school.com.cn/xsl/xsl_transformation.asp
//    uri: https://www.cnblogs.com/f-ck-need-u/p/9757959.html

    public static void main(String[] args) {
        var client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(3))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://www.runoob.com/jeasyui/jeasyui-layout-tabs3.html"))
                .timeout(Duration.ofSeconds(3))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(res->{
            System.out.println(res.headers());
            System.out.println(res.body());
        }).join();
    }
}
