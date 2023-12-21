package com.derry.kt_coroutines.t12.repository

import com.derry.kt_coroutines.t01_t04.entity.LoginRegisterResponse
import com.derry.kt_coroutines.t12.api.APIClient
import com.derry.kt_coroutines.t12.api.WanAndroidAPI
import com.xiangxue.kotlinproject.entity.LoginRegisterResponseWrapper

// 仓库层
class APIRepository {

    // 挂起颜色的问题： 如果你先调用一个挂起函数，自身必须也是挂起函数

    suspend fun requestLogin(username: String, userpwd: String) : LoginRegisterResponseWrapper<LoginRegisterResponse> {
        return APIClient.instance.instanceRetrofit(WanAndroidAPI::class.java)
            .loginActionCoroutine(username, userpwd)
    }

    // 伪代码 假设没有协程
    fun requestLoginTest(username: String, userpwd: String) : LoginRegisterResponseWrapper<LoginRegisterResponse>? {
       return null
    }
}