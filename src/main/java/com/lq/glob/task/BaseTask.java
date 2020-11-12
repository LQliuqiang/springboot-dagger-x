package com.lq.glob.task;

import com.lq.SpringBootCli;
import com.lq.util.FileUtil;

import java.io.File;
import java.io.IOException;

public abstract class BaseTask<R> {

    protected SpringBootCli springBootCli;

    public BaseTask(SpringBootCli springBootCli) {
        this.springBootCli = springBootCli;
    }

    public abstract R execute() throws Exception;

    protected abstract String getPackageName();

    protected boolean checkDir() {
        File dir = new File(springBootCli.getRootPackagePath() + getPackageName());
        if (!dir.exists()) {
            return dir.mkdir();
        }
        return true;
    }

    protected void createFile(String fileName, String content) throws IOException {
        File file = new File(springBootCli.getRootPackagePath() + getPackageName() + File.separator + fileName);
        if (!file.exists() || springBootCli.isForceCover()) {
            FileUtil.createWriteFile(file, content);
        }
    }

}
