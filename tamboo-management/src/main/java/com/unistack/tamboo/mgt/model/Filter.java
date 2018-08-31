package com.unistack.tamboo.mgt.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Filter {
    private static  SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private int filter_id;
    private String filter_name;
    private String filter_type;
    private String filter_desc;
    private String param_conf;
    private String param_desc;
    private String filter_eg;
    private String interacte_param_conf;
    private Date filter_add_time;
    private String isActive;
    private String remark1;
    private String remark2;

    public int getFilter_id() {
        return filter_id;
    }

    public void setFilter_id(int filter_id) {
        this.filter_id = filter_id;
    }

    public String getFilter_name() {
        return filter_name;
    }

    public void setFilter_name(String filter_name) {
        this.filter_name = filter_name;
    }

    public String getFilter_type() {
        return filter_type;
    }

    public void setFilter_type(String filter_type) {
        this.filter_type = filter_type;
    }

    public String getFilter_desc() {
        return filter_desc;
    }

    public void setFilter_desc(String filter_desc) {
        this.filter_desc = filter_desc;
    }

    public String getParam_conf() {
        return param_conf;
    }

    public void setParam_conf(String param_conf) {
        this.param_conf = param_conf;
    }

    public String getParam_desc() {
        return param_desc;
    }

    public void setParam_desc(String param_desc) {
        this.param_desc = param_desc;
    }

    public String getFilter_eg() {
        return filter_eg;
    }

    public void setFilter_eg(String filter_eg) {
        this.filter_eg = filter_eg;
    }

    public String getInteracte_param_conf() {
        return interacte_param_conf;
    }

    public void setInteracte_param_conf(String interacte_param_conf) {
        this.interacte_param_conf = interacte_param_conf;
    }

    public Date getFilter_add_time() {
        return filter_add_time;
    }

    public void setFilter_add_time(Date filter_add_time) {
        this.filter_add_time = filter_add_time;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getRemark1() {
        return remark1;
    }

    public void setRemark1(String remark1) {
        this.remark1 = remark1;
    }

    public String getRemark2() {
        return remark2;
    }

    public void setRemark2(String remark2) {
        this.remark2 = remark2;
    }

    public String toInsertsql(){
        return "insert into filter (filter_name,filter_type,filter_desc,param_conf,param_desc,filter_eg,interacte_param_conf,filter_add_time,isActive,remark1,remark2) values('"+filter_name+"','"+filter_type+"','"+filter_desc+"','"+param_conf+"','"+param_desc+"','"+filter_eg+"','"+interacte_param_conf+"','"+df.format(filter_add_time)+"','"+isActive+"','"+remark1+"','"+remark2+"')";
    }

    private static  String convert(String col){
        if(null == col)
            return "";
        if(col.indexOf("'") != -1){
            String result = col.replace("'","\\'");
            return result;
        }
        return col;
//        return col.contains("'") ? col.replace("'","\\'") : col;
    }

    public static void main(String[] args) {
        String xx = "{\"type\":\"Eval\",\"fields\":[{\"field\":\"new_calc\",\"expr\":\"var ipl=0;ip.split('.').forEach(function(octet){ipl<<=8;ipl+=parseInt(octet);});(ipl >>>0)\"}]}";
        String x = convert(xx);
        System.out.println(x);
    }

    public String toUpdateSql(){
        return "update filter set filter_name='"+convert(filter_name)+"',filter_type='"+convert(filter_type)+"',filter_desc='"+convert(filter_desc)+"',param_conf='"+convert(param_conf)+"',param_desc='"+convert(param_desc)+"',filter_eg='"+convert(filter_eg)+"',interacte_param_conf='"+(interacte_param_conf)+"',filter_add_time='"+df.format(filter_add_time)+"',isActive='"+isActive+"',remark1='"+convert(remark1)+"',remark2='"+convert(remark2)+"' where filter_id="+filter_id ;
    }

    public String toDeleteSql(){
        return "delete form filter where filter_id='"+filter_id+"'";
    }

    public static SimpleDateFormat getDf() {
        return df;
    }

    @Override
    public String toString() {
        return "Filter{" +
                "filter_id=" + filter_id +
                ", filter_name='" + filter_name + '\'' +
                ", filter_type='" + filter_type + '\'' +
                ", filter_desc='" + filter_desc + '\'' +
                ", param_conf='" + param_conf + '\'' +
                ", param_desc='" + param_desc + '\'' +
                ", filter_eg='" + filter_eg + '\'' +
                ", interacte_param_conf='" + interacte_param_conf + '\'' +
                ", filter_add_time=" + filter_add_time +
                ", isActive='" + isActive + '\'' +
                ", remark1='" + remark1 + '\'' +
                ", remark2='" + remark2 + '\'' +
                '}';
    }
//    public static void main(String[] args) {
//        Filter f= new Filter();
//        f.setFilter_add_time(new Date());
//        f.setFilter_desc("filter_desc");
//        f.setFilter_eg("filter_eg");
//        f.setFilter_id(123);
//        f.setFilter_name("myFilter");
//        f.setFilter_type("myType");
//        f.setInteracte_param_conf("interacte_mesg");
//        f.setIsActive("1");
//        f.setParam_conf("参数");
//        f.setParam_desc("参数描述");
//        f.setRemark1("预留1");
//        f.setRemark2("预留2");
//
//        System.out.println(f.toSql());
//    }
}
