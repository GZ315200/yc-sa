package com.unistack.tamboo.mgt.model.sys;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Sets;
import com.unistack.tamboo.mgt.model.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * @author Gyges Zean
 * @date 2018/5/17
 */
@Entity
@Table(name = "sys_user")
public class SysUser extends BaseEntity {

    private Long id;
    private String userGroup; //用户组
    private String username;
    private String password;
    private String name;
    private String email;
    private String phone;
    private Integer isActive;

    private Set<SysRole> role = Sets.newHashSet();

    public SysUser() {
    }

    public SysUser(Long id, String userGroup, String username, String password, String email, String phone, Set<SysRole> role) {
        this.id = id;
        this.userGroup = userGroup;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.isActive = 1;
        this.role = role;
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @Column(name = "user_group")
    public String getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }

    @Column(name = "username")
    @NotNull
    public String getUsername() {
        return username;
    }


    @Column(name = "password")
    @NotNull
    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "sys_role_user", joinColumns = @JoinColumn(name = "user_id"))
    public Set<SysRole> getRole() {
        return role;
    }

    public void setRole(Set<SysRole> roles) {
        this.role = roles;
    }

    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "phone")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Column(name = "is_active")
    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SysUser sysUser = (SysUser) o;

        if (id != null ? !id.equals(sysUser.id) : sysUser.id != null) return false;
        if (userGroup != null ? !userGroup.equals(sysUser.userGroup) : sysUser.userGroup != null) return false;
        if (username != null ? !username.equals(sysUser.username) : sysUser.username != null) return false;
        if (password != null ? !password.equals(sysUser.password) : sysUser.password != null) return false;
        if (name != null ? !name.equals(sysUser.name) : sysUser.name != null) return false;
        if (email != null ? !email.equals(sysUser.email) : sysUser.email != null) return false;
        if (phone != null ? !phone.equals(sysUser.phone) : sysUser.phone != null) return false;
        if (isActive != null ? !isActive.equals(sysUser.isActive) : sysUser.isActive != null) return false;
        return role != null ? role.equals(sysUser.role) : sysUser.role == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (userGroup != null ? userGroup.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (isActive != null ? isActive.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("userGroup", userGroup)
                .add("username", username)
                .add("password", password)
                .add("email", email)
                .add("phone", phone)
                .add("isActive", isActive)
                .add("role", role)
                .toString();
    }
}

