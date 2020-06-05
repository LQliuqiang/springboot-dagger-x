package com.lq.util;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileUtil {

    public static void createWriteFile(File file, String content) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(content.getBytes(StandardCharsets.UTF_8));
        fos.close();
    }

    public static StringBuilder readFileContent(File file) throws IOException {
        if (!file.exists()) {
            return null;
        }
        StringBuilder sbf = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                sbf.append(tempStr).append("\n");
            }
            reader.close();
            return sbf;
        }
    }

}
