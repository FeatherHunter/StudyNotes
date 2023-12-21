package com.derry.kt_coroutines.t65

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// TODO 65-Android协程Channel的capacity
fun main() = runBlocking<Unit> {

    // 通道Channel
    val channel = Channel<Int>()

    // 生产者
    launch {
        (1..1000).forEach {
            channel.send(it) // 容量是0，会挂起2秒钟后，知道 消费完成后，才再次恢复   以此类推....
            println("我生产了一个:$it")
        }
    }

    // 消费者
    launch {
        (1..1000).forEach {
            delay(2000L)
            val r=  channel.receive()
            println("消费了一个:$r")
        }
    }

    // 总结：
    // Channel是一个队列，队列中是有缓冲区的，默认是0，
    // 如果缓冲区满了， receive并没有消费掉，此时send会被挂起，直到receive消费完成后，send再次恢复 再生产一个 以此类推 ...
}