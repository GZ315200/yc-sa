package com.unistack.tamboo.mgt.dao.collect;


import com.unistack.tamboo.mgt.dao.BaseDao;
import com.unistack.tamboo.mgt.model.collect.DataSourceList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DataSourceListDao extends BaseDao<DataSourceList, Long> {


    /**
     * @param pageable
     * @return
     */
    Page<DataSourceList> getAllByCreateByIn(Pageable pageable, List<String> userNames);

    /**
     * 根据dataSourceName和query关键字搜索dataSource
     *
     * @param pageable
     * @param query
     * @return
     */
    @Query("select d from DataSourceList d where d.dataSourceType like %:queryType% and createBy in (:userNames)")
    Page<DataSourceList> getDataSourceListByQueryType(Pageable pageable, @Param("queryType") String queryType, @Param("userNames")List<String> userNames);

    @Query("select d from DataSourceList d where d.dataSourceName like %:queryName% and createBy in (:userNames)")
    Page<DataSourceList> getDataSourceListByQueryName(Pageable pageable, @Param("queryName") String queryName, @Param("userNames")List<String> userNames);

    @Query("select d from DataSourceList d where d.dataSourceType like %:queryType% and d.dataSourceName like %:queryName% and createBy in (:userNames)")
    Page<DataSourceList> getDataSourceListByQuery(Pageable pageable, @Param("queryType") String queryType,@Param("queryName")String queryName, @Param("userNames")List<String> userNames);

    /**
     * 查询指定dataSourceId的dataSource详细信息
     *
     * @param dataSourceId
     * @return
     */
    DataSourceList getDataSourceListByDataSourceId(Long dataSourceId);



    DataSourceList getDataSourceListByDataSourceType(String dataSourceType);


    /**
     * 根据topicId查询dataSourceId
     *
     * @param topicId
     * @return
     */
    DataSourceList getDataSourceListByTopicId(Long topicId);

    /**
     * 修改指定dataSourceId的flag
     * @param dataSourceId
     * @param flag
     * @return
     */
    @Modifying
    @Transactional
    @Query("update DataSourceList d set d.flag= :flag where d.dataSourceId=:dataSourceId")
    int setFlag(@Param("dataSourceId")Long dataSourceId, @Param("flag")int flag);

    /**
     * 统计不同类型的dataSource数量
     * @param type
     * @return
     */
    int countDataSourceListByDataSourceType(String type);

    int countDataSourceListByDataSourceTypeIn(List<String> list);

    int countDataSourceListByFlag(int flag);

    int countDataSourceListByDataSourceName(String dataSourceName);

    int countDataSourceListByCreateByIn(List<String> userNames);

    int countDataSourceListByFlagAndCreateByIn(int flag,List<String> userNames);

    DataSourceList getDataSourceListByDataSourceName(String DataSourceName);

    List<DataSourceList> getDataSourceListByFlag(int flag);

    @Query("select d from DataSourceList d where d.flag=:flag and d.dataSourceType not in (:types)")
    List<DataSourceList> getDataSourceListByFlumeFlag(@Param("flag")int flag,@Param("types")String[] types);

    @Query("select d from DataSourceList d where d.flag=:flag and d.dataSourceType in (:types)")
    List<DataSourceList> getDataSourceListByFlagConnectFlag(@Param("flag")int flag,@Param("types")String[] types);

    List<DataSourceList> getDataSourceListByHostId(int hostId);
}