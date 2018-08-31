package com.unistack.tamboo.compute.utils.commons;

import com.unistack.tamboo.compute.exception.DataFormatErrorException;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.io.*;
import java.util.*;


/**
 * @author hero.li
 */
public class CalcJsonUtil implements Serializable{

    /**
     * 获取target的key对应的值<br/>
     * @param target
     * @param key
     * @return
     */
    public static String getValue(JSONObject target, String key) throws DataFormatErrorException {
        if(null == target || StringUtils.isBlank(key)){
            throw new DataFormatErrorException("参数为空");
        }

        String[] keys = key.split("\\.");
        if(keys.length == 1){
            return target.getString(keys[0]);
        }

        for(int i=0;i<keys.length;i++){
            String item = keys[i];
            if(i == keys.length-1){
                String result = target.getString(item);
                if(null == result) {
                    throw new DataFormatErrorException("该JSON对象没有["+key+"]这个节点");
                }
                return result;
            }else{
                target = target.getJSONObject(item);
                if(null == target) {
                    throw new DataFormatErrorException("该JSON对象没有["+key+"]这个节点");
                }
            }
        }
        return "";
    }

    /**
     * 把数组中的json的值都赋值一份到第一个json中<br/>
     * @param arr
     * @return
     */
    public static JSONObject addTo(JSONObject ... arr){
        if(null == arr) {
            return null;
        }
        if(arr.length == 1) {
            return arr[0];
        }

        JSONObject first = arr[0];
        for(int i=1;i<=arr.length-1;i++){
            JSONObject item =  arr[i];
            if(null == item) {
                continue;
            }
            Iterator<String> itr = item.keySet().iterator();
            while(itr.hasNext()){
                String key = itr.next();
                Object value = item.get(key);
                first.put(key,value);
            }
        }

        return first;
    }



    private static void addTo(JSONObject first,JSONObject seconds){
        Iterator<String> itr = seconds.keySet().iterator();
        while(itr.hasNext()){
            String key = itr.next();
            first.put(key,seconds.get(key));
        }
    }

    /**
     * 获取该JSON的一个扁平化json
     * @param target
     * @return
     */
    public static JSONObject flatJson(JSONObject target){
        JSONObject result = new JSONObject();
        Iterator<String> itr = target.keySet().iterator();
        while(itr.hasNext()){
            String key = itr.next();
            Object o = target.get(key);
            String simpleName = o.getClass().getSimpleName();
            if("String".equals(simpleName)){
                result.put(key,o);
            }else if("JSONObject".equals(simpleName)){
                JSONObject subJson = flatJson((JSONObject) o);
                addTo(result,subJson);
            }
        }
        return result;
    }

    public static JSONObject rowToJson(Row row){
        StructType schema = row.schema();
        scala.collection.Iterator<StructField> itr = schema.toIterator();

        JSONObject result = new JSONObject();
        while(itr.hasNext()){
            StructField item = itr.next();
            String key = item.name();
            Object value = row.getAs(key);
            result.put(key,value);
        }
        return result;
    }

    public static List<JSONObject> dataSetToJsonList(Dataset<Row> dataset){
        String[] columns = dataset.columns();
        Iterator<Row> itr = dataset.toLocalIterator();

        List<JSONObject> result = new ArrayList<>();
        while(itr.hasNext()){
            Row row = itr.next();
            JSONObject o = new JSONObject();
            for(String key : columns){
                o.put(key,row.getAs(key));
            }
            result.add(o);
        }
        return result;
    }

    /**
     *
     * 把json格式的数据库信息改为特定的连接数据库的Map
     *
     {
         "creator": "admin","addTime": 1530849832000,
         "ip": "192.168.1.191","dbName": "test","dbType": "MYSQL",
         "dbtable": "lyzx_test_data2","dataSourceDesc": "193上面的test库",
         "isActive": "1","dataSourceName": "191_test","dataSourceId": 22576,
         "password": "welcome1","port": "3308","username": "root"
     }
     * @param json
     * @return
     */
    public static Map<String,String> jsonToDbMap(JSONObject json){
        String dbType = json.getString("dbType");
        String url = "";
        if("MYSQL".equals(dbType)){
            //192.168.1.191:3308/test?
            String ip = json.getString("ip");
            String port = json.getString("port");
            String dbName = json.getString("dbName");
            url="jdbc:mysql://"+ip+":"+port+"/"+dbName+"?characterEncoding=UTF8";
        }

        HashMap<String,String> resultMap = new HashMap<>();
        resultMap.put("url",url);
        resultMap.put("dbtable",json.getString("dbtable"));
        resultMap.put("user",json.getString("username"));
        resultMap.put("password",json.getString("password"));
        return  resultMap;
    }




//    /**
//     * 这个方法用来转换json到xml格式<br/>
//     * 注意点:1、json的值不能是json格式的字符串<br/>
//     *       2、此json格式必须只有一个根节点并且名字是root<br/>
//     * @param jsonStr json格式的字符串<br/>
//     * @return Dom4j中的Document对象,如果想将其转换为xml格式的字符串只需要调用其doc.asXml()方法即可 <br/>
//     *
//     * @throws DataFormatErrorException 如果传入的jsonStr不是标准的JSON格式的字符串则抛出此异常 <br/>
//     */
//    public Document json2Xml(String jsonStr) throws DataFormatErrorException {
//        return json2Xml(jsonStr,"root");
//    }

//    public Document json2Xml(String jsonStr,String rootName) throws DataFormatErrorException {
//        if(null == jsonStr || "".equals(jsonStr)){
//            throw  new IllegalArgumentException("jsonStr->xml:参数为空");
//        }
//
//        JSONObject target;
//        try{
//            target = JSONObject.parseObject(jsonStr);
//        }catch(Exception e){
//            e.printStackTrace();
//            throw new DataFormatErrorException("字符串["+jsonStr+"]不是json格式");
//        }
//
//        Set<String> set = target.keySet();
//        if(set.size() == 1){
//            Iterator<String> itr = set.iterator();
//            rootName = itr.next();
//        }
//
//        Element e = getElementFromJsonObject(target,rootName);
//
//        Document root = DocumentHelper.createDocument(e);
//        return root;
//    }

//    /**
//     * 把JSONObject转换为对应的Element<br/>
//     *
//     * @param o JSONObject对象<br/>
//     * @param rootName 根节点名字<br/>
//     * @return 转换后的Element元素
//     */
//    private Element getElementFromJsonObject(JSONObject o, String rootName){
//        Element result = new DOMElement(rootName);
//
//        Iterator<String> itr = o.keySet().iterator();
//        while(itr.hasNext()){
//            String key = itr.next();
//            try{
//                JSONObject o1 = o.getJSONObject(key);
//                Element e = getElementFromJsonObject(o1,key);
//                result.add(e);
//            }catch(Exception e){
//                String value = o.getString(key);
//                Element item = new DOMElement(key);
//                item.setText(value);
//                result.add(item);
//            }
//        }
//        return result;
//    }

    //    public static JSONObject toJsonObject(InputStream in){
//        BufferedReader br = null;
//        try {
//            br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
//            String line;
//            StringBuilder sb = new StringBuilder();
//
//            while ((line = br.readLine()) != null){
//                sb.append(line);
//            }
//            return JSONObject.parseObject(sb.toString());
//        }catch(UnsupportedEncodingException e){
//            e.printStackTrace();
//            LOGGER.error("编码格式错误!",e);
//            return null;
//        }catch(IOException e){
//            e.printStackTrace();
//            LOGGER.error("IO异常,文件读取失败",e);
//            return null;
//        }ly {
//            if(null != br) {
//                try {
//                    br.close();
//                }catch(IOException e){
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    //    /**
//     * 对多个json做合并操作，不做扁平化操作 <br/>
//     * @param arr
//     * @return
//     */
//    public static JSONObject jsonUnion(JSONObject... arr){
//        if(null == arr || arr.length == 1){
//            throw new IllegalArgumentException("参数为空、一个JSON不需要合并");
//        }
//
//        JSONObject o = new JSONObject();
//
//        for(JSONObject item : arr){
//            Iterator<String> itr = item.keySet().iterator();
//            while(itr.hasNext()){
//                String key = itr.next();
//                Object c = item.get(key);
//                o.put(key,c);
//            }
//        }
//        return o;
//    }
}