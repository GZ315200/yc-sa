package com.unistack.tamboo.commons.utils.invoking;

public interface Invoking {
    /**
     * 校验配置信息
     * @param config
     * @return
     */
    InvokeResult checkConfig(InvokingConfig config);

    /**
     * 创建配置文件
     * @param dc
     * @return
     */
    InvokeResult createConf(InvokingConfig dc);

    /**
     * 启动采集
     * @param dc
     * @return
     */
    InvokeResult startCollector(InvokingConfig dc);

    /***
     *停止采集
     * @param dc
     * @return
     */
    InvokeResult stopCollector(InvokingConfig dc);

}
