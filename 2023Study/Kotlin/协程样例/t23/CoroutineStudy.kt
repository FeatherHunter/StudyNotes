package com.derry.kt_coroutines.t23

import kotlinx.coroutines.*
import java.lang.Exception
import java.util.concurrent.CancellationException
import kotlin.coroutines.EmptyCoroutineContext

// TODO 23-Android协程CPU密集型运算时取消之ensureActive
fun main() = runBlocking <Unit> {
    var item = 0

    // Default == 每循环一次计算一次 = CPU密集型运算 = 很密集的运算工作
    val job1 = launch (context = Dispatchers.Default) {
        val start = System.currentTimeMillis()
        while (item <= Int.MAX_VALUE) {
            ensureActive()
            if (System.currentTimeMillis() > start) {
                println("while item:${item++}")
            }
        }
    }

    // 调度前 - 协程体 准备工作期
    // job1.cancel()

    // 调度后 - 协程体 执行期
    delay(50)
    /*job1.cancel()
    job1.join()*/
    job1.cancelAndJoin()

    println("最后的值 最后打印是 多少呢:$item")
}