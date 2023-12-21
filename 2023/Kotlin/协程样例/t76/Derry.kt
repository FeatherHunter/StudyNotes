package com.derry.kt_coroutines.t76

import kotlinx.coroutines.*

// TODO 76-深刻理解-互相协作的程序
fun main() = runBlocking<Unit> {

    // TODO 非互相协作的程序 == 传统方式 (必须 前面 一个人干完后   后面那个人 才能再干活)
    // getPhones(createPhones())

    // TODO 互相协作的程序 == 协程 Coroutine (前面一个人 与 后面一个人，同时在协作干活)
    getPhones(createPhones())
}

/*
// 生产手机
fun createPhones() : List<String> {
    val phones = mutableListOf<String> ()
    println("开始生产 华为P10")
    Thread.sleep(2000L)
    phones.add("华为P10")

    println("开始生产 华为P20")
    Thread.sleep(2000L)
    phones.add("华为P20")

    println("开始生产 华为P30")
    Thread.sleep(2000L)
    phones.add("华为P40")

    println("开始生产 华为P40")
    Thread.sleep(2000L)
    phones.add("华为P40")

    println("开始生产 华为P50")
    Thread.sleep(2000L)
    phones.add("华为P50")

    return phones
}

// 消费手机
fun getPhones(phones : List<String>)  {

    var r = phones[0]
    println("消费了一台 $r")

    r = phones[1]
    println("消费了一台 $r")

    r = phones[2]
    println("消费了一台 $r")

    r = phones[3]
    println("消费了一台 $r")

    r = phones[4]
    println("消费了一台 $r")
}*/



// 生产手机
fun createPhones() = sequence <String> {
    val phones = mutableListOf<String> ()
    println("开始生产 华为P10")
    GlobalScope.launch { delay(2000000) }
    yield("华为P10")

    println("开始生产 华为P20")
    GlobalScope.launch { delay(2000000) }
    yield("华为P20")

    println("开始生产 华为P30")
    GlobalScope.launch { delay(2000000) }
    yield("华为P40")

    println("开始生产 华为P40")
    GlobalScope.launch { delay(2000000) }
    yield("华为P40")

    println("开始生产 华为P50")
    GlobalScope.launch { delay(2000000) }
    yield("华为P50")
}

// 消费手机
fun getPhones(sequence : Sequence<String>)  {
    val phones = sequence.iterator()

    var r = phones.next()
    println("消费了一台 $r")

    r = phones.next()
    println("消费了一台 $r")

    r = phones.next()
    println("消费了一台 $r")

    r = phones.next()
    println("消费了一台 $r")

    r = phones.next()
    println("消费了一台 $r")
}
