package com.derry.kt_coroutines.t31

import kotlinx.coroutines.*
import kotlin.coroutines.*


// 代码段15

fun main() {
    testLaunch()
    Thread.sleep(2000L)
}

private fun testLaunch() {
    val scope = CoroutineScope(Job())
    scope.launch {
        println("Hello!")
        delay(1000L)
        println("World!")
    }

    /*GlobalScope.launch {
        delay(1000) {

        }
    }*/

    val suspendLambda: suspend () -> Int = suspend {
        println("suspend run ...")
        666
    }

    val continuationBenti: Continuation<Unit> = suspendLambda.createCoroutine(object : Continuation<Int> {
        override val context: CoroutineContext
            get() = Dispatchers.IO

        override fun resumeWith(result: Result<Int>) {
            println("resumeWith result:${result.getOrNull()}")
        }
    })

    // continuationBenti.resumeWith(Result.success(Unit))
    continuationBenti.resume(Unit)
}

/*
输出结果：
Hello!
World!
*/