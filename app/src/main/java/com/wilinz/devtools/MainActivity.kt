@file:OptIn(ExperimentalMaterial3Api::class)

package com.wilinz.devtools

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.wilinz.accessbilityx.device.screenHeight
import com.wilinz.accessbilityx.device.screenWidth
import com.wilinz.accessbilityx.ensureClick
import com.wilinz.accessbilityx.text1
import com.wilinz.devtools.ui.theme.DevtoolsTheme
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max

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
                                if (Build.VERSION.SDK_INT<Build.VERSION_CODES.R){
                                    Toast.makeText(
                                        this@MainActivity,
                                        "无线调试仅支持Android 11 及以上设备",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    return@ElevatedButton
                                }
                                MainScope().launch {
                                    if (auto == null) return@launch
                                    startActivity(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS))
                                    delay(1000)
                                    auto?.swipe(
                                        screenWidth / 2f, screenHeight - 500f,
                                        screenWidth / 2f, 100f,
                                        100,
                                        isZoom = false
                                    )

                                    auto?.untilFindOne {
                                        it.text1 == "无线调试"
                                    }?.ensureClick()

                                    val address = auto?.untilFindOne {
                                        it.text1?.matches(ipv4Regex) ?: false
                                    }?.text1

                                    val clipboard =
                                        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("ADB debug address", address)
                                    clipboard.setPrimaryClip(clip)

                                    Log.d(TAG, "address: $address")

                                    auto?.apply {
                                        back()
                                        delay(100)
                                        back()
                                    }

                                    delay(200)
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Copied $address",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()

                                }
                            }) {
                                Text(text = "复制无线调试地址")
                            }
                            ElevatedButton(onClick = {
                                MainScope().launch {
                                    if (auto == null) return@launch
                                    startActivity(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS))
                                    delay(1000)

                                    val job = launch {
                                        for (i in 0 until 30) {
                                            auto?.swipe(
                                                screenWidth / 2f, max(500f, screenHeight - 1000f),
                                                screenWidth / 2f, 100f,
                                                100,
                                                isZoom = false
                                            )
                                            delay(100)
                                        }
                                    }

                                    auto?.untilFindOne {
                                        it.text1 == "指针位置"
                                    }

                                    job.cancel()

                                    auto?.swipe(
                                        screenWidth / 2f, screenWidth * 1f,
                                        screenWidth / 2f, screenWidth + 100f,
                                        10,
                                        isZoom = false
                                    )

                                }
                            }) {
                                Text(text = "打开开发者选项指针位置")
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