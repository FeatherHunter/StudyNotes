package com.derry.kt_coroutines.t61

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import java.lang.Exception

// 对下游的异常处理
fun getNumbers() = arrayOf(1, 2, 3, 4, 5, 6).asFlow().onEach { delay(1000L) }

// TODO 61-Android协程Flow的exception
fun main() = runBlocking<Unit> {

    // 对下游的异常处理；我们人为主动 try 属于 命令式 传统方式
    /*try {
        getNumbers()
            .collect {
                println(it)
                if (it == 4) {
                    throw KotlinNullPointerException("下游计算过程中，抛出了异常")
                }
            }
    }catch (e : Exception) {
        println("e:$e")
    }*/

    // TODO --------------------------------------------

    // 对上游的异常处理，采用 声明式 Compose
    flow {
        listOf(100).forEach { value ->
            emit(value)
            throw KotlinNullPointerException("上游抛出了异常")
        }
    }
        .catch {
            // println("e:$it")
            // emit(200)
        }
        .onEach { delay(1000L) }

        .collect { println(it) }
}