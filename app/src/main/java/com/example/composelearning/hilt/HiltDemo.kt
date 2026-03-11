package com.example.composelearning.hilt

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Hilt 依赖注入示例
 * 
 * 使用 Hilt 需要:
 * 1. 在 build.gradle 添加依赖
 * 2. 创建 @HiltAndroidApp Application
 * 3. 创建 @AndroidEntryPoint Activity/Fragment
 * 4. 使用 @Inject 注入依赖
 */

// ============= 1. Application 级别 =============
/*
@HiltAndroidApp
class MyApplication : Application()
*/

// ============= 2. 模块定义 =============
/*
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    // 提供简单值
    @Provides
    @Singleton
    fun provideString(): String = "Hello Hilt"
    
    // 提供依赖
    @Provides
    @Singleton
    fun provideRepository(api: ApiService): Repository {
        return RepositoryImpl(api)
    }
}
*/

// ============= 3. 依赖类示例 =============
/*
// 数据层
class ApiService {
    fun fetchData() = "Data from API"
}

// 仓库层
class Repository(private val api: ApiService) {
    fun getData() = api.fetchData()
}
*/

// ============= 4. ViewModel 注入 =============
/*
@HiltViewModel
class MyViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    val data = repository.getData()
}
*/

// ============= 5. Composable 中使用 =============
/*
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyScreen()
        }
    }
}

@Composable
fun MyScreen(
    viewModel: MyViewModel = hiltViewModel()
) {
    Text(viewModel.data)
}
*/

/**
 * 常用注解说明:
 * 
 * @HiltAndroidApp        - 标记 Application 类，启用 Hilt
 * @AndroidEntryPoint     - 标记 Activity/Fragment
 * @HiltViewModel         - 标记 ViewModel，配合 hiltViewModel() 使用
 * @Inject                - 标记构造函数进行注入
 * @Module                - 标记模块类
 * @Provides              - 标记提供依赖的方法
 * @Singleton             - 单例
 * @Named                 - 区分同名依赖
 * @Qualifier             - 自定义限定符
 */

@Composable
fun HiltDemoScreen() {
    androidx.compose.foundation.layout.Column(
        modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
    ) {
        androidx.compose.material3.Text(
            "Hilt 依赖注入",
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
        )
        
        androidx.compose.material3.Card(
            modifier = androidx.compose.ui.Modifier.fillMaxWidth()
        ) {
            androidx.compose.foundation.layout.Column(
                modifier = androidx.compose.ui.Modifier.padding(16.dp)
            ) {
                androidx.compose.material3.Text("使用步骤:", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                androidx.compose.material3.Text("1. 添加 build.gradle 依赖")
                androidx.compose.material3.Text("2. @HiltAndroidApp 标记 Application")
                androidx.compose.material3.Text("3. @AndroidEntryPoint 标记 Activity")
                androidx.compose.material3.Text("4. @HiltViewModel 标记 ViewModel")
                androidx.compose.material3.Text("5. @Inject 标记构造函数")
            }
        }

        androidx.compose.material3.Card(
            modifier = androidx.compose.ui.Modifier.fillMaxWidth()
        ) {
            androidx.compose.foundation.layout.Column(
                modifier = androidx.compose.ui.Modifier.padding(16.dp)
            ) {
                androidx.compose.material3.Text("Gradle 依赖:", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                androidx.compose.material3.Text("implementation 'com.google.dagger:hilt-android:2.48'")
                androidx.compose.material3.Text("kapt 'com.google.dagger:hilt-android-compiler:2.48'")
            }
        }
    }
}

private fun androidx.compose.ui.Modifier.fillMaxWidth() = this.then(androidx.compose.foundation.layout.fillMaxWidth())
private fun androidx.compose.ui.Modifier.fillMaxSize() = this.then(androidx.compose.foundation.layout.fillMaxSize())
