package com.derry.kt_coroutines.t39

import kotlinx.coroutines.*
import java.lang.Exception

// TODO 39-Android异常捕获常见错误分析
suspend fun main() {

    // CoroutineExceptionHandler 处理的是，顶级launch协程，才能完成异常的捕获
    val exception = CoroutineExceptionHandler { _, e ->
        println("你的协程发生了异常 e:$e")
    }

    val job1 = GlobalScope.launch(exception) {
        launch {
            launch {
                launch {
                    launch {
                        launch {
                            launch {
                                throw KotlinNullPointerException("launch 我错了")
                            }
                        }
                    }
                }
            }
        }
    }

    job1.join()
}