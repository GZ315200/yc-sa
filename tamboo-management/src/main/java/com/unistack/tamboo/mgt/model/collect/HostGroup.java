package com.unistack.tamboo.mgt.model.collect;

import javax.persistence.*;
import java.util.Date;

/**
 * @program: tamboo-sa
 * @description:
 * @author: Asasin
 * @create: 2018-07-20 14:51
 **/
@Entity
@Table(name = "host_group", schema = "sa-mgt", catalog = "")
public class HostGroup {
    private String groupName;
    private Integer hostId;
    private Date createTime;
    private Integer id;

    @Basic
    @Column(name = "group_name")
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Basic
    @Column(name = "host_id")
    public Integer getHostId() {
        return hostId;
    }

    public void setHostId(Integer hostId) {
        this.hostId = hostId;
    }

    @Basic
    @Column(name = "create_time")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int result = groupName != null ? groupName.hashCode() : 0;
        result = 31 * result + (hostId != null ? hostId.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }
}
    