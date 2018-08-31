package com.unistack.tamboo.mgt.controller.alert;

import com.unistack.tamboo.mgt.alert.AlertEngine;
import com.unistack.tamboo.mgt.common.ServerResponse;
import com.unistack.tamboo.mgt.controller.BaseController;
import com.unistack.tamboo.mgt.dao.alert.AlertItemDao;
import com.unistack.tamboo.mgt.model.monitor.AlertItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.unistack.tamboo.mgt.common.ServerResponse.createBySuccess;


/**
 * Alert控制层:Alert的增删改查
 */
@RestController
@RequestMapping("/alert")
@CrossOrigin(origins = "*",maxAge = 3600)
public class AlertController extends BaseController {

    @Autowired
    private AlertItemDao alertItemDao;

    @Autowired
    private AlertEngine alertEngine;

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @Secured({"ROLE_ADMIN"})
    public ServerResponse getAll() {
        return createBySuccess(alertItemDao.findAll());
    }

    /**
     * 创建Alert
     *
     * @param items
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({"ROLE_ADMIN"})
    public ServerResponse createAlertItem(@RequestBody List<AlertItem> items) {
        alertItemDao.save(items);
        alertEngine.rescheduleAlerts();
        return createBySuccess();
    }

    /**
     * 更新Alert
     *
     * @param items
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({"ROLE_ADMIN"})
    public ServerResponse updateAlertItem(@RequestBody List<AlertItem> items) {
        alertItemDao.save(items);
        alertEngine.rescheduleAlerts();
        return createBySuccess();
    }

//    /**
//     * 删除指定name的Alert
//     *
//     * @param alertName
//     * @return
//     */
//    @RequestMapping(value = "/delete", method = RequestMethod.GET)
//    @Secured({"ROLE_ADMIN"})
//    public ServerResponse deleteAlertItem(@RequestParam("alert_name") String alertName,
//                                          @RequestParam(value = "is_active", required = false, defaultValue = "true") boolean isActive) {
//        alertItemDao.updateAlertStatus(isActive, alertName);
//        alertEngine.rescheduleAlerts();
//        return createBySuccess();
//    }
}