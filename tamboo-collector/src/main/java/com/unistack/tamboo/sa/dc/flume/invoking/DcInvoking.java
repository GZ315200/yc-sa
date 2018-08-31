package com.unistack.tamboo.sa.dc.flume.invoking;


import com.unistack.tamboo.commons.utils.errors.GeneralServiceException;
import com.unistack.tamboo.sa.dc.flume.common.DcConfig;
import com.unistack.tamboo.sa.dc.flume.common.DcResult;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class DcInvoking {

    private String conf_path;   //配置文件地址

    public String getConf_path() {
        return conf_path;
    }

    public void setConf_path(String conf_path) {
        this.conf_path = conf_path;
    }

    //创建采集器
    public DcResult createDataCollector(DcConfig dc) {
        try {
            Class invoke = Class.forName(dc.getDcType().getTypeName());
            DcInvoking invoking;
            if (invoke.newInstance() instanceof DcInvoking) {
                invoking = (DcInvoking) invoke.newInstance();
                return invoking.createDataCollector(dc);
            } else {
                return new DcResult(false, String.format("super Class is not dcInvoking", dc.getDcType().getTypeName(), "ERROR"));
            }
        } catch (Exception  e) {
            throw new GeneralServiceException(e);
        }
    }


    public boolean writeFile(String fieleName, Properties properties) {
        try {
            Iterator<Map.Entry<Object, Object>> it = properties.entrySet().iterator();
            StringBuffer stringBuffer = new StringBuffer();
            while (it.hasNext()) {
                Map.Entry<Object, Object> entry = it.next();
                Object key = entry.getKey();
                Object value = entry.getValue();
                stringBuffer.append(key + "=" + value).append("\n");
            }
            File file = new File("config" + File.separator + fieleName);
            if (file.exists() && file.isFile()) {
                file.delete();
            }
            FileWriter fw = new FileWriter("config" + File.separator + fieleName, true);
            fw.write(stringBuffer.toString());
            fw.flush();
            fw.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean writeProperties(String fieleName, Properties properties) {
        try {
            Iterator<Map.Entry<Object, Object>> it = properties.entrySet().iterator();
            StringBuffer stringBuffer = new StringBuffer();
            while (it.hasNext()) {
                Map.Entry<Object, Object> entry = it.next();
                Object key = entry.getKey();
                Object value = entry.getValue();
                stringBuffer.append(key + " = " + value).append("\n");
            }
            FileOutputStream fileOutputStream = new FileOutputStream("tmp" + File.separator + fieleName);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "utf-8");
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
            String s = new String(stringBuffer.toString().getBytes(), "utf-8");
            bufferedWriter.write(s);
            bufferedWriter.flush();
            fileOutputStream.close();
            outputStreamWriter.close();
            bufferedWriter.close();
            //清楚所有装载的键值对
            properties.clear();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

//    public static void main(String[] args) throws URISyntaxException {
//        BaseConf.BASE_CONF.setProperty("test","zc");
//        DcInvoking dcInvoking = new DcInvoking();
//        boolean b  = dcInvoking.writeProperties("test.properties",BaseConf.BASE_CONF);
//        System.out.println(b);
//        System.out.println(System.getProperty("file.encoding"));
//    }

}
