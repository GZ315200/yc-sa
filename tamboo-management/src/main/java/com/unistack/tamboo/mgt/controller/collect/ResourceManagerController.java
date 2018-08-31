package com.unistack.tamboo.mgt.controller.collect;


import com.unistack.tamboo.mgt.common.ServerResponse;
import com.unistack.tamboo.mgt.controller.BaseController;
import com.unistack.tamboo.mgt.model.collect.*;
import com.unistack.tamboo.mgt.service.collect.ResourceServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @program: tamboo-sa
 * @description: 资源管理模块
 * @author: Asasin
 * @create: 2018-05-23 19:19
 **/
@RestController
@RequestMapping("/resource")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ResourceManagerController extends BaseController {
    private static  Logger logger = LoggerFactory.getLogger(DataSourceManagerController.class);

    @Autowired
    private ResourceServiceImpl resourceService;

    /**
     * 集群计算和存储总资源列表
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
    public ServerResponse<List<SourceTable>> list(){
        return resourceService.list();
    }


    /**
     * 用户组分配资源list
     * @return
     */
    @RequestMapping(value = "/getAll", method = RequestMethod.GET, produces = "application/json")
    public ServerResponse<List<DataSourceGroup>> getAll(){
        return resourceService.getAll();
    }

    /**
     * 用户组修改资源分配
     * @param dataFlowInfoGU
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST, produces = "application/json")
    public ServerResponse edit(@RequestBody DataFlowInfoGU  dataFlowInfoGU) {
        return resourceService.edit(dataFlowInfoGU);
    }

    /**
     * 用户组使用资源统计
     * @return
     */
    @RequestMapping(value = "/getUsed", method = RequestMethod.GET, produces = "application/json")
    public ServerResponse<List<DateFlowInfoU>> getUsed(){
        return resourceService.getUsed();
    }

    /**
     * 用户组剩余资源统计
     * @return
     */
    @RequestMapping(value = "/getSurplus", method = RequestMethod.GET, produces = "application/json")
    public ServerResponse<List<DataFlowInfoS>> getSurplus(){
        return resourceService.getSurplus();
    }

    /**
     * 用户组分配和剩余资源统计展示
     * @return
     */
    @RequestMapping(value = "/getAllAndUsed", method = RequestMethod.GET, produces = "application/json")
    public ServerResponse<List<DataFlowInfoGU>> getAllAndUsed(){
        return resourceService.getAllAndUsed();
    }

    /**
     * DashBoard resource百分比展示
     * @return
     */
    @RequestMapping(value = "/countByType", method = RequestMethod.GET, produces = "application/json")
    public ServerResponse countByType() {
        return ServerResponse.createBySuccess(resourceService.countByType());
    }


}
    