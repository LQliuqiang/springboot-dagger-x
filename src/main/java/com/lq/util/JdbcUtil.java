package com.lq.util;

import com.lq.entity.JdbcConfigEntity;
import com.lq.entity.TableFiledEntity;
import com.lq.entity.TableInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcUtil {

    private JdbcConfigEntity jdbcConfig;

    public JdbcUtil(JdbcConfigEntity jdbcConfig) {
        try {
            this.jdbcConfig = jdbcConfig;
            Class.forName(jdbcConfig.getDriverClassName());
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }


    public Connection connection() throws SQLException {
        return DriverManager.getConnection(jdbcConfig.getUrl(), jdbcConfig.getUsername(), jdbcConfig.getPassword());
    }

    public boolean update(String sql) throws SQLException {
        Connection connection = connection();
        PreparedStatement statement = connection.prepareStatement(sql);
        boolean b = statement.executeUpdate() > 0;
        closeConnection(connection);
        closeStatement(statement);
        return b;
    }

    public boolean ddl(String sql) throws SQLException {
        Connection connection = connection();
        Statement statement = connection.createStatement();
        boolean execute = statement.execute(sql);
        closeConnection(connection);
        closeStatement(statement);
        return execute;
    }

    public List<TableInfo> queryTableInfo() throws SQLException {
        List<TableInfo> tableInfos = new ArrayList<>();
        Connection connection = connection();
        PreparedStatement statement = connection.prepareStatement("show TABLES");
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()){
            List<TableFiledEntity> filedEntities = new ArrayList<>();
            String tableName = resultSet.getString(1);
            String tableFieldSql = selectTableFieldSql(tableName);
            PreparedStatement st = connection.prepareStatement(tableFieldSql);
            ResultSet rs = st.executeQuery();
            while (rs.next()){
                String field = rs.getString("Field");
                String type = rs.getString("Type");
                String key = rs.getString("Key");
                String aNull = rs.getString("Null");
                String aDefault = rs.getString("Default");
                String extra = rs.getString("Extra");
                filedEntities.add(new TableFiledEntity(field,type,aNull,key,aDefault,extra));
            }
            tableInfos.add(new TableInfo(tableName,filedEntities));
            closeStatement(st);
            closeResultSet(rs);
        }
        closeConnection(connection);
        closeStatement(statement);
        closeResultSet(resultSet);
        return tableInfos;
    }

    private String selectTableFieldSql(String tableName){
        return  "SHOW COLUMNS FROM " + tableName;
    }

    public void closeResource(Connection conn, Statement st, ResultSet rs) {
        closeResultSet(rs);
        closeStatement(st);
        closeConnection(conn);
    }

    public void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void closeStatement(Statement st) {
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void checkMysqlConnectorJar(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("mysql-connector-java.jar no exist");
        }
    }


}
