package com.unistack.tamboo.compute.process.until.dataclean.join;

import com.alibaba.fastjson.JSONObject;
import org.dom4j.Element;

public interface Join {

    void init(JSONObject config) throws Exception;

    /**
     * 和数据库打交道,做CRUD操作 <br/>
     * @param joinOperate 具体操作流程 <br/>
     * @param paramJson   SQL语句中需要的参数 <br/>
     * @return
     * @throws Exception
     */
    JSONObject join(Element joinOperate, JSONObject paramJson) throws Exception;
}
