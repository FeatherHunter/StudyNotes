package com.derry.kt_coroutines.t71

import com.derry.kt_coroutines.t70.HomeRequestResponseResultData
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.selects.select

// TODO  71-Android协程select与channel
@OptIn(ObsoleteCoroutinesApi::class)
fun main() = runBlocking<Unit> {

    val channels = arrayOf(Channel<String?>(), Channel<String?>())

    launch {
        delay(6000)
        channels[0].send("login successful")
    }

    launch {
        delay(8000)
        channels[1].send("register successful")
    }

    val receiveResultSuccessful = select<String ?> {
        for (channel in channels) {
            channel.onReceive {
                // 做校验 工作
                // ...
                // 省略1000行代码
                "【$it】" // 最后一行作为返回值
            }
        }
    }
    println(receiveResultSuccessful)
}