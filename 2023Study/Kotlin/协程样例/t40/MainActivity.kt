package com.derry.kt_coroutines.t40

import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.derry.kt_coroutines.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.bt
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.coroutines.*

// TODO 40-Android协程异常捕获解决App奔溃
class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        /*bt.setOnClickListener {

            val mProgressDialog = ProgressDialog(this@MainActivity)
            mProgressDialog.setTitle("计算中...")

            val exception = CoroutineExceptionHandler {_, e ->
                Toast.makeText(this, "您输入的内容为空，或，您输入第二项是0 0不能除以谁", Toast.LENGTH_SHORT).show()
                if (mProgressDialog.isShowing)
                    mProgressDialog.dismiss()
            }

            // 父协程
            launch(Dispatchers.Main + exception) {
                val number1 = et_number1.text.toString().toInt()
                val number2 = et_number2.text.toString().toInt()

                mProgressDialog.show()

                // 子协程
                val d = async(Dispatchers.IO) {
                    Thread.sleep(3000) // 网络请求，大型计算耗时操作 ，消耗十秒钟
                    val result = number1 / number2
                    result
                }
                
                // 开心的更新UI
                Toast.makeText(this@MainActivity, "计算的结果是:${d.await()}", Toast.LENGTH_SHORT).show()
                tv_result.text = d.await().toString()
                mProgressDialog.setTitle("计算成功，结果是:${tv_result.text.toString()}")
                GlobalScope.launch(Dispatchers.Main) {
                    delay(1000) // 挂起 与 恢复 实现的 非阻塞式
                    mProgressDialog.dismiss()
                }
            }
        }*/


        bt.setOnClickListener {

            val mProgressDialog = ProgressDialog(this@MainActivity)
            mProgressDialog.setTitle("计算中...")

            // 父协程
            launch(Dispatchers.Main) {
                val number1 = et_number1.text.toString().toInt()
                val number2 = et_number2.text.toString().toInt()

                mProgressDialog.show()

                // 子协程
                val d = async(Dispatchers.IO) {
                    Thread.sleep(3000) // 网络请求，大型计算耗时操作 ，消耗十秒钟
                    val result = number1 / number2
                    result
                }

                // 开心的更新UI
                Toast.makeText(this@MainActivity, "计算的结果是:${d.await()}", Toast.LENGTH_SHORT).show()
                tv_result.text = d.await().toString()
                mProgressDialog.setTitle("计算成功，结果是:${tv_result.text.toString()}")
                GlobalScope.launch(Dispatchers.Main) {
                    delay(1000) // 挂起 与 恢复 实现的 非阻塞式
                    mProgressDialog.dismiss()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}