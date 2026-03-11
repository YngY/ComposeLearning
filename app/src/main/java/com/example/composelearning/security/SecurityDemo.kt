package com.example.composelearning.security

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.MessageDigest
import java.util.Base64

/**
 * Security 示例
 * 包含: 加密存储、密钥管理、签名验证
 */

// ============= 1. 加密存储 =============
class SecureStorage(private val context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    // 保存加密数据
    fun putString(key: String, value: String) {
        encryptedPrefs.edit().putString(key, value).apply()
    }

    // 读取加密数据
    fun getString(key: String): String? {
        return encryptedPrefs.getString(key, null)
    }

    // 删除
    fun remove(key: String) {
        encryptedPrefs.edit().remove(key).apply()
    }

    // 清空
    fun clear() {
        encryptedPrefs.edit().clear().apply()
    }
}

// ============= 2. 哈希工具 =============
object HashUtils {
    // SHA-256 哈希
    fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }

    // MD5 哈希 (不推荐用于安全场景)
    @Suppress("DEPRECATION")
    fun md5(input: String): String {
        val digest = MessageDigest.getInstance("MD5")
        val hashBytes = digest.digest(input.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    // SHA-1 哈希
    @Suppress("DEPRECATION")
    fun sha1(input: String): String {
        val digest = MessageDigest.getInstance("SHA-1")
        val hashBytes = digest.digest(input.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}

// ============= 3. 密钥生成 =============
class KeyGenerator {
    fun generateKey(): KeyGenParameterSpec {
        return KeyGenParameterSpec.Builder(
            "my_key_alias",
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()
    }
}

// ============= 4. Compose 页面 =============

@Composable
fun SecurityDemoScreen() {
    val context = LocalContext.current
    val secureStorage = remember { SecureStorage(context) }

    var inputText by remember { mutableStateOf("") }
    var storedValue by remember { mutableStateOf("") }
    var hashResult by remember { mutableStateOf("") }
    var selectedHash by remember { mutableStateOf("SHA-256") }

    // 加载存储的值
    LaunchedEffect(Unit) {
        storedValue = secureStorage.getString("test_key") ?: ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Security 安全示例", style = MaterialTheme.typography.headlineMedium)

        // 加密存储
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("1. 加密存储 (EncryptedSharedPreferences)",
                    style = MaterialTheme.typography.titleMedium)

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    label = { Text("输入要保存的值") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            secureStorage.putString("test_key", inputText)
                            storedValue = inputText
                            inputText = ""
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("保存")
                    }

                    OutlinedButton(
                        onClick = {
                            secureStorage.remove("test_key")
                            storedValue = ""
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("删除")
                    }
                }

                if (storedValue.isNotEmpty()) {
                    Text(
                        "已保存的值: $storedValue",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // 哈希计算
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("2. 哈希计算", style = MaterialTheme.typography.titleMedium)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("SHA-256", "MD5", "SHA-1").forEach { hash ->
                        FilterChip(
                            selected = selectedHash == hash,
                            onClick = { selectedHash = hash },
                            label = { Text(hash) }
                        )
                    }
                }

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    label = { Text("输入文本") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        hashResult = when (selectedHash) {
                            "SHA-256" -> HashUtils.sha256(inputText)
                            "MD5" -> HashUtils.md5(inputText)
                            "SHA-1" -> HashUtils.sha1(inputText)
                            else -> ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("计算哈希")
                }

                if (hashResult.isNotEmpty()) {
                    Text(
                        "$selectedHash 结果:",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        hashResult,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // 安全要点
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("安全最佳实践:", style = MaterialTheme.typography.titleSmall)
                Text("• 使用 EncryptedSharedPreferences 存储敏感数据", style = MaterialTheme.typography.bodySmall)
                Text("• 密码存储使用强哈希 (SHA-256+)", style = MaterialTheme.typography.bodySmall)
                Text("• 使用 Android Keystore 管理密钥", style = MaterialTheme.typography.bodySmall)
                Text("• 避免在日志中打印敏感信息", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

/**
 * Security 核心要点:
 * 
 * 1. EncryptedSharedPreferences: 加密的 SharedPreferences
 * 2. MasterKey: 主密钥构建器
 * 3. KeyGenParameterSpec: 密钥生成参数
 * 4. KeyProperties: 密钥属性配置
 * 
 * Gradle 依赖:
 * implementation "androidx.security:security-crypto:1.1.0-alpha06"
 */
