package com.derry.kt_coroutines.t84

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

// suspend编译之后 剔除suspend 用 Continuation来代替，而Continuation又包含了coroutineContext，所以可以拿到
suspend fun  requestAction() : CoroutineContext = coroutineContext

// TODO 84-CoroutineContext的理解题
fun main() {
    GlobalScope.launch {
        println(requestAction()) // [StandaloneCoroutine{Active}@21ccc917, Dispatchers.Default]
    }
    Thread.sleep(1000L)
}