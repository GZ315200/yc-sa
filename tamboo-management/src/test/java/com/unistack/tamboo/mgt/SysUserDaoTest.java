package com.unistack.tamboo.mgt;



import com.unistack.tamboo.mgt.dao.sys.RoleDao;
import com.unistack.tamboo.mgt.dao.sys.UserDao;
import com.unistack.tamboo.mgt.model.sys.SysRole;
import com.unistack.tamboo.mgt.model.sys.SysUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Gyges Zean
 * @date 2018/5/17
 */

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class SysUserDaoTest {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;


    @Test
    public void getUserList() {

        System.out.println(userDao.findAll());
    }

    /**
     * 级联插入
     */
    @Test
    public void setUserList() {
        SysUser sysUser = new SysUser();
        sysUser.setUsername("admin");
        sysUser.setPassword("admin123");
        SysRole role = new SysRole();

        role.setRoleName("ROLE_ADMIN");

        Set<SysRole> roles = new HashSet<>();
        roles.add(role);
        sysUser.setRole(roles);
        userDao.save(sysUser);
//        SysRole role = new SysRole();


//        Set<SysResource> resources = new HashSet<>();
//        SysResource resource = new SysResource();
//        resource.setUrl("/permission");
//        resource.setIsShow(1);
//        resources.add(resource);
//        role.setResource(resources);
//        roleDao.save(role);
    }






}
