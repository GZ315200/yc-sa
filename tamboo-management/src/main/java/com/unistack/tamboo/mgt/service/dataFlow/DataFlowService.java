package com.unistack.tamboo.mgt.service.dataFlow;


import com.unistack.tamboo.mgt.model.collect.DataFlowInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;

public interface DataFlowService {

    DataFlowInfo add(DataFlowInfo dataFlowInfo) ;

    int delete(int wf_id);

    ArrayList<DataFlowInfo> listByUser_id(int userId);

    DataFlowInfo queryByWfId(int wf_id);

    Object[] countServices();

    boolean changeCalcStatus(String appName,boolean status);

    Page<DataFlowInfo> listByUser_id(int userId, String query, Pageable pageable);

    void changeDataFlowStatus(int wfId, int i);

    void upDataConf(int wfId, String s);

}
