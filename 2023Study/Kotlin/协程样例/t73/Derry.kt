package com.derry.kt_coroutines.t73

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge

data class Home(val info1: String, val info2: String)

data class HomeRequestResponseResultData(val code: Int, val msg: String, val home: Home)

// 请求本地加载首页数据
fun CoroutineScope.getHomeLocalData(userName: String) = async (Dispatchers.IO) {
    delay(3000)
    Home("数据1...", "数据1...")
}

// 请求网络服务器加载首页数据
fun CoroutineScope.getHomeRemoteData(userName: String) = async (Dispatchers.IO) {
    delay(6000)
    Home("数据3...", "数据4...")
}

// TODO 73-Android协程手写Flow合并
@OptIn(ExperimentalCoroutinesApi::class)
fun main() = runBlocking<Unit> {
    // 1.把多个函数 拿过来
    // 2.组装成协程
    // 3.包装成FLow
    // 4.Flow合并 得到 结果

    coroutineScope {
        val r = listOf(::getHomeLocalData, ::getHomeRemoteData)
            .map {
                it.call("Derry用户")
            }.map {
                flow { emit(it.await()) }
            }
        val r2 = r.merge()
        r2.collect { println(it) }
    }
}