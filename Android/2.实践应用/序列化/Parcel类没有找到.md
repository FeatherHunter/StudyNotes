###E/Parcel: Class not found when unmarshalling
>E/Parcel: Class not found when unmarshalling: xxx.ItemDataBean
>java.lang.ClassNotFoundException: xxx.ItemDataBean

####原因分析
1. Android有两种不同的`classloaders`：`framework classloader`和`apk classloader`
2. `framework classloader`知道怎么加载android classes
3. `apk classloader`知道怎么加载你自定义的类，`apk classloader`继承自`framework classloader`，所以也知道怎么加载`android classes`。
4. 在应用刚启动时，`默认class loader`是`apk classloader`，但在系统内存不足应用被系统回收会再次启动，这个默认`class loader`会变为`framework classloader`了，所以对于自己的类会报`ClassNotFoundException`。

####解决办法
1、【推荐】Client端接收Bundle的时候进行处理
```java
 bundle.setClassLoader(getClass().getClassLoader());
```

2、需要传递的JavaBean继承自Parcelable，成员变量进行一定处理
```java
//原方法
//rect = in.readParcelable(null);

//改为
config = in.readParcelable(Rect.class.getClassLoader());
```
