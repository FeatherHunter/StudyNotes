package com.derry.kt_coroutines.t62

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import java.lang.Exception

fun getNumbers() = arrayOf(1, 2, 3, 4, 5, 6).asFlow().onEach { delay(1000L) }

// 对下游的异常处理
fun getNumbers2() = flow {
    for (i in arrayOf(1)) {
        emit(i)
        throw KotlinNullPointerException("上游抛出了异常")
    }
}

// TODO 62-Android协程Flow的completion
fun main() = runBlocking<Unit> {

    // TODO 1.正常的结束 (命令式)
   /*try {
       getNumbers().collect { println(it) }
   }finally {
       println("协程Flow结束了")
   }*/

    // TODO 2.正常的结束 (声明式)
    // getNumbers().onCompletion { println("协程Flow结束了") }.collect { println(it) }

    // TODO 3.异常的结束(声明式) 上游发生异常
    /*getNumbers2().onCompletion {
            if (it != null) { // 非正常结束  是异常结束
                println("上游 发生了异常 $it")
            }
        }
        .catch { println("被catch到了 上游 发生了异常 $it") }  // .catch是能 捕获到 上游 抛出的异常， 异常的传递过程
        .collect { println(it) }*/

    // TODO 4.异常的结束(命令式) 下游发生异常
    try {
        getNumbers()
            // onComplete 上游 与 下游 的异常信息，都能够知道 能够得到
            .onCompletion {
                if (it != null) { // 非正常结束  是异常结束
                    println("下游 发生了异常 $it")
                }
            }
            // .catch { println("被catch到了 下游 发生了异常 $it") } // .catch是不能 捕获到 下游 抛出的异常
            .collect {
                println(it)
                if (it == 5) throw KotlinNullPointerException("下游异常抛出")
            }
    }catch (e:Exception) {
        println("被catch到了 下游 发生了异常 $e")
    }

    // 总结：
    // 上游的异常抛出，可以使用 声明式
    // 下游的异常抛出，可以使用 命令式
    // onCompletion（声明式） 上游 与 下游 的异常信息，都能够知道 能够得到
    // onCompletion（声明式） 正常的结束 还是 异常的结束，都能知道
    // finally 能够知道正常的结束（命令式）
}