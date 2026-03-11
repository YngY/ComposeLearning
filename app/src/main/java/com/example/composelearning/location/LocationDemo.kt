package com.example.composelearning.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Location Services 示例
 * 包含: 获取位置、位置更新、地址查询
 */

// ============= 1. 位置服务管理器 =============
class LocationManager(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        10000L // 10秒更新一次
    ).apply {
        setMinUpdateIntervalMillis(5000L) // 最小5秒
        setWaitForAccurateLocation(false)
    }.build()

    // 获取最后已知位置
    @SuppressLint("MissingPermission")
    fun getLastLocation(onResult: (Location?) -> Unit) {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                onResult(location)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    // 位置更新Flow
    @SuppressLint("MissingPermission")
    fun locationUpdates(): Flow<Location> = callbackFlow {
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    trySend(location)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            callback,
            Looper.getMainLooper()
        )

        awaitClose {
            fusedLocationClient.removeLocationUpdates(callback)
        }
    }

    // 一次性位置
    @SuppressLint("MissingPermission")
    fun getCurrentLocation(onResult: (Location?) -> Unit) {
        val currentLocationRequest = CurrentLocationRequest.Builder()
            .setMaxUpdateAgeMillis(5000L)
            .setDurationMillis(10000L)
            .build()

        fusedLocationClient.getCurrentLocation(currentLocationRequest, null)
            .addOnSuccessListener { location ->
                onResult(location)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }
}

// ============= 2. 地址查询 =============
class GeocoderHelper(private val context: Context) {
    @SuppressLint("MissingConstant")
    fun getAddressFromLocation(
        latitude: Double,
        longitude: Double,
        onResult: (String?) -> Unit
    ) {
        try {
            val geocoder = android.location.Geocoder(context)
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val result = buildString {
                    address.getAddressLine(0)?.let { append(it) }
                }
                onResult(result)
            } else {
                onResult(null)
            }
        } catch (e: Exception) {
            onResult(null)
        }
    }
}

// ============= 3. Compose 页面 =============

@Composable
fun LocationDemoScreen() {
    val context = LocalContext.current
    val locationManager = remember { LocationManager(context) }
    val geocoder = remember { GeocoderHelper(context) }

    var currentLocation by remember { mutableStateOf<Location?>(null) }
    var address by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isUpdating by remember { mutableStateOf(false) }
    var locationHistory by remember { mutableStateOf(listOf<Location>()) }

    // 收集位置更新
    kotlinx.coroutines.DisposableEffect(isUpdating) {
        if (isUpdating) {
            val job = kotlinx.coroutines.MainScope().launch {
                locationManager.locationUpdates().collect { location ->
                    currentLocation = location
                    locationHistory = (listOf(location) + locationHistory).take(5)

                    // 查询地址
                    geocoder.getAddressFromLocation(
                        location.latitude,
                        location.longitude
                    ) { addr ->
                        address = addr
                    }
                }
            }
            onDispose {
                job.cancel()
            }
        } else {
            onDispose { }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Location 位置服务示例", style = MaterialTheme.typography.headlineMedium)

        // 获取位置按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    isLoading = true
                    locationManager.getLastLocation { location ->
                        currentLocation = location
                        isLoading = false
                        location?.let {
                            locationHistory = (listOf(it) + locationHistory).take(5)
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = !isLoading
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("获取位置")
            }

            Button(
                onClick = {
                    isUpdating = !isUpdating
                },
                modifier = Modifier.weight(1f),
                colors = if (isUpdating) {
                    ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                } else {
                    ButtonDefaults.buttonColors()
                }
            ) {
                Text(if (isUpdating) "停止更新" else "开始更新")
            }
        }

        // 加载状态
        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        // 当前位置
        currentLocation?.let { location ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("当前坐标", style = MaterialTheme.typography.titleMedium)
                    Text("纬度: ${location.latitude}", style = MaterialTheme.typography.bodyMedium)
                    Text("经度: ${location.longitude}", style = MaterialTheme.typography.bodyMedium)
                    Text("精度: ${location.accuracy}m", style = MaterialTheme.typography.bodySmall)
                    Text("海拔: ${if (location.hasAltitude()) location.altitude else "未知"}m",
                        style = MaterialTheme.typography.bodySmall)
                    Text("速度: ${if (location.hasSpeed()) location.speed else "未知"} m/s",
                        style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        // 地址
        address?.let { addr ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("地址", style = MaterialTheme.typography.titleMedium)
                    Text(addr, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // 位置历史
        if (locationHistory.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("位置历史 (最近${locationHistory.size}条)",
                        style = MaterialTheme.typography.titleMedium)
                    locationHistory.forEachIndexed { index, location ->
                        Text(
                            "${index + 1}. ${location.latitude}, ${location.longitude}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        // 说明
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("权限说明:", style = MaterialTheme.typography.titleSmall)
                Text("需要 ACCESS_FINE_LOCATION 和 ACCESS_COARSE_LOCATION 权限",
                    style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

/**
 * Location Services 核心要点:
 * 
 * 1. FusedLocationProviderClient: 融合位置提供者
 * 2. LocationRequest: 位置更新请求配置
 * 3. LocationCallback: 位置更新回调
 * 4. CurrentLocationRequest: 获取当前位置
 * 5. Geocoder: 经纬度转地址
 * 
 * 权限:
 * - ACCESS_FINE_LOCATION: 精确位置
 * - ACCESS_COARSE_LOCATION: 粗略位置
 * - ACCESS_BACKGROUND_LOCATION: 后台位置 (Android 10+)
 */

// 权限处理
/*
@Composable
private fun LocationPermissionHandler() {
    val locationPermissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    when {
        locationPermissionState.allPermissionsGranted -> {
            LocationDemoScreen()
        }
        else -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("需要位置权限")
                Button(onClick = { locationPermissionState.launchMultiplePermissionRequest() }) {
                    Text("请求权限")
                }
            }
        }
    }
}
*/
