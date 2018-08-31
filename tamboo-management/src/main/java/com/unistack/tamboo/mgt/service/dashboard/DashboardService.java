package com.unistack.tamboo.mgt.service.dashboard;


import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.unistack.tamboo.commons.utils.errors.DataException;
import com.unistack.tamboo.mgt.model.dashboard.RealTimeData;
import com.unistack.tamboo.mgt.model.monitor.MOffsetD;
import com.unistack.tamboo.mgt.model.monitor.MOffsetW;
import com.unistack.tamboo.mgt.model.monitor.MonitorRecordVo;
import com.unistack.tamboo.mgt.service.BaseService;
import com.unistack.tamboo.mgt.utils.MonitorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Gyges Zean
 * @date 2018/5/30
 */

@Service
public class DashboardService extends BaseService {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    /**
     * 计算数据总量、message rate总量
     *
     * @param summaryType
     * @return
     */
    public RealTimeData summary(int summaryType) {
        switch (summaryType) {
            case SummaryType.TD:
                return MonitorUtils.summaryDataCollect();
            case SummaryType.TM:
                return MonitorUtils.summaryDataHub();
            case SummaryType.TS:
                return MonitorUtils.summaryDataStreaming();
            case SummaryType.TC:
                return MonitorUtils.summaryConnectSinkData();
            default:
                throw new DataException("unknown error");
        }
    }


    public JSONObject getHistoryRecordIn2Day() {
        String sql = "select m.accept_time as accept_time ,AVG(m.message_rate) as message_rate,AVG(m.offset_add) as offsetAdd from m_offset_d m " +
                "GROUP BY m.accept_time having m.accept_time > DATE_ADD(now(),INTERVAL -2 DAY) order by m.accept_time ASC";

        JSONObject data = new JSONObject();

        List<MOffsetD> mOffsetDList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(MOffsetD.class));
        List<MonitorRecordVo> today = Lists.newArrayList();
        List<MonitorRecordVo> yesterday = Lists.newArrayList();
        mOffsetDList.forEach(offsetD -> {

            MonitorRecordVo monitorRecordVo = new MonitorRecordVo();
            monitorRecordVo.setMessageRate(offsetD.getMessageRate());
            monitorRecordVo.setOffsetAdd(offsetD.getOffsetAdd());
            monitorRecordVo.setTimestamp(offsetD.getAcceptTime().getTime());
            if (offsetD.getAcceptTime().getTime() > getZeroTime()) {
                today.add(monitorRecordVo);
            } else {
                yesterday.add(monitorRecordVo);
            }

        });
        data.put("today", today);
        data.put("yesterday", yesterday);
        return data;
    }


    public JSONObject getHistoryRecordIn2Week() {
        String sql = "select m.accept_time as accept_time ,AVG(m.message_rate) as message_rate,AVG(m.offset_add) as offsetAdd from m_offset_w m " +
                "GROUP BY m.accept_time having m.accept_time > DATE_ADD(NOW(),INTERVAL -14 DAY) order by m.accept_time ASC";

        List<MOffsetW> mOffsetWList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(MOffsetW.class));

        JSONObject data = new JSONObject();

        List<MonitorRecordVo> thisWeek = Lists.newArrayList();
        List<MonitorRecordVo> lastWeek = Lists.newArrayList();

        mOffsetWList.forEach(offsetW -> {

            MonitorRecordVo monitorRecordVo = new MonitorRecordVo();
            monitorRecordVo.setMessageRate(offsetW.getMessageRate());
            monitorRecordVo.setOffsetAdd(offsetW.getOffsetAdd());
            monitorRecordVo.setTimestamp(offsetW.getAcceptTime().getTime());
            if (offsetW.getAcceptTime().getTime() > get7pZeroTime()) {
                thisWeek.add(monitorRecordVo);
            } else {
                lastWeek.add(monitorRecordVo);
            }
        });

        data.put("thisWeek", thisWeek);
        data.put("lastWeek", lastWeek);
        return data;
    }


}
