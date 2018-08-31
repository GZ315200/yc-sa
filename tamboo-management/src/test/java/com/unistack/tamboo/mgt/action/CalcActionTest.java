package com.unistack.tamboo.mgt.action;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

public class CalcActionTest {

    @Test
    public void test1(){
        JSONObject param = new JSONObject();
        param.put("shellName","start_spark.sh");
        param.put("shellPath","/Users/frank/Desktop/shell");

        CalcAction.startCalc(param.toString());
    }


    @Test
    public void test2(){
        JSONObject param = new JSONObject();
        param.put("shellName","stop_spark.sh");
        param.put("shellPath","/Users/frank/Desktop/shell");
        CalcAction.stopCalc(param.toString());
    }
}