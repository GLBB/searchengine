package cn.gl.searchengine.mapper;

import cn.gl.searchengine.dto.GraspPage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FilePageMapperTest {

    @Autowired
    FilePageMapper filePageMapper;

    @Test
    public void test1(){
        GraspPage graspPage = new GraspPage("F:\\IDEA_WorkPlace\\searchengine\\html_repo\\2018_12_20\\afc8408b-1f45-4d7c-ac5b-c6a4d01d67eb", "https://www.cnblogs.com/lbf1994/articles/5677453.html", new Date());
        filePageMapper.insert(graspPage);
    }

    @Test
    public void test2(){
        GraspPage graspPage = filePageMapper.getGraspPageByPath("html_repo/2018_12_21/26fee92f-a0f5-4cea-8f67-416272f4d233");
        System.out.println(graspPage);
    }

}