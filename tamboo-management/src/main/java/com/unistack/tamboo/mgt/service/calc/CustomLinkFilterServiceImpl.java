package com.unistack.tamboo.mgt.service.calc;

import com.unistack.tamboo.mgt.dao.calc.CustomLinkFilterDao;
import com.unistack.tamboo.mgt.model.calc.CustomLinkFilter;
import com.unistack.tamboo.mgt.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName CustomLinkFilterServiceImpl
 * @Description TODO
 * @Author unistack
 * @Date 2018/7/30 15:21
 * @Version 1.0
 */
@Service
public class CustomLinkFilterServiceImpl extends BaseService {


    @Autowired
    private CustomLinkFilterDao customLinkFilterDao ;
    public CustomLinkFilter saveCustomLinkFilter(CustomLinkFilter customLinkFilter) {
        CustomLinkFilter isSave = customLinkFilterDao.save(customLinkFilter);
        return isSave ;

    }
}
