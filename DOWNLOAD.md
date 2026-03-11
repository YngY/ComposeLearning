# 下载 APK

## 方式 1: GitHub Releases (推荐)

1. 访问 releases 页面: https://github.com/YngY/ComposeLearning/releases
2. 下载 `app-debug.apk` 或 `app-release.apk`

## 方式 2: GitHub Actions Artifacts

1. 访问 Actions 页面: https://github.com/YngY/ComposeLearning/actions
2. 点击最新的 "Build and Release APK" workflow
3. 在 Artifacts 部分下载 APK

## 方式 3: 本地构建

```bash
# 克隆项目
git clone https://github.com/YngY/ComposeLearning.git
cd ComposeLearning

# 构建调试版 APK
./gradlew assembleDebug

# 构建发布版 APK
./gradlew assembleRelease

# APK 输出位置
# app/build/outputs/apk/debug/app-debug.apk
# app/build/outputs/apk/release/app-release.apk
```

## 版本信息

- 当前版本: v1.0.0
- 编译 SDK: 34 (Android 14)
- 最低 SDK: 24 (Android 7.0)
- Kotlin: 1.9.20
