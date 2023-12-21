package com.derry.kt_coroutines.t42

import kotlinx.coroutines.*
import java.lang.Exception

// TODO 42-Android协程取消时异常处理
fun main() = runBlocking<Unit> { // 顶级父协程

    // 小结：子协程的取消（内部会抛出一个异常 JobCancellationException，内部会自动静默处理掉 这个异常） 不会影响父协程的工作
    // 父协程
    /*launch {

        // 子协程
        val job2 = launch {
            try {
                delay(200000)
            }

            *//*catch (e: Exception) {
                println("你被取消了:e:$e")
            }*//*

            finally {
                println("子协程 finally end") // 子协程的 delay被取消了，抛出异常
            }
        }

        // 取消子协程
        delay(1000)
        job2.cancel()

        // 我们先知道，父协程 会不会被跟着取消
        job2.join()

        delay(3000)
        println("父协程 执行了")
    }*/


    // TODO >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    // 当一个 子协程抛出 非 JobCancellationException 的时候：
    // 1.子协程 上报给父协程 直到 顶级协程为止
    // 2.顶级协程 会 取消他所有的 子协程
    // 3.顶级协程 取消自己
    // 4.处理异常 如果没有处理，就奔溃，  如果处理了，CoroutineExceptionHandler 就会捕捉

    // 非 JobCancellationException 他会上报给 父协程 父协程 直到 顶级协程为止，处理异常 或 奔溃

    val exception = CoroutineExceptionHandler { _, e ->
        println("你报错了哦:$e")
    }

    // 顶级父协程
    val job = GlobalScope.launch(exception) {

        // 子协程一
        launch {
            try {
                delay(200000)
            }finally {
                println("子协程1 finally end") // 子协程的 delay被取消了，抛出异常   // 2
            }
        }

        // 子协程二
        launch {
            delay(3000)
            println("抛出 非 JobCancellationException") // 1
            throw KotlinNullPointerException("子协程二 抛出的哦")
        }
    }
    job.join()
}