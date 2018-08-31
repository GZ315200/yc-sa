package com.unistack.tamboo.compute.process.until.dataclean.util;

import com.unistack.tamboo.compute.exception.SqlMatchException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SqlUtil {
    private static Pattern PATTERN = Pattern.compile("@([\\.\\w]+)\\b");
    /**
     * 解析模糊的SQL 将其转换为预编译sql和 "参数"即目标值得节点<br/>
     * eg:有如下sql<br/>
     *  select SENDER,RECEIVER FROM USER WHERE ID = @META.ID <br/>
     *  最终要的结果是一个数组，数组有两项 <br/>
     *  第一项是预编译的sql: select SENDER,RECEIVER FROM USER WHERE ID = ? <br/>
     *  第二项是目标值得节点: [META.ID] 是一个List<br/>
     *  这样做是为了解耦，因为此时并不知道ID的值,只知道这个ID对应的值是META.ID,这个值可能从一个<br/>
     *  xml中获取也有可能从一个json中获取。
     * @param vagueSql
     *      模糊sql，类似于 select SENDER,RECEIVER FROM USER WHERE ID = @META.ID <br/>
     *      vagueSql的写法规定: 引用的值必须是 @level1.level2.level3
     * @return
     * 返回数组的第一项是一个字符串 <br/>
     * 第二项是一个 List<String> 里面存放参数值的目标节点<br/>
     */
    public Object[] ParseSql(String vagueSql) throws SqlMatchException {
        //模糊的SQL类似于注释中的update语句，其中的参数来源于另外一个xml文件
        Matcher M = PATTERN.matcher(vagueSql);

        List<String> nodeName = new ArrayList<>();
        boolean isMatch = false;
        while(M.find()){
            nodeName.add(M.group(1));
            isMatch = true;
        }

        if(!isMatch){
            throw new SqlMatchException("模糊SQL:["+vagueSql+"]书写不规范,无法解析!");
        }

        String realSql = vagueSql;
        for(String nodeItem : nodeName){
            realSql = realSql.replace(nodeItem.trim(),"?");
        }

        Object[] result = new Object[2];
        result[0] = realSql;
        result[1] = nodeName;
        return result;
    }

}