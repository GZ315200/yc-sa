package com.unistack.tamboo.mgt.dao.collect;

import com.unistack.tamboo.mgt.dao.BaseDao;
import com.unistack.tamboo.mgt.model.collect.DataFlowInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.ArrayList;
import java.util.List;


public interface DataFlowInfoDao extends BaseDao<DataFlowInfo, Integer> {

    List<DataFlowInfo> getDataFlowInfosByUserIdIn(List<Integer> ids);

//    int delete(int wf_id);

//    DataFlowInfo insert(DataFlowInfo dataFlowInfo);

    //    @Query("select * from DataFlowInfo d where d.userId=:userId")
    ArrayList<DataFlowInfo> getAllByUserId(int userId);

    @Query(value="select d from DataFlowInfo d where d.wfId=:wf_id ")
    DataFlowInfo queryByWfId(@Param("wf_id")int wf_id);

//    platInfo.setCountWf(Long.valueOf(String.valueOf(dataFlowMap[0])).intValue());
//        platInfo.setRunning(Long.valueOf(String.valueOf(dataFlowMap[1])).intValue());
//        platInfo.setUnrunning(Long.valueOf(String.valueOf(dataFlowMap[2])).intValue());
//        platInfo.setWarning(Long.valueOf(String.valueOf(dataFlowMap[3])).intValue());

    @Query("select count(*) as countWf,sum(case d.flag when 1 then 1 else 0 end ) as running,sum(case d.flag when 0 then 1 else 0 end ) as warning,sum(case d.flag when -1 then 1 else 0 end ) as unrunning from DataFlowInfo d")
    Object[] countServices();

    @Modifying
    @Query("delete from DataFlowInfo d where d.wfId=:wf_id ")
    int deleteByWfId(@Param("wf_id")int wf_id);


    @Modifying
    @Query("update DataFlowInfo  d set d.flag=:flag where d.wfId=:wfId ")
    void updataStatus(@Param("wfId")int wfId,@Param("flag") int flag);

    List<DataFlowInfo> getDataFlowInfosByDataSourceId(Integer topicId);

    @Query(value="select d from DataFlowInfo d where d.userId=:userId and d.wfName like %:wfName% ") //and d.wfName like %:query%
    Page<DataFlowInfo> getAllByUserId(@Param("userId")int userId ,@Param("wfName") String wfName, Pageable pageable);// @Param("query")String query,


    @Modifying
    @Query("update DataFlowInfo  d set d.conf=:conf where d.wfId=:wfId ")
    void updataConf(@Param("wfId")int wfId,@Param("conf")String conf);
}
