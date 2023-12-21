package com.derry.kt_coroutines.t10

import androidx.lifecycle.*
import kotlinx.coroutines.launch

class MLifecycle : LifecycleOwner {

    override fun getLifecycle(): Lifecycle {
        TODO("Not yet implemented")
    }

    fun fun01() {
        lifecycleScope.launch {

        }
    }

}