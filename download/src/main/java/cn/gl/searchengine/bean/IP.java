package cn.gl.searchengine.bean;

import java.io.Serializable;
import java.util.Date;

public class IP implements Serializable {
    String contry;
    String ip;
    Integer port;
    String address;
    String anonymouseLevel;
//    String protocal;
    Protocal protocal;
    Double speed;
    Double connectTime;
    Long survivalTime; // 以分钟为单位
    Date vertificationTime;

    public enum Protocal{
        HTTP,HTTPS,OTHER
    }

    @Override
    public String toString() {
        return "IP{" +
                "contry='" + contry + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", address='" + address + '\'' +
                ", anonymouseLevel='" + anonymouseLevel + '\'' +
                ", protocal='" + protocal + '\'' +
                ", speed=" + speed +
                ", connectTime=" + connectTime +
                ", survivalTime=" + survivalTime +
                ", vertificationTime=" + vertificationTime +
                '}';
    }

    public IP(String ip, Integer port, Protocal protocal) {
        this.ip = ip;
        this.port = port;
        this.protocal = protocal;
    }

    public String getContry() {
        return contry;
    }

    public void setContry(String contry) {
        this.contry = contry;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAnonymouseLevel() {
        return anonymouseLevel;
    }

    public void setAnonymouseLevel(String anonymouseLevel) {
        this.anonymouseLevel = anonymouseLevel;
    }

    public Protocal getProtocal() {
        return protocal;
    }

    public void setProtocal(Protocal protocal) {
        this.protocal = protocal;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Double getConnectTime() {
        return connectTime;
    }

    public void setConnectTime(Double connectTime) {
        this.connectTime = connectTime;
    }

    public Long getSurvivalTime() {
        return survivalTime;
    }

    public void setSurvivalTime(Long survivalTime) {
        this.survivalTime = survivalTime;
    }

    public Date getVertificationTime() {
        return vertificationTime;
    }

    public void setVertificationTime(Date vertificationTime) {
        this.vertificationTime = vertificationTime;
    }
}
