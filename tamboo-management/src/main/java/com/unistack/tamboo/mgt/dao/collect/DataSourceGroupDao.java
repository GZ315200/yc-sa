package com.unistack.tamboo.mgt.dao.collect;

import com.unistack.tamboo.mgt.dao.BaseDao;
import com.unistack.tamboo.mgt.model.collect.DataSourceGroup;


public interface DataSourceGroupDao  extends BaseDao<DataSourceGroup,String>  {

//    @Modifying
//    @Transactional
//    @Query("update  DataSourceGroup d set d.sourceCpu= :sourceCpu,d.sourceMem= :sourceMem,d.topicNum= :topicNum,d.topicSize= :topicSize,d.desc= :desc where d.groupName=:groupName")
//    public void updateByGroupName(@Param("sourceCpu")int sourceCpu, @Param("sourceMem")int sourceMem,
//                           @Param("topicNum")int topicNum,@Param("topicSize")int topicSize,
//                           @Param("desc")String desc,@Param("groupName")String groupName);
}
