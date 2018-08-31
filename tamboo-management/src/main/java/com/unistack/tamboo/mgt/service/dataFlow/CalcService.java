package com.unistack.tamboo.mgt.service.dataFlow;


import com.unistack.tamboo.mgt.model.CalcInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public interface CalcService {

    CalcInfo add(CalcInfo calcInfo);

    ArrayList<CalcInfo> queryByWfId(int wf_id);

    ArrayList<CalcInfo> updateByWfId(int wf_id,int flag);

//    ArrayList<CalcInfo> stopByWfId(int wf_id);

    int countByWfId(int wf_id);

    int delByWfId(int wf_id);

    ArrayList<CalcInfo> queryAndUpdataByCalcId(String appName,boolean status);

    ArrayList<CalcInfo> getAllRunningCalcs();

    ArrayList<String> getAllRunningCalcAppnames();

    void updateCalcStatusByAppName(String appname,int falg);

    CalcInfo queryByDataWf(int data_wf);

    CalcInfo updateByDataWf(int data_wf, int type);
}
