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

## 打包成MacOS APP
### 准备工作
1.确认项目结构：
```
target/
├── your-app.jar
└── lib/          # 依赖库目录
```
### 步骤一：创建macOS应用包结构
```
mkdir -p HbaseGUI.app/Contents/{MacOS,Resources}
```

### 步骤二：编写Info.plist
在 MyApp.app/Contents/ 创建 Info.plist：
```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>CFBundleExecutable</key>
    <string>JavaLauncher</string>
    <key>CFBundleIdentifier</key>
    <string>com.hbasegui.HbaseGUI</string>
    <key>CFBundleName</key>
    <string>HBaseGUI</string>
    <key>CFBundleVersion</key>
    <string>1.0</string>
    <key>CFBundleShortVersionString</key>
    <string>1.0</string>
    <key>JVMMainClassName</key>
    <string>com.hbasegui.Main</string>
    <key>JVMOptions</key>
    <array>
        <string>-Dfile.encoding=UTF-8</string>
    </array>
    <key>JVMArguments</key>
    <array/>
</dict>
</plist>
```
### 步骤三：准备启动脚本
在 MyApp.app/Contents/MacOS 创建 JavaLauncher：
```
#!/bin/sh
BASEDIR=$(dirname "$0")
java -jar "$BASEDIR/../Resources/hbase-gui-1.0-SNAPSHOT.jar" com.hbasegui.Main
```
然后赋予执行权限：
```
chmod +x HbaseGUI.app/Contents/MacOS/JavaLauncher
```

### 步骤四：复制文件
复制主JAR
```
cp ~/tmp/hbasegui/target/hbase-gui-1.0-SNAPSHOT.jar MyApp.app/Contents/Resources/
```
复制所有依赖
cp -R target/lib MyApp.app/Contents/Resources/   #没有lib此步骤省略

### 步骤五：验证应用包
测试运行
```
open MyApp.app
```
检查结构
```
tree MyApp.app
```

### 步骤六：生成DMG
```
 hdiutil create -volname "HbaseGUI Installer" \
               -srcfolder HbaseGUI.app \
               -ov -format UDZO \
               HbaseGUI.dmg
```
or
```
create-dmg --volname "HbaseGUI" --app-drop-link 512 512 HbaseGUI.dmg HbaseGUI.app
```

## UI 展示
<img width="1025" height="633" alt="login" src="https://github.com/user-attachments/assets/6b5c5a08-31d6-4c56-979e-0ab824bd07ae" />
<img width="1611" height="937" alt="main" src="https://github.com/user-attachments/assets/275ba80a-2676-4ab8-9cb4-5018954fdd85" />

