转载请注明链接: http://blog.csdn.net/feather_wch/article/details/79153727

>1. 以面试提问形式总结Android动画所有知识点。适合学习或者复习。
>2. 主要包括：View动画的定义和使用，属性动画的定义和使用以及插值器、估值器的作用，以及属性动画的原理
>3. 代码部分用Kotlin/java方式实现。

#Android动画深入分析(37题)
版本: 2018/2/11-1

---

[TOC]

部分知识点参考自:http://blog.csdn.net/feather_wch/article/details/78625945#6-animationset

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
* 需要在res文件夹中创建animator文件夹, 并创建XML文件
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

22、估值器是什么?
>1. TypeEvaluator：类型估值算法，也称为估值器
>2. 作用: 根据当前属性改变的百分比来计算改变后的属性值
>3. 系统预置：IntEvaluator(针对整型属性)、FloatEvaluator(针对浮点型)、ArgbEvaluator(针对Color属性)
>4. TimeInterpolator和TypeEvaluator是实现非匀速动画的重要手段。

23、插值器和估值器的实例解析
>假设一种匀速动画，实现在40ms内，x属性实现从0到40的变换。
>题：当t=20ms时，x的值应该是多少？
>1. t=20ms时，时间流逝百分比为`50% = 20 / 40`，`线性插值器`的返回值和输入值一致，因此属性改变百分比为`50%`
>3. 属性改变百分比当前为`50%`,根据`整型估值器`(开始值 + 属性改变百分比 * (结束值 - 开始值))，最终得到改变后的属性值`20=0 + 0.5 * (40 - 0)`

24、属性动画要点
>1. 属性动画要求该属性必须要有`set/get`方法
>2. 插值器和估值器都可以自定义
>3. 插值器自定义需要实现`Interpolator`或者`TimeInterpolator`
>4. 估值器自定义需要实现`TypeEvaluator`接口
>5. `int/float/Color`以外的类型必须要自定义`类型估值算法`

25、属性动画监听器
>1. AnimatorListener(必须要实现四个接口)
```java
animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }
            @Override
            public void onAnimationEnd(Animator animation) {
            }
            @Override
            public void onAnimationCancel(Animator animation) {
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
```
>2. 此外提供AnimatorListenerAdapter用于选择性使用上面的方法
```java
animator.addListener(new AnimatorListenerAdapter() {
    @Override //只使用一个方法
    public void onAnimationStart(Animator animation) {
        super.onAnimationStart(animation);
    }
});
```
>3. AnimatorUpdateListener，用于监听整个过程，每一帧动画，都会调用一次
```java
animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
    }
});
```

26、对任意属性做动画：View动画和属性动画区别
>1. View动画并不支持对控件宽高做动画, 即使进行放大，本质控件的文字等也会被拉伸
>2. 属性动画就可以给任意属性做动画

27、属性动画想要生效，必须满足两个条件
>1. 该属性需要有`set`和`get`方法
>2. `set`方法所做出的属性改变必须能通过UI等改变反映出来(`Button`的setWidth方法本质就不能改变空间的高度)

28、TextView/Button改变宽高的动画为什么不能生效？
>1. TextView以及子类的确有`getWidth/setWidth`方法，满足条件1，不满足条件2
>2. 源码中`getWidth=mRight-mLeft`的确是View的高度`android:layout_width`，该条满足`条件1`
>3. 而`setWidth`设置的是TextView的最大宽度和最小宽度，对应着`android:width`属性，并不是设置View的宽度，因此不满足`条件2`

29、官方针对属性动画生效的条件问题，提供三种解决办法
>1. 有权限的情况下，给对象加上`get/set`方法————一般难以做到，因为无权给SDK内部实现添加方法
>2. 用类来包装原始对象，间接为其提供`get/set`方法
>3. 采用`ValueAnimator`，监听动画过程，自己实现属性的改变

30、用一个类包装原始对象，间接提供get和set方法
```java
private class WrapperView{
    private View view;
    public WrapperView(View view){
        this.view = view;
    }
    public int getWidth(){
        return view.getLayoutParams().width;
    }
    public void setWidth(int width){
        view.getLayoutParams().width = width;
        view.requestLayout();
    }
}
```

31、采用ValueAnimator，监听动画过程，自己实现属性的改变
```kotlin
fun performAnimate(target: View, start: Int, end: Int){
    val valueAnimator = ValueAnimator.ofInt(1, 100)
    //1. 设置每一帧画面的监听器，并且更改属性
    valueAnimator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener{
        private val mEvaluator = IntEvaluator()
        override fun onAnimationUpdate(animation: ValueAnimator) {
            // 当前动画的进度值，整型， 0~100之间
            val currentValue = animation.animatedValue
            // 当前进度所占整个动画过程的比例
            val fraction = animation.getAnimatedFraction()
            // 直接调用整型估值器，最后将计算出的宽度设置给View
            target.layoutParams.width = mEvaluator.evaluate(fraction, start, end)
            target.requestLayout()
        }
    })
    //2. 开始动画
    valueAnimator.setDuration(500).start()
}
```

32、PropertyValuesHolder实现动画效果
>类似于AnimationSet的作用，将多种效果共同作用于对象。

```java
PropertyValuesHolder pvh1 = PropertyValuesHolder.ofFloat("translationY", 200);
PropertyValuesHolder pvh2 = PropertyValuesHolder.ofFloat("scaleX", 1f, 0, 1f);
PropertyValuesHolder pvh3 = PropertyValuesHolder.ofFloat("scaleY", 1f, 0, 1f);
ObjectAnimator.ofPropertyValuesHolder(imageView, pvh1, pvh2, pvh3).setDuration(1000).start();
```

33、ObjectAnimator.ofPropertyValuesHolder()解析
```java
public static ObjectAnimator ofPropertyValuesHolder(Object target,
        PropertyValuesHolder... values) {
    //1. 本质是创建ObjectAnimator对象，并将`PropertyValuesHolder`存入
    ObjectAnimator anim = new ObjectAnimator();
    anim.setTarget(target);
    anim.setValues(values);
    return anim;
}
```
>1. 本质是创建ObjectAnimator对象，并将`PropertyValuesHolder`存入
>2. ObjectAnimator.start()方法最底层本质就是通过`PropertyValuesHolder`的`setupValue`调用`get`方法，`setAnimatedValue方法`去`set`属性值

## 属性动画原理
34、属性动画为什么需要get/set方法？
>1. 属性动画通过传递给`set`的值不一样，并且越来越接近最终值，最终实现动画效果
>2. 如果动画时没有传递初始值，则需要通过`get`方法获取属性的初始值
>3. 如果初始值已经有了，则不需要`get`方法

35、ObjectAnimator的start()流程
>1. `start()`会先判断：若当前东、等待的动画和延迟的动画中有和当前动画相同的动画，就会取消相同的动画；最终调用父类`ValueAnimator`的`start()`
>3. `ValueAnimator`中属性动画需要运行在Looper线程中；最终会调用`AnimationHandler`的start方法，此AnimationHandler并不是Handler，而是Runnable
>5. 该Runnable中涉及JNI层的交互，最终是进入到ValueAnimatior的`doAnimationFrame`方法
>6. `doAnimationFrame`中最后调用`animationFrame()`方法，其内部调用`animateValue()`方法
>7. `animateValue()`中`calculateValue()`用于计算每帧动画所对应的属性值。
>8. 初始化时，若属性初始值没有提供，则调用`get`方法：`PropertyValuesHolder`中的`setupValue`,通过反射调用的`get`方法
>9. 当动画下一帧动画到来时，`PropertyValuesHolder`中的`setAnimatedValue方法`会将新的属性值设置给对象，通过反射调用其`set`方法

36、属性动画原理要点
>1. 属性动画需要运行在Looper线程中
>2. 初始化时，若没有提供属性初始值，`PropertyValuesHolder`的`setupValue`,通过反射调用的`get`方法
>3. 当动画下一帧动画到来时，`PropertyValuesHolder`的`setAnimatedValue方法`会通过反射调用其`set`方法，设置新的属性值

## 动画的注意事项
37、动画使用的7个注意点
>1. OOM：图片数量较多或者图片较大时容易出现OOM，且尽量避免帧动画
>2. 内存泄露：属性动画中无限循环动画，需要在Acitivty退出后及时停止，否则会导致Activity无法释放。验证后发现View动画并不存在此问题。
>3. 兼容性问题： 3.0以下系统上有兼容问题，需要适配
>4. View动画的问题：View动画并不是真正改变View的状态，可能会动画之后View的setVisibility(GONE)失效，需要调用`view.clearAnimation()`清除View动画后才能解决
>5. 不要使用px：要使用dp，px会导致不同设备上有不同效果
>6. 动画元素的交互：3.0后，属性动画点击事件会跟随View而移动，View动画会停留在原位置
>7. 硬件加速: 建议开启硬件加速，会提高动画的流畅性
