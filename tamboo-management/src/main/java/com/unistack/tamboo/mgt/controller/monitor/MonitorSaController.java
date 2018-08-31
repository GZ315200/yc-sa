package com.unistack.tamboo.mgt.controller.monitor;

import com.unistack.tamboo.mgt.common.ServerResponse;
import com.unistack.tamboo.mgt.common.page.PaginationData;
import com.unistack.tamboo.mgt.controller.BaseController;
import com.unistack.tamboo.mgt.service.monitor.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import static com.unistack.tamboo.mgt.common.ServerResponse.createBySuccess;

/**
 * @author Gyges Zean
 * @date 2018/5/23
 */

@RestController
@RequestMapping("/monitor")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MonitorSaController extends BaseController {

    @Autowired
    private MonitorService monitorService;


    @RequestMapping(value = "/brokers", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public ServerResponse<PaginationData> queryBrokers(@RequestParam(value = "key", required = false) String query,
                                                       @RequestParam(value = "pageIndex", defaultValue = "1") int pageIndex,
                                                       @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        Sort sort = new Sort(Sort.Direction.ASC, "host");
        PageRequest request = new PageRequest(pageIndex - 1, pageSize, sort);
        return createBySuccess(monitorService.queryBrokersServiceInfo(query, request));
    }

    @RequestMapping(value = "/brokersProfile", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public ServerResponse brokersProfile() {
        return createBySuccess(monitorService.getBrokerServiceInfo());
    }


    @RequestMapping(value = "/calc", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public ServerResponse<PaginationData> queryCalcWorkers(@RequestParam(value = "key", required = false) String query,
                                                           @RequestParam(value = "pageIndex", defaultValue = "1") int pageIndex,
                                                           @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        Sort sort = new Sort(Sort.Direction.ASC, "host");
        PageRequest request = new PageRequest(pageIndex - 1, pageSize, sort);
        return createBySuccess(monitorService.queryCalcServiceInfo(query, request));
    }

    @RequestMapping(value = "/calcProfile", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public ServerResponse calcProfile() {
        return createBySuccess(monitorService.getCalcServiceProfile());
    }


    @RequestMapping(value = "/zkProfile", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public ServerResponse zkProfile() {
        return createBySuccess(monitorService.getZookeeperInfo());
    }
}
