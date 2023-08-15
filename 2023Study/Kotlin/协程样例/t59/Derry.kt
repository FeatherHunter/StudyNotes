package com.derry.kt_coroutines.t59

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

fun getNames() = listOf("杜子腾", "史珍香", "刘奋").asFlow().onEach { delay(1000) }
fun getAges() = arrayOf(30, 40, 50).asFlow().onEach { delay(2000) }

// TODO 59-Android协程Flow的zip
fun main() = runBlocking<Unit> {

    // 合并 组合 操作符 zip

    val names = getNames()
    val ages = getAges()

    val time = measureTimeMillis { // 证明 FLow是异步流
        names.zip(ages) { p1, p2 ->
            "name:$p1, age:$p2"
        }.collect {
            println(it)
        }
    }
    println("本次执行 共消耗:$time")
}