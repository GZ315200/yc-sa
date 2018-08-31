package com.unistack.tamboo.mgt.model.sys;

import com.google.common.base.MoreObjects;

import java.util.Set;

/**
 * @author Gyges Zean
 * @date 2018/5/18
 */
public class SysUserPermissionVo {

    private Long userId;
    private String username;
    private String userGroup;
    private Set<SysRole> roles;
    private Set<SysResource> permission;


    public SysUserPermissionVo() {
    }

    public SysUserPermissionVo(Long userId, String username, String userGroup, Set<SysRole> roles, Set<SysResource> permission) {
        this.userId = userId;
        this.username = username;
        this.userGroup = userGroup;
        this.roles = roles;
        this.permission = permission;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }

    public Set<SysRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<SysRole> roles) {
        this.roles = roles;
    }

    public Set<SysResource> getPermission() {
        return permission;
    }

    public void setPermission(Set<SysResource> permission) {
        this.permission = permission;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("userId", userId)
                .add("username", username)
                .add("userGroup", userGroup)
                .add("roles", roles)
                .add("permission", permission)
                .toString();
    }
}
