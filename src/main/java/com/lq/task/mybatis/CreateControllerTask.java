package com.lq.task.mybatis;



import com.lq.SpringBootCli;
import com.lq.entity.TableFiledEntity;
import com.lq.entity.TableInfo;
import com.lq.task.BaseTask;
import com.lq.util.StringUtil;

import java.util.List;

public final class CreateControllerTask extends BaseTask<Boolean> {

    private List<TableInfo> tableInfos;

    public CreateControllerTask(SpringBootCli springBootCli, List<TableInfo> tableInfos) {
        super(springBootCli);
        this.tableInfos = tableInfos;
    }

    @Override
    public Boolean execute() throws Exception {
        if (checkDir()) {
            for (TableInfo tableInfo : tableInfos) {
                String controller = tableInfo2Controller(tableInfo);
                createFile(tableInfo.getTransformTableInfo().getTableName() + "Controller.java", controller);
            }
        }
        return true;
    }

    @Override
    protected String getPackageName() {
        return "controller";
    }

    private String tableInfo2Controller(TableInfo tableInfo) {
        TableInfo transformTableInfo = tableInfo.getTransformTableInfo();
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(springBootCli.getPackageName()).append(".controller;\n\n")
                .append("import ").append(springBootCli.getPackageName()).append(".entity.").append(transformTableInfo.getTableName())
                .append(";\nimport ").append(springBootCli.getPackageName()).append(".util.CommentResponse");
        sb.append(";\nimport ").append(springBootCli.getPackageName()).append(".util.CommentPageResponse;\nimport java.util.List");
        sb.append(";\nimport ").append(springBootCli.getPackageName()).append(".util.WebUtil")
                .append(";\nimport ").append(springBootCli.getPackageName()).append(".service.")
                .append(transformTableInfo.getTableName())
                .append("Service;\nimport org.springframework.validation.BindingResult;\nimport javax.validation.Valid;\nimport javax.annotation.Resource;\nimport org.springframework.web.bind.annotation.*;\n\n@RestController\n@RequestMapping(\"/")
                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                .append("s\")\npublic class ")
                .append(transformTableInfo.getTableName()).append("Controller {\n\n\t@Resource\n\tprivate ")
                .append(transformTableInfo.getTableName())
                .append("Service ").append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                .append("Service;\n");
        List<TableFiledEntity> filedEntities = transformTableInfo.getFiledEntities();
        filedEntities.stream()
                .filter(tableFiledEntity -> tableFiledEntity.getKey().equals("PRI"))
                .findFirst()
                .ifPresent(priKey -> {
                    sb.append("\n\t@PostMapping(")
                            .append("consumes = \"application/json\", produces = \"application/json\")\n\tpublic CommentResponse insert")
                            .append(transformTableInfo.getTableName())
                            .append("(@RequestBody @Valid ")
                            .append(transformTableInfo.getTableName())
                            .append(" ")
                            .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                            .append(", BindingResult result){\n\t\tif (result.getErrorCount() > 0) {\n\t\t\treturn WebUtil.bindingResult(result);\n\t\t} else {\n\t\t\treturn ")
                            .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                            .append("Service.insert")
                            .append(transformTableInfo.getTableName()).append("(")
                            .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName())).append(") ? CommentResponse.success(")
                            .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName())).append(") : CommentResponse.fail();\n\t\t}\n\t}\n\n");

                    boolean conditionFlag = false;
                    for (int x = 1; x < filedEntities.size(); x++) {
                        if (filedEntities.get(x).getType().equals("String")) {
                            conditionFlag = true;
                            break;
                        }
                    }
                    sb.append("\t@GetMapping\n\tpublic CommentPageResponse queryAll")
                            .append(transformTableInfo.getTableName()).append("(");
                    for (int x = 1; x < filedEntities.size(); x++) {
                        TableFiledEntity tableFiledEntity = filedEntities.get(x);
                        if (tableFiledEntity.getType().equals("String")&&tableFiledEntity.getFieldLimitSize()<springBootCli.getQueryFieldLimitLength()) {
                            sb.append("@RequestParam(value = \"")
                                    .append(filedEntities.get(x).getName())
                                    .append("\", required = false) String ").append(filedEntities.get(x).getName()).append(",\n\t\t\t\t\t\t\t\t\t\t\t\t");
                        }
                    }
                    sb.append("@RequestParam(value = \"page\", required = false, defaultValue = \"1\") Integer page,\n\t\t\t\t\t\t\t\t\t\t\t\t@RequestParam(value = \"pageSize\", required = false, defaultValue = \"10\") Integer pageSize){\n\t\tList<")
                            .append(transformTableInfo.getTableName())
                            .append("> ").append(StringUtil.firstToLowerCase(transformTableInfo.getTableName())).append("s = ")
                            .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName())).append("Service.queryAll")
                            .append(transformTableInfo.getTableName()).append("(");
                    for (int x = 1; x < filedEntities.size(); x++) {
                        TableFiledEntity tableFiledEntity = filedEntities.get(x);
                        if (tableFiledEntity.getType().equals("String")&&tableFiledEntity.getFieldLimitSize()<springBootCli.getQueryFieldLimitLength()) {
                            sb.append(tableFiledEntity.getName()).append(",");
                        }
                    }
                    sb.append("page,pageSize);\n\t\tint totalCount = ")
                            .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName())).append("Service.queryAll")
                            .append(transformTableInfo.getTableName()).append("Count(");
                    for (int x = 1; x < filedEntities.size(); x++) {
                        TableFiledEntity tableFiledEntity = filedEntities.get(x);
                        Integer fieldLimitSize = tableFiledEntity.getFieldLimitSize();
                        if (tableFiledEntity.getType().equals("String")&&fieldLimitSize<springBootCli.getQueryFieldLimitLength()) {
                            sb.append(tableFiledEntity.getName()).append(",");
                        }
                    }
                    if (conditionFlag) {
                        sb.deleteCharAt(sb.lastIndexOf(","));
                    }
                    sb.append(");\n\t\tint totalPage = totalCount / pageSize + ((totalCount % pageSize > 0) ? 1 : 0);\n\t\treturn CommentPageResponse.success(")
                            .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName())).append("s, totalPage, totalCount, page, pageSize);\n\t}\n\n");
                    sb.append("\t@GetMapping(\"/{")
                            .append(priKey.getName())
                            .append("}")
                            .append("\")\n\tpublic CommentResponse")
                            .append(" query")
                            .append(transformTableInfo.getTableName())
                            .append("By")
                            .append(StringUtil.firstToUpperCase(priKey.getName())).append("(@PathVariable(\"")
                            .append(priKey.getName()).append("\")")
                            .append(priKey.getType())
                            .append(" ")
                            .append(priKey.getName())
                            .append("){\n\t\t")
                            .append("return CommentResponse.success(").append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                            .append("Service.query")
                            .append(transformTableInfo.getTableName())
                            .append("By")
                            .append(StringUtil.firstToUpperCase(priKey.getName()))
                            .append("(")
                            .append(priKey.getName())
                            .append("));\n\t}\n\n\t@DeleteMapping(\"/{")
                            .append(priKey.getName())
                            .append("}")
                            .append("\")\n\tpublic CommentResponse delete")
                            .append(transformTableInfo.getTableName())
                            .append("By")
                            .append(StringUtil.firstToUpperCase(priKey.getName()))
                            .append("(@PathVariable(\"")
                            .append(priKey.getName()).append("\")")
                            .append(priKey.getType())
                            .append(" ")
                            .append(priKey.getName())
                            .append("){\n\t\treturn ")
                            .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                            .append("Service.delete")
                            .append(transformTableInfo.getTableName())
                            .append("By")
                            .append(StringUtil.firstToUpperCase(priKey.getName()))
                            .append("(")
                            .append(priKey.getName())
                            .append(")>0 ? CommentResponse.success() : CommentResponse.fail();\n\t}\n\n\n\t@DeleteMapping\n\tpublic CommentResponse delete")
                            .append(transformTableInfo.getTableName())
                            .append("By")
                            .append(StringUtil.firstToUpperCase(priKey.getName()))
                            .append("s(@RequestParam(\"")
                            .append(priKey.getName())
                            .append("s\") List<")
                            .append(priKey.getType())
                            .append("> ")
                            .append(priKey.getName())
                            .append("s){\n\t\treturn ")
                            .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                            .append("Service.delete")
                            .append(transformTableInfo.getTableName())
                            .append("By")
                            .append(StringUtil.firstToUpperCase(priKey.getName()))
                            .append("s(")
                            .append(priKey.getName())
                            .append("s)>0 ? CommentResponse.success() : CommentResponse.fail();\n\t}\n\n\n\t@PutMapping(consumes = \"application/json\", produces = \"application/json\")\n\tpublic CommentResponse update")
                            .append(transformTableInfo.getTableName())
                            .append("(@RequestBody ")
                            .append(transformTableInfo.getTableName())
                            .append(" ")
                            .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                            .append("){\n\t\tif(")
                            .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                            .append(".get")
                            .append(StringUtil.firstToUpperCase(priKey.getName()))
                            .append("()==null){\n\t\t\treturn CommentResponse.fail(\"")
                            .append(priKey.getName()).append(" 字段不能为空\");\n\t\t}")
                            .append("\n\t\treturn ")
                            .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                            .append("Service.update")
                            .append(transformTableInfo.getTableName()).append("(")
                            .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName())).append(") ? CommentResponse.success() : CommentResponse.fail();\n\t}\n");
                });
        sb.append("\n}");
        return sb.toString();
    }

}
