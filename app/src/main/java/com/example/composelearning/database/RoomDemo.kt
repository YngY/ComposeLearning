package com.example.composelearning.database

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.room.*

/**
 * Room 数据库示例
 * 包含: Entity, DAO, Database, Repository
 */

// ============= 1. Entity (实体) =============
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    val age: Int,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val priority: Int = 0, // 0: Low, 1: Medium, 2: High
    val createdAt: Long = System.currentTimeMillis()
)

// ============= 2. DAO (数据访问对象) =============
@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): kotlinx.coroutines.flow.Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Long): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()

    @Query("SELECT * FROM users WHERE name LIKE '%' || :query || '%'")
    fun searchUsers(query: String): kotlinx.coroutines.flow.Flow<List<UserEntity>>
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, priority DESC, createdAt DESC")
    fun getAllTasks(): kotlinx.coroutines.flow.Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY priority DESC")
    fun getPendingTasks(): kotlinx.coroutines.flow.Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("UPDATE tasks SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateTaskStatus(id: Long, isCompleted: Boolean)
}

// ============= 3. Database =============
@Database(
    entities = [UserEntity::class, TaskEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun taskDao(): TaskDao
}

// ============= 4. Repository =============
class UserRepository(private val userDao: UserDao) {
    val allUsers = userDao.getAllUsers()
    
    fun searchUsers(query: String) = userDao.searchUsers(query)

    suspend fun insert(user: UserEntity): Long = userDao.insertUser(user)

    suspend fun update(user: UserEntity) = userDao.updateUser(user)

    suspend fun delete(user: UserEntity) = userDao.deleteUser(user)
}

class TaskRepository(private val taskDao: TaskDao) {
    val allTasks = taskDao.getAllTasks()
    val pendingTasks = taskDao.getPendingTasks()

    suspend fun insert(task: TaskEntity): Long = taskDao.insertTask(task)

    suspend fun update(task: TaskEntity) = taskDao.updateTask(task)

    suspend fun delete(task: TaskEntity) = taskDao.deleteTask(task)

    suspend fun toggleComplete(id: Long, isCompleted: Boolean) = 
        taskDao.updateTaskStatus(id, isCompleted)
}

// ============= 5. Database 实例 =============
/*
// 在 Application 或 ViewModel 中创建
val database = Room.databaseBuilder(
    applicationContext,
    AppDatabase::class.java,
    "app_database"
).build()

val userRepository = UserRepository(database.userDao())
val taskRepository = TaskRepository(database.taskDao())
*/

// ============= 6. Compose 页面 =============

@Composable
fun RoomDemoScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("用户管理") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("任务管理") }
            )
        }

        when (selectedTab) {
            0 -> UserManagementScreen()
            1 -> TaskManagementScreen()
        }
    }
}

@Composable
private fun UserManagementScreen() {
    var showDialog by remember { mutableStateOf(false) }
    var editingUser by remember { mutableStateOf<UserEntity?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        // 伪代码：实际需要 ViewModel + Flow
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("用户管理", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Text("使用 Room Database", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("• UserEntity - 用户实体", style = MaterialTheme.typography.bodySmall)
                Text("• UserDao - 数据访问", style = MaterialTheme.typography.bodySmall)
                Text("• AppDatabase - 数据库", style = MaterialTheme.typography.bodySmall)
            }
        }

        Button(
            onClick = { showDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("添加用户")
        }
    }
}

@Composable
private fun TaskManagementScreen() {
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("任务管理", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Text("使用 Room + Flow", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("• TaskEntity - 任务实体", style = MaterialTheme.typography.bodySmall)
                Text("• Flow 响应式查询", style = MaterialTheme.typography.bodySmall)
                Text("• CRUD 操作", style = MaterialTheme.typography.bodySmall)
            }
        }

        Button(
            onClick = { showDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("添加任务")
        }
    }
}

/**
 * Room 核心要点:
 * 
 * 1. Entity: 对应数据库表
 *    - @Entity(tableName = "table_name")
 *    - @PrimaryKey, @ColumnInfo 等注解
 * 
 * 2. DAO: 数据访问接口
 *    - @Dao 注解
 *    - @Query, @Insert, @Update, @Delete
 *    - 返回 Flow 实现响应式查询
 * 
 * 3. Database: 数据库
 *    - @Database 注解
 *    - 继承 RoomDatabase
 *    - 提供 Dao 实例
 * 
 * 4. 使用协程
 *    - 所有数据库操作都是 suspend 函数
 *    - Flow 用于响应式数据更新
 */
