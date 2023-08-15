package com.derry.kt_coroutines.t21

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Exception
import java.util.concurrent.CancellationException

// TODO 21-Android协程取消的异常
fun main() = runBlocking <Unit> // 父协程作用域
{

    // 另外一个协程的作用域
    val job1 = GlobalScope.launch {
        try {
            delay(800)
            println("GlobalScope.launch Success")
        } catch (e: CancellationException) {
            println("协程被取消了 e:$e")
        }
    }

    job1.cancel(CancellationException("Derry手动去取消了协程")) // 如果不捕获异常，会被静默处理到，所以看不到

    // delay(2000)
    job1.join() // 注意：为了让你理解的 job1.join 抢占了所有线程资源 执行阻塞操作，800毫秒后，让后在放开执行权，执行 job1.join() 行后的代码
}