package com.derry.kt_coroutines.t80

import kotlinx.coroutines.*

// TODO 80-launch的delay & delay & sleep
fun main() = runBlocking<Unit> {

    // 阻塞式 一
    /*(1..6).forEach {
        Thread.sleep(1000)
        println("forEach1 it:$it")
    }

    (1..6).forEach {
        Thread.sleep(1000)
        println("forEach2 it:$it")
    }*/

    // 阻塞式 二
    /*launch {
        (1..6).forEach {
            Thread.sleep(1000)
            println("forEach1 it:$it")
        }
    }

    launch {
        (1..6).forEach {
            Thread.sleep(1000)
            println("forEach2 it:$it")
        }
    }

    delay(2000)
    // Thread.sleep(2000)
    println("Derry is OK")*/

    // 在作用域里面的挂起函数  在作用域里面，看起来是 像 阻塞的一样(挂起 1秒后 再恢复)，   但是不会阻塞外面的代码
    /*launch {
        (1..6).forEach {
            delay(1000)
            println("forEach1 it:$it")
        }
    }

    launch {
        (1..6).forEach {
            delay(1000)
            println("forEach2 it:$it")
        }
    }

    delay(2000)
    // Thread.sleep(2000)
    println("Derry is OK")*/

    // 在作用域里面的挂起函数(？) 与 外面（OK）  全部都是 非阻塞的效果
    /*launch {
        (1..6).forEach {
            GlobalScope.launch {
                delay(1000000)
                println("OK")
            }
            println("forEach1 it:$it")
            // delay(50)
        }
    }

    launch {
        (1..6).forEach {
            GlobalScope.launch {
                delay(1000000)
                println("OK") // 看起来像 非常像 阻塞的效果
            }
            println("forEach2 it:$it")
        }
    }*/

    // 升级研究版本
    launch {
        (1..6).forEach {
            GlobalScope.launch(this.coroutineContext) {
                delay(2000) // 那道理 需要 12秒钟
                println("OK")
            }
            println("forEach1 it:$it")
        }
    }

    launch {
        (1..6).forEach {
            GlobalScope.launch(this.coroutineContext) {
                delay(2000)  // 那道理 需要 12秒钟
                println("OK") // 看起来像 非常像 阻塞的效果
            }
            println("forEach2 it:$it")
        }
    }
}
