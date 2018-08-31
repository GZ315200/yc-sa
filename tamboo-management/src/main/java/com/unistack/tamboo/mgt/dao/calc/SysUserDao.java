package com.unistack.tamboo.mgt.dao.calc;

import com.unistack.tamboo.mgt.dao.BaseDao;
import com.unistack.tamboo.mgt.model.sys.SysUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SysUserDao extends BaseDao<SysUser,Integer> {

    @Query(value = "select su from SysUser su where su.username = :username")
    SysUser getSysUserByUsername(@Param("username") String username);
}
