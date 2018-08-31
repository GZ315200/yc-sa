package com.unistack.tamboo.mgt;

import com.unistack.tamboo.mgt.alert.AlertEngine;
import com.unistack.tamboo.mgt.service.monitor.DataInfoCollectorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import java.text.SimpleDateFormat;
import java.util.Arrays;

/**
 * @author Gyges Zean
 * @date 2018/5/17
 */
@SpringBootApplication(scanBasePackages = "com.unistack.tamboo.mgt")
public class MgtApplication {
    private static  Logger log = LoggerFactory.getLogger(MgtApplication.class);

    public static void main(String[] args) {
        String path = System.getProperty("user.dir") + "/";
        System.setProperty("logging.config", path + "config/log4j2.xml");
        System.setProperty("zookeeper.sasl.client", "disable");
        ApplicationContext context = SpringApplication.run(MgtApplication.class);
        log.info("====================================");
        log.info("当前环境:" + Arrays.toString(context.getEnvironment().getActiveProfiles()));
        log.info("应用名称:" + (context.getApplicationName()));
        log.info("启动时间:" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(context.getStartupDate())));
        startJob(context);
        startAlert(context);
    }

    private static void startAlert(ApplicationContext context) {
        AlertEngine alertEngine = context.getBean(AlertEngine.class);
        alertEngine.rescheduleAlerts();
    }
    private static void startJob(ApplicationContext context) {
        try {
            DataInfoCollectorService dataInfoCollectorService = context.getBean(DataInfoCollectorService.class);
            dataInfoCollectorService.start();
        } catch (Exception e) {
            log.error("", e);
        }
    }
}
