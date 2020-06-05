package com.lq.task.mybatis;


import com.lq.SpringBootCli;
import com.lq.entity.TableFiledEntity;
import com.lq.entity.TableInfo;
import com.lq.util.FileUtil;
import com.lq.util.StringUtil;

import java.io.File;
import java.util.List;

public final class CreateRedisConfigTask extends BaseTask<Boolean> {

    private List<TableInfo> tableInfos;

    public CreateRedisConfigTask(SpringBootCli springBootCli, List<TableInfo> tableInfos) {
        super(springBootCli);
        this.tableInfos = tableInfos;
    }

    @Override
    public Boolean execute() throws Exception {
        if (checkDir()) {
            File file = new File(springBootCli.getRootPackagePath() + getPackageName() + File.separator + "RedisConfig.java");
            if (file.exists()) {
                StringBuilder fileContentSb = FileUtil.readFileContent(file);
                if (fileContentSb != null && fileContentSb.length() > 0) {
                    int index = fileContentSb.indexOf("@Configuration");
                    StringBuilder redisConfigPrefix = new StringBuilder(fileContentSb.substring(0, index));
                    StringBuilder redisConfigContent = new StringBuilder(fileContentSb.substring(index, fileContentSb.lastIndexOf("}")));
                    for (TableInfo tableInfo : tableInfos) {
                        TableInfo transformTableInfo = tableInfo.getTransformTableInfo();
                        if (!redisConfigContent.toString().contains(StringUtil.firstToLowerCase(transformTableInfo.getTableName()) + "RedisTemplate")) {
                            writeRedis(redisConfigPrefix, redisConfigContent, transformTableInfo);
                        }
                    }
                    redisConfigContent.append("\n}");
                    redisConfigPrefix.append("\n").append(redisConfigContent);
                    FileUtil.createWriteFile(file, redisConfigPrefix.toString());
                }
            } else {
                StringBuilder redisConfigPrefix = new StringBuilder();
                redisConfigPrefix.append("package ").append(springBootCli.getPackageName()).append(".").append(getPackageName())
                        .append(";\n\nimport org.springframework.context.annotation.Configuration;\n")
                        .append("import org.springframework.context.annotation.Bean;\n")
                        .append("import org.springframework.data.redis.connection.RedisConnectionFactory;\n")
                        .append("import org.springframework.data.redis.core.RedisTemplate;\n")
                        .append("import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;\n")
                        .append("import org.springframework.data.redis.serializer.StringRedisSerializer;\n");

                StringBuilder redisConfigContent = new StringBuilder("\n@Configuration\npublic class RedisConfig {\n\n");
                for (TableInfo tableInfo : tableInfos) {
                    TableInfo transformTableInfo = tableInfo.getTransformTableInfo();
                    writeRedis(redisConfigPrefix, redisConfigContent, transformTableInfo);
                }
                redisConfigContent.append("\n}");
                redisConfigPrefix.append(redisConfigContent);
                createFile("RedisConfig.java", redisConfigPrefix.toString());
            }

        }
        return true;
    }

    private void writeRedis(StringBuilder redisConfigPrefix, StringBuilder redisConfigContent, TableInfo transformTableInfo) {
        redisConfigPrefix.append("import ").append(springBootCli.getPackageName()).append(".entity.")
                .append(transformTableInfo.getTableName()).append(";\n");
        TableFiledEntity pri = transformTableInfo.getFiledEntities().stream()
                .filter(tableFiledEntity -> tableFiledEntity.getKey().equals("PRI"))
                .findFirst().orElseGet(TableFiledEntity::new);
        String redisKeyType = "Object";
        if (pri.getType() != null) {
            redisKeyType = pri.getType();
        }
        redisConfigContent.append("\t@Bean\n\tpublic RedisTemplate<").append(redisKeyType).append(", ")
                .append(transformTableInfo.getTableName()).append(">  ")
                .append(StringUtil.firstToLowerCase(transformTableInfo.getTableName()))
                .append("RedisTemplate(\n\t\t\tRedisConnectionFactory redisConnectionFactory) {\n\t\tRedisTemplate<")
                .append(redisKeyType).append(", ")
                .append(transformTableInfo.getTableName())
                .append("> template = new RedisTemplate<>();\n\t\ttemplate.setKeySerializer(new StringRedisSerializer());\n\t\ttemplate.setConnectionFactory(redisConnectionFactory);\n\t\tJackson2JsonRedisSerializer<")
                .append(transformTableInfo.getTableName())
                .append("> ser = new Jackson2JsonRedisSerializer<>(")
                .append(transformTableInfo.getTableName()).append(".class);\n\t\ttemplate.setDefaultSerializer(ser);\n\t\treturn template;\n\t}\n\n");
    }

    @Override
    String getPackageName() {
        return "config";
    }
}
