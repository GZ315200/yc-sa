package com.unistack.tamboo.mgt;




import com.alibaba.fastjson.JSON;
import com.unistack.tamboo.mgt.helper.TopicHelper;
import com.unistack.tamboo.mgt.service.MqService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Gyges Zean
 * @date 2018/5/28
 */

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class TopicHelperTest {

    private Map<String, Object> map = new HashMap<>();


    @Autowired
    private TopicHelper topicHelper;

    @Autowired
    private MqService mqService;



    @Test
    public void createTopicAndAcl() {
        topicHelper.createTopicAndAcl("an333", 3, (short) 1, "Tu_adhpoc5", "Tp_nbrvwwp");
    }


    @Test
    public void setNumber() {
        mqService.setBytesValue("1".getBytes(),"2".getBytes(),20);
    }


    @Test
    public void getLog() {
        System.out.println(JSON.toJSON(topicHelper.describeLogDir(topicHelper.brokerIds())));
    }
}
