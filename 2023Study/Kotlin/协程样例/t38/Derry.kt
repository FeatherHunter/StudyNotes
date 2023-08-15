package com.derry.kt_coroutines.t38

import kotlinx.coroutines.*
import java.lang.Exception

// TODO 38-Android协程异常捕获时机
// 默认情况下：顶级父协程launch，可以和 exception 关联来捕获  OK
suspend fun main() {

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

    /*try {
        job1.join()
    }catch (e: Exception) {
        println(e)
    }*/

    val job2 = GlobalScope.launch(exception) {
        async {
            async {
                async {
                    async {
                        async {
                            async { // async协程体被创建后，立即开始调度 执行
                                throw KotlinNullPointerException("async1 我错了")
                            }
                        }
                    }
                }
            }
        }
    }

    /*try {
        job2.join()
    }catch (e: Exception) {
        println(e)
    }*/

    val d = GlobalScope.async (exception) {
        async {
            async {
                async {
                    async {
                        async {
                            async { // async协程体被创建后，立即开始调度 执行
                                throw KotlinNullPointerException("async2 我错了")
                            }
                        }
                    }
                }
            }
        }
    }

    // async 的 await 才能 得到返回值 与 异常处理
    d.await()

    Thread.sleep(1000)
}