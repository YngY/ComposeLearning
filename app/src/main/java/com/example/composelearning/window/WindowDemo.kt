package com.example.composelearning.window

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.core.ExperimentalWindowApi
import androidx.window.core.layout.WindowInfo
import androidx.window.core.layout.WindowMetricsCalculator

/**
 * Window 示例
 * 包含: 返回手势、侧滑手势、多窗口支持、窗口状态
 */

// ============= 1. 窗口信息 =============
@OptIn(ExperimentalWindowApi::class)
@Composable
fun WindowInfoDemo() {
    val windowMetricsCalculator = remember {
        WindowMetricsCalculator.getOrCreate()
    }

    // 获取窗口信息
    val windowInfo = WindowInfo.Companion

    Column(modifier = Modifier.padding(16.dp)) {
        Text("窗口信息", style = MaterialTheme.typography.titleMedium)
        Text("支持多窗口和折叠屏设备")
    }
}

// ============= 2. 返回手势处理 =============
@Composable
fun BackHandlerDemo() {
    var showSecondScreen by remember { mutableStateOf(false) }

    if (showSecondScreen) {
        // 返回手势处理
        BackHandler(enabled = true) {
            // 自定义返回逻辑
            showSecondScreen = false
        }

        SecondScreen(onBackPressed = { showSecondScreen = false })
    } else {
        FirstScreen(onNavigate = { showSecondScreen = true })
    }
}

@Composable
private fun FirstScreen(onNavigate: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("第一屏", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNavigate) {
            Text("进入第二屏")
        }
    }
}

@Composable
private fun SecondScreen(onBackPressed: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("第二屏", style = MaterialTheme.typography.headlineMedium)
        Text("点击返回按钮或使用返回手势", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBackPressed) {
            Text("返回")
        }
    }
}

// ============= 3. 侧滑抽屉 =============
@Composable
fun DrawerDemo() {
    var drawerState by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = rememberDrawerState(drawerState),
        drawerContent = {
            ModalDrawerSheet {
                Text("菜单", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
                Divider()
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("首页") },
                    selected = true,
                    onClick = { drawerState = false }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("设置") },
                    selected = false,
                    onClick = { drawerState = false }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("个人") },
                    selected = false,
                    onClick = { drawerState = false }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("侧滑抽屉") },
                    navigationIcon = {
                        IconButton(onClick = { drawerState = true }) {
                            Icon(Icons.Default.Menu, contentDescription = null)
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Text("点击左上角菜单图标打开侧滑抽屉")
            }
        }
    }
}

// ============= 4. 主页面 =============
@Composable
fun WindowDemoScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("返回手势") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("侧滑抽屉") }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("窗口信息") }
            )
        }

        when (selectedTab) {
            0 -> BackHandlerDemo()
            1 -> DrawerDemo()
            2 -> WindowInfoDemo()
        }
    }
}

/**
 * Window 核心要点:
 * 
 * 1. BackHandler: 返回手势处理
 * 2. ModalNavigationDrawer: 模态抽屉
 * 3. WindowMetricsCalculator: 窗口尺寸计算
 * 4. WindowInfo: 窗口状态信息
 * 5. 多窗口: 支持分屏和画中画
 * 
 * Gradle 依赖:
 * implementation "androidx.window:window:1.2.0"
 */
