package com.derry.kt_coroutines.t84

import kotlinx.coroutines.*
import javax.microedition.khronos.opengles.GL

fun main() = runBlocking<Unit> {

    val derryScope = CoroutineScope(context = mDerryThread)

    derryScope.launch {
        println("launch 1 start")
        delay(1000L)
        println("launch 1 end")
    }

    derryScope.launch {
        println("launch 2 start")
        delay(1000L)
        println("launch 2 end")
    }

    derryScope.launch {
        println("launch 3 start")
        delay(1000L)
        println("launch 3 end")
    }

    // ...

    delay(500L)

    derryScope.cancel() // 为什么所有协程体，必须有CoroutineScope就是为了统一管理，可控性，结构化并发管理
}