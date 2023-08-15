package com.derry.kt_coroutines.t72

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.selects.select

// TODO 72-Android协程select与onSend-下
fun main() = runBlocking<Unit> {

   val channels = arrayOf(Channel<Char>(), Channel<Char>())
    println(channels[0])
    println(channels[1])

    // Channel 的 发射源
    launch(Dispatchers.Default) {
        select<Unit> {
            launch {
                delay(2000)
                channels[0].onSend('女') {
                    println("channels[0].onSend('女') { $it }")
                }
            }

            launch {
                delay(3000)
                channels[1].onSend('男') {
                    println("channels[0].onSend('男') { $it }")
                }
            }
        }
    }

    // 下游 接收阶段
    launch { println("channel1 下游接收 ${channels[0].receive()}") }
    launch { println("channel2 下游接收 ${channels[1].receive()}") }
}