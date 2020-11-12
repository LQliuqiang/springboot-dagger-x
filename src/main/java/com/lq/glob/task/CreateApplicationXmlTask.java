package com.lq.glob.task;

import com.lq.SpringBootCli;
import com.lq.util.FileUtil;

import java.io.File;

/**
 * 创建application.yml配置文件
 */
public final class CreateApplicationXmlTask {

    private SpringBootCli springBootCli;
    private String frameModel;

    public CreateApplicationXmlTask(SpringBootCli springBootCli, String frameModel) {
        this.springBootCli = springBootCli;
        this.frameModel = frameModel;
    }

    public void execute() throws Exception {
        String path = springBootCli.getProjectPath() + File.separator + "src" + File.separator + "main"
                + File.separator + "resources" + File.separator + "application.yml";
        File file = new File(path);

        StringBuilder sb = new StringBuilder();
        sb.append("spring: \n  datasource: \n    url: ")
                .append(springBootCli.getJdbcConfigEntity().getUrl())
                .append("\n    driver-class-name: ").append(springBootCli.getJdbcConfigEntity().getDriverClassName())
                .append("\n    hikari: \n      username: ")
                .append(springBootCli.getJdbcConfigEntity().getUsername())
                .append("\n      password: ")
                .append(springBootCli.getJdbcConfigEntity().getPassword());

        if (springBootCli.isUseRedis()) {
            sb.append("\n  redis: \n    host: ").append(springBootCli.getJdbcConfigEntity().getHost())
                    .append("\n    port: ").append("6379")
                    .append("\n    database: ").append("0")
                    .append("\n    timeout: ").append("3000")
                    .append("\n    jedis: \n      pool: ")
                    .append("\n        max-idle: ").append("100")
                    .append("\n        min-idle: ").append("50")
                    .append("\n        max-active: ").append("150")
                    .append("\n    password: ").append("123456");
        }
        if (frameModel.equals(SpringBootCli.FrameModel.MYBATIS)) {
            sb.append("\n\nmybatis:\n  mapper-locations: classpath:mapper/*.xml\n  configuration: \n    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl\n    map-underscore-to-camel-case: true");
        }
        FileUtil.createWriteFile(file, sb.toString());
    }

}
