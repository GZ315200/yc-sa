package com.unistack.tamboo.compute.utils;


/**
 * @author hero.li
 */
public enum DbType {
    MYSQL("MYSQL",1),ORACLE("ORACLE",2),SQLSERVER("SQLSERVER",3);
    private String dbName;
    private int code;

    DbType(String dbName,int code){
        this.dbName = dbName;
        this.code = code;
    }
}