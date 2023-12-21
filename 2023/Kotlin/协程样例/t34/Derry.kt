package com.derry.kt_coroutines.t34

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception

// TODO 34-Android非根协程的异常传播
suspend fun main() {
    val job = GlobalScope.async {
        launch {
            launch {
                launch {
                    launch {
                        launch {
                            launch {
                                launch {
                                    launch {
                                        launch {
                                            launch {
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

    // 开始捕获顶级协程的  GlobalScope.async
    try {
        job.await()
    }catch (e:Exception) {
        println("e:$e")
    }
}