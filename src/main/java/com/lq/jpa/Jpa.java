package com.lq.jpa;

import com.lq.SpringBootCli;
import com.lq.entity.TableInfo;
import com.lq.glob.CheckDependency;
import com.lq.glob.task.CreateApplicationXmlTask;
import com.lq.glob.task.CreatePomXmlTask;
import com.lq.glob.task.CreateRedisConfigTask;
import com.lq.glob.task.CreateTemplateTask;
import com.lq.jpa.interceptor.JpaInterceptor;
import com.lq.jpa.interceptor.ServiceJavaInterceptor;
import com.lq.jpa.task.CreateControllerTask;
import com.lq.jpa.task.CreateJavaBeanTask;
import com.lq.jpa.task.CreateServiceTask;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Jpa {

    private SpringBootCli springBootCli;

    public Jpa(SpringBootCli springBootCli) {
        this.springBootCli = springBootCli;
    }

    public void initSpringBoot(String... filterTableNames) throws Exception {
        new CreatePomXmlTask(springBootCli,SpringBootCli.FrameModel.JPA).execute();
        new CreateApplicationXmlTask(springBootCli,SpringBootCli.FrameModel.JPA).execute();
        new CreateTemplateTask(springBootCli,SpringBootCli.FrameModel.JPA).execute();
        List<String> tableNameList = Arrays.asList(filterTableNames);
        List<TableInfo> tableInfos = new CreateJavaBeanTask(springBootCli).execute(tableInfo -> !tableNameList.contains(tableInfo.getTableName()));
        create(null,tableInfos);
    }

    public void create(String... tableNames) throws Exception {
        create(null, tableNames);
    }

    public void create(List<JpaInterceptor> jpaInterceptors, String... tableNames) throws Exception {
        List<String> tableNameList = Arrays.asList(tableNames);
        List<TableInfo> tableInfos = new CreateJavaBeanTask(springBootCli).execute(tableInfo -> tableNameList.contains(tableInfo.getTableName()));
        create(jpaInterceptors, tableInfos);
    }

    public void create(List<JpaInterceptor> jpaInterceptors, List<TableInfo> tableInfos) throws Exception {
        Optional.ofNullable(jpaInterceptors)
                .orElse(Collections.emptyList())
                .stream()
                .filter(jpaInterceptor -> jpaInterceptor instanceof ServiceJavaInterceptor)
                .findFirst().ifPresent(serviceJavaInterceptor -> serviceJavaInterceptor.handle(springBootCli, tableInfos));
        new CreateServiceTask(springBootCli, tableInfos).execute();
        if (springBootCli.isUseRedis()) {
            new CheckDependency(springBootCli).execute(CheckDependency.REDIS_FLAG);
            new CreateRedisConfigTask(springBootCli, tableInfos).execute();
        }
        new CreateControllerTask(springBootCli, tableInfos).execute();
    }

}
