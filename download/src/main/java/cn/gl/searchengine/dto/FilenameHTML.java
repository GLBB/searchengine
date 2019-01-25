package cn.gl.searchengine.dto;

public class FilenameHTML {

    String fileName;
    String html;

    public FilenameHTML(String fileName, String html) {
        this.fileName = fileName;
        this.html = html;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }
}
