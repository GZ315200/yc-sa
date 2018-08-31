package com.unistack.tamboo.mgt.controller.calc;

import com.unistack.tamboo.compute.process.until.dataclean.filter.impl.DateFilter;
import com.unistack.tamboo.mgt.common.ServerResponse;
import com.unistack.tamboo.mgt.common.page.PageConvert;
import com.unistack.tamboo.mgt.common.page.PaginationData;
import com.unistack.tamboo.mgt.model.calc.DataFilter;
import com.unistack.tamboo.mgt.model.sys.UserSession;
import com.unistack.tamboo.mgt.service.calc.DataCleanServiceImpl;
import com.unistack.tamboo.mgt.utils.SecUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author hero.li
 * 这个类用来对外部提供接口
 */
@RestController
@RequestMapping("/calc/dataClean")
@CrossOrigin(origins = "*",maxAge = 3600)
public class DataCleanController {

    @Autowired
    private DataCleanServiceImpl dataCleanService;

    /**
     * 这个借口用于filter列表的添加
     * @param item
     * @return
     */
    @RequestMapping(value="/filterListAdd",method=RequestMethod.POST)
    public ServerResponse<DataFilter> filterListAdd(@RequestBody DataFilter item){
        try{
            UserSession loggedUserSession = SecUtils.findLoggedUserSession();
            String userName = loggedUserSession.getUsername();
            item.setIsActive("1");
            item.setRemark1(userName);
            item.setFilterAddTime(new Timestamp(System.currentTimeMillis()));
            DataFilter save = dataCleanService.saveDataFilter(item);
            return ServerResponse.createBySuccess(save);
        }catch(Exception e){
            return ServerResponse.createByErrorMsg("保存失败!");
        }
    }


    /**
     * 查询filter的列表,前端提供基于页面的模糊搜索根据filterName/filterType <br/>
     * @param filterNameOrType
     * @return
     */
    @RequestMapping(value="/filterList",method=RequestMethod.GET)
    public ServerResponse<PaginationData<List<DateFilter>>> filterTypeOrNameLike(
           @RequestParam(value = "filterNameOrType",required = false) String filterNameOrType,
           @RequestParam(value = "pageIndex",defaultValue = "1") int pageIndex,
           @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        try{
            Sort sort = new Sort(Sort.Direction.DESC,"filterAddTime");
            PageRequest page = new PageRequest(pageIndex-1, pageSize,sort);
            Page<DataFilter> dataFilters = dataCleanService.queryDataFilter(page, filterNameOrType);
            PaginationData<List<DateFilter>> paginationData = PageConvert.convertJpaPage2PaginationData(dataFilters,pageSize);
            return ServerResponse.createBySuccess(paginationData);
        }catch(Exception e){
            return ServerResponse.createByErrorMsg("查询异常!");
        }
    }

    @RequestMapping(value="/filterConf",method = RequestMethod.GET)
    public ServerResponse<List<String>> getDataFilterConf(){
        List<String> conf = dataCleanService.getDataFilterConf();
        return ServerResponse.createBySuccess(conf);
    }

//    @RequestMapping(value="/dataCleanTest",method = RequestMethod.POST)
//    public ServerResponse<?> dataCleanTest(@RequestBody String msg,@RequestBody String conf){
//        JSONObject result = dataCleanService.dataCleanTest(msg, conf);
//        String code = result.getString("code");
//        String errorMsg = result.getString("msg");
//        return "200".equals(code) ? ServerResponse.createBySuccess() : ServerResponse.createByErrorMsg(errorMsg);
//    }

}