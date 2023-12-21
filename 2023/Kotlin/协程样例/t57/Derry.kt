package com.derry.kt_coroutines.t57

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

private fun <INPUT> Flow<INPUT>.toTake(number : Int) : Flow<INPUT> {
    require(number > 0 ) { "Requested element count 0 should be positive" }

    return flow {
        var i  = 0
        collect {
           if (i++ < number) {
               return@collect emit(it)
           }
        }
    }
}

// TODO 57-Android协程Flow的take
fun main() = runBlocking<Unit> {

    // 限长操作符 take

    listOf(100, 200, 300, 400, 500, 600)
        .asFlow()

        // .take(4)
        .toTake(2)

        .collect { println(it) }

    Thread.sleep(1000)
}