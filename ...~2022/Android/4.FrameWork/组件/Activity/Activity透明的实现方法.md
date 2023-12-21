
转载请注明链接：https://blog.csdn.net/feather_wch/article/details/88343892

> Activity透明的实现方法: 系统透明度样式、自定义透明度样式

# Activity透明的实现方法

版本：2019/3/8-16:02

---

[toc]
## 使用系统的透明样式

```xml
<activity
    xxx
    android:theme="@android:style/Theme.Translucent" >
    <!-- 采用系统默认的主题 -->
</activity>
```

## 自定义透明样式

### 1、styles.xml

```xml
<style name="hostTheme" parent="AppBaseTheme">
    <item name="android:windowIsTranslucent">true</item>
    <item name="android:windowBackground">@color/custom_background</item>
    <item name="android:windowAnimationStyle">@android:style/Animation.Translucent</item>
</style>
```
1. `android:windowIsTranslucent`: 当前Activity是否透明
1. `android:windowBackground`: 透明的背景色，可以自定义透明效果
1. `android:windowAnimationStyle`: Activity进出的方式

### 2、静态设置, AndroidManifest.xml


```xml
<activity
    xxx
    android:theme="@style/hostTheme" >
</activity>
```

### 3、代码设置, MainActivity.java

```java
@Override
protected void onCreate(Bundle savedInstanceState)
{
    super.onCreate(savedInstanceState);

    // 设置主题
    setTheme(R.style.hostTheme);

    // XXX
}
```
