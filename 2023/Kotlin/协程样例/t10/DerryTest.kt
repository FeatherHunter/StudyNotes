package com.derry.kt_coroutines.t10

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

suspend fun fun01() {
    withContext(Dispatchers.IO) {

    }
}

fun main() {

    // 人物1    // 在Android平台上 默认是 Default 异步线程
    GlobalScope.launch(Dispatchers.IO) {
        println("coroutine start thread:${Thread.currentThread().name}")
        println("coroutine end")
    }

    // 人物2
    println("main fun thread:${Thread.currentThread().name}")

    Thread.sleep(10000)
}