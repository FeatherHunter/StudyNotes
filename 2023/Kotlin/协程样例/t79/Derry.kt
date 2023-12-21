package com.derry.kt_coroutines.t79

import kotlinx.coroutines.*

// TODO 79-脑海中建立协程切换思维图
fun main() = runBlocking(Dispatchers.IO) {

    repeat(5) {
        launch {
            repeat(5) {
                delay(1000L)
                println(Thread.currentThread().name)
            }
        }
    }
}
