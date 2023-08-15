package com.derry.kt_coroutines.t55

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

// 生成快
fun getFlow() = flow {
    (1..10).forEach {
        delay(1000L)
        emit(it) // 一秒钟发射一个 一秒钟发射一个 ....
        println("生成了:$it thread:${Thread.currentThread().name}")
    }
}
    .buffer(100) // 设置缓冲区，减少 背压
    .flowOn(Dispatchers.IO)

// TODO 55-Android协程Flow合并和处理最新值
fun main() = runBlocking<Unit> {

    val t = measureTimeMillis {
        getFlow()
            // .conflate()
            .collectLatest {
                delay(3000L)
                println("消费了:$it thread:${Thread.currentThread().name}")
            }
    }
    println("上游 下游 共 消耗:$t 时间")
}