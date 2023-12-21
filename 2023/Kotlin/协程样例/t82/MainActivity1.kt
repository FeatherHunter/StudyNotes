package com.derry.kt_coroutines.t82

import android.app.ProgressDialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.derry.kt_coroutines.R
import kotlinx.android.synthetic.main.activity_main.*

/**
 * 模拟请求服务器后 响应结果信息
 */
interface ResponseCallback {

    /**
     * 请求服务器后 登录成功
     * @param serverResponseInfo 登录成功后的信息集
     */
    fun responseSuccess(serverResponseInfo : String)

    /**
     * 请求服务器后 登录失败/登录错误
     * @param serverResponseErrorMsg 登录失败后的简述
     */
    fun responseError(serverResponseErrorMsg: String)
}

/**
 * 请求加载[用户数据]
 * @param responseCallback 请求加载[用户数据]后 回调给外界的接口
 */
private fun requestLoadUser(responseCallback: ResponseCallback) {
    val isLoadSuccess = true // 加载成功，和，加载失败，的标记

    // 开启异步线程，去请求加载服务器的数据集
    object:Thread() {
        override fun run() {
            super.run()
            try {
                sleep(3000L) // 模拟请求服务器 所造成的耗时

                if (isLoadSuccess) {
                    responseCallback.responseSuccess("加载到[用户数据]信息集")
                } else {
                    responseCallback.responseError("加载[用户数据],加载失败,服务器宕机了")
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }.start()
}

/**
 * 请求加载[用户资产数据]
 * @param responseCallback 请求加载[用户资产数据]后 回调给外界的接口
 */
private fun requestLoadUserAssets(responseCallback: ResponseCallback) {
    val isLoadSuccess = true // 加载成功，和，加载失败，的标记

    // 开启异步线程，去请求加载服务器的数据集
    object:Thread() {
        override fun run() {
            super.run()
            try {
                sleep(3000L) // 模拟请求服务器 所造成的耗时

                if (isLoadSuccess) {
                    responseCallback.responseSuccess("加载到[用户资产数据]信息集")
                } else {
                    responseCallback.responseError("加载[用户资产数据],加载失败,服务器宕机了")
                }

            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }.start()
}

/**
 * 请求加载[用户资产详情数据]
 * @param responseCallback 请求加载[用户资产详情数据]后 回调给外界的接口
 */
private fun requestLoadUserAssetsDetails(responseCallback: ResponseCallback) {
    val isLoadSuccess = true // 加载成功，和，加载失败，的标记

    // 开启异步线程，去请求加载服务器的数据集
    object:Thread() {
        override fun run() {
            super.run()
            try {
                sleep(3000L) // 模拟请求服务器 所造成的耗时

                if (isLoadSuccess) {
                    responseCallback.responseSuccess("加载到[用户资产详情数据]信息集")
                } else {
                    responseCallback.responseError("加载[用户资产详情数据],加载失败,服务器宕机了")
                }

            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }.start()
}

class MainActivity1 : AppCompatActivity() {

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

        // TODO 先执行 异步请求1
        requestLoadUser(object : ResponseCallback {
            override fun responseSuccess(serverResponseInfo: String) {
                // 从异步线程 切换成 Android主线程1 （注意：本次所有代码 是Kotlin的简写，如果是Java代码量更多）
                val handler : Handler = object : Handler(Looper.getMainLooper()) {
                    override fun handleMessage(msg: Message) {
                        super.handleMessage(msg)
                        tv?.text = serverResponseInfo // 更新UI
                        tv?.setTextColor(Color.GREEN) // 更新UI

                        // TODO 更新UI完成后，再去执行 异步请求2
                        requestLoadUserAssets(object : ResponseCallback {
                            override fun responseSuccess(serverResponseInfo: String) {
                                // 从异步线程 切换成 Android主线程2
                                val handler : Handler = object : Handler(Looper.getMainLooper()) {
                                    override fun handleMessage(msg: Message) {
                                        super.handleMessage(msg)
                                        tv?.text = serverResponseInfo // 更新UI
                                        tv?.setTextColor(Color.BLUE) // 更新UI

                                        // TODO 更新UI完成后，再去执行 异步请求3
                                        requestLoadUserAssetsDetails(object : ResponseCallback{
                                            override fun responseSuccess(serverResponseInfo: String) {
                                                // 从异步线程 切换成 Android主线程3
                                                val handler : Handler = object : Handler(Looper.getMainLooper()) {
                                                    override fun handleMessage(msg: Message) {
                                                        super.handleMessage(msg)
                                                        tv?.text = serverResponseInfo // 更新UI
                                                        mProgressDialog?.dismiss() // 更新UI
                                                        tv?.setTextColor(Color.RED) // 更新UI
                                                    }
                                                }
                                                handler.sendEmptyMessage(0)
                                            }

                                            override fun responseError(serverResponseErrorMsg: String) {
                                                // TODO 失败的情况，就不考虑了，不然代码就太多了
                                                //  省略很多行代码...
                                            }
                                        })
                                    }
                                }
                                handler.sendEmptyMessage(0)
                            }

                            override fun responseError(serverResponseErrorMsg: String) {
                                // TODO 失败的情况，就不考虑了，不然代码就太多了
                                //  省略很多行代码...
                            }
                        })
                    }
                }
                handler.sendEmptyMessage(0)
            }

            override fun responseError(serverResponseErrorMsg: String) {
                // TODO 失败的情况，就不考虑了，不然代码就太多了
                //  省略很多行代码...
            }
        })
    }
}