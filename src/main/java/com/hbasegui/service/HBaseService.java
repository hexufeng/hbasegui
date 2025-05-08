package com.hbasegui.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Slf4j
public class HBaseService {
    private Connection connection;
    private Admin admin;
    private Table table;

    public void connect(Properties properties) throws IOException {
        // 设置环境变量
        System.setProperty("HADOOP_USER_NAME", "da_music");
        
        Configuration config = HBaseConfiguration.create();
        properties.forEach((key, value) -> config.set(key.toString(), value.toString()));
        
        connection = ConnectionFactory.createConnection(config);
        admin = connection.getAdmin();
    }

    public String getClusterInfo() throws IOException {
        checkConnection();
        try {
            String version = admin.getClusterMetrics().getHBaseVersion();
            List<ServerName> serversName = admin.getClusterMetrics().getServersName();
            admin.getClusterMetrics().getClusterId();
            int tableCount = admin.listTableNames().length;
            return "HBase版本: " + version + "\n, 服务器数量: " + serversName.size() + "\n, 表数量: " + tableCount + "\n, 集群ID: " + admin.getClusterMetrics().getClusterId();
        } catch (Exception e) {
            log.error("获取版本失败", e);
            throw new IOException("获取版本失败: " + e.getMessage(), e);
        }
    }

    public List<String> listTables() throws IOException {
        checkConnection();
        List<String> tables = new ArrayList<>();
        for (TableName tableName : admin.listTableNames()) {
            tables.add(tableName.getNameAsString());
        }
        return tables;
    }

    public void selectTable(String tableName) throws IOException {
        checkConnection();
        if (table != null) {
            table.close();
        }
        table = connection.getTable(TableName.valueOf(tableName));
    }

    public List<Result> scan(int limit) throws IOException {
        checkConnection();
        Scan scan = new Scan();
        scan.setLimit(limit);

        List<Result> results = new ArrayList<>();
        try (ResultScanner scanner = table.getScanner(scan)) {
            for (Result result : scanner) {
                results.add(result);
                if (results.size() >= limit) {
                    break;
                }
            }
        }
        return results;
    }

    // 修改 hbaseService.scan() 的实现，例如：
//    public List<Result> scan(int limit) throws IOException {
//        Scan scan = new Scan();
//        scan.setMaxResultSize(limit);
//        ResultScanner scanner = table.getScanner(scan);
//        List<Result> results = new ArrayList<>();
//        int count = 0;
//        for (Result result : scanner) {
//            if (count++ >= limit) break;
//            results.add(result);
//        }
//        return results;
//    }

    public void close() throws IOException {
        if (table != null) {
            table.close();
        }
        if (admin != null) {
            admin.close();
        }
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    private void checkConnection() throws IOException {
        if (connection == null || connection.isClosed()) {
            throw new IllegalStateException("HBase连接未初始化或已关闭");
        }
    }
}