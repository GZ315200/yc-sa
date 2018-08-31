package com.unistack.tamboo.compute.calc;


/**
 * 这个类用来测试计算集群 <br/>
 */
public class CalcTest{
//    private static  XmlUtil xmlUtil = new XmlUtil();
//    private static  JSONUtil jsonUtil = new JSONUtil();

//    /**
//     * 测试方法 <br/>
//     * @param originXmlMsg  xml格式的数据消息 <br/>
//     * @param config        Document 类型的配置文件 <br/>
//     * @return
//     */
//    public static Result calcTest(String originXmlMsg,Document config){
//        try {
//            Document originDoc = xmlUtil.toDocument(originXmlMsg);
//            Document flatDoc = xmlUtil.flatXml(originDoc);
//            JSONObject paramJson = xmlUtil.xml2Json(flatDoc.asXML());
//            RootTag rt = new RootTag();
//            Result fr = rt.startParse(config.getRootElement(),paramJson);
//            return fr;
//        }catch(DataFormatErrorException e){
//            e.printStackTrace();
//            Result fr = new Result();
//            fr.setSuccess(false);
//            fr.setError("原消息格式不是标准的xml格式");
//            return fr;
//        }
//    }
//
//
//    /**
//     * 测试方法 <br/>
//     * @param originXmlMsg
//     * @param xmlConfig
//     * @return
//     */
//    public static Result calcTestXmlConf(String originXmlMsg,String xmlConfig){
//        try{
//            Document config  = xmlUtil.toDocument(xmlConfig);
//            Result fr = calcTest(originXmlMsg, config);
//            return fr;
//        }catch(DataFormatErrorException e){
//            e.printStackTrace();
//            Result fr = new Result();
//            fr.setSuccess(false);
//            fr.setError("配置文件不是标准的xml格式");
//            return fr;
//        }
//    }
//
//    /**
//     * 测试方法 <br/>
//     * @param originXmlMsg  xml格式的原数据 <br/>
//     * @param jsonConfig    json格式的配置文件
//     * @return
//     */
//    public static Result calcTestJsonConf(String originXmlMsg,String jsonConfig){
//        if(null == originXmlMsg || "".equals(originXmlMsg.trim()) || null == jsonConfig || "".equals(jsonConfig)){
//            Result fr = new Result();
//            fr.setSuccess(false);
//            fr.setError("参数为空!");
//            return fr;
//        }
//
//        try{
//            Document config = jsonUtil.json2Xml(jsonConfig);
//            return calcTest(originXmlMsg,config);
//        }catch(DataFormatErrorException e){
//            e.printStackTrace();
//            Result fr = new Result();
//            fr.setSuccess(false);
//            fr.setError("json格式的配置文件不是标准格式");
//            return fr;
//        }
//    }


    /**
     *
     * @param conf 数据清洗的配置文件如下所示:
     *   {"filter":[{"type":"Rename","fields":[{"old":"A","new":"A1"},{"old":"B","new":"B1"}]}]}
     *
     * @param mesg 要被清洗的数据
     *
     * @return
     */
//    public static JSONObject cleanTest(String mesg,String conf){
//        JSONObject result = new JSONObject();
//        JSONObject jsonConf;
//        try{
//            jsonConf = JSONObject.parseObject(conf);
//        }catch(Exception e){
//            result.put("code","199");
//            result.put("msg","配置文件不是标准的json格式!");
//            return result;
//        }
//
//        JSONObject jsonData;
//        try{
//            jsonData = JSONObject.parseObject(mesg);
//        }catch(Exception e){
//            result.put("code","199");
//            result.put("msg","要清洗的数据不是标准的json格式!");
//            return result;
//        }
//
//        JSONArray filters = jsonConf.getJSONArray("filter");
//        for(Object o  : filters){
//            JSONObject f  = (JSONObject)o;
//            DataCleanProcess.dataClean(jsonData,f);
//        }
//
//        result.put("code","200");
//        result.put("msg","");
//        result.put("data",jsonData);
//        return result;
//    }

//    @Test
//    public void test1(){
//        String data = "{\"AbcDe\":\"被重命名的字段\",\"B\":\"b-value\",\"C\":\"c-value\",\"D\":\"20180909101010\",\"E\":\"    E-value   \",\"ip\":\"192.168.1.201\"}";
//        System.out.println(data);
//        //trim date
//
//        String jsonInfo = "{\"filter\":[{\"type\":\"IPToLong\",\"fields\":[{\"field\":\"ip\",\"new_field\":\"longIp\"}]}]}";
//
//
//        JSONObject cleanedData = cleanTest(data,jsonInfo);
//        System.out.println(cleanedData.getString("data"));
//    }


}