package com.unistack.tamboo.mgt.controller.calc;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.commons.utils.errors.NoLoginSession;
import com.unistack.tamboo.mgt.common.RestCode;
import com.unistack.tamboo.mgt.common.ServerResponse;
import com.unistack.tamboo.mgt.common.page.PageConvert;
import com.unistack.tamboo.mgt.common.page.PaginationData;
import com.unistack.tamboo.mgt.model.calc.OfflineDataSource;
import com.unistack.tamboo.mgt.model.sys.UserSession;
import com.unistack.tamboo.mgt.service.calc.OfflineDataSourceServiceImpl;
import com.unistack.tamboo.mgt.utils.SecUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * @author hero.li
 * 这个类用来对外部提供接口
 */
@RestController
@RequestMapping("/calc/offlineDataSource")
@CrossOrigin(origins = "*",maxAge = 3600)
public class OfflineDataSourceController {

    @Autowired
    private OfflineDataSourceServiceImpl offlineDataSourceService;

    @RequestMapping(value="/saveOfflineDataSource",method = RequestMethod.POST)
    public ServerResponse<OfflineDataSource> saveOfflineDataSource(@RequestBody OfflineDataSource dataSource){
           return offlineDataSourceService.saveOfflineDataSource(dataSource);
    }


    @RequestMapping(value = "/pageOfflineDataSourceList",method = RequestMethod.GET)
    public ServerResponse<PaginationData<List<OfflineDataSource>>> offlineDataSourceList
            (@RequestParam(value = "dataSourceName",required = false) String dataSourceName,
             @RequestParam(value = "pageIndex",defaultValue = "1") int pageIndex,
             @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        try{
            UserSession userSession = SecUtils.findLoggedUserSession();
            String username = userSession.getUsername();

            Sort sort = new Sort(Sort.Direction.DESC,"addTime");
            PageRequest page = new PageRequest(pageIndex-1, pageSize,sort);
            Page<OfflineDataSource> pageOfflineDataSource = offlineDataSourceService.getOfflineDataSource(page,username,dataSourceName);
            PaginationData<List<OfflineDataSource>> paginationData = PageConvert.convertJpaPage2PaginationData(pageOfflineDataSource,pageSize);
            return ServerResponse.createBySuccess(paginationData);
        }catch (NoLoginSession e){
            return ServerResponse.createByErrorCodeAndMsg(RestCode.NEED_LOGIN.getStatus(),"没有登录！");
        }catch(Exception e){
            return ServerResponse.createByErrorMsg("查询异常!");
        }
    }

    @RequestMapping(value = "/deleteOfflineDataSource",method =RequestMethod.GET)
    public ServerResponse<?> deleteOfflineDataSource(@RequestParam Integer id){
        return offlineDataSourceService.deleteOfflineDataSource(id);
    }

    @RequestMapping(value ="/dbConnectionTest",method = RequestMethod.POST)
    public ServerResponse<String> dbConnectionTest(@RequestBody OfflineDataSource datasource){
        JSONObject result = offlineDataSourceService.dbConnectionTest(datasource);
        String code = result.getString("code");
        String msg = result.getString("msg");
        return "200".equals(code) ? ServerResponse.createBySuccess():ServerResponse.createByErrorMsg(msg);
    }

    @RequestMapping(value = "/OfflineDataSource",method =RequestMethod.GET)
    public ServerResponse<List<String>> getDataSourceByType(@RequestParam String dbType){
        return offlineDataSourceService.getOfsListByType(dbType);
    }

    @RequestMapping(value = "/getTables",method = RequestMethod.POST)
    public ServerResponse<List<String>> getTablesByDatabase(@RequestBody OfflineDataSource dataSource){
        return offlineDataSourceService.getTablesByDatabase(dataSource);
    }

    @RequestMapping(value = "/getColumns",method = RequestMethod.POST)
    public ServerResponse<List<JSONObject>> getColumns(@RequestBody JSONObject dataSource){
        return offlineDataSourceService.getColumns(dataSource);
    }
}