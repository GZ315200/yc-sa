package com.unistack.tamboo.mgt.model.sys;

import com.google.common.base.MoreObjects;
import com.unistack.tamboo.mgt.model.BaseEntity;

import javax.persistence.*;

/**
 * @program: tamboo-sa
 * @description: sys_user_group model
 * @author: Asasin
 * @create: 2018-05-23 17:52
 **/

@Entity
@Table(name = "sys_user_group")
public class SysUserGroup extends BaseEntity {
    private int groupId;
    private String groupName; //用户组名

    private String aclUsername;
    private String aclPassword;


    @Id
    @Column(name = "group_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    @Column(name = "group_name")
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }


    @Column(name = "acl_username")
    public String getAclUsername() {
        return aclUsername;
    }

    public void setAclUsername(String aclUsername) {
        this.aclUsername = aclUsername;
    }

    @Column(name = "acl_password")
    public String getAclPassword() {
        return aclPassword;
    }

    public void setAclPassword(String aclPassword) {
        this.aclPassword = aclPassword;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("groupId", groupId)
                .add("groupName", groupName)
                .add("aclUsername", aclUsername)
                .add("aclPassword", aclPassword)
                .toString();
    }
}
    