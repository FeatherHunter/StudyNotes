package com.derry.kt_coroutines.t17

import kotlinx.coroutines.*

// 先 [请求 用户数据]
private suspend fun requestLoadUser() = withContext(Dispatchers.IO) { delay(1000 * 10) }

// 在 [请求 用户资产数据]
private suspend fun requestLoadUserAssets() = withContext(Dispatchers.IO) { delay(1000 * 6) }

// 在 [请求 用户资产详情数据]
private suspend fun requestLoadUserAssetsDetails() = withContext(Dispatchers.IO) { delay(1000 * 3) }

// TODO t17 -- 17-Android协程的作用域构建器
fun main() = runBlocking<Unit> {
    // TODO this CoroutineScope 为了管理协程（结构化并发）
    /*launch {

    }

    async {

    }

    GlobalScope.launch {

    }

    withContext(Dispatchers.IO) {

    }*/

    // Derry 1
    // TODO coroutineScope 和 runBlocking 很相似（他们两者都会等待 协程体里面的任务全部执行完成，才会结束）
    //  runBlocking阻塞 main thread 来等待 (协程体里面的任务全部执行完成，才会结束)
    //  coroutineScope挂起 直到 (协程体里面的任务全部执行完成，才会恢复)
    // TODO coroutineScope 协程体里面，只有有一个协程失败，其他的兄弟协程，全部跟着失败
    /*coroutineScope {

        // 请求 用户数据  公司项目的网络请求业务
        launch {
            println("requestLoadUser start>>>") // 1
            requestLoadUser()
            println("requestLoadUser successful<<<") // 6
        }

        // 请求 用户资产数据  公司项目的网络请求业务
        launch {
            println("requestLoadUserAssets start>>>") // 2
            requestLoadUserAssets()
            println("requestLoadUserAssets successful<<<") // 5
        }

        // 请求 用户资产详情数据  公司项目的网络请求业务
        launch {
            println("requestLoadUserAssetsDetails start>>>") // 3
            requestLoadUserAssetsDetails()
            println("requestLoadUserAssetsDetails successful<<<") // 4
            throw KotlinNullPointerException("请求 用户资产详情数据 失败了 发生了错误")
        }

        // 面试题1：三个请求，同时发起，如果有一个请求失败了，其他请求必须全部跟着结束（Java 事务）？
        // 答：可以用 协程的 结构化并发，解决方式 coroutineScope{} 包裹所有协程 网络请求的地方，只要有一个失败，其他兄弟协程全部会跟着结束

        // Derry 1
    }*/

    // TODO supervisorScope 协程体里面，有一个协程失败，其他的兄弟协程，不会有任何影响
    supervisorScope {

        // 请求 用户数据  公司项目的网络请求业务
        launch {
            println("requestLoadUser start>>>") // 1
            requestLoadUser()
            println("requestLoadUser successful<<<") // 6
        }

        // 请求 用户资产数据  公司项目的网络请求业务
        launch {
            println("requestLoadUserAssets start>>>") // 2
            requestLoadUserAssets()
            println("requestLoadUserAssets successful<<<") // 5
        }

        // 请求 用户资产详情数据  公司项目的网络请求业务
        launch {
            println("requestLoadUserAssetsDetails start>>>") // 3
            requestLoadUserAssetsDetails()
            println("requestLoadUserAssetsDetails successful<<<") // 4
            throw KotlinNullPointerException("请求 用户资产详情数据 失败了 发生了错误")
        }
    }
}