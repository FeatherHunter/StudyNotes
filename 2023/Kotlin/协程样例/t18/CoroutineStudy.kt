package com.derry.kt_coroutines.t18

import kotlinx.coroutines.*

fun main() = runBlocking<Unit> {

    /*val job = launch {
        println("Active存活了")
        delay(1000 * 10)
        println("delay 完成了")
    }

    common("协程体被创建的正常情况", job)
    delay(20)

    job.cancel()
    common("协程体被取消了 cancel", job)

    delay(50)
    common("从 已取消 到 完成中 的一个50挂起", job)*/

    /*val job2 = async {

    }*/

    // 从创建活跃 到 自然完成
    val job = launch {
        println("launch Active存活了")
        delay(1000 * 10)
        println("launch delay 完成了")
    }

    common("协程体被创建的正常情况", job)

    delay(1000 * 5)
    common("5秒钟后，看看状态", job)

    delay(1000 * 4)
    common("9秒钟后，看看状态", job)

    delay(1000 * 2)
    common("11秒钟后，看看状态", job)
}

private fun common(value: String, job: Job) {
    println("""
        $value
        ${if (job.isActive) "活跃中" else "未活跃"}
        ${if (job.isCancelled) "已取消" else "未取消"}
        ${if (job.isCompleted) "已完成" else "未完成"}
        --------
    """.trimIndent())
}