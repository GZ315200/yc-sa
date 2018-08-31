package com.unistack.tamboo.mgt.service.calc;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.unistack.tamboo.commons.utils.calc.DbMetaDataUtil;
import com.unistack.tamboo.mgt.common.ServerResponse;
import com.unistack.tamboo.mgt.dao.calc.OfflineDataSourceAuthDao;
import com.unistack.tamboo.mgt.dao.calc.OfflineDataSourceDao;
import com.unistack.tamboo.mgt.dao.calc.SysUserDao;
import com.unistack.tamboo.mgt.model.calc.OfflineDataSource;
import com.unistack.tamboo.mgt.model.calc.OfflineDataSourceAuth;
import com.unistack.tamboo.mgt.model.sys.SysUser;
import com.unistack.tamboo.mgt.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OfflineDataSourceServiceImpl extends BaseService {

    @Autowired
    private OfflineDataSourceDao dsDao;
    @Autowired
    private SysUserDao sysUserDao;
    @Autowired
    private OfflineDataSourceAuthDao odsaDao;

    /**
     * 离线数据源保存
     * 1、保存离线数据源本身
     * 2、保存离线数据源和用户组的关系
     * ？？？目前还没有支持事物
     * @param dataSource
     * @return
     */
    public ServerResponse<OfflineDataSource> saveOfflineDataSource(OfflineDataSource dataSource){
        try{
            //1、保存离线数据源
            dataSource.setAddTime(new Timestamp(System.currentTimeMillis()));
            dataSource.setIsActive("1");
            OfflineDataSource ds = dsDao.save(dataSource);

            //2、保存离线数据源及用户组关系
            OfflineDataSourceAuth odsa = new OfflineDataSourceAuth();
            odsa.setCreator(dataSource.getCreator());
            odsa.setIsActive("1");
            Timestamp time = new Timestamp(System.currentTimeMillis());
            odsa.setAddTime(time);
            odsa.setUpdateTime(time);
            odsa.setDataSourceId(ds.getDataSourceId());
            //2.1 查询该用户对应的用户组
            SysUser sysUser = sysUserDao.getSysUserByUsername(ds.getCreator());
            odsa.setUserGroup(sysUser.getUserGroup());
            odsaDao.save(odsa);
            return ServerResponse.createBySuccess("离线数据源保存成功",ds);
        }catch (Exception e){
            return ServerResponse.createByErrorMsg("离线数据源保存失败");
        }
    }

    /**
     * 根据用户名(判断权限) 和 模糊查询  获取数据源列表 (只有有权限的才能查看)
     */
    public Page<OfflineDataSource> getOfflineDataSource(Pageable pageable,String username,String dataSourceName){
        return StringUtils.isBlank(dataSourceName)?dsDao.getOfflineDataSource(pageable,username):dsDao.getOfflineDataSourceWithSearch(pageable,username,dataSourceName);
    }


    public ServerResponse<?> deleteOfflineDataSource(Integer id){

        try{
            OfflineDataSource one = dsDao.getOne(id);
            dsDao.delete(id);
            int dataSourceId = one.getDataSourceId();
            SysUser sysUser = sysUserDao.getSysUserByUsername(one.getCreator());
            odsaDao.deleteOfflineDataSourceAuth(sysUser.getUserGroup(), dataSourceId);
            return ServerResponse.createBySuccess("删除成功");
        }catch(Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMsg("删除失败!");
        }
    }


    /**
     * 用于测试数据库连接
     * @param source 数据库连接信息
     * @return
     */
    public JSONObject dbConnectionTest(OfflineDataSource source){
        JSONObject result = new JSONObject();
        Connection conn = null;
        try{
            String url = source.toUrl();
            String username = source.getUsername();
            String password = source.getPassword();

            if(StringUtils.isBlank(url) || StringUtils.isBlank(username) || StringUtils.isBlank(password)){
                result.put("code","199");
                result.put("msg","url/username/password均不能为空!");
                return result;
            }

            String dbType = source.getDbType();
            List<String> currentSupportDbTypeList = Arrays.asList(new String[]{"MYSQL","ORACLE","SQLSERVER"});
            if(!currentSupportDbTypeList.contains(dbType)){
                result.put("code","198");
                result.put("msg","数据类型["+dbType+"]不支持!");
                return result;
            }

            String driver = "MYSQL".equals(dbType)?"com.mysql.jdbc.Driver":("ORACLE".equals(dbType)?"oracle.jdbc.driver.OracleDriver":"com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
            result.put("code","200");
            result.put("msg","连接成功");
            return result;
        }catch(ClassNotFoundException e){
            result.put("code","196");
            result.put("msg","没有该驱动程序!");
            return result;
        }catch (SQLException e){
            result.put("code","196");
            result.put("msg","获取连接失败,请检查url/username/password");
            return result;
        }finally{
            if(null != conn){try{conn.close();}catch(SQLException e){e.printStackTrace();}}
        }
    }

    /**
     *  根据type查询离线数据源
     */
    public ServerResponse<List<String>> getOfsListByType(String type){
        List<OfflineDataSource> list = dsDao.findByDbTypeIgnoreCase(type);
        List<String> dsNameList = list.stream().map(OfflineDataSource::getDataSourceName).collect(Collectors.toList());
        return ServerResponse.createBySuccess(dsNameList);
    }


    /**
     * 根据数据源获取该数据源中的表名
     * @param source
     * @return
     */
    public ServerResponse<List<String>> getTablesByDatabase(OfflineDataSource source){
        Connection conn = null;
        List<String> result  = Lists.newArrayList();
        try{
            String dbType = source.getDbType();
            String driver = "MYSQL".equals(dbType)?"com.mysql.jdbc.Driver":("ORACLE".equals(dbType)?"oracle.jdbc.driver.OracleDriver":"com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Class.forName(driver);
            conn = DriverManager.getConnection(source.toUrl(),source.getUsername(),source.getPassword());
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getTables(conn.getCatalog(), "%", "%", new String[]{"TABLE"});
            while(rs.next()){
                String table_name = rs.getString("TABLE_NAME");
                result.add(table_name);
            }
        }catch(Exception e){
            return ServerResponse.createByErrorMsg("查询异常!");
        }finally{
            if(null != conn){try{conn.close();}catch(SQLException e){e.printStackTrace();}}
        }
        return  ServerResponse.createBySuccess(result);
    }


    /**
     * 根据数据源信息获取表的字段信息
     *
     * @param dataSourceInfo => {"dbType":"MYSQL","username":"root","password":"welcome1","tableName":"xx"}
     * @return
     */
    public ServerResponse<List<JSONObject>> getColumns(JSONObject dataSourceInfo){
        Optional<List<JSONObject>> columnInfo = DbMetaDataUtil.getColumnInfo(dataSourceInfo);
        return (!columnInfo.isPresent() || columnInfo.get().isEmpty()) ? ServerResponse.createByError() :ServerResponse.createBySuccess(columnInfo.get());
    }
}