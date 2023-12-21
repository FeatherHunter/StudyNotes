package com.derry.kt_coroutines.t08

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.derry.kt_coroutines.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.createCoroutine
import kotlin.coroutines.resume

class MainActivity8 : AppCompatActivity() {

    // 官方框架层
    /*suspend fun fun01() : Float {
        delay(10000L)
        return 2646456.6f
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bt.setOnClickListener {

            // TODO 官方框架层 -- Retrofit在写需求
            /*GlobalScope.launch(Dispatchers.Main) {
                val r = fun01()
                println(r)
            }*/


            // TODO 语言基础层 动态代理，反射，注解 最基础的API   写需求
            val suspendFun : suspend () -> Float = suspend {
                delay(10000L)
                2646456.6f
            }

            // 参数里面的  Continuation<Float> : suspend 协程体 完成时  成果2646456.6f反馈 或者 异常反馈
            // 外面接收的  Continuation<Unit>  : suspend 协程体 的 管理者 协程的本地 是不会激活  (suspend 协程体)
            val continuation : Continuation<Unit> = suspendFun.createCoroutine(object: Continuation<Float>{
                override val context: CoroutineContext
                    get() = Dispatchers.Main

                override fun resumeWith(result: Result<Float>) {
                    println("成果的打印 resumeWith: result:${result.getOrNull()}")
                }
            })

            // continuation 负责人管理者
            continuation.resume(Unit)
            // continuation.resumeWith(Result.success(Unit))

            // 上面的 基础层代码，到底有几次调用 resume ?
            // 第一步：continuation 负责人管理者  去 激活  （suspend 协程体）  一次Resume
            // 第二步：（suspend 协程体） 开始执行 耗时delay(10000L)，十秒钟过后  ，把成果 给 参数里面的  Continuation<Float>  一次Resume
            // 一次简单执行，有两次Resume
        }
    }
}