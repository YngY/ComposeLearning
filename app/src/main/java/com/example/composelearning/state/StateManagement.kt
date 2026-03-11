package com.example.composelearning.state

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 状态管理示例
 * 包含: remember, rememberSaveable, mutableStateOf
 */

/**
 * 示例1: 简单的计数器
 */
@Composable
fun CounterScreen() {
    var count by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "计数: $count",
            style = MaterialTheme.typography.headlineLarge
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = { count++ }) {
                Text("+1")
            }
            Button(onClick = { count-- }) {
                Text("-1")
            }
            Button(onClick = { count = 0 }) {
                Text("重置")
            }
        }
    }
}

/**
 * 示例2: 表单输入
 */
@Composable
fun FormScreen() {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRemember by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("用户名") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("密码") },
            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Checkbox(
                checked = isRemember,
                onCheckedChange = { isRemember = it }
            )
            Text("记住我")
        }

        Button(
            onClick = { /* 登录逻辑 */ },
            modifier = Modifier.fillMaxWidth(),
            enabled = username.isNotBlank() && password.isNotBlank()
        ) {
            Text("登录")
        }
    }
}

/**
 * 示例3: remember vs rememberSaveable
 * remember: 重组时保持状态
 * rememberSaveable: 配置变更时保持状态（如屏幕旋转）
 */
@Composable
fun RememberComparisonScreen() {
    var counter1 by remember { mutableIntStateOf(0) }
    var counter2 by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("remember: $counter1 (重组时保持)", style = MaterialTheme.typography.titleMedium)
        Button(onClick = { counter1++ }) {
            Text("增加")
        }

        Divider()

        Text("rememberSaveable: $counter2 (配置变更也保持)", style = MaterialTheme.typography.titleMedium)
        Button(onClick = { counter2++ }) {
            Text("增加")
        }
    }
}

/**
 * 示例4: 派生状态
 */
@Composable
fun DerivedStateScreen() {
    var items by remember { mutableStateOf(listOf("Apple", "Banana", "Orange")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("原始列表: ${items.size} 项")
        
        // 派生状态: 根据原始状态计算
        val itemCount by remember(items) { 
            derivedStateOf { items.size }
        }
        Text("派生状态: $itemCount 项")

        Button(onClick = { items = items + "New Item" }) {
            Text("添加项目")
        }
    }
}
