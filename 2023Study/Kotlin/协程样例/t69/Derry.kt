package com.derry.kt_coroutines.t69

import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.broadcast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// TODO 69-Android协程BroadcastChannel
@OptIn(ObsoleteCoroutinesApi::class)
fun main() = runBlocking<Unit> {

    val channel = Channel<Int>()
    val broadcastChannel = channel.broadcast(Channel.BUFFERED)

    // 生产者
    launch {
        repeat(8) {
            delay(1000L)
            broadcastChannel.send(it + 100001)
        }
        broadcastChannel.close()
    }


    repeat(8) {
        // 消费者
        launch {
            val r = broadcastChannel.openSubscription()
            for (i in r) {
                println("协程$it ---- 消费者 ${i}")
            }
        }
    }
}