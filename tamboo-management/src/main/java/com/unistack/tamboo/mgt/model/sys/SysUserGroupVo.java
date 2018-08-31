package com.unistack.tamboo.mgt.model.sys;

import com.google.common.base.MoreObjects;

/**
 * @author Gyges Zean
 * @date 2018/6/12
 */
public class SysUserGroupVo {

    private int groupId;

    private String groupName;

    public SysUserGroupVo() {
    }

    public SysUserGroupVo(int groupId, String groupName) {
        this.groupId = groupId;
        this.groupName = groupName;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("groupId", groupId)
                .add("groupName", groupName)
                .toString();
    }
}
