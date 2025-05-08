package com.hbasegui.controller;

import com.hbasegui.service.HBaseService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Properties;

@Slf4j
public class LoginController {

    private HBaseService hbaseService = new HBaseService();

    @FXML
    private TextField quorumField;
    @FXML
    private TextField znodeParentField;
    @FXML
    private TextField rpcTimeoutField;
    @FXML
    private TextField operationTimeoutField;
    @FXML
    private TextField scannerTimeoutField;
    @FXML
    private Label errorLabel;

    @FXML
    private void handleConnect() {
        try {
            Properties properties = new Properties();
            properties.setProperty("hbase.zookeeper.quorum", quorumField.getText());
            properties.setProperty("zookeeper.znode.parent", znodeParentField.getText());
            
            if (!rpcTimeoutField.getText().isEmpty()) {
                properties.setProperty("hbase.rpc.timeout", rpcTimeoutField.getText());
            }
            if (!operationTimeoutField.getText().isEmpty()) {
                properties.setProperty("hbase.client.operation.timeout", operationTimeoutField.getText());
            }
            if (!scannerTimeoutField.getText().isEmpty()) {
                properties.setProperty("hbase.client.scanner.timeout.period", scannerTimeoutField.getText());
            }

            log.info("开始连接HBase...");
            // 测试连接
            hbaseService.connect(properties);
            log.info("连接成功，正在加载主界面...");
            
            // 连接成功，切换到主界面
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) quorumField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("HBase GUI Client - 数据管理");
            
            // 初始化主界面
            MainController mainController = loader.getController();
            mainController.setHBaseService(hbaseService);
            mainController.appendLog("连接成功，正在初始化主界面...");
            mainController.initializeAfterLogin();
            mainController.appendLog("主界面初始化完成。");
            
        } catch (Exception e) {
            log.error("连接HBase失败", e);
            errorLabel.setText("连接失败: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) quorumField.getScene().getWindow();
        stage.close();
    }
} 