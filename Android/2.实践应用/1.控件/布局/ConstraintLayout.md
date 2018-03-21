未完成，勿看！

链接：http://blog.csdn.net/feather_wch/article/details/79585647

[TOC]

##参考和学习资料
1. [解析ConstraintLayout的性能优势](http://mp.weixin.qq.com/s/gGR2itbY7hh9fo61SxaMQQ)
1. [Android ConstraintLayout详解](http://mp.weixin.qq.com/s/8KDmWV_IU2NyP8DwQV52AQ)
2. [拒绝拖拽 使用ConstraintLayout优化你的布局吧](http://mp.weixin.qq.com/s/vI-fPaNoJ7ZBlZcMkEGdLQ)

##基本属性(约束效果)

##Start_toEndof(left\right类似)

##约束链式风格

##长宽比例
```java
<TextView
    android:id="@+id/constraint_banner1"
    android:layout_width="0dp"
    android:layout_height="0dp"
    app:layout_constraintTop_toTopOf="parent"
    app:layout_constraintRight_toRightOf="parent"
    app:layout_constraintLeft_toLeftOf="parent"
    android:text="Banner"
    android:gravity="center"
    android:background="@color/colorAccent"
    app:layout_constraintDimensionRatio="16:1"
    />
```

##margin

##权重

##两侧间隙比例

##GuideLine
`android:orientation`决定线的方向
`app:layout_constraintGuide_percent="0.8"`表示一条线所分隔开的两个区域的比例，如水平线时上面区域所占比例80%，下面区域只有20%
`app:layout_constraintGuide_begin="100dp"`与比例类似，只不过是具体数值。且`end`的优先级比`begin`大，优先采用`end的数值`

```xml
<android.support.constraint.Guideline
    android:id="@+id/constraint_guideline_v"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    app:layout_constraintGuide_percent="0.8"/>

<android.support.constraint.Guideline
    android:id="@+id/constraint_guideline_h"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    app:layout_constraintGuide_begin="100dp"
    app:layout_constraintGuide_end="200dp"/>

<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="hello"
    app:layout_constraintLeft_toRightOf="@id/constraint_guideline_v"
    app:layout_constraintTop_toBottomOf="@id/constraint_guideline_h"/>
```

##BaseLine基线对其

###底部Tab效果
####三个Tab撑满平分整个屏幕
```java
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context="com.example.a6005001819.androiddeveloper.ConstraintFragment">

    <TextView
        android:id="@+id/constraint_bottombar_word_text"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:background="@color/colorPrimary"
        android:gravity="center"
        android:text="单词"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toLeftOf="@id/constraint_bottombar_read_text"
        app:layout_constraintHorizontal_chainStyle="spread" />

    <TextView
        android:id="@+id/constraint_bottombar_read_text"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:background="@color/colorAccent"
        android:gravity="center"
        android:text="阅读"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toRightOf="@id/constraint_bottombar_word_text"
        app:layout_constraintRight_toLeftOf="@id/constraint_bottombar_find_text" />

    <TextView
        android:id="@+id/constraint_bottombar_find_text"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:background="@color/colorPrimaryDark"
        android:gravity="center"
        android:text="发现"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toRightOf="@id/constraint_bottombar_read_text"
        app:layout_constraintRight_toRightOf="parent" />

</android.support.constraint.ConstraintLayout>
```

####三个Tab平分整个屏幕，但不撑满
将上例的`TextView`的宽度改为`wrap_content`或者`固定值`.

####根据权重进行分配
```xml
app:layout_constraintHorizontal_weight="1"
```
上例在`宽度为0`时(大小由约束力进行控制)，通过`权重weight`能给三个Tab分配不同比例大小的区域。

####三个Tab的ChainsStyle(链式风格)为spread_inside
1、控件大小为`0dp`时，效果就是三个Tab撑满平分整个屏幕
2、控件大小为`固定值或wrap_content`时，第一个和最后一个控件的边缘与parent完全对其。

#### bias
1、控件垂直方向上，上侧空隙和下侧空隙比例为 100%：0% = 放置在底部
```xml
app:layout_constraintVertical_bias="1"
```

2、控件垂直方向上，上侧空隙和下侧空隙比例为 80%：20% = 放置在从上到下大概4/5处。
```xml
app:layout_constraintVertical_bias="0.8"
```
