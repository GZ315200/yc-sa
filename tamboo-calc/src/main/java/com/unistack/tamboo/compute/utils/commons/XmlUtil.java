package com.unistack.tamboo.compute.utils.commons;

import com.unistack.tamboo.compute.exception.DataFormatErrorException;
import com.alibaba.fastjson.JSONObject;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.dom.DOMElement;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author hero.li
 */
public class XmlUtil implements java.io.Serializable{
    private static Logger LOGGER = LoggerFactory.getLogger(XmlUtil.class);
    private  Convert convert = new Convert();
    private static  SAXReader SAX_READER = new SAXReader();
    private static   Pattern PATTERN = Pattern.compile("-");
    /**
     * 这个方法负责xml格式的文本字符串转换为JSONObject对象<br/>
     * 如果用户需要字符串类型的返回值则可以通过调用该返回值的toString()方法获取<br/>
     * @param xmlStr  xml格式的字符串<br/>
     * @return  xmlStr对应的JSONObject对象 <br/>
     *
     * @throws DataFormatErrorException <br/>
     *      出现这个异常就意味着xmlStr不是标准的xml格式
     */
    public JSONObject xml2Json(String xmlStr) throws DataFormatErrorException {
        if(null == xmlStr || "".equals(xmlStr)){
            throw new IllegalArgumentException("参数为空");
        }

        Document doc = getDocFromXmlStr(xmlStr);
        Element root = doc.getRootElement();
        JSONObject oo = getJSONObjectFromElement(root);
        return oo;
    }

    /**
     * 递归获取Element元素对应的JSONObject,此方法对外隐藏，只供内部使用<br/>
     * @param e  Element元素实例 <br/>
     * @return    e 对应的JSONObject实例 <br/>
     */
    private JSONObject getJSONObjectFromElement(Element e){
        if(e.isTextOnly()){
            JSONObject o = new JSONObject();
            o.put(e.getName(),e.getData());
        }

        JSONObject result = new JSONObject();
        Iterator<Element> itr = e.elementIterator();
        while(itr.hasNext()){
            Element e1 = itr.next();
            boolean isLeaf = e1.isTextOnly();
            if(isLeaf){
                result.put(e1.getName(),e1.getData());
            }else{
                JSONObject o1 = getJSONObjectFromElement(e1);
                result.put(e1.getName(),o1);
            }
        }
        return result;
    }

    /**
     * 如果某个节点不存在创建该节点并赋值 or 如果某节点存在就该给节点赋值<br/>
     * 给targetRoot这个元素的 nodeName 节点赋值为Value<br/>
     * 但是有些值在originRoot的nodeName节点上<br/>
     *
     * @param targetRoot 目标节点，也就是说会把创建的节点插入这个元素中<br/>
     * @param nodeName   节点的名例如 MATE.SEDR<br/>
     * @param value      要设置的值,具体真实的值例如 20170110 <br/>
     */
    public boolean elementCreateAndSet(Element targetRoot,String nodeName,String value){
        if(null == targetRoot || null == nodeName || "".equals(nodeName)){
            LOGGER.info("参数错误");
            return false;
        }

        try{
            String[] nodesName = nodeName.split("\\.");
            Element outter = targetRoot;
            Element current = targetRoot;
            for(int i=0;i<nodesName.length;i++){
                String nodeItem = nodesName[i];
                Element e = current.element(nodeItem);
                if(null == e){
                    //进入这一步是因为没有这个节点,下面会创建这个节点
                    Element e1 = new DOMElement(nodeItem);
                    if(i == 0){
                        outter = e1;
                        targetRoot.add(outter);
                    }else if(i == nodesName.length - 1){
                        //能到这儿说明已经是叶子节点了,所以需要赋值
                        current.add(e1);
                        setValue(e1,value);
                    }else{
                        current.add(e1);
                        outter = e1;
                    }
                    current = e1;
                }else{
                    if(i == nodesName.length - 1) {
                        setValue(e,value);
                    }
                    current = e;
                }
            }
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    
    /**
     * 给元素e赋值为Value'，value'可能是value也可能通过别的方式计算出来 <br/>
     * value' 具体的计算逻辑是:<br/>
     * 1、value是 C:MU  则Value就代表常量MU，直接赋值即可
     * 2、Value是 K:MU  则Value代表的值从paramJson，这个扁平化的json中获取
     * 3、value是 FORMAT_DATA()/FORMAT_DATA(_,yyyyMMdd,yyyy-MM-dd) 则代表Convert类的一个方法
     *    通过反射的方式获取值，注意其参数中的_ ,代表是当前节点的值
     * @param e
     * @param value
     */
    private boolean setValue(Element e,String value){
        if(null == e ){
            LOGGER.info("设置值失败,原因:参数为空！");
            return false;
        }

        if(null == value || "".equals(value)){
            e.setText(String.valueOf(value));
            return true;
        }

        if(value.length() <=2){
            LOGGER.info("节点["+e.getName()+"]对应的值["+value+"]格式书写错误,无法赋值");
            return false;
        }

        e.setText(value);
        return true;
    }


    /**
     * 获取某个节点的值
     * @param value   值
     *                 如C:AA  常量AA
     *                 K:AA    键AA
     *                 NOW()   函数
     *
     * @param first
     *          原始数据的参数即paramJson
     * @return
     */
    public Optional<String> getValue(String value,JSONObject first,JSONObject second){
        if(null == value || "".equals(value)) {
            return Optional.empty();
        }

        //常量的情况
        if(value.startsWith("C:")) {
            return Optional.of(value.substring(2));
        }

        //值为键的形式,先从first中查找在从second查找
        if(value.startsWith("K:")){
            String k = value.substring(2);
            String v = (null == first.getString(k) && null != second) ? second.getString(k) : first.getString(k);
            return Optional.ofNullable(v);
        }

        /**
         * []拼接形式
         */
        if(value.startsWith("[") && value.endsWith("]")){
            String newValaue = value.substring(1,value.length()-1);
            List<String> paramItems = new ArrayList<>();

            int preIndex = 0;
            Matcher matcher = PATTERN.matcher(newValaue);
            while(matcher.find()){
                int start = matcher.start();
                if(start < newValaue.length()-1 && start !=0){
                    String v = newValaue.substring(start-1,start+2);
                    if("y-M".equalsIgnoreCase(v) || "M-d".equalsIgnoreCase(v)){
                        continue;
                    }
                }
                String v = newValaue.substring(preIndex,start);
                paramItems.add(v);
                preIndex = start+1;
            }
            paramItems.add(newValaue.substring(preIndex,newValaue.length()));

            StringBuilder Value = new StringBuilder();
            for(int i=0;i<paramItems.size();i++){
                String item = paramItems.get(i);
                Optional<String> v = getValue(item,first,second);
                if(v.isPresent()){
                    Value.append(v.get()).append("-");
                }else{
                    LOGGER.info("参数["+item+"]没有找到对应的值paramJson="+first);
                    //在拼接模式下，如果某个值没有就给一个提示
                    Value.append("[第").append(String.valueOf(i+1)).append("个值未知]-");
                }
            }
            Value.deleteCharAt(Value.toString().length()-1);
            return Optional.of(Value.toString());
        }

        int start = value.indexOf("(");
        int end = value.indexOf(")");
        if(start == -1 && end == -1) {
            return  Optional.of(value);
        }

        if(start == -1 && end != -1 || start != -1 && end == -1){
            LOGGER.info("值["+value+"]格式书写错误!");
            return Optional.empty();
        }

        String methodName = value.substring(0,start);
        if(start + 1 == end){
            //这是一个没有参数的函数
            try {
                Method method = Convert.class.getDeclaredMethod(methodName);
                Object result = method.invoke(convert);
                return Optional.of(String.valueOf(result));
            }catch(NoSuchMethodException e1){
                e1.printStackTrace();
                LOGGER.error("Convert类没有["+methodName+"]方法");
            }catch(IllegalAccessException e1){
                e1.printStackTrace();
                LOGGER.error("反射访问异常,访问Convert的无参方法["+methodName+"]访问异常,赋值失败",e1);
            }catch(InvocationTargetException e1){
                e1.printStackTrace();
                LOGGER.error("convert的方法无参方法["+methodName+"],调用异常!",e1);
            }
            return Optional.empty();
        }else{
            /**
             * 凡是配置文件中设计到的转换函数都定义在Convert类中,目前只支持参数全部都是字符串的方法。
             * 而且在Convert类中的转换函数都是大写的
             */
            String[] values = value.substring(start + 1, end).split(",");
            List<String> paramList = new ArrayList<>();
            for(String item : values){
                Optional<String> o = getValue(item,first,second);
                if(o.isPresent()) {
                    paramList.add(o.get());
                }else{
                    return Optional.empty();
                }
            }

            Class[] paramType = new Class[paramList.size()];
            Arrays.fill(paramType,String.class);
            try {
                Method method = Convert.class.getDeclaredMethod(methodName,paramType);
                Object result = method.invoke(convert,paramList.toArray());
                return Optional.of(String.valueOf(result));
            }catch(NoSuchMethodException e1){
                e1.printStackTrace();
                LOGGER.error("Convert类没有["+methodName+"]方法");
            }catch(IllegalAccessException e1){
                e1.printStackTrace();
            }catch(InvocationTargetException e1){
                e1.printStackTrace();
                LOGGER.error("convert的方法["+methodName+"],调用异常!",e1);
            }
            return Optional.empty();
        }
    }



    /**
     * 对xml文档做一个扁平化操作,即把所有的叶子节点放在一个新的根节点中返回,不改变原来文档的内容<br/>
     * @param doc
     * @return
     */
    public Document flatXml(Document doc){
        Element root = doc.getRootElement();
        DOMElement target = new DOMElement("root");
        recursionFlatElement(root,target);
        Document newDoc = DocumentHelper.createDocument(target);
        return newDoc;
    }

    private void recursionFlatElement(Element e,Element target){
        if(e.isTextOnly()){
            target.add(e);
        }

        Iterator<Element> itr = e.elementIterator();
        while(itr.hasNext()){
           Element currentEle = itr.next();
           if(currentEle.isTextOnly()){
               target.add((Element)currentEle.clone());
           }else{
               recursionFlatElement(currentEle,target);
           }
        }
    }


    public Document getDocFromXmlStr(String xmlStr) throws DataFormatErrorException {
        try {
            Document doc = SAX_READER.read(new ByteArrayInputStream(xmlStr.getBytes("UTF-8")));
            return doc;
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
            //正常情况下是不应该出现这种情况的
            throw new IllegalArgumentException("字符编码异常!");
        } catch (DocumentException e) {
            e.printStackTrace();
            throw new DataFormatErrorException("字符串不是标准的xml格式!"+xmlStr);
        }
    }

    /**
     * 把一个xml格式的字符串准换为一个Document对象<br/>
     * @param xmlStr
     * @return
     * @throws DataFormatErrorException
     */
    public Document toDocument(String xmlStr) throws DataFormatErrorException{
        return getDocFromXmlStr(xmlStr);
    }


    public Document toDocument(InputStream in) throws DocumentException{
        return SAX_READER.read(in);
    }

}