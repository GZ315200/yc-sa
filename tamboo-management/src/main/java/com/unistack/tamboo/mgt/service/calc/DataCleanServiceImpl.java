package com.unistack.tamboo.mgt.service.calc;

import com.unistack.tamboo.mgt.dao.calc.dataCleanDao;
import com.unistack.tamboo.mgt.model.calc.DataFilter;
import com.unistack.tamboo.mgt.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DataCleanServiceImpl extends BaseService {
    @Autowired
    private dataCleanDao dateFilterDao;

    public DataFilter saveDataFilter(DataFilter df) {
        DataFilter isSaved = dateFilterDao.save(df);
        return isSaved;
    }


    public Page<DataFilter> queryDataFilter(Pageable pageable,String filterNameOrType){
        return StringUtils.isBlank(filterNameOrType)? dateFilterDao.findAll(pageable) : dateFilterDao.getDataFilter(pageable,filterNameOrType);
    }

    public List<String> getDataFilterConf(){
        return dateFilterDao.getDataFilterConf();
    }


//    /**
//     * 数据清洗业务的实时测试方法,
//     *
//     * @param msg  json格式的数据
//     * @param conf json格式的配置文件
//     * @return
//     */
//    public JSONObject dataCleanTest(String msg, String conf){
//        JSONObject jsonMsg;
//        JSONObject jsonConf;
//        JSONObject result = new JSONObject();
//        try{
//            jsonMsg = JSONObject.parseObject(msg);
//            jsonConf = JSONObject.parseObject(conf);
//        }catch(Exception e){
//            result.put("code", "199");
//            result.put("msg", "要处理的数据和配置文件都必须是json格式");
//            return result;
//        }
//        return DataCleanProcess.dataClean(jsonMsg);
//    }

}