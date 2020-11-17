package com.lq.mybaits;

import com.lq.SpringBootCli;
import com.lq.entity.TableInfo;
import com.lq.glob.CheckDependency;
import com.lq.glob.task.CreateApplicationXmlTask;
import com.lq.glob.task.CreatePomXmlTask;
import com.lq.glob.task.CreateRedisConfigTask;
import com.lq.glob.task.CreateTemplateTask;
import com.lq.mybaits.interceptor.MapperJavaInterceptor;
import com.lq.mybaits.interceptor.MapperXmlInterceptor;
import com.lq.mybaits.interceptor.MybatisInterceptor;
import com.lq.mybaits.interceptor.ServiceJavaInterceptor;
import com.lq.mybaits.task.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Mybatis {
    
    private SpringBootCli springBootCli;

    public Mybatis(SpringBootCli springBootCli){
        this.springBootCli = springBootCli;
    }

    public void initSpringBoot(String... filterTableNames) throws Exception {
        new CreatePomXmlTask(springBootCli, SpringBootCli.FrameModel.MYBATIS).execute();
        new CreateApplicationXmlTask(springBootCli, SpringBootCli.FrameModel.MYBATIS).execute();
        new CreateTemplateTask(springBootCli, SpringBootCli.FrameModel.MYBATIS).execute();
        List<String> tableNameList = Arrays.asList(filterTableNames);
        List<TableInfo> tableInfos = new CreateJavaBeanTask(springBootCli).execute(tableInfo -> !tableNameList.contains(tableInfo.getTableName()));
        create(null, tableInfos);
    }

    public void create(String... tableNames) throws Exception {
        create(null, tableNames);
    }

    public void create(List<MybatisInterceptor> mybatisInterceptors, String... tableNames) throws Exception {
        List<String> tableNameList = Arrays.asList(tableNames);
        List<TableInfo> tableInfos = new CreateJavaBeanTask(springBootCli).execute(tableInfo -> tableNameList.contains(tableInfo.getTableName()));
        create(mybatisInterceptors, tableInfos);
    }

    private void create(List<MybatisInterceptor> mybatisInterceptors, List<TableInfo> tableInfos) throws Exception {
        Optional.ofNullable(mybatisInterceptors)
                .orElse(Collections.emptyList())
                .stream()
                .filter(mybatisInterceptor -> mybatisInterceptor instanceof MapperJavaInterceptor)
                .findFirst().ifPresent(mapperJavaInterceptor -> mapperJavaInterceptor.handle(springBootCli, tableInfos));
        new CreateMapperTask(springBootCli, tableInfos).execute();
        Optional.ofNullable(mybatisInterceptors)
                .orElse(Collections.emptyList())
                .stream()
                .filter(mybatisInterceptor -> mybatisInterceptor instanceof MapperXmlInterceptor)
                .findFirst().ifPresent(mapperXmlInterceptor -> mapperXmlInterceptor.handle(springBootCli, tableInfos));
        new CreateMapperXmlTask(springBootCli, tableInfos).execute();
        Optional.ofNullable(mybatisInterceptors)
                .orElse(Collections.emptyList())
                .stream()
                .filter(mybatisInterceptor -> mybatisInterceptor instanceof ServiceJavaInterceptor)
                .findFirst().ifPresent(serviceJavaInterceptor -> serviceJavaInterceptor.handle(springBootCli, tableInfos));
        new CreateServiceTask(springBootCli, tableInfos).execute();
        if (springBootCli.isUseRedis()) {
            new CheckDependency(springBootCli).execute(CheckDependency.REDIS_FLAG);
            new CreateRedisConfigTask(springBootCli, tableInfos).execute();
        }
        new CreateControllerTask(springBootCli, tableInfos).execute();
    }

}
