package cn.gl.searchengine.dto;

import java.util.Date;

public class GraspPage {

    String filePath;
    String url;
    Date downdate;

    public GraspPage() {
    }

    public GraspPage(String filePath, String url, Date date) {
        this.filePath = filePath;
        this.url = url;
        this.downdate = date;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getDowndate() {
        return downdate;
    }

    public void setDowndate(Date downdate) {
        this.downdate = downdate;
    }

    @Override
    public String toString() {
        return "GraspPage{" +
                "filePath='" + filePath + '\'' +
                ", url='" + url + '\'' +
                ", downdate=" + downdate +
                '}';
    }
}
