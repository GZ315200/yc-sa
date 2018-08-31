package com.unistack.tamboo.mgt;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.mgt.action.DataDist2Action;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author anning
 * @date 2018/6/6 下午8:09
 * @description:
 */

@SpringBootTest(classes = MgtApplication.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringJUnit4ClassRunner.class)
public class AnTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void test(){
        /*List<MTopicGroupIdR> offsetMonitorAllRecord = MonitorUtils.getConnectorMonnitoring();
        for (MTopicGroupIdR mr:offsetMonitorAllRecord) {
            System.out.println(mr.toString());
        }*/

        JSONObject json = new JSONObject();
        JSONObject jsonObject = new JSONObject().fluentPut("whiteList", "name.agr,addr")
                .fluentPut("table_name", "testargs")
                .fluentPut("primaryKey", "name");
        json.fluentPut("wf_id",507)
                .fluentPut("topic_name","admin-383-testars-20180723122519-b2")
                .fluentPut("kafkaAclPassword","Tp_nbrvwwp")
                .fluentPut("type","MYSQL")
                .fluentPut("table_name","testargs")
                .fluentPut("kafkaAclName","Tu_adhpoc5")
                .fluentPut("dataSourceName","anning_mysql")
                .fluentPut("fileds",jsonObject);
        JSONObject jsonObject1 = DataDist2Action.startKafkaSink(json);
        System.out.println("=================================================");
        System.out.println("================================================>"+jsonObject1);
    }

    /*@Test
    public void testRestart(){
        JSONObject sink = DataDist2Action.startKafkaSink(3);
        System.out.println(sink.toJSONString());
        System.out.println(SinkService.statusMap.get("sink-20180630-164509"));

        JSONObject jsonObject = DataDist2Action.stopKafkaSink(3);
        System.out.println(jsonObject.toJSONString());
        System.out.println(SinkService.statusMap.get("sink-20180630-164509"));
    }*/



    @Test
    public void test2(){
        JSONObject json = new JSONObject();
        JSONObject jsonObject = new JSONObject().fluentPut("whiteList", "name.agr,addr")
                .fluentPut("table_name", "testargs")
                .fluentPut("primaryKey", "name");
        json.fluentPut("wf_id",507)
                .fluentPut("topic_name","admin-383-testars-20180723122519-b2")
                .fluentPut("kafkaAclPassword","Tp_nbrvwwp")
                .fluentPut("type","MYSQL")
                .fluentPut("table_name","testargs")
                .fluentPut("kafkaAclName","Tu_adhpoc5")
                .fluentPut("dataSourceName","anning_mysql")
                .fluentPut("fileds",jsonObject);
        JSONObject jsonObject1 = DataDist2Action.startKafkaSink(json);
        System.out.println(jsonObject1);
    }

    @Test
    public void testSchema(){
        ResponseEntity<JSONObject> forEntity = testRestTemplate.getForEntity("/schemas?pageIndex=1", JSONObject.class);
        System.out.println(forEntity.getBody().toJSONString());
    }

}
