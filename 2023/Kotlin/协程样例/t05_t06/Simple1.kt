package com.derry.kt_coroutines.t05_t06

fun fun01() {
    println("模拟服务器请求中...")
    Thread.sleep(8000)
    println("模拟服务器请求完成 数据data{}")
}

fun fun02(action: (String) -> Unit) {
    println("模拟服务器请求中...")
    Thread.sleep(8000)
    action("模拟服务器请求完成 数据data{}")
}

fun fun03() : String {
    println("模拟服务器请求中...")
    Thread.sleep(8000)
    return "模拟服务器请求完成 数据data{}"
}

// 1.学习Java，学习C++等 在常规操作中，有 调用，回调，返回，来做业务的
fun main() {
    // fun01() // 调用

    // 回调
    /*fun02() {
        println(it)
    }*/

    val responseBeanDataResult = fun03() // 返回
    println(responseBeanDataResult)
}