package com.unistack.tamboo.mgt.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.dom.DOMElement;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;


public class JSONUtil implements Serializable{
//    private static Logger LOGGER = LoggerFactory.getLogger(JSONUtil.class);

    /**
     * 这个方法用来转换json到xml格式<br/>
     * 注意点:1、json的值不能是json格式的字符串<br/>
     *       2、此json格式必须只有一个根节点并且名字是root<br/>
     * @param jsonStr json格式的字符串<br/>
     * @return Dom4j中的Document对象,如果想将其转换为xml格式的字符串只需要调用其doc.asXml()方法即可 <br/>
     *
     *   如果传入的jsonStr不是标准的JSON格式的字符串则抛出此异常 <br/>
     */
    public Document json2Xml(String jsonStr)  {
        return json2Xml(jsonStr,"root");
    }

    public Document json2Xml(String jsonStr,String rootName)  {
        if(null == jsonStr || "".equals(jsonStr)){
            throw  new IllegalArgumentException("jsonStr->xml:参数为空");
        }

        JSONObject target;
        try{
            target = JSON.parseObject(jsonStr);
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("字符串["+jsonStr+"]不是json格式");
        }

        Set<String> set = target.keySet();
        if(set.size() == 1){
            Iterator<String> itr = set.iterator();
            rootName = itr.next();
        }

        Element e = getElementFromJsonObject(target,rootName);

        Document root = DocumentHelper.createDocument(e);
        return root;
    }

    /**
     * 把JSONObject转换为对应的Element<br/>
     *
     * @param o JSONObject对象<br/>
     * @param rootName 根节点名字<br/>
     * @return 转换后的Element元素
     */
    private Element getElementFromJsonObject(JSONObject o, String rootName){
        Element result = new DOMElement(rootName);

        Iterator<String> itr = o.keySet().iterator();
        while(itr.hasNext()){
            String key = itr.next();
            try{
                JSONObject o1 = o.getJSONObject(key);
                Element e = getElementFromJsonObject(o1,key);
                result.add(e);
            }catch(Exception e){
                String value = o.getString(key);
                Element item = new DOMElement(key);
                item.setText(value);
                result.add(item);
            }
        }
        return result;
    }


}
