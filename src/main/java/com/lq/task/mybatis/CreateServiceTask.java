package com.lq.task.mybatis;


import com.lq.SpringBootCli;
import com.lq.entity.TableFiledEntity;
import com.lq.entity.TableInfo;
import com.lq.util.FileUtil;
import com.lq.util.StringUtil;

import java.io.File;
import java.util.List;

public final class CreateServiceTask extends BaseTask<Boolean> {

    private List<TableInfo> tableInfos;

    public CreateServiceTask(SpringBootCli springBootCli, List<TableInfo> tableInfos) {
        super(springBootCli);
        this.tableInfos = tableInfos;
    }

    @Override
    public Boolean execute() throws Exception {
        if (checkDir()) {
            for (TableInfo tableInfo : tableInfos) {
                String service = tableInfo2Service(tableInfo);
                createFile(tableInfo.getTransformTableInfo().getTableName() + "Service.java", service);
                String serviceImpl = tableInfo2ServiceImpl(tableInfo);
                File serviceImplDir = new File(springBootCli.getRootPackagePath() + getPackageName() + File.separator + "impl" + File.separator);
                if (!serviceImplDir.exists()) {
                    serviceImplDir.mkdir();
                }
                File file = new File(springBootCli.getRootPackagePath() + getPackageName() + File.separator + "impl" + File.separator + tableInfo.getTransformTableInfo().getTableName() + "ServiceImpl.java");
                if (!file.exists()||springBootCli.isForceCover()) {
                    FileUtil.createWriteFile(file, serviceImpl);
                }
            }
        }
        return true;
    }

    private String tableInfo2Service(TableInfo tableInfo) {
        TableInfo transformTableInfo = tableInfo.getTransformTableInfo();
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(springBootCli.getPackageName()).append(".").append(getPackageName())
                .append(";\n\nimport ").append(springBootCli.getPackageName()).append(".entity.")
                .append(transformTableInfo.getTableName()).append(";\n\n")
                .append("public interface ").append(transformTableInfo.getTableName()).append("Service {\n\n");
        sb.append("\tboolean insert")
                .append(transformTableInfo.getTableName())
                .append("(")
                .append(transformTableInfo.getTableName())
                .append(" ")
                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                .append(");\n\n");
        transformTableInfo.getFiledEntities().stream()
                .filter(tableFiledEntity -> tableFiledEntity.getKey().equals("PRI"))
                .findFirst()
                .ifPresent(priKey -> sb.append("\t")
                        .append(transformTableInfo.getTableName())
                        .append(" query")
                        .append(transformTableInfo.getTableName())
                        .append("By")
                        .append(StringUtil.firstToUpperCase(priKey.getName()))
                        .append("(")
                        .append(priKey.getType())
                        .append(" ")
                        .append(priKey.getName())
                        .append(");\n\n")
                        .append("\tint delete")
                        .append(transformTableInfo.getTableName())
                        .append("By")
                        .append(StringUtil.firstToUpperCase(priKey.getName()))
                        .append("(")
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
                        .append(");\n\n")
                );
        sb.append("\n}");
        return sb.toString();
    }

    private String tableInfo2ServiceImpl(TableInfo tableInfo) {
        TableInfo transformTableInfo = tableInfo.getTransformTableInfo();
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(springBootCli.getPackageName()).append(".").append(getPackageName())
                .append(".impl;\n\nimport ").append(springBootCli.getPackageName()).append(".entity.")
                .append(transformTableInfo.getTableName())
                .append(";\n\nimport com.lq.service.").append(transformTableInfo.getTableName()).append("Service;");
        if (springBootCli.isUseRedis()) {
            sb.append(";\nimport org.springframework.data.redis.core.RedisTemplate");
        }
        sb.append(";\nimport javax.annotation.Resource;\nimport org.springframework.stereotype.Service;\nimport ")
                .append(springBootCli.getPackageName()).append(".mapper.").append(transformTableInfo.getTableName())
                .append("Mapper;\n\n@Service\n")
                .append("public class ").append(transformTableInfo.getTableName()).append("ServiceImpl implements ")
                .append(transformTableInfo.getTableName())
                .append("Service{\n\n\t@Resource\n\tprivate ").append(transformTableInfo.getTableName())
                .append("Mapper ").append(StringUtil.firstToLowerCase(transformTableInfo.getTableName())).append("Mapper;\n");
        if (springBootCli.isUseRedis()) {
            TableFiledEntity pri = transformTableInfo.getFiledEntities().stream()
                    .filter(tableFiledEntity -> tableFiledEntity.getKey().equals("PRI"))
                    .findFirst().orElseGet(TableFiledEntity::new);
            if (pri.getType() != null) {
                sb.append("\n\t@Resource\n\tprivate RedisTemplate<").append(pri.getType()).append(", ");
            } else {
                sb.append("\n\t@Resource\n\tprivate RedisTemplate<Object, ");
            }
            sb.append(transformTableInfo.getTableName()).append("> ")
                    .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName())).append("RedisTemplate;\n");
        }
        sb.append("\n\t@Override\n\tpublic boolean insert")
                .append(transformTableInfo.getTableName())
                .append("(")
                .append(transformTableInfo.getTableName())
                .append(" ")
                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                .append("){\n\t\t")
                .append("return ").append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                .append("Mapper.insert")
                .append(transformTableInfo.getTableName()).append("(")
                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName())).append(");\n\t}\n\n");
        transformTableInfo.getFiledEntities().stream()
                .filter(tableFiledEntity -> tableFiledEntity.getKey().equals("PRI"))
                .findFirst()
                .ifPresent(priKey -> {
                    sb.append("\t@Override\n\tpublic ")
                            .append(transformTableInfo.getTableName())
                            .append(" query")
                            .append(transformTableInfo.getTableName())
                            .append("By")
                            .append(StringUtil.firstToUpperCase(priKey.getName()))
                            .append("(")
                            .append(priKey.getType())
                            .append(" ")
                            .append(priKey.getName())
                            .append("){\n\t\t");
                    if (springBootCli.isUseRedis()) {
                        sb.append(transformTableInfo.getTableName()).append(" ")
                                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append(" = ")
                                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append("RedisTemplate.opsForValue().get(").append(priKey.getName()).append(");\n\t\t")
                                .append("if (").append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append("!=null){\n\t\t\treturn ")
                                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append(";\n\t\t}else{\n\t\t\t")
                                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append(" = ").append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append("Mapper.query")
                                .append(transformTableInfo.getTableName())
                                .append("By")
                                .append(StringUtil.firstToUpperCase(priKey.getName()))
                                .append("(")
                                .append(priKey.getName())
                                .append(");\n\t\t\tif(")
                                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append("!=null){\n\t\t\t\t")
                                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append("RedisTemplate.opsForValue().set(").append(priKey.getName()).append(",")
                                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append(");\n\t\t\t}\n\t\t\treturn ")
                                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append(";\n\t\t}\n\t}\n\n");
                    } else {
                        sb.append("return ").append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append("Mapper.query")
                                .append(transformTableInfo.getTableName())
                                .append("By")
                                .append(StringUtil.firstToUpperCase(priKey.getName()))
                                .append("(")
                                .append(priKey.getName())
                                .append(");\n\t}\n\n");
                    }
                    sb.append("\t@Override\n\tpublic int delete")
                            .append(transformTableInfo.getTableName())
                            .append("By")
                            .append(StringUtil.firstToUpperCase(priKey.getName()))
                            .append("(")
                            .append(priKey.getType())
                            .append(" ")
                            .append(priKey.getName())
                            .append("){\n\t\t");
                    if (springBootCli.isUseRedis()) {
                        sb.append("int deleteFlag = ").append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append("Mapper.delete")
                                .append(transformTableInfo.getTableName())
                                .append("By")
                                .append(StringUtil.firstToUpperCase(priKey.getName()))
                                .append("(")
                                .append(priKey.getName())
                                .append(");\n\t\tif (deleteFlag > 1) {\n\t\t\t")
                                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append("RedisTemplate.delete(")
                                .append(priKey.getName())
                                .append(");\n\t\t}\n\t\treturn deleteFlag;\n\t}\n\n");
                    } else {
                        sb.append("return ").append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append("Mapper.delete")
                                .append(transformTableInfo.getTableName())
                                .append("By")
                                .append(StringUtil.firstToUpperCase(priKey.getName()))
                                .append("(")
                                .append(priKey.getName())
                                .append(");\n\t}\n\n");
                    }
                    sb.append("\t@Override\n\tpublic boolean update")
                            .append(transformTableInfo.getTableName())
                            .append("(")
                            .append(transformTableInfo.getTableName())
                            .append(" ")
                            .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                            .append("){\n\t\t");
                    if (springBootCli.isUseRedis()) {
                        sb.append("boolean updateFlag = ")
                                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append("Mapper.update")
                                .append(transformTableInfo.getTableName()).append("(")
                                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append(");\n\t\tif (updateFlag) {\n\t\t\t")
                                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append("RedisTemplate.opsForValue().set(")
                                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append(".get").append(StringUtil.firstToUpperCase(priKey.getName())).append("(),")
                                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append(");\n\t\t}\n\t\treturn updateFlag;\n\t}\n\n");
                    } else {
                        sb.append("return ")
                                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append("Mapper.update")
                                .append(transformTableInfo.getTableName()).append("(")
                                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append(");\n\t}\n\n");
                    }
                });
        sb.append("\n}");
        return sb.toString();
    }


    @Override
    String getPackageName() {
        return "service";
    }
}
