package com.derry.kt_coroutines.t16

import kotlinx.coroutines.*

// TODO 16-Android协程史上最详细CoroutineStart
fun main() = runBlocking<Unit> { // 父协程

    /*println("main start thread:${Thread.currentThread().name}")

    // TODO 画图 让大家理解 调度中心 的 图概念 脑海里面有自己图
    launch *//*(start = CoroutineStart.DEFAULT)*//* { // 子协程
        println("launch start thread:${Thread.currentThread().name}")
        withContext(Dispatchers.Default) {
            println("withContext start thread:${Thread.currentThread().name}")
            delay(10000)
        }
        println("launch end thread:${Thread.currentThread().name}")
    }*/

    // TODO Default启动模式：协程体被创建后，协程立即开始调度，在调度前 协程被取消，协程会进入"协程取消响应状态" 然后才会取消协程
    /*val job = launch (start = CoroutineStart.DEFAULT) {
        // 再调度后（协程执行阶段）
        println("launch 再调度后（协程执行阶段）>>>1") // 后输出
        println("launch 再调度后（协程执行阶段）>>>2")
        println("launch 再调度后（协程执行阶段）>>>3")
        println("launch 再调度后（协程执行阶段）>>>4")
        println("launch 再调度后（协程执行阶段）>>>5")

        delay(1000 * 10)
        println("launch 再调度后（协程执行阶段）全部结束<<<<")
    }

    // 调度前（先有调度的准备阶段）
    println("协程立即开始调度中.") // 先输出
    println("协程立即开始调度中..")
    println("协程立即开始调度中...")
    println("协程立即开始调度中....")
    println("协程立即开始调度中.....")
    // println("协程立即开始调度中...... 取消协程:${job.cancel()}")
    delay(1)
    println("协程立即开始调度中...... 取消协程:${job.cancel()}")*/

    // TODO ATOMIC启动模式：协程体被创建后，协程立即开始调度，在调度前 协程被取消， 协程体里面会不响应取消的状态(不理你) 直到第一个挂起点（才理你）才能取消
    /*val job = launch (start = CoroutineStart.ATOMIC) {
        // 再调度后（协程执行阶段）
        println("launch 再调度后（协程执行阶段）>>>1") // 后输出
        println("launch 再调度后（协程执行阶段）>>>2")
        println("launch 再调度后（协程执行阶段）>>>3")
        println("launch 再调度后（协程执行阶段）>>>4")
        println("launch 再调度后（协程执行阶段）>>>5")
        println("launch 再调度后（协程执行阶段）>>>6")
        println("launch 再调度后（协程执行阶段）>>>7")
        println("launch 再调度后（协程执行阶段）>>>8")
        println("launch 再调度后（协程执行阶段）>>>9")
        println("网络请求 必须做的工作，哪怕是被取消cancel，也要做的工作，请求的网络埋点，笔记埋点请求的信息 上报给服务器 ...")
        // ... 不管你 cancel 不 cancel，这个工作一定会执行

        // 打个比方：下面代码是 网络请求 耗时
        delay(1000 * 10) // 第一个挂起点，才是取消状态响应中，然后取消，所以下面不会输出
        println("launch 再调度后（协程执行阶段）全部结束<<<<")
    }

    // 调度前（先有调度的准备阶段）
    println("协程立即开始调度中.") // 先输出
    println("协程立即开始调度中..")
    println("协程立即开始调度中...")
    println("协程立即开始调度中....")
    println("协程立即开始调度中.....")
    println("协程立即开始调度中...... 取消协程:${job.cancel()}")*/

    // TODO LAZY启动模式：协程体被创建后，不会调度，会一直等 我们来手动调度（start非挂起，join挂起，await挂起），才开始调度，
    //  在调度前 协程被取消，协程会进入"协程取消响应状态" 然后才会取消协程 == Default模式
    /*val job = launch (start = CoroutineStart.LAZY) {
        // 再调度后（协程执行阶段）
        println("launch 再调度后（协程执行阶段）>>>1") // 后输出
        println("launch 再调度后（协程执行阶段）>>>2")
        println("launch 再调度后（协程执行阶段）>>>3")
        println("launch 再调度后（协程执行阶段）>>>4")
        println("launch 再调度后（协程执行阶段）>>>5")
        println("launch 再调度后（协程执行阶段）>>>6")
        println("launch 再调度后（协程执行阶段）>>>7")
        println("launch 再调度后（协程执行阶段）>>>8")
        println("launch 再调度后（协程执行阶段）>>>9")

        delay(1000 * 10)
        println("launch 再调度后（协程执行阶段）全部结束<<<<")
    }

    job.start()    // 最常用的 （非挂起）  【手动调度】
    // job.join() // 【手动调度】
    // async{}.await // 【手动调度】

    // 调度前（先有调度的准备阶段）
    println("协程立即开始调度中.") // 先输出
    println("协程立即开始调度中..")
    println("协程立即开始调度中...")
    println("协程立即开始调度中....")
    println("协程立即开始调度中.....")
    delay(0)
    println("协程立即开始调度中...... 取消协程:${job.cancel()}")*/

    // 面试题：协程中 Dispatchers.IO，但是我却想让他在 main线程跑协程，有没有办法？
    // 答：CoroutineStart.UNDISPATCHED
    // 特点：由于没有调度中心调度，所以拿不到 Dispatchers.IO 干脆就用 在当前调用栈线程中执行协程，所以是main线程跑协程

    // TODO UNDISPATCHED启动模式：协程体被创建后，（立即 在当前调用栈线程中 执行），没有调度，  协程体里面{一直执行到  第一个挂起点， 然后再执行 父协程的代码}
    // 此模式，协程体被创建后，立即执行，没有调度，既然这么快，请问这个协程是依附在哪个线程呢？
    val job = launch (context = Dispatchers.IO, start = CoroutineStart.UNDISPATCHED) {
        println("launch UNDISPATCHED 立即执行，没有调度 （协程执行阶段）>>>1") // 后输出
        println("launch UNDISPATCHED 立即执行，没有调度（协程执行阶段）>>>2 thread:${Thread.currentThread().name}")
        println("launch UNDISPATCHED 立即执行，没有调度（协程执行阶段）>>>3")
        println("launch UNDISPATCHED 立即执行，没有调度（协程执行阶段）>>>4")
        println("launch UNDISPATCHED 立即执行，没有调度（协程执行阶段）>>>5")
        println("launch UNDISPATCHED 立即执行，没有调度（协程执行阶段）>>>6")
        println("launch UNDISPATCHED 立即执行，没有调度（协程执行阶段）>>>7")
        println("launch UNDISPATCHED 立即执行，没有调度（协程执行阶段）>>>8")
        println("launch UNDISPATCHED 立即执行，没有调度（协程执行阶段）>>>9")

        // 这个就是第一个挂起点
        delay(1000 * 10)
        println("launch 再调度后（协程执行阶段）全部结束<<<<")
    }

    // UNDISPATCHED 没有调度前
    println("UNDISPATCHED 没有调度前.") // 先输出
    println("UNDISPATCHED 没有调度前..")
    println("UNDISPATCHED 没有调度前...")
    println("UNDISPATCHED 没有调度前....")
    println("UNDISPATCHED 没有调度前.....")
    println("UNDISPATCHED 没有调度前...... 取消协程:${job.cancel()}") // 只能取消 delay(1000 * 10)
}