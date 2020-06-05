package com.lq.interceptor;

import com.lq.SpringBootCli;
import com.lq.entity.TableInfo;

import java.util.List;

public interface MybatisInterceptor {

    void handle(SpringBootCli cli, List<TableInfo> tableInfos);
}
