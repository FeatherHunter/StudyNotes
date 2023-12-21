package com.derry.kt_coroutines.t33

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Exception

// TODO 33-Android协程自动传播异常与向用户暴露异常
fun main() = runBlocking<Unit> // 父协程
{

   // 协程用户暴露异常 TODO >>>>>>>>>>>>>>>>>>>>>>>
    // 子协程
    launch {
        try {
            throw KotlinNullPointerException("is null")
        } catch (e:Exception) {
            println("launch catch exception:$e")
        }
    }

    // 子协程
    async {
        try {
            throw KotlinNullPointerException("is null")
        } catch (e:Exception) {
            println("async catch exception:$e")
        }
    }

    // 全局作用域就可以用了，不是子协程
    val d = GlobalScope.async {
        throw KotlinNullPointerException("is null")
    }

    // 协程自动传播异常 TODO >>>>>>>>>>>>>>>>>>>>>>>
    try {
      d.await()
    }catch (e : Exception) {
        println("async 返回值 catch exception:$e")
    }
}