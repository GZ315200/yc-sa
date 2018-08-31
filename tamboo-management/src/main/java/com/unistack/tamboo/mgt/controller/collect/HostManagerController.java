package com.unistack.tamboo.mgt.controller.collect;

import com.unistack.tamboo.mgt.common.RestCode;
import com.unistack.tamboo.mgt.common.ServerResponse;
import com.unistack.tamboo.mgt.common.page.PaginationData;
import com.unistack.tamboo.mgt.controller.BaseController;
import com.unistack.tamboo.mgt.model.collect.DataSourceHost;
import com.unistack.tamboo.mgt.model.collect.DataSourceHostVo;
import com.unistack.tamboo.mgt.service.collect.HostServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;

/**
 * @program: tamboo-sa
 * @description: 主机管理模块
 * @author: Asasin
 * @create: 2018-07-19 09:48
 **/
@RestController
@RequestMapping("/host")
@CrossOrigin(origins = "*", maxAge = 3600)
public class HostManagerController extends BaseController{
    private static  Logger logger = LoggerFactory.getLogger(HostManagerController.class);

    @Autowired
    private HostServiceImpl hostService;




    /**
     * 查询主机列表
     * 根据条件查询主机列表
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
    public ServerResponse<PaginationData<DataSourceHost>> list(@RequestParam(required = false) String query,
                                                               @RequestParam(required = false, defaultValue = "1") int pageIndex,
                                                               @RequestParam(required = false, defaultValue = "10") int pageSize,
                                                               @RequestParam(required = false, defaultValue = "createTime") String orderBy,
                                                               @RequestParam(required = false, defaultValue = "false") boolean asc) {
        try {
            Sort sort = null;
            String[] sortProps = new String[]{"id", "createTime"};
            if (StringUtils.isNotBlank(orderBy) && Arrays.asList(sortProps).contains(orderBy)) {
                if (asc) {
                    sort = new Sort(Sort.Direction.ASC, orderBy);
                } else {
                    sort = new Sort(Sort.Direction.DESC, orderBy);
                }
            }
            return this.hostService.list(getFromSpringStringParam(query), pageIndex, pageSize, sort);
        } catch (Exception e) {
            logger.error("got hosts lists in paging exception = {}", e);
        }
        return ServerResponse.createByErrorCodeAndMsg(RestCode.ILLEGAL_ARGUMENT.getStatus(), RestCode.ILLEGAL_ARGUMENT.getMsg());
    }


    /**
     * 保存主机
     * @param dataSourceHostVo
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
//    @Secured("ROLE_ADMIN")
    public ServerResponse add(@RequestBody DataSourceHostVo dataSourceHostVo) {
        dataSourceHostVo.setLastCheckTime(new Date());
        try {
            dataSourceHostVo.getDataSourceHost().setLastCheckTime(new Date());
            return hostService.add(dataSourceHostVo);
        } catch (Exception e) {
            logger.error("save host exception host = {} exception = {}", dataSourceHostVo.getIp(), e);
            return ServerResponse.createByErrorCodeAndMsg(RestCode.ILLEGAL_ARGUMENT.getStatus(), RestCode.ILLEGAL_ARGUMENT.getMsg());
        }
    }

    /**
     * 删除主机
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.GET, produces = "application/json")
    public ServerResponse delete(@RequestParam int id) {
        return hostService.delete(id);
    }


    /**
     * 获取所有主机
     * @return
     */
    @RequestMapping(value = "/getAllHost", method = RequestMethod.GET, produces = "application/json")
    public ServerResponse getAllHost() {
        return hostService.getAllHost();
    }

}
    