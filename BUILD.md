# ComposeLearning - 构建说明

## 由于服务器环境限制，请在本地构建 APK

## 📋 前置要求

1. **安装 JDK 17**
   ```bash
   # macOS
   brew install openjdk@17
   
   # Linux (Ubuntu/Debian)
   sudo apt update
   sudo apt install openjdk-17-jdk
   
   # Windows
   下载并安装: https://adoptium.net/
   ```

2. **安装 Android Studio**
   - 下载: https://developer.android.com/studio
   - 安装并运行一次（会自动下载 Android SDK）

3. **验证环境**
   ```bash
   java -version
   # 应该显示: openjdk version "17.x.x"
   ```

## 🚀 构建步骤

### 方法 1: 使用 Android Studio（推荐）

1. **克隆项目**
   ```bash
   git clone https://github.com/YngY/ComposeLearning.git
   cd ComposeLearning
   ```

2. **用 Android Studio 打开**
   - 打开 Android Studio
   - File → Open → 选择 ComposeLearning 目录
   - 等待 Gradle 同步完成（首次可能需要下载依赖）

3. **构建 APK**
   - 菜单: Build → Build Bundle(s) / APK(s) → Build APK(s)
   - 等待构建完成

4. **找到 APK**
   - 在弹出的通知中点击 "locate"
   - 位置: `app/build/outputs/apk/debug/app-debug.apk`

### 方法 2: 命令行构建

```bash
# 1. 克隆项目
git clone https://github.com/YngY/ComposeLearning.git
cd ComposeLearning

# 2. 设置 JAVA_HOME (如果需要)
# macOS/Linux
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# Windows
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.x.x-hotspot

# 3. 构建调试版
./gradlew assembleDebug

# 4. 构建发布版
./gradlew assembleRelease

# 5. APK 位置
# Debug: app/build/outputs/apk/debug/app-debug.apk
# Release: app/build/outputs/apk/release/app-release.apk
```

## 🔧 常见问题

### 问题 1: gradlew 权限错误

```bash
# macOS/Linux
chmod +x gradlew

# Windows
gradlew.bat assembleDebug
```

### 问题 2: Gradle 同步失败

```bash
# 清理并重试
./gradlew clean
./gradlew assembleDebug
```

### 问题 3: Android SDK 未找到

打开 Android Studio → Tools → SDK Manager，确保安装了：
- Android SDK Platform 34
- Android SDK Build-Tools 34.0.0

### 问题 4: 找不到 Java

```bash
# 设置 JAVA_HOME
export JAVA_HOME=/path/to/jdk-17
```

## 📱 安装 APK

1. 将 APK 传输到 Android 设备
2. 在手机上启用"未知来源"安装
3. 点击 APK 文件安装

## 📦 项目结构

```
ComposeLearning/
├── app/
│   └── build.gradle          # App 模块配置
├── build.gradle              # 项目级配置
├── settings.gradle           # Gradle 设置
├── local.properties          # SDK 路径配置（自动生成）
└── gradlew                   # Gradle 包装器
```

## 🎯 包含的示例

- ✅ UI 基础组件
- ✅ 布局
- ✅ 状态管理
- ✅ 导航
- ✅ 动画
- ✅ ViewModel
- ✅ Hilt 依赖注入
- ✅ 网络请求
- ✅ Room 数据库
- ✅ DataStore
- ✅ WorkManager
- ✅ 生命周期
- ✅ Paging
- ✅ 协程
- ✅ 多主题
- ✅ CameraX
- ✅ Location
- ✅ Media3
- ✅ Security
- ✅ Startup
- ✅ Window

## 💡 提示

- 首次构建可能需要下载依赖，请耐心等待
- Debug APK 可以直接安装，无需签名
- Release APK 需要签名后才能安装
- 如需签名，参考 Android 官方文档

## 📞 需要帮助？

如果遇到问题，请查看：
- [Android Studio 构建指南](https://developer.android.com/studio/build)
- [Gradle 文档](https://docs.gradle.org/)
- 或在 GitHub 提 Issue: https://github.com/YngY/ComposeLearning/issues
