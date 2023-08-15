package com.derry.kt_coroutines.t94

import kotlin.concurrent.thread

// TODO 94.同步与异步
fun main() {
    /**
     * 同步：执行流是 一条线，前一个工作 会阻塞 后一个工作
     */
    /*repeat(6) {
        print("Derry1 >> $it  ")
        Thread.sleep(1000L)
    }

    repeat(6) {
        print("Derry2 >> $it  ")
        Thread.sleep(1000L)
    }*/

    /**
     * 异步：执行流程是 多条线，前一个工作 与 后一个工作，一起干活，谁也无法阻塞谁
     */
    thread {
        repeat(6) {
            print("Derry1 >> $it  ")
            Thread.sleep(1000L)
        }
    }

    thread {
        repeat(6) {
            print("Derry2 >> $it  ")
            Thread.sleep(800L)
        }
    }
}