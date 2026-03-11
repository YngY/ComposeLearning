package com.example.composelearning.workmanager

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.work.*
import java.util.concurrent.TimeUnit

/**
 * WorkManager 示例
 * 包含: Worker, WorkRequest, WorkManager, 定时任务
 */

// ============= 1. 简单 Worker =============
class SimpleWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // 模拟耗时工作
            val data = inputData.getString(KEY_INPUT) ?: "default"
            
            // 这里执行实际工作
            println("SimpleWorker 执行中: $data")
            
            // 返回结果
            val outputData = workDataOf(KEY_OUTPUT to "处理完成: $data")
            Result.success(outputData)
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val KEY_INPUT = "input"
        const val KEY_OUTPUT = "output"
    }
}

// ============= 2. 带进度的 Worker =============
class ProgressWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            for (i in 1..10) {
                // 更新进度
                setProgress(workDataOf(KEY_PROGRESS to i * 10))
                kotlinx.coroutines.delay(500)
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val KEY_PROGRESS = "progress"
    }
}

// ============= 3. 定时 Worker (PeriodicWorkRequest) =============
class PeriodicSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // 执行定时同步任务
        println("PeriodicSyncWorker 执行定时同步")
        return Result.success()
    }
}

// ============= 4. WorkManager 管理器 =============
class WorkManagerHelper(private val context: Context) {
    private val workManager = WorkManager.getInstance(context)

    // 一次性任务
    fun runOneTimeWork(tag: String = "simple_work") {
        val inputData = workDataOf(SimpleWorker.KEY_INPUT to "Hello WorkManager")
        
        val workRequest = OneTimeWorkRequestBuilder<SimpleWorker>()
            .setInputData(inputData)
            .addTag(tag)
            .build()

        workManager.enqueue(workRequest)
    }

    // 带约束的任务
    fun runWithConstraints() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // 需要网络
            .setRequiresBatteryNotLow(true) // 电量不低
            .build()

        val workRequest = OneTimeWorkRequestBuilder<SimpleWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueue(workRequest)
    }

    // 定时任务 (每15分钟执行)
    fun runPeriodicWork() {
        val workRequest = PeriodicWorkRequestBuilder<PeriodicSyncWorker>(
            15, TimeUnit.MINUTES
        ).build()

        workManager.enqueueUniquePeriodicWork(
            "periodic_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    // 观察工作状态
    fun observeWork(tag: String) = workManager.getWorkInfosByTagLiveData(tag)
}

// ============= 5. 链式任务 =============
class ChainWorkerA(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val output = "Worker A 完成"
        return Result.success(workDataOf("result" to output))
    }
}

class ChainWorkerB(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val input = inputData.getString("result") ?: ""
        return Result.success(workDataOf("result" to "$input -> Worker B 完成"))
    }
}

class ChainWorkerC(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val input = inputData.getString("result") ?: ""
        return Result.success(workDataOf("result" to "$input -> Worker C 完成"))
    }
}

fun runChainWork(workManager: WorkManager) {
    val workA = OneTimeWorkRequestBuilder<ChainWorkerA>().build()
    val workB = OneTimeWorkRequestBuilder<ChainWorkerB>().build()
    val workC = OneTimeWorkRequestBuilder<ChainWorkerC>().build()

    // A -> B -> C 顺序执行
    workManager.beginWith(workA)
        .then(workB)
        .then(workC)
        .enqueue()
}

// ============= 6. Compose 页面 =============

@Composable
fun WorkManagerDemoScreen() {
    val context = LocalContext.current
    val workManagerHelper = remember { WorkManagerHelper(context) }

    var workStatus by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("WorkManager 示例", style = MaterialTheme.typography.headlineMedium)

        // 一次性任务
        Button(
            onClick = {
                workManagerHelper.runOneTimeWork()
                workStatus = "一次性任务已启动"
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("执行一次性任务")
        }

        // 带约束的任务
        Button(
            onClick = {
                workManagerHelper.runWithConstraints()
                workStatus = "带约束的任务已启动 (需要网络)"
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("执行带约束的任务")
        }

        // 定时任务
        Button(
            onClick = {
                workManagerHelper.runPeriodicWork()
                workStatus = "定时任务已启动 (每15分钟)"
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("启动定时同步")
        }

        // 链式任务
        Button(
            onClick = {
                runChainWork(WorkManager.getInstance(context))
                workStatus = "链式任务已启动 (A->B->C)"
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("执行链式任务 (A→B→C)")
        }

        Divider()

        // 状态显示
        Text("任务状态:", style = MaterialTheme.typography.titleMedium)
        Text(workStatus.ifEmpty { "等待执行..." })

        Divider()

        // 说明
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("WorkManager 特点:", style = MaterialTheme.typography.titleSmall)
                Text("• 保证任务一定执行", style = MaterialTheme.typography.bodySmall)
                Text("• 后台任务不依赖 Activity", style = MaterialTheme.typography.bodySmall)
                Text("• 支持约束条件", style = MaterialTheme.typography.bodySmall)
                Text("• 支持定时任务", style = MaterialTheme.typography.bodySmall)
                Text("• 支持链式任务", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
