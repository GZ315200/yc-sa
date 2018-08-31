package com.unistack.tamboo.mgt.service.collect;

import com.google.common.collect.Lists;
import com.unistack.tamboo.mgt.common.ServerResponse;
import com.unistack.tamboo.mgt.common.enums.EWebSocketTopic;
import com.unistack.tamboo.mgt.common.page.PageConvert;
import com.unistack.tamboo.mgt.common.page.PaginationData;
import com.unistack.tamboo.mgt.dao.collect.DataSourceHostDao;
import com.unistack.tamboo.mgt.dao.collect.DataSourceListDao;
import com.unistack.tamboo.mgt.dao.collect.HostGroupDao;
import com.unistack.tamboo.mgt.model.collect.*;
import com.unistack.tamboo.mgt.model.sys.UserSession;
import com.unistack.tamboo.mgt.service.BaseService;
import com.unistack.tamboo.mgt.utils.IpUtil;
import com.unistack.tamboo.mgt.utils.SecUtils;
import com.unistack.tamboo.sa.dc.flume.common.Const;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @program: tamboo-sa
 * @description:
 * @author: Asasin
 * @create: 2018-07-20 10:57
 **/
@Service
public class HostServiceImpl extends BaseService {
    private static  Logger logger = LoggerFactory.getLogger(HostServiceImpl.class);
    private static String PATH = "~/";

    @Autowired
    private DataSourceHostDao dataSourceHostDao;

    @Autowired
    private HostGroupDao hostGroupDao;

    @Autowired
    private DataSourceListDao dataSourceListDao;

    @Autowired
    private DataSourceServiceImpl dataSourceService;

    @Autowired
    private SimpMessagingTemplate template;


    /**
     * 按条件分页查询
     */
    public ServerResponse<PaginationData<DataSourceHost>> list(String key, int pageIndex, int pageSize, Sort sort) {
        PageRequest pageable = new PageRequest(pageIndex - 1, pageSize, sort);
        Page<DataSourceHost> pageData = dataSourceHostDao.findAll(new Specification<DataSourceHost>() {
            @Override
            public Predicate toPredicate(Root<DataSourceHost> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                List<Predicate> lstPredicates = Lists.newArrayList();
                String v = getStringTrim(key);
                if (StringUtils.isNotBlank(v)) {
                    lstPredicates.add(builder.like(root.get("ip"), "%" + v + "%"));
                    lstPredicates.add(builder.like(root.get("group"), "%" + v + "%"));
                    lstPredicates.add(builder.like(root.get("username"), "%" + v + "%"));
                    lstPredicates.add(builder.like(root.get("hostname"), "%" + v + "%"));
                    lstPredicates.add(builder.like(root.get("manager"), "%" + v + "%"));
                }
                if (!lstPredicates.isEmpty()) {
                    Predicate cond = builder.or(lstPredicates.toArray(new Predicate[lstPredicates.size()]));
                    query.where(builder.gt(root.get("installFlag"), -1), (builder.gt(root.get("hostFlag"), 0)),cond);
                } else {
                    query.where(builder.gt(root.get("installFlag"), -1),(builder.gt(root.get("hostFlag"), 0)));
                }
                return null;
            }
        }, pageable);
        pageData.getContent().forEach(ipList -> {
            ipList.setPasswd(StringUtils.EMPTY);
        });
        return ServerResponse.createBySuccess(PageConvert.convertJpaPage2PaginationData(pageData, pageSize));
    }

    /**
     * 添加主机
     *
     * @param dataSourceHostVo
     * @return
     * @throws IOException
     */
    public ServerResponse add(DataSourceHostVo dataSourceHostVo) throws Exception {
        //login check
        UserSession userSession = null;
        try {
            userSession = SecUtils.findLoggedUserSession();
        } catch (Exception e) {
            logger.error("", e);
            return ServerResponse.createByErrorMsg(e.getMessage());
        }
        //input check
        if ((dataSourceHostVo.getId() == null || dataSourceHostVo.getId() <= 0) && dataSourceHostVo.getCreateTime() == null) {//新主机
            dataSourceHostVo.setCreateTime(new Date());
        }
        String url = "";
        String ip = dataSourceHostVo.getDataSourceHost().getIp();
        String hostname = dataSourceHostVo.getDataSourceHost().getHostname();
        String username = dataSourceHostVo.getDataSourceHost().getUsername();
        String password = dataSourceHostVo.getDataSourceHost().getPasswd();
//        if (StringUtils.isBlank(ip)) {
//            try {
//                url = InetAddress.getLocalHost().getHostAddress();
//                dataSourceHostVo.getDataSourceHost().setIp(url);
//            } catch (UnknownHostException e) {
//                logger.error("", e);
//            }
//        }
        if (StringUtils.isBlank(ip) || StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return ServerResponse.createByErrorMsg("IP／用户／密码为空");
        }
        if (!IpUtil.ipCheck(ip)) {
            return ServerResponse.createByErrorMsg("IP格式错误");
        }
        DataSourceHost dataSourceHost1 = this.dataSourceHostDao.getByIps(ip);
        if (dataSourceHost1 != null) {
            return ServerResponse.createByErrorMsg("IP已存在");
        }
        DataSourceHost dataSourceHost2 = this.dataSourceHostDao.getByHostname(hostname);
        if (dataSourceHost2 != null) {
            return ServerResponse.createByErrorMsg("HostName已存在");
        }
        //excute shell check host ,including ip,user,passwd
         String hostName = dataSourceHostVo.getDataSourceHost().getHostname();
        new Thread(() -> {
            logger.info("*********************正在执行主机检查");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                logger.error("", e);
            }
            ServerResponse<String> rs = null;
            DataSourceHost dataSourceHost = null;
            //校验主机信息是否有效
            try {
                dataSourceHost = dataSourceHostDao.getDataSourceHostByHostname(hostName);
                rs = checkHostVaild(dataSourceHostVo.getDataSourceHost());
                if (!rs.isSuccess()) {
                    dataSourceHostDao.delete(dataSourceHost.getId());
                    DataSourceHost host = dataSourceHostDao.getDataSourceHostsById(dataSourceHost.getId());
                    host.setInstallFlag(Const.HostState.DELETED.getValue());
                    template.convertAndSend(EWebSocketTopic.TAMBOO_HOST_INFO.getName(), dataSourceHostDao.getDataSourceHostsById(dataSourceHost.getId()));
                }
            } catch (IOException e) {
                logger.error("", e);
            }
            dataSourceHostDao.setFlag(dataSourceHost.getId(), Const.HostState.INSTALLED.getValue());
            List<HostGroup> hostGroups = Lists.newArrayList();
            List<String> groups = dataSourceHostVo.getUserGroups();

            for (String group : groups) {
                HostGroup hostGroup = new HostGroup();
                hostGroup.setCreateTime(new Date());
                hostGroup.setGroupName(group);
                hostGroup.setHostId(dataSourceHost.getId());
                hostGroups.add(hostGroup);
            }
            hostGroupDao.save(hostGroups);
            logger.info("*********************推送状态信息");
            template.convertAndSend(EWebSocketTopic.TAMBOO_HOST_INFO.getName(), dataSourceHostDao.getDataSourceHostsById(dataSourceHost.getId()));
        }).start();
        //save host
        dataSourceHostVo.getDataSourceHost().setCreateBy(userSession.getUsername());
        dataSourceHostVo.getDataSourceHost().setCreateTime(new Date());
        dataSourceHostVo.getDataSourceHost().setInstallFlag(Const.HostState.INSTALLING.getValue());
        dataSourceHostVo.getDataSourceHost().setHostFlag(Const.HostType.REMOTE.getValue());
        dataSourceHostDao.save(dataSourceHostVo.getDataSourceHost());
        return ServerResponse.createBySuccess();
    }


    /**
     * 检验主机信息合法性及通过shell检验目标主机的有效性
     * 执行脚本： sh main.sh sshAgent -ip 192.168.1.201 -u root -p 123456
     *
     * @param hostInfo 主机信息
     * @return
     */
    private ServerResponse<String> checkHostVaild(DataSourceHost hostInfo) throws IOException {


        String command = this.getAgentCommand(Const.AgentCommandParam.C_SSHAGENT, hostInfo.getIp(), hostInfo.getUsername(), hostInfo.getPasswd(), hostInfo.getPath());
        logger.info("check-command: " + command);

        StringBuilder rsLog = new StringBuilder();
        int succ = 0;
        ServerResponse<String[]> execResult = null;

        execResult = this.execCommand(command);
        if (execResult.isSuccess()) {
            String[] rsLines = execResult.getData();
            for (String line : rsLines) {
                rsLog.append(line).append("\n");

                if (line.startsWith("{\"step")) {
                    if (SUCCESS.equals(processShellResult(line))) {
                        succ++;
                    } else {
                        return ServerResponse.createByErrorMsg("校验失败");
                    }

                }
            }
        }
        logger.info("check-command-retMsg: " + rsLog.toString());
        //校验分 step 1/2/3，只要有两步以上OK，说明服务信息正常
        return succ >= 2 ? ServerResponse.createBySuccess() : ServerResponse.createByErrorMsg("校验失败");
    }

    /**
     * 删除主机
     *
     * @param hostId
     * @return
     */
    public ServerResponse delete(int hostId) {
//            如果该主机上有运行的采集器，则不能删除.反之则删除
        List<DataSourceList> dataSourceLists = dataSourceListDao.getDataSourceListByHostId(hostId);
        for (DataSourceList dataSourceList : dataSourceLists) {
            if (dataSourceList.getFlag() > 0) {
                return ServerResponse.createBySuccess("failed,caused this host has some dataSource are running");
            }
           // dataSourceService.deleteDataSource(dataSourceList.getDataSourceId());
            dataSourceService.deleteDatasource(dataSourceList.getDataSourceId());
        }
        List<HostGroup> hostGroups = hostGroupDao.getHostGroupsByHostId(hostId);
        for (HostGroup hostGroup : hostGroups) {
            hostGroupDao.delete(hostGroup.getId());
        }
        dataSourceHostDao.delete(hostId);
        return ServerResponse.createBySuccess("delete hosts success");
    }

    /**
     * 获取所有Host
     *
     * @return
     */
    public ServerResponse getAllHost() {
        return ServerResponse.createBySuccess(dataSourceHostDao.getDataSourceHosts());
    }

}
    