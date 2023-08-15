package com.derry.kt_coroutines.t49

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

fun getFlowValue() = "Derry老师你好"

suspend fun getFlowValueSuspend(): String {
    withContext(Dispatchers.IO) {
        delay(2000)
    }
    return "Derry老师你好 Suspend"
}

private fun <T> (() -> T).toFlow() = flow {
    emit(invoke())
}

private fun String.toFlow() = flow {
    emit(this@toFlow)
}

private fun <OUTPUT> (suspend () -> OUTPUT).toFlow() = flow {
    emit(invoke())
}

private fun <E> Iterable<E>.toFlow() = flow {
    this@toFlow.forEach { emit(it) }
}

private fun <T> Sequence<T>.toFlow() = flow {
    this@toFlow.forEach { emit(it) }
}

private fun <T> Array<T>.toFlow() = flow {
    // this@toFlow.forEach { emit(it) }
    repeat(this@toFlow.size) {
        emit(this@toFlow[it])
    }
}

private fun IntArray.toFlow() = flow {
    for (i in this@toFlow) {
        emit(i)
    }
}

private fun LongArray.toFlow() = flow {
    for (i in this@toFlow) {
        emit(i)
    }
}

private fun IntRange.toFlow() = flow {
    this@toFlow.forEach { emit(it) }
}

private fun LongRange.toFlow() = flow {
    this@toFlow.forEach { emit(it) }
}

// TODO 49-Android协程Flow简化发射源
fun main() = runBlocking<Unit> { // 顶级协程

    val r: () -> String = ::getFlowValue
    r.toFlow().collect { println(it) }

    getFlowValue().toFlow().collect { println(it) }

    ::getFlowValueSuspend.toFlow().collect { println(it) }

    listOf(1, 2, 3, 4, 5, 6).toFlow().collect { println(it) }
    setOf(100, 200, 300, 400, 500, 600).toFlow().collect { println(it) }

    sequence {
        yield("Derry1")
        yield("Derry2")
        yield("Derry3")
    }.toFlow().collect { println(it) }

    arrayOf('A', 'B', 'C').toFlow().collect { println(it) }

    (1000..1008).toFlow().collect { println(it) }

    val r2 = arrayOf(9)
    // Array<Int> 等价于 IntArray
    val intArray : IntArray = intArrayOf(9, 8, 7)
    intArray.toFlow().collect { println(it) }

    val longArray : LongArray = longArrayOf(9L, 8L, 7L)
    longArray.toFlow().collect { println(it) }

    (1000L..1008L).toFlow().collect { println(it) }

    // Flow特点，元素是连续性的（过滤元素）
    var i = 1
    (100 .. 200).toFlow()
        .filter { it % 2 == 0 }
        .map { "${i++} 好质检员，检查了 第$it 台机器" }
        .map { "【$it】" }
        .map { "*$it*" }
        .collect(::println)
}



