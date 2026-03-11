# Jenkins 配置指南

本指南帮助你在 Jenkins 上配置自动构建并上传 APK 到 GitHub。

## 📋 前置要求

1. **安装 Jenkins**
2. **安装必要的插件**
3. **配置 GitHub Token**
4. **配置 Jenkins Job**

---

## 🔧 步骤 1: 安装 Jenkins 插件

登录 Jenkins，进入：
1. **Manage Jenkins** → **Plugins**
2. 安装以下插件：
   - **Git Plugin**
   - **GitHub Plugin**
   - **Gradle Plugin**
   - **Credentials Binding Plugin**

---

## 🔑 步骤 2: 配置 GitHub Token

### 2.1 创建 GitHub Personal Access Token

1. 访问：https://github.com/settings/tokens
2. 点击 **"Generate new token (classic)"**
3. 填写：
   - Note: `Jenkins-Auto-Build`
   - Expiration: `No expiration`
4. 勾选权限：
   - `repo` (完整仓库控制)
   - `write:packages` (上传 Releases)
5. 点击 **"Generate token"**
6. **复制 Token**（只显示一次！）

### 2.2 在 Jenkins 中添加 Token

1. 进入 **Manage Jenkins** → **Credentials**
2. 点击 **(global)** → **Add Credentials**
3. 填写：
   - Kind: **Secret text**
   - Secret: `粘贴你的 GitHub Token`
   - ID: `github-token`
   - Description: `GitHub Token for APK upload`
4. 点击 **"Create"**

---

## 🚀 步骤 3: 配置 Jenkins Job

### 3.1 创建 Pipeline Job

1. 点击 **"New Item"**
2. 输入名称：`ComposeLearning-AutoBuild`
3. 选择 **"Pipeline"**
4. 点击 **"OK"**

### 3.2 配置 Pipeline

在 Pipeline 配置中：

```groovy
// 在 Pipeline 部分选择：
Definition: Pipeline script from SCM

// SCM:
Git Repository: https://github.com/YngY/ComposeLearning.git
Branch Specifier: */main

// Script Path:
Jenkinsfile
```

### 3.3 配置构建触发器（可选）

在 **Build Triggers** 中：

- **Poll SCM**: 设置定期检查（如 `H/5 * * * *` 每5分钟）
- **GitHub hook trigger**: 配置 GitHub Webhook

---

## ⚙️ 步骤 4: 配置 Webhook（可选）

### 4.1 在 GitHub 上配置

1. 访问：https://github.com/YngY/ComposeLearning/settings/hooks
2. 点击 **"Add webhook"**
3. 填写：
   - Payload URL: `http://your-jenkins-server/github-webhook/`
   - Content type: `application/json`
   - Secret: `设置一个随机密钥`
4. 选择触发事件：
   - Just the push event
5. 点击 **"Add webhook"**

### 4.2 在 Jenkins 上配置

1. 在 Jenkins Job 配置中
2. **Build Triggers** → **GitHub hook trigger for GITScm polling**
3. 填写 Secret（与 GitHub Webhook 一致）

---

## 🎯 步骤 5: 第一次运行

1. 进入 Jenkins Job 页面
2. 点击 **"Build Now"**
3. 等待构建完成（约 5-10 分钟）

---

## 📱 步骤 6: 获取 APK

构建成功后：

1. 访问：https://github.com/YngY/ComposeLearning/releases
2. 下载最新的版本（格式：`v1.0.0-构建号`）
3. 下载 `app-debug.apk` 或 `app-release.apk`

---

## 🔍 故障排查

### 问题 1: Gradle 构建失败

```
解决方案:
- 确保 Jenkins 上已安装 JDK 17
- 确保 ANDROID_HOME 环境变量已设置
- 检查 Android SDK 是否完整
```

### 问题 2: GitHub Token 失败

```
解决方案:
- 确保 Token 有 repo 权限
- 检查 Credentials ID 是否为 "github-token"
- 重新创建 Token
```

### 问题 3: 上传失败

```
解决方案:
- 确保构建成功生成 APK
- 检查文件路径是否正确
- 查看 Jenkins Console Output 详细错误
```

---

## 📊 Jenkinsfile 说明

Jenkinsfile 包含以下阶段：

1. **Checkout** - 拉取代码
2. **Setup** - 配置环境、安装 SDK
3. **Build Debug APK** - 构建调试版
4. **Build Release APK** - 构建发布版
5. **Create GitHub Release** - 创建 Release
6. **Upload APK to Release** - 上传 APK

---

## 🎉 自动化效果

配置完成后：
- ✅ 每次推送到 main 分支自动触发构建
- ✅ 自动创建 GitHub Release
- ✅ 自动上传 APK 到 Release
- ✅ 版本号自动递增（v1.0.0-1, v1.0.0-2...）

---

## 📞 需要帮助？

- [Jenkins 文档](https://www.jenkins.io/doc/)
- [GitHub Actions vs Jenkins](https://github.com/features/actions)

如有问题，可以查看 Jenkins Console Output 获取详细日志。
