@file:OptIn(ExperimentalComposeUiApi::class)

package com.wilinz.devtools.service

import android.content.Context
import android.graphics.PixelFormat
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.WindowManager
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.wilinz.devtools.ui.theme.DevtoolsTheme
import com.wilinz.devtools.util.toast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.absoluteValue

class DraggableFloatingView(private val context: Context) {
    private val windowManager: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private lateinit var layoutParams: WindowManager.LayoutParams
    private lateinit var floatingView: ComposeView

    // 用于跟踪触摸事件的变量
    private var initialX: Int = 0
    private var initialY: Int = 0
    private var initialTouchX: Float = 0f
    private var initialTouchY: Float = 0f

    var onClickCallback : (() -> Unit)? = null

    private fun setLifecycleOwner(lifecycleOwner: MySavedStateRegistryOwner,viewModelStoreOwner: ViewModelStoreOwner){
//        val viewModelStore = ViewModelStore()
        lifecycleOwner.performRestore(null)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        floatingView.setViewTreeLifecycleOwner(lifecycleOwner)
//        ViewTreeLifecycleOwner.set(floatingView, lifecycleOwner)
//        ViewTreeViewModelStoreOwner.set(floatingView) { viewModelStore }
        floatingView.setViewTreeViewModelStoreOwner(viewModelStoreOwner)
        floatingView.setViewTreeSavedStateRegistryOwner(lifecycleOwner)
    }

    fun create(lifecycleOwner: MySavedStateRegistryOwner,viewModelStoreOwner: ViewModelStoreOwner) {
        layoutParams = WindowManager.LayoutParams().apply {
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            format = PixelFormat.TRANSLUCENT
            gravity = Gravity.CENTER or Gravity.CENTER
        }

        floatingView = ComposeView(context)

        setContent()

        setLifecycleOwner(lifecycleOwner,viewModelStoreOwner)

        windowManager.addView(floatingView, layoutParams)
    }

    @Composable
    fun CircularIconButton(
        modifier: Modifier = Modifier,
        icon: ImageVector,
        iconDescription: String? = null,
        iconColor: Color = Color.White,
        backgroundColor: Color = Color.Gray,
        interactionSource: MutableInteractionSource // 添加这个参数
    ) {
        // 使用 rememberRipple 创建水波纹效果
        val ripple = rememberRipple(bounded = true, color = MaterialTheme.colorScheme.onSurface)

        Surface(
            modifier = modifier
                .size(32.dp)
                .clip(CircleShape) // 确保 Surface 本身是圆形的
                .indication(interactionSource, ripple), // 使用 indication 和 interactionSource
            color = backgroundColor,
            shape = CircleShape
        ) {
            IconButton(onClick = { /* 这里不处理点击事件 */ }) {
                Icon(
                    imageVector = icon,
                    contentDescription = iconDescription,
                    tint = iconColor
                )
            }
        }
    }

    fun setContent() {

        val threshold = ViewConfiguration.get(context).scaledTouchSlop // Threshold for considering a touch as a drag

        floatingView.setContent {
            var isDrag by remember {
                mutableStateOf(false)
            }  // Add this variable to track if the view was dragged
            var isClicked by remember {
                mutableStateOf(false)
            }
            val interactionSource = remember { MutableInteractionSource() }
            val coroutineScope = rememberCoroutineScope()
            DevtoolsTheme {
                Box(
                    modifier = Modifier
                        .wrapContentSize()
                        .pointerInteropFilter { motionEvent ->
                            when (motionEvent.action) {
                                MotionEvent.ACTION_DOWN -> {
                                    isDrag = false // Reset the drag state on ACTION_DOWN
                                    initialX = layoutParams.x
                                    initialY = layoutParams.y
                                    initialTouchX = motionEvent.rawX
                                    initialTouchY = motionEvent.rawY
                                    true // Handle the event
                                }

                                MotionEvent.ACTION_MOVE -> {
                                    val deltaX = (motionEvent.rawX - initialTouchX).toInt()
                                    val deltaY = (motionEvent.rawY - initialTouchY).toInt()
                                    if (abs(deltaX) > threshold || abs(deltaY) > threshold) {
                                        isDrag =
                                            true // Set isDrag to true as the view is being dragged
                                        layoutParams.x = initialX + deltaX
                                        layoutParams.y = initialY + deltaY
                                        windowManager.updateViewLayout(floatingView, layoutParams)
                                    }
                                    true // Handle the event
                                }

                                MotionEvent.ACTION_UP -> {
                                    if (!isDrag) {
                                        isClicked = true
                                        coroutineScope.launch {
                                            val position = Offset(motionEvent.x, motionEvent.y)
                                            val press = PressInteraction.Press(position)
                                            interactionSource.emit(press)

                                            // 延迟一段时间后，发出 Release 交互以完成水波纹效果
                                            delay(100) // 水波纹的持续时间
                                            interactionSource.emit(PressInteraction.Release(press))
                                        }
                                    }
                                    true // Do not handle the event
                                }

                                else -> false // Do not handle the event
                            }
                        }
                ) {
                    val context = LocalContext.current
                    LaunchedEffect(key1 = isClicked) {
                        if (isClicked) {
                            isClicked = false
                            onClickCallback?.invoke()
                        }
                    }
                    CircularIconButton(
                        icon = Icons.Default.PlayArrow,
                        backgroundColor = Color(0x10000000),
                        interactionSource = interactionSource // 传递 interactionSource
                    )
                }
            }
        }
    }



    fun remove() {
        windowManager.removeView(floatingView)
    }


}
