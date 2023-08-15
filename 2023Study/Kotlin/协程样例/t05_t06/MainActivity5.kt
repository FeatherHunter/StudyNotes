package com.derry.kt_coroutines.t05_t06

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.derry.kt_coroutines.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity5 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bt.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) { // 在Android平台中，默认的 Default 异步线程
                Log.i("test", "遇上 requestLogin 协程块 19 行挂起") // 第一步打印
                requestLogin()
                Log.i("test", "requestLogin执行完成后，就开始恢复了") // 第五步打印

                for (i in 1..20) {
                    Log.i("test", "requestLogin执行完成后，恢复继续往下执行中... i:$i") // 第六步打印
                }
            }
        }
    }

    private suspend fun requestLogin() {
        // 用户名 和 密码 的 校验工作 等等 优化工作
        // ...  200行代码

        Log.d("test", "遇上 loginAction 协程块 34 行 被挂起 ") // 第二步打印
        val responseBeanDataResult = loginAction()
        Log.d("test", "遇上 【responseBeanDataResult 左边变量的时候】 就已经被恢复了 ") // 第三步打印

        Log.d("test", "requestLogin responseBeanDataResult:$responseBeanDataResult") // 第四步打印
    }

    private suspend fun loginAction() = withContext(Dispatchers.IO) {
        delay(8000L)
        "模拟服务器请求完成 数据data{}"
    }
}