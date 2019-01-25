package cn.gl.analysis.test;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogTest {

    static Logger log = LoggerFactory.getLogger(LogTest.class);
    public static void main(String[] args) {
        log.trace("-----trace");
        log.debug("-----debug");
        log.info("-----info");
        log.warn("-----warn");
        log.error("----error");

        log.warn("file: {}, hello: {}", "hh", "world");

    }
}
