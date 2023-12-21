package com.derry.kt_coroutines.t78

import kotlinx.coroutines.*

// TODO 78-深刻理解-线程与协程
fun main() = runBlocking<Unit> {

    println(Thread.currentThread().name) // main thread @coroutine#1  正确

    /*object : Thread() {
        override fun run() {
            super.run()
        }
    }.start()*/

    Thread {
        println(Thread.currentThread().name) //  Thread-0 thread  正确
    }.start()

    launch {
        println(Thread.currentThread().name) // main thread @coroutine#2  正确
    }

}