package com.unistack.tamboo.sa.dc.flume.util;

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
import java.util.Iterator;


public class XmlUtil implements java.io.Serializable{
    private static Logger LOGGER = LoggerFactory.getLogger(XmlUtil.class);
    private static  SAXReader SAX_READER = new SAXReader();

//    /**
//     * 通过一个元素的节点名，取出target元素对应的值 <br/>
//     * 如果target元素没有nodeName这个节点则返回 <b>null</b> <br/>
//     * @param nodeName level1.level2.level3
//     * @param target
//     * @return
//     *
//     * 注意空指针,后面优化
//     */
//    public Element getElement(Element target, String nodeName){
//        if(null == nodeName || "".equals(nodeName))
//            return null;
//        String[] nodesName = nodeName.trim().split("\\.");
//        Element e = target;
//        for(String nodeItem : nodesName){
//            e = e.element(nodeItem);
//            if(null == e)
//               return null;
//        }
//        return e;
//    }


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


    public Document getDocFromXmlStr(String xmlStr) {
        try {
            Document doc = SAX_READER.read(new ByteArrayInputStream(xmlStr.getBytes("UTF-8")));
            return doc;
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
            //正常情况下是不应该出现这种情况的
            throw new IllegalArgumentException("字符编码异常!");
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 把一个xml格式的字符串准换为一个Document对象<br/>
     * @param xmlStr
     * @return
     * @throws
     */
    public Document toDocument(String xmlStr) {

        return getDocFromXmlStr(xmlStr);
    }


    public Document toDocument(InputStream in) throws DocumentException {
        return SAX_READER.read(in);
    }

}