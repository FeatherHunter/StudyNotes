package com.derry.kt_coroutines.t64

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// TODO 64-Android协程Channel进行协程通信
fun main() = runBlocking<Unit> {

    // 通道Channel
    val channel = Channel<Int>()

    // 生产者
    launch {
        (1..6).forEach {
            delay(2000L)
            channel.send(it)
            println("我生产了一个:$it")
        }
    }

    // 消费者
    launch {
        (1..6).forEach {
            val r=  channel.receive()
            println("消费了一个:$r")
        }
    }
}