package com.unistack.tamboo.mgt.service.collect;

import com.google.common.collect.Lists;
import com.unistack.tamboo.commons.utils.Const;
import com.unistack.tamboo.mgt.common.ServerResponse;
import com.unistack.tamboo.mgt.dao.collect.DataFlowInfoDao;
import com.unistack.tamboo.mgt.dao.collect.DataSourceGroupDao;
import com.unistack.tamboo.mgt.dao.collect.SourceTableDao;
import com.unistack.tamboo.mgt.dao.sys.UserDao;
import com.unistack.tamboo.mgt.dao.sys.UserGroupDao;
import com.unistack.tamboo.mgt.model.collect.*;
import com.unistack.tamboo.mgt.model.sys.SysUser;
import com.unistack.tamboo.mgt.utils.CharacterUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @program: tamboo-sa
 * @description: 资源管理服务实现类
 * @author: Asasin
 * @create: 2018-05-23 19:23
 **/
@Service
public class ResourceServiceImpl {

    @Autowired
    private DataSourceGroupDao dataSourceGroupDao;

    @Autowired
    private DataFlowInfoDao dataFlowInfoDao;

    @Autowired
    private UserGroupDao userGroupDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private SourceTableDao sourceTableDao;


    /**
     * 查询集群存储和计算资源
     *
     * @return
     */
    public ServerResponse<List<SourceTable>> list() {
        return ServerResponse.createBySuccess(sourceTableDao.findAll());
    }

    /**
     * 所有用户组分配资源list
     *
     * @return
     */
    public ServerResponse<List<DataSourceGroup>> getAll() {
        return ServerResponse.createBySuccess(dataSourceGroupDao.findAll());
    }

    /**
     * 用户组资源管理分配修改
     *
     * @param dataFlowInfoGU
     * @return
     */
    public ServerResponse edit(DataFlowInfoGU dataFlowInfoGU) {
        if (StringUtils.isBlank(dataFlowInfoGU.getGroupName())) {
            return ServerResponse.createByErrorMsg(Const.ErrorMessage.NULL_PARAM_INPUT.toString());
        }
        DataSourceGroup dataSourceGroup1 = dataSourceGroupDao.findOne(dataFlowInfoGU.getGroupName());
        dataSourceGroup1.setCalcSourceCpu(dataFlowInfoGU.getSourceCpu());
        dataSourceGroup1.setCalcSourceMem(dataFlowInfoGU.getSourceMem());
        dataSourceGroup1.setTopicNum(dataFlowInfoGU.getTopicNum());
        dataSourceGroup1.setTopicSize(dataFlowInfoGU.getTopicSize());
        return ServerResponse.createBySuccess(dataSourceGroupDao.saveAndFlush(dataSourceGroup1));
    }

    /**
     * 用户组使用资源统计
     *
     * @return
     */
    public ServerResponse<List<DateFlowInfoU>> getUsed() {
        List<DateFlowInfoU> dateFlowInfoUS = new ArrayList<>();
        Set<String> groups = userGroupDao.getGroups();
        for (String groupName : groups) {
            int groupUsedCalcCpu = 0;
            int groupUsedCalcMem = 0;
            int groupUsedTopicNum = 0;
            DateFlowInfoU dateFlowInfoU = new DateFlowInfoU();
            dateFlowInfoU.setGroupName(groupName);
            List<SysUser> sysUsers = userDao.getUserIdsByUserGroup(groupName);
            List<Integer> ids = Lists.newArrayList();
            for (SysUser sysUser : sysUsers) {
                Integer id = sysUser.getId().intValue();
                ids.add(id);
            }
            List<DataFlowInfo> dataFlowInfos = dataFlowInfoDao.getDataFlowInfosByUserIdIn(ids);
            for (DataFlowInfo dataFlowInfo : dataFlowInfos) {
                groupUsedCalcCpu += dataFlowInfo.getCalcCpu();
                groupUsedCalcMem += dataFlowInfo.getCalcMem();
                groupUsedTopicNum += dataFlowInfo.getTopicNum();
            }
            dateFlowInfoU.setUsedCpu(groupUsedCalcCpu);
            dateFlowInfoU.setUsedMem(groupUsedCalcMem);
            dateFlowInfoU.setUsedTopicNum(groupUsedTopicNum);
            dateFlowInfoUS.add(dateFlowInfoU);
        }
        return ServerResponse.createBySuccess(dateFlowInfoUS);
    }

    /**
     * 用户组剩余资源统计
     *
     * @return
     */
    public ServerResponse<List<DataFlowInfoS>> getSurplus() {
        List<DataFlowInfoS> dataFlowInfoSList = new ArrayList<>();
        List<DataSourceGroup> dataSourceGroups = dataSourceGroupDao.findAll();
        List<DateFlowInfoU> dateFlowInfoUS = this.getUsed().getData();
        for (DataSourceGroup dataSourceGroup : dataSourceGroups) {
            for (DateFlowInfoU dateFlowInfoU : dateFlowInfoUS) {
                if (dataSourceGroup.getGroupName().equals(dateFlowInfoU.getGroupName())) {
                    DataFlowInfoS dataFlowInfoS = new DataFlowInfoS();
                    dataFlowInfoS.setGroupName(dataSourceGroup.getGroupName());
                    dataFlowInfoS.setSurplusCpu(dataSourceGroup.getCalcSourceCpu() - dateFlowInfoU.getUsedCpu());
                    dataFlowInfoS.setSurplusMem(dataSourceGroup.getCalcSourceMem() - dateFlowInfoU.getUsedMem());
                    dataFlowInfoS.setSurplusTopicNum(dataSourceGroup.getTopicNum() - dateFlowInfoU.getUsedTopicNum());
                    dataFlowInfoSList.add(dataFlowInfoS);
                    break;
                }
            }
        }
        return ServerResponse.createBySuccess(dataFlowInfoSList);
    }

    /**
     * 用户组使用的和配置资源统计
     *
     * @return
     */
    public ServerResponse<List<DataFlowInfoGU>> getAllAndUsed() {
        List<DateFlowInfoU> dataFlowInfoSList = getUsed().getData();
        List<DataSourceGroup> dataSourceGroups = dataSourceGroupDao.findAll();
        List<DataFlowInfoGU> dataFlowInfoGUList = new ArrayList<>();
        for (DataSourceGroup dataSourceGroup : dataSourceGroups) {
            for (DateFlowInfoU dateFlowInfoU : dataFlowInfoSList) {
                if (dataSourceGroup.getGroupName().equals(dateFlowInfoU.getGroupName())) {
                    DataFlowInfoGU dataFlowInfoGU = new DataFlowInfoGU();
                    dataFlowInfoGU.setGroupName(dateFlowInfoU.getGroupName());
                    dataFlowInfoGU.setSourceCpu(dataSourceGroup.getCalcSourceCpu());
                    dataFlowInfoGU.setSourceMem(dataSourceGroup.getCalcSourceMem());
                    dataFlowInfoGU.setTopicNum(dataSourceGroup.getTopicNum());
                    dataFlowInfoGU.setUsedCpu(dateFlowInfoU.getUsedCpu());
                    dataFlowInfoGU.setUsedMem(dateFlowInfoU.getUsedMem());
                    dataFlowInfoGU.setUsedTopicNum(dateFlowInfoU.getUsedTopicNum());
                    dataFlowInfoGU.setTopicSize(dataSourceGroup.getTopicSize());
                    dataFlowInfoGUList.add(dataFlowInfoGU);
                    break;
                }
            }
        }
        return ServerResponse.createBySuccess(dataFlowInfoGUList);

    }

    /**
     * resource百分比
     *
     * @return
     */
    public Map<String, String> countByType() {
        Map<String, String> map = new HashMap<>();
        List<DateFlowInfoU> usedList = getUsed().getData();
        int usedCpu = 0;
        int usedMem = 0;
        int usedDisk = 0;
        for (DateFlowInfoU dateFlowInfoU : usedList) {
            usedCpu = dateFlowInfoU.getUsedCpu() + usedCpu;
            usedMem = dateFlowInfoU.getCalcSourceMem() + usedMem;
            usedDisk = dateFlowInfoU.getTopicSize() + usedDisk;
        }
        List<SourceTable> totalList = list().getData();
        int totalCpu = totalList.get(0).getCpu();
        int totalMem = totalList.get(0).getMem();
        int totalDisk = totalList.get(0).getTopicSize();
        map.put("CPU", CharacterUtils.getPercentage(usedCpu, totalCpu));
        map.put("内存", CharacterUtils.getPercentage(usedMem, totalMem));
        map.put("磁盘", CharacterUtils.getPercentage(usedDisk, totalDisk));
        return map;
    }

    /**
     * 判断传入用户组是否超过剩余资源
     * @param group
     * @param cpu
     * @param mem
     * @param usedTopicNum
     * @return
     */
    public boolean isMoreResource(String group,int cpu,int mem,int usedTopicNum){
        ResourceServiceImpl resourceService = new ResourceServiceImpl();
        List<DataFlowInfoS> dataFlowInfoSs = resourceService.getSurplus().getData();
        for (DataFlowInfoS dataFlowInfoS :dataFlowInfoSs){
            if (StringUtils.equals(group,dataFlowInfoS.getGroupName())){
                if (dataFlowInfoS.getSurplusCpu()>=cpu&&dataFlowInfoS.getSurplusMem()>=mem
                        &&dataFlowInfoS.getSurplusTopicNum()>=usedTopicNum){
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }

    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        Integer[] integers = new Integer[list.size()];
        integers = list.toArray(integers);
        System.out.println(Arrays.toString(integers));
    }



}
    