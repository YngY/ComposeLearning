package com.example.composelearning.startup

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.startup.InitializationProvider
import androidx.startup.WorkManagerInitializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * App Startup 示例
 * 包含: 初始化器、依赖初始化、WorkManager 集成
 */

// ============= 1. 自定义初始化器 =============
class AppInitializer(
    private val context: Context
) {
    companion object {
        const val TAG = "AppInitializer"
    }

    // 模拟初始化
    suspend fun initialize(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "开始初始化...")

            // 模拟初始化工作
            // 1. 初始化数据库
            // initializeDatabase()

            // 2. 加载配置
            // loadConfiguration()

            // 3. 预热缓存
            // warmUpCache()

            Thread.sleep(500) // 模拟耗时

            Log.d(TAG, "初始化完成")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "初始化失败", e)
            Result.failure(e)
        }
    }
}

// ============= 2. WorkManager 初始化器 =============
class WorkManagerInitializer : androidx.startup.Initializer<WorkManager> {
    override fun create(context: Context): WorkManager {
        Log.d("WorkManagerInit", "初始化 WorkManager")

        val config = androidx.work.Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()

        return androidx.work.WorkManager.getInstance(context)
    }

    override fun dependencies(): List<Class<out androidx.startup.Initializer<*>>> {
        // 依赖其他初始化器
        return emptyList()
    }
}

// ============= 3. 数据库初始化器 =============
class DatabaseInitializer : androidx.startup.Initializer<Unit> {
    override fun create(context: Context): Unit {
        Log.d("DatabaseInit", "初始化数据库")
        // 初始化 Room 数据库
        // DatabaseManager.initialize(context)
    }

    override fun dependencies(): List<Class<out androidx.startup.Initializer<*>>> {
        return emptyList()
    }
}

// ============= 4. 网络初始化器 =============
class NetworkInitializer : androidx.startup.Initializer<Unit> {
    override fun create(context: Context): Unit {
        Log.d("NetworkInit", "初始化网络库")
        // 配置 OkHttp
        // 预热连接
    }

    override fun dependencies(): List<Class<out androidx.startup.Initializer<*>>> {
        return listOf(DatabaseInitializer::class.java)
    }
}

// ============= 5. Compose 页面 =============

@Composable
fun StartupDemoScreen() {
    var initStatus by remember { mutableStateOf<String?>(null) }
    var isInitializing by remember { mutableStateOf(false) }
    var startTime by remember { mutableLongStateOf(0L) }
    var endTime by remember { mutableLongStateOf(0L) }

    val context = LocalContext.current
    val initializer = remember { AppInitializer(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("App Startup 示例", style = MaterialTheme.typography.headlineMedium)

        // 使用 WorkManager 初始化器
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("1. WorkManager 初始化",
                    style = MaterialTheme.typography.titleMedium)

                Text(
                    "WorkManager 在应用启动时自动初始化，可以配置自定义初始化器。",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                CodeBlock("""
                    <!-- AndroidManifest.xml -->
                    <provider
                        android:name="androidx.startup.InitializationProvider"
                        android:authorities="\${applicationId}.androidx-startup"
                        android:exported="false">
                        <meta-data
                            android:name="androidx.work.WorkManagerInitializer"
                            android:value="androidx.startup" />
                    </provider>
                """.trimIndent())
            }
        }

        // 使用初始化器
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("2. 自定义初始化器",
                    style = MaterialTheme.typography.titleMedium)

                Text(
                    "使用 App Startup 库可以优化应用启动性能，延迟初始化非必要组件。",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        isInitializing = true
                        startTime = System.currentTimeMillis()

                        kotlinx.coroutines.MainScope().launch {
                            val result = initializer.initialize()
                            endTime = System.currentTimeMillis()
                            isInitializing = false

                            initStatus = if (result.isSuccess) {
                                "初始化成功! 耗时: ${endTime - startTime}ms"
                            } else {
                                "初始化失败: ${result.exceptionOrNull()?.message}"
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isInitializing
                ) {
                    Text(if (isInitializing) "初始化中..." else "执行初始化")
                }

                if (isInitializing) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                initStatus?.let { status ->
                    Text(
                        status,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (status.contains("成功"))
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        // 声明初始化器
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("3. 在 AndroidManifest 中声明",
                    style = MaterialTheme.typography.titleMedium)

                CodeBlock("""
                    <provider
                        android:name="androidx.startup.InitializationProvider"
                        android:authorities="\${applicationId}.androidx-startup"
                        android:exported="false">
                        <meta-data
                            android:name="com.example.app.DatabaseInitializer"
                            android:value="androidx.startup" />
                        <meta-data
                            android:name="com.example.app.NetworkInitializer"
                            android:value="androidx.startup" />
                    </provider>
                """.trimIndent())
            }
        }

        // 优点
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("App Startup 优点:",
                    style = MaterialTheme.typography.titleSmall)
                Text("• 优化应用启动速度", style = MaterialTheme.typography.bodySmall)
                Text("• 统一初始化流程", style = MaterialTheme.typography.bodySmall)
                Text("• 管理初始化依赖关系", style = MaterialTheme.typography.bodySmall)
                Text("• 支持延迟初始化", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun CodeBlock(code: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text(
            code,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(12.dp)
        )
    }
}

private fun kotlinx.coroutines.MainScope() = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main)

/**
 * App Startup 核心要点:
 * 
 * 1. Initializer<T>: 初始化器接口
 * 2. create(): 执行初始化
 * 3. dependencies(): 声明依赖的初始化器
 * 4. InitializationProvider: AndroidManifest 配置
 * 
 * Gradle 依赖:
 * implementation "androidx.startup:startup-runtime:1.1.1"
 */
