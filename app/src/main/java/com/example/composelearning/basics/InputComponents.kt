package com.example.composelearning.basics

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 输入组件示例
 * 包含: TextField, OutlinedTextField, Switch, Checkbox, RadioButton, Slider
 */
@Composable
fun InputComponentsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. TextField 基本输入框
        var text by remember { mutableStateOf("") }
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("用户名") },
            placeholder = { Text("请输入用户名") },
            modifier = Modifier.fillMaxWidth()
        )

        // 2. PasswordField 密码输入框
        var password by remember { mutableStateOf("") }
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("密码") },
            placeholder = { Text("请输入密码") },
            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        // 3. Switch 开关
        var switchChecked by remember { mutableStateOf(false) }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Switch(
                checked = switchChecked,
                onCheckedChange = { switchChecked = it }
            )
            Text(if (switchChecked) "已开启" else "已关闭")
        }

        // 4. Checkbox 复选框
        var checkboxChecked by remember { mutableStateOf(false) }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Checkbox(
                checked = checkboxChecked,
                onCheckedChange = { checkboxChecked = it }
            )
            Text("我同意服务条款")
        }

        // 5. RadioButton 单选按钮
        var selectedOption by remember { mutableStateOf("A") }
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedOption == "A",
                    onClick = { selectedOption = "A" }
                )
                Text("选项 A")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedOption == "B",
                    onClick = { selectedOption = "B" }
                )
                Text("选项 B")
            }
        }

        // 6. Slider 滑块
        var sliderValue by remember { mutableFloatStateOf(0.5f) }
        Column {
            Text("进度: ${(sliderValue * 100).toInt()}%")
            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it }
            )
        }

        // 7. DropdownMenu 下拉菜单
        var expanded by remember { mutableStateOf(false) }
        var selectedItem by remember { mutableStateOf("选择一项") }
        
        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(selectedItem)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("选项 1") },
                    onClick = { 
                        selectedItem = "选项 1"
                        expanded = false 
                    }
                )
                DropdownMenuItem(
                    text = { Text("选项 2") },
                    onClick = { 
                        selectedItem = "选项 2"
                        expanded = false 
                    }
                )
            }
        }
    }
}
