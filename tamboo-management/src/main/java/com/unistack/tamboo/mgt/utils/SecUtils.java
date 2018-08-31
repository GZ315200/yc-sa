package com.unistack.tamboo.mgt.utils;

import com.google.common.collect.Sets;
import com.unistack.tamboo.commons.utils.errors.GeneralServiceException;
import com.unistack.tamboo.commons.utils.errors.NoLoginSession;
import com.unistack.tamboo.commons.utils.util.DESUtil;
import com.unistack.tamboo.mgt.config.security.CustomUserDetails;
import com.unistack.tamboo.mgt.dao.sys.RoleDao;
import com.unistack.tamboo.mgt.dao.sys.UserDao;
import com.unistack.tamboo.mgt.dao.sys.UserGroupDao;
import com.unistack.tamboo.mgt.model.sys.*;
import com.unistack.tamboo.mgt.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author Gyges Zean
 * @date 2018/5/17
 */
public class SecUtils extends BaseService {

    public static  Logger logger = LoggerFactory.getLogger(SecUtils.class);

    private static UserDao userDao = SpringContextHolder.getBean(UserDao.class);
    private static RoleDao roleDao = SpringContextHolder.getBean(RoleDao.class);
    private static UserGroupDao userGroupDao = SpringContextHolder.getBean(UserGroupDao.class);

    /**
     * 获取登录的用户session信息
     *
     * @return
     */
    public static UserSession findLoggedUserSession() throws Exception {
        Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails instanceof CustomUserDetails) {
            UserSession userSession = new UserSession();
            userSession.setUserId((Long) ((CustomUserDetails) userDetails).getId());
            userSession.setUserGroup(((CustomUserDetails) userDetails).getUserGroup());
            userSession.setUsername(((CustomUserDetails) userDetails).getUsername());
            return userSession;
        } else {
            throw new NoLoginSession("Can't find logged user session.");
        }
    }


    /**
     * 根据用户组获取用户组信息
     *
     * @param userGroup
     * @return
     */
    public static SysUserGroup getUserGroupByUserGroup(String userGroup) {
        return userGroupDao.getSysUserGroupByGroupName(userGroup);
    }


    public static SysUserGroup create(SysUserGroup sysUserGroup) {
        SysUserGroup userGroup = userGroupDao.getSysUserGroupByGroupName(sysUserGroup.getGroupName());
        if (!Objects.isNull(userGroup)) {
            throw new GeneralServiceException("user group is exist");
        }
        sysUserGroup.setAclUsername(getStringRandom(USERNAME));
        sysUserGroup.setAclPassword(DESUtil.encrypt(getStringRandom(PASSWORD)));
        MonitorUtils.saveDataSourceGroup(sysUserGroup.getGroupName(), StringUtils.EMPTY);
        return userGroupDao.save(sysUserGroup);
    }


    public static SysUserGroup update(SysUserGroup sysUserGroup) {
        return userGroupDao.save(sysUserGroup);
    }

    public static void delUserGroup(Integer groupId) {
        userGroupDao.delete(groupId);
    }


    public static Set<String> getAllUserGroups() {
        Set<String> userGroups = Sets.newHashSet();
        userGroupDao.findAll().forEach(sysUserGroup -> {
            userGroups.add(sysUserGroup.getGroupName());
        });
        return userGroups;
    }

    public static List<SysUserGroup> getAllUserGroup() {
        return userGroupDao.findAll();
    }


    public static SysUser getSysUserByUsername(String username) {
        SysUser user = userDao.getSysUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username " + username + " not found.");
        }
        return user;
    }


    public static SysUser userNameIsExist(String username) {
        return userDao.getSysUserByUsername(username);

    }


    public static SysUser getSysUserByUserId(Long userId) {
        SysUser user = userDao.findSysUserById(userId);
        if (user == null) {
            throw new NullPointerException("this userId " + userId + " is not exist.");
        }
        return user;
    }


    public static Set<SysResource> getResourceByRoleId(Long roleId) {
        SysRole role = roleDao.getSysRoleById(roleId);
        if (role == null) {
            return null;
        }
        return role.getResource();
    }


    public static SysUserPermissionVo getUserPermissionByUsername(String username) {
        SysUser sysUser = userDao.getSysUserByUsername(username);
        if (sysUser == null) {
            return null;
        }
        Set<SysRole> roles = sysUser.getRole();
        SysUserPermissionVo permission = new SysUserPermissionVo();
        roles.forEach(sysRole -> {
            permission.setRoles(roles);
            permission.setUserGroup(sysUser.getUserGroup());
            permission.setUserId(sysUser.getId());
            permission.setUsername(sysUser.getUsername());
            permission.setPermission(getResourceByRoleId(sysRole.getId()));
        });
        return permission;
    }


}
