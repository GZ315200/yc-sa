package com.unistack.tamboo.mgt;

import com.unistack.tamboo.mgt.dao.collect.DataSourceListDao;
import com.unistack.tamboo.mgt.service.collect.DataSourceServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @program: tamboo-sa
 * @description: 数据源test类
 * @author: Asasin
 * @create: 2018-07-13 12:17
 **/
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class DataSourceTest {

  @Autowired
  private DataSourceListDao dataSourceListDao;

  @Autowired
  private DataSourceServiceImpl dataSourceService;

  @Test
  public void getDataSourceListByFlag(){
      String [] types = new String[]{"MYSQL","ORACLE","SQLSERVER","PG","HBASE"};
     // List<DataSourceList> lists = dataSourceListDao.getDataSourceListByFlagAndType(DataSourceEnum.DataSourceState.STARTED.getValue(),types);
     /* for (DataSourceList dataSourceList:lists){
          System.out.println(dataSourceList);
      }*/
//      String [] types = new String[]{"MYSQL","ORACLE","SQLSERVER","PG","HBASE"};
//      List<DataSourceList> lists = dataSourceListDao.getDataSourceListByFlagAndType(DataSourceEnum.DataSourceState.STARTED.getValue(),types);
//      for (DataSourceList dataSourceList:lists){
//          System.out.println(dataSourceList);
//      }
  }

    @Test
    public void webSocket(){
        dataSourceService.webSocket();
    }
}
    