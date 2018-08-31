package com.unistack.tamboo.mgt.dao.sys;

import com.unistack.tamboo.mgt.dao.BaseDao;
import com.unistack.tamboo.mgt.model.sys.SysUserGroup;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface UserGroupDao  extends BaseDao<SysUserGroup, Integer> {


    /**
     * 获取所有用户组名
     * @return
     */
    @Query("select distinct s.groupName from SysUserGroup s")
    Set<String> getGroups();


    SysUserGroup getSysUserGroupByGroupName(String groupName);

}
