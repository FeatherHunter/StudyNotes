package com.derry.kt_coroutines.t94

import java.lang.Exception
import java.lang.Thread.sleep
import kotlin.concurrent.thread

interface ResponseCallback<T> {
    fun responseSuccessful(result: T) // 异步的 正常结果返回

    fun responseFailure(e: Throwable) // 异步的 异常结果返回
}

// 异步逻辑
// 任何的异步请求，一定会有 正常结果返回 异常结果返回
fun requestServer(responseCallback: ResponseCallback<String>) {
    thread {
        try {
            sleep((1000L..10000L).random()) // 模拟网络请求 网络慢 就慢
            responseCallback.responseSuccessful("request response successful")
        } catch (e: Exception) {
            responseCallback.responseFailure(e)
        }
    }
}

// TODO 通用异步的设计-96.异步逻辑-异步写法    如果是协程的话(异步的逻辑-同步的写法)
fun main() {
    // 异步写法
    requestServer(object: ResponseCallback<String> {
        override fun responseSuccessful(result: String) = println("异步请求成功 result:$result")

        override fun responseFailure(e: Throwable) = println("异步请求失败 e:$e")
    })
    println("用户点击按钮，开始请求中...")
}