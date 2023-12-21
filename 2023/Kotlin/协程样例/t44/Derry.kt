package com.derry.kt_coroutines.t44

import kotlinx.coroutines.*

// TODO 44-Android协程为了引出Flow登场
fun main() = runBlocking<Unit> { // 顶级协程

    // TODO 目标：异步 返回多个值，协作的一个一个得到

    // 同步 返回多个值，协作的一个一个得到
    fun getList() = listOf(100, 200, 300, 400, 500, 600)

    // 同步 返回多个值，协作的一个一个得到
    fun getSequence() = sequence {
        for (item in 100..106) {
            // this.delay(1000)
            Thread.sleep(1000)
            this.yield(item)
        }
    }

    // 异步 返回多个值，没有做到协作的一个一个得到，一股脑一次性的输出了
    suspend fun getListSuspendAction() : List<Int> {
        delay(1000)
        return listOf(100, 200, 300, 400, 500, 600)
    }

    // 顶级协程
    val job = GlobalScope.launch {
        // getList().forEach { println(it) }

        // getSequence().forEach { println(it) }

        getListSuspendAction().forEach { println(it) }
    }

    job.join()
}