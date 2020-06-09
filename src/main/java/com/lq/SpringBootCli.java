package com.lq;

import com.lq.entity.JdbcConfigEntity;
import com.lq.entity.TableInfo;
import com.lq.interceptor.MapperJavaInterceptor;
import com.lq.interceptor.MapperXmlInterceptor;
import com.lq.interceptor.MybatisInterceptor;
import com.lq.interceptor.ServiceJavaInterceptor;
import com.lq.task.CreateApplicationXmlTask;
import com.lq.task.CreatePomXmlTask;
import com.lq.task.CreateTemplateTask;
import com.lq.task.mybatis.*;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SpringBootCli {

    private JdbcConfigEntity jdbcConfigEntity;
    private String springBootVersion;
    private String mybatisVersion;
    private String mysqlConnectorVersion;
    private String druidVersion;
    private String fastJsonVersion;
    private String filterTableNameStr;
    private boolean generateController;
    private boolean useRedis;
    private boolean usePage;
    private boolean forceCover;
    private String projectPath;
    private String packageName;
    private String rootPackagePath;

    public SpringBootCli(Builder builder) {
        this.jdbcConfigEntity = builder.jdbcConfigEntity;
        this.springBootVersion = builder.springBootVersion;
        this.mybatisVersion = builder.mybatisVersion;
        this.mysqlConnectorVersion = builder.mysqlConnectorVersion;
        this.druidVersion = builder.druidVersion;
        this.fastJsonVersion = builder.fastJsonVersion;
        this.filterTableNameStr = builder.filterTableNameStr;
        this.generateController = builder.generateController;
        this.useRedis = builder.useRedis;
        this.usePage = builder.usePage;
        this.forceCover = builder.forceCover;
        String path = new File(builder.aClass.getResource("").getPath()).getPath();
        this.projectPath = path.substring(0, path.indexOf(File.separator + "target" + File.separator));
        this.packageName = builder.aClass.getPackage().getName();
        String filePath = this.projectPath + File.separator + "src" + File.separator + "main" + File.separator + "java" +
                File.separator + new File(builder.aClass.getName()).getPath().replace(".", File.separator) + ".java";
        this.rootPackagePath = filePath.replace(builder.aClass.getSimpleName() + ".java", "");
    }


    public void initSpringBoot(String... filterTableNames) throws Exception {
        new CreatePomXmlTask(this).execute();
        new CreateApplicationXmlTask(this).execute();
        new CreateTemplateTask(this).execute();
        List<String> tableNameList = Arrays.asList(filterTableNames);
        List<TableInfo> tableInfos = new CreateJavaBeanTask(this).execute(tableInfo -> !tableNameList.contains(tableInfo.getTableName()));
        createMybatis(null, tableInfos);
    }

    public void createMybatis(String... tableNames) throws Exception {
        createMybatis(null, tableNames);
    }

    public void createMybatis(List<MybatisInterceptor> mybatisInterceptors,String... tableNames) throws Exception {
        List<String> tableNameList = Arrays.asList(tableNames);
        List<TableInfo> tableInfos = new CreateJavaBeanTask(this).execute(tableInfo -> tableNameList.contains(tableInfo.getTableName()));
        createMybatis(mybatisInterceptors, tableInfos);
    }

    private void createMybatis(List<MybatisInterceptor> mybatisInterceptors, List<TableInfo> tableInfos) throws Exception {
        Optional.ofNullable(mybatisInterceptors)
                .orElse(Collections.emptyList())
                .stream()
                .filter(mybatisInterceptor -> mybatisInterceptor instanceof MapperJavaInterceptor)
                .findFirst().ifPresent(mapperJavaInterceptor -> mapperJavaInterceptor.handle(this, tableInfos));
        new CreateMapperTask(this, tableInfos).execute();
        Optional.ofNullable(mybatisInterceptors)
                .orElse(Collections.emptyList())
                .stream()
                .filter(mybatisInterceptor -> mybatisInterceptor instanceof MapperXmlInterceptor)
                .findFirst().ifPresent(mapperXmlInterceptor -> mapperXmlInterceptor.handle(this, tableInfos));
        new CreateMapperXmlTask(this, tableInfos).execute();
        Optional.ofNullable(mybatisInterceptors)
                .orElse(Collections.emptyList())
                .stream()
                .filter(mybatisInterceptor -> mybatisInterceptor instanceof ServiceJavaInterceptor)
                .findFirst().ifPresent(serviceJavaInterceptor -> serviceJavaInterceptor.handle(this, tableInfos));
        new CreateServiceTask(this, tableInfos).execute();
        if (this.useRedis) {
            new CreateRedisConfigTask(this, tableInfos).execute();
        }
        if (this.generateController) {
            new CreateControllerTask(this, tableInfos).execute();
        }
    }

    public void useRedis() throws Exception {
        this.useRedis = true;
        new CreatePomXmlTask(this).execute();
    }

    public JdbcConfigEntity getJdbcConfigEntity() {
        return jdbcConfigEntity;
    }

    public String getSpringBootVersion() {
        return springBootVersion;
    }

    public String getMybatisVersion() {
        return mybatisVersion;
    }

    public String getMysqlConnectorVersion() {
        return mysqlConnectorVersion;
    }

    public String getDruidVersion() {
        return druidVersion;
    }

    public String getFastJsonVersion() {
        return fastJsonVersion;
    }

    public String getFilterTableNameStr() {
        return filterTableNameStr;
    }

    public boolean isGenerateController() {
        return generateController;
    }

    public boolean isUseRedis() {
        return useRedis;
    }

    public String getProjectPath() {
        return projectPath;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getRootPackagePath() {
        return rootPackagePath;
    }

    public boolean isForceCover() {
        return forceCover;
    }

    public boolean isUsePage() {
        return usePage;
    }

    public void setGenerateController(boolean generateController) {
        this.generateController = generateController;
    }

    public void setUseRedis(boolean useRedis) {
        this.useRedis = useRedis;
    }

    public void setForceCover(boolean forceCover) {
        this.forceCover = forceCover;
    }

    public static class Builder {

        private Class<?> aClass;
        private JdbcConfigEntity jdbcConfigEntity;
        private String springBootVersion = "2.2.4.RELEASE";
        private String mybatisVersion = "2.1.2";
        private String mysqlConnectorVersion = "5.1.49";
        private String druidVersion = "1.1.22";
        private String fastJsonVersion = "1.2.47";
        private String filterTableNameStr;
        private boolean generateController;
        private boolean usePage;
        private boolean useRedis;
        private boolean forceCover;

        public Builder(Class<?> aClass, JdbcConfigEntity jdbcConfigEntity) {
            this.jdbcConfigEntity = jdbcConfigEntity;
            this.aClass = aClass;
        }

        public Builder springBootVersion(String springBootVersion) {
            this.springBootVersion = springBootVersion;
            return this;
        }

        public Builder mybatisVersion(String mybatisVersion) {
            this.mybatisVersion = mybatisVersion;
            return this;
        }

        public Builder mysqlConnectorVersion(String mysqlConnectorVersion) {
            this.mysqlConnectorVersion = mysqlConnectorVersion;
            return this;
        }

        public Builder druidVersion(String druidVersion) {
            this.druidVersion = druidVersion;
            return this;
        }

        public Builder fastJsonVersion(String fastJsonVersion) {
            this.fastJsonVersion = fastJsonVersion;
            return this;
        }

        public Builder generateController(boolean generateController) {
            this.generateController = generateController;
            return this;
        }

        public Builder useRedis(boolean useRedis) {
            this.useRedis = useRedis;
            return this;
        }

        public Builder setFilterTableNameStr(String filterTableNameStr) {
            this.filterTableNameStr = filterTableNameStr;
            return this;
        }

        public Builder setForceCover(boolean forceCover) {
            this.forceCover = forceCover;
            return this;
        }

        public Builder setUsePage(boolean usePage) {
            this.usePage = usePage;
            return this;
        }

        public SpringBootCli build() {
            return new SpringBootCli(this);
        }
    }

}
