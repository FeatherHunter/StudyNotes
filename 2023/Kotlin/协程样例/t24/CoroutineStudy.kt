package com.derry.kt_coroutines.t24

import kotlinx.coroutines.*
import java.lang.Exception
import java.util.concurrent.CancellationException
import kotlin.coroutines.EmptyCoroutineContext

// TODO 24-Android协程CPU密集型运算时取消之yield
fun main() = runBlocking <Unit> {
    var item = 0

    // Default == 每循环一次计算一次 = CPU密集型运算 = 很密集的运算工作
    val job1 = launch (context = Dispatchers.Default) {
        val start = System.currentTimeMillis()
        while (item <= Int.MAX_VALUE) {
            yield()
            if (System.currentTimeMillis() > start) {
                println("while item:${item++}")
            }
        }
    }

    delay(50)
    // job1.cancelAndJoin()
    job1.cancel()

    println("最后的值 最后打印是 多少呢:$item")
}