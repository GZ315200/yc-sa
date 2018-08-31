package com.unistack.tamboo.sa.dc.flume.source;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.commons.utils.TambooConfig;
import com.unistack.tamboo.sa.dc.flume.common.DcConfig;
import com.unistack.tamboo.sa.dc.flume.common.DcResult;
import com.unistack.tamboo.sa.dc.flume.common.DcType;
import com.unistack.tamboo.sa.dc.flume.invoking.DcInvoking;
import com.unistack.tamboo.sa.dc.flume.invoking.FlumeInvoking;
import com.unistack.tamboo.sa.dc.flume.model.Syslog;
import com.unistack.tamboo.sa.dc.flume.model.WebService;
import com.unistack.tamboo.sa.dc.flume.util.CheckConfigUtil;
import com.unistack.tamboo.sa.dc.flume.util.DataConversionUtil;
import com.unistack.tamboo.sa.dc.flume.util.PropertiesUtil;
import com.unistack.tamboo.sa.dc.flume.util.SellOperateUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import static com.unistack.tamboo.sa.dc.flume.util.CheckConfigUtil.PATH;

/**
 * @program: tamboo-sa
 * @description: syslog source opration
 * @author: Asasin
 * @create: 2018-05-16 10:58
 **/
public class SysLogSource extends DcInvoking implements FlumeInvoking {
    private  Logger logger = LoggerFactory.getLogger(SysLogSource.class);
    private Syslog syslog = new Syslog();
    private String sourceName = "syslogSource";
    private String channelName = "syslogChannel";
    private String syslogConfName = "syslog.properties";
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
        if ((dc.getDcType() == null) && dc.getDcType() != (DcType.EXEC)
                && StringUtils.isBlank(type) && !type.equals("SYSLOG")) {
            return new DcResult(false, "dcType must be specified right");
        }
        syslog.setDcType(dc.getDcType());
        String inputCharset = dc.getConf().getString("inputCharset");
        if ((StringUtils.isBlank(inputCharset))) {
            return new DcResult(false, "inputCharset must be specified");
        }
        syslog.setInputCharset(inputCharset);
        String bind = dc.getConf().getString("bind");
        System.out.println(bind);
        if ((StringUtils.isBlank(bind))) {
            return new DcResult(false, "bind must be specified");
        }
        syslog.setBind(bind);
        String port = dc.getConf().getString("port");
        System.out.println(bind);
        if ((StringUtils.isBlank(port))) {
            return new DcResult(false, "port must be specified");
        }
        syslog.setPort(port);

        //校验远程主机配置
        String ip = dc.getConf().getString("ip");
        if ((StringUtils.isBlank(ip))) {
            return new DcResult(false, "ip must be specified");
        }
        String user = dc.getConf().getString("user");
        if ((StringUtils.isBlank(user))) {
            return new DcResult(false, "user must be specified");
        }
        String path = dc.getConf().getString("path");
        if ((StringUtils.isBlank(path))) {
            syslog.setPath(PATH);
        }
        syslog.setPath(path);
        String httpPort = dc.getConf().getString("httpPort");
        if ((StringUtils.isBlank(httpPort))) {
            return new DcResult(false, "httpPort");
        }
        syslog.setHttpPort(httpPort);
        return dcResult;
    }

    @Override
    public DcResult createConf(DcConfig dc) {
        DcResult dcResult = checkConfig(dc);
        String path = WebService.class.getResource("/").getFile();
        //写入指定文件地址
        //other
        syslog.setTopic(dc.getConf().getString("topic"));
        syslog.setUsername(dc.getConf().getString("username"));
        syslog.setPassword(dc.getConf().getString("password"));
        syslog.setServers(dc.getConf().getString("servers"));
        //写入properties
        PropertiesUtil propertiesUtil = new PropertiesUtil();
        Properties prop = propertiesUtil.getBaseConf();
        //source
        prop.setProperty("agent.sources",sourceName);
        prop.setProperty("agent.sources."+sourceName+".type", "syslogtcp");
        prop.setProperty("agent.sources."+sourceName+".inputCharset", syslog.getInputCharset());
        prop.setProperty("agent.sources."+sourceName+".bind", syslog.getBind());
        prop.setProperty("agent.sources."+sourceName+".port", syslog.getPort());
        //channel
        prop.setProperty("agent.channels", channelName);
        prop.setProperty("agent.channels." + channelName + ".type", "memory");
        prop.setProperty("agent.channels." + channelName + ".capacity", "1000000");
        prop.setProperty("agent.channels." + channelName + ".transactionCapacity", "1000000");
        ///sink
        prop.setProperty("agent.sinks.kafkaSink.kafka.topic", syslog.getTopic());
        prop.setProperty("agent.sinks.kafkaSink.kafka.topic.acl.username", syslog.getUsername());
        prop.setProperty("agent.sinks.kafkaSink.kafka.topic.acl.password", syslog.getPassword());
        prop.setProperty("agent.sinks.kafkaSink.kafka.bootstrap.servers", syslog.getServers());
        //绑定
        prop.setProperty("agent.sources." + sourceName + ".channels", channelName);
        prop.setProperty("agent.sinks.kafkaSink.channel", channelName);
        //写入natcat.prperties新配置文件
        writeProperties(syslogConfName, prop);
        String classPath = NetCatSource.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (classPath.contains(".jar")) {
//            Properties p = new Properties();
//            try {
//                InputStream inputStream = new FileInputStream("config" + File.separator + "application.properties");
//                if (null == inputStream) {
//                    new DcResult(false, "application.properties 配置文件加载失败");
//                }
//                p.load(inputStream);
//                String execConfigPath = p.getProperty("dataSource.config.path") + syslogConfName;
//                System.out.println(execConfigPath);
//                flumePath = p.getProperty(("flume.path"));
//                System.out.println(flumePath);
//                setConf_path(execConfigPath);
//                inputStream.close();
//            } catch (Exception e) {
//                new DcResult(false, "application.properties 配置文件加载异常!");
//            }
            String execConfigPath = TambooConfig.COLLECT_FLUME_PATH+syslogConfName;
            System.out.println(execConfigPath);
            setConf_path(execConfigPath);
            flumePath = TambooConfig.DATASOURCE_CONF_PATH;
            System.out.println(flumePath);
        } else {
            String execConfigPath = classPath + "../../../tmp/" + syslogConfName;
            setConf_path(execConfigPath);
            flumePath = classPath + "../../flume.tar.gz";
        }
        return dcResult;
    }

    @Override
    public DcResult startCollector(DcConfig dcConfig) {
        String ip = dcConfig.getConf().getString("ip");
        String user = dcConfig.getConf().getString("user");
        String httpPort = dcConfig.getConf().getString("httpPort");
        String path = syslog.getPath();
        String configPath = getConf_path();
        //启动命令
        String opinionFile = "echo \"[ -f " + path + "flume.tar.gz ] && echo 'Found' || echo 'Not found'\" | ssh -T " + user + "@" + ip;
        DcResult dcResult = SellOperateUtil.execCommand(opinionFile);
        if (("[Not found]".equals(dcResult.getMsg()))) {
            //send flume  to The remote
            String sendFlume = "scp -r " + flumePath + " " + user + "@" + ip + ":" + path + "\n";
            SellOperateUtil.execCommand(sendFlume);
            //Unpack flume  to The remote
            String unpackFlume = "ssh -o StrictHostKeyChecking=no " + user + "@" + ip + " \"" + "tar -zxvf " + path + "flume.tar.gz -C " + path + " > /dev/null 2>&1 &\"";
            SellOperateUtil.execCommand(unpackFlume);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                return new DcResult(false, "unpackFlume failed：" + e);
            }
        }
        String sendConf = "scp -r " + configPath + " " + user + "@" + ip + ":" + path + "flume/conf\n";
        SellOperateUtil.execCommand(sendConf);
        String execstart2 = "echo \"" + path + "flume/bin/flume-ng agent -c " + path + "flume/conf/ -f " + path + "flume/conf/"+syslogConfName+" -n " + "agent -Dflume.monitoring.type=http -Dflume.monitoring.port=" + httpPort + " -Dflume.root.logger=INFO,console > " + path + "flume/logs/flume.log 2>&1 &\" | ssh -T " + user + "@" + ip;
        SellOperateUtil.execCommand(execstart2);
        //Remote check pid
        String pid  = "echo \"ps -ef | grep "+httpPort+"| grep -v grep | awk  '{print $2}'\"  | ssh -T " + user + "@" + ip;
        DcResult dcResult2 = SellOperateUtil.execCommand(pid);
        if (StringUtils.isBlank(dcResult2.getMsg())||(!dcResult2.isCode())) {
            return new DcResult(false, "start agent failed ,cause can not get pid");
        }
        return new DcResult(true, "start agent successed");
    }

    @Override
    public DcResult stopCollector(DcConfig dcConfig) {
        String ip = dcConfig.getConf().getString("ip");
        String user = dcConfig.getConf().getString("user");
        String httpPort = dcConfig.getConf().getString("httpPort");
        String kill = "ssh " + user + "@" + ip + " \"ps -ef | grep " + httpPort + " | grep -v grep | awk  '{print $2}'|xargs kill -9\"";
        DcResult dcResult = SellOperateUtil.execCommand(kill);
        return dcResult;
    }

    public static void main(String[] args) {
        SysLogSource sysLogSource = new SysLogSource();
        //加载完整配置到输入流
        InputStream is = WebService.class.getResourceAsStream("/tamboo-collector/src/main/resources/syslog1.properties");
        PropertiesUtil prop = new PropertiesUtil();
        try {
            prop.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //获取properties中所有数据并转成JSONObject
        JSONObject jsonObject = DataConversionUtil.MapToJSONObject(prop.getValues(prop));
//        System.out.println(jsonObject);
        //创建DcConfig配置对象
        DcConfig dcConfig = new DcConfig();
        //设置JSONObject conf值
        dcConfig.setConf(jsonObject);
        //设置DcType
        dcConfig.setDcType(DcType.SYSLOG);
        //设置监控httpPort
        sysLogSource.syslog.setHttpPort(dcConfig.getConf().getString("httpPort"));
        sysLogSource.createConf(dcConfig);
//        sysLogSource.startCollector(dcConfig);
//        sysLogSource.createDataCollector(dcConfig);
    }
}
    