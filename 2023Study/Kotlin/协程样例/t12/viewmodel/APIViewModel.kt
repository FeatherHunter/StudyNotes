package com.derry.kt_coroutines.t12.viewmodel

import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.derry.kt_coroutines.t01_t04.entity.LoginRegisterResponse
import com.derry.kt_coroutines.t12.repository.APIRepository
import com.xiangxue.kotlinproject.entity.LoginRegisterResponseWrapper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch
import java.util.logging.Handler

class APIViewModel : ViewModel() {
    var userLiveData = MutableLiveData<LoginRegisterResponseWrapper<LoginRegisterResponse>>()

    fun requestLogin(username: String, userpwd: String) {
        viewModelScope.launch /*(Dispatchers.Main)*/ {  // viewModelScope是main线程，这个是特殊点，其他的默认是Default
            // 左边：主线程                  右边：异步线程
            userLiveData.value     =      APIRepository().requestLogin(username, userpwd)
        }
    }

    // 伪代码 假设没有协程
    fun requestLoginTest(username: String, userpwd: String) {
        object: Thread() {
            override fun run() {
                super.run()

                val result =
                    APIRepository().requestLoginTest(username, userpwd)

                // 此时：这个时候，是异步线程，能更新UI? 尽量不要这样做
                // 我们使用Handler切换到 安卓的主线程 更新UI
                val msg = mHandler.obtainMessage()
                msg.obj = result!!
                mHandler.sendMessage(msg)
            }
        }.start()
    }

    // Handler切换到 安卓的主线程 更新UI
    val mHandler = android.os.Handler(Looper.getMainLooper()) {

        val result = it.obj as LoginRegisterResponseWrapper<LoginRegisterResponse>
        // 安卓的主线程 更新UI
        userLiveData.value = result // 属于UI操作
        false
    }
}