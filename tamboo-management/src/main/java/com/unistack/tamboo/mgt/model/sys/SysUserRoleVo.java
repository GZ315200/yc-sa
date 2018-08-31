package com.unistack.tamboo.mgt.model.sys;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Sets;

import java.util.Date;
import java.util.Set;


/**
 * @author Gyges Zean
 * @date 2018/6/12
 */
public class SysUserRoleVo {

    private Long userId;

    private String userGroup;

    private String username;

    private String email;

    private String phone;

    private String name;

    private Integer isActive;

    private Set<SysRole> role = Sets.newHashSet();

    private Date createTime;

    public SysUserRoleVo() {
    }


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Set<SysRole> getRole() {
        return role;
    }

    public void setRole(Set<SysRole> role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("userId", userId)
                .add("userGroup", userGroup)
                .add("username", username)
                .add("email", email)
                .add("phone", phone)
                .add("name", name)
                .add("isActive", isActive)
                .add("role", role)
                .add("createTime", createTime)
                .toString();
    }
}
