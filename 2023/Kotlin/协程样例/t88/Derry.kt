package com.derry.kt_coroutines.t88

import kotlinx.coroutines.*
import java.lang.Exception
import kotlin.coroutines.*

open class ResponseBean
data class Successful(val code: Int, val msg: String, val data: String) : ResponseBean()
data class Error(val code: Int, val msg: String, val data: String) : ResponseBean()

suspend fun requestLogin(userName: String , userPwd: String) : ResponseBean
    = suspendCancellableCoroutine { continuation: CancellableContinuation<ResponseBean> ->

    continuation.invokeOnCancellation {
        println("本次协程执行完毕，做清理回收工作 ... $it")
        /*xxx.close()
        xxx.release()
        xxx.清理工作*/
        // ...
    }

    try {
        if ("Derry" == userName && "123456" == userPwd) {
            continuation.resume(Successful(200, "恭喜，请求登录成功", "data{success}"))
        } else {
            continuation.resume(Error(404, "不恭喜，登录失败了", "data{error}"))
        }
    } catch (e:Exception) {
        continuation.resumeWithException(Throwable("请求发送了异常:$e"))
    } finally {
        continuation.cancel(Throwable("cancel"))
    }
}

fun main() = runBlocking <Unit> {

    /*suspend {
        requestLogin("Derry", "123456")
    }.createCoroutine(object: Continuation<ResponseBean> {
        override val context: CoroutineContext
            get() = EmptyCoroutineContext

        override fun resumeWith(result: Result<ResponseBean>) {
            println("请求结果是:${result.getOrNull()}")
        }
    }).resume(Unit)*/


    GlobalScope.launch {
        val result = requestLogin("Derry", "123456")
        println("请求结果是:${result}")
    }.join()

}