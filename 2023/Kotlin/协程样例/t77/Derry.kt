package com.derry.kt_coroutines.t77

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce

// TODO 77-深刻理解-互相协作的程序之Channel
fun main() = runBlocking<Unit> {

    // TODO 互相协作的程序 == 协程 Coroutine (前面一个人 与 后面一个人，同时在协作干活)
    val phones = createPhones(this) // 明确，就是 createPhones先执行

    getPhones(phones) // 然后再 getPhones后执行

    // Thread.sleep(1000000)
}

// 生产手机
@OptIn(ExperimentalCoroutinesApi::class)
fun createPhones(scope : CoroutineScope) = scope.produce(/*capacity = 5*/) {
    println("开始生产 华为P10")
    GlobalScope.launch { delay(2000000) }
    send("华为P10")

    println("开始生产 华为P20")
    GlobalScope.launch { delay(2000000) }
    send("华为P20")

    println("开始生产 华为P30")
    GlobalScope.launch { delay(2000000) }
    send("华为P40")

    println("开始生产 华为P40")
    GlobalScope.launch { delay(2000000) }
    send("华为P40")

    println("开始生产 华为P50")
    GlobalScope.launch { delay(2000000) }
    send("华为P50")
}

// 消费手机
suspend fun getPhones(phones : ReceiveChannel<String>)  {
    delay(2000L)
    var r = phones.receive()
    println("消费了一台 $r")

    delay(2000L)
    r = phones.receive()
    println("消费了一台 $r")

    delay(2000L)
    r = phones.receive()
    println("消费了一台 $r")

    delay(2000L)
    r = phones.receive()
    println("消费了一台 $r")

    delay(2000L)
    r = phones.receive()
    println("消费了一台 $r")
}
