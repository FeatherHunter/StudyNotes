package com.derry.kt_coroutines.t53

import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

fun getFlow() = flow {
    (1..10).forEach { emit(it) }
}.onEach { delay(1000) }

// TODO 53-Android协程Flow流取消和检测
fun main() = runBlocking<Unit> {

    // 协程取消后，Flow管道流 也会跟着被取消

    /*getFlow().collect {
        println(it)
        if (it == 5) cancel()
    }*/

    (1..10).asFlow().cancellable().collect {
        println(it)
        if (it == 5) cancel()
    }
}