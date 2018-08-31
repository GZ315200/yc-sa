package com.unistack.tamboo.mgt.service.dataFlow;

import com.unistack.tamboo.mgt.dao.collect.DataFlowInfoDao;
import com.unistack.tamboo.mgt.model.CalcInfo;
import com.unistack.tamboo.mgt.model.collect.DataFlowInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class DataFlowServiceImpl implements DataFlowService{

    @Autowired
    DataFlowInfoDao dataFlowInfoDao;

    @Autowired
    CalcService calcService;

    @Override
    public DataFlowInfo add(DataFlowInfo dataFlowInfo) {

        return dataFlowInfoDao.save(dataFlowInfo);
    }

    @Override
    @Transactional
    public int delete(int wf_id) {

        return dataFlowInfoDao.deleteByWfId(wf_id);
    }

    @Override
    public ArrayList<DataFlowInfo> listByUser_id(int userId) {
        return dataFlowInfoDao.getAllByUserId(userId);
    }

    @Override
    public DataFlowInfo queryByWfId(int wf_id) {
        return dataFlowInfoDao.queryByWfId(wf_id);
    }

    @Override
    public Object[] countServices() {
        return dataFlowInfoDao.countServices();
    }

    @Override
    @Transactional
    public boolean changeCalcStatus(String appName,boolean status) {
        ArrayList<CalcInfo> calcInfos=calcService.queryAndUpdataByCalcId(appName,status);
        int wf_id=0;
        boolean flag=false;
        if(status){
            for (CalcInfo calcinfo:calcInfos) {
                wf_id=calcinfo.getWfId();
               if(calcinfo.getFlag()==-1){
                   flag=false;
                    break;
               }
               flag=true;
            }
        }
        dataFlowInfoDao.updataStatus(wf_id,flag?1:-1);

        return true;
    }

    @Override
    public Page<DataFlowInfo> listByUser_id(int userId, String query, Pageable pageable) {
        return dataFlowInfoDao.getAllByUserId(userId,query, pageable);
    }

    @Override
    @Transactional
    public void changeDataFlowStatus(int wfId, int flag){
        dataFlowInfoDao.updataStatus(wfId,flag);
    }

    @Override
    public void upDataConf(int wfId, String conf) {
        dataFlowInfoDao.updataConf(wfId,conf);
    }


}
