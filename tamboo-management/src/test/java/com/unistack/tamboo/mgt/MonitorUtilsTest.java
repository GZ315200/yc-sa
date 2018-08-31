package com.unistack.tamboo.mgt;

import com.unistack.tamboo.mgt.utils.MonitorUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Gyges Zean
 * @date 2018/6/14
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class MonitorUtilsTest {


    @Test
    public void findAll() {
        System.out.println(MonitorUtils.getOffsetMonitorAllRecord().toString());
    }
}
