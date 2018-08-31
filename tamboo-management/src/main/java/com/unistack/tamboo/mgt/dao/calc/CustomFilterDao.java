package com.unistack.tamboo.mgt.dao.calc;

import com.unistack.tamboo.mgt.dao.BaseDao;
import com.unistack.tamboo.mgt.model.calc.CustomFilter;

public interface CustomFilterDao extends BaseDao<CustomFilter, Integer> {
/*    //利用原生的SQL进行插入操作
    @Modifying
    @Query(value = "insert into custom_filter(jar_name,jar_user,jar_updatetime,jar_desc,jar_classpath,remark) value(?1,?2,?3,?4,?5,?6)", nativeQuery = true)
    public void insertCustom(CustomFilter customFilter);*/
}
