package com.unistack.tamboo.mgt.service.calc;

import com.unistack.tamboo.mgt.dao.calc.CustomFilterDao;
import com.unistack.tamboo.mgt.model.calc.CustomFilter;
import com.unistack.tamboo.mgt.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileUploadServiceImpl extends BaseService {
    @Autowired
    private CustomFilterDao customFilterDao;

    public CustomFilter saveCustomFilter(CustomFilter cf) {
        CustomFilter isSaved = customFilterDao.save(cf);
        return isSaved;
    }


}