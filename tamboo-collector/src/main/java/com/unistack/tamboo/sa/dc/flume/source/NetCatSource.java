package com.unistack.tamboo.sa.dc.flume.source;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.commons.utils.TambooConfig;
import com.unistack.tamboo.sa.dc.flume.common.DcConfig;
import com.unistack.tamboo.sa.dc.flume.common.DcResult;
import com.unistack.tamboo.sa.dc.flume.common.DcType;
import com.unistack.tamboo.sa.dc.flume.invoking.DcInvoking;
import com.unistack.tamboo.sa.dc.flume.invoking.FlumeInvoking;
import com.unistack.tamboo.sa.dc.flume.model.NetCat;
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
 * @description: netcat source opration
 * @author: Asasin
 * @create: 2018-05-16 11:03
 **/
public class NetCatSource extends DcInvoking implements FlumeInvoking {
    private  Logger logger = LoggerFactory.getLogger(NetCatSource.class);
    private NetCat netCat = new NetCat();
    private String sourceName = "netcatSource";
    private String channelName = "netcatChannel";
    private String netCatConfName = "netcat.properties";
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
        if ((dc.getDcType() == null) && dc.getDcType() != (DcType.NETCAT)
                && StringUtils.isBlank(type) && !type.equals("NETCAT")) {
            return new DcResult(false, "dcType must be specified right");
        }
        netCat.setDcType(dc.getDcType());

        String inputCharset = dc.getConf().getString("inputCharset");
        if ((StringUtils.isBlank(inputCharset))) {
            netCat.setInputCharset("utf-8");
        }
        netCat.setInputCharset(inputCharset);
        String bind = dc.getConf().getString("bind");
        System.out.println(bind);
        if ((StringUtils.isBlank(bind))) {
            return new DcResult(false, "bind must be specified");
        }
        netCat.setBind(bind);
        String port = dc.getConf().getString("port");
        System.out.println(bind);
        if ((StringUtils.isBlank(port))) {
            return new DcResult(false, "port must be specified");
        }
        netCat.setPort(port);

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
            netCat.setPath(PATH);
        }
        netCat.setPath(path);
        String httpPort = dc.getConf().getString("httpPort");
        if ((StringUtils.isBlank(httpPort))) {
            return new DcResult(false, "httpPort");
        }
        netCat.setHttpPort(httpPort);
        return dcResult;
    }

    @Override
    public DcResult createConf(DcConfig dc) {
        DcResult dcResult = checkConfig(dc);
        //other
        netCat.setTopic(dc.getConf().getString("topic"));
        netCat.setUsername(dc.getConf().getString("username"));
        netCat.setPassword(dc.getConf().getString("password"));
        netCat.setServers(dc.getConf().getString("servers"));
        //写入properties
        //source
        PropertiesUtil propertiesUtil = new PropertiesUtil();
        Properties prop = propertiesUtil.getBaseConf();
        //写入properties
        //source
        prop.setProperty("agent.sources",sourceName);
        prop.setProperty("agent.sources."+sourceName+".type", "netcat");
        prop.setProperty("agent.sources."+sourceName+".inputCharset", netCat.getInputCharset());
        prop.setProperty("agent.sources."+sourceName+".bind", netCat.getBind());
        prop.setProperty("agent.sources."+sourceName+".port", netCat.getPort());
        //channel
        prop.setProperty("agent.channels", channelName);
        prop.setProperty("agent.channels." + channelName + ".type", "memory");
        prop.setProperty("agent.channels." + channelName + ".capacity", "1000000");
        prop.setProperty("agent.channels." + channelName + ".transactionCapacity", "1000000");
        //sink
        prop.setProperty("agent.sinks.kafkaSink.kafka.topic", netCat.getTopic());
        prop.setProperty("agent.sinks.kafkaSink.kafka.topic.acl.username", netCat.getUsername());
        prop.setProperty("agent.sinks.kafkaSink.kafka.topic.acl.password", netCat.getPassword());
        prop.setProperty("agent.sinks.kafkaSink.kafka.bootstrap.servers", netCat.getServers());
        //绑定
        prop.setProperty("agent.sources." + sourceName + ".channels", channelName);
        prop.setProperty("agent.sinks.kafkaSink.channel", channelName);
        //写入natcat.prperties新配置文件
        writeProperties(netCatConfName, prop);

        String classPath = NetCatSource.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (classPath.contains(".jar")) {
//            Properties p = new Properties();
//            try {
//                InputStream inputStream = new FileInputStream("config" + File.separator + "application.properties");
//                if (null == inputStream) {
//                    new DcResult(false, "application.properties 配置文件加载失败");
//                }
//                p.load(inputStream);
//                String execConfigPath = p.getProperty("dataSource.config.path") + netCatConfName;
//                System.out.println(execConfigPath);
//                flumePath = p.getProperty(("flume.path"));
//                System.out.println(flumePath);
//                setConf_path(execConfigPath);
//                inputStream.close();
//            } catch (Exception e) {
//                new DcResult(false, "application.properties 配置文件加载异常!");
//            }

            String execConfigPath = TambooConfig.COLLECT_FLUME_PATH+netCatConfName;
            System.out.println(execConfigPath);
            setConf_path(execConfigPath);
            flumePath = TambooConfig.DATASOURCE_CONF_PATH;
            System.out.println(flumePath);
        } else {
            String execConfigPath = classPath + "../../../tmp/" + netCatConfName;
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
        String path = netCat.getPath();
        String configPath = getConf_path();
        //remote check flume.tar.gz
        String opinionFile = "echo \"[ -f " + path + "flume.tar.gz ] && echo 'Found' || echo 'Not found'\" | ssh -T " + user + "@" + ip;
        DcResult dcResult = SellOperateUtil.execCommand(opinionFile);
        if (("[Not found]".equals(dcResult.getMsg()))) {
            //send flume  to The remote
            String sendFlume = "scp -r " + flumePath + " " + user + "@" + ip + ":" + path + "\n";
            DcResult dcResult1 = SellOperateUtil.execCommand(sendFlume);
            if (dcResult1.isCode()){
                //Unpack flume  to The remote
                String unpackFlume = "ssh -o StrictHostKeyChecking=no " + user + "@" + ip + " \"" + "tar -zxvf " + path + "flume.tar.gz -C " + path + " > /dev/null 2>&1 &\"";
                SellOperateUtil.execCommand(unpackFlume);
            }else {
                return dcResult1;
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                return new DcResult(false, "unpackFlume failed：" + e);
            }
        }
        //send source config to The remote
        String sendConf = "scp -r " + configPath + " " + user + "@" + ip + ":" + path + "flume/conf\n";
        SellOperateUtil.execCommand(sendConf);
        //Remote start flume
//        String execstart1 = "ssh -o StrictHostKeyChecking=no " + user + "@" + ip + " \"source /etc/profile && " + path + "flume/bin/flume-ng agent --conf conf --conf-file " + path + "flume/conf/exec.properties" + " --name agent -Dflume.monitoring.type=http -Dflume.monitoring.port=" + httpPort + " > "+path+"flume/logs/flume.log 2>&1 &\"";
        String execstart2 = "echo \"" + path + "flume/bin/flume-ng agent -c " + path + "flume/conf/ -f " + path + "flume/conf/"+netCatConfName+" -n " + "agent -Dflume.monitoring.type=http -Dflume.monitoring.port=" + httpPort + " -Dflume.root.logger=INFO,console > " + path + "flume/logs/flume.log 2>&1 &\" | ssh -T " + user + "@" + ip;
        SellOperateUtil.execCommand(execstart2);
        //Remote check pid
        String pid  = "echo \"ps -ef | grep  "+httpPort+"| grep -v grep | awk  '{print $2}'\"  | ssh -T " + user + "@" + ip;
        DcResult dcResult2 = SellOperateUtil.execCommand(pid);
        if (StringUtils.isBlank(dcResult2.getMsg())||(!dcResult2.isCode())) {
            return new DcResult(false, "start agent failed ,cause can not get pid");
        }
        return new DcResult(true, "start agent successed");
//        String httpString = "http://" + ip + ":" + httpPort + "/metrics";
//        try {
//            logger.debug("request data " + 1 + " time(s)");
//            OkHttpResponse response = null;
//            for (int i = 0; i < 3; i++) {
//                response = OkHttpUtils.get(httpString);
//            }
//            if (response.getCode() == 200) {
//                return new DcResult(true, "execute successfully.");
//            } else {
//                return new DcResult(false, "execute failed1.");
//            }
//        } catch (Exception e) {
//            logger.debug("retry to request data " + 2 + " time(s)");
//            try {
//                Thread.sleep(5000);
//                OkHttpResponse response = OkHttpUtils.get(httpString);
//                if (response.getCode() == 200) {
//                    return new DcResult(true, "execute successfully.");
//                } else {
//                    return new DcResult(false, "execute failed2.");
//                }
//            } catch (Exception e1) {
//                logger.error("", e1);
//            }
//            return new DcResult(false, "execute failed3.");
//        }
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
        NetCatSource netCatSource = new NetCatSource();
        //加载完整配置到输入流
        InputStream is = WebService.class.getResourceAsStream("/tamboo-collector/src/main/resources/netcat1.properties");
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
        dcConfig.setDcType(DcType.NETCAT);
        //设置监控httpPort
        netCatSource.netCat.setHttpPort(dcConfig.getConf().getString("httpPort"));
        netCatSource.createConf(dcConfig);
//        tailDirSource.startCollector(dcConfig);
//        netCatSource.createDataCollector(dcConfig);
    }
}
    