package com.lq.mybaits.task;


import com.lq.SpringBootCli;
import com.lq.entity.TableFiledEntity;
import com.lq.entity.TableInfo;
import com.lq.glob.task.BaseTask;
import com.lq.util.StringUtil;

import java.util.List;

public final class CreateMapperTask extends BaseTask<Boolean> {

    private List<TableInfo> tableInfos;

    public CreateMapperTask(SpringBootCli springBootCli, List<TableInfo> tableInfos) {
        super(springBootCli);
        this.tableInfos = tableInfos;
    }


    @Override
    public Boolean execute() throws Exception {
        if (checkDir()) {
            for (TableInfo tableInfo : tableInfos) {
                String mapper = tableInfo2Mapper(tableInfo);
                createFile(tableInfo.getTransformTableInfo().getTableName() + "Mapper.java", mapper);
            }
        }
        return true;
    }

    private String tableInfo2Mapper(TableInfo tableInfo) {
        TableInfo transformTableInfo = tableInfo.getTransformTableInfo();
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(springBootCli.getPackageName()).append(".").append(getPackageName()).append(";\n\n")
                .append("import org.apache.ibatis.annotations.Param;\n")
                .append("import ").append(springBootCli.getPackageName()).append(".entity.")
                .append(transformTableInfo.getTableName()).append(";\nimport java.util.List;\n\npublic interface ")
                .append(transformTableInfo.getTableName()).append("Mapper {\n\n");
        List<TableFiledEntity> filedEntities = transformTableInfo.getFiledEntities();
        filedEntities.stream()
                .filter(tableFiledEntity -> tableFiledEntity.getKey().equals("PRI"))
                .findFirst()
                .ifPresent(priKey -> {
                            sb.append("\tboolean insert")
                                    .append(transformTableInfo.getTableName())
                                    .append("(")
                                    .append(transformTableInfo.getTableName())
                                    .append(" ")
                                    .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                    .append(");\n\n\t");
                            sb.append("List<").append(transformTableInfo.getTableName()).append("> queryAll")
                                    .append(transformTableInfo.getTableName());
                            sb.append("(");
                            for (int x = 1; x < filedEntities.size(); x++) {
                                TableFiledEntity tableFiledEntity = filedEntities.get(x);
                                if (tableFiledEntity.getType().equals("String")&&tableFiledEntity.getFieldLimitSize()<springBootCli.getQueryFieldLimitLength()&&
                                        tableFiledEntity.getFieldLimitSize() != null && tableFiledEntity.getFieldLimitSize() > 0) {
                                    sb.append("@Param(\"")
                                            .append(tableFiledEntity.getName())
                                            .append("\")String ")
                                            .append(tableFiledEntity.getName()).append(",\n\t\t\t\t\t\t\t");
                                }
                            }
                            sb.append("@Param(\"page\")Integer page, @Param(\"pageSize\")Integer pageSize);\n\n\t")
                                    .append("Integer queryAll").append(transformTableInfo.getTableName()).append("Count(");
                            for (int x = 1; x < filedEntities.size(); x++) {
                                TableFiledEntity tableFiledEntity = filedEntities.get(x);
                                if (tableFiledEntity.getType().equals("String")&&tableFiledEntity.getFieldLimitSize()<springBootCli.getQueryFieldLimitLength()&&
                                        tableFiledEntity.getFieldLimitSize() != null && tableFiledEntity.getFieldLimitSize() > 0) {
                                    sb.append("@Param(\"")
                                            .append(tableFiledEntity.getName())
                                            .append("\")String ")
                                            .append(tableFiledEntity.getName()).append(",\n\t\t\t\t\t\t\t");
                                }
                            }
                            for (int x = 1; x < filedEntities.size(); x++) {
                                if (filedEntities.get(x).getType().equals("String")) {
                                    sb.deleteCharAt(sb.lastIndexOf(","));
                                    break;
                                }
                            }
                            sb.append(");\n\n\t");
                            sb.append(transformTableInfo.getTableName())
                                    .append(" query")
                                    .append(transformTableInfo.getTableName())
                                    .append("By")
                                    .append(StringUtil.firstToUpperCase(priKey.getName()))
                                    .append("(@Param(\"")
                                    .append(priKey.getName())
                                    .append("\") ")
                                    .append(priKey.getType())
                                    .append(" ")
                                    .append(priKey.getName())
                                    .append(");\n\n")
                                    .append("\tint delete")
                                    .append(transformTableInfo.getTableName())
                                    .append("By")
                                    .append(StringUtil.firstToUpperCase(priKey.getName()))
                                    .append("(@Param(\"")
                                    .append(priKey.getName())
                                    .append("\") ")
                                    .append(priKey.getType())
                                    .append(" ")
                                    .append(priKey.getName())
                                    .append(");\n\n\tint delete")
                                    .append(transformTableInfo.getTableName())
                                    .append("By")
                                    .append(StringUtil.firstToUpperCase(priKey.getName()))
                                    .append("s(@Param(\"")
                                    .append(priKey.getName())
                                    .append("s\") List<")
                                    .append(priKey.getType())
                                    .append("> ")
                                    .append(priKey.getName())
                                    .append("s);\n\n\tboolean update")
                                    .append(transformTableInfo.getTableName())
                                    .append("(")
                                    .append(transformTableInfo.getTableName())
                                    .append(" ")
                                    .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                    .append(");\n\n");
                        }
                );
        sb.append("\n}");
        return sb.toString();
    }

    @Override
    protected String getPackageName() {
        return "mapper";
    }
}
