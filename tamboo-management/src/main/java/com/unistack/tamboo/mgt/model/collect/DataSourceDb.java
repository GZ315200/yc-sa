package com.unistack.tamboo.mgt.model.collect;

import javax.persistence.*;

/**
 * @program: tamboo-sa
 * @description:
 * @author: Asasin
 * @create: 2018-08-01 11:02
 **/
@Entity
@Table(name = "data_source_db", schema = "sa-mgt", catalog = "")
public class DataSourceDb {
    private Integer id;
    private Long dataSourceId;
    private String dbPassword;
    private String dbTable;
    private String dbUrl;
    private String dbUsername;
    private String dbDatabase;
    private Integer dbMaxtask;
    private String dbConfs;
    private String dbMode;
    private String dbIncretColumn;
    private String dbTimstampColumn;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "data_source_id")
    public Long getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(Long dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    @Basic
    @Column(name = "db_password")
    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    @Basic
    @Column(name = "db_table")
    public String getDbTable() {
        return dbTable;
    }

    public void setDbTable(String dbTable) {
        this.dbTable = dbTable;
    }

    @Basic
    @Column(name = "db_url")
    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    @Basic
    @Column(name = "db_username")
    public String getDbUsername() {
        return dbUsername;
    }

    public void setDbUsername(String dbUsername) {
        this.dbUsername = dbUsername;
    }

    @Basic
    @Column(name = "db_database")
    public String getDbDatabase() {
        return dbDatabase;
    }

    public void setDbDatabase(String dbDatabase) {
        this.dbDatabase = dbDatabase;
    }

    @Basic
    @Column(name = "db_maxtask")
    public Integer getDbMaxtask() {
        return dbMaxtask;
    }

    public void setDbMaxtask(Integer dbMaxtask) {
        this.dbMaxtask = dbMaxtask;
    }

    @Basic
    @Column(name = "db_confs")
    public String getDbConfs() {
        return dbConfs;
    }

    public void setDbConfs(String dbConfs) {
        this.dbConfs = dbConfs;
    }

    @Basic
    @Column(name = "db_mode")
    public String getDbMode() {
        return dbMode;
    }

    public void setDbMode(String dbMode) {
        this.dbMode = dbMode;
    }

    @Basic
    @Column(name = "db_incret_column")
    public String getDbIncretColumn() {
        return dbIncretColumn;
    }

    public void setDbIncretColumn(String dbIncretColumn) {
        this.dbIncretColumn = dbIncretColumn;
    }

    @Basic
    @Column(name = "db_timstamp_column")
    public String getDbTimstampColumn() {
        return dbTimstampColumn;
    }

    public void setDbTimstampColumn(String dbTimstampColumn) {
        this.dbTimstampColumn = dbTimstampColumn;
    }


    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (dataSourceId != null ? dataSourceId.hashCode() : 0);
        result = 31 * result + (dbPassword != null ? dbPassword.hashCode() : 0);
        result = 31 * result + (dbTable != null ? dbTable.hashCode() : 0);
        result = 31 * result + (dbUrl != null ? dbUrl.hashCode() : 0);
        result = 31 * result + (dbUsername != null ? dbUsername.hashCode() : 0);
        result = 31 * result + (dbDatabase != null ? dbDatabase.hashCode() : 0);
        result = 31 * result + (dbMaxtask != null ? dbMaxtask.hashCode() : 0);
        result = 31 * result + (dbConfs != null ? dbConfs.hashCode() : 0);
        result = 31 * result + (dbMode != null ? dbMode.hashCode() : 0);
        result = 31 * result + (dbIncretColumn != null ? dbIncretColumn.hashCode() : 0);
        result = 31 * result + (dbTimstampColumn != null ? dbTimstampColumn.hashCode() : 0);
        return result;
    }
}
    