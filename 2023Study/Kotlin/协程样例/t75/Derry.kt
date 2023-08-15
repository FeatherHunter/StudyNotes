package com.derry.kt_coroutines.t75

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

// TODO 75-Android协程并发安全解决方案
fun main() = runBlocking<Unit> {
    // TODO 解决方案一：Java的API来解决
    /*var i = AtomicInteger(0)
    List(10000) {
        GlobalScope.launch {
            i.incrementAndGet()
        }
    }.joinAll()
    println(i.get())
    */

    // TODO 解决方案二：KT的API来解决 Channel并发安全队列

    // TODO 解决方案三：KT的API来解决 锁 C++的互斥锁
    /*val m = Mutex()
    var i = 0
    List(10000) {
        GlobalScope.launch {
            m.toLock {
                i++
            }
        }
    }.joinAll()
    println(i)*/

    // TODO 解决方案四：KT的API来解决 信号量控制
    /*val s = Semaphore(1) //  Semaphore(1) 等价于 Mutex
    var i = 0
    List(10000) {
        GlobalScope.launch {
            s.toPermit {
                i++
            }
        }
    }.joinAll()
    println(i)*/

    // TODO 直接在写代码的时候，不出现并发安全问题，不就行了，这样的话，就不需要解决方案
    //  不变性
    var i = 0
    val r = i + List(10000) {
        GlobalScope.async { 1 }
    }.map { it.await() }.sum()
    println(r)
}

private suspend inline fun <T> Mutex.toLock(action: () -> T) {
    this.lock() // 先锁住
    try {
        action() // 调用Lambda
    } finally {
        this.unlock() // 再解锁
    }
}

private suspend inline fun <T> Semaphore.toPermit(action: () -> T) {
    this.acquire()
    try {
        action()
    } finally {
        this.release()
    }
}