package com.unistack.tamboo.mgt.dao.dataFlow;


import com.unistack.tamboo.mgt.dao.BaseDao;
import com.unistack.tamboo.mgt.model.CalcInfo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;



public interface CalcDao extends BaseDao<CalcInfo,Long> {
//    CalcInfo sav(CalcInfo calcInfo);

    @Modifying
    @Query("select c from CalcInfo c where c.wfId = :wfId ")
    ArrayList<CalcInfo> queryByWfId(@Param("wfId") int wfId);

    @Transactional
    @Modifying
    @Query("update CalcInfo  c set c.flag=:flag where c.wfId=:wf_id")
    void updateByWfId(@Param("wf_id")int wf_id,@Param("flag")int flag);
//
//    @Query("select * from CalcInfo  ")
//    ArrayList<CalcInfo> stopByWfId(int wf_id);

    @Query("select count(c) from CalcInfo c where c.wfId=:wf_id")
    int countByWfId(@Param("wf_id")int wf_id);

    @Transactional
    @Modifying
    @Query("update CalcInfo  c set c.flag=-1 where c.wfId=:wfId ")
    int deleteByWfId( @Param("wfId")int wfId);



    @Query("select c from CalcInfo  c where c.calcId=:calcId ")
    CalcInfo findByCalcId(@Param("calcId")String calcId);

    @Transactional
    @Modifying
    @Query("update CalcInfo  c set c.flag=:flag where c.dataWf=:dataWf ")
    void updateByDataWf(@Param("dataWf")int dataWf,@Param("flag") int flag);


    @Modifying
    @Query("select c from CalcInfo  c  where c.flag=1 ")
    ArrayList<CalcInfo> getAllRunningCalcs();

    @Modifying
    @Query("update CalcInfo  c set c.flag=:flag where c.calcId=:appname  ")
    void updateCalcStatusByAppName(@Param("appname")String appname,@Param("flag") int flag);

    @Query("select c from CalcInfo  c where c.dataWf=:dataWf ")
    CalcInfo queryByDataWf(@Param("dataWf")int dataWf);

//    int delByWfId(int wf_id);

}
