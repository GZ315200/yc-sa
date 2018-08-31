package com.unistack.tamboo.mgt.dao.sys;


import com.unistack.tamboo.mgt.dao.BaseDao;
import com.unistack.tamboo.mgt.model.sys.SysUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Gyges Zean
 * @date 2018/5/17
 */
public interface UserDao extends BaseDao<SysUser, Long> {

    SysUser findSysUserById(Long userId);

    SysUser getSysUserByUsername(String username);

    @Modifying
    @Query("update SysUser su set su.isActive =:is_active where su.id =:user_id")
    int modifyIsActive(@Param(value = "user_id") Long userId, @Param(value = "is_active") int isActive);

    @Query("select s from SysUser s where s.userGroup in :userGroup")
    List<SysUser> getUserIdsByUserGroup(@Param("userGroup") String userGroup);


    @Query("select s from SysUser s where s.username like %:username%")
    Page<SysUser> getSysUserByUsernameLike(Pageable pageable, @Param(value = "username") String username);

    @Override
    Page<SysUser> findAll(Pageable pageable);

    //查询当前用户所在用户组的所有用户
    List<SysUser> getSysUserByUserGroup(String groupName);
}
