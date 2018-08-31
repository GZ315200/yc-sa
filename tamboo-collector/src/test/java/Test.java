import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.sa.dc.flume.common.DcConfig;
import com.unistack.tamboo.sa.dc.flume.common.DcType;
import com.unistack.tamboo.sa.dc.flume.invoking.DcInvoking;


public class Test {


    public static void main(String[] args) {

        DcConfig dcConfig=new DcConfig();

        dcConfig.setDcType(DcType.WEBSERVICE);
        dcConfig.setConf(JSONObject.parseObject("{\"a\":\"b\"}"));


        DcInvoking invoking=new DcInvoking();
        System.out.println(invoking.createDataCollector(dcConfig));
    }
}
