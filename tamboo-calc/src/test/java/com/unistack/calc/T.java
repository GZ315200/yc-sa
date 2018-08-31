package com.unistack.calc;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.commons.utils.enums.EDatabaseType;
import com.unistack.tamboo.compute.exception.DataFormatErrorException;
import com.unistack.tamboo.compute.model.DbColumns;
import com.unistack.tamboo.compute.process.until.dataclean.filter.impl.ContainsFilter;
import com.unistack.tamboo.compute.utils.RedisUtil;
import com.unistack.tamboo.compute.utils.commons.XmlUtil;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;


public class T {

    @Test
    public void test1(){
            Optional<String> op1 = Optional.of("");
            System.out.println(op1.get()+"   "+op1.isPresent());

            Optional op2 = Optional.empty();
            System.out.println(op2.isPresent());
    }


    @Test
    public void test4() throws DocumentException, DataFormatErrorException {
        String x = "<s:Envelope xmlns:a=\"http://www.w3.org/2005/08/addressing\"" +
                "    xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\">" +
                "    <s:Header>\n" +
                "        <a:Action s:mustUnderstand=\"1\">http://schemas.microsoft.com/windows/management/2012/01/enrollment/IDiscoveryService/Discover</a:Action>" +
                "        <a:MessageID>urn:uuid:748132ec-a575-4329-b01b-6171a9cf8478</a:MessageID>" +
                "        <a:ReplyTo>\n" +
                "            <a:Address>http://www.w3.org/2005/08/addressing/anonymous</a:Address>" +
                "        </a:ReplyTo>\n" +
                "        <a:To s:mustUnderstand=\"1\">https://host.linyiheng.cn:8004/EnrollmentServer/Discovery.svc</a:To>" +
                "    </s:Header>" +
                "    <s:Body>"+
                "        <Discover" +
                "            xmlns=\"http://schemas.microsoft.com/windows/management/2012/01/enrollment\">" +
                "            <request xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                "                <EmailAddress>admin@aa</EmailAddress>" +
                "                <RequestVersion>1.0</RequestVersion>" +
                "            </request>" +
                "        </Discover>" +
                "    </s:Body>" +
                "</s:Envelope>";

        XmlUtil u = new XmlUtil();
        u.toDocument(x);


//        Document doc = DocumentHelper.parseText(x);
//        recursive(doc.getRootElement());
    }


    private void recursive(Element e){
        if(e.isTextOnly()){
            System.out.println(e.getName());
        }

        Iterator<Element> itr = e.elementIterator();
        while(itr.hasNext()){
            Element item = itr.next();
            if(item.isTextOnly()){
                System.out.println(item.getName()+"     "+item.getNamespacePrefix());
            }else{
                recursive(item);
            }
        }
    }


//    @Test
//    public void test5() throws IOException{
//        ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream("/Users/frank/Desktop/shell/result.obj"));
//        o.writeObject(new JsonJoin(new JSONObject()));
//
//    }

    @Test
    public void test6(){
        DbColumns aa = new DbColumns.Builder()
                .columnName("aa")
                .columnSize("23")
                .dataType("1")
                .builder();

        System.out.println(aa);
    }

    @Test
    public void test7(){
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String format = f.format(now);
        System.out.println(format);
    }


    @Test
    public void test8(){
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        TemporalAccessor parse = f.parse("2019-10-10 11:11:00");
//        LocalDateTime now = LocalDateTime.now();
//
//        System.out.println(parse.getClass().getName());


        LocalDateTime parse1 = LocalDateTime.parse("2018-10-10 10:10:10",f);
        System.out.println(parse1);
    }

    @Test
    public void test9(){
        System.out.println("====");
//        System.out.println(TambooConfig.CALC_MASTER_IP);

    }

    @Test
    public void test10(){
        String rule =  "{\"rule\":\"A.B.C\",\"value\":\"alibabaAndTencent\"}";
        JSONObject ruleJson = JSONObject.parseObject(rule);

        String data = "{\"A\":{\"B\":{\"C\":\"alibabaAndTencent\"}}}";

        ContainsFilter cf = new ContainsFilter();
        try{
            cf.init(ruleJson);
            JSONObject filter = cf.filter(JSONObject.parseObject(data));
            System.out.println(filter);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void test11() throws ClassNotFoundException {
        Class.forName("com.unistack.tamboo.commons.utils.TambooConfig");
        RedisUtil ru = new RedisUtil(EDatabaseType.SPARK);

//        String value = "{\"template\":{\"streamTables\":[{\"streamTable\":\"yh6_server\",\"alias\":\"yh6_alias\",\"topicName\":\"yh6\"}],\"datasources\":[],\"windowLen\":2,\"windowSlide\":1,\"group\":[\"admin\"],\"sql\":\"select * from yh5_alias a inner join yh6_alias b on a.yh5_id = b.yh6_id\"},\"type\":14,\"appName\":\"calc_zc3_admin_14_20180803103227\",\"queue\":\"root.admin\",\"kafka\":{\"acl_username\":\"Tu_adhpoc5\",\"from_topic\":\"yh5\",\"alias\":\"yh5_alias\",\"to_topic\":\"admin-383-vf-1533263546967-E1\",\"group_id\":\"a\",\"acl_password\":\"Tp_nbrvwwp\"}}";
//
//        String calc_test1 = ru.getResource().set("calc_zc3_admin_14_20180803103227", value);
//        System.out.println(calc_test1);


        Jedis jedis = ru.getResource();
        String s = jedis.get("calc_source_hy_test_14_20180804133820");
        System.out.println(s);
        jedis.close();


    }

}