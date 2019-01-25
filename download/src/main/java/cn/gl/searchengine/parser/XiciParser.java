package cn.gl.searchengine.parser;


import cn.gl.searchengine.bean.IP;
import cn.gl.searchengine.dto.FilenameHTML;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.util.ArrayList;
import java.util.List;

public class XiciParser {

    /**
     * 将全部html 中的ip 提取出来
     * @param htmlList
     * @return
     */
    public ArrayList<IP> getAllIP(List<FilenameHTML> htmlList) {
        ArrayList<IP> ipList = new ArrayList<>();
        htmlList.forEach(filenameHTML -> {
            extractIP(filenameHTML.getHtml(), ipList);
        });
        return ipList;
    }

    /**
     * 为 getAllIP 方法准备的
     * @param html
     * @param ipList
     */
    private void extractIP(String html, List<IP> ipList){
        Document doc = Jsoup.parse(html);
        Elements trs = doc.select("#ip_list").select("tbody").first().children();
        for (int i = 0; i < trs.size(); i++) {
            if (i==0) {
                continue;
            }
            var tr = trs.get(i);
            var tds = tr.children();

            Element countryTd = tr.select(".country").first();
            String country = getCountry(countryTd);

            Element ipTd = tds.get(1);
            String ip = getIP(ipTd);

            Element portElem = tds.get(2);
            Integer port = getPort(portElem);

            Element addrElem = tds.get(3);
            String addr = getAddr(addrElem);

            Element anonymouseLevelElem = tds.get(4);
            String anonymouseLevel = anonymouseLevelElem.text();

//            String protocal = tds.get(5).text();
            IP.Protocal protocal = getProtocal(tds.get(5));

//            Double speed = getSpeed(tds.get(6));

//            Double connectTime = getConnectTime(tds.get(7));

//            IP ipEntity = new IP(country, ip, port, addr, anonymouseLevel, protocal, 0.0, 0.0, 0L, null);
            IP ipEntity = new IP(ip, port, protocal);

            ipList.add(ipEntity);
        }

    }




    private String getCountry(Element contryElem){
        try {
            var srcCountry = contryElem.select("img").first().attr("src");
            if (srcCountry.contains("cn")){
                return "China";
            }else {
                return "unknown";
            }
        }catch (NullPointerException e){
            return "unknown";
        }
    }

    private String getIP(Element ipElem){
        var ip = ipElem.text();
        return ip;
    }

    private Integer getPort(Element portElem){
        var portStr = portElem.text();
        return Integer.parseInt(portStr);
    }

    private String getAddr(Element addrElem){
        return addrElem.text();
    }

    private Double getSpeed(Element speedElem){
        String speed = speedElem.select("div").first().attr("title");
        return Double.parseDouble(speed);
    }

    private Double getConnectTime(Element connectTimeElem) {
        String connnectTime = connectTimeElem.select("div").first().attr("title");
        return Double.parseDouble(connnectTime);
    }

    private Long getSurvivalTime(Element survivalTimeElem) {
        String survivalTimeStr = survivalTimeElem.text();
//        if (survia)
        return null;
    }

//    public IP getIP(String country, String ip, Integer port, String addr, String anonymouseLevel, ){
//        IP ip = new IP();
//    }

    private IP.Protocal getProtocal(Element protocalElem){
        String protocalStr = protocalElem.text();
        if ("HTTP".equals(protocalStr.toUpperCase())){
            return IP.Protocal.HTTP;
        }else if ("HTTPS".equals(protocalStr.toUpperCase())){
            return IP.Protocal.HTTPS;
        }else {
            return IP.Protocal.OTHER;
        }
    }

}
