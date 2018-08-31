package com.unistack.tamboo.mgt.service.sys;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.unistack.tamboo.mgt.dao.sys.ResourceDao;
import com.unistack.tamboo.mgt.dao.sys.RoleDao;
import com.unistack.tamboo.mgt.dao.sys.UserDao;
import com.unistack.tamboo.mgt.model.sys.SysResource;
import com.unistack.tamboo.mgt.model.sys.SysRole;
import com.unistack.tamboo.mgt.model.sys.SysUser;
import com.unistack.tamboo.mgt.model.sys.SysUserRoleVo;
import com.unistack.tamboo.mgt.service.BaseService;
import com.unistack.tamboo.mgt.utils.MD5Util;
import com.unistack.tamboo.mgt.utils.SecUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author Gyges Zean
 * @date 2018/5/18
 */

@Service
public class SecurityService extends BaseService {


    @Autowired
    private UserDetailsService userDetailsService;


    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private ResourceDao resourceDao;

    private static  Logger logger = LoggerFactory.getLogger(SecurityService.class);


    public void autoLogin(String username, String password) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
        authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        if (usernamePasswordAuthenticationToken.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            logger.debug(String.format("Auto login %s successfully!", username));
        }
    }

    public SysUser updateUser(SysUser user) {
        Objects.requireNonNull(user.getId(), "User id should be specified.");
        userValidator(user);
        SysUser oldUser = SecUtils.getSysUserByUserId(user.getId());
        String oldPassword = oldUser.getPassword();
        String newPassword = user.getPassword();

        if (oldPassword.equals(newPassword)) {
            user.setPassword(oldPassword);
        } else {
            user.setPassword(MD5Util.encode(newPassword));
        }
        userDao.save(user);
        user.setPassword(StringUtils.EMPTY);//set return password is null
        return user;
    }

    public void deleteUser(Long id) {
        userDao.delete(id);
    }

    @Transactional
    public void logicDeleteUser(Long id, int isActive) {
        userDao.modifyIsActive(id, isActive);
    }


    public SysUser createUser(SysUser user) {
        userValidator(user);
        user.setPassword(MD5Util.encode(user.getPassword()));
        user.setIsActive(1);
        user.getRole().forEach(sysRole -> {
            String roleName = sysRole.getRoleName();
            if (!roleName.contains("ROLE_")) {
                sysRole.setRoleName("ROLE_" + roleName);
            }
        });
        userDao.save(user);
        user.setPassword(StringUtils.EMPTY);//set return password is null
        return user;
    }

    public Page<SysUserRoleVo> getAllUser(String username, PageRequest pageRequest) {

        List<SysUserRoleVo> sysUserRoleVos = Lists.newArrayList();
        Page<SysUser> sysUserList = null;

        if (StringUtils.isBlank(username)) {
            sysUserList = userDao.findAll(pageRequest);
        } else {
            sysUserList = userDao.getSysUserByUsernameLike(pageRequest, username);
        }
        sysUserList.getContent().forEach(sysUser -> {
            SysUserRoleVo sysUserRoleVo = new SysUserRoleVo();
            sysUserRoleVo.setName(sysUser.getName());
            sysUserRoleVo.setCreateTime(sysUser.getUpdateTime());
            sysUserRoleVo.setUserId(sysUser.getId());
            sysUserRoleVo.setUsername(sysUser.getUsername());
            sysUserRoleVo.setUserGroup(sysUser.getUserGroup());
            sysUserRoleVo.setPhone(sysUser.getPhone());
            sysUserRoleVo.setIsActive(sysUser.getIsActive());
            sysUserRoleVo.setEmail(sysUser.getEmail());
            sysUserRoleVo.setRole(sysUser.getRole());
            sysUserRoleVos.add(sysUserRoleVo);
        });
        return new PageImpl<>(sysUserRoleVos, pageRequest, sysUserRoleVos.size());
    }


    public SysRole createRole(SysRole role) {
        return roleDao.save(role);
    }

    public SysRole updateRole(SysRole role) {
        return roleDao.save(role);
    }

    public void deleteRole(Long id) {
        roleDao.delete(id);
    }

    public List<SysRole> getAllRoles() {
        return roleDao.findAll();
    }

    public SysResource createResource(SysResource resource) {
        return resourceDao.save(resource);
    }

    public SysResource updateResource(SysResource resource) {
        Objects.requireNonNull(resource.getId(), "Resource id should be specified.");
        return resourceDao.save(resource);
    }

    public Integer deleteResource(Long id) {
        return resourceDao.resourceIsShow(id, Show.Y);
    }

    public List<SysResource> getAllResources() {
        return resourceDao.findAll();
    }


    public Set<String> getUserGroups() {
        Set<String> userGroups = Sets.newHashSet();
        List<SysUser> sysUsers = userDao.findAll();
        for (SysUser sysUser : sysUsers) {
            if (StringUtils.isBlank(sysUser.getUserGroup())) {
                continue;
            }
            userGroups.add(sysUser.getUserGroup());
        }
        return userGroups;
    }

    public Set<String> getRoleNames() {
        Set<String> roleNames = Sets.newHashSet();
        List<SysRole> sysRoles = getAllRoles();
        sysRoles.forEach(sysRole -> {
            roleNames.add(sysRole.getRoleName());
        });
        return roleNames;
    }


    public Set<String> getUrls() {
        Set<String> urls = Sets.newHashSet();
        List<SysResource> sysResources = getAllResources();
        sysResources.forEach(resource -> {
            urls.add(resource.getUrl());
        });
        return urls;
    }

    public Set<String> getPermissions() {
        Set<String> permissions = Sets.newHashSet();
        List<SysResource> sysResources = getAllResources();
        sysResources.forEach(resource -> {
            permissions.add(resource.getPermission());
        });
        return permissions;
    }


}
