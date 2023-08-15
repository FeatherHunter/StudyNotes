package com.derry.kt_coroutines.t31

import kotlinx.coroutines.*

// TODO 31-Android协程上下文的继承
fun main() = runBlocking<Unit> {

    // 这样才是 子父类协程关系
    /*launch { 子类

    }*/




    val scope = CoroutineScope(context = Job() + Dispatchers.IO + CoroutineName("自定义协程"))

    val job = scope.launch {
        println("launch 从上下文获取协程:${coroutineContext.get(Job)} --- 当前线程与协程:${Thread.currentThread().name}")

        async {
            println("async1 从上下文获取协程:${coroutineContext.get(Job)} --- 当前线程与协程:${Thread.currentThread().name}")
            "DerryOK"
        }.await()

        async {
            println("async2 从上下文获取协程:${coroutineContext.get(Job)} --- 当前线程与协程:${Thread.currentThread().name}")
            "DerryOK"
        }.await()

        async {
            println("async3 从上下文获取协程:${coroutineContext.get(Job)} --- 当前线程与协程:${Thread.currentThread().name}")
            "DerryOK"
        }.await()
    }

    job.join()
}