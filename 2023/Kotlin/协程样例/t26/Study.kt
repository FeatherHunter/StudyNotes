package com.derry.kt_coroutines.t26

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.FileReader
import java.util.concurrent.CancellationException

// TODO 26-Android企业中use函数释放
fun main() = runBlocking<Unit> {
    val job = launch {
        BufferedReader(FileReader("D:\\Derry.txt"))
            .use {
                var lineContent: String?
                while (true) {
                    delay(1000)
                    lineContent = it.readLine()
                    lineContent ?: break // 如果读取的内容是空的，就跳出循环循环
                    println(lineContent)
                }
            }
    }

    // 调度前
    // job.cancel()

    // 调度后
    delay(2000)
    job.cancel()
}