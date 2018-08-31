package com.unistack.tamboo.mgt.service.calc;

import com.unistack.tamboo.mgt.dao.calc.DataFilterDao;
import com.unistack.tamboo.mgt.model.calc.DataFilter;
import com.unistack.tamboo.mgt.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName SendCustomFilterServiceImpl
 * @Description TODO
 * @Author unistack
 * @Date 2018/7/30 11:12
 * @Version 1.0
 */
@Service
public class SendCustomFilterServiceImpl extends BaseService {

    @Autowired
    private DataFilterDao dataFilterDao;
    public DataFilter saveDataFilter(DataFilter dataFilter) {
        DataFilter isSaved = dataFilterDao.save(dataFilter);
        return isSaved ;


    }

    public String getCustomFilterMess(String filterType) {
        String isSaved = dataFilterDao.getCustomFilterMess(filterType);
        return isSaved;
    }
}
