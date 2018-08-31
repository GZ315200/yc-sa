package com.unistack.tamboo.compute.utils.commons;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author hero.li
 * 这是一个数据转换的工具类 <br/>
 * 使用Java8里的日期时间API <br/>
 *
 * **特别说明**
 * * 下面有些方法名使用大写,千万不要修改,因为这些方法需要在配置文件中通过反射类获取
 * *
 */
public class Convert implements java.io.Serializable{
    private static  AtomicLong COUNTER = new AtomicLong(0);
    private  DateTimeFormatter standard_data_format = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
    private  DateTimeFormatter yyyyMMdd_data_format = DateTimeFormatter.ofPattern("yyyyMMdd");
    private  DateTimeFormatter yyyy_MM_dd_HH_mm_data_format = DateTimeFormatter.ofPattern("yyyyy/MM/dd HH:mm");


   private LocalDateTime FORMAT_DATE(String datetime,String pattern){
      DateTimeFormatter format = getDateFormat(pattern);
      LocalDateTime dateTime = LocalDateTime.parse(datetime, format);
      return dateTime;
   }

   private DateTimeFormatter getDateFormat(String pattern){
       String type1 = "yyyyMMdd";
       if(type1.equals(pattern)){
           return yyyyMMdd_data_format;
       }

       String type2 = "yyyy-MM-dd hh:mm:ss";
       if(type2.equals(pattern)){
           return standard_data_format;
       }

       String type3 = "yyyy/MM/dd HH:mm";
       if(type3.equals(pattern)){
           return yyyy_MM_dd_HH_mm_data_format;
       }
       return DateTimeFormatter.ofPattern(pattern);
   }


   public String FORMAT_DATE(String time, String currentPatter, String newPatter){
       DateTimeFormatter currentFormat = getDateFormat(currentPatter);
       LocalDateTime currentPatterDateTime = LocalDateTime.parse(time,currentFormat);

       DateTimeFormatter newFormat = getDateFormat(newPatter);
       String newPatterDateTime = currentPatterDateTime.format(newFormat);
       return newPatterDateTime;
   }

   public String NOW(String pattern){
       LocalDateTime now = LocalDateTime.now();
       DateTimeFormatter format = getDateFormat(pattern);
       return now.format(format);
   }

   public String NOW(){
        return NOW("yyyyMMddHHmm");
   }

   public String NEXT_VAL(){
       long value = COUNTER.incrementAndGet();
       return String.valueOf(value);
   }

    /**
     * 生成唯一的数字标识符，范围[1,max]，超过后从1重新开始<br/>
     * @param maxValue
     * @return
     */
   public String NEXT_VAL(String maxValue){
        long value = COUNTER.incrementAndGet() % Long.parseLong(maxValue);
        return (value == 0) ? String.valueOf(maxValue) : String.valueOf(value);
   }

}