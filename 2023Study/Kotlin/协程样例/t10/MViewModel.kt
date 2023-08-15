package com.derry.kt_coroutines.t10

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MViewModel : ViewModel() {

    fun derry() {
        viewModelScope.launch {

        }
    }

}