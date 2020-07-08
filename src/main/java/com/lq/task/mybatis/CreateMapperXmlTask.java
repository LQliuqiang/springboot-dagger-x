package com.lq.task.mybatis;


import com.lq.SpringBootCli;
import com.lq.entity.TableFiledEntity;
import com.lq.entity.TableInfo;
import com.lq.task.BaseTask;
import com.lq.util.FileUtil;
import com.lq.util.StringUtil;

import java.io.File;
import java.util.List;

public final class CreateMapperXmlTask extends BaseTask<Boolean> {

    private List<TableInfo> tableInfos;

    public CreateMapperXmlTask(SpringBootCli springBootCli, List<TableInfo> tableInfos) {
        super(springBootCli);
        this.tableInfos = tableInfos;
    }


    public Boolean execute() throws Exception {
        String mapperResourcesPath = springBootCli.getProjectPath() + File.separator + "src" + File.separator + "main"
                + File.separator + getPackageName();
        File mapperDir = new File(mapperResourcesPath);
        if (!mapperDir.exists()) {
            boolean mkdirs = mapperDir.mkdirs();
            if (!mkdirs){
                return false;
            }
        }
        for (TableInfo tableInfo : tableInfos) {
            String mapperXml = tableInfo2MapperXml(tableInfo);
            File file = new File(mapperResourcesPath + tableInfo.getTransformTableInfo().getTableName()+ "Mapper.xml");
            if (!file.exists()||springBootCli.isForceCover()){
                FileUtil.createWriteFile(file, mapperXml);
            }
        }
        return true;
    }

    @Override
    protected String getPackageName() {
        return "resources" + File.separator + "mapper" + File.separator;
    }


    private String tableInfo2MapperXml(TableInfo tableInfo) {
        TableInfo transformTableInfo = tableInfo.getTransformTableInfo();
        String mapperXmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<!DOCTYPE mapper\n" +
                "        PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\"\n" +
                "        \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n<mapper namespace=\"";
        StringBuilder sb = new StringBuilder(mapperXmlHeader);
        List<TableFiledEntity> filedEntities = tableInfo.getFiledEntities();
        StringBuilder sqlField = new StringBuilder();
        for (TableFiledEntity filedEntity : filedEntities) {
            sqlField.append(filedEntity.getName()).append(",");
        }
        sb.append(springBootCli.getPackageName()).append(".mapper.").append(transformTableInfo.getTableName())
                .append("Mapper").append("\">\n\n")
                .append("\t<sql id=\"")
                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                .append("ColumnSql\">\n\t\t")
                .append(sqlField.deleteCharAt(sqlField.length() - 1))
                .append("\n\t</sql>");
        List<TableFiledEntity> filedEntities2 = transformTableInfo.getFiledEntities();
        for (int i = 0; i < filedEntities2.size(); i++) {
            TableFiledEntity priKey = filedEntities2.get(i);
            if (priKey.getKey().equals("PRI")) {
                sb.append("\n\n\t<insert id=\"insert")
                        .append(transformTableInfo.getTableName())
                        .append("\" parameterType=\"").append(springBootCli.getPackageName()).append(".entity.")
                        .append(transformTableInfo.getTableName()).append("\"");
                if (priKey.getExtra().equals("auto_increment")) {
                    sb.append(" useGeneratedKeys=\"true\" keyProperty=\"")
                            .append(priKey.getName())
                            .append("\"");
                }
                sb.append(">\n\t\tinsert into ")
                        .append(tableInfo.getTableName())
                        .append("(<include refid=\"")
                        .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                        .append("ColumnSql\"/>)\n\t\tvalues(");
                for (TableFiledEntity filedEntity : filedEntities2) {
                    String fieldName = StringUtil.firstIsUpperCase(filedEntity.getName()) ? StringUtil.firstToLowerCase(filedEntity.getName()) : filedEntity.getName();
                    sb.append("#{").append(fieldName).append("},");
                }
                sb.deleteCharAt(sb.length() - 1).append(")\n\t</insert>");

                sb.append("\n\n\t <resultMap id=\"BaseResultMap\" type=\"")
                        .append(springBootCli.getPackageName()).append(".entity.")
                        .append(transformTableInfo.getTableName()).append("\">\n\t\t<id column=\"")
                        .append(filedEntities.get(i).getName()).append("\" property=\"")
                        .append(priKey.getName()).append("\"/>");
                for (int x = 1; x < filedEntities.size(); x++) {
                    sb.append("\n\t\t<result column=\"")
                            .append(filedEntities.get(x).getName())
                            .append("\" property=\"")
                            .append(filedEntities2.get(x).getName()).append("\"/>");
                }
                sb.append("\n\t</resultMap>")
                        .append("\n\n\t<select id=\"query")
                        .append(transformTableInfo.getTableName())
                        .append("By")
                        .append(StringUtil.firstToUpperCase(priKey.getName()))
                        .append("\" resultMap=\"BaseResultMap\">\n\t\tselect <include refid=\"")
                        .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                        .append("ColumnSql\"/>\n\t\tfrom ")
                        .append(tableInfo.getTableName())
                        .append("\n\t\twhere ")
                        .append(filedEntities.get(i).getName())
                        .append("=#{")
                        .append(priKey.getName())
                        .append("}\n\t</select>");
                if (springBootCli.isUsePage()) {
                    sb.append("\n\n\t<select id=\"queryAll")
                            .append(transformTableInfo.getTableName())
                            .append("\" resultMap=\"BaseResultMap\">\n\t\tselect <include refid=\"")
                            .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                            .append("ColumnSql\"/>\n\t\tfrom ")
                            .append(tableInfo.getTableName())
                            .append("\n\t\tlimit #{page},#{pageSize}\n\t</select>\n\n\t<select id=\"queryAll")
                            .append(transformTableInfo.getTableName()).append("Count\" resultType=\"java.lang.Integer\">\n\t\tselect count(*) from ")
                            .append(tableInfo.getTableName()).append("\n\t</select>");

                }
                sb.append("\n\n\t<delete id=\"delete")
                        .append(transformTableInfo.getTableName())
                        .append("By")
                        .append(StringUtil.firstToUpperCase(priKey.getName()))
                        .append("\">\n\t\tdelete from ")
                        .append(tableInfo.getTableName())
                        .append(" where ")
                        .append(filedEntities.get(i).getName())
                        .append("=#{")
                        .append(priKey.getName())
                        .append("}\n\t</delete>\n\n\t<update id=\"update")
                        .append(transformTableInfo.getTableName())
                        .append("\">\n\t\tupdate ")
                        .append(tableInfo.getTableName()).append("\n\t\t<trim prefix=\"set\" suffixOverrides=\",\"> \n\t\t\t");
                for (int x = 0; x < filedEntities2.size(); x++) {
                    TableFiledEntity filedEntity = filedEntities.get(x);
                    TableFiledEntity filedEntity1 = filedEntities2.get(x);
                    if (!filedEntity.getName().equals(priKey.getName())) {
                        if (filedEntity1.getType().equals("String")) {
                            sb.append("<if test=\"")
                                    .append(filedEntity1.getName())
                                    .append(" != null and  ")
                                    .append(filedEntity1.getName()).append(" != ''\">")
                                    .append(filedEntity.getName()).append("=#{").append(filedEntity1.getName());
                        } else {
                            sb.append("<if test=\" ").append(filedEntity1.getName())
                                    .append(" != null\">").append(filedEntity.getName())
                                    .append("=#{").append(filedEntity1.getName());
                        }
                        if (x == filedEntities2.size() - 1) {
                            sb.append("},</if>\n\t\t");
                        } else {
                            sb.append("},</if>\n\t\t\t");
                        }
                    }
                }
                sb.append("</trim>")
                        .append("\n\t\twhere ")
                        .append(filedEntities.get(i).getName())
                        .append("=#{")
                        .append(priKey.getName())
                        .append("}\n\t</update>");
                break;
            }
        }
        sb.append("\n\n</mapper>");
        return sb.toString();
    }

}
