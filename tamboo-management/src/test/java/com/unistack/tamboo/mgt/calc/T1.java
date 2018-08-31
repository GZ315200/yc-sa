package com.unistack.tamboo.mgt.calc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.commons.utils.TambooConfig;
import com.unistack.tamboo.mgt.model.calc.OfflineDataSource;
import org.junit.Test;

public class T1 {


    @Test
    public void test2(){
        System.out.println("=====");
    }


    @Test
    public void test3(){
        OfflineDataSource source = new OfflineDataSource();
        Object o = JSON.toJSON(source);
        System.out.println(o);
    }

    @Test
    public void test4(){
        System.out.println(TambooConfig.CALC_MASTER_IP);
    }
}