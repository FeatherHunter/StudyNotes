package com.derry.kt_coroutines.t12.api

import com.derry.kt_coroutines.t01_t04.entity.LoginRegisterResponse
import com.xiangxue.kotlinproject.entity.LoginRegisterResponseWrapper
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

// 客户端API 可以访问 服务器的API
interface WanAndroidAPI {

    // TODO >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 下面是协程API

    /** https://www.wanandroid.com/blog/show/2
     * 登录API
     * username=Derry-vip&password=123456
     */
    @POST("/user/login")
    @FormUrlEncoded
    suspend fun loginActionCoroutine(@Field("username") username: String,
                    @Field("password") password: String)
            : LoginRegisterResponseWrapper<LoginRegisterResponse> // 返回值

    /** https://www.wanandroid.com/blog/show/2
     * 注册的API
     */
    @POST("/user/register")
    @FormUrlEncoded
    suspend fun registerActionCoroutine(@Field("username") username: String,
                       @Field("password") password: String,
                       @Field("repassword") repassword: String)
            : LoginRegisterResponseWrapper<LoginRegisterResponse> // 返回值
}