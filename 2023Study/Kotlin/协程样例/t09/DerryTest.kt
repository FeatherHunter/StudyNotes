package com.derry.kt_coroutines.t09

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

// JVM 程序而已，是没有 安卓平台的，所以 安卓的 Dispatchers.Main 我们拿不到，会报错
fun main() {
    // 人物1
    GlobalScope.launch(Dispatchers.IO) { // 在Android平台上 默认是 Default 异步线程
        println("coroutine start thread:${Thread.currentThread().name}")
        println("coroutine end")
    }

    // 人物2
    println("main fun thread:${Thread.currentThread().name}")

    Thread.sleep(10000)
}