@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)

package com.wilinz.devtools

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowInsetsControllerCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.wilinz.accessbilityx.app.launchAppPackage
import com.wilinz.devtools.service.AutoAccessibilityService
import com.wilinz.devtools.service.FloatingWindowService
import com.wilinz.devtools.ui.theme.DevtoolsTheme
import com.wilinz.devtools.util.PermissionsSettingUtil
import com.wilinz.devtools.util.toast
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    val auto get() = AutoAccessibilityService.instance

    val ipv4Regex = Regex("""^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}:\d+$""")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.Transparent.toArgb()
        window.navigationBarColor = Color.Transparent.toArgb()
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        setContent {
            DevtoolsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = {
                            SmallTopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) })
                        }
                    ) {
                        Column(
                            Modifier
                                .padding(it)
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {


                            ElevatedButton(onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(
                                        this@MainActivity
                                    )
                                ) {
                                    startService(
                                        Intent(
                                            this@MainActivity,
                                            FloatingWindowService::class.java
                                        )
                                    )
                                } else {
                                    val intent =
                                        PermissionsSettingUtil.getAppPermissionsSettingIntent()
                                    startActivity(intent)
                                }
                            }) {
                                Text(text = "打开悬浮窗")
                            }

                            val scope = rememberCoroutineScope()
                            val context = LocalContext.current

                            val notificationPermission =
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS) { ok ->
                                        if (ok) {
                                            toast("已授权")
                                        }
                                    }
                                } else {
                                    null
                                }

                            ElevatedButton(onClick = {
                                notificationPermission?.launchPermissionRequest() ?: run {
                                    toast("已授权")
                                }
                            }) {
                                Text(text = "打开通知权限")
                            }

                            ElevatedButton(onClick = {
                                launchAppPackage("com.tencent.mm")

                            }) {
                                Text(text = "打开微信")
                            }

                        }
                    }

                }
            }
        }
    }

}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DevtoolsTheme {
        Greeting("Android")
    }
}

const val TAG = "MainActivity.kt"