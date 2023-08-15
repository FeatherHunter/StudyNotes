package com.derry.kt_coroutines.t07

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.derry.kt_coroutines.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity7 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bt.setOnClickListener {
            // 阻塞的演示
            /*Thread.sleep(20000)
            Toast.makeText(this@MainActivity7, "一号人物过安检", Toast.LENGTH_SHORT).show()

            Toast.makeText(this@MainActivity7, "二号人物过安检", Toast.LENGTH_SHORT).show()

            Toast.makeText(this@MainActivity7, "三号人物过安检", Toast.LENGTH_SHORT).show()

            Toast.makeText(this@MainActivity7, "四号人物过安检", Toast.LENGTH_SHORT).show()*/



            // 挂起 -- 非阻塞
            GlobalScope.launch { // 在Android平台中，默认的 Default 异步线程
                delay(20000) // 此时被挂起 执行耗时20秒
                // 执行耗时20秒后，开始恢复，执行 33 开始 往下走 恢复
                runOnUiThread {
                    Toast.makeText(this@MainActivity7, "20秒后 睡醒了 恢复了 一号人物过安检", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            Toast.makeText(this@MainActivity7, "二号人物过安检", Toast.LENGTH_SHORT).show()

            Toast.makeText(this@MainActivity7, "三号人物过安检", Toast.LENGTH_SHORT).show()

            Toast.makeText(this@MainActivity7, "四号人物过安检", Toast.LENGTH_SHORT).show()
        }
    }
}