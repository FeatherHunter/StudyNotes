package com.derry.kt_coroutines.t67

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.produce

// TODO 67-Android协程的快捷方式
@OptIn(ExperimentalCoroutinesApi::class, ObsoleteCoroutinesApi::class)
fun main() = runBlocking<Unit> {
    /*// 生产者的快捷方式
    val produce = produce {
        (1..20).forEach { delay(2000L) ; send(it) }
    }

    // 普通的消费
    launch {
        for (item in produce) {
            println("消费了一个:$item")
        }
    }*/

    // TODO  -----------------------

    // 消费者的快捷方式
    val producer = actor<Int> {
        (1..20).forEach {
            println("消费了一个:${receive()}")
        }
    }

    // 普通的生成
    launch {
        (1..20).forEach { delay(2000L) ; producer.send(it) }
    }

}