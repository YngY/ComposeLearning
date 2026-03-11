# ComposeLearning

🧑‍🏫 Jetpack Compose 学习项目

## 项目结构

```
app/src/main/java/com/example/composelearning/
├── MainActivity.kt              # 入口文件
├── basics/                      # 基础组件
│   ├── BasicComponents.kt       # 文本、按钮、图标按钮
│   └── InputComponents.kt        # 输入框、开关、复选框、滑块
├── layout/                      # 布局组件
│   └── LayoutComponents.kt       # Column、Row、Box、权重
├── state/                       # 状态管理
│   ├── StateManagement.kt        # remember、rememberSaveable
│   └── ListComponents.kt         # LazyColumn、列表操作
├── navigation/                  # 导航
│   └── NavigationDemo.kt        # NavHost、底部导航
├── animation/                   # 动画
│   └── AnimationDemo.kt          # 颜色、大小、可见性动画
├── viewmodel/                   # ViewModel
│   └── ViewModelDemo.kt         # StateFlow、LiveData
├── hilt/                        # 依赖注入
│   └── HiltDemo.kt              # Hilt 注解和使用
├── network/                     # 网络请求
│   └── NetworkDemo.kt           # OkHttp、Retrofit、协程
├── database/                    # Room 数据库
│   └── RoomDemo.kt              # Entity、DAO、Repository
├── datastore/                   # 本地存储
│   └── DataStoreDemo.kt         # Preferences DataStore
├── workmanager/                 # 后台任务
│   └── WorkManagerDemo.kt       # Worker、定时任务
├── lifecycle/                   # 生命周期
│   └── LifecycleDemo.kt         # LifecycleOwner、LiveData
├── paging/                      # 分页加载
│   └── PagingDemo.kt            # Paging 3、无限滚动
├── coroutines/                  # 协程
│   └── CoroutinesDemo.kt        # launch、async、Flow
├── theme/                       # 多主题支持
│   └── ThemeDemo.kt             # 深色/浅色、动态颜色、自定义主题
├── camera/                      # 相机
│   └── CameraXDemo.kt           # 相机预览、拍照、切换摄像头
├── location/                    # 位置服务
│   └── LocationDemo.kt          # GPS定位、位置更新、地址查询
├── media/                       # 媒体
│   └── Media3Demo.kt            # 视频播放、音频播放
├── security/                    # 安全
│   └── SecurityDemo.kt          # 加密存储、哈希、密钥管理
├── startup/                     # 应用启动
│   └── StartupDemo.kt           # 初始化器、优化启动
└── window/                      # 窗口管理
    └── WindowDemo.kt            # 返回手势、侧滑、多窗口
```

## 学习路线

### 阶段 1: 基础
- [x] **basics/** - 基础 UI 组件
- [x] **layout/** - 布局排版
- [x] **state/** - 状态管理（核心）

### 阶段 2: 进阶
- [x] **navigation/** - 页面导航
- [x] **animation/** - 动画效果
- [x] **viewmodel/** - MVVM 架构

### 阶段 3: Jetpack 生态
- [x] **hilt/** - 依赖注入
- [x] **network/** - 网络请求
- [x] **database/** - Room 数据库
- [x] **datastore/** - 本地存储
- [x] **workmanager/** - 后台任务
- [x] **lifecycle/** - 生命周期
- [x] **paging/** - 分页加载
- [x] **coroutines/** - 协程

### 阶段 4: 高级功能
- [x] **theme/** - 多主题支持
- [x] **camera/** - CameraX 相机
- [x] **location/** - 位置服务
- [x] **media/** - Media3 媒体播放
- [x] **security/** - 安全加密
- [x] **startup/** - 应用启动优化
- [x] **window/** - 窗口管理

## 运行项目

1. 在本地克隆项目并构建 APK（详见 [BUILD.md](BUILD.md)）
2. 或在 Android Studio 中打开并运行

**构建指南:** [BUILD.md](BUILD.md)
**下载指南:** [DOWNLOAD.md](DOWNLOAD.md)

## 技术栈

| 技术 | 版本 |
|------|------|
| Kotlin | 1.9.20 |
| Jetpack Compose BOM | 2023.10.01 |
| Material 3 | 最新 |
| Navigation Compose | 2.7.5 |
| ViewModel | 2.6.2 |
| Hilt | 2.48 |
| Room | 2.6.0 |
| Retrofit | 2.9.0 |
| OkHttp | 4.12.0 |
| Paging 3 | 3.2.1 |
| WorkManager | 2.9.0 |
| Android SDK | 34 |

## 推荐学习顺序

1. **UI 基础** → basics/ + layout/
2. **状态管理** → state/ + viewmodel/
3. **导航动画** → navigation/ + animation/
4. **数据层** → database/ + network/ + datastore/
5. **后台任务** → workmanager/ + paging/
6. **架构** → hilt/ + coroutines/ + lifecycle/
