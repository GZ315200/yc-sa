package com.unistack.tamboo.mgt.controller;

import com.unistack.tamboo.mgt.model.sys.SysUser;
import com.unistack.tamboo.mgt.utils.SecUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @author Gyges Zean
 * @date 2018/5/17
 */
public abstract class BaseController {


    public interface Type {
        int DAY = 60 * 24 * 2;
        int WEEK = 60 * 24 * 14;
    }


    protected boolean usernameIsExist(String username) {
        SysUser sysUser = SecUtils.userNameIsExist(username);
        return !Objects.isNull(sysUser);
    }

    protected boolean userIsActive(String username) {
        SysUser sysUser = SecUtils.userNameIsExist(username);
        return sysUser.getIsActive() == 1;
    }

    /**
     * 处理Spring对空值字符串识别为“null”字符串的问题
     * @param springValue spring框架解析的值
     * @return
     */
    public String getFromSpringStringParam(String springValue) {
        if (StringUtils.isBlank(springValue)) {
            return springValue;
        }
        if (StringUtils.equals("null",springValue)) {
            return null;
        }
        return springValue;
    }

}
