package com.unistack.tamboo.sa.dc.flume.source;

import com.unistack.tamboo.commons.utils.TambooConfig;
import com.unistack.tamboo.sa.dc.flume.common.DcConfig;
import com.unistack.tamboo.sa.dc.flume.common.DcResult;
import com.unistack.tamboo.sa.dc.flume.common.DcType;
import com.unistack.tamboo.sa.dc.flume.invoking.DcInvoking;
import com.unistack.tamboo.sa.dc.flume.invoking.FlumeInvoking;
import com.unistack.tamboo.sa.dc.flume.model.*;
import com.unistack.tamboo.sa.dc.flume.util.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Properties;
import static com.unistack.tamboo.sa.dc.flume.util.CheckConfigUtil.PATH;

/**
 * @program: tamboo-sa
 * @description: exec source opration
 * @author: Asasin
 * @create: 2018-05-16 11:02
 **/
public class ExecSource extends DcInvoking implements FlumeInvoking {
    private  Logger logger = LoggerFactory.getLogger(ExecSource.class);
    public Exec exec = new Exec();
    private String sourceName = "execSource";
    private String channelName = "execChannel";
    private String execConfName = "exec.properties";
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
                && StringUtils.isBlank(type) && !type.equals("EXEC")) {
            return new DcResult(false, "dcType must be specified right");
        }
        exec.setDcType(dc.getDcType());
        String command = dc.getConf().getString("command");
        if ((StringUtils.isBlank(command))) {
            return new DcResult(false, "command must be specified");
        }
        exec.setCommand(command);
        String inputCharset = dc.getConf().getString("inputCharset");
        if ((StringUtils.isBlank(inputCharset))) {
            exec.setInputCharset("utf-8");
        }
        exec.setInputCharset(inputCharset);

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
            exec.setPath(PATH);
        }
        exec.setPath(path);
        String httpPort = dc.getConf().getString("httpPort");
        if ((StringUtils.isBlank(httpPort))) {
            return new DcResult(false, "httpPort");
        }
        exec.setHttpPort(httpPort);
        return dcResult;
    }

    @Override
    public DcResult createConf(DcConfig dc) {
        DcResult dcResult = checkConfig(dc);
        //other
        exec.setTopic(dc.getConf().getString("topic"));
        exec.setUsername(dc.getConf().getString("username"));
        exec.setPassword(dc.getConf().getString("password"));
        exec.setServers(dc.getConf().getString("servers"));
        //写入properties
        //source
        PropertiesUtil propertiesUtil = new PropertiesUtil();
        Properties properties = propertiesUtil.getBaseConf();
        properties.setProperty("agent.sources", sourceName);
        properties.setProperty("agent.sources." + sourceName + ".type", "exec");
        properties.setProperty("agent.sources." + sourceName + ".command", exec.getCommand());
        properties.setProperty("agent.sources." + sourceName + ".inputCharset", exec.getInputCharset());
        //channel
        properties.setProperty("agent.channels", channelName);
        properties.setProperty("agent.channels." + channelName + ".type", "memory");
        properties.setProperty("agent.channels." + channelName + ".capacity", "1000000");
        properties.setProperty("agent.channels." + channelName + ".transactionCapacity", "1000000");
        //sink
        properties.setProperty("agent.sinks.kafkaSink.kafka.topic", exec.getTopic());
        properties.setProperty("agent.sinks.kafkaSink.kafka.topic.acl.username", exec.getUsername());
        properties.setProperty("agent.sinks.kafkaSink.kafka.topic.acl.password", exec.getPassword());
        properties.setProperty("agent.sinks.kafkaSink.kafka.bootstrap.servers", exec.getServers().replace("\\", ""));
        //绑定
        properties.setProperty("agent.sources." + sourceName + ".channels", channelName);
        properties.setProperty("agent.sinks.kafkaSink.channel", channelName);
        //写入exec.prperties新配置文件
        writeProperties(execConfName, properties);
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
//                String execConfigPath = p.getProperty("dataSource.config.path") + execConfName;
//                System.out.println(execConfigPath);
//                flumePath = p.getProperty(("flume.path"));
//                System.out.println(flumePath);
//                setConf_path(execConfigPath);
//                inputStream.close();
//            } catch (Exception e) {
//                new DcResult(false, "collectPath.properties 配置文件加载异常!");
//            }
            String execConfigPath = TambooConfig.COLLECT_FLUME_PATH+execConfName;
            System.out.println(execConfigPath);
            setConf_path(execConfigPath);
            flumePath = TambooConfig.DATASOURCE_CONF_PATH;
            System.out.println(flumePath);
        } else {
            String execConfigPath = classPath + "../../../tmp/" + execConfName;
            setConf_path(execConfigPath);
            flumePath = classPath + "../../flume.tar.gz";
        }
        //封装properties.store()写入exec.prperties新配置文件,但是"："和"="转义无法处理
//        prop.store(execConfigPath, "UTF-8");
//        writeFile(execConfigPath, prop);
        return dcResult;
    }


    @Override
    public DcResult startCollector(DcConfig dcConfig) {
        //get
        String ip = dcConfig.getConf().getString("ip");
        String user = dcConfig.getConf().getString("user");
        String httpPort = dcConfig.getConf().getString("httpPort");
        String path = exec.getPath();
        String configPath = getConf_path();
        //启动命令
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
        String execstart2 = "echo \"" + path + "flume/bin/flume-ng agent -c " + path + "flume/conf/ -f " + path + "flume/conf/"+execConfName+" -n " + "agent -Dflume.monitoring.type=http -Dflume.monitoring.port=" + httpPort + " -Dflume.root.logger=INFO,console > " + path + "flume/logs/flume.log 2>&1 &\" | ssh -T " + user + "@" + ip;
        SellOperateUtil.execCommand(execstart2);

//        String pid = "echo \"ps ax | grep -i "+httpPort+" | grep java | grep -v grep | awk '{print $1}' " + user + "@" + ip;
//        String pid = "echo \"netstat -anp|grep " + httpPort + "|awk '{printf $7}'|cut -d/ -f1 \" | ssh -T " + user + "@" + ip;
//        String pid = "" + user + "@" + ip + " \"" + "netstat -anp|grep " + httpPort + "|awk '{printf $7}'|cut -d/ -f1\"";
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
//        String pid = "ssh "+user+"@"+ip+" \"pid=$(ps -ef | grep "+httpPort+" | grep -v grep | awk  '{print $2}'| awk '{print $2}')\"";
//        SellOperateUtil.execCommand(pid);
//        String stop = "ssh "+user+"@"+ip+" \"kill -9 ${pid}\"";
//        SellOperateUtil.execCommand(stop);
        //kill agent by httpPort
        String kill = "ssh " + user + "@" + ip + " \"ps -ef | grep " + httpPort + " | grep -v grep | awk  '{print $2}'|xargs kill -9\"";
        DcResult dcResult = SellOperateUtil.execCommand(kill);
        return dcResult;
    }

    //功能测试
//    public static void main(String[] args) {
//        ExecSource execSource = new ExecSource();
//        //加载完整配置到输入流
//        InputStream is = WebService.class.getResourceAsStream("/tamboo-collector/src/main/resources/exec1.properties");
//        PropertiesUtil prop = new PropertiesUtil();
//        try {
//            prop.load(is);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        //获取properties中所有数据并转成JSONObject
//        JSONObject jsonObject = DataConversionUtil.MapToJSONObject(prop.getValues(prop));
////        System.out.println(jsonObject);
//        //创建DcConfig配置对象
//        DcConfig dcConfig = new DcConfig();
//        //设置JSONObject conf值
//        dcConfig.setConf(jsonObject);
//        //设置DcType
//        dcConfig.setDcType(DcType.EXEC);
//        //设置监控httpPort
//        execSource.exec.setHttpPort(dcConfig.getConf().getString("httpPort"));
//        execSource.createConf(dcConfig);
////        execSource.createDataCollector(dcConfig);
////        execSource.stopCollector(dcConfig);
//    }

    //远程flume.tar.gz包判断是否存在测试
//    public static void main(String[] args) {
//        String path = "/home/zc/";
//        String user = "root";
//        String ip = "192.168.1.201";
//        String opinionFile = "echo \"[ -f "+path+"flume.tar.gz ] && echo 'Found' || echo 'Not found'\" | ssh -T "+user+"@"+ip;
//        DcResult dcResult = SellOperateUtil.execCommand(opinionFile);
//        if(!("Not found".equals(dcResult.getMsg()))){
////            return new DcResult(false,"服务器采集器已存在。");
//            System.out.println("tar包已存在。");
//        }
//    }

//    public static void main(String[] args) {
////        String classPath = ExecSource.class.getProtectionDomain().getCodeSource().getLocation().getPath();
////        String classPath = "/Users/aa/Unistack/tamboo-sa/tamboo-collector/tamboo-collector-flume/target/classes/";
////        String classPath = "file:/Users/aa/Unistack/tamboo-sa/tamboo-management/target/tamboo-management/tamboo-management.jar!/BOOT-INF/lib/tamboo-collector-flume-1.0.0.jar!/";
//        String classPath = "file:/opt/apps/tamboo-management/current/tamboo-management.jar!/BOOT-INF/lib/tamboo-collector-flume-1.0.0.jar!/";
//        String flumePath = null;
//        if (classPath.contains("classes")) {
//            flumePath = classPath + "../../flume.tar.gz";
//            System.out.println(("flumePaht：" + flumePath));
//        } else if (classPath.contains("current")) {
//            int indexOf2 = classPath.indexOf("tamboo-management.jar!");
//            System.out.println(indexOf2);
//            classPath = classPath.substring(0,indexOf2);
//            flumePath = classPath + "flume.tar.gz";
//            System.out.println(("flumePaht：" + flumePath));
//        }else {
//            int indexOf1 = classPath.indexOf("target");
//            System.out.println(indexOf1);
//            classPath = classPath.substring(0,indexOf1);
//            flumePath = classPath + "flume.tar.gz";
//            System.out.println(("flumePaht：" + flumePath));
//        }
//    }

//    public static void main(String[] args) {
//        String fwqPath = "file:/opt/apps/tamboo-management/20180612-164815/tamboo-management.jar!/BOOT-INF/lib/tamboo-collector-flume-1.0.0.jar!/";
//        String classPath1 = "file:/Users/aa/Unistack/tamboo-sa/tamboo-management/target/tamboo-management/tamboo-management.jar!/BOOT-INF/lib/tamboo-collector-flume-1.0.0.jar!/";
//        System.out.println(classPath1.substring(5,classPath1.length()));
//        int indexOf1 = classPath1.indexOf("tamboo-management.jar!");
//        classPath1 = classPath1.substring(0,indexOf1);
//        String execConfigPath = classPath1 + "config/exec.properties";
//        String classPath2 = ExecSource.class.getClassLoader().getResource("").getPath();
//        String classPath3 = ExecSource.class.getProtectionDomain().getCodeSource().getLocation().getPath();
//        System.out.println(classPath2);
//        System.out.println(classPath3);
//        System.out.println(execConfigPath);
//    }
}
    