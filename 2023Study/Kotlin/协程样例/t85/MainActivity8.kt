package com.derry.kt_coroutines.t85

import android.app.ProgressDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.derry.kt_coroutines.R
import com.derry.kt_coroutines.t82.DispatchedContinuation
import com.derry.kt_coroutines.t82.HandlerDispatcher
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlin.concurrent.thread
import kotlin.coroutines.*

// TODO  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 下面是 t85通用代码：

/*
 * 请求加载[用户数据]
 */
private suspend fun requestLoadUser() : String {
    val isLoadSuccess = true // 加载成功，和，加载失败，的标记

    // 开启异步线程，去请求加载服务器的数据集
    withContext(Dispatchers.IO) {
        delay(3000L) // 模拟请求服务器 所造成的耗时
    }

    if (isLoadSuccess) {
        return "加载到[用户数据]信息集"
    } else {
        return "加载[用户数据],加载失败,服务器宕机了"
    }
}
private fun requestLoadUser(continuation: Continuation<String>) {
    val isLoadSuccess = true // 加载成功，和，加载失败，的标记

    // 开启异步线程，去请求加载服务器的数据集
    thread {
        Thread.sleep(3000L) // 模拟请求服务器 所造成的耗时

        if (isLoadSuccess) {
            continuation.resume("加载到[用户数据]信息集")
        } else {
            continuation.resumeWithException(Throwable("加载[用户数据],加载失败,服务器宕机了"))
        }
    }
}

/*
 * 请求加载[用户资产数据]
 */
private suspend fun requestLoadUserAssets() : String {
    val isLoadSuccess = true // 加载成功，和，加载失败，的标记

    // 开启异步线程，去请求加载服务器的数据集
    withContext(Dispatchers.IO) {
        delay(4000L) // 模拟请求服务器 所造成的耗时
    }

    if (isLoadSuccess) {
        return "加载到[用户资产数据]信息集"
    } else {
        return "加载[用户资产数据],加载失败,服务器宕机了"
    }
}

/*
 * 请求加载[用户资产详情数据]
 */
private suspend fun requestLoadUserAssetsDetails() : String {
    val isLoadSuccess = true // 加载成功，和，加载失败，的标记

    // 开启异步线程，去请求加载服务器的数据集
    withContext(Dispatchers.IO) {
        delay(5000L) // 模拟请求服务器 所造成的耗时
    }

    if (isLoadSuccess) {
        return "加载到[用户资产详情数据]信息集"
    } else {
        return "加载[用户资产详情数据],加载失败,服务器宕机了"
    }
}

class MainActivity8 : AppCompatActivity() {
    var mProgressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun startRequest(view: View) {
        mProgressDialog = ProgressDialog(this)
        mProgressDialog?.setTitle("请求服务器中...")
        mProgressDialog?.show()

        GlobalScope.launch(context = Dispatchers.Main) { // 默认是Default异步线程
            // TODO >>>>>>>>>>>>>>>>>>>>>>>>  t85：协程背后状态机原理 ① >>>>>>>>>>>>>>>>>>>>>>>>
            // TODO 先执行 异步请求1
            var serverResponseInfo = requestLoadUser()
            tv?.text = serverResponseInfo // 更新UI
            tv?.setTextColor(Color.GREEN) // 更新UI

            // TODO 更新UI完成后，再去执行 异步请求2
            serverResponseInfo = requestLoadUserAssets()
            tv?.text = serverResponseInfo // 更新UI
            tv?.setTextColor(Color.BLUE) // 更新UI

            // TODO 更新UI完成后，再去执行 异步请求3
            serverResponseInfo = requestLoadUserAssetsDetails()
            tv?.text = serverResponseInfo // 更新UI
            mProgressDialog?.dismiss() // 更新UI
            tv?.setTextColor(Color.RED) // 更新UI
        }

        // TODO >>>>>>>>>>>>>>>>>>>>>>>>  t85：协程背后状态机原理 ② >>>>>>>>>>>>>>>>>>>>>>>>
        requestLoadUser(DispatchedContinuation(object : Continuation<String> {
            override val context: CoroutineContext
                get() = EmptyCoroutineContext // 在这里我们破坏了协程的规则，所以这句话不生效

            override fun resumeWith(result: Result<String>) {
                var serverResponseInfo = result.getOrThrow()
                tv?.text = serverResponseInfo // 更新UI
                tv?.setTextColor(Color.GREEN) // 更新UI

                suspend {
                    // TODO 更新UI完成后，再去执行 异步请求2
                    requestLoadUserAssets()
                }.startCoroutine(object : Continuation<String> {
                    override val context: CoroutineContext
                        get() = Dispatchers.Main // 生效的  因为拦截器intercepted 会调用  Dispatchers.Main 切换到Main

                    override fun resumeWith(result: Result<String>) {
                        var serverResponseInfo = result.getOrThrow()
                        tv?.text = serverResponseInfo // 更新UI
                        tv?.setTextColor(Color.BLUE) // 更新UI

                        suspend {
                            // TODO 更新UI完成后，再去执行 异步请求3
                            requestLoadUserAssetsDetails() // 最后一行Lambda返回
                        }.createCoroutine(object: Continuation<String> {
                            override val context: CoroutineContext
                                get() = Dispatchers.Main // 生效的  因为拦截器intercepted 会调用  Dispatchers.Main 切换到Main

                            override fun resumeWith(result: Result<String>) {
                                var serverResponseInfo = result.getOrThrow()
                                tv?.text = serverResponseInfo // 更新UI
                                mProgressDialog?.dismiss() // 更新UI
                                tv?.setTextColor(Color.RED) // 更新UI
                            }
                        }).resumeWith(Result.success(Unit))
                    }
                })
            }
        }, HandlerDispatcher))
    }

    // TODO >>>>>>>>>>>>>>>>>>>>>>>>  t85：协程背后状态机原理 ③ >>>>>>>>>>>>>>>>>>>>>>>>
    suspend fun showCoroutine() {
        println("开始执行啦")

        val user = requestLoadUser()
        Toast.makeText(this, "更新UI:$user", Toast.LENGTH_SHORT).show()

        val userAssets = requestLoadUserAssets()
        Toast.makeText(this, "更新UI:$userAssets", Toast.LENGTH_SHORT).show()

        val userAssetsDetails = requestLoadUserAssetsDetails()
        Toast.makeText(this, "更新UI:$userAssetsDetails", Toast.LENGTH_SHORT).show()
    }
    // TODO >>>>>>>>>>>>>>>>>>>>>>>>  t85：协程背后状态机原理 ④ >>>>>>>>>>>>>>>>>>>>>>>>
    // 大概可以这样理解 ，简化版
    /*fun showCoroutine2(continuation: Continuation<String>) {
        println("开始执行啦")

        val user = requestLoadUser(continuation)
        Toast.makeText(this, "更新UI:$user", Toast.LENGTH_SHORT).show()

        val userAssets = requestLoadUserAssets(continuation)
        Toast.makeText(this, "更新UI:$userAssets", Toast.LENGTH_SHORT).show()

        val userAssetsDetails = requestLoadUserAssetsDetails(continuation)
        Toast.makeText(this, "更新UI:$userAssetsDetails", Toast.LENGTH_SHORT).show()
    }*/
}

fun testDerry() : Int {
    println("testDerry 1")
    thread {
        Thread.sleep(5000L)
        println("thread invokeSuspend 调用了 ")
    }
    println("testDerry 2")
    return 200 // 模拟立刻马上返回挂起标记
}

fun main() {
    println(testDerry())
}