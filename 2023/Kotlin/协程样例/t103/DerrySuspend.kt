package com.derry.kt_coroutines.t103

import androidx.annotation.UiThread
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.createCoroutine

// 挂起，恢复，结束，状态（密封类）
sealed class State {
    class NoStart(val continuation: Continuation<Unit>? = null) : State() { // continuation只是为了协程的恢复
        init {
            println("NoStart 更新了")
        }
    }

    class Start(val continuation: Continuation<Unit>? = null) : State() { // continuation只是为了协程的恢复
        init {
            println("Start 更新了")
        }
    }

    object Close : State()
}

abstract class Helper { // 中转站
    abstract suspend fun send(inputValue: Int)
}

// 生产者函数
fun setData(lambda: suspend Helper.() -> Unit): () -> SetGetImpl {
    return {
        SetGetImpl(lambda, 10)
    }
}

class SetGetImpl(lambda: suspend Helper.() -> Unit, startValue: Int) : Helper(), Iterator<Int> {

    private var state: State

    // 先在构造代码块中，保存lambda
    init {
        // 先在构造代码块中，可以证明还没有启动

        /*val continuationBenti : Continuation<Unit> = lambda.createCoroutine(this, object: Continuation<Unit> {

            override val context: CoroutineContext
                get() = EmptyCoroutineContext

            override fun resumeWith(result: Result<Unit>) {
                state = State.Close
                println("生产者的suspendLambda执行完成了 协程体执行完后的最后一步的 Resume 全部结束了")
            }
        })

        state = State.NoStart (continuationBenti)*/

        state = State.NoStart(
            lambda.createCoroutine(this, object : Continuation<Unit> {

                override val context: CoroutineContext
                    get() = EmptyCoroutineContext

                override fun resumeWith(result: Result<Unit>) {
                    state = State.Close
                    println("消费者的suspendLambda执行完成了 协程体执行完后的最后一步的 Resume 全部结束了")
                }
            })
        )
    }

    // 生产一个值后，挂起，      直到被消费后，再恢复
    override suspend fun send(inputValue: Int) {

    }

    // TODO =============== 下面两个方法，消费者循环遍历时，会触发，是给消费者用的 start
    override fun hasNext(): Boolean {
        TODO("Not yet implemented")
    }

    override fun next(): Int {
        TODO("Not yet implemented")
    }
    // TODO =============== 下面两个方法，消费者循环遍历时，会触发，是给消费者用的 end
}

// TODO 手写挂起与恢复（协程的高潮环节，协程的鼎盛时期）
// 冷流：消费者开关打开后，  生产者才开始工作
fun main() {
    // 生产者
    val suspendLambda: suspend Helper.() -> Unit = {
        for (i in 0..5) {
            val r = send(i) // send后就会挂起了
        }
    }

    val setObj: () -> SetGetImpl = setData(suspendLambda)

    val item: SetGetImpl = setObj()

    // 消费者
    for (i in item) {
        Thread.sleep(1000L)
        println("消费-循环 i:$i")
    }
}