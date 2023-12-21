转载请注明链接：https://blog.csdn.net/feather_wch/article/details/88293585

>试过很多办法，但是都是标题党。本问题绝对可以解决。

# RecyclerView 高度 wrap_content 失效的问题

版本：2019-03-07(12:30)

> RecyclerView的属性`android:layout_height="wrap_content"`出现了失效的问题，我是用的GridLayoutManager，结果只有一行的数据。


**该问题是谷歌的BUG**

## 修复方法

1、修复方法
> 1. 用`RelativeLayout`包裹`RecyclerView`，RelativeLayout的高度采用`wrap_content`
> 1. `RecyclerView`的高度，采用`match_parent`
>
> 如下:
```xml
<RelativeLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content">

    <android.support.v7.widget.RecyclerView
        android:id="@+id/home_videos_recyclerview"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
    </android.support.v7.widget.RecyclerView>

</RelativeLayout>

```
