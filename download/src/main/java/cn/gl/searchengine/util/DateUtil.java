package cn.gl.searchengine.util;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static String getDateString(){
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");
        String format = dateFormat.format(date);
        return format;
    }

    public static void main(String[] args) {
        String dateString = getDateString();
        // 创建文件夹
        File f = new File("xici/"+dateString);
        if (!f.exists()) {

        }

        String s = "123452555544";


        try(
                FileWriter fw = new FileWriter("xici/" + dateString + "/test");
        ){
            fw.write(s);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
