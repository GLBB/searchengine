package cn.gl.analysis.holder;

import cn.gl.analysis.mapper.FilePageMapper;
import cn.gl.analysis.vo.FilepathURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

@Service
public class FilepathURIHolder {

    private FilepathURIHolder(){}

//    private static FilepathURIHolder filepathURIHolder = new FilepathURIHolder();

    private Logger logger = LoggerFactory.getLogger("cn.gl.analysis.holder.FilepathURIHolder");

    private HashMap<String, String> filepathURLMap = new HashMap<>();

    private static volatile boolean init = false;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private FilePageMapper filePageMapper;

    public void init(){
        Set<FilepathURL> allFilepathURL = filePageMapper.getAllFilepathURL();
        Iterator<FilepathURL> iterator = allFilepathURL.iterator();
        while (iterator.hasNext()) {
            FilepathURL filepathURL = iterator.next();
            String filepath = filepathURL.getFilepath();
            String url = filepathURL.getUrl();
            if (filepath != null && url != null) {
                filepathURLMap.put(filepath, url);
            }else {
                logger.warn("filepath: {}, url: {}", filepath, url);
            }
        }
        init = true;
    }

//    @Bean
//    public synchronized static FilepathURIHolder getInstance(){
//        if (init) {
//            return filepathURIHolder;
//        }else {
//            filepathURIHolder.init();
//            return filepathURIHolder;
//        }
//    }

    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    public HashMap<String, String> getFilepathURLMap() {
        return filepathURLMap;
    }

    public void setFilepathURLMap(HashMap<String, String> filepathURLMap) {
        this.filepathURLMap = filepathURLMap;
    }
}
