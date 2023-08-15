package com.derry.kt_coroutines.t25

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.FileReader
import java.lang.Exception
import java.util.concurrent.CancellationException

// TODO 25-Android协程取消的影响
fun main() = runBlocking<Unit> {
    val fileBuffer = BufferedReader(FileReader("D:\\Derry.txt"))

    val job1 = launch {
        fileBuffer.also {
            var lineContent : String ?
            try {
                while (true) {
                    delay(1000)
                    lineContent = it.readLine()
                    lineContent ?: break // 如果读取的内容是空的，就跳出循环循环
                    println(lineContent)
                }
            } catch (e: CancellationException) {
                println("协程被取消了 异常是:$e")
            } finally {
                println("BufferedReader 被释放")
                it.close()
            }
        }
    }

    delay(2000)
    job1.cancel()


}