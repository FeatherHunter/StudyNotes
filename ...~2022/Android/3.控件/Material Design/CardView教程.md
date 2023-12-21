#CardView教程
版本：2018/4/24-1

转载请注明链接：https://blog.csdn.net/feather_wch/article/details/80069108

1、背景介绍
1. CardView和RecyclerView一样是Android5.0以后新出现的控件，伴随着Material Design设计而诞生。

2、使用实例
>1. 导入`compile 'com.android.support:cardview-v7:26.1.0'`
>2. `CardView`包裹住`需要添加卡片效果的布局`即可。
```xml
<android.support.v7.widget.CardView
    android:id="@+id/stars_rv_profile_item_cardview"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:cardCornerRadius="5dp"
    app:cardMaxElevation="6dp"
    app:cardElevation="6dp"
    app:cardPreventCornerOverlap="true"
    app:cardUseCompatPadding="true">
     <android.support.constraint.ConstraintLayout>
            xxxxxx
     </android.support.constraint.ConstraintLayout>
</android.support.v7.widget.CardView>
```

3、CardView属性
```xml
app:cardBackgroundColor      设置背景颜色

app:cardCornerRadius         设置圆角大小

app:cardElevation            设置z轴阴影高度

app:cardMaxElevation         设置z轴最大高度值

app:contentPadding           内容与边距的间隔

app:contentPaddingLeft       内容与左边的间隔

app:contentPaddingTop        内容与顶部的间隔

app:contentPaddingRight      内容与右边的间隔

app:contentPaddingBottom     内容与底部的间隔

app:paddingStart             内容与边距的间隔起始

app:paddingEnd               内容与边距的间隔终止

app:cardUseCompatPadding     设置内边距，在API21及以上版本和之前的版本仍旧具有一样的计算方式

app:cardPreventConrerOverlap 在API20及以下版本中添加内边距，这个属性为了防止内容和边角的重叠

```
> 注意：CardView中使用android:background设置背景颜色无效。

4、CardView点击的波纹特效(Ripple)
```xml
android:foreground=”?android:attr/selectableItemBackground”
```
