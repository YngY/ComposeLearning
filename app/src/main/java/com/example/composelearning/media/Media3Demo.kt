package com.example.composelearning.media

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

/**
 * Media3 示例
 * 包含: 视频播放、音频播放、MediaSession
 */

// ============= 1. 播放器管理器 =============
class MediaPlayerManager(private val context: Context) {
    private var exoPlayer: ExoPlayer? = null

    // 初始化播放器
    fun getPlayer(): ExoPlayer {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build().apply {
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        // 播放状态变化
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        // 播放/暂停变化
                    }
                })
            }
        }
        return exoPlayer!!
    }

    // 播放视频URL
    fun playVideo(url: String) {
        val player = getPlayer()
        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    // 播放音频URL
    fun playAudio(url: String) {
        playVideo(url) // ExoPlayer 同时支持音频和视频
    }

    // 播放本地文件
    fun playLocalFile(filePath: String) {
        val player = getPlayer()
        val mediaItem = MediaItem.fromUri("file://$filePath")
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    // 控制
    fun play() = exoPlayer?.play()
    fun pause() = exoPlayer?.pause()
    fun stop() = exoPlayer?.stop()
    fun seekTo(position: Long) = exoPlayer?.seekTo(position)
    fun release() = exoPlayer?.release()

    // 获取当前位置
    fun getCurrentPosition(): Long = exoPlayer?.currentPosition ?: 0
    fun getDuration(): Long = exoPlayer?.duration ?: 0
    fun isPlaying(): Boolean = exoPlayer?.isPlaying ?: false
}

// ============= 2. Compose 页面 =============

@Composable
fun Media3DemoScreen() {
    val context = LocalContext.current
    val playerManager = remember { MediaPlayerManager(context) }

    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableLongStateOf(0) }
    var duration by remember { mutableLongStateOf(0) }
    var videoUrl by remember { mutableStateOf("") }
    var showControls by remember { mutableStateOf(true) }

    // 收集播放状态
    kotlinx.coroutines.DisposableEffect(Unit) {
        val job = kotlinx.coroutines.MainScope().launch {
            while (true) {
                kotlinx.coroutines.delay(500)
                currentPosition = playerManager.getCurrentPosition()
                duration = playerManager.getDuration()
                isPlaying = playerManager.isPlaying()
            }
        }
        onDispose {
            job.cancel()
            playerManager.release()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Media3 媒体示例", style = MaterialTheme.typography.headlineMedium)

        // 视频/音频输入
        OutlinedTextField(
            value = videoUrl,
            onValueChange = { videoUrl = it },
            label = { Text("视频/音频 URL") },
            placeholder = { Text("输入 URL 或使用示例") },
            modifier = Modifier.fillMaxWidth()
        )

        // 示例URL按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val sampleVideos = listOf(
                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4" to "示例视频1",
                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4" to "示例视频2"
            )

            sampleVideos.forEach { (url, name) ->
                OutlinedButton(
                    onClick = { videoUrl = url },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(name, maxLines = 1)
                }
            }
        }

        // 播放控制
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    if (videoUrl.isNotEmpty()) {
                        playerManager.playVideo(videoUrl)
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("播放")
            }

            Button(
                onClick = { playerManager.pause() },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Pause, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("暂停")
            }

            Button(
                onClick = { playerManager.stop() },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Stop, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("停止")
            }
        }

        // 进度条
        if (duration > 0) {
            Column {
                Text(
                    "进度: ${formatTime(currentPosition)} / ${formatTime(duration)}",
                    style = MaterialTheme.typography.bodySmall
                )
                Slider(
                    value = currentPosition.toFloat(),
                    onValueChange = { playerManager.seekTo(it.toLong()) },
                    valueRange = 0f..duration.toFloat()
                )
            }
        }

        // 播放状态
        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("播放状态", style = MaterialTheme.typography.titleMedium)
                Text(
                    if (isPlaying) "▶ 播放中" else "⏸ 已暂停",
                    color = if (isPlaying) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 说明
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Media3 特点:", style = MaterialTheme.typography.titleSmall)
                Text("• 统一的媒体播放 API", style = MaterialTheme.typography.bodySmall)
                Text("• 支持视频、音频、流媒体", style = MaterialTheme.typography.bodySmall)
                Text("• 更好的性能和维护", style = MaterialTheme.typography.bodySmall)
                Text("• 支持 MediaSession 集成", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

private fun formatTime(millis: Long): String {
    val seconds = millis / 1000
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

private fun kotlinx.coroutines.MainScope() = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main)

/**
 * Media3 核心要点:
 * 
 * 1. ExoPlayer: Android 媒体播放器
 * 2. MediaItem: 媒体项（URL或本地文件）
 * 3. Player.Listener: 播放状态监听
 * 4. PlayerView: 视频播放视图
 * 5. MediaSession: 媒体会话集成
 * 
 * Gradle 依赖:
 * implementation "androidx.media3:media3-exoplayer:1.2.0"
 * implementation "androidx.media3:media3-ui:1.2.0"
 * implementation "androidx.media3:media3-session:1.2.0"
 */
