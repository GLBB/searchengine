package cn.gl.searchengine.dto;

public class Seed {

    private String url;
    private String sitemap;
    /**
     * sitemap 是否是多重目录结构
     */
    private Mutiple include;

    public enum Mutiple{
        YES,NO
    }

    public Seed(String url, String sitemap) {
        this.url = url;
        this.sitemap = sitemap;
    }

    public Seed(String url, String sitemap, Mutiple include) {
        this.url = url;
        this.sitemap = sitemap;
        this.include = include;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSitemap() {
        return sitemap;
    }

    public void setSitemap(String sitemap) {
        this.sitemap = sitemap;
    }

    public Mutiple getInclude() {
        return include;
    }

    public void setInclude(Mutiple include) {
        this.include = include;
    }
}
