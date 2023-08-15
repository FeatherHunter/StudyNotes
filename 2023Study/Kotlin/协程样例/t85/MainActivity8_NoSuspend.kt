package com.derry.kt_coroutines.t85

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

// TODO  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 下面是 t85通用代码：
/*
 * 请求加载[用户数据]
 */
private suspend fun requestLoadUser_NoSuspend() : String {  // 挂起函数 的 伪挂起
    val isLoadSuccess = true // 加载成功，和，加载失败，的标记
    if (isLoadSuccess) {
        return "加载到[用户数据]信息集"
    } else {
        return "加载[用户数据],加载失败,服务器宕机了"
    }
}

/*
 * 请求加载[用户资产数据]
 */
private suspend fun requestLoadUserAssets_NoSuspend(): String {  // 挂起函数 的 伪挂起
    val isLoadSuccess = true // 加载成功，和，加载失败，的标记

    if (isLoadSuccess) {
        return "加载到[用户资产数据]信息集"
    } else {
        return "加载[用户资产数据],加载失败,服务器宕机了"
    }
}

/*
 * 请求加载[用户资产详情数据]
 */
private suspend fun requestLoadUserAssetsDetails_NoSuspend() : String {  // 挂起函数 的 伪挂起
    val isLoadSuccess = true // 加载成功，和，加载失败，的标记

    if (isLoadSuccess) {
        return "加载到[用户资产详情数据]信息集"
    } else {
        return "加载[用户资产详情数据],加载失败,服务器宕机了"
    }
}

class MainActivity8_NoSuspend : AppCompatActivity() {

    suspend fun showCoroutine2() {
        println("开始执行了哦")
        val user = requestLoadUser_NoSuspend() // 未挂起
        Toast.makeText(this, "更新UI:$user", Toast.LENGTH_SHORT).show()

        val userAssets = requestLoadUserAssets_NoSuspend() // 未挂起
        Toast.makeText(this, "更新UI:$userAssets", Toast.LENGTH_SHORT).show()

        val userAssetsDetails = requestLoadUserAssetsDetails_NoSuspend() // 未挂起
        Toast.makeText(this, "更新UI:$userAssetsDetails", Toast.LENGTH_SHORT).show()
    }
}