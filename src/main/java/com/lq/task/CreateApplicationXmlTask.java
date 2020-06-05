package com.lq.task;

import com.lq.SpringBootCli;
import com.lq.util.FileUtil;

import java.io.File;

public final class CreateApplicationXmlTask {

    private SpringBootCli springBootCli;

    public CreateApplicationXmlTask(SpringBootCli springBootCli) {
        this.springBootCli = springBootCli;
    }
    
    public void execute() throws Exception {
        String path = springBootCli.getProjectPath() + File.separator + "src" + File.separator + "main"
                + File.separator + "resources" + File.separator + "application.yml";
        File file = new File(path);

        StringBuilder sb = new StringBuilder();
        sb.append("spring: \n  datasource: \n    username: ")
                .append(springBootCli.getJdbcConfigEntity().getUsername())
                .append("\n    password: ").append(springBootCli.getJdbcConfigEntity().getPassword())
                .append("\n    url: ").append(springBootCli.getJdbcConfigEntity().getUrl())
                .append("\n    driver-class-name: ").append(springBootCli.getJdbcConfigEntity().getDriverClassName())
                 .append("\n    type: ").append("com.alibaba.druid.pool.DruidDataSource");
        if (springBootCli.isUseRedis()) {
            sb.append("\n  redis: \n    host: ").append("127.0.0.1")
                    .append("\n    port: ").append("6379")
                    .append("\n    database: ").append("0")
                    .append("\n    timeout: ").append("3000")
                    .append("\n    jedis: \n      pool: ")
                    .append("\n        max-idle: ").append("100")
                    .append("\n        min-idle: ").append("50")
                    .append("\n        max-active: ").append("150");
        }
        sb.append("\n\nmybatis:\n  mapper-locations: classpath:mapper/*.xml\n  configuration: \n    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl\n    map-underscore-to-camel-case: true");
        FileUtil.createWriteFile(file, sb.toString());
    }
    
}
