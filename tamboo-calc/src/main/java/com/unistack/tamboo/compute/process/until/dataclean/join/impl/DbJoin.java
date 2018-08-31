package com.unistack.tamboo.compute.process.until.dataclean.join.impl;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.compute.exception.DataFormatErrorException;
import com.unistack.tamboo.compute.exception.SqlMatchException;
import com.unistack.tamboo.compute.process.until.dataclean.util.DBUtil;
import com.unistack.tamboo.compute.utils.spark.SqlUtil;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.security.InvalidParameterException;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;

/**
 * @author  hero.li
 */
public class DbJoin {
    private static Logger LOGGER = LoggerFactory.getLogger(DbJoin.class);
    private JSONObject result = null;
    private  SqlUtil sqlUtil = new SqlUtil();
    private  DBUtil dbUtil = new DBUtil();
//    private CalcJsonUtil jsonUtil = new CalcJsonUtil();


    public void init(JSONObject config) throws Exception {

    }

    /**
     *
     *<join sourceId="mysql1">
     *     <updateSql>update table set c1 = @A.B.C where id = @A.B.D;</updateSql>
     *     <!-- 将结果并入到数据源中-->
     *     <joinSql> select * from table where id=#sysinfo.aocid </joinSql>
     *     <!-- 直接将结果作为数据源-->
     *     <resultSql>select * from table where id=#sysinfo.aocid </resultSql>
     *</join>
     *
     * @param joinOperate  结构如上 <br/>
     * @param paramJson    json对象 <br/>
     * @return
     * @throws Exception
     */

    public JSONObject join(Element joinOperate, JSONObject paramJson){
        if(null == joinOperate || null == paramJson){
            LOGGER.info("join.参数为空！");
            throw new IllegalArgumentException("参数为空!");
        }

        String sourceId = joinOperate.attributeValue("sourceId");
        Iterator<Element> itr = joinOperate.elementIterator();
        while(itr.hasNext()){
            Element e = itr.next();
            String keyName = e.getName();
            if(!e.isTextOnly()){
                LOGGER.info("节点["+keyName+"]");
                continue;
            }

            switch(keyName){
                case "updateSql" : updatesqlProcess(e,paramJson,sourceId);          break;
                case "resultSql" :  resultSqlProcess(e,paramJson,sourceId); break;
//                case "joinSql"   : joinsqlProcess(e,paramJson,sourceId);            break;
                default: new InvalidParameterException("xml文件配置错误,["+keyName+"]是无效的标签");
            }
        }
        return result;
    }


    /**
     * 对于update节点的处理<br/>
     *  <updateSql>
     *     update table set c1 = @A.B.C where id = @A.B.D;
     *  </updateSql>
     * @param updateEle update节点结构如上.
     * @param paramJson 参数所在的json
     */
    private int updatesqlProcess(Element updateEle, JSONObject paramJson, String sourceId){
        try {
            String updateSql = updateEle.getText();
            Object[] prepareSql = sqlUtil.ParseSql(updateSql);
            String exeSql = sqlUtil.getExeSql(prepareSql,paramJson);
            int count = (Integer)dbUtil.executeSQL(sourceId,exeSql);
            LOGGER.info("update执行成功,影响的条数是:"+count);
            return count;
        }catch(SqlMatchException e){
            e.printStackTrace();
            LOGGER.error("sql书写不规范",e);
            return -10;
        }catch(DataFormatErrorException e){
            e.printStackTrace();
            LOGGER.error("数据格式错误",e);
            return -11;
        }
        catch (InvalidParameterException e) {
            e.printStackTrace();
            LOGGER.error("参数无效",e);
            return -12;
        }
    }

    /**
     *  <resultSql>
     *      select * from table where id=#sysinfo.aocid;
     *  </resultSql>
     *
     * @param resultEle
     * @param paramJson
     * @param sourceId
     * @return
     */
    private List<JSONObject> resultSqlProcess(Element resultEle, JSONObject paramJson, String sourceId){
        String vagueSql = resultEle.getText();
        try{
            Object[]  prepareSql = sqlUtil.ParseSql(vagueSql);
            String exeSql = SqlUtil.getExeSql(prepareSql,paramJson);
            ResultSet result = (ResultSet) dbUtil.executeSQL(sourceId,exeSql);
            return SqlUtil.resultSetToJSON(result);
        }catch (SqlMatchException e){
            e.printStackTrace();
            LOGGER.error("sql匹配异常",e);
            return null;
        }catch(InvalidParameterException e) {
            e.printStackTrace();
            LOGGER.error("无效的参数",e);
            return null;
        }catch(DataFormatErrorException e) {
            e.printStackTrace();
            LOGGER.error("数据格式错误",e);
            return null;
        }
    }

//    /**
//     * <joinSql>
//     *    select * from table where id=#sysinfo.aocid;
//     * </joinSql>
//     *
//     * @param joinsqlEle 格式如上 <br/>
//     * @param paramJson  参数所在的json,这个json是通过xml扁平化后而得来的 <br/>
//     * @param sourceId   数据源标识 <br/>
//     *
//     * join操作是将sql查询的结果和接手收到的源数据做一个union操作后再作为数据源 <br/>
//     * 相比result操作多了一步union操作 <br/>
//     */
//    private JSONObject joinsqlProcess(Element joinsqlEle, JSONObject paramJson, String sourceId){
//        List<JSONObject> resultSqlResult = resultSqlProcess(joinsqlEle, paramJson, sourceId);
//        return jsonUtil.jsonUnion(resultSqlResult,paramJson);
//    }
}
