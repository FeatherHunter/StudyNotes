#Drawable
##基本知识点
1、Drawable是什么
1. 一个抽象类
2. 用于将一个可绘制的资源，按要求绘制成图形，显示在屏幕之上。
3. Android默认提供了很多Drawable的实现类。
4. 可以通过`XML`或者`代码`使用。

2、xml标签和Drawable的关系图
1. selector|StateListDrawable
2. level-list|LevelListDrawable
3. layer-list|LayerDrawable
4. transition|TransitionDrawable
5. color|ColorDrawable
6. shape|GradientDrawable
7. scale|ScaleDrawable
8. clip|ClipDrawable
9. rotate|RotateDrawable
10. animation-list|AnimationDrawable
11. inset|InsetDrawable
12. bitmap|BitmapDrawable
13. nine-patch|NinePatchDrawable

## shape/GradientDrawable
1、标签：`<shape>`
2、属性标签
1. solid-主体
2. corners-圆角
3. stroke-边(color颜色；width宽度)
4. gradient-渐变色

## VectorDrawable
1、是什么
1. 静态矢量图


2、具有的标签
1. path-具有属性`android:fillColor`和`android:pathData`、`strokeColor/strokeWidth/strokeLineCap`
2. group标签

## AnimatedVectorDrawable
1、是什么
1. 矢量资源的动画

1、矢量动画需要三部分
1. `vector标签`(res/drawable)指明的矢量图
2. `animated-vector`(res/drawable)在`target`中指明目标(vector矢量图)以及相应的动画`android:animation="@anim/rotation"`
3. `objectAnimator`(res/anim)-属性动画

2、使用
```xml
android:drawable="@drawable/矢量动画"
```
```java
for(Drawable drawable : textview.getCompoundDrawables){
  (Animatable)drawable.start();
}
```


## layer-list/LayerDrawable
1、将Drawable进行分层
```xml
<layer-list>
  <item>AAA</item>
  <item>BBB</item>
</layer-list>
```
>1. BBB的内容在AAA之上，可以实现类似单边有边框等效果
>2. 可以实现图片默认背景的logo不会变形(9patch也可以实现)

## selector/StateListDrawable
1、作用
1. 支持在不同状态下，显示不同的Drawable
2. 内部使用`<item>`标签

2、item的状态
1. state_pressed: 按下
2. state_checkable: 是否可以设置checked状态的效果
3. state_selected: 支持selected，并当前处于selected状态
4. state_checked: 支持checkable，并当前处于checked状态

## StateListAnimator
1、作用
1. 根据状态，选择相应的`动画Animator`
2. 需要在`res/animator`目录下的XML文件中使用`selector`标签
3. 动画通过`<objectAnimator>`实现
4. 通过`view.setStateListAnimator()`或者`xml属性：android:stateListAnimator`进行设置。


## BitmapDrawable
1、Bitmap的Drawable
2、属性
1. `android:hint="@color/colorPrimary"`会将所有有颜色的地方都着色为指定颜色，且保留透明度。
2. `android:tintMode="screen"`会去着色没有颜色的区域，有颜色的区域会变成白色
3. `android:tileMode="mirror"`平铺效果：mirror-镜像；repeat-重复；

## NinePatchDrawable
1、具有属性
1. `android:hint="xxxx"`效果与Bitmap相同

## LevelListDrawable
1、是什么？
1. 带层级`level`的drawable
2. 每个Item能指定`minLevel和maxLevel`
3. 根据`setImageLevel()`方法设定的`level`，会显示不同Item所包含的Drawable
