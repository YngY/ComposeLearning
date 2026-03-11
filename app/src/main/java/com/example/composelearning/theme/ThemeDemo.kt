package com.example.composelearning.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp

/**
 * 多主题支持示例
 * 包含: 深色/浅色主题、自定义颜色、动态颜色、主题切换
 */

// ============= 1. 颜色定义 =============
object AppColors {
    // 主色调
    val Primary = Color(0xFF6200EE)
    val PrimaryVariant = Color(0xFF3700B3)
    val Secondary = Color(0xFF03DAC6)
    val SecondaryVariant = Color(0xFF018786)
    
    // 浅色主题
    val LightBackground = Color(0xFFFFFBFE)
    val LightSurface = Color(0xFFFFFBFE)
    val LightOnPrimary = Color.White
    val LightOnSecondary = Color.Black
    val LightOnBackground = Color(0xFF1C1B1F)
    val LightOnSurface = Color(0xFF1C1B1F)
    
    // 深色主题
    val DarkBackground = Color(0xFF1C1B1F)
    val DarkSurface = Color(0xFF1C1B1F)
    val DarkOnPrimary = Color.White
    val DarkOnSecondary = Color.White
    val DarkOnBackground = Color(0xFFE6E1E5)
    val DarkOnSurface = Color(0xFFE6E1E5)
    
    // 自定义颜色
    val Purple80 = Color(0xFFD0BCFF)
    val PurpleGrey80 = Color(0xFFCCC2DC)
    val Pink80 = Color(0xFFEFB8C8)
    
    val Purple40 = Color(0xFF6650a4)
    val PurpleGrey40 = Color(0xFF625b71)
    val Pink40 = Color(0xFF7D5260)
    
    // 品牌色
    val BrandBlue = Color(0xFF2196F3)
    val BrandGreen = Color(0xFF4CAF50)
    val BrandOrange = Color(0xFFFF9800)
    val BrandRed = Color(0xFFF44336)
}

// ============= 2. 主题配置 =============
private val DarkColorScheme = darkColorScheme(
    primary = AppColors.Purple80,
    secondary = AppColors.PurpleGrey80,
    tertiary = AppColors.Pink80,
    background = AppColors.DarkBackground,
    surface = AppColors.DarkSurface,
    onPrimary = AppColors.DarkOnPrimary,
    onSecondary = AppColors.DarkOnSecondary,
    onBackground = AppColors.DarkOnBackground,
    onSurface = AppColors.DarkOnSurface
)

private val LightColorScheme = lightColorScheme(
    primary = AppColors.Purple40,
    secondary = AppColors.PurpleGrey40,
    tertiary = AppColors.Pink40,
    background = AppColors.LightBackground,
    surface = AppColors.LightSurface,
    onPrimary = AppColors.LightOnPrimary,
    onSecondary = AppColors.LightOnSecondary,
    onBackground = AppColors.LightOnBackground,
    onSurface = AppColors.LightOnSurface
)

// 品牌主题
private val BrandBlueDark = darkColorScheme(
    primary = AppColors.BrandBlue,
    secondary = AppColors.BrandGreen,
    tertiary = AppColors.BrandOrange,
    background = Color(0xFF0D1B2A),
    surface = Color(0xFF1B263B),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFFE0E1DD),
    onSurface = Color(0xFFE0E1DD)
)

private val BrandBlueLight = lightColorScheme(
    primary = AppColors.BrandBlue,
    secondary = AppColors.BrandGreen,
    tertiary = AppColors.BrandOrange,
    background = Color(0xFFF0F4F8),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF1B263B),
    onSurface = Color(0xFF1B263B)
)

// ============= 3. 主题模式 =============
enum class ThemeMode {
    LIGHT,      // 浅色
    DARK,       // 深色
    SYSTEM,     // 跟随系统
    BRAND_BLUE, // 品牌蓝
    BRAND_GREEN,// 品牌绿
    BRAND_ORANGE,// 品牌橙
    CUSTOM      // 自定义
}

// ============= 4. 主题状态管理 =============
class ThemeManager {
    private val _themeMode = mutableStateOf(ThemeMode.SYSTEM)
    val themeMode: ThemeMode get() = _themeMode

    private val _isDarkMode = mutableStateOf(false)
    val isDarkMode: Boolean get() = _isDarkMode

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        _isDarkMode.value = when (mode) {
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
            ThemeMode.SYSTEM -> isSystemInDarkTheme()
            else -> false
        }
    }

    fun getColorScheme(): ColorScheme {
        val isDark = when (_themeMode.value) {
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
            ThemeMode.SYSTEM -> isSystemInDarkTheme()
            else -> false
        }

        return when (_themeMode.value) {
            ThemeMode.BRAND_BLUE -> if (isDark) BrandBlueDark else BrandBlueLight
            ThemeMode.BRAND_GREEN -> if (isDark) DarkColorScheme else LightColorScheme
            ThemeMode.BRAND_ORANGE -> if (isDark) DarkColorScheme else LightColorScheme
            ThemeMode.CUSTOM -> if (isDark) DarkColorScheme else LightColorScheme
            else -> if (isDark) DarkColorScheme else LightColorScheme
        }
    }
}

// ============= 5. 动态颜色 (Android 12+) =============
@Composable
fun DynamicColorScheme(darkTheme: Boolean): ColorScheme {
    val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val context = LocalContext.current

    return when {
        dynamicColor && darkTheme -> {
            androidx.compose.material3.dynamicDarkColorScheme(context)
        }
        dynamicColor -> {
            androidx.compose.material3.dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
}

// ============= 6. 主题切换Composable =============

/**
 * 主题演示主页面
 */
@Composable
fun ThemeDemoScreen() {
    var selectedTheme by remember { mutableStateOf(ThemeMode.SYSTEM) }
    var useDynamicColor by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "多主题演示",
            style = MaterialTheme.typography.headlineMedium
        )

        // 动态颜色开关
        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("使用动态颜色 (Android 12+)")
                Switch(
                    checked = useDynamicColor,
                    onCheckedChange = { useDynamicColor = it }
                )
            }
        }

        // 主题选择
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "选择主题",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

                ThemeMode.entries.forEach { mode ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedTheme == mode,
                            onClick = { selectedTheme = mode }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(getThemeDisplayName(mode))
                    }
                }
            }
        }

        // 预览
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            ThemePreviewContent(selectedTheme, useDynamicColor)
        }
    }
}

@Composable
private fun ThemePreviewContent(themeMode: ThemeMode, useDynamicColor: Boolean) {
    val isDark = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        else -> false
    }

    val colorScheme = when {
        useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            DynamicColorScheme(isDark)
        }
        else -> when (themeMode) {
            ThemeMode.BRAND_BLUE -> if (isDark) BrandBlueDark else BrandBlueLight
            else -> if (isDark) DarkColorScheme else LightColorScheme
        }
    }

    // 应用主题
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "主题预览",
            style = MaterialTheme.typography.titleMedium,
            color = colorScheme.onSurface
        )

        // 颜色展示
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ColorBox("Primary", colorScheme.primary, Modifier.weight(1f))
            ColorBox("Secondary", colorScheme.secondary, Modifier.weight(1f))
            ColorBox("Tertiary", colorScheme.tertiary, Modifier.weight(1f))
        }

        // 按钮示例
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary
                )
            ) { Text("主要按钮") }
            
            OutlinedButton(
                onClick = { },
                modifier = Modifier.weight(1f)
            ) { Text("轮廓按钮") }
        }

        // 输入框示例
        OutlinedTextField(
            value = "",
            onValueChange = { },
            label = { Text("输入框", color = colorScheme.onSurfaceVariant) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorScheme.primary,
                unfocusedBorderColor = colorScheme.outline
            )
        )

        // 卡片示例
        Card(
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surfaceVariant
            )
        ) {
            Text(
                "卡片内容",
                modifier = Modifier.padding(16.dp),
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ColorBox(name: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(60.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                name,
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
        }
    }
}

private fun getThemeDisplayName(mode: ThemeMode): String {
    return when (mode) {
        ThemeMode.LIGHT -> "浅色主题"
        ThemeMode.DARK -> "深色主题"
        ThemeMode.SYSTEM -> "跟随系统"
        ThemeMode.BRAND_BLUE -> "品牌蓝"
        ThemeMode.BRAND_GREEN -> "品牌绿"
        ThemeMode.BRAND_ORANGE -> "品牌橙"
        ThemeMode.CUSTOM -> "自定义"
    }
}

// ============= 7. 全局主题应用示例 =============

/**
 * 应用入口主题
 */
@Composable
fun AppTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        else -> false
    }

    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            DynamicColorScheme(darkTheme)
        }
        else -> when (themeMode) {
            ThemeMode.BRAND_BLUE -> if (darkTheme) BrandBlueDark else BrandBlueLight
            else -> if (darkTheme) DarkColorScheme else LightColorScheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

/**
 * 使用示例
 */
/*
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 从 DataStore 读取保存的主题
        val dataStore = SettingsDataStore(this)
        
        setContent {
            var themeMode by remember { mutableStateOf(ThemeMode.SYSTEM) }
            
            AppTheme(themeMode = themeMode) {
                // 你的 UI
                ThemeDemoScreen()
            }
        }
    }
}
*/
