package com.unistack.tamboo.mgt;

import com.unistack.tamboo.mgt.model.sys.SysRole;
import com.unistack.tamboo.mgt.service.sys.SecurityService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Gyges Zean
 * @date 2018/5/20
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class SysRoleServiceTest {


    @Autowired
    private SecurityService securityService;


    @Test
    public void createRoles() {

        SysRole role = new SysRole();
        role.setRoleName("ADMIN");
        role.setRemarks("用于...");
        securityService.createRole(role);
    }


}
