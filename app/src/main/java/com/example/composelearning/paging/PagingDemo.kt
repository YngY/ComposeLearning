package com.example.composelearning.paging

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.*
import kotlinx.coroutines.flow.flow

/**
 * Paging 3 示例
 * 包含: PagingData, PagingSource, RemoteMediator
 */

// ============= 1. 数据模型 =============
data class Article(
    val id: Int,
    val title: String,
    val description: String,
    val author: String,
    val publishedAt: Long
)

// ============= 2. PagingSource =============
class ArticlePagingSource : PagingSource<Int, Article>() {
    
    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        return try {
            val page = params.key ?: 1
            val pageSize = params.loadSize

            // 模拟网络请求
            val articles = fetchArticles(page, pageSize)

            LoadResult.Page(
                data = articles,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (articles.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private suspend fun fetchArticles(page: Int, pageSize: Int): List<Article> {
        // 模拟网络延迟
        kotlinx.coroutines.delay(1000)

        // 生成模拟数据
        val startId = (page - 1) * pageSize
        return (0 until pageSize).map { i ->
            Article(
                id = startId + i,
                title = "文章标题 ${startId + i + 1}",
                description = "这是文章 ${startId + i + 1} 的描述内容，包含了丰富的信息...",
                author = listOf("张三", "李四", "王五").random(),
                publishedAt = System.currentTimeMillis() - (startId + i) * 3600000L
            )
        }
    }
}

// ============= 3. Repository =============
class ArticleRepository {
    fun getArticles(): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                initialLoadSize = 20
            ),
            pagingSourceFactory = { ArticlePagingSource() }
        ).flow
    }
}

// ============= 4. 无限滚动列表 =============
@Composable
fun PagingListScreen(
    repository: ArticleRepository = remember { ArticleRepository() }
) {
    val articles = repository.getArticles().collectAsLazyPagingItems()

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            count = articles.itemCount,
            key = { index -> articles[index]?.id ?: index }
        ) { index ->
            articles[index]?.let { article ->
                ArticleCard(article = article)
            }
        }

        // 加载状态
        when (articles.loadState.append) {
            is LoadState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
            is LoadState.Error -> {
                item {
                    Text(
                        "加载更多失败",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            else -> {}
        }

        // 初始加载
        when (articles.loadState.refresh) {
            is LoadState.Loading -> {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
            is LoadState.Error -> {
                item {
                    Text(
                        "加载失败: ${(articles.loadState.refresh as LoadState.Error).error.message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            else -> {}
        }
    }
}

@Composable
private fun ArticleCard(article: Article) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                article.title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                article.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    article.author,
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    formatTime(article.publishedAt),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

private fun formatTime(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val hours = diff / 3600000
    return if (hours < 24) "${hours}小时前" else "${hours / 24}天前"
}

// ============= 5. 带索引的 Paging =============
@Composable
fun PagingWithIndexScreen() {
    val listState = rememberLazyListState()
    var isLoading by remember { mutableStateOf(false) }
    var items by remember { mutableStateOf(listOf<Article>()) }
    var currentPage by remember { mutableIntStateOf(1) }

    // 无限滚动检测
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null && lastVisibleItem.index >= items.size - 5
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value && !isLoading && items.isNotEmpty()) {
            isLoading = true
            // 模拟加载更多
            kotlinx.coroutines.delay(1000)
            val newItems = (0 until 10).map {
                Article(
                    id = items.size + it,
                    title = "加载的文章 ${items.size + it + 1}",
                    description = "这是加载的更多内容...",
                    author = "作者",
                    publishedAt = System.currentTimeMillis()
                )
            }
            items = items + newItems
            currentPage++
            isLoading = false
        }
    }

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items) { article ->
            ArticleCard(article = article)
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

// ============= 6. Compose 页面 =============

@Composable
fun PagingDemoScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Paging 3") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("无限滚动") }
            )
        }

        when (selectedTab) {
            0 -> PagingListScreen()
            1 -> PagingWithIndexScreen()
        }
    }
}

/**
 * Paging 核心要点:
 * 
 * 1. PagingSource: 数据源，负责分页加载
 *    - getRefreshKey(): 刷新时的 key
 *    - load(): 加载数据
 * 
 * 2. Pager: 配置分页参数
 *    - pageSize: 每页数量
 *    - enablePlaceholders: 占位符
 *    - initialLoadSize: 初始加载数量
 * 
 * 3. PagingData: 分页数据流
 *    - collectAsLazyPagingItems(): 收集为 LazyPagingItems
 * 
 * 4. LazyPagingItems:
 *    - itemCount: 总数
 *    - loadState: 加载状态
 *    - refresh(): 刷新
 *    - retry(): 重试
 * 
 * 5. LoadState:
 *    - Loading: 加载中
 *    - NotLoading: 未加载
 *    - Error: 错误
 */
