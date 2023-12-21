package com.derry.kt_coroutines.t74

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge

// TODO 74-Android协程并发安全问题
fun main() = runBlocking<Unit> {
    var i = 0
    List(10000) {
        GlobalScope.launch {
            i ++
        }
    }
    println(i)
}