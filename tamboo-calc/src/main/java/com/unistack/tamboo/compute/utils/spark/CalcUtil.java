package com.unistack.tamboo.compute.utils.spark;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author hero.li
 * 这个类放置一下计算模块专有的工具方法
 */
public class CalcUtil {
    public static String readConfig(){
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try{
            String line;
            br = new BufferedReader(new FileReader("config"+ File.separator+"config.json"));
            while((line = br.readLine()) != null){
                sb.append(line);
            }
        }catch(Exception e){e.printStackTrace();}{if(null != br){try{br.close();}catch (IOException e) {e.printStackTrace();}}}
        return sb.toString();
    }
}
