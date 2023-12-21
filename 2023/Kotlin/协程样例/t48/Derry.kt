package com.derry.kt_coroutines.t48

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

fun getFlow() = flow {
    println("getFlow 111") // 2
    emit("Derry老师") // 3
    println("getFlow 222") // 4
}

// TODO 48-Android协程Flow是冷流的概念
fun main() = runBlocking<Unit> { // 顶级协程

    // Flow是冷流  == RxJava也是冷流

    // 冷流 生活中的例子：
    // 你去餐厅吃饭，坐到椅子上， 服务器 拿菜单，   服务器 拿筷子给我    服务器 端菜给我   ...  【服务极差】
    // 案例二： 挑水喝，你打开水龙头的那一刻 没有水， 叫一声，给我一点水，就真的给一点水

    // 热流 生活中的例子：
    // 你去餐厅吃饭，坐到椅子上 什么话都不说，人家服务器 各种 给你考虑到 热情的  【服务很好 很热情】
    // 案例二：你打开水龙头的那一刻，就马上立刻有水（因为 早就把水准备好了，就等你用了）

    // 子协程
    launch {
        println("launch start")

        delay(6000)

        println("launch end")

        getFlow()

        println("我已经调用了 getFlow方法了")

        println("开始收集上游") // 1
        getFlow().collect { println(it) } // 4
    }

}