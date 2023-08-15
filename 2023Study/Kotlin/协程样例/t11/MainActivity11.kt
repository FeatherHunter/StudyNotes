package com.derry.kt_coroutines.t11

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.derry.kt_coroutines.R
import com.derry.kt_coroutines.t01_t04.api.APIClient
import com.derry.kt_coroutines.t01_t04.api.WanAndroidAPI
import com.derry.kt_coroutines.t01_t04.entity.LoginRegisterResponse
import com.xiangxue.kotlinproject.entity.LoginRegisterResponseWrapper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity11 : AppCompatActivity(), CoroutineScope by MainScope() {

    private val TAG = "Derry"
    private var mProgressDialog: ProgressDialog? = null

    // private val mainScope = MainScope() // 简单的工厂设计模式

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 当用户点击按钮 点击事件
        bt.setOnClickListener {
            launch(Dispatchers.Main) {
                println(3)
                bt.text = "32行的协程体中 被挂起了"
                val responseResult = requestLogin("Derry-vip", "123456")
                bt.text = "33行的协程体中 的挂起 被恢复了"
                tv.text = "请求响应的成果是:$responseResult"
                Toast.makeText(this@MainActivity11, "请求响应的成果是:$responseResult", Toast.LENGTH_SHORT).show()
                delay(6000L)
                mProgressDialog ?.dismiss()
                tv.text = "恢复全部完毕"
            }

            // Derry告诉大家， 非阻塞式的原因
            // 例如：就算 上面的 协程块 耗时30封装，也不可能，影响我弹框，因为 非阻塞式的原因
            println(1)
            mProgressDialog = ProgressDialog(this)
            mProgressDialog ?.setTitle("服务器请求中...")
            tv.text = "服务器请求中..."
            mProgressDialog ?.show()
            println(2)
        }
    }

    private suspend fun requestLogin(userName: String ,userPwd: String) : LoginRegisterResponseWrapper<LoginRegisterResponse> {
        // 校验 用户名 和 密码 检查工作
        // ...
        if (userName.isEmpty() || userPwd.isEmpty()) {
            throw KotlinNullPointerException("userName or userPwd is null")
        }

        val responseResult = withContext(Dispatchers.IO) {
            delay(10000L)
            val responseResult
            = APIClient.instance.instanceRetrofit(WanAndroidAPI::class.java)
                .loginActionCoroutine(userName, userPwd)
            responseResult
        }

        return responseResult
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    /*override val coroutineContext: CoroutineContext
        get() = MainScope().coroutineContext*/
}