#Android动画深入分析

> 1.以面试提问形式总结Android动画所有知识点。适合学习或者复习。
> 2.Java代码部分均用Kotlin方式实现。

1、Android动画分为三种：
>1. View动画(Animation)
>2. 帧动画(属于View动画，但有区别)
>3. 属性动画

## View动画

2、 View动画的分类

|分类|XML标签|效果|
|---|---|---|
|TranslateAnimation|translate|移动View|
|ScaleAnimation|scale|放大或缩小View|
|RotateAnimation|rotate|旋转View|
|AlphaAnimation|alpha|改变透明度|

3、 View动画的使用
>1. 可以采用XML定义动画
>2. 也可以通过代码动态创建
>3. 建议使用XML方法创建，使得动画的可读性更好
>4. XML形式的View动画，需要在`res/anim/`目录下创建XML文件`custom.xml`

4、 View动画通过XML定义的源码和各属性要点
```xml
<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android"
    android:shareInterpolator="true"
    android:duration="2000" //持续时间
    android:fillAfter="true"> //动画后是否停留在结束位置
    <alpha
        android:fillAfter="true"
        android:dutaion="1000"  //动画持续时间
        android:fromAlpha="0.1" //初始透明度，1为不透明，0为完全透明
        android:toAlpha="1"/>

    <scale
        android:fromXScale="0.5" //水平方向缩放，从0.5放大至1.2
        android:toXScale="1.2"
        android:fromYScale="1.1" //垂直方向缩放，从1.1缩小至0.3
        android:toYScale="0.3"
        android:pivotX="0.5"     //轴点(X,Y)
        android:pivotY="0.6"/>

    <translate
        android:fromXDelta="10" //x的初始值
        android:toXDelta="120"  //x的结束值
        android:fromYDelta="0"
        android:toYDelta="100"/>

    <rotate
        android:dutaion="1000"
        android:fromDegrees="0" //旋转开始的角度
        android:toDegrees="180" //旋转结束的角度
        android:pivotY="0"   //根据轴点进行旋转
        android:pivotX="0"/>

</set>
```
>1. `android:duration`表示持续时间，set有duration属性，内部动画的`duration`全部以set的为准
>2. `set标签`没有duration时，内部的各种动画标签均以自身的`duration`为准
>3. `android:fillAfter`动画结束后，是否留在结束位置
>4. `scale`中的`(pivotX,pivotY)`是以该点坐标为中心进行缩放。无论坐标超过View本身的范围。
>5. `rotate`中的`(pivotX,pivotY)`是旋转的中心坐标，以此点进行旋转。

5、如何应用XML定义的动画
```java
val imageview = findViewById<ImageView>(R.id.imaview)
val animation = AnimationUtils.loadAnimation(this, R.anim.custom_animation)
imageview.startAnimation(animation)
```

6、代码动态定义动画的方法(ScaleAnimation为例)
```java
val scaleAnimation = ScaleAnimation(1f, 0.5f, 1f, 0.5f, 100f, 200f)
scaleAnimation.duration = 1000
imageview.startAnimation(scaleAnimation)
```

7、Animation可以调用`setAnimationListener()`设置过程监听：
```java
scaleAnimation.setAnimationListener(object: Animation.AnimationListener{
    override fun onAnimationRepeat(animation: Animation?) {
    }
    override fun onAnimationEnd(animation: Animation?) {
    }
    override fun onAnimationStart(animation: Animation?) {
    }
})
```

8、如何自定义View动画
>1. 自定义动画继承`Animation`
>2. 重写`initialize`-做一些初始化操作
>3. 重写`applyTransformation`-进行一定的矩阵变换即可，通常通过`Camera`简化矩阵转换过程

9、 帧动画的使用和注意点
>1. 帧动画就是`Drawable`中的`AnimationDrawable`，具体在Drawable相关知识点中有所总结
>2. 帧动画有可能出现`OOM`,因此图片不能太大

## View动画的特殊使用场景

10、LayoutAnimation的作用和使用步骤
>1. 作用于ViewGroup，为其每个子元素提供出场动画
>2. 定义布局动画，使用`layoutAnimation`标签, 并引用item动画
>3. 定义item动画(和一般View动画一样定义)
```xml
//布局动画：res/anim/layout_animation
<?xml version="1.0" encoding="utf-8"?>
<layoutAnimation xmlns:android="http://schemas.android.com/apk/res/android"
    android:delay="0.3"
    android:animationOrder="normal"
    android:animation="@anim/item_animation"/>
```
```xml
//item动画：res/anim/item_animation
<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android"
    android:shareInterpolator="true"
    android:duration="300"
    android:fillBefore="false">
    <alpha
        android:fromAlpha="0"
        android:toAlpha="1"/>
    <translate
        android:fromXDelta="500"
        android:toXDelta="0"/>
</set>
```
```xml
//ListView中使用
<ListView
    android:id="@+id/listview"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layoutAnimation="@anim/layout_animation">
</ListView>
```

11、LayoutAnimation要点
>1. `android:delay="0.3"`是指子元素动画延时开始的间隔。比如item动画时间为200ms，则每个子元素动画开始的间隔就是60ms.
>2. `android:animationOrder="normal"`动画的顺序：顺序、逆序和随机
>3. `  android:animation="@anim/item_animation"`引用子元素所采用的动画

12、LayoutAnimation通过代码使用的方法
```java
//1. Item的动画效果
val itemAnimation = AnimationUtils.loadAnimation(this, R.anim.item_animation)
//2. LayoutAnimation
val layoutAnimationController = LayoutAnimationController(itemAnimation)
layoutAnimationController.delay = 0.5f
layoutAnimationController.order = LayoutAnimationController.ORDER_NORMAL
//3. 给ListView设置LayoutAnimation
listview.layoutAnimation = layoutAnimationController
```

13、Acitivity切换动画
>1. 在Acitivty中调用`overridePendingTransition`实现
>2. `overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)`第一个参数，为新Acitivty进入时动画。第二个参数，为旧acitivty退出时动画。
>3. 必须紧挨着`startActivity()`或者`finish()`函数之后调用

14、Fragment的切换动画
>1. 通过`FragmentTransaction`的`setCustomAnimations()`方法设置
>2. 必须是`View`动画

## 属性动画
15、属性动画是什么
>1. API11提出的新特性
>2. 能对任何对象做动画，甚至没有对象
>3. 支持更多的动画效果
>4. 包含了`ValueAnimator`、`ObjectAnimator`、`AnimatorSet`等新概念
>5. `ObjectAnimator`继承自`ValueAnimator`，`AnimatorSet`是动画集合
>6. 实际开发中建议代码来实现，使用XML并不方便，难以确定一些属性的起始值

16、属性动画实例：移动
```java
//X轴平移一定距离
ObjectAnimator.ofFloat(imageView, "translationX", 100f).start()
```

17、属性动画实例：背景颜色变化
```kotlin
val colorAnim = ObjectAnimator.ofInt(imageview, "backgroundColor", -0x7f80, -0x7f7f01)
colorAnim.setDuration(1000)
colorAnim.setEvaluator(ArgbEvaluator())
colorAnim.repeatCount = ValueAnimator.INFINITE
colorAnim.repeatMode = ValueAnimator.REVERSE
colorAnim.start()
```

18、属性动画合集：平移、旋转、透明、缩放
```kotlin
val animatorSet = AnimatorSet()
animatorSet.playTogether(
        ObjectAnimator.ofFloat(imageview, "trasnlationX", 100f),
        ObjectAnimator.ofFloat(imageview, "trasnlationY", 200f),
        ObjectAnimator.ofFloat(imageview, "rotation", 0f, -90f),
        ObjectAnimator.ofFloat(imageview, "scaleX", 1f, 1.5f),
        ObjectAnimator.ofFloat(imageview, "alpha", 1f, 0.25f, 1f)
)
animatorSet.setDuration(5000).start()
```

19、XML中定义属性动画
```xml
<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android"
    android:ordering="together" //表明动画集合中子动画是同时播放还是顺序播放
    >

    <objectAnimator  //对应ObjectAnimator
        android:propertyName="translationX" //属性名称
        android:duration="1000"        //持续时间
        android:valueFrom="200"        //属性起始值
        android:valueTo="500"          //属性结束值
        android:startOffset="10"       //动画的延迟时间，动画开始后，需要多少ms才真正播放动画
        android:repeatCount="10"       //动画的重复次数，默认0，-1为无限循环
        android:repeatMode="restart"   //动画的重复模式
        android:valueType="intType"    //表示propertyName所指属性的类型，但当属性表示颜色时不需要指定valueType
        />

    <animator //对应ValueAnimator
          //相比于objectAnimator缺少一个android:propertyName
        />

    <set> //对应set
        ...
    </set>
</set>
```

20、代码中使用XML中定义的属性动画
```kotlin
val set = AnimatorInflater.loadAnimator(this, R.animator.animator) as AnimatorSet
set.setTarget(listView)
set.start()
```

21、插值器是什么？
>1. 是TimeInterpolator，也叫时间插值器
>2. 作用是：根据时间流逝的百分比来计算出当前属性值改变的百分比
>3. 系统预置了：LinearInterpolator(匀速动画)、AccelerateDecelerateInterpolator(动画两头慢中间快)、DecelerateInterpolator(动画越来越慢)等等

15、
>1.
>2.
>3.

15、
>1.
>2.
>3.

15、
>1.
>2.
>3.

15、
>1.
>2.
>3.

15、
>1.
>2.
>3.
