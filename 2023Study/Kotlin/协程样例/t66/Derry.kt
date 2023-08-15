package com.derry.kt_coroutines.t66

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// TODO 66-Android协程Channel让send不挂起
fun main() = runBlocking<Unit> {

    // 通道Channel
    val channel = Channel<Int>(Channel.UNLIMITED)

    // 生产者
    launch {
        (1..8).forEach {
            channel.send(it) // 容量是2147483647，不会再让send挂起了，因为直接 一次性send到缓冲区了
            println("我生产了一个:$it")
        }
    }

    // 消费者
    launch {
        // 第一种发方式 消费
        /*(1..8).forEach {
            delay(2000L)
            val r=  channel.receive()
            println("消费了一个:$r")
        }*/

        // 第二种发方式 消费
        /*val it = channel.iterator()
        while (it.hasNext()) {
            val item = it.next()
            delay(2000L)
            println("消费了一个:$item")
        }*/

        // 第三种发方式 消费
        for (item in channel) {
            delay(2000L)
            println("消费了一个:$item")
        }
    }

    // 总结：
    // Channel是一个队列，队列中是有缓冲区的，修改了2147483647容量
    // 缓冲区不可能满， receive并没有消费掉，此时send也不会被挂起，因为一股脑存入缓冲区
}