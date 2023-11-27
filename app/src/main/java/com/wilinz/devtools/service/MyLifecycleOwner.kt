package com.wilinz.devtools.service

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner

interface MySavedStateRegistryOwner : SavedStateRegistryOwner , ViewModelStoreOwner{
    private val mLifecycleRegistry: LifecycleRegistry
        get() = LifecycleRegistry(this)

    private val mSavedStateRegistryController: SavedStateRegistryController
        get() = SavedStateRegistryController.create(this)

    /**
     * @return True if the Lifecycle has been initialized.
     */
    val isInitialized: Boolean
        get() = true

    override val savedStateRegistry: SavedStateRegistry
        get() = mSavedStateRegistryController.savedStateRegistry


//    override fun getLifecycle(): Lifecycle {
//        return mLifecycleRegistry
//    }

    override val lifecycle: Lifecycle
        get() = mLifecycleRegistry

    override val viewModelStore: ViewModelStore
        get() = ViewModelStore()

    fun setCurrentState(state: Lifecycle.State) {
        mLifecycleRegistry.currentState = state
    }

    fun handleLifecycleEvent(event: Lifecycle.Event) {
        mLifecycleRegistry.handleLifecycleEvent(event)
    }

    fun performRestore(savedState: Bundle?) {
        mSavedStateRegistryController.performRestore(savedState)
    }

    fun performSave(outBundle: Bundle) {
        mSavedStateRegistryController.performSave(outBundle)
    }
}