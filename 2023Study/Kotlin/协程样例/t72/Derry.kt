package com.derry.kt_coroutines.t72

import kotlinx.coroutines.*
import kotlinx.coroutines.selects.select

// TODO 72-Android协程select与onJoin
fun main() = runBlocking<Unit> {

    val job1 = launch {
        delay(3000L)
        println("launch1 run")
    }

    val job2 = launch {
        delay(4000L)
        println("launch2 run")
    }

    select<Unit> {
        job1.onJoin { println("launch1 执行完成了 很快") }

        job2.onJoin { println("launch2 执行完成了 很快") }
    }
}