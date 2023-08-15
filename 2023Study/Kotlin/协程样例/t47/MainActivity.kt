package com.derry.kt_coroutines.t47

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.derry.kt_coroutines.R
import kotlinx.android.synthetic.main.activity_main3.*
import kotlinx.android.synthetic.main.activity_main3.bt
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn

// 47-Android企业级Flow项目的开发案例编写
class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        bt.setOnClickListener {
            launch (Dispatchers.Main) {
                println("bt.setOnClickListener thread:${Thread.currentThread().name}")
                NetworkRequest.uploadRequestAction().flowOn(Dispatchers.IO).collect { tv.text = "正在上传服务器中... 进度是:$it" ; pb.progress = it }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}