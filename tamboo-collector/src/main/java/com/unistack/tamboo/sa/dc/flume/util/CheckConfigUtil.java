package com.unistack.tamboo.sa.dc.flume.util;


import com.unistack.tamboo.sa.dc.flume.common.DcConfig;
import com.unistack.tamboo.sa.dc.flume.common.DcResult;
import org.apache.commons.lang3.StringUtils;

public class CheckConfigUtil {
    private static DcResult dcResult = new DcResult(true,null);
    public static String PATH = "/home/" ;
    /**
     * 检查sink 可变配置
     * @param dcConfig
     * @return
     */
    public static DcResult checkSinkConfig(DcConfig dcConfig) {
        String topic = dcConfig.getConf().getString("topic");
        if ((StringUtils.isBlank(topic))) {
            return  new DcResult(false,"topic must be specified");
        }
        String username = dcConfig.getConf().getString("username");
        if ((StringUtils.isBlank(username))) {
            return  new DcResult(false,"username must be specified");
        }
        String password = dcConfig.getConf().getString("password");
        if ((StringUtils.isBlank(password))) {
            return  new DcResult(false,"password must be specified");
        }
        String brokerServers = dcConfig.getConf().getString("servers");
        if ((StringUtils.isBlank(brokerServers))) {
            return  new DcResult(false,"brokerServers must be specified");
        }
        return dcResult;
    }



}
