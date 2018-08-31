package com.unistack.tamboo.mgt.controller.sys;

import com.unistack.tamboo.mgt.common.ServerResponse;
import com.unistack.tamboo.mgt.common.page.PageConvert;
import com.unistack.tamboo.mgt.common.page.PaginationData;
import com.unistack.tamboo.mgt.controller.BaseController;
import com.unistack.tamboo.mgt.model.sys.*;
import com.unistack.tamboo.mgt.service.sys.SecurityService;
import com.unistack.tamboo.mgt.utils.SecUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @author Gyges Zean
 * @date 2018/5/17
 */

@RestController
@RequestMapping(value = "/system")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SecurityController extends BaseController {

    public static  Logger log = LoggerFactory.getLogger(SecurityController.class);

    @Autowired
    private SecurityService securityService;

    /**
     * ******************************
     * * Relation to User operation *
     * ******************************
     **/

    @RequestMapping(value = "/user/create", method = RequestMethod.POST)
    @Secured({"ROLE_ADMIN"})
    public ServerResponse createUser(@RequestBody SysUser sysUser) {
        try {
            boolean isExist = usernameIsExist(sysUser.getUsername());
            if (isExist) {
                return ServerResponse.createByErrorMsg("username is exist.");
            } else return ServerResponse.createBySuccess(securityService.createUser(sysUser));
        } catch (Exception e) {
            log.error("", e);
            return ServerResponse.createByErrorMsg(e.getMessage());
        }
    }


    @RequestMapping(value = "/user/update", method = RequestMethod.POST)
    @Secured({"ROLE_ADMIN"})
    public ServerResponse updateUser(@RequestBody SysUser sysUser) {
        try {
            return ServerResponse.createBySuccess(securityService.updateUser(sysUser));
        } catch (Exception e) {
            log.error("", e);
            return ServerResponse.createByErrorMsg(e.getMessage());
        }
    }

    @RequestMapping(value = "/user/delete", method = RequestMethod.GET)
    @Secured({"ROLE_ADMIN"})
    public ServerResponse deleteUser(@RequestParam(value = "user_id") Long id,
                                     @RequestParam(value = "is_active", defaultValue = "1") int active) {
        securityService.logicDeleteUser(id, active);
        return ServerResponse.createBySuccess();
    }

    @RequestMapping(value = "/users/get", method = RequestMethod.GET)
    @Secured({"ROLE_ADMIN"})
    public ServerResponse<PaginationData<SysUserRoleVo>> allUsers(@RequestParam(value = "username", required = false) String username,
                                                                  @RequestParam(value = "pageIndex", defaultValue = "1") int pageIndex,
                                                                  @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        PageRequest request = new PageRequest(pageIndex - 1, pageSize, sort);
        PaginationData<SysUserRoleVo> paginationData =
                PageConvert.convertJpaPage2PaginationData(securityService.getAllUser(username, request), pageSize);

        return ServerResponse.createBySuccess(paginationData);
    }


    /**
     * ******************************
     * * Relation to Role operation *
     * ******************************
     **/

    @RequestMapping(value = "/role/create", method = RequestMethod.POST)
    public ServerResponse createRole(@RequestBody SysRole sysRole) {
        return ServerResponse.createBySuccess(securityService.createRole(sysRole));
    }

    @RequestMapping(value = "/role/update", method = RequestMethod.POST)
    public ServerResponse updateRole(@RequestBody SysRole sysRole) {
        return ServerResponse.createBySuccess(securityService.updateRole(sysRole));
    }

    @RequestMapping(value = "/role/delete", method = RequestMethod.POST)
    public ServerResponse deleteRole(@RequestParam(value = "role_id") Long id) {
        securityService.deleteRole(id);
        return ServerResponse.createBySuccess();
    }

    @RequestMapping(value = "/allRoles/get", method = RequestMethod.GET)
    public ServerResponse<List<SysRole>> allRoles() {
        return ServerResponse.createBySuccess(securityService.getAllRoles());
    }


    /**
     * **********************************
     * * Relation to Resource operation *
     * **********************************
     **/
    @RequestMapping(value = "/resource/create", method = RequestMethod.POST)
    public ServerResponse createRole(@RequestBody SysResource resource) {
        return ServerResponse.createBySuccess(securityService.createResource(resource));
    }

    @RequestMapping(value = "/resource/update", method = RequestMethod.POST)
    public ServerResponse updateRole(@RequestBody SysResource resource) {
        return ServerResponse.createBySuccess(securityService.updateResource(resource));
    }

    @RequestMapping(value = "/resource/delete", method = RequestMethod.POST)
    public ServerResponse deleteResource(@RequestParam(value = "resource_id") Long id) {
        return ServerResponse.createBySuccess(securityService.deleteResource(id));
    }

    @RequestMapping(value = "/allResources/get", method = RequestMethod.GET)
    public ServerResponse allResources() {
        return ServerResponse.createBySuccess(securityService.getAllResources());
    }


    /**
     * list role names
     *
     * @return
     */
    @RequestMapping(value = "/roleNames/get", method = RequestMethod.GET)
    public ServerResponse<Set<String>> listRoleNames() {
        return ServerResponse.createBySuccess(securityService.getRoleNames());
    }

    /**
     * list permissions
     *
     * @return
     */
    @RequestMapping(value = "/permission/get", method = RequestMethod.GET)
    public ServerResponse<Set<String>> listPermissions() {
        return ServerResponse.createBySuccess(securityService.getPermissions());
    }

    /**
     * list urls
     *
     * @return
     */
    @RequestMapping(value = "/urls/get", method = RequestMethod.GET)
    public ServerResponse<Set<String>> listUrls() {
        return ServerResponse.createBySuccess(securityService.getUrls());
    }


    @RequestMapping(value = "/userGroup/get", method = RequestMethod.GET)
    @Secured({"ROLE_ADMIN"})
    public ServerResponse<Set<String>> getUserGroups() {
        return ServerResponse.createBySuccess(SecUtils.getAllUserGroups());
    }

    @RequestMapping(value = "/userGroup/getAll", method = RequestMethod.GET)
    @Secured({"ROLE_ADMIN"})
    public ServerResponse<List<SysUserGroup>> getAllGroups() {
        return ServerResponse.createBySuccess(SecUtils.getAllUserGroup());
    }


    @RequestMapping(value = "/userGroup/create", method = RequestMethod.POST)
    @Secured({"ROLE_ADMIN"})
    public ServerResponse createUserGroup(@RequestBody SysUserGroup sysUserGroup) {
        try {
            return ServerResponse.createBySuccess(SecUtils.create(sysUserGroup));
        } catch (Exception e) {
            log.error("", e);
            return ServerResponse.createByErrorMsg(e.getMessage());
        }
    }


    @RequestMapping(value = "/userGroup/update", method = RequestMethod.POST)
    @Secured({"ROLE_ADMIN"})
    public ServerResponse updateUserGroup(@RequestBody SysUserGroup sysUserGroup) {
        return ServerResponse.createBySuccess(SecUtils.update(sysUserGroup));
    }


    @RequestMapping(value = "/userGroup/delete", method = RequestMethod.GET)
    @Secured({"ROLE_ADMIN"})
    public ServerResponse delUserGroup(@RequestParam Integer groupId) {
        SecUtils.delUserGroup(groupId);
        return ServerResponse.createBySuccess();
    }


}
