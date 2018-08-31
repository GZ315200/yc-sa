package com.unistack.tamboo.mgt.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.unistack.tamboo.mgt.common.MonitorKeyField;
import com.unistack.tamboo.mgt.common.ServerResponse;
import com.unistack.tamboo.mgt.model.sys.SysUser;
import com.unistack.tamboo.mgt.utils.PropertiesUtil;
import com.unistack.tamboo.sa.dc.flume.common.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Gyges Zean
 * @date 2018/5/17
 */
public abstract class BaseService extends MonitorKeyField {
    protected static final String SUCCESS = "SUCCESS";

    public static String USERNAME = "username";
    public static String PASSWORD = "password";

    private static final String EMAIL_REGEX = "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?";

    private static final String PHONE_REGEX = "0?(13|14|15|18)[0-9]{9}";

    protected Logger logger = LoggerFactory.getLogger(BaseService.class);

    public interface Show {
        int Y = 1;
        int N = 0;
    }

    public enum EClusterType {
        SPARK,
        KAFKA;
    }


    public interface AclOperation {
        /**
         * 读和写
         */
        int READ_WRITE = 1;
        /**
         * 读
         */
        int READ = 2;
        /**
         * 写
         */
        int WRITE = 3;
    }


    public interface SummaryType {
        int TD = 1; //采集器
        int TM = 2; //kafka
        int TS = 3;//spark streaming
        int TC = 4;//kafka com.unistack.tamboo.sa.dc.connect
    }


    public static final String KAFKA_RECORD = "KAFKA_RECORD_";

    public static final String FLUME_RECORD = "FLUME_RECORD_";

    public static final String SPARK_RECORD = "SPARK_RECORD_";

    public static final String OFFSET_COLLECT = "OFFSET_COLLECT_";

    public static final String CONNECT_RECORD = "CONNECT_RECORD_";

    public static final String CALC_SERVICE_INFO = "calcServiceInfo";

    public static final String ZOOKEEPER_METRICS_DATA = "zookeeperMetricsData";


    public static long getZeroTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.AM_PM, Calendar.AM);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime().getTime();
    }

    private static long getNextDayZeroTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.AM_PM, Calendar.AM);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime().getTime() + 24 * 60 * 60 * 1000;
    }

    public static long get7pZeroTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek() + 1);
        cal.set(Calendar.AM_PM, Calendar.AM);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }


    protected void userValidator(SysUser sysUser) {
        checkArgument(check(EMAIL_REGEX, sysUser.getEmail()), "email is not right");
        checkArgument(check(PHONE_REGEX, sysUser.getPhone()), "phone number is not right");
    }


    private boolean check(String regex, String value) {
        Pattern r = Pattern.compile(regex);
        Matcher m = r.matcher(value);
        return m.matches();
    }


    protected long computeMessageRate(long subOffset, long timeInterval) {
        BigDecimal bigDecimal = new BigDecimal(subOffset);
        BigDecimal bigDecimal1 = new BigDecimal(timeInterval);
        return bigDecimal.divide(bigDecimal1, RoundingMode.CEILING).longValue();
    }


    //生成随机用户名，数字和字母组成,
    public static String getStringRandom(String type) {
        String salt = "Tu_";
        String passSalt = "Tp_";
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 7;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        if (type.equals("username")) return salt + buffer.toString();
        else if (type.equals("password")) return passSalt + buffer.toString();
        else return null;
    }

    /**
     * 执行shell命令
     *
     * @param commandLine 命令行
     * @return
     */
    protected ServerResponse<String[]> execCommand(String commandLine) {
        Process ps;
        final List<String> rsLine = Lists.newArrayList();
        if (Objects.isNull(commandLine)) {
            return ServerResponse.createByErrorMsg("空命令行");
        }
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
            return ServerResponse.createBySuccess(rsLine.toArray(new String[0]));
        } catch (IOException | InterruptedException e) {
            logStr.append(e.getMessage());
            logger.error("exec-error:" + logStr.toString());
            return ServerResponse.createByErrorMsg("Exception: " + e.getMessage());
        }
    }

    /**
     * 期望执行的命令后的输出格式：
     * {step:2,code:0,msg:"username/passwd for ip:192.168.1.201 error"}
     * {step:2,code:1,msg:"username/passwd for ip:192.168.1.201 success"}
     *
     * @return
     */
    protected String processShellResult(String retMsg) {
        if (retMsg != null && retMsg.startsWith("{\"step")) {
            Map m = (Map) JSON.parse(retMsg);
            Integer v = (Integer) m.get("code");
            return v != null && v > 0 ? SUCCESS : (String) m.get("msg");
        }

        return retMsg;
    }

    /**
     * 取得对应shell执行命令行
     *
     * @param opParam 参数及用途
     *                help                      display this help text
     *                install              	    install agent
     *                checkAgent	  	    checkAgent is usefull
     *                sshAgent		    ssh-copy-id agent
     *                startAgent                start agent
     *                stopAgent		    stop agent
     *                version		    display version
     * @return
     */
    protected String getAgentCommand(Const.AgentCommandParam opParam, String ip, String user, String pwd, String path) {
        //进入到相应目录后执行才可能不会出现路径错误问题
        String cd = "cd " + PropertiesUtil.get(Const.ConfigKey.DC_SHELL_SCRIPT_PATH.getKey());
        String baseCommand = " sh " + Const.AgentCommandParam.C_MAIN.getParm() + " " + opParam.getParm() + " -ip " + ip + " -u " + user + " -p " + pwd + " -h " + path;

        return cd + " && " + baseCommand;
    }

    public String getStringTrim(Object obj) {
        if (obj == null) {
            return "";
        }
        return obj.toString().trim();
    }
}
