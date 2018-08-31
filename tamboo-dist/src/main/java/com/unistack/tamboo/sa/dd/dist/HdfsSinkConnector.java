package com.unistack.tamboo.sa.dd.dist;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.sa.dd.constant.ConverterType;
import com.unistack.tamboo.sa.dd.constant.DdType;
import com.unistack.tamboo.sa.dd.constant.FormatClass;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.unistack.tamboo.sa.dd.util.DdUtil.succeedResult;

/**
 * @author anning
 * @date 2018/5/22 上午11:31
 * @description: hdfs连接器
 */
public class HdfsSinkConnector extends BaseSinkConnector implements KafkaSink{

    private  Logger logger = LoggerFactory.getLogger(HdfsSinkConnector.class);
    public static  String HDFS_CONNECTOR_CLASS = "HdfsSinkConnector";

    @Override
    public JSONObject checkConfig(DdType ddType,JSONObject dconfig) {





        return succeedResult("  ");
    }



    @Override
    public JSONObject createConfigJson(DdType ddType,JSONObject dconfig,String connectName) {

        int tasks_max = dconfig.getIntValue("tasks_max");
        if (tasks_max < 1) tasks_max = 1;
        int flush_size = dconfig.getIntValue("flush_size");
        if (flush_size<3) flush_size=3;

        JSONObject connectorMeta = new JSONObject(true);
        connectorMeta.put("name", connectName);

        JSONObject connectorConfig = new JSONObject(true);
        connectorConfig.put("connector.class",HDFS_CONNECTOR_CLASS);
        connectorConfig.put("tasks.max",tasks_max);
        connectorConfig.put("topics",dconfig.getString("topics"));
        connectorConfig.put("hdfs.url",dconfig.getString("hdfs_url"));
        if (!StringUtils.isBlank(dconfig.getString("hdfs_path")))
            connectorConfig.put("hdfs.path",dconfig.getString("hdfs_path"));
        connectorConfig.put("flush.size",flush_size);

        //hdfssink 默认设置：key和value的转换器都为string,也可用默认的Json转换器发送带schema的json
        connectorConfig.put("key.converter", ConverterType.STRING);
        connectorConfig.put("value.converter",ConverterType.STRING);

        connectorConfig.put("key.converter.schemas.enable",false);
        connectorConfig.put("value.converter.schemas.enable",false);

        //confluent默认为AvroFormat，生成的文件为.avro
        connectorConfig.put("format.class", FormatClass.STRING);

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
