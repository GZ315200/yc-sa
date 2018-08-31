package com.unistack.tamboo.mgt.service.dataFlow;

import com.unistack.tamboo.mgt.model.CalcInfo;
import com.unistack.tamboo.mgt.dao.dataFlow.CalcDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CalcServiceImpl implements CalcService {

    @Autowired
    private CalcDao calcDao;

    @Override
    public CalcInfo add(CalcInfo calcInfo) {
        return calcDao.saveAndFlush(calcInfo);
    }

    @Override
    public ArrayList<CalcInfo> queryByWfId(int wf_id) {
        return calcDao.queryByWfId(wf_id);
    }

    @Override
    public ArrayList<CalcInfo> updateByWfId(int wf_id,int flag) {
        calcDao.updateByWfId(wf_id,flag);
        return calcDao.queryByWfId(wf_id);
    }

//    @Override
//    public ArrayList<CalcInfo> stopByWfId(int wf_id) {
//        return calcDao.queryByWfId(wf_id);
//    }

    @Override
    public int countByWfId(int wf_id) {
        return calcDao.countByWfId(wf_id);
    }

    @Override
    public int delByWfId(int wf_id) {
        return calcDao.deleteByWfId(wf_id);
    }

    @Override
    public ArrayList<CalcInfo> queryAndUpdataByCalcId(String appName, boolean status) {
        CalcInfo calcInfo=calcDao.findByCalcId(appName);
        calcDao.updateByDataWf(calcInfo.getDataWf(),status?1:-1);
        ArrayList<CalcInfo> calcInfos=calcDao.queryByWfId(calcInfo.getWfId());
        return calcInfos;
    }

    @Override
    public ArrayList<CalcInfo> getAllRunningCalcs() {
        return calcDao.getAllRunningCalcs();
    }

    @Override
    public ArrayList<String> getAllRunningCalcAppnames() {
        ArrayList<String> res=new ArrayList<>();
        getAllRunningCalcs().forEach(calcInfo -> res.add(calcInfo.getCalcId()));
        return res;
    }

    @Override
    public void updateCalcStatusByAppName(String appname,int flag) {
        calcDao.updateCalcStatusByAppName(appname,flag);
    }

    @Override
    public CalcInfo queryByDataWf(int data_wf) {
        return calcDao.queryByDataWf(data_wf);
    }

    @Override
    public CalcInfo updateByDataWf(int data_wf, int type) {
        calcDao.updateByDataWf(data_wf,type);
        return calcDao.queryByDataWf(data_wf);
    }


}
