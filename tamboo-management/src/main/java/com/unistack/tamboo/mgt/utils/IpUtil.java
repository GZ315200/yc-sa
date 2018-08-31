package com.unistack.tamboo.mgt.utils;

/**
 * @program: tamboo-sa
 * @description: 用于监控url校验
 * @author: Asasin
 * @create: 2018-06-29 14:50
 **/
public class IpUtil {
        /**
         * 判断IP地址的合法性，这里采用了正则表达式的方法来判断
         * return true，合法
         * */
        public  static  boolean ipCheck(String text) {
            if (text != null && !text.isEmpty()) {
                // 定义正则表达式
                String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                        +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                        +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                        +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
                // 判断ip地址是否与正则表达式匹配
                if (text.matches(regex)) {
                    // 返回判断信息
                    return true;
                } else {
                    // 返回判断信息
                    return false;
                }
            }
            return false;
        }

    public static void main(String[] args) {
        boolean b = ipCheck("192.168.1.202");
        System.out.println(b);
    }
}
    