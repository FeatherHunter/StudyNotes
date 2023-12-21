package com.derry.kt_coroutines.t32

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

// TODO 32-Android协程上下文的继承公式
fun main() {
    // val r : CoroutineContext = Job()

    val exception = CoroutineExceptionHandler { _, e ->
        println("顶级父协程发生了异常：e:$e")
    }

    val exception4 = CoroutineExceptionHandler { _, e ->
        println("子协程4发生了异常：e:$e")
    }


    // CoroutineName = coroutine                   如果子协程没有，就用父类的，  如果子协程有，就有子类的
    // CoroutineDispatcher == Dispatchers.Default  如果子协程没有，就用父类的   如果子协程有，就有子类的
    // CoroutineExceptionHandler                   如果子协程没有，就用父类的   如果子协程有，就有子类的

    // Job == 子协程自己的Job实例                    如果子协程没有，就用实例化子类的Job，   如果子协程有，就有子类的
    val coroutineScope = CoroutineScope(Job() + exception + Dispatchers.Main + CoroutineName("coroutineD"))

    // 为什么 Dispatchers.IO 输出后，看起来是 DefaultDispatcher-worker    复用 Dispatchers.Default可用的线程池
    coroutineScope.launch(Dispatchers.Default + CoroutineName("DerryCCC")) {

        launch {
            println("launch1 从上下文获取协程:${coroutineContext[Job]} --- 当前线程与协程:${Thread.currentThread().name}")
        }

        launch {
            println("launch2 从上下文获取协程:${coroutineContext[Job]} --- 当前线程与协程:${Thread.currentThread().name}")
        }

        launch(CoroutineName("C4") + exception4) {
            launch {
                launch {
                    launch {
                        println("launch4 从上下文获取协程:${coroutineContext[Job]} --- 当前线程与协程:${Thread.currentThread().name}")
                    }
                }
            }
        }
    }

    Thread.sleep(1000)
}