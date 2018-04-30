转载请注明链接：https://blog.csdn.net/feather_wch/article/details/80021699

#CoordinatorLayout、AppbarLayout和CollapsingToolbarLayout
版本：2018/4/23-1

[TOC]

##CoordinatorLayout

1、导入包：
```xml
compile 'com.android.support:design:28.0.0-alpha1'
```

2、CoordinatorLayout的作用
>1. 加强版`FrameLayout`，适合作为`应用顶层的布局`，提供`交互行为`
>2. 通过给`子View`设定`Behavior`可以实现他们的交互性为。`Behavior`能实现一系列的交互行为和布局变化，包括`侧滑菜单`、`可滑动删除的UI元素`、`View之间`跟随移动。

3、CoordinatorLayout实现两控件联动(隐式关联通过Behavior的layoutDependsOn方法)
>布局: 需要给`观察者`设置`Behavoir`
```xml
<?xml version="1.0" encoding="utf-8"?>
<android.support.design.widget.CoordinatorLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:background="#20b2aa">

        <com.feather.imageview.View.TextPathView
            android:id="@+id/text_path_view"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="NULL"
            android:gravity="center"
            android:textSize="80dp"
            android:textColor="@color/colorAccent"
            android:background="#ffffff"
            app:layout_behavior=".FollowBehavior"/>

        <Button
            android:id="@+id/path_btn"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Button"
            android:textSize="40dp"
            />

</android.support.design.widget.CoordinatorLayout>
```
>自定义`Behavior`
```java
public class FollowBehavior extends CoordinatorLayout.Behavior<TextView>{

    /**
     * 必须要有该构造方法，不然无法通过`XML`构造
     */
    public FollowBehavior(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }
    /**
     * 让布局知道布局中的Button就是被观察者
     */
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, TextView child, View dependency) {
        return dependency instanceof Button;
    }

    /**
     * 2. 被观察者发生变化后，回调该方法。可以改变两者的属性.
     */
    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, TextView child, View dependency) {
        child.setX(dependency.getX());
        child.setY(dependency.getY() + dependency.getHeight() + 10);
        return true;
    }
}
```

4、CoordinatorLayout的Behavior原理
>1. `CoordinatorLayout`将主要`交互行为的处理`交给`Behavior`处理。
>2. `观察者(绑定Behavior)`的`测量、布局`会被`Behavior拦截`
>3. `被观察者`的`Touch事件、嵌套滚动`会被`Behavior拦截`

5、Behavior如何拦截`Touch事件`
>1. `CoordinatorLayout`会`重写onInterceptTouchEvent`方法，让`绑定了Behavior`的子View会先执行`Behavior`的`OnInterceptTouchEvent`。如果`返回true`则交给`Behavior处理Touch事件`，否则还是`子View`原先的处理逻辑。

6、Behavior如何拦截`测量、布局`
>1. `CoordinatorLayout`的`onMeasure`中会执行`behavior`的`onMeasureChild方法`，如果`返回true`则由`Behavior`接管测量过程。
>2. `onLayout`中也是交由`Behavior`接管`布局过程`。

7、View关联的两种方法
>1. `隐式关联`: `为child绑定Behavior`，并在`Bhavior`的`layoutDependsOn()`方法中，如果`dependency`为`被观察者`则返回`返回true`
>2. `显式依赖`：通过` app:layout_anchor=`指定`被观察者ID`，通过`app:layout_anchorGravity=`表明需要的`响应效果`。
```xml
//显式依赖的属性
<com.feather.imageview.View.TextPathView
    xxx
    app:layout_anchor="@+id/path_btn"
    app:layout_anchorGravity="bottom"/>
```

##AppBarLayout
8、AppBarLayout是什么
>1. `垂直的LinearLayout`实现了`Material Design`中`App bar`的`Scrolling Gesture特性`
>2. `AppBarLayout`的`子View`需要声明`滚动行为`，通过`layout_scrollFlags属性`或者`setScrollFlags()方法`指定。
>3. `AppBarLayout`必须要作为`CoordinatorLayout`的直接`子View`
>4. `CoordinatorLayout`中必须要提供可滚动的View`Scrolling View`.
>5. `AppBarLayout`和`Scrolling View`的关联，需要通过将`Scrolling View`的`Behavior`设置为`AppBarLayout.ScrollingViewBehavior`来建立。

9、`app:layout_scrollFlags`的具体参数
>备注：使用`ScrollView`是无效的，原因不明。`RecyclerView`是有效的。

|参数|作用|
|---|---|
|scroll|表明`View`和`Scrolling View`是一体的，必须要有|
|enterAlways|标题栏跟随者`Scrolling View`同时出现。|
|exitUntilCollapsed|先滚动该`View`,达到最小高度后，开始滚动`Scrolling View`。`该View`折叠后，`Scrolling View`滚动到最上方完全出现后，才开始滚动`该View`|
|enterAlwaysCollapsed|View和手指向下滑动，先`Scrolling View`顶部完全出现后，`标题栏`才会出现|
|snap|“观察者-标题栏”要么完全出现，要么完全为0|

10、AppBarLayout的联动使用实例
>1. `CoordinatorLayout`包裹着一个`AppBarLayout`和一个`Scrolling View(RV等等)`
>2. `AppBarLayout`内部控件需要指明`scrollFlags`
>3. `Scrolling View`需要指定`layout_behavior`
```xml
<android.support.design.widget.CoordinatorLayout
  xxx>

    <android.support.design.widget.AppBarLayout
        android:id="@+id/appbarlayout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content">

        <android.support.design.widget.TabLayout
            android:id="@+id/toolbar"
            android:layout_width="match_parent"
            android:layout_height="250dp"
            android:background="#00A999"
            app:layout_scrollFlags="scroll|exitUntilCollapsed|enterAlwaysCollapsed|snap"/>
    </android.support.design.widget.AppBarLayout>

    <android.support.v7.widget.RecyclerView
        android:id="@+id/recyclerview"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layout_behavior="@string/appbar_scrolling_view_behavior">
    </android.support.v7.widget.RecyclerView>
</android.support.design.widget.CoordinatorLayout>
```

11、AppBarLayout展开和折叠的状态判断
```java
//监听是展开还是折叠
mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if(verticalOffset == 0){
            //完全展开状态
        }else if(Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()){
            //折叠状态
        }else{
            //中间状态
        }
    }
});
```


##CollapsingToolBarLayout

12、CollapsingToolBarLayout的特点
>1. 用于实现`具有折叠效果`的顶部栏
>2. `CollapsingToolBarLayout`必须是`AppBarLayout`的直接`子View`

13、CollapsingToolBarLayout包含的特性
|特性||
|---|---|
|Collapsing title(可折叠标题)|布局完全可见时，标题比较大；布局折叠起来时，标题会变小。外观可以通过`expandedTextAppearance`和`collapsedTextAppearance`属性调整。|
|Content Scrim(内容麻布)|如果滚动到临界点，内容麻布会显示或者隐藏。`setContentScrim(Drawable)`设置麻布内容。|
|Status bar Scrim(状态栏纱布)|`setStatusBarScrim(Drawable)`设置.`Android 5.0`上需要`fitSystemWindows=true`才生效|
|Parallax scrolling children(视差滚动子View)|子View以`视差`的效果进行滚动(子View比其他View滚动慢一点)|
|Pinned Position children|子View可以固定在某一位置上。|


14、CollapsingToolbarLayout的重要属性
```xml
/**======================
 * 自身属性
 *======================*/
# 纱布
app:contentScrim="?attr/colorPrimary" //完全折叠后的背景色
app:scrimAnimationDuration="1000"     //纱布动画持续时间
app:scrimVisibleHeightTrigger="20dp"  //纱布可见的最小距离(低于就会开始隐身)

# 状态栏纱布(下面会特别介绍)
app:statusBarScrim="@color/colorAccent"

# 标题
app:title="Hello"             //标题优先于"ToolBar标题Title"
app:titleEnabled="true"       //是否使用大标题，true-使用；false-不使用

app:expandedTitleMargin="1dp" //"自身Title"或者"ToolBar标题Title"的Margin值
app:expandedTitleMarginStart="1dp"
app:expandedTitleMarginEnd="1dp"
app:expandedTitleMarginTop="1dp"
app:expandedTitleMarginBottom="1dp"
app:expandedTitleTextAppearance="?attr/cardBackgroundColor"

app:expandedTitleGravity="bottom|center_horizontal" //"ToolBar标题Title"的重力

# 折叠后Title的重力和样式
app:collapsedTitleGravity="center_horizontal"
app:collapsedTitleTextAppearance="?attr/colorAccent"

# 需要参考AppBarLayout的`app:layout_scrollFlags`
app:layout_scrollFlags=“xxxx”

# 指明toolbar的ID
app:toolbarId="@id/stars_toolbar"
```
```xml
/**======================
 * 内部控件的属性
 *======================*/
# 视差(以ParallaxMultiplier的速度折叠，0:最慢~1:最快)
app:layout_collapseMode="parallax"
app:layout_collapseParallaxMultiplier="0.5"

# 折叠后会固定在顶部
app:layout_collapseMode="pin"
```

15、状态栏纱布(statusBarScrim)如何生效?
>1. 给`CollapsingToolBarLayout`设置`app:statusBarScrim="@color/colorAccent"`
>2. 给父容器`AppBarLayout`设置属性`android:fitsSystemWindows="true"`，防止`状态栏透明后`出现`标题栏`移位到`状态栏`的错误。
>3. 通过`三种方法之一`将状态栏设置`为全透明`
```java
1.在style.xml中增加下面的代码把状态栏设置成全透明

 <item name="android:statusBarColor">@android:color/transparent</item>

2.在style.xml中增加下面的代码把状态栏设置成半透明

 <item name="android:windowTranslucentStatus">true</item>

3.在java代码中onCreate()方法里把状态栏设置成半透明

 getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
```
>补充：如果你已经在style里面把状态栏设置成了透明，那么CollapsingToolbarLayout也想要透明效果的话就无需设置了。

16、FloatingActionButton的属性以及CoordinatorLayout中的锚点属性
```xml
# 参考的锚点
app:layout_anchor=”@id/appbar”

# 与锚点的位置关系
app:layout_anchorGravity=”end|bottom|right”
```

17、代码设置`CollapsingToolbarLayout`的`标题颜色`
```java
//1. 展开时标题颜色
mCollapsingToolbarLayout.setExpandedTitleColor(Color.BLACK);
//2. 折叠时标题颜色
mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
```

##知识储备
1、

##参考资料
1. [CollapsingToolBarLayout用法](https://blog.csdn.net/baidu_31093133/article/details/52807465)
1. [ 利用 CollapsingToolbarLayout 完成联动的动画效果](https://blog.csdn.net/u012045061/article/details/69568807)
1. [CollapsingToolbarLayout 收缩时状态栏颜色设置不生效](https://blog.csdn.net/aaa111/article/details/70244116)
