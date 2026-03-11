package com.example.composelearning.datastore

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * DataStore 示例
 * 包含: Preferences DataStore, Proto DataStore
 */

// ============= 1. 扩展属性 =============
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

// ============= 2. 定义 Keys =============
object PreferencesKeys {
    val USER_NAME = stringPreferencesKey("user_name")
    val USER_EMAIL = stringPreferencesKey("user_email")
    val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    val NOTIFICATION_ENABLED = booleanPreferencesKey("notification_enabled")
    val SELECTED_LANGUAGE = stringPreferencesKey("selected_language")
    val FONT_SIZE = intPreferencesKey("font_size")
}

// ============= 3. DataStore Manager =============
class SettingsDataStore(private val context: Context) {
    
    // 读取数据
    val userName = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USER_NAME] ?: ""
    }

    val isDarkMode = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.IS_DARK_MODE] ?: false
    }

    val notificationEnabled = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.NOTIFICATION_ENABLED] ?: true
    }

    val fontSize = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.FONT_SIZE] ?: 14
    }

    // 保存数据
    suspend fun saveUserName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = name
        }
    }

    suspend fun saveDarkMode(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_DARK_MODE] = isDark
        }
    }

    suspend fun saveNotificationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATION_ENABLED] = enabled
        }
    }

    suspend fun saveFontSize(size: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FONT_SIZE] = size
        }
    }

    // 清除所有数据
    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

// ============= 4. 完整设置管理器 =============
class SettingsManager(context: Context) {
    private val dataStore = context.dataStore

    // 保存用户设置
    suspend fun saveUserSettings(
        name: String,
        email: String,
        isDarkMode: Boolean,
        notificationEnabled: Boolean,
        language: String,
        fontSize: Int
    ) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = name
            preferences[PreferencesKeys.USER_EMAIL] = email
            preferences[PreferencesKeys.IS_DARK_MODE] = isDarkMode
            preferences[PreferencesKeys.NOTIFICATION_ENABLED] = notificationEnabled
            preferences[PreferencesKeys.SELECTED_LANGUAGE] = language
            preferences[PreferencesKeys.FONT_SIZE] = fontSize
        }
    }

    // 读取所有设置
    suspend fun getAllSettings(): UserSettings {
        val preferences = dataStore.data.first()
        return UserSettings(
            userName = preferences[PreferencesKeys.USER_NAME] ?: "",
            userEmail = preferences[PreferencesKeys.USER_EMAIL] ?: "",
            isDarkMode = preferences[PreferencesKeys.IS_DARK_MODE] ?: false,
            notificationEnabled = preferences[PreferencesKeys.NOTIFICATION_ENABLED] ?: true,
            selectedLanguage = preferences[PreferencesKeys.SELECTED_LANGUAGE] ?: "zh-CN",
            fontSize = preferences[PreferencesKeys.FONT_SIZE] ?: 14
        )
    }
}

data class UserSettings(
    val userName: String,
    val userEmail: String,
    val isDarkMode: Boolean,
    val notificationEnabled: Boolean,
    val selectedLanguage: String,
    val fontSize: Int
)

// ============= 5. Compose 页面 =============

@Composable
fun DataStoreDemoScreen() {
    val context = LocalContext.current
    val dataStore = remember { SettingsDataStore(context) }

    var userName by remember { mutableStateOf("") }
    var isDarkMode by remember { mutableStateOf(false) }
    var notificationEnabled by remember { mutableStateOf(true) }
    var fontSize by remember { mutableIntStateOf(14) }
    var savedMessage by remember { mutableStateOf<String?>(null) }

    // 加载初始值
    LaunchedEffect(Unit) {
        dataStore.userName.collect { userName = it }
    }
    LaunchedEffect(Unit) {
        dataStore.isDarkMode.collect { isDarkMode = it }
    }
    LaunchedEffect(Unit) {
        dataStore.notificationEnabled.collect { notificationEnabled = it }
    }
    LaunchedEffect(Unit) {
        dataStore.fontSize.collect { fontSize = it }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("DataStore 设置", style = MaterialTheme.typography.headlineMedium)

        // 用户名
        OutlinedTextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text("用户名") },
            modifier = Modifier.fillMaxWidth()
        )

        // 深色模式
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("深色模式")
            Switch(
                checked = isDarkMode,
                onCheckedChange = { isDarkMode = it }
            )
        }

        // 通知
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("启用通知")
            Switch(
                checked = notificationEnabled,
                onCheckedChange = { notificationEnabled = it }
            )
        }

        // 字体大小
        Column {
            Text("字体大小: $fontSize")
            Slider(
                value = fontSize.toFloat(),
                onValueChange = { fontSize = it.toInt() },
                valueRange = 12f..24f,
                steps = 5
            )
        }

        // 保存按钮
        Button(
            onClick = {
                kotlinx.coroutines.MainScope().launch {
                    dataStore.saveUserName(userName)
                    dataStore.saveDarkMode(isDarkMode)
                    dataStore.saveNotificationEnabled(notificationEnabled)
                    dataStore.saveFontSize(fontSize)
                    savedMessage = "设置已保存!"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("保存设置")
        }

        // 清除按钮
        OutlinedButton(
            onClick = {
                kotlinx.coroutines.MainScope().launch {
                    dataStore.clearAll()
                    userName = ""
                    isDarkMode = false
                    notificationEnabled = true
                    fontSize = 14
                    savedMessage = "已清除所有数据"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("清除所有数据")
        }

        savedMessage?.let {
            Text(it, color = MaterialTheme.typography.bodySmall)
        }
    }
}

private fun kotlinx.coroutines.MainScope() = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main)
