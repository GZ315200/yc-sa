package com.unistack.tamboo.mgt.model.calc;


import javax.persistence.*;

/**
 * @ClassName CustomLinkFilter
 * @Description TODO
 * @Author unistack
 * @Date 2018/7/28 11:39
 * @Version 1.0
 */
@Entity
@Table(name = "custom_link_filter", schema = "sa-mgt", catalog = "")
public class CustomLinkFilter {


    private int id;
    private int jarId;
    private int filterId;
    private String classPath;

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "jar_id")

    public int getJarId() {
        return jarId;
    }

    public void setJarId(int jarId) {
        this.jarId = jarId;
    }

    @Column(name = "filter_id")
    public int getFilterId() {
        return filterId;
    }

    public void setFilterId(int filterId) {
        this.filterId = filterId;
    }

    @Column(name = "class_path")

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }


}
