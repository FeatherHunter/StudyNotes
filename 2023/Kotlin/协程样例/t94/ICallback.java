package com.derry.kt_coroutines.t94;

public interface ICallback<T> {

    void resume(T result); // SAM转换  接口单一方法转换   使用  {}

    // void resume2(T result); // SAM转换  接口单一方法转换   使用  {}
}