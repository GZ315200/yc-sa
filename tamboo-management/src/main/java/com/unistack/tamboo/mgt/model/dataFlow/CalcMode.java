package com.unistack.tamboo.mgt.model.dataFlow;

public class CalcMode {

//    filter类型: {"type":"clean","template":{"fields":[{"type":"Rename","fields":[{"old":"A1","new":"A"}]},{"type":"Remove","fields":[{"removeFiled":"C"}]}]},"kafka":{"kafka.from_topic":"t1","kafka.to_topic":"t2","kafka.group_id":"test203","kafka.batchInterval":"1000","kafka.auto_offset_reset":"earliest","kafka.spark_streaming_kafka_maxRatePerPartition":"3000"}}
//    calc类型:   {"type":"calc","template":{"messageFilter":"ELDT,ETOT","source":{"dbSource":{"password":"welcome1","url":"jdbc:mysql://192.168.1.191:3308/tamboo_manager","username":"root"}},"calcs":{"calc":{"operate":{"array":{"FLOP.ERUT.AOBT":"K:PLAN_DEPART_TM_UTC","FLOP.ERUT.FLID":"K:FLIGHT_ID","FLOP.ERUT.ACAT":"K:PLAN_ARRIVAL_TM_UTC","FLOP.ERUT.ATOT":"K:PLAN_DEPART_TM_UTC","FLOP.ERUT.AIBT":"K:PLAN_ARRIVAL_TM_UTC","FLOP.ERUT.FFID":"[K:CARRIER_CD-K:FLIGHT_NO-C:A-FORMAT_DATE(K:PLAN_ARRIVAL_TM_UTC,yyyy-MM-dd HH:mm:ss,yyyyMMdd)-C:D]","FLOP.ERUT.DAPCD":"K:ACTUAL_DEPART_AIRPORT_CD3","FLOP.ERUT.RTNO":"K:FLIGHT_NO","FLOP.ERUT.AAPCD":"K:ACTUAL_ARRIVAL_AIRPORT_CD3"},"assemble":{"META.SEQN":"NEXT_VAL(10)","FLOP.RENO":"K:TAIL_NO","META.DTTM":"NOW(yyyyMMddHHmmss)","META.STYP":"K:MessageType","META.SNDR":"C:LYH","META.TYPE":"C:FLOP"}},"join":{"updateSql":"update ceairtest set plan_depart_tm_utc='@ETOT'  where flight_id = '@AocId'","resultSql":"select * from ceairtest where tail_no in (select tail_no from ceairtest where flight_id ='@AocId') and length(plan_arrival_tm_utc) > 12 and length(plan_depart_tm_utc) > 12"}}}},"kafka":{"kafka.from_topic":"source","kafka.to_topic":"target","kafka.group_id":"test202","kafka.batchInterval":"1000","kafka.auto_offset_reset":"earliest","kafka.spark_streaming_kafka_maxRatePerPartition":"3000"}}
    private int type;
    private String template;
    private String kafka;
    private String appName;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getKafka() {
        return kafka;
    }

    public void setKafka(String kafka) {
        this.kafka = kafka;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
