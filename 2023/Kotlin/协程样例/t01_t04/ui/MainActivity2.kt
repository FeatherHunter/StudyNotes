package com.derry.kt_coroutines.t01_t04.ui

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.derry.kt_coroutines.R
import com.derry.kt_coroutines.t01_t04.api.APIClient
import com.derry.kt_coroutines.t01_t04.api.WanAndroidAPI
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

// TODO  02-Android协程请求服务器写法
class MainActivity2 : AppCompatActivity() {

    private val TAG = "Derry"
    var mProgressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun startRequest(v: View) {
        mProgressDialog = ProgressDialog(this)
        mProgressDialog?.setTitle("请求服务器中...")
        mProgressDialog?.show()

        GlobalScope.launch (Dispatchers.Main) { // 如果是在Android上，他默认是异步线程 上的 协程块
            // 我们在安卓主线程上，运行耗时操作，居然没有任何问题？ 答：suspend的两件大事
            val result = APIClient.instance.instanceRetrofit(WanAndroidAPI::class.java)
                .loginActionCoroutine("Derry-vip", "123456")

            // 更新UI
            Log.d(TAG, "errorMsg: ${result.data}")
            tv.text = result.data.toString() // 属于UI操作
            mProgressDialog?.dismiss() // 隐藏加载框  属于UI操作
        }
    }

}