package com.lq.jpa.task;

import com.lq.SpringBootCli;
import com.lq.entity.TableFiledEntity;
import com.lq.entity.TableInfo;
import com.lq.glob.task.BaseTask;
import com.lq.util.FileUtil;
import com.lq.util.StringUtil;

import java.io.File;
import java.util.List;

public class CreateServiceTask extends BaseTask<Boolean> {

    private List<TableInfo> tableInfos;

    public CreateServiceTask(SpringBootCli springBootCli, List<TableInfo> tableInfos) {
        super(springBootCli);
        this.tableInfos = tableInfos;
    }

    @Override
    public Boolean execute() throws Exception {
        if (checkDir()) {
            for (TableInfo tableInfo : tableInfos) {
                TableFiledEntity pri = tableInfo.getTransformTableInfo().getFiledEntities().stream()
                        .filter(tableFiledEntity -> tableFiledEntity.getKey().equals("PRI"))
                        .findFirst().orElse(null);
                if (pri != null) {
                    String service = tableInfo2Service(tableInfo);
                    createFile(tableInfo.getTransformTableInfo().getTableName() + "Service.java", service);
                    String serviceImpl = tableInfo2ServiceImpl(tableInfo);
                    File serviceImplDir = new File(springBootCli.getRootPackagePath() + getPackageName() + File.separator + "impl" + File.separator);
                    if (!serviceImplDir.exists()) {
                        serviceImplDir.mkdir();
                    }
                    File serviceImplFile = new File(springBootCli.getRootPackagePath() + getPackageName() + File.separator + "impl" + File.separator + tableInfo.getTransformTableInfo().getTableName() + "ServiceImpl.java");
                    if (!serviceImplFile.exists() || springBootCli.isForceCover()) {
                        FileUtil.createWriteFile(serviceImplFile, serviceImpl);
                    }
                    String repository = tableInfo2Repository(tableInfo);
                    File repositoryImplDir = new File(springBootCli.getRootPackagePath() + getPackageName() + File.separator + "repository" + File.separator);
                    if (!repositoryImplDir.exists()) {
                        repositoryImplDir.mkdir();
                    }
                    File repositoryFile = new File(springBootCli.getRootPackagePath() + getPackageName() + File.separator + "repository" + File.separator + tableInfo.getTransformTableInfo().getTableName() + "Repository.java");
                    if (!repositoryFile.exists() || springBootCli.isForceCover()) {
                        FileUtil.createWriteFile(repositoryFile, repository);
                    }
                }
            }
        }
        return true;
    }

    private String tableInfo2Repository(TableInfo tableInfo) {
        TableInfo transformTableInfo = tableInfo.getTransformTableInfo();
        StringBuilder sb = new StringBuilder();
        transformTableInfo.getFiledEntities().stream()
                .filter(tableFiledEntity -> tableFiledEntity.getKey().equals("PRI"))
                .findFirst()
                .ifPresent(priKey -> {
                    sb.append("package ").append(springBootCli.getPackageName()).append(".").append(getPackageName())
                            .append(".repository;\n\nimport ").append(springBootCli.getPackageName()).append(".entity.")
                            .append(transformTableInfo.getTableName())
                            .append(";\nimport org.springframework.data.jpa.repository.JpaRepository;\nimport org.springframework.data.jpa.repository.JpaSpecificationExecutor;\n\npublic interface ")
                            .append(transformTableInfo.getTableName()).append("Repository extends JpaRepository<")
                            .append(transformTableInfo.getTableName()).append(",").append(priKey.getType())
                            .append(">, JpaSpecificationExecutor<").append(transformTableInfo.getTableName()).append("> {\n\n}");
                });

        return sb.toString();
    }

    private String tableInfo2ServiceImpl(TableInfo tableInfo) {
        TableInfo transformTableInfo = tableInfo.getTransformTableInfo();
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(springBootCli.getPackageName()).append(".").append(getPackageName())
                .append(".impl;\n\nimport ").append(springBootCli.getPackageName()).append(".entity.")
                .append(transformTableInfo.getTableName())
                .append(";\n\nimport ")
                .append(springBootCli.getPackageName()).append(".service.")
                .append(transformTableInfo.getTableName()).append("Service");
        if (springBootCli.isUseRedis()) {
            sb.append(";\nimport org.springframework.data.redis.core.RedisTemplate");
        }
        sb.append(";\nimport java.util.List;\nimport java.util.ArrayList;\nimport java.util.stream.Collectors;\nimport javax.annotation.Resource;\nimport org.springframework.stereotype.Service;\nimport javax.persistence.criteria.Predicate;\nimport org.springframework.data.domain.Page;\nimport org.springframework.data.domain.PageRequest;\nimport org.springframework.data.jpa.domain.Specification;\n\nimport ")
                .append(springBootCli.getPackageName()).append(".service.repository.").append(transformTableInfo.getTableName())
                .append("Repository;\n\n@Service\n")
                .append("public class ").append(transformTableInfo.getTableName()).append("ServiceImpl implements ")
                .append(transformTableInfo.getTableName())
                .append("Service{\n\n\t@Resource\n\tprivate ").append(transformTableInfo.getTableName())
                .append("Repository ").append(StringUtil.firstToLowerCase(transformTableInfo.getTableName())).append("Repository;\n");
        List<TableFiledEntity> filedEntities = transformTableInfo.getFiledEntities();
        if (springBootCli.isUseRedis()) {
            TableFiledEntity pri = filedEntities.stream()
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
        filedEntities.stream()
                .filter(tableFiledEntity -> tableFiledEntity.getKey().equals("PRI"))
                .findFirst()
                .ifPresent(priKey -> {
                    sb.append("\n\t@Override\n\tpublic ")
                            .append(transformTableInfo.getTableName())
                            .append(" insert")
                            .append(transformTableInfo.getTableName())
                            .append("(")
                            .append(transformTableInfo.getTableName())
                            .append(" ")
                            .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                            .append("){\n\t\t");

                    if (springBootCli.isUseRedis()) {
                        sb.append(StringUtil.firstToLowerCase(transformTableInfo.getTableName())).append(" = ").append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append("Repository.save(")
                                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append(");\n\t\t")
                                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append("RedisTemplate.opsForValue().set(")
                                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append(".get").append(StringUtil.firstToUpperCase(priKey.getName())).append("(),")
                                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName())).append(");\n\t\treturn ")
                                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName())).append(";\n\t}\n\n");
                    } else {
                        sb.append("return ").append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append("Repository.save(")
                                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName())).append(");\n\t}\n\n");
                    }
                    sb.append("\t@Override\n\tpublic Page<").append(transformTableInfo.getTableName()).append("> queryAll")
                            .append(transformTableInfo.getTableName()).append("(");
                    for (int x = 1; x < filedEntities.size(); x++) {
                        TableFiledEntity tableFiledEntity = filedEntities.get(x);
                        if (tableFiledEntity.getType().equals("String") && tableFiledEntity.getFieldLimitSize() < springBootCli.getQueryFieldLimitLength() &&
                                tableFiledEntity.getFieldLimitSize() != null && tableFiledEntity.getFieldLimitSize() > 0) {
                            sb.append("String ").append(tableFiledEntity.getName()).append(",");
                        }
                    }
                    sb.append("Integer page,Integer pageSize){\n\t\t").append(transformTableInfo.getTableName()).append(" ")
                            .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                            .append("=new ")
                            .append(transformTableInfo.getTableName())
                            .append("();\n\t\t");
                    for (int x = 1; x < filedEntities.size(); x++) {
                        TableFiledEntity tableFiledEntity = filedEntities.get(x);
                        if (tableFiledEntity.getType().equals("String") && tableFiledEntity.getFieldLimitSize() < springBootCli.getQueryFieldLimitLength() &&
                                tableFiledEntity.getFieldLimitSize() != null && tableFiledEntity.getFieldLimitSize() > 0) {
                            sb.append(StringUtil.firstToLowerCase(transformTableInfo.getTableName())).append(".set")
                                    .append(StringUtil.firstToUpperCase(tableFiledEntity.getName()))
                                    .append("(").append(tableFiledEntity.getName()).append(");\n\t\t");
                        }
                    }
                    sb.append("Specification<").append(transformTableInfo.getTableName()).append("> specification=querySpecification(")
                            .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName())).append(");\n\t\t");
                    sb.append("return ")
                            .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                            .append("Repository.findAll(specification, PageRequest.of(page, pageSize));\n\t}\n\n\t@Override\n\tpublic ")
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
                                .append("==null){\n\t\t\t")
                                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append(" = ").append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append("Repository.findById")
                                .append("(")
                                .append(priKey.getName())
                                .append(").orElse(null);\n\t\t\tif(")
                                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append("!=null){\n\t\t\t\t")
                                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append("RedisTemplate.opsForValue().set(").append(priKey.getName()).append(",")
                                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append(");\n\t\t\t}\n\t\t}\n\t\treturn ")
                                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append(";\n\t}\n\n");
                    } else {
                        sb.append("return ").append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append("Repository.findById(")
                                .append(priKey.getName())
                                .append(").orElse(null);\n\t}\n\n");
                    }
                    sb.append("\t@Override\n\tpublic void delete")
                            .append(transformTableInfo.getTableName())
                            .append("By")
                            .append(StringUtil.firstToUpperCase(priKey.getName()))
                            .append("(")
                            .append(priKey.getType())
                            .append(" ")
                            .append(priKey.getName())
                            .append("){\n\t\t");
                    if (springBootCli.isUseRedis()) {
                        sb.append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append("Repository.deleteById")
                                .append("(")
                                .append(priKey.getName())
                                .append(");\n\t\t")
                                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append("RedisTemplate.delete(")
                                .append(priKey.getName())
                                .append(");\n\t}\n\n");
                    } else {
                        sb.append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append("Repository.deleteById(")
                                .append(priKey.getName())
                                .append(");\n\t}\n\n");
                    }

                    sb.append("\t@Override\n\tpublic void delete")
                            .append(transformTableInfo.getTableName())
                            .append("s(List<")
                            .append(transformTableInfo.getTableName())
                            .append("> ")
                            .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                            .append("s){\n\t\t");
                    if (springBootCli.isUseRedis()) {
                        sb.append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append("Repository.deleteInBatch(")
                                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append("s);\n\t\t")
                                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append("RedisTemplate.delete(")
                                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName())).append("s.stream().map(")
                                .append(transformTableInfo.getTableName())
                                .append("::get")
                                .append(StringUtil.firstToUpperCase(priKey.getName()))
                                .append(").collect(Collectors.toList())")
                                .append(");\n\t}\n\n");
                    } else {
                        sb.append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append("Repository.deleteInBatch(")
                                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append("s);\n\t}\n\n");
                    }

                    sb.append("\t@Override\n\tpublic ").append(transformTableInfo.getTableName()).append(" update")
                            .append(transformTableInfo.getTableName())
                            .append("(")
                            .append(transformTableInfo.getTableName())
                            .append(" ")
                            .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                            .append("){\n\t\t");
                    if (springBootCli.isUseRedis()) {
                        sb.append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append("Repository.deleteById(")
                                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append(".get")
                                .append(StringUtil.firstToUpperCase(priKey.getName()))
                                .append("());\n\t\treturn ")
                                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append("Repository.saveAndFlush(")
                                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append(");\n\t}\n\n");
                    } else {
                        sb.append("return ")
                                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append("Repository.saveAndFlush(")
                                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                .append(");\n\t}\n\n");
                    }
                    sb.append("\tpublic Specification<")
                            .append(transformTableInfo.getTableName())
                            .append("> querySpecification(")
                            .append(transformTableInfo.getTableName()).append(" ")
                            .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                            .append(") {\n\t\treturn (Specification<")
                            .append(transformTableInfo.getTableName())
                            .append(">) (root, query, cb) -> {\n\t\t\tList<Predicate> predicates = new ArrayList<>();");
                    for (int x = 1; x < filedEntities.size(); x++) {
                        TableFiledEntity tableFiledEntity = filedEntities.get(x);
                        if (tableFiledEntity.getType().equals("String") && tableFiledEntity.getFieldLimitSize() < springBootCli.getQueryFieldLimitLength() &&
                                tableFiledEntity.getFieldLimitSize() != null && tableFiledEntity.getFieldLimitSize() > 0) {
                            sb.append("\n\t\t\tif(")
                                    .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName())).append(".get")
                                    .append(StringUtil.firstToUpperCase(tableFiledEntity.getName()))
                                    .append("() != null) {\n\t\t\t\tpredicates.add(cb.equal(root.get(\"")
                                    .append(tableFiledEntity.getName()).append("\"), \"%\" + ")
                                    .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                    .append(".get").append(StringUtil.firstToUpperCase(tableFiledEntity.getName()))
                                    .append("() + \"%\"));\n\t\t\t}");
                        }
                    }
                    sb.append("\n\t\t\treturn cb.and(predicates.toArray(new Predicate[predicates.size()]));\n\t\t};\n\t}");
                });
        sb.append("\n}");
        return sb.toString();
    }

    private String tableInfo2Service(TableInfo tableInfo) {
        TableInfo transformTableInfo = tableInfo.getTransformTableInfo();
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(springBootCli.getPackageName()).append(".").append(getPackageName())
                .append(";\n\nimport ").append(springBootCli.getPackageName()).append(".entity.")
                .append(transformTableInfo.getTableName()).append(";\nimport java.util.List;\nimport org.springframework.data.domain.Page;\n\npublic interface ")
                .append(transformTableInfo.getTableName()).append("Service {\n\n");
        List<TableFiledEntity> filedEntities = transformTableInfo.getFiledEntities();
        filedEntities.stream()
                .filter(tableFiledEntity -> tableFiledEntity.getKey().equals("PRI"))
                .findFirst()
                .ifPresent(priKey -> {
                            sb.append("\t").append(transformTableInfo.getTableName()).append(" insert")
                                    .append(transformTableInfo.getTableName())
                                    .append("(")
                                    .append(transformTableInfo.getTableName())
                                    .append(" ")
                                    .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                    .append(");\n\n\t");
                            sb.append("Page<").append(transformTableInfo.getTableName()).append("> queryAll")
                                    .append(transformTableInfo.getTableName()).append("(");
                            for (int x = 1; x < filedEntities.size(); x++) {
                                TableFiledEntity tableFiledEntity = filedEntities.get(x);
                                if (tableFiledEntity.getType().equals("String") && tableFiledEntity.getFieldLimitSize() < springBootCli.getQueryFieldLimitLength() &&
                                        tableFiledEntity.getFieldLimitSize() != null && tableFiledEntity.getFieldLimitSize() > 0) {
                                    sb.append("String ").append(tableFiledEntity.getName()).append(",");
                                }
                            }
                            sb.append("Integer page,Integer pageSize);\n\n\t");
                            sb.append(transformTableInfo.getTableName())
                                    .append(" query")
                                    .append(transformTableInfo.getTableName())
                                    .append("By")
                                    .append(StringUtil.firstToUpperCase(priKey.getName()))
                                    .append("(")
                                    .append(priKey.getType())
                                    .append(" ")
                                    .append(priKey.getName())
                                    .append(");\n\n\tvoid delete")
                                    .append(transformTableInfo.getTableName())
                                    .append("By")
                                    .append(StringUtil.firstToUpperCase(priKey.getName()))
                                    .append("(")
                                    .append(priKey.getType())
                                    .append(" ")
                                    .append(priKey.getName())
                                    .append(");\n\n\tvoid delete")
                                    .append(transformTableInfo.getTableName())
                                    .append("s(List<")
                                    .append(transformTableInfo.getTableName())
                                    .append("> ")
                                    .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                    .append("s);\n\n\t")
                                    .append(transformTableInfo.getTableName())
                                    .append(" update")
                                    .append(transformTableInfo.getTableName())
                                    .append("(")
                                    .append(transformTableInfo.getTableName())
                                    .append(" ")
                                    .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                                    .append(");\n");
                        }
                );
        sb.append("\n}");
        return sb.toString();
    }

    @Override
    protected String getPackageName() {
        return "service";
    }
}
