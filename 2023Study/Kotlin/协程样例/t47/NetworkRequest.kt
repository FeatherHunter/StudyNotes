package com.derry.kt_coroutines.t47

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

object NetworkRequest {

    fun uploadRequestAction() = flow {
        println("uploadRequestAction thread:${Thread.currentThread().name}")
        for (item in 1..100) {
            delay(1000L)
            emit(item) // 给 MainActivity 反馈 文件上传的进度
        }
    }
}