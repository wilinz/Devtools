package androidx.lifecycle

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import androidx.annotation.CallSuper
import androidx.compose.ui.platform.ComposeView
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.wilinz.devtools.service.MySavedStateRegistryOwner

/**
 * A Service that implements SavedStateRegistryOwner.
 */
open class SavedStateLifecycleService : Service(), MySavedStateRegistryOwner {

    private val lifecycleDispatcher = ServiceLifecycleDispatcher(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    @CallSuper
    override fun onCreate() {
        super.onCreate()
        lifecycleDispatcher.onServicePreSuperOnCreate()
        savedStateRegistryController.performRestore(null)
    }

    @CallSuper
    override fun onBind(intent: Intent): IBinder? {
        lifecycleDispatcher.onServicePreSuperOnBind()
        return null
    }

    @CallSuper
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        lifecycleDispatcher.onServicePreSuperOnStart()
        return super.onStartCommand(intent, flags, startId)
    }

    @CallSuper
    override fun onDestroy() {
        lifecycleDispatcher.onServicePreSuperOnDestroy()
        super.onDestroy()
    }

//    override fun getLifecycle(): Lifecycle {
//        return lifecycleDispatcher.lifecycle
//    }

    override val lifecycle: Lifecycle
        get() = lifecycleDispatcher.lifecycle

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry


    // This method is where you would call savedStateRegistryController.performSave(outState)
    // to save the state. You will need to determine the appropriate time to save your service's state.
    // Remember that unlike Activities and Fragments, Services do not have a built-in mechanism
    // for saving and restoring state, so you need to manage this yourself.
    @CallSuper
    protected fun onSaveInstanceState(outState: Bundle) {
        savedStateRegistryController.performSave(outState)
    }

    // Similarly, you would call this method to restore the state. You need to pass the saved state Bundle
    // that contains the state information you previously saved.
    @CallSuper
    protected fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            savedStateRegistryController.performRestore(it)
        }
    }
}
