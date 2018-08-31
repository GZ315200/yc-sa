package com.unistack.tamboo.sa.dc.flume.source;

import com.unistack.tamboo.commons.utils.TambooConfig;
import com.unistack.tamboo.sa.dc.flume.common.DcConfig;
import com.unistack.tamboo.sa.dc.flume.common.DcResult;
import com.unistack.tamboo.sa.dc.flume.common.DcType;
import com.unistack.tamboo.sa.dc.flume.invoking.DcInvoking;
import com.unistack.tamboo.sa.dc.flume.invoking.FlumeInvoking;
import com.unistack.tamboo.sa.dc.flume.model.Http;
import com.unistack.tamboo.sa.dc.flume.util.CheckConfigUtil;
import com.unistack.tamboo.sa.dc.flume.util.PropertiesUtil;
import com.unistack.tamboo.sa.dc.flume.util.SellOperateUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import static com.unistack.tamboo.sa.dc.flume.util.CheckConfigUtil.PATH;

/**
 * @program: tamboo-sa
 * @description: 用于http数据采集
 * @author: Asasin
 * @create: 2018-08-04 14:36
 **/
public class HttpSource extends DcInvoking implements FlumeInvoking {
    private Logger logger = LoggerFactory.getLogger(ExecSource.class);
    public Http http = new Http();
    private String sourceName = "httpSource";
    private String channelName = "httpChannel";
    private String httpConfName = "http.properties";
    private String flumePath = null;

    @Override
    public DcResult createDataCollector(DcConfig dc) {
        DcResult dcResult = createConf(dc);
        if (dcResult.isCode()) {
            return startCollector(dc);
        }
        return dcResult;
    }


    @Override
    public DcResult checkConfig(DcConfig dc) {
        DcResult dcResult = CheckConfigUtil.checkSinkConfig(dc);
        //校验source可变配置和DcType
        String type = dc.getConf().getString("type");
        if ((dc.getDcType() == null) && dc.getDcType() != (DcType.HTTP)
                && StringUtils.isBlank(type) && !type.equals("HTTP")) {
            return new DcResult(false, "dcType must be specified right");
        }

        String inputCharset = dc.getConf().getString("inputCharset");
        if ((StringUtils.isBlank(inputCharset))) {
            http.setInputCharset("utf-8");
        }
        http.setInputCharset(inputCharset);
        String bind = dc.getConf().getString("bind");
        System.out.println(bind);
        if ((StringUtils.isBlank(bind))) {
            return new DcResult(false, "bind must be specified");
        }
        http.setBind(bind);
        String port = dc.getConf().getString("port");
        System.out.println(bind);
        if ((StringUtils.isBlank(port))) {
            return new DcResult(false, "port must be specified");
        }
        http.setPort(port);
        //校验主机配置
        String path = dc.getConf().getString("path");
        if ((StringUtils.isBlank(path))) {
            http.setPath(PATH);
        }
        http.setPath(path);
        String httpPort = dc.getConf().getString("httpPort");
        if ((StringUtils.isBlank(httpPort))) {
            return new DcResult(false, "httpPort");
        }
        http.setHttpPort(httpPort);
        return dcResult;
    }


    @Override
    public DcResult createConf(DcConfig dc) {
        DcResult dcResult = checkConfig(dc);
        //other
        http.setTopic(dc.getConf().getString("topic"));
        http.setUsername(dc.getConf().getString("username"));
        http.setPassword(dc.getConf().getString("password"));
        http.setServers(dc.getConf().getString("servers"));
        //source
        PropertiesUtil propertiesUtil = new PropertiesUtil();
        Properties properties = propertiesUtil.getBaseConf();

        //写入properties
        //source
        properties.setProperty("agent.sources",sourceName);
        properties.setProperty("agent.sources."+sourceName+".type", "http");
        properties.setProperty("agent.sources."+sourceName+".inputCharset", http.getInputCharset());
        properties.setProperty("agent.sources."+sourceName+".bind", http.getBind());
        properties.setProperty("agent.sources."+sourceName+".port", http.getPort());
        //channel
        properties.setProperty("agent.channels", channelName);
        properties.setProperty("agent.channels." + channelName + ".type", "memory");
        properties.setProperty("agent.channels." + channelName + ".capacity", "1000000");
        properties.setProperty("agent.channels." + channelName + ".transactionCapacity", "1000000");
        //sink
        properties.setProperty("agent.sinks.kafkaSink.kafka.topic", http.getTopic());
        properties.setProperty("agent.sinks.kafkaSink.kafka.topic.acl.username", http.getUsername());
        properties.setProperty("agent.sinks.kafkaSink.kafka.topic.acl.password", http.getPassword());
        properties.setProperty("agent.sinks.kafkaSink.kafka.bootstrap.servers", http.getServers());
        //绑定
        properties.setProperty("agent.sources." + sourceName + ".channels", channelName);
        properties.setProperty("agent.sinks.kafkaSink.channel", channelName);
        //写入natcat.prperties新配置文件
        writeProperties(httpConfName,properties);
        String classPath = NetCatSource.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (classPath.contains(".jar")) {
            String execConfigPath = TambooConfig.COLLECT_FLUME_PATH+httpConfName;
            System.out.println(execConfigPath);
            setConf_path(execConfigPath);
            flumePath = TambooConfig.DATASOURCE_CONF_PATH;
            System.out.println(flumePath);
        }else {
            String execConfigPath = classPath + "../../../tmp/"+httpConfName;
            setConf_path(execConfigPath);
            flumePath = classPath + "../../flume.tar.gz";
        }
        return dcResult;
    }

    @Override
    public DcResult startCollector(DcConfig dcConfig) {
        //get
        String httpPort = dcConfig.getConf().getString("httpPort");
        String path = http.getPath();
        String configPath = getConf_path();
        //local check flume.tar.gz
        String opinionFile = "[ -f ~/flume.tar.gz ] && echo 'Found' || echo 'Not found'";
        DcResult dcResult1 = SellOperateUtil.execCommand(opinionFile);
        if(("[Not found]".equals(dcResult1.getMsg()))||(!dcResult1.isCode())){
            String unpackFlume = "tar -zxvf "+flumePath+" -C "+path + " > /dev/null 2>&1 &";
            SellOperateUtil.execCommand(unpackFlume);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                return new DcResult(false, "unpackFlume failed："+e);
            }
        }
        //local send source config to The remote
        String sendConf = "cp -r " + configPath + " " + path + "flume/conf\n";
        SellOperateUtil.execCommand(sendConf);
        //local start
        String execstart2 = path + "flume/bin/flume-ng agent -c " + path + "flume/conf/ -f " + path + "flume/conf/"+httpConfName+" -n " + "agent -Dflume.monitoring.type=http -Dflume.monitoring.port=" + httpPort + " -Dflume.root.logger=INFO,console > " + path + "flume/logs/flume.log 2>&1 &";
        SellOperateUtil.execCommand(execstart2);
        //local check pid
        String pid = "ps -ef | grep " + httpPort + " | grep -v grep | awk  '{print $2}'";
        DcResult dcResult2 = SellOperateUtil.execCommand(pid);
        if (StringUtils.isBlank(dcResult2.getMsg())||(!dcResult2.isCode())) {
            return new DcResult(false, "start agent failed ,cause can not get pid");
        }
        return new DcResult(true, "start agent successed");
    }

    /**
     * 停止 配置采集器
     *
     * @param dcConfig
     * @return
     */
    @Override
    public DcResult stopCollector(DcConfig dcConfig) {
        String httpPort = dcConfig.getConf().getString("httpPort");
        String kill = "ps -ef | grep " + httpPort + " | grep -v grep | awk  '{print $2}'|xargs kill -9";
        DcResult dcResult = SellOperateUtil.execCommand(kill);
        return dcResult;
    }

}
