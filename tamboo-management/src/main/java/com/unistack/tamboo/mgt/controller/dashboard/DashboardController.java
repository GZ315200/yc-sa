package com.unistack.tamboo.mgt.controller.dashboard;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.mgt.common.ServerResponse;
import com.unistack.tamboo.mgt.controller.BaseController;
import com.unistack.tamboo.mgt.model.dashboard.RealTimeData;
import com.unistack.tamboo.mgt.service.dashboard.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Gyges Zean
 * @date 2018/5/30
 */
@RestController
@RequestMapping(value = "/dashboard")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DashboardController extends BaseController {

    @Autowired
    private DashboardService dashboardService;


    /**
     * 数据量汇总
     *
     * @param summaryType
     * @return
     */
    @RequestMapping(value = "/summary", method = RequestMethod.GET)
    public ServerResponse<RealTimeData> summary(@RequestParam(value = "summary_type") int summaryType) {
        return ServerResponse.createBySuccess(dashboardService.summary(summaryType));
    }


    /**
     * 获取历史的记录
     *
     * @param type 15 : 15分钟内  60分钟 : 1小时内
     * @return
     */
    @RequestMapping(value = "/compare", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public ServerResponse<JSONObject> getHistoryRecord(@RequestParam(value = "type", defaultValue = "2880") int type) {
        if (type <= Type.DAY) {
            return ServerResponse.createBySuccess(dashboardService.getHistoryRecordIn2Day());
        } else if (type > Type.DAY + 1 && type <= Type.WEEK) {
            return ServerResponse.createBySuccess(dashboardService.getHistoryRecordIn2Week());
        }
        return ServerResponse.createByErrorMsg("unknown error");
    }



}
