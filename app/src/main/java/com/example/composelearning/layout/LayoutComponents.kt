package com.example.composelearning.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 布局组件示例
 * 包含: Column, Row, Box, Spacer, VerticalScroll
 */
@Composable
fun LayoutComponentsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Column 垂直布局
        Text("1. Column 垂直布局", style = MaterialTheme.typography.titleMedium)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Gray)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.Red)
            )
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.Green)
            )
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.Blue)
            )
        }

        // 2. Row 水平布局
        Text("2. Row 水平布局", style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Gray)
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.Red)
            )
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.Green)
            )
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.Blue)
            )
        }

        // 3. Box 绝对定位
        Text("3. Box 绝对定位", style = MaterialTheme.typography.titleMedium)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .border(1.dp, Color.Gray)
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(Color.Red)
                    .align(Alignment.TopStart)
            )
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(Color.Green)
                    .align(Alignment.Center)
            )
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(Color.Blue)
                    .align(Alignment.BottomEnd)
            )
        }

        // 4. Spacer 间距
        Text("4. Spacer 间距", style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(Color.Red)
            )
            Spacer(modifier = Modifier.width(20.dp))
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(Color.Green)
            )
        }

        // 5. weight 权重分配
        Text("5. weight 权重分配", style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .background(Color.Red)
            )
            Box(
                modifier = Modifier
                    .weight(2f)
                    .height(50.dp)
                    .background(Color.Green)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .background(Color.Blue)
            }
        }
    }
}
