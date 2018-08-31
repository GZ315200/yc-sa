package com.unistack.tamboo.sa.dc.flume.source;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.commons.utils.TambooConfig;
import com.unistack.tamboo.sa.dc.flume.common.*;
import com.unistack.tamboo.sa.dc.flume.invoking.DcInvoking;
import com.unistack.tamboo.sa.dc.flume.invoking.FlumeInvoking;
import com.unistack.tamboo.sa.dc.flume.model.TailDir;
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
import java.util.*;

import static com.unistack.tamboo.sa.dc.flume.util.CheckConfigUtil.PATH;

/**
 * @program: tamboo-sa
 * @description: taildir source opration
 * @author: Asasin
 * @create: 2018-05-16 11:00
 **/
public class TailDirSource extends DcInvoking implements FlumeInvoking {
    private  Logger logger = LoggerFactory.getLogger(TailDirSource.class);
    private TailDir tailDir = new TailDir();
    private String sourceName = "taildirSource";
    private String channelName = "taildirChannel";
    private String taildirConfName = "taildir.properties";
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
        if ((dc.getDcType() == null) && dc.getDcType() != (DcType.TAILDIR)
                && StringUtils.isBlank(type) && !type.equals("TAILDIR")) {
            return new DcResult(false, "dcType must be specified right");
        }
        tailDir.setDcType(dc.getDcType());

        //filegroups
        String filegroups = dc.getConf().getString("filegroups");
        if ((StringUtils.isBlank(filegroups))) {
            return new DcResult(false, "filegroups must be specified");
        }
        tailDir.setFilegroups(filegroups);

        //filegroupsPath
        String filegroupsPath = dc.getConf().getString("filegroupsPath");
        if ((StringUtils.isBlank(filegroupsPath))) {
            return new DcResult(false, "filegroupsPath must be specified");
        }
        //组装filegroups和filegroupsPath
        Map<String,String> map = new HashMap<String, String>();
        String[] strings1 = null;
        String[] strings2 = null;
        if (filegroups.contains(" ")){
            strings1= filegroups.split(" ");
        }else {
            strings1 = new String[1];
            strings1[0] = filegroups;
        }
        if (filegroupsPath.contains(",")){
            strings2= filegroupsPath.split(",");
        }else {
            strings2 = new String[1];
            strings2[0]=filegroupsPath;
        }
        for (int i = 0;i<strings1.length;i++){

            String key = "filegroups."+strings1[i];
            map.put(key,strings2[i]);
        }
        tailDir.setFilegroupsPath(map);

        String batchSize = dc.getConf().getString("batchSize");
        if ((StringUtils.isBlank(batchSize))) {
            return new DcResult(false, "soapAction must be specified");
        }
        tailDir.setBatchSize(batchSize);

        String batchDurationMillis = dc.getConf().getString("batchDurationMillis");
        if ((StringUtils.isBlank(batchDurationMillis))) {
            return new DcResult(false, "batchDurationMillis must be specified");
        }
        tailDir.setBatchDurationMillis(batchDurationMillis);
        String fileHeader = dc.getConf().getString("fileHeader");
        if ((StringUtils.isBlank(fileHeader))) {
            return new DcResult(false, "fileHeader must be specified");
        }
        tailDir.setFileHeader(fileHeader);
        String inputCharset = dc.getConf().getString("inputCharset");
        if ((StringUtils.isBlank(inputCharset))) {
            return new DcResult(false, "inputCharset must be specified");
        }
        tailDir.setInputCharset(inputCharset);

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
            tailDir.setPath(PATH);
        }
        tailDir.setPath(path);
        String httpPort = dc.getConf().getString("httpPort");
        if ((StringUtils.isBlank(httpPort))) {
            return new DcResult(false, "httpPort");
        }
        tailDir.setHttpPort(httpPort);

        return dcResult;
    }

    @Override
    public DcResult createConf(DcConfig dc) {
        DcResult dcResult = checkConfig(dc);
        //other
        tailDir.setTopic(dc.getConf().getString("topic"));
        tailDir.setUsername(dc.getConf().getString("username"));
        tailDir.setPassword(dc.getConf().getString("password"));
        tailDir.setServers(dc.getConf().getString("servers"));
        tailDir.setPositionFile(PATH+"flume/tmp/taildir_position.json");
        //写入properties
        //source
        PropertiesUtil propertiesUtil = new PropertiesUtil();
        Properties prop = propertiesUtil.getBaseConf();
        prop.setProperty("agent.sources",sourceName);
        prop.setProperty("agent.sources."+sourceName+".type", "taildir");
        String positionFile = tailDir.getPositionFile();
        prop.setProperty("agent.sources."+sourceName+".positionFile", positionFile);
        prop.setProperty("agent.sources."+sourceName+".filegroups", tailDir.getFilegroups());
        Map<String,String> map = tailDir.getFilegroupsPath();
        Iterator<String> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            prop.setProperty("agent.sources."+sourceName+"."+key,map.get(key));
        }
        prop.setProperty("agent.sources."+sourceName+".batchSize", tailDir.getBatchSize());
        prop.setProperty("agent.sources."+sourceName+".batchDurationMillis", tailDir.getBatchDurationMillis());
        prop.setProperty("agent.sources."+sourceName+".fileHeader", tailDir.getFileHeader());
        prop.setProperty("agent.sources."+sourceName+".inputCharset", tailDir.getInputCharset());
        //channel
        prop.setProperty("agent.channels", channelName);
        prop.setProperty("agent.channels." + channelName + ".type", "memory");
        prop.setProperty("agent.channels." + channelName + ".capacity", "1000000");
        prop.setProperty("agent.channels." + channelName + ".transactionCapacity", "1000000");
        //sink
        prop.setProperty("agent.sinks.kafkaSink.kafka.topic", tailDir.getTopic());
        prop.setProperty("agent.sinks.kafkaSink.kafka.topic.acl.username", tailDir.getUsername());
        prop.setProperty("agent.sinks.kafkaSink.kafka.topic.acl.password", tailDir.getPassword());
        prop.setProperty("agent.sinks.kafkaSink.kafka.bootstrap.servers", tailDir.getServers().replace("\\", ""));
        //绑定
        prop.setProperty("agent.sources." + sourceName + ".channels", channelName);
        prop.setProperty("agent.sinks.kafkaSink.channel", channelName);
        //写入taildir.prperties新配置文件
        writeProperties(taildirConfName, prop);
        String classPath = ExecSource.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        System.out.println(classPath);
        if (classPath.contains(".jar")) {
//            Properties p = new Properties();
//            try {
//                InputStream inputStream = new FileInputStream("config" + File.separator + "application.properties");
//                if (null == inputStream) {
//                    new DcResult(false, "application.properties 配置文件加载失败");
//                }
//                p.load(inputStream);
//                String execConfigPath = p.getProperty("dataSource.config.path") + taildirConfName;
//                System.out.println(execConfigPath);
//                flumePath = p.getProperty(("flume.path"));
//                System.out.println(flumePath);
//                setConf_path(execConfigPath);
//                inputStream.close();
//            } catch (Exception e) {
//                new DcResult(false, "application.properties 配置文件加载异常!");
//            }
            String execConfigPath = TambooConfig.COLLECT_FLUME_PATH+taildirConfName;
            System.out.println(execConfigPath);
            setConf_path(execConfigPath);
            flumePath = TambooConfig.DATASOURCE_CONF_PATH;
            System.out.println(flumePath);
        } else {
            String execConfigPath = classPath + "../../../tmp/" + taildirConfName;
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

        String path = tailDir.getPath();
        String configPath = getConf_path();

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
        String execstart2 = "echo \"" + path + "flume/bin/flume-ng agent -c " + path + "flume/conf/ -f " + path + "flume/conf/"+taildirConfName+" -n " + "agent -Dflume.monitoring.type=http -Dflume.monitoring.port=" + httpPort + " -Dflume.root.logger=INFO,console > " + path + "flume/logs/flume.log 2>&1 &\" | ssh -T " + user + "@" + ip;
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
        TailDirSource tailDirSource = new TailDirSource();
        //加载完整配置到输入流
        InputStream is = WebService.class.getResourceAsStream("/tamboo-collector/src/main/resources/taildir1.properties");
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
        dcConfig.setDcType(DcType.TAILDIR);
        //设置监控httpPort
        tailDirSource.tailDir.setHttpPort(dcConfig.getConf().getString("httpPort"));

        tailDirSource.createConf(dcConfig);
//        tailDirSource.startCollector(dcConfig);
//        tailDirSource.createDataCollector(dcConfig);
    }
}
    