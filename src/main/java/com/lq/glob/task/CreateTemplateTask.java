package com.lq.glob.task;

import com.lq.SpringBootCli;
import com.lq.util.FileUtil;

import java.io.File;
import java.io.IOException;

/**
 * 创建模板文件
 */
public final class CreateTemplateTask {

    private SpringBootCli springBootCli;
    private String frameModel;

    public CreateTemplateTask(SpringBootCli springBootCli, String frameModel) {
        this.springBootCli = springBootCli;
        this.frameModel = frameModel;
    }

    public void execute() throws IOException {
        if (frameModel.equals(SpringBootCli.FrameModel.MYBATIS)) {
            createLog4j();
        }
        createSpringBootApplication();
        createCorsConfigClass();
        createGlobExceptionHandler();
        createWebCommentResponse();
    }

    /**
     * 创建springboot启动文件
     *
     * @throws IOException
     */
    private void createSpringBootApplication() throws IOException {
        File file = new File(springBootCli.getRootPackagePath() + "Application.java");
        if (!file.exists()) {
            if (file.createNewFile()) {
                String sb = "package " + springBootCli.getPackageName() + ";" + "\n\n";
                sb += "import org.springframework.boot.SpringApplication;\nimport org.springframework.boot.autoconfigure.SpringBootApplication;\n";
                if (frameModel.equals(SpringBootCli.FrameModel.MYBATIS)) {
                    sb += "import org.mybatis.spring.annotation.MapperScan;\n\n";
                    sb += "@MapperScan(\"" +
                            springBootCli.getPackageName() +
                            ".mapper\")";
                }
                sb += "\n@SpringBootApplication\n" +
                        "public class Application {\n\n\tpublic static void main(String[] args){\n\t\tSpringApplication.run(Application.class, args);\n\t}\n}";
                FileUtil.createWriteFile(file, sb);
            }
        }
    }

    /**
     * 创建后端跨域文件
     *
     * @throws IOException
     */
    private void createCorsConfigClass() throws IOException {
        String configDirPath = springBootCli.getRootPackagePath() + "config" + File.separator;
        File configDir = new File(configDirPath);
        if (!configDir.exists()) {
            if (!configDir.mkdir()) {
                return;
            }
        }
        File file = new File(configDirPath + "CorsConfig.java");
        if (!file.exists()) {
            if (file.createNewFile()) {
                String corsConfig = "package " + springBootCli.getPackageName() +
                        ".config;\n\nimport org.springframework.context.annotation.Bean;" +
                        "\nimport org.springframework.context.annotation.Configuration;" +
                        "\nimport org.springframework.web.cors.CorsConfiguration;" +
                        "\nimport org.springframework.web.cors.UrlBasedCorsConfigurationSource;\nimport org.springframework.web.filter.CorsFilter;" +
                        "\n\n@Configuration\npublic class CorsConfig {\n\n\tprivate CorsConfiguration buildConfig() {\n\t\tCorsConfiguration corsConfiguration = new CorsConfiguration();\n\t\t" +
                        "corsConfiguration.addAllowedOrigin(\"*\");\n\t\tcorsConfiguration.addAllowedHeader(\"*\");\n\t\tcorsConfiguration.addAllowedMethod(\"*\");\n\t\treturn corsConfiguration;\n\t}" +
                        "\n\n\t@Bean\n\tpublic CorsFilter corsFilter() {\n\t\tUrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();" +
                        "\n\t\tsource.registerCorsConfiguration(\"/**\", buildConfig());\n\t\treturn new CorsFilter(source);\n\t}\n\n}";
                FileUtil.createWriteFile(file, corsConfig);
            }
        }

    }

    /**
     * 创建全局异常处理文件
     *
     * @throws IOException
     */
    private void createGlobExceptionHandler() throws IOException {
        String exceptionDirPath = springBootCli.getRootPackagePath() + "exception" + File.separator;
        File exceptionDir = new File(exceptionDirPath);
        if (!exceptionDir.exists()) {
            if (!exceptionDir.mkdir()) {
                return;
            }
        }
        File globExceptionHandlerFile = new File(exceptionDirPath + "GlobExceptionHandler.java");
        if (!globExceptionHandlerFile.exists()) {
            if (globExceptionHandlerFile.createNewFile()) {
                String globExceptionHandler = "package " + springBootCli.getPackageName() + ".exception;\n\n";
                globExceptionHandler += "import org.springframework.stereotype.Component;\n" +
                        "import org.springframework.web.servlet.HandlerExceptionResolver;\n" +
                        "import org.springframework.web.servlet.ModelAndView;\n" +
                        "\n" +
                        "import javax.servlet.http.HttpServletRequest;\n" +
                        "import javax.servlet.http.HttpServletResponse;\n" +
                        "import java.io.IOException;\n" +
                        "import java.io.PrintWriter;\n" +
                        "\n" +
                        "@Component\n" +
                        "public class GlobExceptionHandler implements HandlerExceptionResolver {\n" +
                        "\n" +
                        "    public static final ModelAndView MV = new ModelAndView();\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse response, Object handler, Exception ex) {\n" +
                        "        String jsonTemplate = clientJsonTemplate(ex.getMessage());\n" +
                        "        responseClientData(response,jsonTemplate);\n" +
                        "        return MV;\n" +
                        "    }\n" +
                        "\n" +
                        "    private static String clientJsonTemplate(String error) {\n" +
                        "        return \"{\\n\" +\n" +
                        "                \" \\\"errorInfo\\\":\\\"\" + error + \"\\\",\\n\" +\n" +
                        "                \" \\\"data\\\": null,\\n\" +\n" +
                        "                \" \\\"status\\\": 240\\n\" +\n" +
                        "                \"}\";\n" +
                        "    }\n" +
                        "\n" +
                        "    private void responseClientData(HttpServletResponse response, String json) {\n" +
                        "        response.setCharacterEncoding(\"UTF-8\");\n" +
                        "        response.setContentType(\"application/json; charset=utf-8\");\n" +
                        "        try (PrintWriter out = response.getWriter()) {\n" +
                        "            out.append(json);\n" +
                        "        } catch (IOException e) {\n" +
                        "            e.printStackTrace();\n" +
                        "        }\n" +
                        "    }\n" +
                        "\n" +
                        "}";
                FileUtil.createWriteFile(globExceptionHandlerFile, globExceptionHandler);
            }
        }
    }


    /**
     * 创建依赖web相关的文件
     *
     * @throws IOException
     */
    private void createWebCommentResponse() throws IOException {
        String utilDirPath = springBootCli.getRootPackagePath() + "util" + File.separator;
        File utilDir = new File(utilDirPath);
        if (!utilDir.exists()) {
            if (!utilDir.mkdir()) {
                return;
            }
        }
        File commentResponseFile = new File(utilDirPath + "CommentResponse.java");
        if (!commentResponseFile.exists()) {
            if (!commentResponseFile.createNewFile()) {
                return;
            } else {
                String commentResponse = "package " + springBootCli.getPackageName() + ".util;\n\n";
                commentResponse += "import java.net.HttpURLConnection;\n" +
                        "\n" +
                        "public class CommentResponse<T> {\n" +
                        "\n" +
                        "    private static final String SUCCESS_MSG = \"success\";\n" +
                        "    static final String FAIL_MSG = \"fail\";\n" +
                        "    static final int FAIL_CODE = 240;\n" +
                        "    private static final CommentResponse FAIL_COMMENT_RESPONSE = new CommentResponse<>(FAIL_CODE, null, FAIL_MSG);\n" +
                        "    private static final CommentResponse SUCCESS_COMMENT_RESPONSE = new CommentResponse<>(HttpURLConnection.HTTP_OK,SUCCESS_MSG,null );\n" +
                        "\n" +
                        "    private int status;\n" +
                        "\n" +
                        "    private T data;\n" +
                        "\n" +
                        "    private String errorInfo;\n" +
                        "\n" +
                        "    CommentResponse(int status, T data, String errorInfo) {\n" +
                        "        this.status = status;\n" +
                        "        this.data = data;\n" +
                        "        this.errorInfo = errorInfo;\n" +
                        "    }\n" +
                        "\n" +
                        "    public int getStatus() {\n" +
                        "        return status;\n" +
                        "    }\n" +
                        "\n" +
                        "    public void setStatus(int status) {\n" +
                        "        this.status = status;\n" +
                        "    }\n" +
                        "\n" +
                        "    public T getData() {\n" +
                        "        return data;\n" +
                        "    }\n" +
                        "\n" +
                        "    public void setData(T data) {\n" +
                        "        this.data = data;\n" +
                        "    }\n" +
                        "\n" +
                        "    public String getErrorInfo() {\n" +
                        "        return errorInfo;\n" +
                        "    }\n" +
                        "\n" +
                        "    public void setErrorInfo(String errorInfo) {\n" +
                        "        this.errorInfo = errorInfo;\n" +
                        "    }\n" +
                        "\n" +
                        "    public static <T> CommentResponse<T> success(T t) {\n" +
                        "        return new CommentResponse<>(HttpURLConnection.HTTP_OK, t, null);\n" +
                        "    }\n" +
                        "\n" +
                        "    public static CommentResponse success() {\n" +
                        "        return SUCCESS_COMMENT_RESPONSE;\n" +
                        "    }\n" +
                        "\n" +
                        "    public static CommentResponse fail() {\n" +
                        "        return FAIL_COMMENT_RESPONSE;\n" +
                        "    }\n" +
                        "\n" +
                        "    public static <T> CommentResponse<T> fail(String msg) {\n" +
                        "        return new CommentResponse<>(FAIL_CODE, null, msg);\n" +
                        "    }\n" +
                        "}";
                FileUtil.createWriteFile(commentResponseFile, commentResponse);
            }
        }
        if (frameModel.equals(SpringBootCli.FrameModel.MYBATIS)) {
            File commentPageResponseFile = new File(utilDirPath + "CommentPageResponse.java");
            if (!commentPageResponseFile.exists()) {
                if (commentPageResponseFile.createNewFile()) {
                    String commentPageResponse = "package " + springBootCli.getPackageName() + ".util;\n\n";
                    commentPageResponse += "import java.net.HttpURLConnection;\n" +
                            "\n" +
                            "public class CommentPageResponse<T> extends CommentResponse<T> {\n" +
                            "\n" +
                            "    private static final CommentPageResponse FAIL_COMMENT_RESPONSE =  new CommentPageResponse<>(FAIL_CODE, null, FAIL_MSG);\n" +
                            "\n" +
                            "    private Integer totalPage;\n" +
                            "    private Integer totalSize;\n" +
                            "    private Integer page;\n" +
                            "    private Integer pageSize;\n" +
                            "\n" +
                            "    private CommentPageResponse(int status, T data, String errorInfo) {\n" +
                            "        super(status, data, errorInfo);\n" +
                            "    }\n" +
                            "\n" +
                            "    private CommentPageResponse(int status, T data, String errorInfo, Integer totalPage, Integer totalSize, Integer page, Integer pageSize) {\n" +
                            "        super(status, data, errorInfo);\n" +
                            "        this.totalPage = totalPage;\n" +
                            "        this.totalSize = totalSize;\n" +
                            "        this.page = page;\n" +
                            "        this.pageSize = pageSize;\n" +
                            "    }\n" +
                            "\n" +
                            "    public Integer getTotalPage() {\n" +
                            "        return totalPage;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setTotalPage(Integer totalPage) {\n" +
                            "        this.totalPage = totalPage;\n" +
                            "    }\n" +
                            "\n" +
                            "    public Integer getTotalSize() {\n" +
                            "        return totalSize;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setTotalSize(Integer totalSize) {\n" +
                            "        this.totalSize = totalSize;\n" +
                            "    }\n" +
                            "\n" +
                            "    public Integer getPage() {\n" +
                            "        return page;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setPage(Integer page) {\n" +
                            "        this.page = page;\n" +
                            "    }\n" +
                            "\n" +
                            "    public Integer getPageSize() {\n" +
                            "        return pageSize;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setPageSize(Integer pageSize) {\n" +
                            "        this.pageSize = pageSize;\n" +
                            "    }\n" +
                            "\n" +
                            "    public static <T> CommentPageResponse<T> success(T t,Integer totalPage, Integer totalSize, Integer page, Integer pageSize) {\n" +
                            "        return new CommentPageResponse<>(HttpURLConnection.HTTP_OK, t, null,totalPage,totalSize,page,pageSize);\n" +
                            "    }\n" +
                            "\n" +
                            "    public static <T> CommentPageResponse<T> fail(String msg) {\n" +
                            "        return new CommentPageResponse<>(FAIL_CODE, null, msg);\n" +
                            "    }\n" +
                            "\n" +
                            "    public static <T> CommentPageResponse<T> selfFail() {\n" +
                            "        return  FAIL_COMMENT_RESPONSE;\n" +
                            "    }\n" +
                            "}";
                    FileUtil.createWriteFile(commentPageResponseFile, commentPageResponse);
                }
            }
        }
        File jacksonUtilFile = new File(utilDirPath + "JacksonUtil.java");
        if (!jacksonUtilFile.exists()) {
            if (jacksonUtilFile.createNewFile()) {
                String jacksonUtil = "package " + springBootCli.getPackageName() + ".util;\n\n";
                jacksonUtil += "import com.fasterxml.jackson.core.JsonProcessingException;\n" +
                        "import com.fasterxml.jackson.databind.JavaType;\n" +
                        "import com.fasterxml.jackson.databind.ObjectMapper;\n" +
                        "import javax.annotation.Resource;\n" +
                        "import org.springframework.stereotype.Component;\n" +
                        "\n" +
                        "import java.util.List;\n" +
                        "\n" +
                        "@Component\n" +
                        "public class JacksonUtil{\n" +
                        "\n" +
                        "    @Resource\n" +
                        "    private ObjectMapper objectMapper;\n" +
                        "\n" +
                        "    public String objectToJson(Object data) {\n" +
                        "        try {\n" +
                        "            return objectMapper.writeValueAsString(data);\n" +
                        "        } catch (JsonProcessingException e) {\n" +
                        "            System.err.println(e.toString());\n" +
                        "        }\n" +
                        "        return null;\n" +
                        "    }\n" +
                        "\n" +
                        "    public <T> T jsonToPojo(String jsonData, Class<T> beanType) {\n" +
                        "        try {\n" +
                        "            return objectMapper.readValue(jsonData, beanType);\n" +
                        "        } catch (Exception e) {\n" +
                        "            System.err.println(e.toString());\n" +
                        "        }\n" +
                        "        return null;\n" +
                        "    }\n" +
                        "\n" +
                        "    public <T> List<T> jsonToList(String jsonData, Class<T> beanType) {\n" +
                        "        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, beanType);\n" +
                        "        try {\n" +
                        "            return objectMapper.readValue(jsonData, javaType);\n" +
                        "        } catch (Exception e) {\n" +
                        "            System.err.println(e.toString());\n" +
                        "        }\n" +
                        "        return null;\n" +
                        "    }\n" +
                        "\n" +
                        "}\n";
                FileUtil.createWriteFile(jacksonUtilFile, jacksonUtil);
            }
        }

        File webUtilFile = new File(utilDirPath + "WebUtil.java");
        if (!webUtilFile.exists()) {
            if (webUtilFile.createNewFile()) {
                String webUtil = "package " + springBootCli.getPackageName() + ".util;\n\n";
                webUtil += "import org.springframework.validation.BindingResult;\n" +
                        "import org.springframework.validation.FieldError;\n" +
                        "import java.time.format.DateTimeFormatter;\n" +
                        "\n" +
                        "public class WebUtil {\n" +
                        "\n" +
                        "    private WebUtil(){}\n" +
                        "\n" +
                        "    public static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern(\"yyyy-MM-dd HH:mm:ss\");\n" +
                        "\n" +
                        "    public static CommentResponse bindingResult(BindingResult result){\n" +
                        "        StringBuilder sb = new StringBuilder();\n" +
                        "        for (FieldError fieldError : result.getFieldErrors()) {\n" +
                        "            sb.append(fieldError.getDefaultMessage()).append(\";\");\n" +
                        "        }\n" +
                        "        String errorInfo = sb.toString();\n" +
                        "        return CommentResponse.fail(errorInfo);\n" +
                        "    }\n" +
                        "\n" +
                        "}";
                FileUtil.createWriteFile(webUtilFile, webUtil);
            }
        }
    }


    /**
     * 创建Log4j的配追文件
     *
     * @throws IOException
     */
    private void createLog4j() throws IOException {
        String log4j2Path = springBootCli.getProjectPath() + File.separator + "src" + File.separator + "main"
                + File.separator + "resources" + File.separator + "log4j2-spring.xml";
        File log4j2File = new File(log4j2Path);
        if (!log4j2File.exists()) {
            if (log4j2File.createNewFile()) {
                String log4j = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<configuration status=\"warn\" monitorInterval=\"30\">\n" +
                        "    <Properties>\n" +
                        "        <property name=\"LOG_PATTERN\" value=\"%date{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n\" />\n" +
                        "        <property name=\"FILE_PATH\" value=\"log\" />\n" +
                        "    </Properties>\n" +
                        "\n" +
                        "    <appenders>\n" +
                        "        <console name=\"Console\" target=\"SYSTEM_OUT\">\n" +
                        "            <PatternLayout pattern=\"${LOG_PATTERN}\"/>\n" +
                        "            <ThresholdFilter level=\"info\" onMatch=\"ACCEPT\" onMismatch=\"DENY\"/>\n" +
                        "            <PatternLayout pattern=\"${LOG_PATTERN}\"/>\n" +
                        "        </console>\n" +
                        "\n" +
                        "        <RollingFile name=\"RollingFileInfo\" fileName=\"${sys:user.home}/logs/info.log\"\n" +
                        "                     filePattern=\"${sys:user.home}/logs/$${date:yyyy-MM}/info-%d{yyyy-MM-dd}-%i.log\">\n" +
                        "            <Filters>\n" +
                        "                <ThresholdFilter level=\"INFO\" onMatch=\"ACCEPT\" onMismatch=\"DENY\"/>\n" +
                        "                <ThresholdFilter level=\"WARN\" onMatch=\"DENY\" onMismatch=\"NEUTRAL\"/>\n" +
                        "            </Filters>\n" +
                        "            <PatternLayout pattern=\"${LOG_PATTERN}\"/>\n" +
                        "            <Policies>\n" +
                        "                <TimeBasedTriggeringPolicy/>\n" +
                        "                <SizeBasedTriggeringPolicy size=\"50 MB\"/>\n" +
                        "            </Policies>\n" +
                        "        </RollingFile>\n" +
                        "\n" +
                        "        <RollingFile name=\"RollingFileWarn\" fileName=\"${sys:user.home}/logs/warn.log\"\n" +
                        "                     filePattern=\"${sys:user.home}/logs/$${date:yyyy-MM}/warn-%d{yyyy-MM-dd}-%i.log\">\n" +
                        "            <Filters>\n" +
                        "                <ThresholdFilter level=\"WARN\" onMatch=\"ACCEPT\" onMismatch=\"DENY\"/>\n" +
                        "                <ThresholdFilter level=\"ERROR\" onMatch=\"DENY\" onMismatch=\"NEUTRAL\"/>\n" +
                        "            </Filters>\n" +
                        "            <PatternLayout pattern=\"${LOG_PATTERN}\"/>\n" +
                        "            <Policies>\n" +
                        "                <TimeBasedTriggeringPolicy/>\n" +
                        "                <SizeBasedTriggeringPolicy size=\"50 MB\"/>\n" +
                        "            </Policies>\n" +
                        "            <DefaultRolloverStrategy max=\"20\"/>\n" +
                        "        </RollingFile>\n" +
                        "\n" +
                        "        <RollingFile name=\"RollingFileError\" fileName=\"${sys:user.home}/logs/error.log\"\n" +
                        "                     filePattern=\"${sys:user.home}/logs/$${date:yyyy-MM}/error-%d{yyyy-MM-dd}-%i.log\">\n" +
                        "            <ThresholdFilter level=\"ERROR\"/>\n" +
                        "            <PatternLayout pattern=\"${LOG_PATTERN}\"/>\n" +
                        "            <Policies>\n" +
                        "                <TimeBasedTriggeringPolicy/>\n" +
                        "                <SizeBasedTriggeringPolicy size=\"50 MB\"/>\n" +
                        "            </Policies>\n" +
                        "        </RollingFile>\n" +
                        "    </appenders>\n" +
                        "\n" +
                        "\n" +
                        "    <loggers>\n" +
                        "        <logger name=\"org.springframework\" level=\"INFO\" additivity=\"false\">\n" +
                        "        </logger>\n" +
                        "        <logger name=\"org.hibernate\" level=\"INFO\" additivity=\"false\">\n" +
                        "        </logger>\n" +
                        "        <logger name=\"org.thymeleaf\" level=\"TRACE\" additivity=\"false\">\n" +
                        "        </logger>\n" +
                        "        <root level=\"all\">\n" +
                        "            <appender-ref ref=\"Console\"/>\n" +
                        "            <appender-ref ref=\"RollingFileInfo\"/>\n" +
                        "            <appender-ref ref=\"RollingFileWarn\"/>\n" +
                        "            <appender-ref ref=\"RollingFileError\"/>\n" +
                        "        </root>\n" +
                        "    </loggers>\n" +
                        "\n" +
                        "\n" +
                        "</configuration>";
                FileUtil.createWriteFile(log4j2File, log4j);
            }
        }
    }

}
