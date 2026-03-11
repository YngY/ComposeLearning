package com.example.composelearning.basics

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 基础组件示例
 * 包含: Text, Button, OutlinedButton, TextButton, IconButton
 */
@Composable
fun BasicComponentsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Text 文本
        Text(
            text = "Hello Compose!",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Text(
            text = "这是正文内容",
            style = MaterialTheme.typography.bodyLarge
        )

        // 2. Button 按钮
        Button(onClick = { /* 点击事件 */ }) {
            Text("主要按钮")
        }

        // 3. OutlinedButton 轮廓按钮
        OutlinedButton(onClick = { /* 点击事件 */ }) {
            Text("轮廓按钮")
        }

        // 4. TextButton 文字按钮
        TextButton(onClick = { /* 点击事件 */ }) {
            Text("文字按钮")
        }

        // 5. IconButton 图标按钮
        var count by remember { mutableIntStateOf(0) }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("计数: $count")
            IconButton(onClick = { count++ }) {
                Text("+")
            }
            IconButton(onClick = { count-- }) {
                Text("-")
            }
        }

        // 6. FloatingActionButton 悬浮按钮
        FloatingActionButton(
            onClick = { /* 点击事件 */ },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("+")
        }
    }
}
