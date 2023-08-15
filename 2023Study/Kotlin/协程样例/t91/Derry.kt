package com.derry.kt_coroutines.t91

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.derry.kt_coroutines.t82.Dispatcher
import com.derry.kt_coroutines.t82.HandlerDispatcher
import kotlinx.coroutines.Dispatchers
import java.lang.Thread.sleep
import kotlin.concurrent.thread
import kotlin.coroutines.*

interface IContinuation<T> {
    val context: Dispatcher

    fun resume(result : T)
}

fun getLength(str: String, iContinuation: IContinuation<Int>) {
    thread { // 网络请求的时候，切换到 异步线程
        sleep((5000L..20000L).random()) // 模拟网络请求

        println("1 getLength thread:${Thread.currentThread().name}")

        iContinuation.context.dispatch { // 切换回主线程main
            iContinuation.resume(str.length) // 调用Continuation回调 最终的成果长度 给 用户
        }
    }
    println("真正读取中...")
}

suspend fun getLength(str: String)  = suspendCoroutine<Int>{
    thread { // 网络请求的时候，切换到 异步线程
        sleep((5000L..20000L).random()) // 模拟网络请求

        println("2 getLength thread:${Thread.currentThread().name}")

        // iContinuation.context.dispatch { // 切换回主线程main
            it.resume(str.length) // 调用Continuation回调 最终的成果长度 给 用户
        // }
    }
    println("真正读取中...")
}

class Derry : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getLength("Derry",object : IContinuation<Int> {
            override val context: Dispatcher
                get() = HandlerDispatcher

            override fun resume(result: Int) {
                println(" 1 读完完成 长度是:${result} thread:${Thread.currentThread().name}")
            }
        })

        suspend { getLength("Derry2") }.startCoroutine(object : Continuation<Int> {
            override val context: CoroutineContext
                get() = Dispatchers.Main

            override fun resumeWith(result: Result<Int>) {
                println(" 2 读完完成 长度是:${result} thread:${Thread.currentThread().name}")
            }
        })
    }
}