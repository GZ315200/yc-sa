package com.unistack.tamboo.mgt.controller.collect;

import com.unistack.tamboo.mgt.common.RestCode;
import com.unistack.tamboo.mgt.common.ServerResponse;
import com.unistack.tamboo.mgt.common.page.PaginationData;
import com.unistack.tamboo.mgt.controller.BaseController;
import com.unistack.tamboo.mgt.dao.collect.DataSourceListDao;
import com.unistack.tamboo.mgt.model.collect.DataSourceList;
import com.unistack.tamboo.mgt.model.collect.DataSourceListAS;
import com.unistack.tamboo.mgt.service.collect.DataSourceServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;


/**
 * @program: tamboo-sa
 * @description: 数据源管理模块
 * @author: Asasin
 * @create: 2018-05-17 12:10
 **/
@RestController
@RequestMapping("/dataSource")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DataSourceManagerController extends BaseController {
    private static  Logger logger = LoggerFactory.getLogger(DataSourceManagerController.class);

    @Autowired
    private DataSourceServiceImpl dadaSourceListService;

    @Autowired
    private DataSourceListDao dataSourceListDao;

    /**
     * 分页查询数据源列表
     * @param queryType
     * @param queryName
     * @param pageIndex
     * @param pageSize
     * @param orderBy
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
    public ServerResponse<PaginationData<DataSourceList>> list(@RequestParam(required = false, defaultValue = "") String queryType,
                                                               @RequestParam(required = false, defaultValue = "") String queryName,
                                                               @RequestParam(required = false, defaultValue = "1") int pageIndex,
                                                               @RequestParam(required = false, defaultValue = "10") int pageSize,
                                                               @RequestParam(required = false, defaultValue = "createTime") String orderBy) {
        return dadaSourceListService.list(queryType, queryName, orderBy, pageIndex, pageSize);
    }

    /**
     *
     *获取所有运行中的dataSource
     * @return
     */
    @RequestMapping(value = "/getRunning", method = RequestMethod.GET)
    public ServerResponse getRunning() {
        return dadaSourceListService.getRunning();
    }


    /**
     * 添加数据源
     * @param dataSourceListAS
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json")
    public ServerResponse add(@RequestBody DataSourceListAS dataSourceListAS) {
        if (dataSourceListAS == null) {
            return ServerResponse.createByErrorCodeAndMsg(RestCode.ILLEGAL_ARGUMENT.getStatus(), RestCode.ILLEGAL_ARGUMENT.getMsg());
        }
        try {
            String[] str = new String[]{"MYSQL","ORACLE","SQLSERVER","PG","HBASE"};
            for (String string:str){
                if (StringUtils.equals(dataSourceListAS.getDataSourceList().getDataSourceType(),string)){
                    return dadaSourceListService.addByConnect(dataSourceListAS);
                }
            }
            return dadaSourceListService.addByFlume(dataSourceListAS);
        } catch (Exception e) {
            return ServerResponse.createByErrorMsg(e.getMessage());
        }
    }

    /**
     * 删除数据源e
     *
     * @param dataSourceId
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.GET, produces = "application/json")
    public ServerResponse delete(@RequestParam Long dataSourceId) {
        if (dataSourceId == null) {
            return ServerResponse.createByErrorCodeAndMsg(RestCode.ILLEGAL_ARGUMENT.getStatus(), RestCode.ILLEGAL_ARGUMENT.getMsg());
        }
        try {
            return dadaSourceListService.deleteDatasource(dataSourceId);
        } catch (Exception e) {
            return ServerResponse.createByErrorMsg(e.getMessage());
        }
    }

    /**
     *  查询数据源配置信息
     * @param dataSourceId
     * @return
     */
    @RequestMapping(value = "/queryById", method = RequestMethod.GET, produces = "application/json")
    public ServerResponse queryById(@RequestParam Long dataSourceId) {
        if (dataSourceId == null) {
            return ServerResponse.createByErrorCodeAndMsg(RestCode.ILLEGAL_ARGUMENT.getStatus(), RestCode.ILLEGAL_ARGUMENT.getMsg());
        }
        return dadaSourceListService.queryById(dataSourceId);
    }

    /**
     *  编辑数据源
     * @param dataSourceListAS
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST, produces = "application/json")
    public ServerResponse edit(@RequestBody DataSourceListAS dataSourceListAS) {
        if (dataSourceListAS == null) {
            return ServerResponse.createByErrorCodeAndMsg(RestCode.ILLEGAL_ARGUMENT.getStatus(), RestCode.ILLEGAL_ARGUMENT.getMsg());
        }
        try {
            return dadaSourceListService.editDatasource(dataSourceListAS);
        } catch (Exception e) {
            return ServerResponse.createByErrorMsg(e.getMessage());
        }
    }

    /**
     * 启动数据源（若后期项目需要再完善）
     * @param dataSourceId
     * @return
     */
    @RequestMapping(value = "/start", method = RequestMethod.GET, produces = "application/json")
    public ServerResponse start(@RequestParam Long dataSourceId) {
        if (dataSourceId == null) {
            return ServerResponse.createByErrorCodeAndMsg(RestCode.ILLEGAL_ARGUMENT.getStatus(), RestCode.ILLEGAL_ARGUMENT.getMsg());
        }
        try {
            DataSourceList dataSourceList = dataSourceListDao.getDataSourceListByDataSourceId(dataSourceId);
            String[] str = new String[]{"MYSQL","ORACLE","SQLSERVER","PG","HBASE"};
            for (String string:str){
                if (StringUtils.equals(dataSourceList.getDataSourceType(),string)){
                    return dadaSourceListService.startByConnect(dataSourceId);
                }
            }
            return dadaSourceListService.startByFlume(dataSourceId);
        } catch (Exception e) {
            return ServerResponse.createByErrorMsg(e.getMessage());
        }
    }

    /**
     * 停止数据源
     * @param dataSourceId
     * @return
     */
    @RequestMapping(value = "/stop", method = RequestMethod.GET, produces = "application/json")
    public ServerResponse stop(@RequestParam Long dataSourceId) {
        if (dataSourceId == null) {
            return ServerResponse.createByErrorCodeAndMsg(RestCode.ILLEGAL_ARGUMENT.getStatus(), RestCode.ILLEGAL_ARGUMENT.getMsg());
        }
        try {
            DataSourceList dataSourceList = dataSourceListDao.getDataSourceListByDataSourceId(dataSourceId);
            String[] str = new String[]{"MYSQL","ORACLE","SQLSERVER","PG","HBASE"};
            for (String string:str){
                if (StringUtils.equals(dataSourceList.getDataSourceType(),string)){
                    return dadaSourceListService.stopByConnect(dataSourceId);
                }
            }
            return dadaSourceListService.stopByFlume(dataSourceId);
        } catch (Exception e) {
            return ServerResponse.createByErrorMsg(e.getMessage());
        }
    }

    /**
     * 数据源Topic和开放Topic统计
     *
     * @return
     */
    @RequestMapping(value = "/getDataSourceTopic", method = RequestMethod.GET, produces = "application/json")
    public ServerResponse countDataSourceTopic() {
        return dadaSourceListService.countDataSourceTopic();
    }

    /**
     * 获取数据源采集数据量和速率
     *
     * @return
     */
    @RequestMapping(value = "/getTotalSize", method = RequestMethod.GET, produces = "application/json")
    public ServerResponse getTotalSize() {
        return dadaSourceListService.getTotalSize();
    }


    /**
     * 数据源Topic,开放Topic,临时Topic
     *
     * @return
     */
    @RequestMapping(value = "/getTopicByType", method = RequestMethod.POST, produces = "application/json")
    public ServerResponse getTopicByType(@RequestParam int topicType) {
        if (StringUtils.isNotBlank(String.valueOf(topicType))) {
            return dadaSourceListService.getTopicByTopicType(topicType);
        }
        return ServerResponse.createByErrorCodeAndMsg(RestCode.ILLEGAL_ARGUMENT.getStatus(), RestCode.ILLEGAL_ARGUMENT.getMsg());
    }

    /**
     * 数据源查看
     *
     * @return
     */
    @RequestMapping(value = "/queryDataSource", method = RequestMethod.POST, produces = "application/json")
    public ServerResponse queryDataSource(@RequestParam Long dataSourceId) {
        if (dataSourceId == null) {
            return ServerResponse.createByErrorCodeAndMsg(RestCode.ILLEGAL_ARGUMENT.getStatus(), RestCode.ILLEGAL_ARGUMENT.getMsg());
        }
        return dadaSourceListService.queryDataSource(dataSourceId);
    }

    /**
     * 统计所有数据源数量
     *
     * @return
     */
    @RequestMapping(value = "/countAll", method = RequestMethod.GET)
    public ServerResponse countAll() {
        return dadaSourceListService.countAll();
    }

    /**
     * DashBoard dataSource饼图
     *
     * @return
     */
    @RequestMapping(value = "/countByType", method = RequestMethod.GET, produces = "application/json")
    public ServerResponse countByType() {
        return dadaSourceListService.countByType();
    }

    /**
     * 统计runging数据源数量
     *
     * @return
     */
    @RequestMapping(value = "/countRunning", method = RequestMethod.GET)
    public ServerResponse countRunning() {
        return dadaSourceListService.countRunning();
    }

    /**
     * 用户列表
     *
     * @return
     */
    @RequestMapping(value = "/getUserGroups", method = RequestMethod.GET)
    public ServerResponse<Set<String>> listGroup() {
        return ServerResponse.createBySuccess(dadaSourceListService.getGroups());
    }




}