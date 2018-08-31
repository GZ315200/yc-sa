package com.unistack.tamboo.mgt.dao.calc;

import com.unistack.tamboo.mgt.dao.BaseDao;
import com.unistack.tamboo.mgt.model.calc.OfflineDataSource;
import com.unistack.tamboo.mgt.model.sys.SysUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface OfflineDataSourceDao extends BaseDao<OfflineDataSource,Integer> {

//    @Query(value = "select d from OfflineDataSource d where  d.dataSourceName like %:dataSourceName%")
//    Page<OfflineDataSource> getOfflineDataSource(Pageable pageable, @Param("dataSourceName") String dataSourceName);


    /**
     * 获取某个用户所在的组
     * @param username
     * @return
     */
    @Query(value="select distinct su.userGroup from SysUser su where su.username = :username and su.userGroup is not null")
    List<SysUser> getUserGroup(@Param("username") String username);

    /**
     * 1、根据用户组名查出数据源id
     * 2、通过数据源id查出该用户可以查看的所有数据源
     *
     *
     *    @Query(value = "select ds from OfflineDataSource ds where ds.dataSourceId in (select distinct odsa.dataSourceId from OfflineDataSourceAuth odsa where odsa.userGroup = :userGroup) and ds.dataSourceName like %:dataSourceName%")
    List<OfflineDataSource> getOfflineDataSourceWithSearch(@Param("userGroup") String userGroup,@Param("dataSourceName") String dataSourceName);


     @Query(value = "select ds from OfflineDataSource ds where ds.dataSourceId in (select distinct odsa.dataSourceId from OfflineDataSourceAuth odsa where odsa.userGroup = :userGroup)")
     List<OfflineDataSource> getOfflineDataSource(@Param("userGroup") String userGroup);
     * @param
     * @return
     */


    @Query(value = "select ds from OfflineDataSource ds where ds.dataSourceId in (select distinct odsa.dataSourceId from OfflineDataSourceAuth odsa where odsa.userGroup in (select distinct su.userGroup from SysUser su where su.username = :username and su.userGroup is not null)) and ds.dataSourceName like %:dataSourceName%")
    Page<OfflineDataSource> getOfflineDataSourceWithSearch(Pageable pageable,@Param("username") String username,@Param("dataSourceName") String dataSourceName);


    @Query(value = "select ds from OfflineDataSource ds where ds.dataSourceId in (select distinct odsa.dataSourceId from OfflineDataSourceAuth odsa where odsa.userGroup in(select distinct su.userGroup from SysUser su where su.username = :username and su.userGroup is not null))")
    Page<OfflineDataSource> getOfflineDataSource(Pageable pageable,@Param("username") String username);

    /**
     * 通过类型查找数据源
     * @param dbtype 类型
     * @return
     */
    List<OfflineDataSource> findByDbTypeIgnoreCase(String dbtype);

    List<OfflineDataSource> findByDataSourceNameIgnoreCase(String dataSourceName);

}