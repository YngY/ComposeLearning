package com.example.composelearning.viewmodel

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ViewModel 示例
 * 包含: ViewModel, StateFlow, rememberCoroutineScope
 */

// 1. 简单的 ViewModel 示例
class CounterViewModel : ViewModel() {
    // 状态使用 StateFlow
    private val _count = mutableStateOf(0)
    val count: Int get() = _count.value

    fun increment() {
        _count.value++
    }

    fun decrement() {
        _count.value--
    }

    fun reset() {
        _count.value = 0
    }
}

// 2. 使用 StateFlow 的 ViewModel
class TimerViewModel : ViewModel() {
    private val _time = mutableStateOf(0)
    val time: Int get() = _time.value

    private val _isRunning = mutableStateOf(false)
    val isRunning: Boolean get() = _isRunning.value

    private var job: kotlinx.coroutines.Job? = null

    fun start() {
        if (_isRunning.value) return
        _isRunning.value = true
        job = viewModelScope.launch {
            while (_isRunning.value) {
                delay(1000)
                _time.value++
            }
        }
    }

    fun stop() {
        _isRunning.value = false
        job?.cancel()
    }

    fun reset() {
        stop()
        _time.value = 0
    }
}

// 3. 带异步加载的 ViewModel
data class User(val id: Int, val name: String, val email: String)

class UserViewModel : ViewModel() {
    private val _users = mutableStateOf<List<User>>(emptyList())
    val users: List<User> get() = _users.value

    private val _isLoading = mutableStateOf(false)
    val isLoading: Boolean get() = _isLoading.value

    private val _error = mutableStateOf<String?>(null)
    val error: String? get() = _error.value

    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // 模拟网络请求
                delay(1500)
                _users.value = listOf(
                    User(1, "张三", "zhangsan@example.com"),
                    User(2, "李四", "lisi@example.com"),
                    User(3, "王五", "wangwu@example.com")
                )
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}

// ============= Compose 页面 =============

@Composable
fun CounterViewModelScreen(
    viewModel: CounterViewModel = androidx.lifecycle.viewModel.compose.rememberViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("计数器 (ViewModel)", style = MaterialTheme.typography.headlineMedium)
        Text(
            "${viewModel.count}",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { viewModel.increment() }) { Text("+") }
            Button(onClick = { viewModel.decrement() }) { Text("-") }
            Button(onClick = { viewModel.reset() }) { Text("重置") }
        }
    }
}

@Composable
fun TimerViewModelScreen(
    viewModel: TimerViewModel = androidx.lifecycle.viewmodel.compose.rememberViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("计时器 (ViewModel)", style = MaterialTheme.typography.headlineMedium)
        Text(
            "${viewModel.time} 秒",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { viewModel.start() },
                enabled = !viewModel.isRunning
            ) { Text("开始") }
            Button(onClick = { viewModel.stop() }) { Text("暂停") }
            Button(onClick = { viewModel.reset() }) { Text("重置") }
        }
    }
}

@Composable
fun UserViewModelScreen(
    viewModel: UserViewModel = androidx.lifecycle.viewmodel.compose.rememberViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("用户列表 (ViewModel)", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        when {
            viewModel.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            viewModel.error != null -> {
                Text("错误: ${viewModel.error}", color = MaterialTheme.colorScheme.error)
            }
            else -> {
                viewModel.users.forEach { user ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(user.name, style = MaterialTheme.typography.titleMedium)
                            Text(user.email, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
