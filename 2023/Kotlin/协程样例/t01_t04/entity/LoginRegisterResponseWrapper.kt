package com.xiangxue.kotlinproject.entity

/**
 * 包装Bean

    {
        "data": {
            "admin": false,
            "chapterTops": [],
            "collectIds": [],
            "email": "",
            "icon": "",
            "id": 66720,
            "nickname": "Derry-vip",
            "password": "",
            "publicName": "Derry-vip",
            "token": "",
            "type": 0,
            "username": "Derry-vip"
        },
        "errorCode": 0,
        "errorMsg": ""
        }

    {
    "data": null,
    "errorCode": -1,
    "errorMsg": "账号密码不匹配！"
    }

 */
data class LoginRegisterResponseWrapper<T>(val data: T, val errorCode: Int, val errorMsg: String)