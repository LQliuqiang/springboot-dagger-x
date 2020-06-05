package com.lq.entity;

import java.util.regex.Pattern;

public class TableFiledEntity {

    private String name;
    private String type;
    private String aNull;
    private String key;
    private String aDefault;
    private String extra;
    private Integer fieldLimitSize;

    public TableFiledEntity() {
    }

    public TableFiledEntity(String name, String aNull, String key, String aDefault, String extra) {
        this.name = name;
        this.aNull = aNull;
        this.key = key;
        this.aDefault = aDefault;
        this.extra = extra;
    }

    public TableFiledEntity(String name, String type, String aNull, String key, String aDefault, String extra) {
        this.name = name;
        this.type = type;
        this.aNull = aNull;
        this.key = key;
        this.aDefault = aDefault;
        this.extra = extra;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getaNull() {
        return aNull;
    }

    public void setaNull(String aNull) {
        this.aNull = aNull;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getaDefault() {
        return aDefault;
    }

    public void setaDefault(String aDefault) {
        this.aDefault = aDefault;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public Integer getFieldLimitSize() {
        return fieldLimitSize;
    }

    public void setFieldLimitSize(Integer fieldLimitSize) {
        this.fieldLimitSize = fieldLimitSize;
    }

    @Override
    public String toString() {
        return "TableFiledEntity{" +
                "name='" + name + '\'' +
                ", type='" + type.split(Pattern.quote("("))[0] + '\'' +
                ", aNull='" + aNull + '\'' +
                ", key='" + key + '\'' +
                ", aDefault='" + aDefault + '\'' +
                ", extra='" + extra + '\'' +
                '}';
    }
}
