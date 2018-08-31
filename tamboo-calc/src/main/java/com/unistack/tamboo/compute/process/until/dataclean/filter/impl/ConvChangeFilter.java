package com.unistack.tamboo.compute.process.until.dataclean.filter.impl;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.compute.process.until.dataclean.filter.Filter;
import java.util.Iterator;

/**
 * @ClassName ConvChangeFilter
 * @Description TODO  {"type":"ConvChange","filedCount":1 ,"fields":[{"sourceField":"输入的值#int","toBase":"需要转化的进制#int"}]}
 * @Author unistack
 * @Date 2018/7/20 9:17
 * @Version 1.0
 */
public class ConvChangeFilter implements Filter {
    private String sourceField = "sourceField";
    private String toBase = "toBase";
    /**
     * @author      cyq
     * @methodDec   初始化方法
     * @param        config 配置过滤文件
     * @return
     * @exception
     * @date        2018/7/23 15:34
     */
    @Override
    public void init(JSONObject config) throws Exception {
        JSONObject jsonConfig = config.getJSONArray("fields").getJSONObject(0);
        initMethod(jsonConfig);

    }
    /**
     * @author      cyq
     * @methodDec   真正的需要过滤的值
     * @param        source   过滤值
     * @return
     * @exception
     * @date        2018/7/23 15:35
     */
    @Override
    public JSONObject filter(JSONObject source) throws Exception {
        if (source.containsKey(sourceField) && source.containsKey(toBase)) {
            String sourceFieldValue = source.getString(sourceField);
            String toBaseValue = source.getString(toBase);
            String system = changeSystem(sourceFieldValue, toBaseValue);
            source.put(sourceField, sourceFieldValue);
            source.put(toBase, system);
            return source;
        } else {
            source.put("msg", "key不存在！");
            return source;

        }
    }
    /**
     * @author      cyq
     * @methodDec   过滤条件的逻辑处理
     * @param       sourceFieldValue   输入的值    toBaseValue 转换进制的值
     * @return
     * @exception
     * @date        2018/7/23 15:38
     */
    private String changeSystem(String sourceFieldValue, String toBaseValue) {
        int sourceFieldSystem = Integer.parseInt(sourceFieldValue);
        int toBaseSystem = Integer.parseInt(toBaseValue);

        String system = "";

        switch (toBaseSystem) {
            case 2:
                system = Integer.toBinaryString(sourceFieldSystem);
                //判断一下：如果转化为二进制为0或者1或者不满8位，要在数后补0
                int bit = 8 - system.length();
                if (system.length() < 8) {
                    for (int j = 0; j < bit; j++) {
                        system = "0" + system;
                    }
                }
                break;
            case 8:
                system = Integer.toOctalString(sourceFieldSystem);
                break;
            case 10:
                system = sourceFieldValue;
                break;
            case 16:
                system = Integer.toHexString(sourceFieldSystem);
                break;
            default:
                System.out.println("无此进制");

        }

        return system;


    }
    /**
     * @author      cyq
     * @methodDec   初始化配置文件拿到key值
     * @param        jsonConfig  初始化文件
     * @return
     * @exception
     * @date        2018/7/23 15:34
     */
    private void initMethod(JSONObject jsonConfig) {
        Iterator<String> iteratorConfig = jsonConfig.keySet().iterator();
        String[] str = new String[jsonConfig.size()];
        int count = 0;
        while (iteratorConfig.hasNext()) {
            String key = iteratorConfig.next();
            str[count++] = key;
        }
        for (int i = 0; i < str.length; i++) {
            if (sourceField.equals(str[i])) {
                sourceField = str[i];
            }

            if (toBase.equals(str[i])) {
                toBase = str[i];
            }
        }
    }


}
