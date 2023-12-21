package com.derry.kt_coroutines.t68

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.broadcast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// TODO 68-Android协程Channel的结束
fun main() = runBlocking<Unit> {
    val channel = Channel<Int>(6)

    // 生产者
    launch {
        (1..6).forEach {
            if (!channel.isClosedForSend) {
                channel.send(it)
                println("我生产了一个$it")

                // if (it == 3) channel.close() // 大部分情况下，是生产者 去close
            }
        }
        println("close前 isClosedForSend:${channel.isClosedForSend} " +
                " isClosedForReceive:${channel.isClosedForReceive}")
        channel.close()
        println("close后 isClosedForSend:${channel.isClosedForSend} " +
                " isClosedForReceive:${channel.isClosedForReceive}")
    }
    // 总结：
    // channel.close() 之前 isClosedForSend == false
    // channel.close() 之后 isClosedForSend == true
    // 如果消费完了 isClosedForReceive == true，   否则就是false
    // 如果缓冲区里面还有内容，没有消费完 也是 false

    // 消费者
    launch {
       try {
           for (i in channel) {
               delay(2000L)
               println("我消费了一个:$i")
           }
       }finally {
           println("finally isClosedForSend:${channel.isClosedForSend} " +
                   " isClosedForReceive:${channel.isClosedForReceive}")
       }
    }
}