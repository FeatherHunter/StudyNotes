package com.derry.kt_coroutines.t94

import java.lang.Thread.sleep
import kotlin.concurrent.thread

fun requestAction1(callback: ICallback<Int>) {
    thread {
        sleep((1000L..8000L).random()) // 模拟网络请求 网络慢 就慢
        callback.resume(1000000)
    }
}

fun requestAction2(callback: ICallback<Int>) {
    thread {
        sleep((1000L..8000L).random()) // 模拟网络请求 网络慢 就慢
        callback.resume(2000000)
    }
}

fun requestAction3(callback: ICallback<Int>) {
    thread {
        sleep((1000L..8000L).random()) // 模拟网络请求 网络慢 就慢
        callback.resume(13000000)
    }
}


// TODO 用异步，就难免出现，  95.异步的回调地狱
fun main() {
    requestAction1 {
        println("requestAction1 result:$it")
        requestAction2 {
            println("requestAction2 result:$it")
            requestAction3 {
                println("requestAction3 result:$it")
            }
        }
    }
    println("用户点击了按钮，开始网络请求...")
}