package com.unistack.tamboo.sa.dc.flume.invoking;

import com.unistack.tamboo.sa.dc.flume.common.DcConfig;
import com.unistack.tamboo.sa.dc.flume.common.DcResult;

public interface FlumeInvoking {


    /**
     * 校验配置信息
     * @param dc
     * @return
     */
    DcResult checkConfig(DcConfig dc);

    /**
     * 创建配置文件
     * @param dc
     * @return
     */
    DcResult createConf(DcConfig dc);

    /**
     * 启动采集
     * @param dc
     * @return
     */
    DcResult startCollector(DcConfig dc);

    /***
     *停止采集
     * @param dc
     * @return
     */
    DcResult stopCollector(DcConfig dc);

}
