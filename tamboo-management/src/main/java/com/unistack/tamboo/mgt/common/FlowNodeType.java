package com.unistack.tamboo.mgt.common;

import java.util.HashMap;
import java.util.HashSet;

/**
 * @author hero.li
 * 节点规则验证
 */
public class FlowNodeType {
    public static HashMap<Integer,NODETPYE> nodeModes=new HashMap<>();
    public static HashMap<String,NODETPYE> nodeTypes=new HashMap<>();


    public enum NODETPYE{
        DATASOURCE(0,"0"),
        CALC_CLEAN(1,"11"),
        CALC_JOIN(1,"12"),
        CALC_SPLIT(1,"13"),
        CALC_SQL(1,"14"),
        MQ_KAFKA(2,"21"),
        DIST(3,"31");

        /**
         * 大类型 数据源，计算，下发
         */
        private int mode;
        /**
         * 小类型 eg:SQL join clean  split
         */
        private String type;

        public String getType(){
            return type;
        }

        public int getMode(){
            return mode;
        }

        public void setMode(int mode) {
            this.mode = mode;
        }

        public void setType(String type) {

            this.type = type;
        }

        NODETPYE(int mode,String type) {
            this.mode=mode;
            this.type=type;
        }
    }

    private static HashMap<Integer,HashSet<Integer>> nodeFlow=new HashMap<Integer, HashSet<Integer>>();

    static {

        HashSet<Integer> datasource=new HashSet<>();
        datasource.add(1);
        nodeFlow.put(0,datasource);

        HashSet<Integer> calc=new HashSet<>();
        calc.add(1);
        calc.add(2);
        calc.add(3);
        nodeFlow.put(1,calc);

        HashSet<Integer> mq=new HashSet<>();
        mq.add(1);
        mq.add(3);
        nodeFlow.put(2,mq);

        HashSet<Integer> dist=new HashSet<>();
        nodeFlow.put(3,dist);


        for(NODETPYE nodetpye:NODETPYE.values()){
            nodeModes.put(nodetpye.getMode(),nodetpye);
        }

        for(NODETPYE nodetpye:NODETPYE.values()){
            nodeTypes.put(nodetpye.getType(),nodetpye);
        }


    }


    public static boolean checkNode(NODETPYE node,NODETPYE next_node){
        if(nodeFlow.get(node.getMode()).contains(next_node.getMode())){
            return true;
        }
        return false;
    }
}
