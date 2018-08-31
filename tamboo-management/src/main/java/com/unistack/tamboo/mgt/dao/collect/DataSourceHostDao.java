package com.unistack.tamboo.mgt.dao.collect;

import com.unistack.tamboo.mgt.dao.BaseDao;
import com.unistack.tamboo.mgt.model.collect.DataSourceHost;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @program: tamboo-sa
 * @description:
 * @author: Asasin
 * @create: 2018-07-19 11:14
 **/
public interface DataSourceHostDao extends BaseDao<DataSourceHost, Integer>, JpaSpecificationExecutor<DataSourceHost> {

    @Query(value = "select a from DataSourceHost a where a.installFlag=1 and a.ip =:ip")
    DataSourceHost getByIps(@Param("ip") String ip);

    @Query(value = "select a from DataSourceHost a where a.installFlag=1 and a.hostname =:hostname")
    DataSourceHost getByHostname(@Param("hostname") String hostname);


    /**
     * 修改指定host的flag
     *
     * @param hostId
     * @param installFlag
     * @return
     */
    @Modifying
    @Transactional
    @Query("update DataSourceHost d set d.installFlag= :installFlag where d.id=:hostId")
    void setFlag(@Param("hostId") Integer hostId, @Param("installFlag") int installFlag);

    DataSourceHost getDataSourceHostByHostname(String hostName);

    DataSourceHost getDataSourceHostsById(int hostId);

    @Query(value = "select a from DataSourceHost a where a.hostname not like 'localhost'")
    List<DataSourceHost> getDataSourceHosts();
}
    