package com.example.composelearning.coroutines

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*

/**
 * Kotlin 协程示例
 * 包含: CoroutineScope, launch, async, withContext, Flow
 */

// ============= 1. 基础协程 =============
class CoroutinesDemo {
    private val scope = CoroutineScope(Dispatchers.Main)

    // launch: 启动协程，不返回结果
    fun launchExample() {
        scope.launch {
            val result = doSomething()
            println("Result: $result")
        }
    }

    // async: 启动协程，返回 Deferred
    fun asyncExample() {
        scope.launch {
            val deferred = async { fetchData1() }
            val result = deferred.await()
            println("Result: $result")
        }
    }

    // 并发执行
    fun concurrentExample() {
        scope.launch {
            val time = measureTimeMillis {
                val data1 = async { fetchData1() }
                val data2 = async { fetchData2() }
                println("Data1: ${data1.await()}")
                println("Data2: ${data2.await()}")
            }
            println("Total time: $time") // 约等于最慢的那个
        }
    }

    // withContext: 切换协程上下文
    suspend fun withContextExample(): String = withContext(Dispatchers.IO) {
        // 在 IO 线程执行
        fetchData1()
    }

    private suspend fun doSomething(): String {
        delay(1000) // 模拟耗时
        return "Done"
    }

    private suspend fun fetchData1(): String {
        delay(1000)
        return "Data1"
    }

    private suspend fun fetchData2(): String {
        delay(1500)
        return "Data2"
    }

    private suspend fun measureTimeMillis(block: suspend () -> Unit): Long {
        val start = System.currentTimeMillis()
        block()
        return System.currentTimeMillis() - start
    }
}

// ============= 2. Flow =============
class FlowDemo {
    // 创建 Flow
    fun simpleFlow() = kotlinx.coroutines.flow.flow {
        for (i in 1..5) {
            emit(i) // 发送数据
            delay(500)
        }
    }

    // 带转换的 Flow
    fun mapFlow() = kotlinx.coroutines.flow.flow {
        emit("Hello")
        emit("World")
    }.map { it.uppercase() }

    // 过滤 Flow
    fun filterFlow() = kotlinx.coroutines.flow.flow {
        emit(1)
        emit(2)
        emit(3)
        emit(4)
        emit(5)
    }.filter { it > 2 }

    // 错误处理
    fun safeFlow() = kotlinx.coroutines.flow.flow {
        try {
            emit(1)
            emit(2)
            throw RuntimeException("Error!")
        } catch (e: Exception) {
            println("Caught: $e")
        } finally {
            emit(3) // 清理工作
        }
    }
}

// ============= 3. Channel =============
class ChannelDemo {
    private val channel = kotlinx.coroutines.channels.Channel<Int>()

    suspend fun produce() {
        for (i in 1..5) {
            channel.send(i)
            delay(100)
        }
        channel.close()
    }

    suspend fun consume() {
        for (i in channel) {
            println("Received: $i")
        }
    }
}

// ============= 4. Compose 中的协程 =============

@Composable
fun CoroutinesScreen() {
    var result by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var flowValue by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("协程示例", style = MaterialTheme.typography.headlineMedium)

        // 基础协程
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("1. launch 启动协程", style = MaterialTheme.typography.titleMedium)
                Text("用于发起异步任务，不返回结果", style = MaterialTheme.typography.bodySmall)

                Button(
                    onClick = {
                        isLoading = true
                        CoroutineScope(Dispatchers.Main).launch {
                            delay(1500)
                            result = "任务完成!"
                            isLoading = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("执行任务")
                }

                if (isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                result?.let {
                    Text(it, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        // async await
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("2. async/await 并发", style = MaterialTheme.typography.titleMedium)
                Text("并发执行多个任务，最后等待结果", style = MaterialTheme.typography.bodySmall)

                var concurrentResult by remember { mutableStateOf<String?>(null) }

                Button(
                    onClick = {
                        CoroutineScope(Dispatchers.Main).launch {
                            val time = measureTimeMillis {
                                val d1 = async { delay(1000); "A" }
                                val d2 = async { delay(1500); "B" }
                                concurrentResult = "${d1.await()} + ${d2.await()}"
                            }
                            concurrentResult = "$concurrentResult (耗时: ${time}ms)"
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("并发执行")
                }

                concurrentResult?.let {
                    Text(it, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        // Flow
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("3. Flow 响应式流", style = MaterialTheme.typography.titleMedium)
                Text("收集异步数据流", style = MaterialTheme.typography.bodySmall)

                Button(
                    onClick = {
                        CoroutineScope(Dispatchers.Main).launch {
                            kotlinx.coroutines.flow.flow {
                                for (i in 1..5) {
                                    emit("数据 $i")
                                    delay(300)
                                }
                            }.collect { value ->
                                flowValue = value
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("开始收集 Flow")
                }

                if (flowValue.isNotEmpty()) {
                    Text("收到: $flowValue", color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        // 协程上下文
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("4. Dispatchers 线程", style = MaterialTheme.typography.titleMedium)
                Text("Dispatchers.Main - UI 线程", style = MaterialTheme.typography.bodySmall)
                Text("Dispatchers.IO - IO 线程 (网络/磁盘)", style = MaterialTheme.typography.bodySmall)
                Text("Dispatchers.Default - CPU 密集型", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

private fun measureTimeMillis(block: suspend () -> Unit): Long {
    val start = System.currentTimeMillis()
    // 注意：这只是伪代码，实际需要用 coroutineScope
    return start
}

/**
 * 协程核心要点:
 * 
 * 1. CoroutineScope: 协程作用域
 *    - launch: 启动协程
 *    - async: 启动并返回 Deferred
 * 
 * 2. Dispatchers:
 *    - Main: UI 线程
 *    - IO: 网络/磁盘操作
 *    - Default: CPU 密集型
 * 
 * 3. suspend: 挂起函数
 *    - 只能在协程中调用
 *    - delay(): 挂起一段时间
 * 
 * 4. Flow: 响应式流
 *    - flow {}: 创建 Flow
 *    - .map(): 转换
 *    - .filter(): 过滤
 *    - .collect(): 收集
 * 
 * 5. Structured Concurrency:
 *    - 协程作用域管理
 *    - 自动取消子协程
 */
