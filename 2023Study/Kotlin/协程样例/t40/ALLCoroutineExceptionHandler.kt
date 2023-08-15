package com.derry.kt_coroutines.t40

import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlin.coroutines.CoroutineContext

// 整个项目，协程发生了异常，此类会被调用，然后可以上报异常给服务器
class ALLCoroutineExceptionHandler : CoroutineExceptionHandler {
    override val key= CoroutineExceptionHandler

    override fun handleException(context: CoroutineContext, exception: Throwable) {
        // 项目统一 可以上报异常给服务器 上报给 友盟统计
        // ...
        // 省略 1000行代码...

        Log.d("Derry", "全局异常捕获 handleException e:$exception")
    }
}