package com.unistack.tamboo.mgt.model.calc;

import com.alibaba.fastjson.JSONObject;
import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "data_filter", schema = "sa-mgt", catalog = "")
public class DataFilter {
    private int filterId;
    private String filterName;
    private String filterType;
    private String filterDesc;
    private String paramConf;
    private String paramDesc;
    private String filterEg;
    private String interacteParamConf;
    private Timestamp filterAddTime;
    private String isActive;
    private String remark1;
    private String remark2;

    @GeneratedValue(strategy=GenerationType.AUTO)
    @Id
    @Column(name = "filter_id")
    public int getFilterId() {
        return filterId;
    }

    public void setFilterId(int filterId) {
        this.filterId = filterId;
    }

    @Column(name = "filter_name")
    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    @Column(name = "filter_type")
    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    @Column(name="filter_desc")
    public String getFilterDesc() {
        return filterDesc;
    }

    public void setFilterDesc(String filterDesc) {
        this.filterDesc = filterDesc;
    }

    @Column(name="param_conf")
    public String getParamConf() {
        return paramConf;
    }

    public void setParamConf(String paramConf) {
        this.paramConf = paramConf;
    }

    @Column(name="param_desc")
    public String getParamDesc() {
        return paramDesc;
    }

    public void setParamDesc(String paramDesc) {
        this.paramDesc = paramDesc;
    }

    @Column(name="filter_eg")
    public String getFilterEg() {
        return filterEg;
    }

    public void setFilterEg(String filterEg) {
        this.filterEg = filterEg;
    }

    @Column(name="interacte_param_conf")
    public String getInteracteParamConf() {
        return interacteParamConf;
    }

    public void setInteracteParamConf(String interacteParamConf) {
        this.interacteParamConf = interacteParamConf;
    }

    @Column(name="filter_add_time")
    public Timestamp getFilterAddTime() {
        return filterAddTime;
    }

    public void setFilterAddTime(Timestamp filterAddTime) {
        this.filterAddTime = filterAddTime;
    }

    @Column(name="isActive")
    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    @Column(name="remark1")
    public String getRemark1() {
        return remark1;
    }

    public void setRemark1(String remark1) {
        this.remark1 = remark1;
    }

    @Column(name="remark2")
    public String getRemark2() {
        return remark2;
    }

    public void setRemark2(String remark2) {
        this.remark2 = remark2;
    }


    public JSONObject toJson(){
        JSONObject result = new JSONObject();
        result.put("filterId",filterId);
        result.put("filterName",filterName);
        result.put("filterType",filterType);
        result.put("filterDesc",filterDesc);
        result.put("paramConf",paramConf);
        result.put("paramDesc",paramDesc);
        result.put("filterEg",filterEg);
        result.put("interacteParamConf",interacteParamConf);
        result.put("filterAddTime",filterAddTime);
        result.put("remark1",remark1);
        return result;
    }

}
