package com.lq.entity;

import java.util.List;

public class TableInfo {

    private String tableName;
    private List<TableFiledEntity> filedEntities;
    private TableInfo transformTableInfo;

    public TableInfo() {
    }

    public TableInfo(String tableName, List<TableFiledEntity> filedEntities) {
        this.tableName = tableName;
        this.filedEntities = filedEntities;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<TableFiledEntity> getFiledEntities() {
        return filedEntities;
    }

    public void setFiledEntities(List<TableFiledEntity> filedEntities) {
        this.filedEntities = filedEntities;
    }

    public TableInfo getTransformTableInfo() {
        return transformTableInfo;
    }

    public void setTransformTableInfo(TableInfo transformTableInfo) {
        this.transformTableInfo = transformTableInfo;
    }
}
