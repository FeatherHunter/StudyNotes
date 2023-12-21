package com.derry.kt_coroutines.t52

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

// 发射源区域
fun getFlowValue() =
    listOf(100, 200, 300, 400, 500, 600)
        .asFlow()
        .onEach { delay(2000) }
        .flowOn(Dispatchers.Default)


// TODO 52-Android协程Flow的launchIn收集流
fun main() = runBlocking<Unit> {

    // 收集消费区域
    /*getFlowValue()
        .collect {
            println("thread:${Thread.currentThread().name}   $it")
        }*/

    // 收集消费区域
    val job = getFlowValue()
        .onEach { println("thread:${Thread.currentThread().name}   $it")  }
        .launchIn(CoroutineScope(Dispatchers.IO + CoroutineName("自定义协程")))

    delay(6500)
    job.cancel()
    job.join()
}