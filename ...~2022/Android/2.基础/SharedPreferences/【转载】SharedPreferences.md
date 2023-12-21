本文链接: https://blog.csdn.net/feather_wch/article/details/51013629

> 年代久远找不到原作者了，Sorry

# 【转载】SharedPreferences

存储轻型数据，适合保存配置文件等。

定义变量
```java
apSharedPreferences =  null;
apSharedPreferences = getSharedPreferences("saved", Activity.MODE_PRIVATE);//获取
```
保存数据
```java
SharedPreferences.Editor editor = apSharedPreferences.edit();//用putString的方法保存数据
editor.putString("account", accountString);
editor.putString("password", passwordString);//提交当前数据
editor.commit();
```
获取数据
```java
accountString  = apSharedPreferences.getString("account", ""); // getString方法获得value，第2个参数是value的默认值
passwordString = apSharedPreferences.getString("password", "");
```
