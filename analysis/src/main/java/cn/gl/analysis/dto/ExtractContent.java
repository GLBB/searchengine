package cn.gl.analysis.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExtractContent implements Serializable {

    private static final long serialVersionUID = 6529685098267757690L;

    private String uri;
    private String fileName;
    private String title;
    /**
     * key 是链接
     * value 是()-|-()
     */
    private Map<String, String> anchorTextContent;
    private Set<String> anchor;
//    private Map<String, String> titleText = new HashMap<>();

    // 正文
    private String text;

    // description
    private String description;

    // keywords
    private String keywors;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Map<String, String> getAnchorTextContent() {
        return anchorTextContent;
    }

    public void setAnchorTextContent(Map<String, String> anchorTextContent) {
        this.anchorTextContent = anchorTextContent;
    }

    public Set<String> getAnchor() {
        return anchor;
    }

    public void setAnchor(Set<String> anchor) {
        this.anchor = anchor;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKeywors() {
        return keywors;
    }

    public void setKeywors(String keywors) {
        this.keywors = keywors;
    }

    @Override
    public String toString() {
        return "ExtractContent{" +
                "uri='" + uri + '\'' +
                ", fileName='" + fileName + '\'' +
                ", title='" + title + '\'' +
                ", anchorTextContent=" + anchorTextContent +
                ", anchor=" + anchor +
                ", text='" + text + '\'' +
                ", description='" + description + '\'' +
                ", keywors='" + keywors + '\'' +
                '}';
    }
}
