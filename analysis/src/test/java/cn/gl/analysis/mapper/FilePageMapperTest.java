package cn.gl.analysis.mapper;

import cn.gl.analysis.vo.FilepathURL;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
public class FilePageMapperTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    FilePageMapper filePageMapper;

    @Test
    public void test1(){
        Set<String> allFilename = filePageMapper.getAllFilename();
        System.out.println(allFilename);
        System.out.println();
    }

    @Test
    public void test2(){
        Set<FilepathURL> allFilepathURL = filePageMapper.getAllFilepathURL();
        System.out.println();
    }

}