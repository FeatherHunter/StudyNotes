package com.derry.kt_coroutines.t50

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

private fun <T> flows(value : T) = flow { emit(value) }
private fun <T> T.flows2() = flow { emit(this@flows2) }

private fun <T> flowOfs(vararg values : T) = flow {
    repeat(values.size) {
        emit(values[it])
    }
}

// TODO 50-Android协程Flow仿listOf构建
fun main() = runBlocking {
    flows("Derry").collect { println(it) }
    "Teacher".flows2().collect { println(it) }

    flowOfs("李元霸", "李连杰").collect { println(it) }
    flowOfs(111111, 222222).collect { println(it) }
    flowOf('女', '男').collect { println(it) }
}