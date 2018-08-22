转载请注明链接：https://blog.csdn.net/feather_wch/article/details/81407581

>解决OkHttp的报错问题：okhttp3.internal.http.RealResponseBody

如果有帮助请点个赞！万分感谢！

# okhttp3.internal.http.RealResponseBody

## 报错提示

>D/OkHttp: okhttp3.internal.http.RealResponseBody @ f11e81d

## 原因分析

通过OkHttp请求网络，结果请求下来的数据一直无法解析并且报错，这需要将`String  res = response.body().toString()`更改为`String  res = response.body().string()`

## 解决办法

旧代码:
```java
@Override
public void onResponse(Call call, Response response) throws IOException {
    String str = response.body().toString();
    Log.d("OkHttp", str);
}
```

更改为：
```java
@Override
public void onResponse(Call call, Response response) throws IOException {
    String str = response.body().string();
    Log.d("OkHttp", str);
}
```
