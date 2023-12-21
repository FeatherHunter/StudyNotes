package com.derry.kt_coroutines.t09

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.derry.kt_coroutines.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity9 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bt.setOnClickListener {

            GlobalScope.launch(Dispatchers.Main) { // 在Android平台上 默认是 Default 异步线程
                // 在安卓平台上，可以拿到 Dispatchers.Main，所以没有任何问题
            }

        }
    }
}