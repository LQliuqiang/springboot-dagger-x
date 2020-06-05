package com.lq.task.mybatis;

import com.lq.SpringBootCli;
import com.lq.entity.TableInfo;
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
        if (checkDir()){
            for (TableInfo tableInfo : tableInfos) {
                String controller = tableInfo2Controller(tableInfo);
                createFile(tableInfo.getTransformTableInfo().getTableName()+"Controller.java",controller);
            }
        }
        return true;
    }

    @Override
    String getPackageName() {
        return "controller";
    }

    private String tableInfo2Controller(TableInfo tableInfo) {
        TableInfo transformTableInfo = tableInfo.getTransformTableInfo();
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(springBootCli.getPackageName()).append(".controller;\n\n")
                .append("import ").append(springBootCli.getPackageName()).append(".entity.").append(transformTableInfo.getTableName())
                .append(";\nimport ").append(springBootCli.getPackageName()).append(".util.CommentResponse")
                .append(";\nimport ").append(springBootCli.getPackageName()).append(".util.WebUtil")
                .append(";\nimport ").append(springBootCli.getPackageName()).append(".service.")
                .append(transformTableInfo.getTableName())
                .append("Service;\nimport org.springframework.validation.BindingResult;\nimport javax.validation.Valid;\nimport javax.annotation.Resource;\nimport org.springframework.web.bind.annotation.*;\n\n@RestController\n@RequestMapping(\"/")
                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                .append("\")\npublic class ")
                .append(transformTableInfo.getTableName()).append("Controller {\n\n\t@Resource\n\tprivate ")
                 .append(transformTableInfo.getTableName())
                .append("Service ").append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                .append("Service;\n").append("\n\t@PostMapping(value = \"insert")
                .append(transformTableInfo.getTableName())
                .append("\", consumes = \"application/json\", produces = \"application/json\")\n\tpublic CommentResponse insert")
                .append(transformTableInfo.getTableName())
                .append("(@RequestBody @Valid ")
                .append(transformTableInfo.getTableName())
                .append(" ")
                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                .append(", BindingResult result){\n\t\tif (result.getErrorCount() > 0) {\n\t\t\treturn WebUtil.bindingResult(result);\n\t\t} else {\n\t\t\treturn ")
                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                .append("Service.insert")
                .append(transformTableInfo.getTableName()).append("(")
                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName())).append(") ? CommentResponse.success() : CommentResponse.fail();\n\t\t}\n\t}\n\n");
        transformTableInfo.getFiledEntities().stream()
                .filter(tableFiledEntity -> tableFiledEntity.getKey().equals("PRI"))
                .findFirst()
                .ifPresent(priKey -> sb.append("\t@GetMapping(\"/query")
                        .append(transformTableInfo.getTableName())
                        .append("By")
                        .append(StringUtil.firstToUpperCase(priKey.getName()))
                        .append("\")\n\tpublic CommentResponse")
                        .append(" query")
                        .append(transformTableInfo.getTableName())
                        .append("By")
                        .append(StringUtil.firstToUpperCase(priKey.getName()))
                        .append("(")
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
                        .append("));\n\t}\n\n\t@DeleteMapping(\"/delete")
                        .append(transformTableInfo.getTableName())
                        .append("By")
                        .append(StringUtil.firstToUpperCase(priKey.getName()))
                        .append("\")\n\tpublic CommentResponse delete")
                        .append(transformTableInfo.getTableName())
                        .append("By")
                        .append(StringUtil.firstToUpperCase(priKey.getName()))
                        .append("(")
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
                        .append(")>0 ? CommentResponse.success() : CommentResponse.fail();\n\t}\n\n\n\t@PutMapping(value = \"update")
                        .append(transformTableInfo.getTableName())
                        .append("\", consumes = \"application/json\", produces = \"application/json\")\n\tpublic CommentResponse update")
                        .append(transformTableInfo.getTableName())
                        .append("(@RequestBody ")
                        .append(transformTableInfo.getTableName())
                        .append(" ")
                        .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                        .append("){\n\t\treturn ")
                        .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                        .append("Service.update")
                        .append(transformTableInfo.getTableName()).append("(")
                        .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName())).append(") ? CommentResponse.success() : CommentResponse.fail();\n\t}\n"));
        sb.append("\n}");
        return sb.toString();
    }

}
