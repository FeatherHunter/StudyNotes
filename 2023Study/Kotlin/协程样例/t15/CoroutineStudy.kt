package com.derry.kt_coroutines.t15

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

// 先 [请求 用户数据] 网络请求耗时10秒钟
suspend fun requestLoadUser() = withContext(Dispatchers.IO) { delay(1000 * 10) }

// 再 [请求 用户资产数据] 网络请求耗时6秒钟
suspend fun requestLoadUserAssets() = withContext(Dispatchers.IO) { delay(1000 * 6) }

// 再 [请求 用户资产详情数据] 网络请求耗时3秒钟
suspend fun requestLoadUserAssetsDetails() = withContext(Dispatchers.IO) { delay(1000 * 3) }

fun main() = runBlocking<Unit> {
    val deferred1 = async {
        requestLoadUser()
        println("[请求 用户数据] 第一次请求 Successful")
        "Derry1"
    }

    // deferred1.join() TODO test1 1 2 3 顺序串行顺序执行的

    val deferred2 = async {
        requestLoadUserAssets()
        println("[请求 用户资产数据] 第二次请求 Successful")
        "Derry2"
    }

    // deferred2.join() // TODO test1 1 2 3 顺序串行顺序执行的

    val deferred3 = async {
        requestLoadUserAssetsDetails()
        println("[请求 用户资产详情数据] 第三次请求 Successful")
        "Derry3"
    }

    // deferred3.join() // TODO test1 1 2 3 顺序串行顺序执行的

    /*// TODO test2 3 2 1 并发执行的并行执行的
    deferred1.join()
    deferred2.join()
    // 第三个协程体，才是属于 挂起 3秒钟  恢复 41行代码往下执行
    deferred3.join()
    println("41 行 协程体三 恢复执行了")*/

    val timeValue = measureTimeMillis {
        deferred1.await()
        deferred2.await()
        deferred3.await()
    }

    println("measureTimeMillis 一共执行，总共消耗时间是:$timeValue")
}