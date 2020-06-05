package com.lq.task.mybatis;

import com.lq.SpringBootCli;
import com.lq.entity.TableFiledEntity;
import com.lq.entity.TableInfo;
import com.lq.util.JdbcUtil;
import com.lq.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class CreateJavaBeanTask extends BaseTask<List<TableInfo>> {

    private List<String> numberType = Arrays.asList("tinyint", "smallint", "mediumint", "int", "bigint", "float", "double", "decimal");

    private final JdbcUtil jdbcUtil;

    private String[] filterTableNames;

    public CreateJavaBeanTask(SpringBootCli springBootCli, String[] filterTableNames) {
        super(springBootCli);
        jdbcUtil = new JdbcUtil(springBootCli.getJdbcConfigEntity());
        this.filterTableNames = filterTableNames;
    }


    @Override
    String getPackageName() {
        return "entity";
    }

    public List<TableInfo> execute() throws Exception {
        if (checkDir()) {
            List<TableInfo> tableInfos = jdbcUtil.queryTableInfo();
            if (tableInfos != null && tableInfos.size() > 0) {
                List<String> tableNameList = Arrays.asList(filterTableNames);
                tableInfos = tableInfos.stream()
                        .filter(tableInfo -> !tableNameList.contains(tableInfo.getTableName()))
                        .collect(Collectors.toList());
                for (TableInfo tableInfo : tableInfos) {
                    TableInfo transformTableInfo = transformTableInfo(tableInfo);
                    String javaBean = tableInfo2JavaBean(transformTableInfo);
                    createFile(tableInfo.getTransformTableInfo().getTableName() + ".java", javaBean);
                }
            }
            return tableInfos;
        }
        return Collections.emptyList();
    }

    private TableInfo transformTableInfo(TableInfo tableInfo) {
        String tableInfoTableName = tableInfo.getTableName();
        if (springBootCli.getFilterTableNameStr() != null) {
            tableInfoTableName = tableInfoTableName.replaceAll(springBootCli.getFilterTableNameStr(), "");
        }
        String tableName = StringUtil.firstToUpperCase(StringUtil.underlineToHump(tableInfoTableName));
        List<TableFiledEntity> tableFiledEntities = new ArrayList<>();
        for (TableFiledEntity tableFiledEntity : tableInfo.getFiledEntities()) {
            String type = filedType2DataType(tableFiledEntity.getType());
            String name = StringUtil.underlineToHump(tableFiledEntity.getName());
            TableFiledEntity filedEntity = new TableFiledEntity(name, tableFiledEntity.getaNull(), tableFiledEntity.getKey(), tableFiledEntity.getaDefault(), tableFiledEntity.getExtra());
            if (type.startsWith("String")) {
                String[] typeSplit = type.split("_");
                type = typeSplit[0];
                filedEntity.setFieldLimitSize(Integer.parseInt(typeSplit[1]));
            }
            filedEntity.setType(type);
            tableFiledEntities.add(filedEntity);
        }
        TableInfo transformTableInfo = new TableInfo(tableName, tableFiledEntities);
        tableInfo.setTransformTableInfo(transformTableInfo);
        return transformTableInfo;
    }

    private String tableInfo2JavaBean(TableInfo transformTableInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(springBootCli.getPackageName()).append(".").append(getPackageName())
                .append(";\n\nimport org.hibernate.validator.constraints.Length;\nimport javax.validation.constraints.NotBlank;\n\n");
        sb.append("public class ").append(transformTableInfo.getTableName()).append(" {\n\n");
        transformTableInfo.getFiledEntities().forEach(tableFiledEntity -> {
            if (!tableFiledEntity.getKey().equals("PRI") && tableFiledEntity.getaNull().equals("NO") && tableFiledEntity.getType().equals("String")) {
                sb.append("\t@NotBlank(message=\"").append(tableFiledEntity.getName()).append("不能为空\")\n");
                if (tableFiledEntity.getFieldLimitSize() != 0) {
                    sb.append("\t@Length(max = ").append(tableFiledEntity.getFieldLimitSize()).append(",message = \"")
                            .append(tableFiledEntity.getName()).append("长度不能超过")
                            .append(tableFiledEntity.getFieldLimitSize()).append("位\")\n");
                }
            }
            String fieldName = StringUtil.firstIsUpperCase(tableFiledEntity.getName()) ? StringUtil.firstToLowerCase(tableFiledEntity.getName()) : tableFiledEntity.getName();
            sb.append("\tprivate ").append(tableFiledEntity.getType())
                    .append(" ").append(fieldName).append(";\n");
        });
        sb.append("\n");
        transformTableInfo.getFiledEntities().forEach(tableFiledEntity -> {
            String type = tableFiledEntity.getType();
            String nameUpperCase = StringUtil.firstIsUpperCase(tableFiledEntity.getName()) ? tableFiledEntity.getName() : StringUtil.firstToUpperCase(tableFiledEntity.getName());
            String fieldName = StringUtil.firstIsUpperCase(tableFiledEntity.getName()) ? StringUtil.firstToLowerCase(tableFiledEntity.getName()) : tableFiledEntity.getName();
            sb.append("\tpublic ").append(type).append(" get").append(nameUpperCase).append("(){\n")
                    .append("\t\treturn ").append(fieldName).append(";\n\t}\n\n");
            sb.append("\tpublic void").append(" set").append(nameUpperCase).append("(").
                    append(type).append(" ").append(fieldName).append("){\n")
                    .append("\t\tthis.").append(fieldName).append(" = ")
                    .append(fieldName).append(";\n\t}\n\n");
        });
        sb.append("\n}");
        return sb.toString();
    }

    private String filedType2DataType(String filedType) {
        String[] types = filedType.split(Pattern.quote("("));
        String type = types[0];
        if (numberType.contains(type)) {
            if (type.equals("bigint")) {
                return Long.class.getSimpleName();
            } else if (type.endsWith("int")) {
                return Integer.class.getSimpleName();
            } else if (type.equals("decimal") || type.equals("double")) {
                return Double.class.getSimpleName();
            } else {
                return type;
            }
        } else {
            String fieldLimitSize = "0";
            if (types.length == 2) {
                fieldLimitSize = types[1].replace(")", "");
            }
            return "String_" + fieldLimitSize;
        }
    }

}
