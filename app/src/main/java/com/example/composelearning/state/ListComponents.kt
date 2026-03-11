package com.example.composelearning.state

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 列表示例
 * 包含: LazyColumn, LazyColumnWithIndex
 */

/**
 * 示例1: 简单的可删除列表
 */
@Composable
fun TodoListScreen() {
    var todos by remember { 
        mutableStateOf(
            listOf(
                TodoItem(1, "学习 Compose", false),
                TodoItem(2, "完成项目", false),
                TodoItem(3, "阅读文档", true)
            )
        ) 
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "待办事项 (${todos.size})",
            style = MaterialTheme.typography.headlineSmall
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(todos, key = { it.id }) { todo ->
                TodoItemRow(
                    todo = todo,
                    onToggle = { 
                        todos = todos.map { 
                            if (it.id == todo.id) it.copy(done = !it.done) else it 
                        }
                    },
                    onDelete = {
                        todos = todos.filter { it.id != todo.id }
                    }
                )
            }
        }
    }
}

data class TodoItem(
    val id: Int,
    val title: String,
    val done: Boolean
)

@Composable
fun TodoItemRow(
    todo: TodoItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Checkbox(
                    checked = todo.done,
                    onCheckedChange = { onToggle() }
                )
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "删除")
            }
        }
    }
}

/**
 * 示例2: 带索引的列表
 */
@Composable
fun NumberListScreen() {
    val numbers = remember { (1..100).toList() }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        itemsIndexed(numbers) { index, number ->
            Text(
                "第 ${index + 1} 项: $number",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * 示例3: 分组列表
 */
@Composable
fun GroupedListScreen() {
    val groupedItems = remember {
        mapOf(
            "已完成" to listOf("任务 A", "任务 B"),
            "进行中" to listOf("任务 C", "任务 D", "任务 E"),
            "未开始" to listOf("任务 F")
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        groupedItems.forEach { (category, items) ->
            item {
                Text(
                    text = "$category (${items.size})",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            items(items) { item ->
                Text(
                    text = "• $item",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}
