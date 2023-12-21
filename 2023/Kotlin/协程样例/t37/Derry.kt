package com.derry.kt_coroutines.t37

import kotlinx.coroutines.*
import java.lang.Exception

// TODO 37-Android协程supervisorScope
fun main() = runBlocking <Unit> // 父协程
{

    // 子协程
    val scope = supervisorScope {
        launch {
            println("launch1 start ...")
            delay(1000)
            throw KotlinNullPointerException("协程1 一秒后 抛出异常")
        }

         launch {
            println("launch2 start ...")
            delay(1300)
            println("launch2 end ...")
        }

        launch {
            println("launch3 start ...")
            delay(2300)
            println("launch3 end ...")
        }

        launch {
            println("launch4 start ...")
            delay(2300)
            println("launch4 end ...")
        }

        launch {
            println("launch5 start ...")
            delay(2300)
            println("launch5 end ...")
        }

        // 2秒后的协程必须全部取消
        /*delay(2000)
        cancel()*/

        println("supervisorScope run ")

        yield() // 使用yield 来让出执行权，先让协程1  1秒钟后 抛出异常，    然后49行执行完成后，抛出  supervisorScope {} 里面抛出异常
        delay(1000)
        println("supervisorScope run end")

        // 你敢在 supervisorScope {} 里面抛出异常，这个作用域里面所有的协程都会被取消
        throw KotlinNullPointerException("supervisorScope {} 里面抛出异常")

        '男'
    }

    // runBlocking会等待子协程结束
}