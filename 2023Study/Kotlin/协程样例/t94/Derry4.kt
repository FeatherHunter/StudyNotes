package com.derry.kt_coroutines.t94

import java.lang.Exception
import kotlin.concurrent.thread

// 异步逻辑
// 任何的异步请求，一定会有 正常结果返回 异常结果返回
fun requestServerAction(responseSuccessful: (String) -> Unit, responseFailure: (Throwable) -> Unit) {
    thread {
        try {
            Thread.sleep((1000L..10000L).random()) // 模拟网络请求 网络慢 就慢
            responseSuccessful("request response successful")
        } catch (e: Exception) {
            responseFailure(e)
        }
    }
}

// TODO 尽量极致的做到同步的样子-- 97.异步逻辑-同步写法
fun main() {
    // 同步写法
    println("用户点击按钮，开始请求中...")
    requestServerAction(::responseSuccessful, ::responseFailure)
}
fun responseSuccessful(result: String) = println("异步请求成功 result:$result")
fun responseFailure(e: Throwable) =  println("异步请求失败 e:$e")