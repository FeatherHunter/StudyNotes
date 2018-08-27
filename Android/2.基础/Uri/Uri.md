转载请注明链接：https://blog.csdn.net/feather_wch/article/details/79416504

>文本总结Uri相关知识点

#Android Uri详细知识点汇总
版本：2018/8/27-1

1、URI和Uri的区别
>所属的包不同：
>1. URI位置在java.net.URI,是Java提供的一个类。
>2. Uri位置在android.net.Uri,是由Android提供的一个类。
>作用的不同:
>3. URI类代表了一个URI（这个URI不是类，而是其本来的意义：通用资源标志符——Uniform Resource Identifier)实例。
>4. Uri类是一个不可改变的URI引用，包括一个URI和一些碎片，URI跟在“#”后面。建立并且转换URI引用。而且Uri类对无效的行为不敏感，对于无效的输入没有定义相应的行为，如果没有额外制定，它将返回垃圾而不是抛出一个异常。

2、Uri的特点
>1. Uri是URI的“扩展”以适应Android系统的需要。

3、Uri实例解析
`http://www.java2s.com:8080/yourpath/fileName.htm?stove=10&path=32&id=4#harvic`

|部分|内容|方法|
|---|---|---|
|scheme|http|getScheme()|
|authority|host+port: www.java2s.com:8080|getAuthority()|
|host|www.java2s.com|getHost()|
|port|8080|getPort()|
|path|authority和query中间部分: /yourpath/fileName.htm|getPath()|
|fragment|#号后面: harvic|getFragment()|
|query|参数部分: stove=10&path=32&id=4|getQuery()|
|scheme-specific-part|http:和frgament之间的内容: //www.java2s.com:8080/yourpath/fileName.htm?stove=10&path=32&id=4|getSchemeSpecificPart()|

4、getPathSegments()作用
>1.将`Path`整个部分接取下来，并拆分成字符串数组
```java
String mUriStr = "http://www.java2s.com:8080/yourpath/fileName.htm?stove=10&path=32&id=4#harvic";
Uri mUri = Uri.parse(mUriStr);
List<String> pathSegList = mUri.getPathSegments();
for (String pathItem:pathSegList){
    Log.d("getPathSegments","pathSegItem:"+pathItem);
}
```
>2.Path被拆分为“yourpath”和“fileName.htm”

5、getQueryParameter(String key)-获取参数中key对应的value
>能获取到stove、id、path对应的数值，如果value不存在会返回`null`
```java
String mUriStr = "http://www.java2s.com:8080/yourpath/fileName.htm?stove=10&path=32&id#harvic";
mUri = Uri.parse(mUriStr);
Log.d(tag,"stove="+mUri.getQueryParameter("stove"));
Log.d(tag,"id="+mUri.getQueryParameter("id"));
```

6、绝对URI和相对URI
>1. 绝对URI：`http://fsjohnhuang.cnblogs.com`
>2. 相对URI：`fsjohnhuang.cnblogs.com`

7、不透明URI和分层URI
>1. 不透明URI: `mailto:fsjohnhuang@xxx.com`
>2. 分层URI：`http://fsjohnhuang.com`

## 参考资料
1. [Uri详解之——Uri结构与代码提取](https://blog.csdn.net/harvic880925/article/details/44679239)
