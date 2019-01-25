package cn.gl.analysis.writer;

import cn.gl.analysis.dto.ExtractContent;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

public class WriterTest {

    @Test
    public void test1(){
        String path = "";
        try(
                FileInputStream fis = new FileInputStream("../analysis_repo/2018_12_22/d3a787e0-3449-437d-ad7c-19514b44ab07");
                ObjectInputStream ois = new ObjectInputStream(fis);
        ) {
            ExtractContent extractContent = (ExtractContent) ois.readObject();
            System.out.println();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test2(){
        String s = "    ss   ss ";
        s = s.replaceAll(" +", " ");
        System.out.println(s);
    }


}
