package com.unistack.tamboo.mgt.model.calc;


import javax.persistence.*;
import java.util.Date;

/**
 * @ClassName CustomFilter
 * @Description TODO
 * @Author unistack
 * @Date 2018/7/28 11:39
 * @Version 1.0
 */
@Entity
@Table(name = "custom_filter", schema = "sa-mgt", catalog = "")
public class CustomFilter {
    private int jarId;
    private String jarName;
    private String jarUser;
    private Date jarUpdatetime;
    private String jarDesc;
    private String jarClasspath;
    private String remark;
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Id
    @Column(name = "jar_id")
    public int getJarId() {
        return jarId;
    }

    public void setJarId(int jarId) {
        this.jarId = jarId;
    }
    @Column(name = "jar_name")
    public String getJarName() {
        return jarName;
    }

    public void setJarName(String jarName) {
        this.jarName = jarName;
    }
    @Column(name = "jar_user")
    public String getJarUser() {
        return jarUser;
    }

    public void setJarUser(String jarUser) {
        this.jarUser = jarUser;
    }
    @Column(name="jar_updatetime")
    public Date getJarUpdatetime() {
        return jarUpdatetime;
    }

    public void setJarUpdatetime(Date jarUpdatetime) {
        this.jarUpdatetime = jarUpdatetime;
    }
    @Column(name="jar_desc")
    public String getJarDesc() {
        return jarDesc;
    }

    public void setJarDesc(String jarDesc) {
        this.jarDesc = jarDesc;
    }
    @Column(name="jar_classpath")
    public String getJarClasspath() {
        return jarClasspath;
    }

    public void setJarClasspath(String jarClasspath) {
        this.jarClasspath = jarClasspath;
    }
    @Column(name="remark")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
