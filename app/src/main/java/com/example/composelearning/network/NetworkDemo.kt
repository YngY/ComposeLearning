package com.example.composelearning.network

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * 网络请求示例
 * 包含: OkHttp, Retrofit (伪代码), 协程 + suspend
 */

// ============= 1. OkHttp 示例 =============
class NetworkClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // 同步请求 (需在协程中使用)
    suspend fun fetchUrl(url: String): String = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url)
            .build()
        
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            response.body?.string() ?: ""
        }
    }

    // 异步请求
    fun fetchUrlAsync(url: String, callback: (Result<String>) -> Unit) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                callback(Result.failure(e))
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.use {
                    if (it.isSuccessful) {
                        callback(Result.success(it.body?.string() ?: ""))
                    } else {
                        callback(Result.failure(IOException("Error: ${it.code}")))
                    }
                }
            }
        })
    }
}

// ============= 2. 数据模型 =============
data class Post(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)

data class User(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val phone: String,
    val website: String
)

// ============= 3. Repository 示例 =============
class PostRepository {
    private val networkClient = NetworkClient()

    suspend fun getPosts(): List<Post> {
        return try {
            val json = networkClient.fetchUrl("https://jsonplaceholder.typicode.com/posts")
            val array = JSONArray(json)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                Post(
                    userId = obj.getInt("userId"),
                    id = obj.getInt("id"),
                    title = obj.getString("title"),
                    body = obj.getString("body")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getUsers(): List<User> {
        return try {
            val json = networkClient.fetchUrl("https://jsonplaceholder.typicode.com/users")
            val array = JSONArray(json)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                User(
                    id = obj.getInt("id"),
                    name = obj.getString("name"),
                    username = obj.getString("username"),
                    email = obj.getString("email"),
                    phone = obj.getString("phone"),
                    website = obj.getString("website")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}

// ============= 4. ViewModel =============
class NetworkViewModel : androidx.lifecycle.ViewModel() {
    private val repository = PostRepository()

    private val _posts = mutableStateOf<List<Post>>(emptyList())
    val posts: List<Post> get() = _posts.value

    private val _users = mutableStateOf<List<User>>(emptyList())
    val users: List<User> get() = _users.value

    private val _isLoading = mutableStateOf(false)
    val isLoading: Boolean get() = _isLoading.value

    private val _error = mutableStateOf<String?>(null)
    val error: String? get() = _error.value

    fun loadPosts() {
        androidx.lifecycle.viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _posts.value = repository.getPosts()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadUsers() {
        androidx.lifecycle.viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _users.value = repository.getUsers()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}

// ============= 5. Compose 页面 =============

@Composable
fun NetworkDemoScreen(
    viewModel: NetworkViewModel = androidx.lifecycle.viewmodel.compose.rememberViewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Posts") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Users") }
            )
        }

        when (selectedTab) {
            0 -> PostsTab(viewModel)
            1 -> UsersTab(viewModel)
        }
    }
}

@Composable
private fun PostsTab(viewModel: NetworkViewModel) {
    LaunchedEffect(Unit) {
        if (viewModel.posts.isEmpty()) {
            viewModel.loadPosts()
        }
    }

    when {
        viewModel.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        viewModel.error != null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: ${viewModel.error}", color = MaterialTheme.colorScheme.error)
            }
        }
        else -> {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.posts.take(10)) { post ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                post.title,
                                style = MaterialTheme.typography.titleSmall,
                                maxLines = 2
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                post.body,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 3
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UsersTab(viewModel: NetworkViewModel) {
    LaunchedEffect(Unit) {
        if (viewModel.users.isEmpty()) {
            viewModel.loadUsers()
        }
    }

    when {
        viewModel.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        else -> {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.users) { user ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(user.name, style = MaterialTheme.typography.titleMedium)
                            Text(user.email, style = MaterialTheme.typography.bodySmall)
                            Text(user.website, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
