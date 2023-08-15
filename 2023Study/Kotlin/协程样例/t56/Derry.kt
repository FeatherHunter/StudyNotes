package com.derry.kt_coroutines.t56

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.runBlocking

// 自己写的变换
fun <T, R> T.mapAction(lambdaAction: T.() -> R) = lambdaAction(this)

// TODO 56-Android协程Flow的transform
fun main() = runBlocking<Unit> {
    // 自己写的变换
    /*listOf(100, 200, 300, 400, 500, 600)
        .asFlow()
        .map {
            it.mapAction { "你好啊数字$it" }
        }
        .collect { println(it) }*/

    // Flow的transform变换
    listOf(100, 200, 300, 400, 500, 600)
        .asFlow()
        .transform {
            this.emit("你好啊数字$it")
        }.collect { println(it) }
}