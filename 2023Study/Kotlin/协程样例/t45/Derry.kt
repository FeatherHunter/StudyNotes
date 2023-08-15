package com.derry.kt_coroutines.t45

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

// TODO 45-Android协程Flow异步返回多值
fun main() = runBlocking<Unit> { // 顶级协程

    // TODO 目标：异步 返回多个值，协作的一个一个得到
    fun getFlow() = flow {
        for (item in 1..8) {
            delay(1000)

            // flow的发射端（起点 Observable） 反射 元素 元素
            emit(item)
        }
    }

    // 顶级协程
    val job = GlobalScope.launch {
        // flow的末尾端 （终点 Observer）
        getFlow().collect { println(it) }
    }

    job.join()

    // RxJava 和 Flow 几乎一样，      以后的Flow替代LiveData
}