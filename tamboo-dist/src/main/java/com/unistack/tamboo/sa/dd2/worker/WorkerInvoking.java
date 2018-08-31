package com.unistack.tamboo.sa.dd2.worker;

import com.unistack.tamboo.sa.dd2.KafkaSinkWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author anning
 * @date 2018/7/20 下午5:38
 * @description: woker invoking
 */
public class WorkerInvoking {
    private static  Logger logger = LoggerFactory.getLogger(WorkerInvoking.class);
    private static  Map<String, Supplier<KafkaSinkWorker>> workerMap = new HashMap();

    public WorkerInvoking() {
    }

    public static KafkaSinkWorker getSinkWorkerByTypeName(String name) {
        Supplier<KafkaSinkWorker> sinkWorkerSupplier = workerMap.get(name.toLowerCase());
        if (sinkWorkerSupplier != null) {
            return sinkWorkerSupplier.get();
        } else {
            logger.error("===========> no such SinkWorker for " + name);
            throw new IllegalArgumentException("no such SinkWorker for " + name);
        }
    }

    static {
        workerMap.put("mysql", JdbcWorker::new);
        workerMap.put("oracle", JdbcWorker::new);
        workerMap.put("sqlserver", JdbcWorker::new);
        workerMap.put("postgre", JdbcWorker::new);
        workerMap.put("file", FileWorker::new);
        workerMap.put("kafka", KafkaWorker::new);
        workerMap.put("activemq", ActiveMqWorker::new);
        workerMap.put("elasticsearch", ElasticsearchWorker::new);
        workerMap.put("hdfs", HdfsWorker::new);
    }
}
