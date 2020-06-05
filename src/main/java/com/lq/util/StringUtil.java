package com.lq.util;

public class StringUtil {

    public static String formatSingleLine(int tabNum, String srcString) {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<tabNum; i++) {
            sb.append("\t");
        }
        sb.append(srcString);
        sb.append("\n");
        return sb.toString();
    }

    public static String firstToLowerCase(String str) {
        char[] cs = str.toCharArray();
        cs[0] += 32;
        return String.valueOf(cs);
    }

    public static String firstToUpperCase(String str) {
        char[] cs = str.toCharArray();
        cs[0] -= 32;
        return String.valueOf(cs);
    }

    public static boolean firstIsUpperCase(String s) {
        return Character.isUpperCase(s.charAt(0));
    }


    private final static String UNDERLINE = "_";

    public static String underlineToHump(String para) {
        StringBuilder result = new StringBuilder();
        String a[] = para.split(UNDERLINE);
        for (String s : a) {
            if (!para.contains(UNDERLINE)) {
                result.append(s);
                continue;
            }
            if (result.length() == 0) {
                result.append(s.toLowerCase());
            } else {
                result.append(s.substring(0, 1).toUpperCase());
                result.append(s.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }

    public static String humpToUnderline(String para) {
        StringBuilder sb = new StringBuilder(para);
        int temp = 0;
        if (!para.contains(UNDERLINE)) {
            for (int i = 0; i < para.length(); i++) {
                if (Character.isUpperCase(para.charAt(i))) {
                    sb.insert(i + temp, UNDERLINE);
                    temp += 1;
                }
            }
        }
        return sb.toString().toLowerCase();
    }

}
