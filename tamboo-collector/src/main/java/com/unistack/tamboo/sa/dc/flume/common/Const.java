package com.unistack.tamboo.sa.dc.flume.common;

/**
 * @program: tamboo-sa
 * @description: 模块一些枚举类
 * @author: Asasin
 * @create: 2018-05-15 14:12
 **/
public class Const {

    public static enum ErrorMessage {
        INTERNAL_SERVER_UNKNOWN_ERROR("服务器异常,请联系管理员或稍后重试。"),
        DATABASE_ERROR("数据库异常,请联系管理员或稍后重试。"),
        NOT_LOGIN_ERROR("您还没有登录,请登录"),
        OPERATION_ERROR("操作失败"),
        NULL_PARAM_INPUT("无输入参数或参数值为空"),
        EMPTY_VALUES("目标数据为空"),
        UPLOAD_ERROR("上传失败"),
        UPLOAD_SIZE_ERROR("Upload file is too large, please upload files less than 20M."),
        RESOURCE_MANAGER_ERROR("资源管理器交互错误");

        private String message;

        private ErrorMessage(String message) {
            this.message = message;
        }

        public String toString() {
            return this.message;
        }
    }

    public static enum AgentCommandParam {
        C_MAIN("main.sh", "agent操作shell脚本"),
        C_INSTALL("install", "安装，包含全流程"),
        C_CHECKAGENT("checkAgent", "检验IP网络、用户名密码"),
        C_SSHAGENT("sshAgent", "检验登陆"),
        C_STARTAGENT("startAgent", "启动采集器"),
        C_STOPAGENT("stopAgent", "停止采集器"),
        C_VERSION("version", "查看版本"),
        C_HELP("help", "帮助文档");

        private String param;
        private String tip;

        private AgentCommandParam(String param, String tip) {
            this.param = param;
            this.tip = tip;
        }

        public String getParm() {
            return this.param;
        }

        public String getTip() {
            return this.tip;
        }
    }

    public static enum ConfigKey {
        UPLOAD_FILE_PATH("upload.file.path", "上传文件存储路径"),
        UPLOAD_FILE_PATH_TMP("upload.file.path.tmp", "上传文件的临时路径"),
        DC_SHELL_SCRIPT_PATH("dc.shell.script.path", "采集器校验与执行的脚本存放目录"),
        QUEUE_LEN("job.queue.len", "任务队列长度");

        private String keyName;
        private String tip;

        private ConfigKey(String keyName, String tip) {
            this.keyName = keyName;
            this.tip = tip;
        }

        public String getKey() {
            return this.keyName;
        }

        public String getTip() {
            return this.tip;
        }
    }


    public static enum HostState {
        DELETED(-1, "已删除"),
        MANAGED(0, "已受托管，agent-未安装"),
        INSTALLED(1, "agent-已安装"),
        INSTALLING(2, "agent-安装中");
//        STARTING(3, "agent-启动中"),
//        STOPING(4, "agent-停止中"),
//        RUNNING(5, "agent-运行中");

        private int state;
        private String tip;

        private HostState(int state, String tip) {
            this.state = state;
            this.tip = tip;
        }

        public int getValue() {
            return this.state;
        }

        public int getResolvedValue() {
            return 1;
        }

        public String getTip() {
            return this.tip;
        }
    }

    public static enum HostType {
        LOCAL(0, "本地主机"),
        REMOTE(1, "远程主机");
        private int state;
        private String tip;

        private HostType(int state, String tip) {
            this.state = state;
            this.tip = tip;
        }

        public int getValue() {
            return this.state;
        }

        public int getResolvedValue() {
            return 1;
        }

        public String getTip() {
            return this.tip;
        }
    }
}
    