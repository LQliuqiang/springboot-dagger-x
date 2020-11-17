package com.lq.jpa.task;

import com.lq.SpringBootCli;
import com.lq.entity.TableFiledEntity;
import com.lq.entity.TableInfo;
import com.lq.glob.task.BaseTask;
import com.lq.util.JdbcUtil;
import com.lq.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CreateJavaBeanTask extends BaseTask<List<TableInfo>> {

    private List<String> numberType = Arrays.asList("tinyint", "smallint", "mediumint", "int", "bigint", "float", "double", "decimal");

    private final JdbcUtil jdbcUtil;

    public CreateJavaBeanTask(SpringBootCli springBootCli) {
        super(springBootCli);
        jdbcUtil = new JdbcUtil(springBootCli.getJdbcConfigEntity());
    }

    @Override
    public List<TableInfo> execute() throws Exception {
        return null;
    }


    @Override
    protected String getPackageName() {
        return "entity";
    }

    public List<TableInfo> execute(Predicate<? super TableInfo> predicate) throws Exception {
        if (checkDir()) {
            List<TableInfo> tableInfos = jdbcUtil.queryTableInfo();
            if (tableInfos != null && tableInfos.size() > 0) {
                tableInfos = tableInfos.stream()
                        .filter(predicate)
                        .collect(Collectors.toList());
                for (TableInfo tableInfoItem : tableInfos) {
                    TableInfo tableInfo = transformTableInfo(tableInfoItem);
                    TableFiledEntity pri = tableInfo.getTransformTableInfo().getFiledEntities().stream()
                            .filter(tableFiledEntity -> tableFiledEntity.getKey().equals("PRI"))
                            .findFirst().orElse(null);
                    if (pri!=null){
                        String javaBean = tableInfo2JavaBean(tableInfo);
                        createFile(tableInfo.getTransformTableInfo().getTableName() + ".java", javaBean);
                    }
                }
            }
            return tableInfos;
        }
        return Collections.emptyList();
    }

    private TableInfo transformTableInfo(TableInfo tableInfo) {
        String tableInfoTableName = tableInfo.getTableName();
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
        tableInfo.setTransformTableInfo(new TableInfo(tableName, tableFiledEntities));
        return tableInfo;
    }

    private String tableInfo2JavaBean(TableInfo tableInfo) {
        TableInfo transformTableInfo = tableInfo.getTransformTableInfo();
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(springBootCli.getPackageName()).append(".").append(getPackageName())
                .append(";\n\nimport org.hibernate.validator.constraints.Length;\nimport javax.persistence.*;\nimport javax.validation.constraints.NotBlank;\n\n");
        sb.append("@Entity\n@Table(name = \"")
                .append(tableInfo.getTableName())
                .append("\")\npublic class ").append(transformTableInfo.getTableName()).append(" {\n\n");
        List<TableFiledEntity> filedEntities = transformTableInfo.getFiledEntities();
        for (int i = 0; i < filedEntities.size(); i++) {
            TableFiledEntity tableFiledEntity = filedEntities.get(i);
            if (!tableFiledEntity.getKey().equals("PRI") && tableFiledEntity.getaNull().equals("NO") && tableFiledEntity.getType().equals("String")) {
                sb.append("\t@NotBlank(message=\"").append(tableFiledEntity.getName()).append("不能为空\")\n");
                if (tableFiledEntity.getFieldLimitSize() != 0) {
                    sb.append("\t@Length(max = ").append(tableFiledEntity.getFieldLimitSize()).append(",message = \"")
                            .append(tableFiledEntity.getName()).append("长度不能超过")
                            .append(tableFiledEntity.getFieldLimitSize()).append("位\")\n");
                }
            }
            String fieldName = StringUtil.firstIsUpperCase(tableFiledEntity.getName()) ? StringUtil.firstToLowerCase(tableFiledEntity.getName()) : tableFiledEntity.getName();
            if (tableFiledEntity.getKey().equals("PRI")) {
                sb.append("\t@Id\n");
                if ("auto_increment".equals(tableFiledEntity.getExtra())) {
                    sb.append("\t@GeneratedValue(strategy = GenerationType.IDENTITY)\n");
                }
            }
            sb.append("\t@Column(name=\"").append(tableInfo.getFiledEntities().get(i).getName()).append("\"");
            if (tableFiledEntity.getFieldLimitSize() != null && tableFiledEntity.getFieldLimitSize() > 0) {
                sb.append(",length = ").append(tableFiledEntity.getFieldLimitSize());
            }
            if (tableFiledEntity.getaNull().equals("NO")) {
                sb.append(",nullable = false");
            }
            if (tableFiledEntity.getaDefault() != null && tableFiledEntity.getaDefault().length() > 0) {
                sb.append(",columnDefinition =\"").append(tableFiledEntity.getaDefault()).append("\"");
            }
            sb.append(")\n\tprivate ").append(tableFiledEntity.getType())
                    .append(" ").append(fieldName).append(";\n");
        }
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
