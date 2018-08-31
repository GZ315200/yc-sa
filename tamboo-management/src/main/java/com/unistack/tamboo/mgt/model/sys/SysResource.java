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
@Table(name = "sys_resource")
public class SysResource extends BaseEntity {


    private Long id;
    private Set<SysRole> roles = Sets.newHashSet();
    private String url;
    private String permission;
    private Integer isShow = 1;//默认开启


    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "url")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Column(name = "permission")
    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    @Column(name = "is_show")
    public Integer getIsShow() {
        return isShow;
    }

    public void setIsShow(Integer isShow) {
        this.isShow = isShow;
    }


    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "resource")
    @Transient
    public Set<SysRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<SysRole> roles) {
        this.roles = roles;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SysResource that = (SysResource) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (roles != null ? !roles.equals(that.roles) : that.roles != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        if (permission != null ? !permission.equals(that.permission) : that.permission != null) return false;
        return isShow != null ? isShow.equals(that.isShow) : that.isShow == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (roles != null ? roles.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (permission != null ? permission.hashCode() : 0);
        result = 31 * result + (isShow != null ? isShow.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("roles", roles)
                .add("url", url)
                .add("permission", permission)
                .add("isShow", isShow)
                .toString();
    }
}
