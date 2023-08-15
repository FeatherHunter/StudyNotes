package com.derry.kt_coroutines.t01_t04.ui

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.derry.kt_coroutines.R
import com.derry.kt_coroutines.t01_t04.api.APIClient
import com.derry.kt_coroutines.t01_t04.api.WanAndroidAPI
import com.derry.kt_coroutines.t01_t04.entity.LoginRegisterResponse
import com.xiangxue.kotlinproject.entity.LoginRegisterResponseWrapper
import kotlinx.android.synthetic.main.activity_main.*

// TODO  01-Android传统异步请求服务器写法 - 演示弊端
class MainActivity1 : AppCompatActivity() {

    private val TAG = "Derry"
    var mProgressDialog: ProgressDialog? = null

    // 第二大步骤：主线程 更新UI（注意：本次所有代码 是Kotlin的简写，如果是Java代码量更多）
    val mHandler = Handler(Looper.getMainLooper()) {

        val result =  it.obj as LoginRegisterResponseWrapper<LoginRegisterResponse>
        Log.d(TAG, "errorMsg: ${result.data}")
        tv.text = result.data.toString() // 属于UI操作

        mProgressDialog?.dismiss() // 隐藏加载框  属于UI操作

        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun startRequest(view: View) {
        mProgressDialog = ProgressDialog(this)
        mProgressDialog?.setTitle("请求服务器中...")
        mProgressDialog?.show()

        // 第一大步骤：异步线程请求服务器 & 把服务器响应结果转发给Handler
        object : Thread() {
            override fun run() {
                super.run()

                Thread.sleep(2000)

                val loginResult
                = APIClient.instance.instanceRetrofit(WanAndroidAPI::class.java)
                    .loginAction("Derry-vip", "123456")

                val result
                = loginResult.execute().body()

                // 此时：这个时候，是异步线程，能更新UI? 尽量不要这样做
                // 我们使用Handler切换到 安卓的主线程 更新UI
                val msg = mHandler.obtainMessage()
                msg.obj = result
                mHandler.sendMessage(msg)
            }
        }.start()
    }


}