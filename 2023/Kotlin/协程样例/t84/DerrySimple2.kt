package com.derry.kt_coroutines.t84

import kotlinx.coroutines.*
import java.util.concurrent.Executors

val mDerryThread = Executors.newSingleThreadExecutor {
    Thread(it, "DerryCustomThread").apply { isDaemon = true }
}.asCoroutineDispatcher()

// TODO 84-CoroutineContext的理解题
// -Dkotlinx.coroutines.debug
fun main() {
    GlobalScope.launch(context = mDerryThread) {
        println("父协程 ${Thread.currentThread().name}")

        launch {
            println("子协程 1 ${Thread.currentThread().name}")
        }

        launch {
            println("子协程 2 ${Thread.currentThread().name}")
        }

        launch {
            println("子协程 3 ${Thread.currentThread().name}")
        }
    }

    Thread.sleep(1000L)
}