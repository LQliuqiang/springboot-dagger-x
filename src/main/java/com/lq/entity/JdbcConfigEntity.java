package com.lq.entity;

public class JdbcConfigEntity {

    private String url;
    private String username;
    private String password;
    private String host;

    private JdbcConfigEntity(Builder builder) {
        this.host = builder.host;
        this.url = builder.url;
        this.username = builder.username;
        this.password = builder.password;
    }

    public String getDriverClassName() {
        return "com.mysql.jdbc.Driver";
    }

    public String getUrl() {
        return url;
    }

    public String getHost() {
        return host;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public static class Builder {
        private String url;
        private String dbName;
        private String username;
        private String password;
        private String host = "localhost";

        public Builder(String username, String password,String dbName) {
            this.url = "jdbc:mysql://localhost:3306/"+dbName+"?useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8";
            this.username = username;
            this.password = password;
            this.dbName = dbName;
        }

        public Builder url(String host,int port) {
            this.url = "jdbc:mysql://"+host+":"+port+"/"+this.dbName+"?useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8";
            this.host = host;
            return this;
        }

        public JdbcConfigEntity build() {
            return new JdbcConfigEntity(this);
        }

    }

}
