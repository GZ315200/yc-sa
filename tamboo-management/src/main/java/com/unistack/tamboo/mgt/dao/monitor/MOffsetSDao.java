package com.unistack.tamboo.mgt.dao.monitor;

import com.unistack.tamboo.mgt.dao.BaseDao;
import com.unistack.tamboo.mgt.model.monitor.MOffsetS;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * @author Gyges Zean
 * @date 2018/6/2
 */
public interface MOffsetSDao extends BaseDao<MOffsetS, Long> {

    MOffsetS getFirstByTopicIdOrderByAcceptTimeDesc(Long topicId);


    @Query(value = "select d from MOffsetS d where d.topicId=:topicId and d.acceptTime > :date ")
    List<MOffsetS> getThirtyMinMonitors(@Param("topicId") long topicId, @Param("date") Date date);


    @Query(value = "select d from MOffsetS d where d.topicId=:topicId and d.acceptTime > :cur and d.acceptTime < :last")
    List<MOffsetS> getOffsets(@Param("topicId") long topicId, @Param("cur") Date cur, @Param("last") Date last);

}
