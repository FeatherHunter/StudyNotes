package com.derry.kt_coroutines.t27

import kotlinx.coroutines.*

// TODO 27-Android无法取消的协程任务
fun main() = runBlocking <Unit> {
    /*val job = launch {
        try {
            for (i in 1..Int.MAX_VALUE) {
                println("item i:$i")
                delay(1000L)
            }
        } finally {
            // TODO 默认情况下，哪怕是写在finally里面的代码，还是可以被取消的
            *//*for (i in 1..Int.MAX_VALUE) {
                println("finally i:$i")
                delay(1000L)
            }*//*

            // 我们以前学习Java的时候，已经是根深蒂固的思路，在finally里面的代码，必须执行（我们印象中，这里面的代码，不能被取消）
            // TODO NonCancellable 不响应取消，不理job.cancel()，所以下面的代码，一定会执行，无法被取消
            withContext(NonCancellable) {
                for (i in 1..Int.MAX_VALUE) {
                    println("finally i:$i")
                    delay(1000L)
                }
            }
        }
    }*/

    // TODO 并不是 NonCancellable 只能放在 finally里面，  只要你想 这段代码，不能被取消，就可以用 “NonCancellable”
    val job = launch {
        withContext(NonCancellable) {
            for (i in 1..Int.MAX_VALUE) {
                println("item i:$i")
                delay(1000L)
            }
        }
    }

    // 调度前 被取消，协程会进入取消响应操作,然后取消
    // job.cancel()

    delay(6000L) // 让你跑六秒钟，那么你 大概会输出6次
    job.cancel()
    println("cancel 协程被取消了")
}
