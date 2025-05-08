# HBase GUI Client

这是一个基于JavaFX开发的HBase桌面客户端，提供了图形化界面来连接和操作HBase数据库。

## 功能特点

- 支持配置HBase连接参数
- 显示HBase表列表
- 预览表数据
- 执行HBase命令
- 美观的图形界面

## 系统要求

- Java 8
- Maven 3.6 或更高版本
- HBase 2.x 集群

## 构建和运行

1. 克隆项目到本地
2. 在项目根目录执行：
   ```bash
   mvn clean package
   ```
3. 运行生成的jar文件：
   ```bash
   java -jar target/hbase-gui-1.0-SNAPSHOT.jar
   ```

## 使用说明

1. 启动程序后，首先需要配置HBase连接信息：
   - Zookeeper Quorum：HBase集群的Zookeeper地址
   - ZNode Parent：HBase在Zookeeper中的根节点路径
   - 可选参数：RPC超时、操作超时、扫描超时等

2. 连接成功后，左侧会显示HBase表列表
3. 点击表名可以预览该表的前10条数据
4. 在顶部命令区域可以输入HBase命令并执行

## 注意事项

- 确保HBase集群可以正常访问
- 建议在生产环境中使用适当的超时设置
- 大量数据操作时注意性能影响 