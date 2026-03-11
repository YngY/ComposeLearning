# ComposeLearning

🧑‍🏫 Jetpack Compose 学习项目

## 项目结构

```
app/src/main/java/com/example/composelearning/
├── MainActivity.kt          # 入口文件
├── basics/                  # 基础组件
│   ├── BasicComponents.kt   # 文本、按钮、图标按钮
│   └── InputComponents.kt    # 输入框、开关、复选框、滑块
├── layout/                  # 布局组件
│   └── LayoutComponents.kt  # Column、Row、Box、权重
├── state/                   # 状态管理
│   ├── StateManagement.kt   # remember、rememberSaveable
│   └── ListComponents.kt    # LazyColumn、列表操作
├── navigation/              # 导航
│   └── NavigationDemo.kt    # NavHost、底部导航
└── animation/               # 动画
    └── AnimationDemo.kt     # 颜色、大小、可见性动画
```

## 学习路线

### 1️⃣ 基础组件 (basics/)
- **BasicComponents.kt**: Text、Button、OutlinedButton、IconButton、FloatingActionButton
- **InputComponents.kt**: TextField、Switch、Checkbox、RadioButton、Slider、DropdownMenu

### 2️⃣ 布局 (layout/)
- **LayoutComponents.kt**: Column、Row、Box、Spacer、weight权重分配

### 3️⃣ 状态管理 (state/)
- **StateManagement.kt**: remember、rememberSaveable、派生状态
- **ListComponents.kt**: LazyColumn、items、itemsIndexed、列表增删改

### 4️⃣ 导航 (navigation/)
- **NavigationDemo.kt**: NavHost、NavigationBar、路由传参

### 5️⃣ 动画 (animation/)
- **AnimationDemo.kt**: animateColorAsState、animateFloatAsState、AnimatedVisibility、AnimatedContent、无限动画

## 运行项目

1. 打开 Android Studio
2. File → Open → 选择项目目录
3. 等待 Gradle 同步完成
4. 运行 app 模块

## 技术栈

- Kotlin 1.9.20
- Jetpack Compose BOM 2023.10.01
- Material 3
- Navigation Compose 2.7.5
- Android SDK 34
