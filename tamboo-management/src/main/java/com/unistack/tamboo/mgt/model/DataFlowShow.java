package com.unistack.tamboo.mgt.model;

//TODO: 用于列表页数据展示，集成部分监控信息
public class DataFlowShow {

   private int wf_id;
   private String wf_name;
   private int rate;
   private int totalRecords;
   private int latency;
   private int workers;
   private int flag;


   public int getWf_id() {
      return wf_id;
   }

   public void setWf_id(int wf_id) {
      this.wf_id = wf_id;
   }

   public String getWf_name() {
      return wf_name;
   }

   public void setWf_name(String wf_name) {
      this.wf_name = wf_name;
   }

   public int getRate() {
      return rate;
   }

   public void setRate(int rate) {
      this.rate = rate;
   }

   public int getTotalRecords() {
      return totalRecords;
   }

   public void setTotalRecords(int totalRecords) {
      this.totalRecords = totalRecords;
   }

   public int getLatency() {
      return latency;
   }

   public void setLatency(int latency) {
      this.latency = latency;
   }

   public int getWorkers() {
      return workers;
   }

   public void setWorkers(int workers) {
      this.workers = workers;
   }

   public int getFlag() {
      return flag;
   }

   public void setFlag(int flag) {
      this.flag = flag;
   }
}
