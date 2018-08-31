package com.unistack.tamboo.mgt.controller.sys;

import com.unistack.tamboo.mgt.common.ServerResponse;
import com.unistack.tamboo.mgt.controller.BaseController;
import com.unistack.tamboo.mgt.model.sys.SysUser;
import com.unistack.tamboo.mgt.model.sys.SysUserPermissionVo;
import com.unistack.tamboo.mgt.service.sys.SecurityService;
import com.unistack.tamboo.mgt.utils.SecUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

import static com.unistack.tamboo.mgt.common.ServerResponse.createByErrorMsg;
import static com.unistack.tamboo.mgt.common.ServerResponse.createBySuccess;

/**
 * @author Gyges Zean
 * @date 2018/5/17
 */
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class LoginController extends BaseController {

    public static  Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private SecurityService securityService;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public Map<String, String> index() {
        return System.getenv();
    }


    /**
     * login and return user with permission
     *
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ServerResponse<SysUserPermissionVo> login(@RequestBody SysUser sysUser) {

        if (usernameIsExist(sysUser.getUsername())) {
            if (userIsActive(sysUser.getUsername())) {
                securityService.autoLogin(sysUser.getUsername(), sysUser.getPassword());
                return createBySuccess(SecUtils.getUserPermissionByUsername(sysUser.getUsername()));
            } else return createByErrorMsg("this user is not active, please contact with system admin.");
        } else return createByErrorMsg("username is not exist.");
    }


    @RequestMapping(value = "/user/logout", method = RequestMethod.GET)
    public ServerResponse<String> logout(HttpServletRequest request) {
        HttpSession session;
        SecurityContextHolder.clearContext();
        session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            cookie.setMaxAge(0);
        }
        log.info("logout successfully.");
        return createBySuccess("logout successfully.");
    }

}
