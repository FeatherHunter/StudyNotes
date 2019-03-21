
转载请注明链接：http://blog.csdn.net/feather_wch/article/details/79585647

# ConstraintLayout

版本: 2019/3/21(23:47)

---

[toc]
## 1-相对定位属性

1、ConstraintLayout的定位属性
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

#用于LTR、RTL布局
#LTR布局为例
app:layout_constraintStart_toStartOf //左侧与目标左侧对齐
app:layout_constraintStart_toEndOf   //左侧与目标右侧对齐
app:layout_constraintEnd_toStartOf   //右侧与目标左侧对齐
app:layout_constraintEnd_toEndOf     //右侧与目标右侧对齐
```

### 基线对齐: toBaselineOf

2、基线对齐属性
>与另一个`TextView`中的文本的基线`对齐`
```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Hi"
    android:textSize="80dp"
    app:layout_constraintBaseline_toBaselineOf="@id/ll_cl_aa"/>
```

### 两侧间隙比例: bias(偏向某一边)

3、通过bias控制约束力，能决定原本居中的控件距离哪边更近
> 分为水平、垂直两个方向

#### 垂直约束力

4、垂直约束力的属性为`app:layout_constraintVertical_bias`

5、控件放置在底部
> 等效于`控件垂直方向上，上侧空隙和下侧空隙比例为 100%：0%`
```xml
app:layout_constraintVertical_bias="1"
```

6、控件 放置在从上到下大概4/5处。
> 等效于`控件垂直方向上，上侧空隙和下侧空隙比例为 80%：20%`
```xml
app:layout_constraintVertical_bias="0.8"
```

#### 水平约束力

7、水平约束力的属性为`app:layout_constraintHorizontal_bias`

### 链式风格: Chains
![约束链式风格](https://upload-images.jianshu.io/upload_images/291600-f946591834de8def.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/700)

1、链式风格的属性
```xml
app:layout_constraintHorizontal_chainStyle="spread | spread_inside | packed"
app:layout_constraintVertical_chainStyle=""

#spread：不填充型平分---上图第一种风格
#spread_inside：不填充型平分，只有中间有间隙---上图第二种风格
# 填充型平分-宽度0dp即可
# 填充型加权平分(weight)---上图第三种风格
#packed：集中式---上图第四种风格
# 集中式bias平分---上图第四种
```
> 权重
```xml
# 加权重。只要有一个有权重，其他不加权重的权重都为0

app:layout_constraintHorizontal_weight
app:layout_constraintVertical_weight
```
> bias
```xml
# 1. 必须用于链式的第一个控件
# 2. 必须packed风格
# 3. 表明整个链的左侧占据剩余空间的比例，0.3 = 左侧只占据30% = 左侧:右侧 = 3:7

app:layout_constraintHorizontal_bias
app:layout_constraintVertical_bias
```


2、不填充型平分(spread-图1)
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

3、不填充型平分-且只在中间有间隔(spread_inside-图2)
>`实例1`中更改为`        app:layout_constraintHorizontal_chainStyle="spread_inside"`即可

4、填充型平分(任何风格，控件都用0dp)
>将上面实例中的`水平方向上的宽度`更改为`0dp`即可


5、填充型加weight权重平分(图3)
>1. `宽度为0dp`
>2. `app:layout_constraintHorizontal_weight="3"`指明水平方向`权重`
>3. `垂直方向同理`
> 1. 不加权重的默认为0
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

6、集中式(packed-图4)
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

7、bias平分的链式效果
>1. `链式的第一个控件`的`app:layout_constraintHorizontal_bias="0.3"`属性才有效。
>2. 需要是`packed`的风格。
>3. `app:layout_constraintHorizontal_bias="0.3"`表明`整个链`的`左侧和右侧`的`比例为3:7`

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

## 2-边距(Margin)

3、边距属性，距离参照物的距离
```xml
# margin不需要解释
android:layout_marginStart
android:layout_marginEnd
android:layout_marginLeft
android:layout_marginTop
android:layout_marginRight
android:layout_marginBottom
```

### 可见性问题

4、目标可见性问题，目标如果为GONE会化为一个点，且该目标控件的Margin属性失效
> 1. 控件A、控件B从左向右顺序排列
> 1. A的MarginLeft = 10dp, B的MarginLeft = 30dp
> 1. A为GONE之后，B的MarginLeft距离父布局左侧为30dp
```
// 原效果
-控件A---控件B

// 控件A GONE
---控件B
```
```xml
<TextView
    android:id="@+id/a_txt"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="A"
    android:textSize="30dp"
    app:layout_constraintTop_toTopOf="parent"
    app:layout_constraintLeft_toLeftOf="parent"
    app:layout_constraintRight_toLeftOf="@+id/b_txt"
    android:layout_marginLeft="10dp"
    android:visibility="gone"/>

<TextView
    android:id="@+id/b_txt"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="B"
    android:textSize="30dp"
    app:layout_constraintTop_toTopOf="parent"
    app:layout_constraintLeft_toRightOf="@+id/a_txt"
    android:layout_marginLeft="30dp"
    />
```

### Gone Margin(根据约束的目标的可见性决定本身的外边距)

5、Gone Margin属性
> 如果goneMargin相同方向的约束目标，该目标变成不可见。就会在该方向所有属性生效之后的位置上，通过该marigin值进行偏移。
```xml
layout_goneMarginStart
layout_goneMarginEnd
layout_goneMarginLeft
layout_goneMarginTop
layout_goneMarginRight
layout_goneMarginBottom
```

## 3-控件宽高属性: width/height

1、控件的宽高属性
```xml
# 宽高。wrap_content、0dp、具体dp
android:layout_width
android:layout_height

# 最小/最大宽高。在layout_width/height属性为wrap_content时生效
android:minWidth
android:maxWidth
android:minHeight
android:maxHeight
```

### 宽高比例约束

2、控件的宽高还可以通过比例设定
> 1. 至少有一个`宽高`为`0dp`
```
# 指定宽高比
app:layout_constraintDimensionRatio="16:1"
```

|设定方式|解释|layout_constraintDimensionRatio="xxx"|含义|
|---|---|---|---|
|浮点数|宽高比|0.3 |宽为0.3,高为1|
|||3 |宽为3,高为1|
|比例|宽度/高度|16:9|宽为16,高为9|
|指定分子||W, 16:9 == 16:9|W指明 W:H = 16:9|
|||H, 16：9|H指明 H:W = 16:9


```xml
<TextView
    android:id="@+id/constraint_banner1"
    android:layout_width="0dp"
    android:layout_height="0dp"
    app:layout_constraintTop_toTopOf="parent"
    app:layout_constraintRight_toRightOf="parent"s
    app:layout_constraintLeft_toLeftOf="parent"
    android:text="Banner"
    android:gravity="center"
    android:background="@color/colorAccent"
    app:layout_constraintDimensionRatio="16:1"
    />
```

### 强制约束wrap_content(1.1.x)

1、view的宽高如果为wrap_content，并且内容特别长的时候会出现问题
```xml
# 正常场景
|AAA-BBB-|

# 控件B的内容特别长, 出现奇怪的错误
|AAA-----|
|-BBBBBBB|

# 理想效果
|AAABBBBB|
```
> 宽度为wrap_content的控件B，内容过长，导致约束效果不理想
> 示例: 可以看效果
```xml
<TextView
    android:id="@+id/a_txt"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="AAA"
    android:textSize="30dp"
    app:layout_constraintTop_toTopOf="parent"
    app:layout_constraintLeft_toLeftOf="parent"/>

<TextView
    android:id="@+id/b_txt"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="BBBBBBBBBBBBBBBXXXXXXXXXXXXXXXXX"
    android:textSize="30dp"
    app:layout_constraintTop_toTopOf="parent"
    app:layout_constraintRight_toRightOf="parent"
    app:layout_constraintLeft_toRightOf="@+id/a_txt"
    />

```

2、解决wrap_content问题的强制约束属性
> true: 进行强制约束
> false: 默认效果
```xml
app:layout_constrainedWidth="true | false"
app:layout_constrainedHeight="true | false"
```

### 0dp时占据父布局剩余空间的百分比(1.1.x)

1、控件宽高采用0dp时会占据父布局剩余空间，通过百分比决定占据多少空间
```
# 宽高必须为0dp
# 居中，且占据百分之多少的空间
app:layout_constraintWidth_percent="0~1"
app:layout_constraintHeight_percent="0~1"

# 百分比的基础上，最低宽高
app:layout_constraintWidth_min="100dp"
app:layout_constraintHeight_min="100dp"

# 百分比的基础上，最高宽高
app:layout_constraintWidth_max="200dp"
app:layout_constraintHeight_max="200dp"
```

2、控件只占据父容器剩余空间的40%
```xml
app:layout_constraintWidth_percent="0.4"
```
```xml
# 当前效果，居中，且只有40%
|---AAAA---|

# 原效果
|AAAAAAAAAA|
```

2、`app:layout_constraintWidth_default`的作用
|属性|作用|
|---|---|
|wrap|等效于`android:layout_width="wrap_content"`【让layout_constraintWidth_percent属性失效】|
|percent|百分比属性生效|
|spread|等效于`android:layout_width="match_parent"`【默认】【目前和percent效果一样】|

## 4-圆形定位(1.1.x)

1、ConstraintLayout提供圆形定位的功能：以目标为圆形，360度定位
> 提供三个属性
```
# 参照的控件ID
app:layout_constraintCircle

# 距离圆心的半径
app:layout_constraintCircleRadius

# 旋转的角度。0~360顺时针。举例-0:正上方 90:正右方 180:正下方 270:正左方
app:layout_constraintCircleAngle
```

## 5-虚拟视图

### Guideline(辅助线)

1、Guideline能创建水平/垂直方向的准线
> 属性如下:
```xml
<android.support.constraint.Guideline
# 宽高随意
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"

# 指明方向: vertical | horizontal
    android:orientation="vertical"

# 分隔开的区域的比例
# layout_constraintGuide_percent优先级大于begin/end
      vertical 左边区域：右边区域　８：２
      horizontal　上边区域：下边区域　８：２
    app:layout_constraintGuide_percent="0.8"

# 距离左侧/顶部具体的数值
    app:layout_constraintGuide_begin="100dp"
# 距离右侧/底部具体的数值，begin优先级更大
    app:layout_constraintGuide_end="200dp"/>
```

2、Guideline实例
> 1. `android:orientation`决定线的方向
> 1. `app:layout_constraintGuide_percent="0.8"`表示一条线所分隔开的两个区域的比例，如水平线时上面区域所占比例80%，下面区域只有20%
> 1. `app:layout_constraintGuide_begin="100dp"`与比例类似，只不过是具体数值。且`end`的优先级比`begin`大，优先采用`end的数值`
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

### Barrier(屏障)

1、Barrier屏障的作用和具有的属性
> 1. Barrier能根据多个View的约束状况，形成一个类似于Guideline的参照物
```xml
<android.support.constraint.Barrier
    android:id="@+id/barrier"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
# 相当于给定View的位置 top | bottom | left | right | start | end
    app:barrierDirection="right"
# 参考哪些View，不需要@id
    app:constraint_referenced_ids="a_txt,b_txt"/>
```

2、Barrier使用实例
> TextViewC在TexViewA和TextViewB的右侧，并且是两者的最宽的右侧
```xml
<TextView
    android:id="@+id/a_txt"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="AAA"
    app:layout_constraintTop_toTopOf="parent"
    app:layout_constraintLeft_toLeftOf="parent"
    android:background="#39b8ff"/>

<TextView
    android:id="@+id/b_txt"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="BBBBBB"
    app:layout_constraintLeft_toLeftOf="parent"
    app:layout_constraintTop_toBottomOf="@+id/a_txt"
    android:background="#39b8ff"/>

<android.support.constraint.Barrier
    android:id="@+id/barrier"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:barrierDirection="right"
    app:constraint_referenced_ids="a_txt,b_txt"/>

<TextView
    android:id="@+id/c_txt"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="BBBBBB"
    app:layout_constraintLeft_toRightOf="@+id/barrier"
    app:layout_constraintTop_toTopOf="parent"
    android:background="#39b8ff"/>
```

### Group(分组)

1、Group分组用于将View进行分组，用于控制一组View的显示与否
```xml
# 指明该组有哪些View，不需要@id
app:constraint_referenced_ids="a_txt,b_txt"
```
> 1. `不能用Group包裹View`，Group是一个`不执行onDraw()的View`
> 1. 不要将View重复放到多个Group中，可能会导致`隐藏失效，冲突`


2、Group将两个TextView一起隐藏
```xml
<android.support.constraint.Group
    android:id="@+id/group"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:constraint_referenced_ids="a_txt,b_txt"
    android:visibility="invisible"/>

<TextView
    android:id="@+id/a_txt"
    xxx/>

<TextView
    android:id="@+id/b_txt"
    xxx/>
```

### Placeholder

1、Placeholder的作用?
> 1. 一种占位的作用
> 1. 通过`Placeholder的setContentId(view.getId());`可以设置具体的控件
> 1. `TransitionManager.beginDelayedTransition(mConstraintLayout);`启动平滑的动画效果
```xml
<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:id="@+id/constraintLayout"
    android:layout_height="match_parent">

    <android.support.constraint.Placeholder
        android:id="@+id/placeholder"
        android:layout_width="100dp"
        android:layout_height="100dp"
        android:layout_marginStart="8dp"
        android:layout_marginTop="8dp"
        android:layout_marginEnd="8dp"
        android:layout_marginBottom="8dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="@+id/mail" />

    <ImageButton
        android:id="@+id/favorite"
        android:layout_width="48dp"
        android:layout_height="48dp"
        android:layout_marginTop="16dp"
        android:background="#00000000"
        android:tint="#E64A19"
        android:src="@drawable/ic_launcher_foreground"
        app:layout_constraintEnd_toStartOf="@id/mail"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <ImageButton
        android:tint="#512DA8"
        android:id="@+id/mail"
        android:layout_width="48dp"
        android:layout_height="48dp"
        android:layout_marginTop="16dp"
        android:background="#00000000"
        android:src="@drawable/ic_launcher_foreground"
        app:layout_constraintEnd_toStartOf="@id/save"
        app:layout_constraintStart_toEndOf="@id/favorite"
        app:layout_constraintTop_toTopOf="parent" />

    <ImageButton
        android:tint="#D32F2F"
        android:id="@+id/save"
        android:layout_width="48dp"
        android:layout_height="48dp"
        android:layout_marginTop="16dp"
        android:background="#00000000"
        android:src="@drawable/ic_launcher_foreground"
        app:layout_constraintEnd_toStartOf="@id/play"
        app:layout_constraintStart_toEndOf="@id/mail"
        app:layout_constraintTop_toTopOf="parent" />

    <ImageButton
        android:tint="#FFA000"
        android:id="@+id/play"
        android:layout_width="48dp"
        android:layout_height="48dp"
        android:layout_marginTop="16dp"
        android:background="#00000000"
        android:src="@drawable/ic_launcher_foreground"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toEndOf="@id/save"
        app:layout_constraintTop_toTopOf="parent" />


</android.support.constraint.ConstraintLayout>
```
```java
public class ConstraintLayoutActivity extends AppCompatActivity {

    Placeholder mPlaceholder;
    ConstraintLayout mConstraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_constraint_layout);

        findViewById(R.id.favorite).setOnClickListener(new onClick());
        findViewById(R.id.mail).setOnClickListener(new onClick());
        findViewById(R.id.save).setOnClickListener(new onClick());
        findViewById(R.id.play).setOnClickListener(new onClick());
        mConstraintLayout = findViewById(R.id.constraintLayout);
        mPlaceholder = findViewById(R.id.placeholder);
    }

    class onClick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            // 1. 执行顺滑的动画效果
            TransitionManager.beginDelayedTransition(mConstraintLayout);
            // 2. 将点击的View放置到placeholder中
            mPlaceholder.setContentId(v.getId());
        }
    }
}
```

### Constraints

### 虚拟视图的原理

1、如上所有虚拟视图内部的原理是什么?
> 1. 都直接or间接继承自View
> 1. 构造方法中将自己置为`GONE`，`setVisibility()为空方法`，永远为GONE了
> 1. `draw()方法是空方法`，不会绘制
> 1. `onMeasure()方法`中将长宽设置为0

2、虚拟视图就是一种不可见、不测量、不绘制的View
> 几乎没有性能消耗

## 6-动画

### ConstraintSet

1、ConstraintSet是什么?
> 能在代码中轻松地改变控件的位置大小，不再需要使用LayoutParams

2、ConstraintSet的方法
```java
ConstraintSet constraintSet = new ConstraintSet();
// 1. clone
constraintSet.clone(ConstraintLayout constraintLayout);
constraintSet.clone(ConstraintSet set);
constraintSet.clone(Context context, int constraintLayoutId);
constraintSet.clone(Constraints constraints);

// 2. 设置flow1控件的顶边与flow2的底边对齐,且之间margin值是50px:
constraintSet.connect(view1.getId(), ConstraintSet.TOP,    // 第一个控件的顶部
                      view2.getId(), ConstraintSet.BOTTOM, // 第二个控件的底部
                      50);        // 之间的margin

// 3. 设置view2水平剧中于parent
constraintSet.centerVertically(R.id.view2, ConstraintSet.PARENT_ID);
//constraintSet.centerHorizontally(R.id.view2, ConstraintSet.PARENT_ID);

// 4. 设置view1的高度为120px
constraintSet.constrainHeight(R.id.view1, 300);

// 5. apply使设置生效
constraintSet.applyTo(binding.constraintLayout);
```

### TransitionManager

1、ConstraintSet和TransitionManager实现水平、垂直布局变化的效果
> 1-水平方向布局
```xml
<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <ImageButton
        android:id="@+id/favorite"
        android:layout_width="60dp"
        android:layout_height="60dp"
        android:background="#00000000"
        android:tint="#E64A19"
        android:src="@drawable/ic_launcher_foreground"
        app:layout_constraintEnd_toStartOf="@id/mail"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"/>

    <ImageButton
        android:tint="#512DA8"
        android:id="@+id/mail"
        android:layout_width="60dp"
        android:layout_height="60dp"
        android:background="#00000000"
        android:src="@drawable/ic_launcher_foreground"
        app:layout_constraintEnd_toStartOf="@id/save"
        app:layout_constraintStart_toEndOf="@id/favorite"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"/>

    <ImageButton
        android:tint="#D32F2F"
        android:id="@+id/save"
        android:layout_width="60dp"
        android:layout_height="60dp"
        android:background="#00000000"
        android:src="@drawable/ic_launcher_foreground"
        app:layout_constraintEnd_toStartOf="@id/play"
        app:layout_constraintStart_toEndOf="@id/mail"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"/>

    <ImageButton
        android:tint="#FFA000"
        android:id="@+id/play"
        android:layout_width="60dp"
        android:layout_height="60dp"
        android:background="#00000000"
        android:src="@drawable/ic_launcher_foreground"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toEndOf="@id/save"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"/>


</android.support.constraint.ConstraintLayout>
```
> 2-垂直方向布局，ID相同
```xml
<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <ImageButton
        android:id="@+id/favorite"
        android:layout_width="60dp"
        android:layout_height="60dp"
        android:layout_marginTop="72dp"
        android:layout_marginEnd="8dp"
        android:background="#00000000"
        android:src="@drawable/ic_launcher_foreground"
        android:tint="#E64A19"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.51"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <ImageButton
        android:id="@+id/mail"
        android:layout_width="60dp"
        android:layout_height="60dp"
        android:layout_marginStart="8dp"
        android:layout_marginTop="112dp"
        android:layout_marginEnd="8dp"
        android:background="#00000000"
        android:src="@drawable/ic_launcher_foreground"
        android:tint="#512DA8"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.498"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/favorite" />

    <ImageButton
        android:id="@+id/save"
        android:layout_width="60dp"
        android:layout_height="60dp"
        android:layout_marginStart="8dp"
        android:layout_marginTop="100dp"
        android:layout_marginEnd="8dp"
        android:background="#00000000"
        android:src="@drawable/ic_launcher_foreground"
        android:tint="#D32F2F"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.498"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/mail" />

    <ImageButton
        android:id="@+id/play"
        android:layout_width="60dp"
        android:layout_height="60dp"
        android:layout_marginStart="8dp"
        android:layout_marginTop="8dp"
        android:background="#00000000"
        android:src="@drawable/ic_launcher_foreground"
        android:tint="#FFA000"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.486"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/save"
        app:layout_constraintVertical_bias="0.383" />

</android.support.constraint.ConstraintLayout>
```
> 3-测试页面Activity的布局
```xml
<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <include layout="@layout/constraint_horizontal_layout"
        android:id="@+id/constraintLayout"/>

    <Button
        android:id="@+id/vertical_btn"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="垂直布局"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintRight_toLeftOf="@+id/horizontal_btn"/>


    <Button
        android:id="@+id/horizontal_btn"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="水平布局"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toRightOf="@+id/vertical_btn"/>

</android.support.constraint.ConstraintLayout>
```
> 4-Activity中动画切换
```java
public class ConstraintLayoutActivity extends AppCompatActivity {

    ConstraintLayout mConstraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_constraint_layout);

        mConstraintLayout = findViewById(R.id.constraintLayout);

        // 1. 转换为垂直的布局
        findViewById(R.id.vertical_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(ConstraintLayoutActivity.this, R.layout.constraint_vertical_layout);
                TransitionManager.beginDelayedTransition(mConstraintLayout);
                constraintSet.applyTo(mConstraintLayout);
            }
        });
        // 2. 转换为水平的布局
        findViewById(R.id.horizontal_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(ConstraintLayoutActivity.this, R.layout.constraint_horizontal_layout);
                TransitionManager.beginDelayedTransition(mConstraintLayout);
                constraintSet.applyTo(mConstraintLayout);
            }
        });
    }
}
```

2、constraintSet.clone(activity,layout):的作用
> 将布局的定位信息，存储到constraintSet中

3、TransitionManager.beginDelayedTransition():的作用
> 通过新旧scene的变化，简单实现了动画效果

## 7-优化Optimizer(1.1.x)

1、通过`app:layout_optimizationLevel`能开启优化，参数如下
```
app:layout_optimizationLevel="none | standard | direct | barrier | chain | demensions"
```
> 1. none: 不优化
> 1. standard: 【默认】仅仅优化直接和障碍约束
> 1. direct: 优化直接约束
> 1. barrier: 优化障碍约束
> 1. chain: 优化chains约束
> 1. demensions: 优化维度测量，减少匹配约束元素的度量数量


## 8-实战

### 实例1-三个图片都是3:4比例、最左最右间隔为5dp、中间间隔为10dp

1、实例
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

## 知识扩展

1、ConstraintLayout的性能问题
> 1. 同样的页面布局，采用`ConstraintLayout的视图树`从`onMeasure()->onDraw()`的时间比`Relativelayout和linearlayout`少很多
> 1. ConstraintLayout的性能体现在了`Android实现的Cassowary algorithm`算法上
> 1. ConstraintLayout要避免嵌套了多层布局

### Android Transition Framework

1. [Android Transition Framework详解---超炫的动画框架](https://www.jianshu.com/p/e497123652b5)

## 参考和学习资料
1. [ConstraintLayout 完全解析 快来优化你的布局吧](https://blog.csdn.net/lmj623565791/article/details/78011599)
1. [android ConstraintLayout使用详解](https://www.jianshu.com/p/f86f800964d2)
1. [官方: ConstraintLayout](https://codelabs.developers.google.com/codelabs/constraint-layout/index.html)
1. [解析ConstraintLayout的性能优势](https://mp.weixin.qq.com/s/gGR2itbY7hh9fo61SxaMQQ)
1. [Beautiful animations using Android ConstraintLayout](https://robinhood.engineering/beautiful-animations-using-android-constraintlayout-eee5b72ecae3)
