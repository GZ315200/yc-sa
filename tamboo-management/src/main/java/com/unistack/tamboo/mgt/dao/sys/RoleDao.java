package com.unistack.tamboo.mgt.dao.sys;

import com.unistack.tamboo.mgt.dao.BaseDao;
import com.unistack.tamboo.mgt.model.sys.SysRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author Gyges Zean
 * @date 2018/5/17
 */
public interface RoleDao extends BaseDao<SysRole, Long> {

    SysRole getSysRoleById(Long roleId);

    @Override
    Page<SysRole> findAll(Pageable pageable);
}
