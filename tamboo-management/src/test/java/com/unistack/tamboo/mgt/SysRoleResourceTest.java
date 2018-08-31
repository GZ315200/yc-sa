package com.unistack.tamboo.mgt;

import com.google.common.collect.Sets;
import com.unistack.tamboo.mgt.dao.sys.ResourceDao;
import com.unistack.tamboo.mgt.model.sys.SysResource;
import com.unistack.tamboo.mgt.model.sys.SysRole;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Set;

/**
 * @author Gyges Zean
 * @date 2018/5/18
 */

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class SysRoleResourceTest {


    @Autowired
    private ResourceDao resourceDao;


    @Test
    public void setResource() {
        SysResource resource = new SysResource();
        resource.setUrl("/permission");

        Set<SysRole> roles = Sets.newHashSet();

        SysRole role = new SysRole();

        role.setRoleName("admin");
        role.setId(1L);
        roles.add(role);

        resource.setRoles(roles);

        resourceDao.save(resource);
    }




}
