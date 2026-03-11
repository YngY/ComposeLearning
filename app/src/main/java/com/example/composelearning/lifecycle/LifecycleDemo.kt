package com.example.composelearning.lifecycle

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.*

/**
 * Lifecycle 示例
 * 包含: LifecycleOwner, LiveData, Flow 收集, 生命周期感知
 */

// ============= 1. LiveData =============
class LifecycleViewModel : ViewModel() {
    // 使用 MutableLiveData
    private val _counter = MutableLiveData(0)
    val counter: LiveData<Int> = _counter

    private val _message = MutableLiveData<String>("初始消息")
    val message: LiveData<String> = _message

    // 使用 MutableStateFlow (推荐)
    private val _state = MutableStateFlow(LifecycleState())
    val state: StateFlow<LifecycleState> = _state

    fun increment() {
        _counter.value = (_counter.value ?: 0) + 1
    }

    fun setMessage(msg: String) {
        _message.value = msg
    }

    fun updateState(name: String, age: Int) {
        _state.value = LifecycleState(name, age)
    }
}

data class LifecycleState(
    val name: String = "",
    val age: Int = 0
)

// ============= 2. 生命周期感知组件 =============
class LifecycleAwareObserver(
    private val onResume: () -> Unit = {},
    private val onPause: () -> Unit = {}
) : DefaultLifecycleObserver {

    override fun onResume(owner: LifecycleOwner) {
        onResume()
    }

    override fun onPause(owner: LifecycleOwner) {
        onPause()
    }
}

// 使用示例
@Composable
fun LifecycleAwareComponent() {
    val lifecycleOwner = LocalLifecycleOwner.current
    var status by remember { mutableStateOf("") }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleAwareObserver(
            onResume = { status = "已恢复" },
            onPause = { status = "已暂停" }
        )
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Text(status)
}

// ============= 3. Flow 收集 (生命周期感知) =============
@Composable
fun <T> observeAsState(
    flow: kotlinx.coroutines.flow.Flow<T>,
    initial: T
): State<T> {
    val state = remember { mutableStateOf(initial) }
    
    LaunchedEffect(flow) {
        flow.collect { state.value = it }
    }
    
    return state
}

// ============= 4. Compose 页面 =============

@Composable
fun LifecycleDemoScreen(
    viewModel: LifecycleViewModel = androidx.lifecycle.viewmodel.compose.rememberViewModel()
) {
    // 收集 LiveData
    val counter by viewModel.counter.observeAsState(0)
    val message by viewModel.message.observeAsState("初始消息")

    // 收集 StateFlow
    val lifecycleState by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Lifecycle 示例", style = MaterialTheme.typography.headlineMedium)

        // 计数器
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("LiveData 计数器", style = MaterialTheme.typography.titleMedium)
                Text(
                    "$counter",
                    style = MaterialTheme.typography.displayMedium
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { viewModel.increment() }) {
                        Text("+1")
                    }
                }
            }
        }

        // 消息
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("LiveData 消息", style = MaterialTheme.typography.titleMedium)
                Text(message, style = MaterialTheme.typography.bodyLarge)
                
                var input by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    label = { Text("输入消息") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { 
                        viewModel.setMessage(input)
                        input = ""
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("更新消息")
                }
            }
        }

        // StateFlow
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("StateFlow 状态", style = MaterialTheme.typography.titleMedium)
                Text("姓名: ${lifecycleState.name.ifEmpty { "未设置" }}")
                Text("年龄: ${if (lifecycleState.age > 0) lifecycleState.age else "未设置"}")

                var name by remember { mutableStateOf("") }
                var age by remember { mutableStateOf("") }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("姓名") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("年龄") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { 
                        viewModel.updateState(name, age.toIntOrNull() ?: 0)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("更新状态")
                }
            }
        }

        // 生命周期感知
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("生命周期感知", style = MaterialTheme.typography.titleMedium)
                Text("观察 Activity/Fragment 生命周期", style = MaterialTheme.typography.bodySmall)
                LifecycleAwareComponent()
            }
        }
    }
}

/**
 * Lifecycle 核心要点:
 * 
 * 1. LifecycleOwner: 拥有生命周期的组件 (Activity, Fragment)
 * 2. Lifecycle: 存储生命周期状态
 * 3. LiveData: 可观察的数据持有类
 * 4. StateFlow: Kotlin 协程中的响应式数据流
 * 5. observeAsState(): 在 Compose 中收集 LiveData
 * 6. collectAsState(): 在 Compose 中收集 Flow
 * 7. DisposableEffect: 生命周期清理
 */
