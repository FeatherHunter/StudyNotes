package com.derry.kt_coroutines.t70

import kotlinx.coroutines.*
import kotlinx.coroutines.selects.select

data class Home(val info1: String, val info2: String)

data class HomeRequestResponseResultData(val code: Int, val msg: String, val home: Home)

// 请求本地加载首页数据
fun CoroutineScope.getHomeLocalData() = async (Dispatchers.IO) {
    delay(3000)
    Home("数据1...", "数据1...")
}

// 请求网络服务器加载首页数据
 fun CoroutineScope.getHomeRemoteData() = async (Dispatchers.IO) {
    delay(6000)
    Home("数据3...", "数据4...")
}

// TODO  70-Android协程select择优选择数据
@OptIn(ObsoleteCoroutinesApi::class)
fun main() = runBlocking<Unit> {

    launch {
        val localRequestAction = getHomeLocalData()
        val remoteRequestAction = getHomeRemoteData()

        val resultResponse = select<HomeRequestResponseResultData> {
            localRequestAction.onAwait {
                // 做校验 工作
                // ...
                // 省略1000行代码
                HomeRequestResponseResultData(200, "恭喜你，请求成功", it) // 最后一行作为返回值
            }

            remoteRequestAction.onAwait {
                // 做校验 工作
                // ...
                // 省略1000行代码
                HomeRequestResponseResultData(200, "恭喜你，请求成功", it) // 最后一行作为返回值
            }
        }
        println("resultResponse:$resultResponse")
    }

}