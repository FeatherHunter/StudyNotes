package com.derry.kt_coroutines.t51

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

fun getFlow1() = flow {
    delay(6000)
    println("上游发送 context:${Thread.currentThread().name}")
    emit("Derry Teacher run ...")
}

fun getFlow2() = flow {
    withContext(Dispatchers.IO) { // 上游 打破上下文的保存
        delay(6000)
        println("上游发送 context:${Thread.currentThread().name}")
        emit("Derry Teacher run ...")
    }
}

fun getFlow3() = flow {
    delay(6000)
    println("上游发送 context:${Thread.currentThread().name}")
    emit("Derry Teacher run ...")
}.flowOn(Dispatchers.IO)

// TODO 51-Android协程Flow上下文保存机制
fun main() = runBlocking<Unit> {

    // 1.getFlow1 getFlow2 可以收集的原因是： 上游发射  与 下游收集  是同一个上下文（上下文保存机制）
    // 下游收集 收集时候的挂起操作 是依靠 上游发射的上下文，所以  上游发射 与 下游收集 必须保持 同一个上下文（上下文保存机制）

    // 2.getFlow3 是没有遵循 上下文保存机制， 上游 打破上下文的保存

    getFlow3().collect { println(it); println("下游收集 context:${Thread.currentThread().name}") }

}