package com.unistack.tamboo.sa.dd.file;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.sa.dd.SinkWorker;
import com.unistack.tamboo.sa.dd.util.DdUtil;
import org.apache.commons.io.FileUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author anning
 * @date 2018/6/12 上午9:49
 * @description: 数据下发到文件
 */
public class ConsumerFileWorker implements SinkWorker {

    @Override
    public JSONObject checkConfig(JSONObject json) {


        return DdUtil.succeedResult("");
    }

    public JSONObject insertInto(String connectName,JSONObject args, List<ConsumerRecord<String, String>> list) {
        JSONObject insertResult;
        List<String> insertData = new ArrayList<>();
        try {
            String fileName = args.getString("file");
            for (Iterator<ConsumerRecord<String, String>> it = list.iterator();it.hasNext();){
                ConsumerRecord<String, String> next = it.next();
                insertData.add(next.value());
            }

            FileUtils.writeLines(new File(fileName),insertData,true);
            insertResult = DdUtil.succeedResult("");
        } catch (IOException e) {
            insertResult = DdUtil.failResult("写入文件失败 ======>"+e.toString());
        }
        return insertResult;
    }
}
