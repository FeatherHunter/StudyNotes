package com.derry.kt_coroutines.t35

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception

// TODO 35-Android协程传播的补充
suspend fun main() {
    val job = GlobalScope.launch {
        launch {
            launch {
                launch {
                    launch {
                        launch {
                            launch {
                                launch {
                                    launch {
                                        launch {
                                            async { // async会立即开始调度，所以会往父协程抛异常
                                                // await() 拿返回值 和 异常
                                                throw KotlinNullPointerException("我错了")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    Thread.sleep(1000)
}