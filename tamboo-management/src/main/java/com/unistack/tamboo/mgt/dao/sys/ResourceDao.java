package com.unistack.tamboo.mgt.dao.sys;

import com.unistack.tamboo.mgt.dao.BaseDao;
import com.unistack.tamboo.mgt.model.sys.SysResource;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Gyges Zean
 * @date 2018/5/17
 */
public interface ResourceDao extends BaseDao<SysResource, Long> {


    @Modifying
    @Query("update SysResource rs set rs.isShow =:is_show where rs.id =:resource_id")
    Integer resourceIsShow(@Param("resource_id") Long resourceId, @Param("is_show") Integer isShow);

    @Override
	List<SysResource> findAll();
}
