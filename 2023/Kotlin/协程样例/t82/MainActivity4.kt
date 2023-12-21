package com.derry.kt_coroutines.t82

import android.app.ProgressDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.derry.kt_coroutines.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.lang.Exception
import java.lang.Thread.sleep
import kotlin.concurrent.thread
import kotlin.coroutines.*

// 用于给Java测试的 suspend挂起函数
suspend fun suspendFunction(){}

/**
 * 请求加载[用户数据]
 */
private fun requestLoadUser(continuation: Continuation<String>) {
        val isLoadSuccess = true // 加载成功，和，加载失败，的标记

        // 开启异步线程，去请求加载服务器的数据集
        thread {
            try {
                sleep(3000L) // 模拟请求服务器 所造成的耗时

                if (isLoadSuccess) {
                    continuation.resume("加载到[用户数据]信息集")
                } else {
                    continuation.resumeWithException(Throwable("加载[用户数据],加载失败,服务器宕机了"))
                }
            } catch (e: Exception) {
                continuation.resumeWithException(e)
                e.printStackTrace()
            }
        }
    }

/**
 * 请求加载[用户资产数据]
 */
private fun requestLoadUserAssets(continuation: Continuation<String>) {
        val isLoadSuccess = true // 加载成功，和，加载失败，的标记

        // 开启异步线程，去请求加载服务器的数据集
        thread {
            try {
                sleep(3000L) // 模拟请求服务器 所造成的耗时

                if (isLoadSuccess) {
                    continuation.resume("加载到[用户资产数据]信息集")
                } else {
                    continuation.resumeWithException(Throwable("加载[用户资产数据],加载失败,服务器宕机了"))
                }
            } catch (e: Exception) {
                continuation.resumeWithException(e)
                e.printStackTrace()
            }
        }
    }

/**
 * 请求加载[用户资产详情数据]
 */
private fun requestLoadUserAssetsDetails(continuation: Continuation<String>) {
        val isLoadSuccess = true // 加载成功，和，加载失败，的标记

        // 开启异步线程，去请求加载服务器的数据集
        thread {
            try {
                sleep(3000L) // 模拟请求服务器 所造成的耗时

                if (isLoadSuccess) {
                    continuation.resume("加载到[用户资产详情数据]信息集")
                } else {
                    continuation.resumeWithException(Throwable("加载[用户资产详情数据],加载失败,服务器宕机了"))
                }
            } catch (e: Exception) {
                continuation.resumeWithException(e)
                e.printStackTrace()
            }
        }
    }

// TODO 04-Android协程解决复杂业务的痛点
class MainActivity4 : AppCompatActivity() {

    private val TAG = "Derry"
    var mProgressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun startRequest(view: View) {
        mProgressDialog = ProgressDialog(this)
        mProgressDialog?.setTitle("请求服务器中...")
        mProgressDialog?.show()

        // TODO 不在使用协程官方提供的 GlobalScope.launch { Continuation }了 ， 而是自定义 随意发挥 Continuation(意味着 破坏协程流程，自己来玩)

        // TODO 先执行 异步请求1
        requestLoadUser(object : Continuation<String> {
            override val context: CoroutineContext
                get() = EmptyCoroutineContext // 本来这个CoroutineContext 可以用来切换线程的，但是现在报废了

            override fun resumeWith(result: Result<String>) {
                println("先执行 异步请求1 resumeWith thread:${Thread.currentThread().name}")

                runOnUiThread { // 手动切回主线程
                    val serverResponseInfo = result.getOrThrow()
                    tv?.text = serverResponseInfo // 更新UI
                    tv?.setTextColor(Color.GREEN) // 更新UI

                    // TODO 更新UI完成后，再去执行 异步请求2
                    requestLoadUserAssets(object : Continuation<String> {
                        override val context: CoroutineContext
                            get() = EmptyCoroutineContext // 本来这个CoroutineContext 可以用来切换线程的，但是现在报废了

                        override fun resumeWith(result: Result<String>) {
                            runOnUiThread { // 手动切回主线程
                                val serverResponseInfo = result.getOrThrow()
                                tv?.text = serverResponseInfo // 更新UI
                                tv?.setTextColor(Color.BLUE) // 更新UI

                                // TODO 更新UI完成后，再去执行 异步请求3
                                requestLoadUserAssetsDetails(object : Continuation<String> {
                                    override val context: CoroutineContext
                                        get() = EmptyCoroutineContext // 本来这个CoroutineContext 可以用来切换线程的，但是现在报废了

                                    override fun resumeWith(result: Result<String>) {
                                        runOnUiThread { // 手动切回主线程
                                            val serverResponseInfo = result.getOrThrow()
                                            tv?.text = serverResponseInfo // 更新UI
                                            mProgressDialog?.dismiss() // 更新UI  隐藏加载框
                                            tv?.setTextColor(Color.RED) // 更新UI
                                        }
                                    }
                                })
                            }
                        }
                    })
                }
            }
        })
    }
}