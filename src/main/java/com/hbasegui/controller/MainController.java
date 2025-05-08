package com.hbasegui.controller;

import com.hbasegui.service.HBaseService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
public class MainController {

    @FXML
    private ListView<String> tableListView;
    @FXML
    private TextArea clusterInfoArea;
    @FXML
    private TextField commandField;
    @FXML
    private TableView<TableData> dataTableView;
    @FXML
    private TableColumn<TableData, String> rowKeyColumn;
    @FXML
    private TableColumn<TableData, String> familyColumn;
    @FXML
    private TableColumn<TableData, String> qualifierColumn;
    @FXML
    private TableColumn<TableData, String> valueColumn;
    @FXML
    private TableColumn<TableData, String> timestampColumn;
    @FXML
    private TextArea logArea;

    private final ObservableList<TableData> tableData = FXCollections.observableArrayList();
    private HBaseService hbaseService;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private String currentTable;

    @FXML
    public void initialize() {
        setupTableColumns();
        setupTableSelection();
    }

    public void initializeAfterLogin() {
        try {
            appendLog("初始化主界面，获取集群信息...");
            updateClusterInfo();
            appendLog("集群信息获取成功，刷新表列表...");
            refreshTableList();
            appendLog("成功连接到HBase集群");
        } catch (Exception e) {
            log.error("初始化失败", e);
            showError("初始化失败: " + e.getMessage());
            appendLog("初始化失败: " + e.getMessage());
        }
    }

    private void updateClusterInfo() {
        try {
            appendLog("正在获取HBase集群版本和表数量...");
            String clusterInfo = hbaseService.getClusterInfo();
            clusterInfoArea.setText(clusterInfo);
            appendLog(clusterInfo);
        } catch (Exception e) {
            appendLog("获取集群信息失败: " + e.getMessage());
            log.error("获取集群信息失败", e);
        }
    }

    private void setupTableColumns() {
        appendLog("初始化表格列...");
        rowKeyColumn.setCellValueFactory(new PropertyValueFactory<>("rowKey"));
        familyColumn.setCellValueFactory(new PropertyValueFactory<>("family"));
        qualifierColumn.setCellValueFactory(new PropertyValueFactory<>("qualifier"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        dataTableView.setItems(tableData);
        appendLog("表格列初始化完成");
    }

    private void setupTableSelection() {
        appendLog("设置表选择监听...");
        tableListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                currentTable = newVal;
                appendLog("选择表: " + currentTable + "，开始加载数据...");
                loadTableData();
            }
        });
        appendLog("表选择监听设置完成");
    }

    @FXML
    private void handleRefresh() {
        try {
            appendLog("刷新表列表...");
            refreshTableList();
            appendLog("刷新表列表成功");
        } catch (Exception e) {
            log.error("刷新表列表失败", e);
            appendLog("刷新表列表失败: " + e.getMessage());
        }
    }

    private void refreshTableList() {
        try {
            List<String> tables = hbaseService.listTables();
            Platform.runLater(() -> {
                tableListView.setItems(FXCollections.observableArrayList(tables));
                appendLog("已加载 " + tables.size() + " 个表");
            });
        } catch (Exception e) {
            appendLog("加载表列表失败: " + e.getMessage());
            log.error("加载表列表失败", e);
        }
    }

    private void loadTableData() {
        try {
            appendLog("准备加载表数据: " + currentTable);
            hbaseService.selectTable(currentTable);
            int limit = 10;
            List<Result> results = hbaseService.scan(limit);
            appendLog("HBase返回结果行数: " + results.size());

            Platform.runLater(() -> {
                dataTableView.getItems().clear();
                for (Result result : results) {
                    String rowKey = Bytes.toString(result.getRow());
                    // 修改遍历逻辑，获取每个 Cell 的时间戳
                    for (Cell cell : result.listCells()) {
                        String family = Bytes.toString(CellUtil.cloneFamily(cell));
                        String qualifier = Bytes.toString(CellUtil.cloneQualifier(cell));
                        String value = Bytes.toString(CellUtil.cloneValue(cell));
                        long timestamp = cell.getTimestamp();
                        // 创建 TableData 对象并添加到表格
                        TableData tableData = new TableData(
                                rowKey,
                                family,
                                qualifier,
                                value,
                                String.valueOf(timestamp)  // 替换为实际时间戳
                        );
                        dataTableView.getItems().add(tableData);
                    }
                }
                appendLog("已加载 " + dataTableView.getItems().size() + " 行数据");
            });
        } catch (Exception e) {
            log.error("加载表数据失败", e);
            appendLog("加载表数据失败: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        if (currentTable != null) {
            appendLog("执行查询，表: " + currentTable);
            loadTableData();
        } else {
            appendLog("请先选择一个表");
        }
    }

    @FXML
    private void handleExecuteCommand() {
        String command = commandField.getText().trim();
        if (command.isEmpty()) {
            appendLog("命令为空，未执行");
            return;
        }
        appendLog("执行命令: " + command);
        showError("命令执行功能尚未实现");
        appendLog("命令执行失败: 功能尚未实现");
    }

    @FXML
    private void handleClearLogs() {
        logArea.clear();
        appendLog("日志已清除");
    }

    public void appendLog(String message) {
        String timestamp = LocalDateTime.now().format(TIME_FORMATTER);
        Platform.runLater(() -> {
            logArea.appendText(String.format("[%s] %s%n", timestamp, message));
            logArea.setScrollTop(Double.MAX_VALUE);
        });
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("错误");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setHBaseService(HBaseService hbaseService) {
        this.hbaseService = hbaseService;
    }

    public static class TableData {
        private final SimpleStringProperty rowKey;
        private final SimpleStringProperty family;
        private final SimpleStringProperty qualifier;
        private final SimpleStringProperty value;
        private final SimpleStringProperty timestamp;

        public TableData(String rowKey, String family, String qualifier, String value, String timestamp) {
            this.rowKey = new SimpleStringProperty(rowKey);
            this.family = new SimpleStringProperty(family);
            this.qualifier = new SimpleStringProperty(qualifier);
            this.value = new SimpleStringProperty(value);
            this.timestamp = new SimpleStringProperty(timestamp);
        }

        // Getter 和 Property 方法
        public String getRowKey() { return rowKey.get(); }
        public SimpleStringProperty rowKeyProperty() { return rowKey; }

        public String getFamily() { return family.get(); }
        public SimpleStringProperty familyProperty() { return family; }

        public String getQualifier() { return qualifier.get(); }
        public SimpleStringProperty qualifierProperty() { return qualifier; }

        public String getValue() { return value.get(); }
        public SimpleStringProperty valueProperty() { return value; }

        public String getTimestamp() { return timestamp.get(); }
        public SimpleStringProperty timestampProperty() { return timestamp; }
    }
} 