package com.unistack.tamboo.mgt.model.sys;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Sets;
import com.unistack.tamboo.mgt.model.BaseEntity;

import javax.persistence.*;
import java.util.Set;

/**
 * @author Gyges Zean
 * @date 2018/5/17
 */
@Entity
@Table(name = "sys_role")
public class SysRole extends BaseEntity {

    private Long id;
    private String roleName;

    private Set<SysUser> users = Sets.newHashSet();

    private Set<SysResource> resource = Sets.newHashSet();


    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "role_name")
    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @ManyToMany(mappedBy = "role", fetch = FetchType.LAZY
            , cascade = CascadeType.ALL)
    @Transient
    public Set<SysUser> getUsers() {
        return users;
    }

    public void setUsers(Set<SysUser> users) {
        this.users = users;
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "sys_role_resource", joinColumns = @JoinColumn(name = "role_id"))
    public Set<SysResource> getResource() {
        return resource;
    }

    public void setResource(Set<SysResource> resource) {
        this.resource = resource;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SysRole sysRole = (SysRole) o;

        if (id != null ? !id.equals(sysRole.id) : sysRole.id != null) return false;
        if (roleName != null ? !roleName.equals(sysRole.roleName) : sysRole.roleName != null) return false;
        if (users != null ? !users.equals(sysRole.users) : sysRole.users != null) return false;
        return resource != null ? resource.equals(sysRole.resource) : sysRole.resource == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (roleName != null ? roleName.hashCode() : 0);
        result = 31 * result + (users != null ? users.hashCode() : 0);
        result = 31 * result + (resource != null ? resource.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("roleName", roleName)
                .add("users", users)
                .add("resource", resource)
                .toString();
    }
}
