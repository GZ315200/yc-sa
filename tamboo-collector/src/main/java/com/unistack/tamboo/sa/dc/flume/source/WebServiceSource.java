package com.unistack.tamboo.sa.dc.flume.source;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.commons.utils.TambooConfig;
import com.unistack.tamboo.sa.dc.flume.common.DcConfig;
import com.unistack.tamboo.sa.dc.flume.common.DcResult;
import com.unistack.tamboo.sa.dc.flume.common.DcType;
import com.unistack.tamboo.sa.dc.flume.invoking.DcInvoking;
import com.unistack.tamboo.sa.dc.flume.invoking.FlumeInvoking;
import com.unistack.tamboo.sa.dc.flume.model.WebService;
import com.unistack.tamboo.sa.dc.flume.util.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.URISyntaxException;
import java.util.Properties;
import static com.unistack.tamboo.sa.dc.flume.util.CheckConfigUtil.PATH;

/**
 * @program: tamboo-sa
 * @description: webService source opration
 * @author: Asasin
 * @create: 2018-05-16 10:58
 **/
public class WebServiceSource extends DcInvoking implements FlumeInvoking {
    private  Logger logger = LoggerFactory.getLogger(WebServiceSource.class);
    private WebService webService = new WebService();
    private String sourceName = "webServiceSource";
    private String channelName = "webServiceChannel";
    private String WebServiceConfName = "webService.properties";
    private String flumePath = null;
    @Override
    public DcResult createDataCollector(DcConfig dc) {
        DcResult dcResult = createConf(dc);
        if (dcResult.isCode()) {
            return startCollector(dc);
        }
        return dcResult;
    }

    /**
     * 校验source，sink可变配置和DcType
     * @param dc
     * @return
     */
    @Override
    public DcResult checkConfig(DcConfig dc) {
        DcResult dcResult = CheckConfigUtil.checkSinkConfig(dc);
        //校验source可变配置和DcType
        String type = dc.getConf().getString("type");
        if ((dc.getDcType() == null) && dc.getDcType() != (DcType.WEBSERVICE)
                && StringUtils.isBlank(type) && !type.equals("WEBSERVICE")) {
            return new DcResult(false, "dcType must be specified right");
        }
        webService.setDcType(dc.getDcType());
        String postUrl = dc.getConf().getString("postUrl");
        if ((StringUtils.isBlank(postUrl))) {
            return new DcResult(false, "postUrl must be specified");
        }
        webService.setPostUrl(postUrl);
        String soapXml = dc.getConf().getString("soapXml");
        if ((StringUtils.isBlank(soapXml))) {
            return new DcResult(false, "soapXml must be specified");
        }
        webService.setSoapXml(soapXml);
        String soapAction = dc.getConf().getString("soapAction");
        if ((StringUtils.isBlank(soapAction))) {
            return new DcResult(false, "soapAction must be specified");
        }
        webService.setSoapAction(soapAction);
        Integer intervalMillisInt = dc.getConf().getInteger("intervalMillis");
        if ((intervalMillisInt==null)) {
            return new DcResult(false, "intervalMillisInt must be specified");
        }
        webService.setIntervalMillis(intervalMillisInt);
        String inputCharset = dc.getConf().getString("inputCharset");
        if ((StringUtils.isBlank(inputCharset))) {
            webService.setInputCharset("utf-8");
        }
        webService.setInputCharset(inputCharset);

        //校验主机配置
        String path = dc.getConf().getString("path");
        if ((StringUtils.isBlank(path))) {
            webService.setPath(PATH);
        }
        webService.setPath(path);
        String httpPort = dc.getConf().getString("httpPort");
        if ((StringUtils.isBlank(httpPort))) {
            return new DcResult(false, "httpPort");
        }
        webService.setHttpPort(httpPort);
        return dcResult;
        }


    /**
     * 根据参数生成配置文件(并具有校验功能)，并保存配置文件路径（保存至指定地址）
     *
     * @param dc
     * @return
     */
    @Override
    public DcResult createConf(DcConfig dc) {
        DcResult dcResult = checkConfig(dc);
        //other
        webService.setTopic(dc.getConf().getString("topic"));
        webService.setUsername(dc.getConf().getString("username"));
        webService.setPassword(dc.getConf().getString("password"));
        webService.setServers(dc.getConf().getString("servers"));
        //source
        PropertiesUtil propertiesUtil = new PropertiesUtil();
        Properties properties = propertiesUtil.getBaseConf();
        properties.setProperty("agent.sources", sourceName);
        properties.setProperty("agent.sources." + sourceName + ".type", "org.apache.flume.source.webService.WebServiceSource");
        properties.setProperty("agent.sources." + sourceName + ".postUrl", webService.getPostUrl());
        properties.setProperty("agent.sources." + sourceName + ".soapXml", webService.getSoapXml());
        properties.setProperty("agent.sources." + sourceName + ".soapAction", webService.getSoapAction());
        properties.setProperty("agent.sources." + sourceName + ".intervalMillis", String.valueOf(webService.getIntervalMillis()));
        properties.setProperty("agent.sources." + sourceName + ".inputCharset", webService.getInputCharset());
        //channel
        properties.setProperty("agent.channels", channelName);
        properties.setProperty("agent.channels." + channelName + ".type", "memory");
        properties.setProperty("agent.channels." + channelName + ".capacity", "1000000");
        properties.setProperty("agent.channels." + channelName + ".transactionCapacity", "1000000");
        //sink
        properties.setProperty("agent.sinks.kafkaSink.kafka.topic", webService.getTopic());
        properties.setProperty("agent.sinks.kafkaSink.kafka.topic.acl.username", webService.getUsername());
        properties.setProperty("agent.sinks.kafkaSink.kafka.topic.acl.password", webService.getPassword());
        properties.setProperty("agent.sinks.kafkaSink.kafka.bootstrap.servers", webService.getServers().replace("\\", ""));
        //绑定
        properties.setProperty("agent.sources." + sourceName + ".channels", channelName);
        properties.setProperty("agent.sinks.kafkaSink.channel", channelName);
        //写入exec.prperties新配置文件
        writeProperties(WebServiceConfName,properties);

        String classPath = WebServiceSource.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (classPath.contains(".jar")) {
//            Properties properties1 = new Properties();
//            try {
//                InputStream inputStream = new FileInputStream("config" + File.separator + "application.properties");
//                if (null == inputStream) {
//                    new DcResult(false,"application.properties 配置文件加载失败");
//                }
//                properties1.load(inputStream);
//                String execConfigPath = properties1.getProperty("dataSource.config.path")+WebServiceConfName;
//                System.out.println(execConfigPath);
//                flumePath = properties1.getProperty(("flume.path"));
//                System.out.println(flumePath);
//                setConf_path(execConfigPath);
//                inputStream.close();
//            } catch (Exception e) {
//                new DcResult(false,"application.properties 配置文件加载异常!");
//            }
            String execConfigPath = TambooConfig.COLLECT_FLUME_PATH+WebServiceConfName;
            System.out.println(execConfigPath);
            setConf_path(execConfigPath);
            flumePath = TambooConfig.DATASOURCE_CONF_PATH;
            System.out.println(flumePath);
        }else {
            String execConfigPath = classPath + "../../../tmp/"+WebServiceConfName;
            setConf_path(execConfigPath);
            flumePath = classPath + "../../flume.tar.gz";
        }
        return dcResult;
    }

    /**
     * 启动 配置采集器
     *
     * @param dcConfig
     * @return
     */
    @Override
    public DcResult startCollector(DcConfig dcConfig) {
        //get
        String httpPort = dcConfig.getConf().getString("httpPort");
        String path = webService.getPath();
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
        String execstart2 = path + "flume/bin/flume-ng agent -c " + path + "flume/conf/ -f " + path + "flume/conf/"+WebServiceConfName+" -n " + "agent -Dflume.monitoring.type=http -Dflume.monitoring.port=" + httpPort + " -Dflume.root.logger=INFO,console > " + path + "flume/logs/flume.log 2>&1 &";
        SellOperateUtil.execCommand(execstart2);
        //local check pid
        String pid = "ps -ef | grep " + httpPort + " | grep -v grep | awk  '{print $2}'";
        DcResult dcResult2 = SellOperateUtil.execCommand(pid);
        if (StringUtils.isBlank(dcResult2.getMsg())||(!dcResult2.isCode())) {
            return new DcResult(false, "start agent failed ,cause can not get pid");
        }
        return new DcResult(true, "start agent successed");
    }
//        //存放shell脚本目录地址
//        String shellPath = WebService.class.getResource("../bin").getPath();
//        //配置文件地址
//        String configPath = getConf_path();
//        String httpPort = webService.getHttpPort();
//        String shellName = "agent-start.sh";
//        //赋予脚本权限
//        ProcessBuilder builder = new ProcessBuilder("/bin/chmod", "755", shellPath+"/"+shellName);
//        SellOperateUtil.execudShell(builder,shellPath);
//        ProcessBuilder pb = new ProcessBuilder("./" + shellName,configPath,httpPort);
//        SellOperateUtil.execudShell(pb,shellPath);
//        return null;
//    }

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
        //        String shellPath = WebService.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        //存放shell脚本目录地址
//        String shellPath = WebService.class.getResource("../bin").getPath();
//        String httpPort = webService.getHttpPort();
//        String shellName = "agent-stop.sh";
//        //赋予脚本权限
//        ProcessBuilder builder = new ProcessBuilder("/bin/chmod", "755", shellPath+"/"+shellName);
//        SellOperateUtil.execudShell(builder,shellPath);
//        ProcessBuilder pb = new ProcessBuilder("./" + shellName,httpPort);
//        SellOperateUtil.execudShell(pb,shellPath);
//        return null;
    }





    public static void main(String[] args) throws URISyntaxException {
//        //资源访问地址=>file:/Users/aa/Unistack/tamboo-sa/tamboo-collector/tamboo-collector-flume/target/classes/
//        URL webServiceConfigPath1 = WebService.class.getResource("/");
//        //File file1 = new File(webServiceConfigPath1); 不能通过URL new出File
//        String file1 = WebService.class.getResource("/").getFile();
//        //资源标记=>file:/Users/aa/Unistack/tamboo-sa/tamboo-collector/tamboo-collector-flume/target/classes/
//        URI webServiceConfigPath2 = WebService.class.getResource("/").toURI();
//        File file2 = new File(webServiceConfigPath2);
//        //访问地址:/Users/aa/Unistack/tamboo-sa/tamboo-collector/tamboo-collector-flume/target/classes/
//        String webServiceConfigPath3 = WebService.class.getResource("/").getPath();
//        File file3 = new File(webServiceConfigPath2);
//        System.out.println(webServiceConfigPath1);
//        System.out.println(webServiceConfigPath2);
//        System.out.println(webServiceConfigPath3);

        /**
         * test
         */
        WebServiceSource webServiceSource = new WebServiceSource();
        //加载完整配置到输入流
        InputStream is = WebService.class.getResourceAsStream("/tamboo-collector/src/main/resources/webService1.properties");
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
        dcConfig.setDcType(DcType.WEBSERVICE);
        //设置监控httpPort
        webServiceSource.webService.setHttpPort(dcConfig.getConf().getString("httpPort"));
        //方法测试
//        //1.校验dcConfig配置
//        webServiceSource.checkConfig(dcConfig);
//        //2.创建webService配置
        webServiceSource.createConf(dcConfig);
        //3.创建collector
        //4.启动采集器
//        webServiceSource.startCollector(dcConfig);
        //5.停止采集器
//        webServiceSource.stopCollector(dcConfig);
//        webServiceSource.createDataCollector(dcConfig);
    }
}