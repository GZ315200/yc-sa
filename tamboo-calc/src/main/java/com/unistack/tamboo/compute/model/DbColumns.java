package com.unistack.tamboo.compute.model;

/**
 * @author hero.li
 * 数据库中表的元数据,目前只用于查询数据时拼接sql
 */
public class DbColumns implements java.io.Serializable{
    /**
     * 数据类型
     * 4   ->  int
     * 12  ->  varchar
     * 91  ->  date
     * 1   ->  char
     *
     * dataType   :列类型
     * columnSize :列的长度,使用String是为了方便在map中存储
     * columnName :列名
     */
    private String dataType;
    private String columnSize;
    private String columnName;


    public static class Builder{
        private String dataType;
        private String columnSize;
        private String columnName;

        public Builder dataType(String dataType){
            this.dataType = dataType;
            return this;
        }

        public Builder columnSize(String columnSize){
            this.columnSize = columnSize;
            return this;
        }

        public Builder columnName(String columnName){
            this.columnName = columnName;
            return this;
        }

        public DbColumns builder(){
            return new DbColumns(this);
        }
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getColumnSize() {
        return columnSize;
    }

    public void setColumnSize(String columnSize) {
        this.columnSize = columnSize;
    }

    public String getColumnName(){
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public DbColumns(Builder b){
        this.columnName = b.columnName;
        this.columnSize = b.columnSize;
        this.dataType = b.dataType;
    }

    @Override
    public String toString() {
        return "DbColumns{" +
                "dataType='" + dataType + '\'' +
                ", columnSize='" + columnSize + '\'' +
                ", columnName='" + columnName + '\'' +
                '}';
    }
}