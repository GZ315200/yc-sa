package com.unistack.tamboo.mgt.dao.monitor;

import com.unistack.tamboo.mgt.dao.BaseDao;
import com.unistack.tamboo.mgt.model.monitor.MHistoryOffset;
import org.springframework.data.jpa.repository.Query;

/**
 * @author Gyges Zean
 * @date 2018/5/23
 */
public interface MHistoryOffsetDao extends BaseDao<MHistoryOffset, Integer> {

//    List<MHistoryOffset> getMHistoryOffsetByTopicId(Long topicId);

    @Query(value = "SELECT m.* FROM m_history_offset m WHERE m.group_id = ?1 ORDER BY m.id DESC LIMIT 1",nativeQuery = true)
    MHistoryOffset getLastMHistoryByGroupId(String groupId);


}
