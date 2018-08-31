package com.unistack.tamboo.mgt;

import com.unistack.tamboo.mgt.model.sys.SysRole;
import com.unistack.tamboo.mgt.model.sys.SysUser;
import com.unistack.tamboo.mgt.service.sys.SecurityService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Gyges Zean
 * @date 2018/5/18
 */

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class SysUserServiceTest {


    @Autowired
    private SecurityService securityService;


    @Test
    public void createUser() {
        SysUser sysUser = new SysUser();
//        user.setUserType(EUserType.CUSTOM_USER.getName());
        sysUser.setUsername("admin");
        sysUser.setPassword("2241883");
        sysUser.setId(2L);

        SysRole role = new SysRole();

        role.setRoleName("ADMIN");

        Set<SysRole> roles = new HashSet<>();
        roles.add(role);
        sysUser.setRole(roles);

        securityService.updateUser(sysUser);
    }


    @Test
    public void deleteUser() {
        securityService.deleteUser(5L);
    }


}
