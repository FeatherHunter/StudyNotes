package com.derry.kt_coroutines.t29

import kotlinx.coroutines.*
import kotlin.coroutines.EmptyCoroutineContext

// TODO 29-Android协程CoroutineContext
fun main() = runBlocking<Unit> {
    val job = launch(context = EmptyCoroutineContext) {
        withContext(context = Dispatchers.IO) {

        }
    }

    async(context = EmptyCoroutineContext) {

    }

    // 协程必须有协程上下文 CoroutineContext

}