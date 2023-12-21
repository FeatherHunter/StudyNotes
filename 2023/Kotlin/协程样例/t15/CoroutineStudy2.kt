package com.derry.kt_coroutines.t15

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

fun main()  = runBlocking<Unit> {
    val timeValue = measureTimeMillis {
        /*async {
            requestLoadUser()
            println("[请求 用户数据] 第一次请求 Successful")
        }.await()

        async {
            requestLoadUserAssets()
            println("[请求 用户资产数据] 第二次请求 Successful")
        }.await()

        async {
            requestLoadUserAssetsDetails()
            println("[请求 用户资产详情数据] 第三次请求 Successful")
        }.await()*/
        // 19秒

        /*val d1 = async {
            requestLoadUser()
            println("[请求 用户数据] 第一次请求 Successful")
        }

        val d2 = async {
            requestLoadUserAssets()
            println("[请求 用户资产数据] 第二次请求 Successful")
        }

        val d3 = async {
            requestLoadUserAssetsDetails()
            println("[请求 用户资产详情数据] 第三次请求 Successful")
        }

        // println("${d1.await()} ${d2.await()} ${d3.await()}") // 10 秒钟

        // 这个是面试题迷惑  // 10 秒钟
        d1.await()
        d2.await()
        d3.await()*/

        /*async { delay(1000) }.await()
        async { delay(1000) }.await()
        async { delay(1000) }.await()*/  // 3秒钟

        val d1 = async { delay(1000) }
        val d2 = async { delay(1000) }
        val d3 = async { delay(1000) }
        d1.await()
        d2.await()
        d3.await()
    }
    // Derry保姆式教学风格
    println("measureTimeMillis 一共执行，总共消耗时间是:$timeValue")
}