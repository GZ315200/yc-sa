package com.unistack.calc.dataclean;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.compute.process.until.dataclean.filter.impl.RemoveFilter;
import org.junit.Test;

public class DataCleanTest {



    @Test
    public void testRemove(){
        RemoveFilter rf = new RemoveFilter();
        String config = "{\"type\":\"Remove\",\"filedCount\":2,\"fields\":[{\"removeFiled\":\"id\"}]}";
        rf.init(JSONObject.parseObject(config));

        JSONObject target = new JSONObject();
        target.put("id","11");
        target.put("name","李狗娃");
        target.put("age","8");

        rf.filter(target);

        System.out.println(target);

    }
}
