package com.derry.kt_coroutines.t85.state

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import java.lang.Exception
import kotlin.concurrent.thread
import kotlin.coroutines.Continuation
import kotlin.coroutines.resumeWithException

@SuppressLint("StaticFieldLeak") val context: Context? = null

// TODO >>>>>>>>>>>>>>>>>>>>>>>>  t85：协程背后状态机原理 ⑤ >>>>>>>>>>>>>>>>>>>>>>>>
fun showCoroutine(continuation: Continuation<Any?>): Any? {

    class ShowContinuation(continuation: Continuation<Any?>) : ContinuationImpl(continuation) {

        // 表示协程状态机当前的状态
        var label = 0

        // 协程返回结果
        var result: Any? = null

        // 用于保存之前协程的计算结果
        var user: Any? = null
        var userAssets: Any? = null

        // invokeSuspend 是协程的关键
        // 它最终会调用 showCoroutine(this) 开启协程状态机
        // 状态机相关代码就是后面的 when 语句
        // 协程的本质，可以说就是 Continuation + 状态机 完成的
        override fun invokeSuspend(_result: Result<Any?>): Any? {
            result = _result
            label = label or Int.Companion.MAX_VALUE
            return showCoroutine(this)
        }
    }

    val continuationState: ShowContinuation = if (continuation is ShowContinuation) {
        continuation
    } else {
        ShowContinuation(continuation) // 如果是初次运行，会把continuation: Continuation<Any?>作为参数传递进去
    }

    // 三个变量，对应原函数的三个变量
    lateinit var user: String
    lateinit var userAssets: String
    lateinit var userAssetsDetails: String

    // result 接收协程的运行结果
    var result = continuationState.result

    // suspendReturn 接收挂起函数的返回值
    var suspendReturn: Any? = null

    // CoroutineSingletons 是个枚举类
    // COROUTINE_SUSPENDED 代表当前函数被挂起了
    val sFlag = CoroutineSingletons.COROUTINE_SUSPENDED

    var loop = true

    while (loop) {

        // 协程状态机核心代码
        when (continuationState.label) {
            0 -> {
                // 检测异常
                throwOnFailure(result)

                println("开始执行了哦")

                // 将 label 置为 1，准备进入下一次状态
                continuationState.label = 1

                // 执行 requestLoadUser
                suspendReturn = requestLoadUser(continuation) // withContent(IO)执行耗时任务

                // 判断是否挂起
                if (suspendReturn == sFlag) {
                    return suspendReturn // suspend挂起了，执行耗时任务完成后，返回suspendReturn == 加载到[用户数据]信息集
                } else {
                    result = suspendReturn // 未挂起，result 接收协程的运行结果
                    // 马上进入下一个专题
                }
            }

            1 -> {
                // 检测异常
                throwOnFailure(result)

                // 获取 user 值
                user = result as String
                toast("更新UI:$user")

                // 将协程结果存到 continuation 里
                continuationState.user = user

                // 准备进入下一个状态
                continuationState.label = 2

                // 执行 requestLoadUserAssets
                suspendReturn = requestLoadUserAssets(continuation) // withContent(IO)执行耗时任务

                // 判断是否挂起
                if (suspendReturn == sFlag) {
                    return suspendReturn // suspend挂起了，执行耗时任务完成后，返回suspendReturn == 加载到[用户资产数据]信息集
                } else {
                    result = suspendReturn // 未挂起，result 接收协程的运行结果
                    // 马上进入下一个专题
                }

            }

            2 -> {
                throwOnFailure(result)

                user = continuationState.user as String

                // 获取 friendList userAssets 的值
                userAssets = result as String
                toast("更新UI:$userAssets")

                // 将协程结果存到 continuation 里
                continuationState.user = user
                continuationState.userAssets = userAssets

                // 准备进入下一个状态
                continuationState.label = 3

                // 执行 requestLoadUserAssetsDetails
                suspendReturn = requestLoadUserAssetsDetails(continuation) // withContent(IO)执行耗时任务

                // 判断是否挂起
                if (suspendReturn == sFlag) {
                    return suspendReturn // suspend挂起了，执行耗时任务完成后，返回suspendReturn == 加载到[用户资产详情数据]信息集
                } else {
                    result = suspendReturn // 未挂起，result 接收协程的运行结果
                    // 马上进入下一个专题
                }
            }

            3 -> {
                throwOnFailure(result)

                user = continuationState.user as String
                userAssets = continuationState.userAssets as String
                userAssetsDetails = continuationState.result as String
                toast("更新UI:$userAssetsDetails")
                loop = false
            }
        }
    }

    return Unit
}

private fun requestLoadUser(completion: Continuation<String>): Any? {
    // no implement
    // 调用 invokeSuspend
    thread {
        try {
            Thread.sleep(3000L) // 这里的睡眠，模拟了请求网络时的耗时操作中...
            if (completion is ContinuationImpl) {
                completion.invokeSuspend(Result.success("加载到[用户数据]信息集")) // 状态流转
            }
        } catch (e: Exception) {
            e.printStackTrace()
            completion.resumeWithException(Throwable("加载[用户数据],加载失败,服务器宕机了 e:$e"))
        }
    }
    return CoroutineSingletons.COROUTINE_SUSPENDED // 代表requestLoadUser是真正的挂起了 挂起函数
}

private fun requestLoadUserAssets(completion: Continuation<String>): Any? {
    // no implement
    // 调用 invokeSuspend
    thread {
        try {
            Thread.sleep(4000L) // 这里的睡眠，模拟了请求网络时的耗时操作中...
            if (completion is ContinuationImpl) {
                completion.invokeSuspend(Result.success("加载到[用户资产数据]信息集"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            completion.resumeWithException(Throwable("加载[用户资产数据],加载失败,服务器宕机了 e:$e"))
        }
    }
    return CoroutineSingletons.COROUTINE_SUSPENDED // 代表requestLoadUserAssets是真正的挂起了 挂起函数
}

private fun requestLoadUserAssetsDetails(completion: Continuation<String>): Any? {
    // no implement
    // 调用 invokeSuspend
    thread {
        try {
            Thread.sleep(5000L) // 这里的睡眠，模拟了请求网络时的耗时操作中...
            if (completion is ContinuationImpl) {
                completion.invokeSuspend(Result.success("加载到[用户资产详情数据]信息集"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            completion.resumeWithException(Throwable("加载[用户资产详情数据],加载失败,服务器宕机了 e:$e"))
        }
    }
    return CoroutineSingletons.COROUTINE_SUSPENDED // 代表requestLoadUserAssets是真正的挂起了 挂起函数
}

private fun throwOnFailure(value: Any?) {
    if (value is Result<*>) {
        value.exceptionOrNull()
    }
}

private fun toast(msg: Any) {
    Toast.makeText(context, "${Thread.currentThread().name} msg=$msg", Toast.LENGTH_SHORT).show()
}
