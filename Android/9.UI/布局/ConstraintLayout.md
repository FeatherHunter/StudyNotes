链接：http://blog.csdn.net/feather_wch/article/details/79585647

[TOC]

#ConstraintLayout
版本：2018/4/12-1


## 1-基本属性(约束效果)
```xml
#通过约束力进行定位
layout_constraintLeft_toLeftOf
layout_constraintLeft_toRightOf
layout_constraintRight_toLeftOf
layout_constraintRight_toRightOf
layout_constraintTop_toTopOf
layout_constraintTop_toBottomOf
layout_constraintBottom_toTopOf
layout_constraintBottom_toBottomOf
```

## 2-Start_toEndof(left\right类似)
```xml
app:layout_constraintStart_toStartOf //左侧与目标左侧对齐
app:layout_constraintStart_toEndOf   //左侧与目标右侧对齐
app:layout_constraintEnd_toStartOf   //右侧与目标左侧对齐
app:layout_constraintEnd_toEndOf     //右侧与目标右侧对齐
```

## 3-Gone Margin(根据约束的目标的可见性决定本身的外边距)
如果goneMargin相同方向的约束目标，该目标变成不可见。就会在该方向所有属性生效之后的位置上，通过该marigin值进行偏移。
```xml
layout_goneMarginStart
layout_goneMarginEnd
layout_goneMarginLeft
layout_goneMarginTop
layout_goneMarginRight
layout_goneMarginBottom
```

## 4-约束链式风格
![约束链式风格](https://upload-images.jianshu.io/upload_images/291600-f946591834de8def.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/700)

1、不填充平分(Spread)-图1
>1. 该例程在`水平方向`形成`链式效果(层层约束)`，通过`app:layout_constraintHorizontal_chainStyle="spread"`属性。
>2. `注意在水平方向上宽度为固定值或者wrap_content`
```xml
<TextView
    android:id="@+id/ll_cl_aa"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    xxx
    app:layout_constraintHorizontal_chainStyle="spread"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintLeft_toLeftOf="parent"
    app:layout_constraintRight_toLeftOf="@id/ll_cl_bb"
    app:layout_constraintTop_toTopOf="parent" />

<TextView
    android:id="@+id/ll_cl_bb"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    xxx
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintLeft_toRightOf="@id/ll_cl_aa"
    app:layout_constraintRight_toLeftOf="@id/ll_cl_cc"
    app:layout_constraintTop_toTopOf="parent" />

<TextView
    android:id="@+id/ll_cl_cc"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    xxx
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintLeft_toRightOf="@id/ll_cl_bb"
    app:layout_constraintRight_toRightOf="parent"
    app:layout_constraintTop_toTopOf="parent" />
```

2、填充型平分(图中没有)
>将上面实例中的`水平方向上的宽度`更改为`0dp`即可

3、不填充型平分-且只在中间有间隔(图2)
>`实例1`中更改为`        app:layout_constraintHorizontal_chainStyle="spread_inside"`即可

4、有weight权重的填充型平分(图3)
>1. `宽度为0dp`
>2. `app:layout_constraintHorizontal_weight="3"`指明水平方向`权重`
>3. `垂直方向同理`
```xml
<TextView
        android:id="@+id/ll_cl_aa"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        xxx
        app:layout_constraintHorizontal_weight="1"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintHorizontal_chainStyle="spread_inside"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toLeftOf="@id/ll_cl_bb"
        app:layout_constraintTop_toTopOf="parent" />

    <TextView
        android:id="@+id/ll_cl_bb"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        xxx
        app:layout_constraintHorizontal_weight="2"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toRightOf="@id/ll_cl_aa"
        app:layout_constraintRight_toLeftOf="@id/ll_cl_cc"
        app:layout_constraintTop_toTopOf="parent" />

    <TextView
        android:id="@+id/ll_cl_cc"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        xxx
        app:layout_constraintHorizontal_weight="3"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toRightOf="@id/ll_cl_bb"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent" />
```

5、聚集在中间(图4)
>1. `宽度为wrap_content或者固定值`
>2. `第一个控件的chainStyle`为`packed`
```xml
<TextView
        android:id="@+id/ll_cl_aa"
        android:layout_width="wrap_content"
        xxx
        app:layout_constraintHorizontal_chainStyle="packed"/>

    <TextView
        android:id="@+id/ll_cl_bb"
        android:layout_width="wrap_content"
        xxx/>

    <TextView
        android:id="@+id/ll_cl_cc"
        android:layout_width="wrap_content"
        xxx/>
```

6、具有bias的链式效果
>1. `链式的第一个控件`的`app:layout_constraintHorizontal_bias="0.3"`属性才有效。
>2. 需要是`packed`的风格。
>3. `app:layout_constraintHorizontal_bias="0.3"`表明`整个链`的`左侧和右侧`的`比例为1:1`

```java
<TextView
    android:id="@+id/ll_cl_aa"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    xxx
    app:layout_constraintHorizontal_chainStyle="packed"
    app:layout_constraintHorizontal_bias="0.3"/>

<TextView
    android:id="@+id/ll_cl_bb"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    xxx />

<TextView
    android:id="@+id/ll_cl_cc"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    xxx/>
```

##5-长宽比例
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

## 6-margin
```xml
# margin不需要解释
android:layout_marginStart
android:layout_marginEnd
android:layout_marginLeft
android:layout_marginTop
android:layout_marginRight
android:layout_marginBottom
```

## 7-权重

##8-两侧间隙比例(bias)
1、控件垂直方向上，上侧空隙和下侧空隙比例为 100%：0% = 放置在底部
```xml
app:layout_constraintVertical_bias="1"
```

2、控件垂直方向上，上侧空隙和下侧空隙比例为 80%：20% = 放置在从上到下大概4/5处。
```xml
app:layout_constraintVertical_bias="0.8"
```

## 9-GuideLine
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

## 10-BaseLine基线对齐
>与另一个`TextView`中的文本的基线`对齐`
```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Hi"
    android:textSize="80dp"
    app:layout_constraintBaseline_toBaselineOf="@id/ll_cl_aa"/>
```

## 实例：
> 1. 三个ImageView平分水平方向
> 2. 三个ImageView的间隔为10dp
> 3. ImageView宽高比为3:4
```xml
<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:paddingLeft="5dp"
    android:paddingRight="5dp">

    <android.support.constraint.ConstraintLayout
        android:id="@+id/cla"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginLeft="5dp"
        android:layout_marginRight="5dp"
        android:background="@color/colorAccent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toLeftOf="@+id/clb">

        <ImageView
            android:id="@+id/aaa"
            android:layout_width="0dp"
            android:layout_height="0dp"
            android:background="#2b8"
            app:layout_constraintDimensionRatio="3:4"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintRight_toRightOf="parent" />

    </android.support.constraint.ConstraintLayout>

    <android.support.constraint.ConstraintLayout
        android:id="@+id/clb"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginLeft="5dp"
        android:layout_marginRight="5dp"
        android:background="@color/colorPrimary"
        app:layout_constraintLeft_toRightOf="@+id/cla"
        app:layout_constraintRight_toLeftOf="@+id/clc">

        <ImageView
            android:id="@+id/bbb"
            android:layout_width="0dp"
            android:layout_height="0dp"
            android:background="#3cf"
            app:layout_constraintDimensionRatio="3:4"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintRight_toRightOf="parent" />
    </android.support.constraint.ConstraintLayout>

    <android.support.constraint.ConstraintLayout
        android:id="@+id/clc"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginLeft="5dp"
        android:layout_marginRight="5dp"
        android:background="@color/colorAccent"
        app:layout_constraintLeft_toRightOf="@+id/clb"
        app:layout_constraintRight_toRightOf="parent">

        <ImageView
            android:id="@+id/ccc"
            android:layout_width="0dp"
            android:layout_height="0dp"
            android:background="#2b8"
            app:layout_constraintDimensionRatio="3:4"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintRight_toRightOf="parent" />
    </android.support.constraint.ConstraintLayout>


</android.support.constraint.ConstraintLayout>
```

## 实例1-三个图片都是3:4比例、最左最右间隔为5dp、中间间隔为10dp
```xml
<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:paddingLeft="5dp"
    android:paddingRight="5dp">

    <android.support.constraint.ConstraintLayout
        android:id="@+id/cla"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginLeft="5dp"
        android:layout_marginRight="5dp"
        android:background="@color/colorAccent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toLeftOf="@+id/clb">

        <ImageView
            android:id="@+id/aaa"
            android:layout_width="0dp"
            android:layout_height="0dp"
            android:background="#2b8"
            app:layout_constraintDimensionRatio="3:4"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintRight_toRightOf="parent" />

    </android.support.constraint.ConstraintLayout>

    <android.support.constraint.ConstraintLayout
        android:id="@+id/clb"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginLeft="5dp"
        android:layout_marginRight="5dp"
        android:background="@color/colorPrimary"
        app:layout_constraintLeft_toRightOf="@+id/cla"
        app:layout_constraintRight_toLeftOf="@+id/clc">

        <ImageView
            android:id="@+id/bbb"
            android:layout_width="0dp"
            android:layout_height="0dp"
            android:background="#3cf"
            app:layout_constraintDimensionRatio="3:4"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintRight_toRightOf="parent" />
    </android.support.constraint.ConstraintLayout>

    <android.support.constraint.ConstraintLayout
        android:id="@+id/clc"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginLeft="5dp"
        android:layout_marginRight="5dp"
        android:background="@color/colorAccent"
        app:layout_constraintLeft_toRightOf="@+id/clb"
        app:layout_constraintRight_toRightOf="parent">

        <ImageView
            android:id="@+id/ccc"
            android:layout_width="0dp"
            android:layout_height="0dp"
            android:background="#2b8"
            app:layout_constraintDimensionRatio="3:4"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintRight_toRightOf="parent" />
    </android.support.constraint.ConstraintLayout>


</android.support.constraint.ConstraintLayout>

```


## 参考和学习资料
1. [ConstraintLayout 完全解析 快来优化你的布局吧](https://blog.csdn.net/lmj623565791/article/details/78011599)
