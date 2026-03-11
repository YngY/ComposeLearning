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
└── coroutines/                  # 协程
    └── CoroutinesDemo.kt        # launch、async、Flow
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

## 运行项目

1. 打开 Android Studio
2. File → Open → 选择项目目录
3. 等待 Gradle 同步完成
4. 运行 app 模块

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
