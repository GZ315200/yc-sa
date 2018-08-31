package com.unistack.tamboo.mgt.model.sys;

import com.google.common.base.MoreObjects;

/**
 * @author Gyges Zean
 * @date 2018/5/29
 * 封装用户的session
 */
public class UserSession {

    private String username;

    private Long userId;

    private String userGroup;

    private int userGroupId;

    public UserSession() {
    }

    public UserSession(String username, Long userId, String userGroup) {
        this.username = username;
        this.userId = userId;
        this.userGroup = userGroup;
    }

    public int getUserGroupId() {
        return userGroupId;
    }

    public void setUserGroupId(int userGroupId) {
        this.userGroupId = userGroupId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("username", username)
                .add("userId", userId)
                .add("userGroup", userGroup)
                .add("userGroupId", userGroupId)
                .toString();
    }
}
