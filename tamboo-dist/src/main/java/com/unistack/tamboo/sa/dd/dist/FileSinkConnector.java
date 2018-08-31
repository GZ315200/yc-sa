package com.unistack.tamboo.sa.dd.dist;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.sa.dd.constant.ConverterType;
import com.unistack.tamboo.sa.dd.constant.DdType;
import com.unistack.tamboo.sa.dd.util.DdUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * @author anning
 * @date 2018/6/2 下午2:53
 * @description: 下发到文件
 */
public class FileSinkConnector extends BaseSinkConnector implements KafkaSink{
    private static String FILE_CONNECTOR_CLASS = "FileStreamSink";

    @Override
    public JSONObject checkConfig(DdType ddType, JSONObject config) {


        return DdUtil.succeedResult("");
    }

    @Override
    public JSONObject createConfigJson(DdType ddType, JSONObject dconfig, String connectorName) {
        String file = dconfig.getString("file");
//        int tasks_max = dconfig.getIntValue("tasks_max");    //下发到文件应该只是单线程
//        if (tasks_max < 1) tasks_max = 1;
        String topics = dconfig.getString("topics");
        JSONObject connectorMeta = new JSONObject(true);
        connectorMeta.put("name", connectorName);

        JSONObject connectorConfig = new JSONObject(true);
        connectorConfig.put("connector.class", FILE_CONNECTOR_CLASS);
        connectorConfig.put("topics", topics);
//        connectorConfig.put("tasks_max", tasks_max);
        connectorConfig.put("file",file);

        //下发到文件默认是使用STRING格式转换器,目前不让用户自定义
        connectorConfig.put("key.converter", ConverterType.STRING.getConverter());
        connectorConfig.put("value.converter",ConverterType.STRING.getConverter());

        //加入ssl验证
        /*if (!StringUtils.isBlank(dconfig.getString("security.protocol")))
            connectorConfig.put("security.protocol",dconfig.getString("security.protocol"));
        connectorConfig.put("ssl.truststore.location",dconfig.getString("ssl.truststore.location"));
        connectorConfig.put("ssl.truststore.password",dconfig.getString("ssl.truststore.password"));
        connectorConfig.put("ssl.keystore.location",dconfig.getString("ssl.keystore.location"));
        connectorConfig.put("ssl.keystore.password",dconfig.getString("ssl.keystore.password"));*/

        //加入sasl acl验证
        connectorConfig.put("com.unistack.tamboo.commons.tools.sasl.jaas.config","");         //jass.conf路径
        connectorConfig.put("com.unistack.tamboo.commons.tools.sasl.mechanism","PLAIN");
        connectorConfig.put("security.protocol","SASL_PLAINTEXT");

        connectorMeta.put("config",connectorConfig);
        return connectorMeta;
    }
}
