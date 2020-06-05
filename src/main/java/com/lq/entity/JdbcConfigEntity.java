package com.lq.entity;

public class JdbcConfigEntity {

    private String driverClassName;
    private String url;
    private String username;
    private String password;

    private JdbcConfigEntity(Builder builder) {
        this.driverClassName = builder.driverClassName;
        this.url = builder.url;
        this.username = builder.username;
        this.password = builder.password;
    }

    public String getDriverClassName() {
        return driverClassName != null && driverClassName.length() > 0 ? driverClassName : "com.mysql.jdbc.Driver";
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public static class Builder {
        private String driverClassName;
        private String url;
        private String dbName;
        private String username;
        private String password;

        public Builder(String username, String password,String dbName) {
            this.url = "jdbc:mysql://localhost:3306/"+dbName+"?useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8";
            this.username = username;
            this.password = password;
            this.dbName = dbName;
        }

        public Builder url(String host,int port) {
            this.url = "jdbc:mysql://"+host+":"+port+"/"+this.dbName+"?useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8";
            return this;
        }

        public Builder driverClassName(String driverClassName) {
            this.driverClassName = driverClassName;
            return this;
        }

        public JdbcConfigEntity build() {
            return new JdbcConfigEntity(this);
        }

    }

}
