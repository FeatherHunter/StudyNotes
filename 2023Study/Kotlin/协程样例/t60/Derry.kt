package com.derry.kt_coroutines.t60

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

fun getNumbers() = (1..6).asFlow()

fun runWork(inputValue:Int) = flow {
    emit("$inputValue 号员工开始工作了")
    delay(1000L)
    emit("$inputValue 号员工结束工作了")
}


// TODO 60-Android协程Flow的flatMap
// @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
fun main() = runBlocking<Unit> {

    // 不使用展平操作符
    /*getNumbers()
        .onEach { delay(1000L)}
        .map { runWork(it) }
        // Flow<Flow<String>>
        .collect { it.collect { a -> println(a)} }*/

    // 展平 操作符 flatMap
    getNumbers()
        .onEach { delay(1000L)}
        .flatMapConcat { runWork(it) }
        // .flatMapMerge { runWork(it) }
        // .flatMapLatest { runWork(it) }
        // Flow<Flow<String>> 展平 Flow<String>
        .collect { println(it) }
}