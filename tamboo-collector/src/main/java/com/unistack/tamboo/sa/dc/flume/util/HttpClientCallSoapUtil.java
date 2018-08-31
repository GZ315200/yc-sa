package com.unistack.tamboo.sa.dc.flume.util;



import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.Iterator;

public class HttpClientCallSoapUtil {
    private static int socketTimeout = 30000;// 请求超时时间
    private static int connectTimeout = 30000;// 传输超时时间
    private static  Logger logger = LoggerFactory.getLogger(HttpClientCallSoapUtil.class);

    /**
     * 使用SOAP1.1发送消息
     * @param postUrl
     * @param soapXml
     * @param soapAction
     * @return
     */
    public  static String web(String postUrl,String soapXml,String soapAction){
        String retStr = "";
        String text = "";
        // 创建HttpClientBuilder
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        // HttpClient
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        HttpPost httpPost = new HttpPost(postUrl);
        //  设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout).build();
        httpPost.setConfig(requestConfig);
        try {
            httpPost.setHeader("Content-Type", "text/xml;charset=UTF-8");
            httpPost.setHeader("SOAPAction", soapAction);
            StringEntity data = new StringEntity(soapXml,
                    Charset.forName("UTF-8"));
            httpPost.setEntity(data);
            CloseableHttpResponse response = closeableHttpClient
                    .execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                // 打印响应内容
                retStr = EntityUtils.toString(httpEntity, "UTF-8");
//               logger.info("response:" + retStr);

                XmlUtil xmlUtil = new XmlUtil();
                Document doc = xmlUtil.getDocFromXmlStr(retStr);
                Document flatXml = xmlUtil.flatXml(doc);

                Iterator<Element> itr = flatXml.getRootElement().elementIterator();
                while (itr.hasNext()){
                    Element current = itr.next();
                    text = current.getText();

                }

            }
            // 释放资源
            closeableHttpClient.close();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return text;
    }

    public static void main(String[] args) {
        String addSoapXml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://server/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <ser:add>\n" +
                "         <a>?</a>\n" +
                "         <b>?</b>\n" +
                "      </ser:add>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        String getContentSoapXml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://server/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <ser:getContent/>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
//
        String postUrl = "http://192.168.1.251:8888/myservice";
//        System.out.println(web(postUrl,addSoapXml,"add"));

        System.out.println(web(postUrl,getContentSoapXml,""));

        web(postUrl,getContentSoapXml,"");
//        //数据库content列数据样例
//        String str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><FOISRoot xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><SysInfo>" +
//                "<MessageSequenceID>2045374</MessageSequenceID><ServiceType>FOD</ServiceType><MessageType>CGCT</MessageType>" +
//                "<SendDateTime>2017-12-11 13:27:26</SendDateTime><CreateDateTime>2017-12-11 13:27:26</CreateDateTime>" +
//                "<Receiver>SOA</Receiver><Sender>FOIS</Sender></SysInfo><FlightInfo><PrimaryInfo><Legid>347716279</Legid>" +
//                "<AocId>105513942</AocId><Pdate>2017-12-11</Pdate><Carrier>KN</Carrier>" +
//                "<FlightNo>2959</FlightNo>" +
//                "<DeptAirport>NAY</DeptAirport>" +
//                "<ArrAirport>HLH</ArrAirport>" +
//                "</PrimaryInfo>" +
//                "<FlightDataInfo>" +
//                "<CargoClose>2017-12-11 13:04:00</CargoClose>" +
//                "</FlightDataInfo><" +
//                "/FlightInfo>" +
////                "</FOISRoot>";
    }
}
