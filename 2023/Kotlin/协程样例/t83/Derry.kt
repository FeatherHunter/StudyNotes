package com.derry.kt_coroutines.t83

import kotlinx.coroutines.*

private suspend fun requestLogin() {
    withContext(Dispatchers.IO) {
        println("requestLogin 协程正在执行中 请求中...")
        delay((1000L..10000L).random())
    }
}

private suspend fun requestRegister() {
    withContext(Dispatchers.IO) {
        println("requestRegister 协程正在执行中 请求中...")
        delay((1000L..10000L).random())
    }
}

private suspend fun requestDownload() : Int {
    withContext(Dispatchers.IO) {
        println("requestRegister 协程正在执行中 请求中...")
        delay((1000L..10000L).random())
    }
    return 100
}

fun main() = runBlocking<Unit> {

    // TODO 1.得知协程执行完毕
    /*val job1 = launch { requestLogin() }
    job1.join()
    println("协程执行完毕，可以更新UI操作了")*/

    /*val job2 = launch { requestRegister() }
    job2.invokeOnCompletion {
        println("协程执行完毕，可以更新UI操作了")
    }*/

    /*val deferred = async { // 创建协程体【执行顺序1】
        requestDownload() // 调度后 运行期【执行顺序3】
    }
    val r = deferred.await() // 调度前 准备期 挂起42行【执行顺序2】   获取最终的100成果【执行顺序4】
    println("协程执行完毕，可以更新UI操作了 r:$r") // 【执行顺序5】*/

    // TODO 2.协程句柄(job,deferred) 所表现的结构化并发

    // TODO 2.1 等所有的子协程全部完成，然后父协程完成， 才是完全的完成
    /*var childrenJob1 : Job ? = null
    var childrenJob2 : Job ? = null
    var childrenJob3 : Job ? = null
    val startTime = System.currentTimeMillis()
    // 父协程
    val fuJob = launch {
        childrenJob1 = launch { delay(1000L) } // 子协程
        childrenJob2 = launch { delay(2000L) } // 子协程
        childrenJob3 = launch { delay(3000L) } // 子协程
    }

    delay(600L) // 为什么要写这句话，就是为了变成调度后 运行期时候，执行下面的循环遍历对比
    fuJob.children.forEachIndexed { index, job ->
        when(index) {
            0 -> println("fuJob等不等于childrenJob1=${job == childrenJob1}")
            1 -> println("fuJob等不等于childrenJob2=${job == childrenJob2}")
            2 -> println("fuJob等不等于childrenJob3=${job == childrenJob3}")
        }
    }
    fuJob.join()
    println("完全的完成 ${startTime - System.currentTimeMillis()}")*/

    // TODO 2.2 父协程被取消，然后所有的子协程全部取消， 才是完全的取消
    /*var childrenJob1 : Job ? = null
    var childrenJob2 : Job ? = null
    var childrenJob3 : Job ? = null
    // 父协程
    val fuJob = launch { // 子协程
        childrenJob1 = launch {
            println("launch 1 start")
            delay(1000L)
            println("launch 1 end")
        }
        childrenJob2 = launch { // 子协程
            println("launch 2 start")
            delay(2000L)
            println("launch 2 end")
        }
        childrenJob3 = launch { // 子协程
            println("launch 3 start")
            delay(3000L)
            println("launch 3 end")
        }
    }

    delay(600L) // 为什么要写这句话，就是为了变成调度后 运行期时候，执行下面的循环遍历对比
    fuJob.children.forEachIndexed { index, job ->
        when(index) {
            0 -> println("fuJob等不等于childrenJob1=${job == childrenJob1}")
            1 -> println("fuJob等不等于childrenJob2=${job == childrenJob2}")
            2 -> println("fuJob等不等于childrenJob3=${job == childrenJob3}")
        }
    }
    fuJob.cancel()*/

    // TODO 2.3 结构化并发，强调的是 父子协程关系 才会有结构化并发
    var childrenJob1 : Job ? = null
    var childrenJob2 : Job ? = null
    var childrenJob3 : Job ? = null
    // 父协程
    val fuJob = launch { // 子协程

        // 此协程，并不是 fuJob的子协程了
        // 此协程是 runBlocking 的子协程了，和 fuJob没有任何关系了
        childrenJob1 = launch(this@runBlocking.coroutineContext) {
            println("launch 1 start")
            delay(1000L)
            println("launch 1 end")
        }

        childrenJob2 = launch { // 子协程
            println("launch 2 start")
            delay(2000L)
            println("launch 2 end")
        }
        childrenJob3 = launch { // 子协程
            println("launch 3 start")
            delay(3000L)
            println("launch 3 end")
        }
    }

    delay(600L) // 为什么要写这句话，就是为了变成调度后 运行期时候，执行下面的循环遍历对比
    fuJob.children.forEachIndexed { index, job ->
        when(index) {
            0 -> println("fuJob等不等于childrenJob1=${job == childrenJob2}")
            1 -> println("fuJob等不等于childrenJob2=${job == childrenJob3}")
        }
    }
    fuJob.cancel()
    this.cancel() // launch 1 会抛出一个异常，给 父协程处理 runBlocking， 但是父协程没有处理，所有就奔溃出来了
}