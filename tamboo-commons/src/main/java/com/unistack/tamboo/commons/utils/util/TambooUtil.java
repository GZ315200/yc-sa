package com.unistack.tamboo.commons.utils.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class TambooUtil {

    private static String key;
    private static int validityDay = 10;

    private static String md5(String key) {
        if (key == null || key.length() == 0) {
            throw new IllegalArgumentException("String to encrypt cannot be null or zero length");
        }
        StringBuffer hexString = new StringBuffer();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(key.getBytes());
            byte[] hash = md.digest();

            for (int i = 0; i < hash.length; i++) {
                if(i>0 && i%2==0){
                    hexString.append("-");
                }
                hexString.append(Integer.toHexString(0xFF & hash[i]));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return hexString.toString().toUpperCase();
    }

    public static String encrypt(String key, int validityDay) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH,validityDay);
        String dateStr = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
        return md5(key + dateStr);
    }

    public static boolean validate(Map<String, String> map){
    	return validate(map.get("key"), map.get("license"));
    }
    
    public static boolean validate(String key,String license){
        int i = 0;
        while(i<1000){
            if(encrypt(key,i).equals(license)){
            	System.out.println("Residual validate day is " + i);
                return true;
            }
            i++;
        }
        return false;
    }

    public static void main(String[] args) throws Exception{
        if(args.length > 0){
        	List<String> list = Arrays.asList(args);
        	if(list.contains("-key")){
        		key = list.get(list.indexOf("-key")+1);
        	}
        	if(list.contains("-validity.day")){
        		validityDay = Integer.parseInt(list.get(list.indexOf("-validity.day")+1));
        	}
            System.out.println(encrypt(key, validityDay));
        }else{
            //System.out.println("Please set parameter, -key or -validity.day(default is 90).");
            System.out.println("Please contact info@unistacks.com if you need license.");
        }

//    	validate("unistack","A8C2-277-FD7A-BED3-F5C6-7212-29CC-3A60");
    	
    }
}
