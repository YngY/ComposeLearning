package com.example.composelearning.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

/**
 * 导航示例
 * 包含: NavHost, Navigation Compose
 */

// 定义路由
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Detail : Screen("detail/{itemId}") {
        fun createRoute(itemId: Int) = "detail/$itemId"
    }
    object Profile : Screen("profile")
}

@Composable
fun NavigationDemo() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.Detail.route) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")?.toIntOrNull() ?: 0
            DetailScreen(itemId, navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController)
        }
    }
}

@Composable
fun HomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("首页", style = MaterialTheme.typography.headlineMedium)
        
        Button(onClick = { navController.navigate(Screen.Detail.createRoute(1)) }) {
            Text("查看详情 1")
        }
        
        Button(onClick = { navController.navigate(Screen.Detail.createRoute(2)) }) {
            Text("查看详情 2")
        }
        
        Button(onClick = { navController.navigate(Screen.Profile.route) }) {
            Text("个人资料")
        }
    }
}

@Composable
fun DetailScreen(itemId: Int, navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("详情页", style = MaterialTheme.typography.headlineMedium)
        Text("项目 ID: $itemId")
        
        Button(onClick = { navController.popBackStack() }) {
            Text("返回")
        }
    }
}

@Composable
fun ProfileScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("个人资料", style = MaterialTheme.typography.headlineMedium)
        Text("用户名: YngY")
        Text("邮箱: yngy@example.com")
        
        Button(onClick = { navController.popBackStack() }) {
            Text("返回")
        }
    }
}

/**
 * 带底部导航栏的示例
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationDemo() {
    var selectedItem by remember { mutableIntStateOf(0) }
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedItem == 0,
                    onClick = { selectedItem = 0 },
                    icon = { Text("首页") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = selectedItem == 1,
                    onClick = { selectedItem = 1 },
                    icon = { Text("搜索") },
                    label = { Text("Search") }
                )
                NavigationBarItem(
                    selected = selectedItem == 2,
                    onClick = { selectedItem = 2 },
                    icon = { Text("我的") },
                    label = { Text("Profile") }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("当前选中: ${listOf("首页", "搜索", "我的")[selectedItem]}")
        }
    }
}
