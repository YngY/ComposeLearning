package com.example.composelearning.camera

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * CameraX 示例
 * 包含: 相机预览、拍照、切换前后摄像头
 */

// ============= 1. 相机管理器 =============
class CameraManager(private val context: Context) {
    private var imageCapture: ImageCapture? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null
    private var preview: Preview? = null

    // 获取相机提供者
    suspend fun getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(context).also { future ->
            future.addListener({
                continuation.resume(future.get())
            }, ContextCompat.getMainExecutor(context))
        }
    }

    // 绑定相机用例
    suspend fun bindCamera(
        lifecycleOwner: androidx.lifecycle.LifecycleOwner,
        previewView: PreviewView,
        cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    ) {
        cameraProvider = getCameraProvider()

        // 预览用例
        preview = Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

        // 拍照用例
        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()

        try {
            cameraProvider?.unbindAll()
            camera = cameraProvider?.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (e: Exception) {
            Log.e("CameraManager", "Use case binding failed", e)
        }
    }

    // 拍照
    fun takePhoto(onResult: (String?) -> Unit) {
        val imageCapture = imageCapture ?: run {
            onResult(null)
            return
        }

        val photoFile = File(
            context.cacheDir,
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                .format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    onResult(photoFile.absolutePath)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraManager", "Photo capture failed: ${exception.message}", exception)
                    onResult(null)
                }
            }
        )
    }

    // 切换闪光灯
    fun toggleFlash() {
        camera?.let {
            val cameraInfo = it.cameraInfo
            if (cameraInfo.hasFlashUnit()) {
                val newState = cameraInfo.torchState.value != TorchState.ON
                it.cameraControl.enableTorch(newState)
            }
        }
    }

    // 释放资源
    fun release() {
        cameraProvider?.unbindAll()
    }
}

// ============= 2. Compose 页面 =============

@Composable
fun CameraXDemoScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isCameraReady by remember { mutableStateOf(false) }
    var isFrontCamera by remember { mutableStateOf(false) }
    var isFlashOn by remember { mutableStateOf(false) }
    var lastPhotoPath by remember { mutableStateOf<String?>(null) }
    var showPreview by remember { mutableStateOf(false) }

    val cameraManager = remember { CameraManager(context) }

    DisposableEffect(Unit) {
        onDispose {
            cameraManager.release()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("CameraX 相机示例", style = MaterialTheme.typography.headlineMedium)

        // 相机预览
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (showPreview) {
                AndroidView(
                    factory = { ctx ->
                        PreviewView(ctx).apply {
                            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    update = { previewView ->
                        val cameraSelector = if (isFrontCamera) {
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        } else {
                            CameraSelector.DEFAULT_BACK_CAMERA
                        }

                        kotlinx.coroutines.MainScope().launch {
                            cameraManager.bindCamera(lifecycleOwner, previewView, cameraSelector)
                            isCameraReady = true
                        }
                    }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("点击下方按钮开启相机")
                }
            }
        }

        // 拍照结果
        lastPhotoPath?.let { path ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("拍照成功!", style = MaterialTheme.typography.titleSmall)
                    Text("保存路径: $path", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        // 控制按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 开启相机
            Button(
                onClick = { showPreview = true },
                modifier = Modifier.weight(1f)
            ) {
                Text(if (showPreview) "相机已开启" else "开启相机")
            }

            // 拍照
            Button(
                onClick = {
                    cameraManager.takePhoto { path ->
                        lastPhotoPath = path
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = isCameraReady
            ) {
                Text("拍照")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 切换摄像头
            OutlinedButton(
                onClick = {
                    isFrontCamera = !isFrontCamera
                    isCameraReady = false
                },
                modifier = Modifier.weight(1f),
                enabled = showPreview
            ) {
                Text(if (isFrontCamera) "后置摄像头" else "前置摄像头")
            }

            // 闪光灯
            OutlinedButton(
                onClick = {
                    cameraManager.toggleFlash()
                    isFlashOn = !isFlashOn
                },
                modifier = Modifier.weight(1f),
                enabled = isCameraReady && !isFrontCamera
            ) {
                Text(if (isFlashOn) "闪光灯: 开" else "闪光灯: 关")
            }
        }
    }
}

private fun kotlinx.coroutines.MainScope() = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main)

/**
 * CameraX 核心要点:
 * 
 * 1. ProcessCameraProvider: 相机生命周期管理
 * 2. Preview: 预览用例
 * 3. ImageCapture: 拍照用例
 * 4. ImageAnalysis: 图像分析
 * 5. VideoCapture: 视频录制
 * 6. CameraSelector: 选择前后/广角等摄像头
 * 7. CameraControl: 控制闪光灯/变焦等
 */

// 权限处理示例
/*
@Composable
private fun CameraPermissionHandler() {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    when {
        cameraPermissionState.status.isGranted -> {
            CameraXDemoScreen()
        }
        else -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("需要相机权限")
                Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                    Text("请求权限")
                }
            }
        }
    }
}
*/
