package com.lq;

import com.lq.entity.JdbcConfigEntity;
import com.lq.jpa.Jpa;
import com.lq.mybaits.Mybatis;

import java.io.File;


public class SpringBootCli {

    public interface FrameModel{
        String MYBATIS = "mybatis";
        String JPA = "jpa";
    }


    private JdbcConfigEntity jdbcConfigEntity;
    private int queryCriteriaLimit;
    private boolean useRedis;
    //强制覆盖所有的
    private boolean forceCover;
    //项目名称路径，如：D:\beacon-project\springboot-dagger-project\springboot-dagger
    private String projectPath;
    //包路径，如：com.fii
    private String packageName;
    //项目包路径，如：D:\beacon-project\springboot-dagger-project\springboot-dagger\src\main\java\com\fii\
    private String rootPackagePath;

    public SpringBootCli(Builder builder) {
        this.jdbcConfigEntity = builder.jdbcConfigEntity;
        this.queryCriteriaLimit = builder.queryCriteriaLimit;
        this.useRedis = builder.useRedis;
        this.forceCover = builder.forceCover;
        String path = new File(builder.aClass.getResource("").getPath()).getPath();
        this.projectPath = path.substring(0, path.indexOf(File.separator + "target" + File.separator));
        this.packageName = builder.aClass.getPackage().getName();
        String filePath = this.projectPath + File.separator + "src" + File.separator + "main" + File.separator + "java" +
                File.separator + new File(builder.aClass.getName()).getPath().replace(".", File.separator) + ".java";
        this.rootPackagePath = filePath.replace(builder.aClass.getSimpleName() + ".java", "");
    }

    public Mybatis Mybatis() {
        return new Mybatis(this);
    }

    public Jpa Jpa() {
        return new Jpa(this);
    }

    public JdbcConfigEntity getJdbcConfigEntity() {
        return jdbcConfigEntity;
    }

    public int getQueryFieldLimitLength() {
        return queryCriteriaLimit;
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

    public void setUseRedis(boolean useRedis) {
        this.useRedis = useRedis;
    }

    public void setForceCover(boolean forceCover) {
        this.forceCover = forceCover;
    }

    public static class Builder {

        private Class<?> aClass;
        private JdbcConfigEntity jdbcConfigEntity;
        private boolean forceCover;
        private boolean useRedis;
        private int queryCriteriaLimit = 70;

        public Builder(Class<?> aClass, JdbcConfigEntity jdbcConfigEntity) {
            this.jdbcConfigEntity = jdbcConfigEntity;
            this.aClass = aClass;
        }

        public Builder setQueryCriteriaLimit(int queryCriteriaLimit) {
            this.queryCriteriaLimit = queryCriteriaLimit;
            return this;
        }

        public Builder useRedis(boolean useRedis) {
            this.useRedis = useRedis;
            return this;
        }


        public Builder setForceCover(boolean forceCover) {
            this.forceCover = forceCover;
            return this;
        }

        public SpringBootCli build() {
            return new SpringBootCli(this);
        }
    }

}
