package com.lq.task.mybatis;


import com.lq.SpringBootCli;
import com.lq.entity.TableInfo;
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
                .append(transformTableInfo.getTableName()).append(";");
        if (springBootCli.isUsePage()) {
            sb.append("\nimport java.util.List;");
        }
        sb.append("\n\npublic interface ").append(transformTableInfo.getTableName()).append("Mapper {\n\n");
        transformTableInfo.getFiledEntities().stream()
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
                            if (springBootCli.isUsePage()) {
                                sb.append("List<").append(transformTableInfo.getTableName()).append("> queryAll")
                                        .append(transformTableInfo.getTableName())
                                        .append("(@Param(\"page\")Integer page,@Param(\"pageSize\")Integer pageSize);\n\n\t")
                                        .append("Integer queryAll").append(transformTableInfo.getTableName()).append("Count();\n\n\t");
                            }
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
                                    .append(");\n\n")
                                    .append("\tboolean update")
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
    String getPackageName() {
        return "mapper";
    }
}
