package com.example.composelearning.animation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * 动画示例
 * 包含: animateColorAsState, animateFloatAsState, AnimatedVisibility, AnimatedContent
 */

/**
 * 示例1: 颜色动画
 */
@Composable
fun ColorAnimationScreen() {
    var isBlue by remember { mutableStateOf(true) }
    
    val backgroundColor by animateColorAsState(
        targetValue = if (isBlue) Color.Blue else Color.Red,
        label = "color"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .clickable { isBlue = !isBlue },
        contentAlignment = Alignment.Center
    ) {
        Text("点击切换颜色", color = Color.White)
    }
}

/**
 * 示例2: 大小动画
 */
@Composable
fun SizeAnimationScreen() {
    var isExpanded by remember { mutableStateOf(false) }
    
    val size by animateDpAsState(
        targetValue = if (isExpanded) 200.dp else 100.dp,
        animationSpec = tween(durationMillis = 500),
        label = "size"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .background(Color.Green)
                .clickable { isExpanded = !isExpanded }
        )
    }
}

/**
 * 示例3: 位移动画
 */
@Composable
fun OffsetAnimationScreen() {
    var moved by remember { mutableStateOf(false) }
    
    val offsetX by animateFloatAsState(
        targetValue = if (moved) 200f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "offsetX"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .size(50.dp)
                .background(Color.Magenta)
                .clickable { moved = !moved }
        )
    }
}

/**
 * 示例4: 可见性动画
 */
@Composable
fun VisibilityAnimationScreen() {
    var visible by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(onClick = { visible = !visible }) {
            Text(if (visible) "隐藏" else "显示")
        }

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color.Cyan)
            ) {
                Text("我出现了!", Modifier.align(Alignment.Center))
            }
        }
    }
}

/**
 * 示例5: 内容切换动画
 */
@Composable
fun ContentSwitchAnimationScreen() {
    var isStateA by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(onClick = { isStateA = !isStateA }) {
            Text("切换")
        }

        AnimatedContent(
            targetState = isStateA,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith
                        fadeOut(animationSpec = tween(300))
            },
            label = "content"
        ) { state ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(if (state) Color.Blue else Color.Red),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (state) "状态 A" else "状态 B",
                    color = Color.White
                )
            }
        }
    }
}

/**
 * 示例6: 无限循环动画
 */
@Composable
fun InfiniteAnimationScreen() {
    val infiniteTransition = rememberInfiniteTransition(label = "infinite")
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Color.Blue.copy(alpha = alpha))
        )
    }
}
