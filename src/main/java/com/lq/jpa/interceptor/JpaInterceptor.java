package com.lq.jpa.interceptor;

import com.lq.SpringBootCli;
import com.lq.entity.TableInfo;

import java.util.List;

public interface JpaInterceptor {

    void handle(SpringBootCli cli, List<TableInfo> tableInfos);
}
