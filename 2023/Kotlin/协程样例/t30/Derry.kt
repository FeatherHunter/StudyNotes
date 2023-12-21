package com.derry.kt_coroutines.t30

import kotlinx.coroutines.*
import kotlin.coroutines.EmptyCoroutineContext

// TODO 30-Android组合协程上下文元素
fun main() = runBlocking<Unit> {
    val job = launch(context = Dispatchers.Default + CoroutineName("我是Derry协程")) {
        println("launch this thread:${Thread.currentThread().name}")
    }
}