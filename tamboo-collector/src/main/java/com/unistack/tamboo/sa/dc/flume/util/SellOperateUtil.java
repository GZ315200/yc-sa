package com.unistack.tamboo.sa.dc.flume.util;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.unistack.tamboo.sa.dc.flume.common.Const;
import com.unistack.tamboo.sa.dc.flume.common.DcConfig;
import com.unistack.tamboo.sa.dc.flume.common.DcResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @program: tamboo-sa
 * @description: 关于shell脚本一些操作
 * @author: Asasin
 * @create: 2018-05-15 14:34
 **/
public class SellOperateUtil {
    private static  Logger logger = LoggerFactory.getLogger(SellOperateUtil.class);
    private static  String SUCCESS = "SUCCESS";
    private static  String FAILED = "FAILED";
    //采集器校验与执行的脚本存放目录
    private  static String DC_SHELL_SCRIPT_PATH = "/home/flume/tss/bin";

    /**
     * 执行shell命令
     * @param commandLine 命令行
     * @return
     */
//    public static DcResult execCommand(String commandLine) {
//        Process ps;
//         StringBuffer rsLine = new StringBuffer();
//        if (Objects.isNull(commandLine)) {
//            return new DcResult(false,"空命令行");
//        }
//        System.out.println(commandLine);
//        StringBuilder logStr = new StringBuilder(commandLine);
//        String[] cmds = new String[]{"/bin/sh", "-c", commandLine};
//        logStr.append("\nexec-result:");
//        try {
//            ps = Runtime.getRuntime().exec(cmds);
//            ps.waitFor();
//            BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
//            String line;
//            try {
//                while ((line = br.readLine()) != null) {
//                    logStr.append(line).append("\n");
//                }
//            } ly {
//                br.close();
//            }
//            logger.info("exec-finish:" + logStr.toString());
//            return new DcResult(true,rsLine.toString());
//        } catch (IOException | InterruptedException e) {
//            logStr.append(e.getMessage());
//            logger.error("exec-error:" + logStr.toString());
//            return new DcResult(false,"Exception: " + e.getMessage());
//        }
//    }
    public static DcResult execCommand(String commandLine) {
        Process ps;
         List<String> rsLine = Lists.newArrayList();
        if (Objects.isNull(commandLine)) {
            return new DcResult(false,"空命令行");
        }
        System.out.println(commandLine);
        StringBuilder logStr = new StringBuilder(commandLine);
        String[] cmds = new String[]{"/bin/sh", "-c", commandLine};
        logStr.append("\nexec-result:");
        try {
            ps = Runtime.getRuntime().exec(cmds);
            ps.waitFor();

            BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            String line;
            try {
                while ((line = br.readLine()) != null) {
                    rsLine.add(line);
                    logStr.append(line).append("\n");
                }
            } finally {
                br.close();
            }
            logger.info("exec-finish:" + logStr.toString());
            return new DcResult(true,rsLine.toString());
        } catch (Exception  e) {
            logStr.append(e.getMessage());
            logger.error("exec-error:" + logStr.toString());
            return new DcResult(false,"Exception: " + e.getMessage());
        }
    }



    /**
     * 取得对应shell脚本执行命令行
     *
     * @param opParam 参数及用途
     *                help              display this help text
     *                install           install agent
     *                checkAgent	  	checkAgent is usefull
     *                sshAgent		    ssh-copy-id agent
     *                startAgent        start agent
     *                stopAgent		    stop agent
     *                version		    display version
     * @return
     */
    //String ip, String user, String pwd, String path
    public static String getAgentCommand(Const.AgentCommandParam opParam, DcConfig dcConfig) {
        //进入到相应目录后执行才可能不会出现路径错误问题
        String ip = dcConfig.getConf().getString("ip");
        String user = dcConfig.getConf().getString("user");
        String pwd = dcConfig.getConf().getString("pwd");
        //远程地址agent安装地址
        String path = dcConfig.getConf().getString("path");
        //脚本目录
        String cd = "cd " + DC_SHELL_SCRIPT_PATH;
        String baseCommand = " sh " + Const.AgentCommandParam.C_MAIN.getParm() + " " + opParam.getParm() + " -ip " + ip + " -u " + user + " -p " + pwd+ " -h " +path;
        return cd + " && " + baseCommand;
    }

    /**
     * 期望执行的命令后的输出格式：
     * {step:2,code:0,msg:"username/passwd for ip:192.168.1.201 error"}
     * {step:2,code:1,msg:"username/passwd for ip:192.168.1.201 success"}
     *
     * @return
     */
    public String processShellResult(String retMsg) {
        if (retMsg != null && retMsg.startsWith("{\"step")) {
            Map m = (Map) JSON.parse(retMsg);
            Integer v = (Integer) m.get("code");
            return v != null && v > 0 ? SUCCESS : (String) m.get("msg");
        }
        return retMsg;
    }

    /**
     * 用于检查配置文件是否可用
     * @param value
     * @return
     */

    public static boolean matchValue(String value) {
        String regex = "[(\\w)+\\.]+\\s*=\\s*(\\w*)\\s(\\w*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    public static boolean matchValueSec(String value) {
        String regex = "[(\\w)+\\.]+\\s*=\\s*(\\/\\w*)+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    public static boolean matchValueThird(String value) {
        String regex = "[(\\w)+\\.]+\\s*=\\s*[(\\w)+\\.]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    /**
     * 执行脚本
     * @param processBuilder
     * @param shellPath
     * @return
     */
    public static DcResult execudShell(ProcessBuilder processBuilder, String shellPath){
        int runningStatus = 0;
        String s = null;
        //判断文件地址是否存在
        if(StringUtils.isBlank(shellPath)){
            return  new DcResult(false," filepath inexistence");
        }
        processBuilder.directory(new File(shellPath));
        try {
            Process p = processBuilder.start();
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((s = stdInput.readLine()) != null) {
                logger.error(s);
            }
            while ((s = stdError.readLine()) != null) {
                logger.error(s);
            }
            try {
                runningStatus = p.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        if (runningStatus != 0) {
            return  new DcResult(false,"IOException");
        }
        return null;
    }

}
    