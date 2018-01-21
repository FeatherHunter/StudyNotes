#Android知识点汇总大全
@Auhtor: feather
@Tel: QQ975559549

[TOC]

# View的事件体系(45题)
包括：View的滑动、事件分发和滑动冲突
链接：http://blog.csdn.net/feather_wch/article/details/78955689

## 1-View基础
1、什么是View
> 1. View是所有控件的基类
> 2. View有一个特殊子类ViewGroup，ViewGroup能包含一组View，但ViewGroup的本身也是View。
> 3. 由于View和ViewGourp的存在，意味着View可以是单个控件也可以是一组控件。这种结构形成了View树。

2、View的位置参数：top,left,right,bottom
>1. top-左上角的y轴坐标(全部是相对坐标，相对于父容器)
>2. left-左上角的x轴坐标
>3. right-右下角的x轴坐标
>4. bottom-右下角的y轴坐标
>5. 在View中获取这些成员变量的方法，是getLeft(),getRight(),getTop(),getBottom()即可

3、View从3.0开始新增的参数：x,y,translationX,translationY
>1. x,y是View当前左上角的坐标
>2. translationX,translationY是在滑动/动画后，View当前位置和View最原始位置的距离。
>3. 因此得出等式：x(View左上角当前位置) = left(View左上角初始位置) + translationX(View左上角偏移的距离)
>4. View平移时top、left等参数不变，改变的是x,y,tranlsationX和tranlsationY

4、MotionEvent包含的手指触摸事件
>1. ACTION_DOWN\MOVE\UP对应三个触摸事件。
>2. getX/getY能获得触摸点的坐标，相当于当前View左上角的(x,y)
>3. getRawX/getRawY，获得触摸点相当于手机左上角的(x,y)坐标

5、TouchSlop是什么？
>1. 是一个常量，是滑动的最小距离，低于该值则认为没有滑动。该值与设备相关。
>2. 通过`ViewConfiguration.get(getBaseContext()).scaledTouchSlop`能获得该值，在两次滑动距离小于该值时可以判断未滑动，以提高用户体验。

6、VelocityTracker的作用和使用
>作用： 速度追踪：手指滑动中水平和竖直方向的速度
>使用：
>1. 在View的onTouchEvent中追踪当前点击事情的速度
>2. 通过VelocityTracker的computeCurrentVelocity方法先计算速度
>3. 再获取VelocityTracker的xVelocity/yVelocity获取速度
>4. 速度是指：在给定时间内手机滑过的像素数，如果从右到左，就是负值(例如1000ms内速度为100，就是在1s内滑过100个像素)
>5. 使用完毕时需要调用clear和recycle方法进行清理并回收内存

7、VelocityTracker代码如下
```kotlin
        //追踪速度
        val velocityTracker = VelocityTracker.obtain()
        velocityTracker.addMovement(event)

        //获取当前速度，但必须在获取前进行速度计算
        velocityTracker.computeCurrentVelocity(1000)
        val xVelocity = velocityTracker.xVelocity
        val yVelocity = velocityTracker.yVelocity

        velocityTracker.clear()
        velocityTracker.recycle()
```

8、GestureDetector作用
>手势检测，用于检测用户的单机，滑动，长按，双击等行为。

9、GestureDetector的使用步骤
>1. 创建GestureDetector对象，并实现OnGestureListener接口
>2. 接管目标View的onTouchEvent方法
>3. 之后可以选择性实现OnGestureListener接口和OnDoubleTapListener中的方法

10、GestureDetector的使用代码
```
class CustomView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

//创建GestureDetector对象，并实现OnGestureListener接口
    val mGestureDetector: GestureDetector
        get() = GestureDetector(myGestureListener)
    val myGestureListener = MyGestureListener()

//自定义类实现OnGestureListener接口
    class MyGestureListener: GestureDetector.OnGestureListener {
        override fun onShowPress(p0: MotionEvent?) {
            Log.i("CustomeView", "onShowPress")
        }

        override fun onSingleTapUp(p0: MotionEvent?): Boolean {
            Log.i("CustomeView", "onSingleTapUp")
            return true
        }

        override fun onDown(p0: MotionEvent?): Boolean {
            Log.i("CustomeView", "onDown")
            return true
        }

        override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
            Log.i("CustomeView", "onFling")
            return true
        }

        override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
            Log.i("CustomeView", "onScroll")
            return true
        }

        override fun onLongPress(p0: MotionEvent?) {
            Log.i("CustomeView", "onLongPress")
        }
    }

//接管onTouchEvent方法
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return mGestureDetector.onTouchEvent(event)
    }
}
```

11、Scroller作用
>1. 通过View的scrollTo/scrollBy方法实现的滑动是瞬间完成，没有过渡效果导致用户体验很差。这就需要Scroller

12、坐标系的种类和区别
>1. 坐标系分为Android坐标系和视图View坐标系
>2. Android坐标系以屏幕左上角为原点，向右X轴为正半轴，向下Y轴为正半轴
>3. View坐标系是以当前视图的父视图的左上角作为原点建立的坐标系，方向和Android坐标系一致
>4. 触摸事件中getRawX()和getRawY()获得的就是Android坐标系的坐标
>5. 触摸事件中getX()和getY()获得的就是视图坐标系中的坐标

## 2-View的滑动

13、View滑动的三种方法：
>1. 通过View本身的scrollTo/scrollBy进行滑动
>2. 通过动画给View施加平移效果实现华东
>3. 通过改变View的LayoutParams是的View重新布局从而实现滑动

14、scrollTo和scrollBy
>1. View提供的scrollTo方法，实现了基于参数的绝对滑动——直接到新的x,y坐标处
>2. scrollBy内部是调用scrollTo，实现了基于当前位置的相对滑动

15、scrollTo/By内部的mScrollX和mScrollY的意义
>1. mScrollX的值，相当于手机屏幕相对于View左边缘向右移动的距离，手机屏幕向右移动时，mScrollX的值为正；手机屏幕向左移动(等价于View向右移动)，mScrollX的值为负。
>2. mScrollY和X的情况相似，手机屏幕向下移动，mScrollY为+正值；手机屏幕向上移动，mScrollY为-负值。
>3. mScrollX/Y是根据第一次滑动前的位置来获得的，例如：第一次向左滑动200(等于手机屏幕向右滑动200)，mScrollX = 200；第二次向右滑动50, mScrollX = 200 + （-50）= 150，而不是（-50）。

16、动画实现滑动的方法
>1. 可以通过传统动画或者属性动画的方式实现
>2. 传统动画需要通过设置fillAfter为true来保留动画后的状态(但是无法在动画后的位置进行点击操作，这方面还是属性动画好)
>3. 属性动画会保留动画后的状态，能够点击

17、改变布局参数实现滑动
>1. 通过父控件设置View在父控件的位置，但需要指定父布局的类型，不好
>2. 用ViewGroup的MariginLayoutParams的方法去设置margin

18、布局参数实现滑动代码如下：
```
//方法三：通过布局设置在父控件的位置。但是必须要有父控件, 而且要指定父布局的类型，不好的方法。
RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
layoutParams.leftMargin = getLeft() + offsetX;
layoutParams.topMargin = getTop() + offsetY;
setLayoutParams(layoutParams);

//方法四：用ViewGroup的MarginLayoutParams的方法去设置marign
// 相比于上面方法, 就不需要知道父布局的类型。
// 缺点：滑动到右侧控件会缩小
ViewGroup.MarginLayoutParams mlayoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
mlayoutParams.leftMargin = getLeft() + offsetX;
mlayoutParams.topMargin = getTop() + offsetY;
setLayoutParams(mlayoutParams);
```

19、三种滑动方式的优缺点：
>1. scrollTo/scrollBy: 操作简单，适合对View内容的滑动
>2. 动画：操作简单，主要适用于没有交互的View和实现复杂的动画效果
>3. 改变参数布局：操作稍微复杂，适合有交互的View

## 3-弹性滑动
20、Scroller的用法：
```kotlin
//Scroller进行初始化
    val mScroller: Scroller
        get() = Scroller(context)
//滑动到X,Y
    fun smoothScrollTo(destX: Int, destY: Int){
        val deltaX = destX - scrollX
        val deltaY = destY - scrollY
        //1000ms内向坐标滑动
        mScroller.startScroll(scrollX, scrollY, deltaX, deltaY, 100)
        invalidate()
    }
//重载
    override fun computeScroll() {
        //Scroller判断是否执行完毕
        if(mScroller.computeScrollOffset()){
            //通过父控件进行当前视图的滑动
            (parent as View).scrollTo(mScroller.currX, mScroller.currY)
            //通过重绘来不断调用computeScroll
            invalidate()
        }
    }
```

21、Scroller方法要点解析
>1. 调用startScroll方法时，Scroller只是单纯的保存参数
>2. 之后的invalidate方法导致的View重绘
>3. View重绘之后draw方法会调用自己实现的computeScroll()，才真正实现了滑动

22、Scroller工作原理
>1. Scroller本身不能实现View的滑动，需要配合View的computeScroll方法实现弹性滑动
>2. 不断让View重绘，每一次重绘距离华东的开始时间有一个时间间隔，通过该时间可以得到View当前的滑动距离
>3. View的每次重绘都会导致View的小幅滑动，多次小幅滑动就组成了弹性滑动

23、通过动画实现弹性滑动

5、通过延时策略实现弹性滑动。
>1. 通过handler、View的postDelayed、或者线程的sleep方法。

## 4-View的事件分发机制

24、事件分发
>1. 点击事件的对象就是MotionEvent，因此事件的分发，就是MotionEvent的分发过程，
>2. 点击事件有三个重要方法来完成：dispatchTouchEvent、onInterceptTouchEvent和onTouchEvent

25、dispatchTouchEvent的作用
>1. 用于进行事件的分发
>2. 只要事件传给当前View，该方法一定会被调用
>3. 返回结果受到当前View的onTouchEvent和下级View的dispatchTouchEvent影响
>4. 表示是否消耗当前事件

26、onInterceptTouchEvent的作用
>1. 在dispatchTouchEvent的内部调用，用于判断是否拦截某个事件
>2.

27、onTouchEvent的作用
>1. 在dispatchTouchEvent的中调用，用于处理点击事件
>2. 返回结果表示是否消耗当前事件

28、事件的传递规则：
>1. 点击事件产生后，会先传递给根ViewGroup，并调用dispatchTouchEvent
>2. 之后会通过onInterceptTouchEvent判断是否拦截该事件，如果true，则表示拦截并交给该ViewGroup的onTouchEvent方法进行处理
>3. 如果不拦截，则当前事件会传递给子元素，调用子元素的dispatchTouchEvent，如此反复直到事件被处理

29、View处理事件的优先级
>1. 在View需要处理事件时，会先调用OnTouchListener的onTouch方法，并判断onTouch的返回值
>2. 返回true，表示处理完成，不会调用onTouchEvent方法
>3. 返回false，表示未完成，调用onTouchEvent方法进行处理
>4. 可见，onTouchEvent的优先级没有OnTouchListener高
>5. 平时常用的OnClickListener优先级最低，属于事件传递尾端

30、点击事件传递过程遵循如下顺序：
>1. Activity->Window->View->分发
>2. 如果View的onTouchEvent返回false，则父容器的onTouchEvent会被调用，最终可以传递到Activity的onTouchEvent

31、事件传递规则要点
>1. View一旦拦截事件，则整个事件序列都由它处理(ACTION_DOWN\UP等)，onInterceptTouchEvent不会再调用(因为默认都拦截了)
>2. 但是一个事件序列也可以通过特殊方法交给其他View处理(onTouchEvent)
>3. 如果View开始处理事件(已经拦截)，如果不消耗ACTIO_DOWN事件(onTouchEvent返回false)，则同一事件序列的剩余内容都直接交给父onTouchEvent处理
>4. View消耗了ACTION_DOWN，但不处理其他的事件，整个事件序列会消失(父onTouchEvent)不会调用。这些消失的点击事件最终会传给Activity处理。
>5. ViewGroup默认不拦截任何事件(onInterceptTouchEvent默认返回false)
>6. View没有onInterceptTouchEvent方法，一旦有事件传递给View，onTouchEvent就会被调用
>7. View的onTouchEvent默认都会消耗事件return true, 除非该View不可点击(clickable和longClickable同时为false)
>8. View的enable属性不影响onTouchEvent的默认返回值。即使是disable状态。
>9. onClick的发生前提是当前View可点击，并且收到了down和up事件
>10. 事件传递过程是由父到子，层层分发，可以通过requestDisallowInterceptTouchEvent让子元素干预父元素的事件分发(ACTION_DOWN除外)

32、Activity事件分发的过程
>1. 事件分发过程：Activity->Window->Decor View(当前界面的底层容器，setContentView的View的父容器)->View
>2. Activity的dispatchTouchEvent，会交给Window处理(`getWindow().superDispatchTouchEvent()`)，
>3. 返回true：事件全部结束
>4. 返回false：所有View都没有处理(onTouchEvent返回false)，则调用Activity的onTouchEvent

33、Window事件分发
>1. Window和superDispatchTouchEvent分别是抽象类和抽象方法
>2. Window的实现类是PhoneWindow
>3. PhoneWindow的`spuerDispatchTouchEvent()`直接调用`mDecor.spuerDispatchTouchEvent()`,也就是直接传给了DecorView

34、DecorView的事件分发
>1. DecorView继承自FrameLayout
>2. 作为setContentView的父View，事件会传递给顶层根View(一般都是ViewGroup)

35、根View的事件分发
>1. 顶层View调用dispatchTouchEvent
>2. 调用onInterceptTouchEvent方法
>3. 返回true，事件由当前View处理。如果有onTouchiListener，会执行onTouch，并且屏蔽掉onTouchEvent。没有则执行onTouchEvent。如果设置了onClickListener，会在onTouchEvent后执行onClickListener
>4. 返回false，不拦截，交给子View重复如上步骤。

36、ViewGroup的dispatchTouchEvent解析
>1. 当事件ACTION_DOWN || mFirstTouchTarget != null（不拦截事件并传递给子元素处理）时需要判断是否拦截——(也就是在ACTION_DOWN时才判断是否拦截，ACTION_DOWN无论是否拦截，都会导致后续都不需要判断了)
>2. 但是存在特殊情况(子View会通过requestDisallowInterceptTouchEvent设置FLAG_DISALLOW_INTERCEPT标志位)，一旦设置ViewGroup将只能拦截ACTION_DOWN
>3. 因为ACTION_DOWN到来时会重置状态，`FLAG_DISALLOW_INTERCEPT`也会被重置, 因此依旧总是调用自己的onInterceptTouchEvent方法(判断是否拦截)。

37、Viewgroup不拦截事件时，事件下发给子View处理的逻辑：
>1. 遍历所有子元素，并判断是否能接受点击事件，以及点击事件坐标是否在子元素内。
>2. 如果能接受点击事件，调用dispatchTransformedTouchEvent方法，内部就是调用了子元素的dispatchTouchEvent方法。

38、View对点击事件的处理过程(不包括ViewGroup)


## 5-View的滑动冲突
39、滑动冲突的三种场景
>1. 内层和外层滑动方向不一致：一个垂直，一个水平
>2. 内存和外层滑动方向一致：均垂直or水平
>3. 前两者层层嵌套

40、 滑动冲突处理原则
>1. 对于内外层滑动方向不同，只需要根据滑动方向来给相应控件拦截
>2. 对于内外层滑动方向相同，需要根据业务来进行事件拦截
>3. 前两者嵌套的情况，根据前两种原则层层处理即可。

41、 滑动冲突解决办法
>1. 外部拦截：在父容器进行拦截处理，需要重写父容器的onInterceptTouchEvent方法
>2. 内部拦截：父容器不拦截任何事件，事件都传递给子元素。子元素需要就处理，否则给父容器处理。需要配合`requestDisallowInterceprtTouchEvent`方法。

42、外部拦截法要点
>1. 父容器的`onInterceptTouchEvent`方法中处理
>2. ACTION_DOWN不拦截，一旦拦截会导致后续事件都直接交给父容器处理。
>3. ACTION_MOVE中根据情况进行拦截，拦截：return true，不拦截：return false（外部拦截核心）
>4. ACTION_UP不拦截，如果父控件拦截UP，会导致子元素接收不到UP进一步会让onClick方法无法触发。此外UP拦截也没什么用。

43、内部拦截法要点
>1. 子View的`dispatchTouchEvent`方法处理
>2. ACTION_DOWN，让父容器不拦截(也不能拦截，否则会导致后续事件都无法传递到子View)-`parent.requestDisallowInterceptTouchEvent(true)`
>3. ACTION_MOVE,如父容器需要该事件，则父容器拦截requestDisallowInterceptTouchEvent(false)
>4. ACTION_UP，无操作，正常执行

44、内部拦截Kotlin代码
```kotlin
class CustomHorizontalScrollView(context: Context,
                                 attrs: AttributeSet?,
                                 defStyleAttr: Int,
                                 defStyleRes: Int): HorizontalScrollView(context, attrs, defStyleAttr, defStyleRes){
  //构造器
    constructor(context: Context): this(context, null, 0, 0)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): this(context, attrs, defStyleAttr, 0)

    var downX: Int = 0
    var downY: Int = 0
  //拦截处理
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        var intercepted = super.onInterceptTouchEvent(ev)
        when(ev.action){
	//不拦截
            ACTION_DOWN -> {
                downX = ev.x.toInt()
                downY = ev.y.toInt()
                intercepted = false
            }
	//判断是否拦截
            ACTION_MOVE -> {
                val curX = ev.x.toInt()
                val curY = ev.y.toInt()
                //水平滑动进行拦截
                if(Math.abs(curX - downX) > Math.abs(curY - downY)){
                    intercepted = true
                }
            }
	//不拦截
            ACTION_UP -> intercepted = false
            else -> null
        }
        return intercepted
    }
}

```

45、外部拦截，自定义ScrollView
```kotlin
class CustomScrollView(context: Context,
                       attrs: AttributeSet?,
                       defStyleAttr: Int,
                       defStyleRes: Int): ScrollView(context, attrs, defStyleAttr, defStyleRes) {

    constructor(context: Context) : this(context, null, 0, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    var lastX: Int = 0
    var lastY: Int = 0

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {

        val curX = ev.x.toInt()
        val curY = ev.y.toInt()

        when(ev.action){
            ACTION_DOWN -> {
                parent.requestDisallowInterceptTouchEvent(true)
            }
            ACTION_MOVE -> {
                //如果是水平滑动则交给父容器处理
                if(Math.abs(curX - lastX) > Math.abs(curY - lastY)){
                    parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            ACTION_UP -> null
            else -> null
        }
        lastX = curX
        lastY = curY
        return super.dispatchTouchEvent(ev)
    }
}
```

# View的工作原理和自定义View(36题)
包括：View的三大流程、自定义View
链接：http://blog.csdn.net/feather_wch/article/details/79080571

## 1-ViewRoot和DecorView

1、ViewRoot是什么？
>1. ViewRoot对应于ViewRootImpl类
>2. 是连接WindowManager和DecorView的纽带
>3. View的三大流程(测量、布局、绘制)均通过ViewRoot完成
>4. Activity对象在ActivityThread中创建完毕后，会将DecorView添加到Window中，同时会创建ViewRootImpl，并将ViewRootImpl和DevorView建立关联
>过程代码如下：
>```Java
>root = new ViewRootImpl(view.getContext(), display)
>root.setView(view, wparams, panelParentView)
>```

2、ViewRoot如何完成View的三大流程？
>1. ViewRoot的`performTraversals()`开始View的绘制流程，依次调用`performMeasure()`、`performLayout()`和`performDraw()`
>2. performMeasure()最终执行父容器的measure()方法，并依此执行所有子View的measure方法。
>3. performLayout()和performDraw()同理

3、View三大流程的作用
>1. measure决定了View的宽/高，测量后可以通过`getMeasuredWidth/Height`来获得View测量后的宽/高，除特殊情况外该值等于View最终的宽/高
>2. layout决定了View的顶点坐标以及实际View的宽/高：完成后可以通过`getTop/Bottom/Left/Right`获取顶点坐标，并通过`getWidth/Height()`获得View的最终宽/高
>3. draw决定了View的显示，最终将View显示出来

4、DecorView的作用
>1. DecorView是顶级View，本质就是一个FrameLayout
>2. 包含了两个部分，标题栏和内容栏
>3. 内容栏id是content，也就是activity中setContentView所设置的部分，最终将布局添加到id为content的FrameLayout中
>4. 获取content：`ViewGroup content = findViewById(R.android.id.content)`
>5. 获取设置的View：`content.getChidlAt(0)`

## 2-MeasureSpec

5、MeasureSpec是什么？
>1. MeasureSpec是一种“测量规则”或者“测量说明书”，决定了View的测量过程
>2. View的MeasureSpec会根据自身的LayoutParamse和父容器的MeasureSpec生成。
>3. 最终根据View的MeasureSpec测量出View的宽/高(测量时数据并非最终宽高)

6、MeasureSpec要点解析
>1. MeasureSpec代表一个32位int值，高2位是SpecMode，低30位是SpecSize
>2. SpecMode是指测量模式
>3. SpecSize是指在某种测量模式下的大小
>4. 类MesaureSpec提供了用于SpecMode和SpecSize打包和解包的方法

7、测量模式SpecMode的类型
>1. UNSPECIFIED：父容器不对View有任何限制，一般用于系统内部
>2. EXACTLY：精准模式，View的最终大小就是SpecSize指定的值（对应于LayoutParams的match_parent和具体的数值）
>3. AT_MOST：最大值模式，大小不能大于父容器指定的值SpecSize(对应于wrap_content)

8、MeasureSpec和LayoutParams的对应关系
>1. View的MeasureSpec是需要通过`自身的LayoutParams`和`父容器`一起才能决定
>2. DecorView(顶级View)是例外，其本身MeasureSpec由`窗口尺寸`和`自身LayoutParams`共同决定
>3. MeasureSpec一旦确定，onMeasure中就可以确定View的测量宽/高

9、普通View的Measure的创建规则
>1. View本身布局参数为具体dp/px数值，模式：EXACTLY，尺寸：自身尺寸(不管父容器的MeasureSpec)
>2. View为match_parent， 模式：EXACTLY/AT_MOST由父容器MeasureSpec决定，尺寸：父容器目前可用大小
>3. View为wrap_content，模式：AT_MOST,尺寸：父容器可用尺寸(不能超过该尺寸)
>4. 当父容器为UNSPECIFIED时，View为具体数值时规则不变；其余match_parent/wrap_content，模式均为：UNSPECIFIED，尺寸：0
>5. UNSPECIFIED一般用于系统内部多次measure的情况，不需要关注该模式。

## 3-View的工作流程
10、View的工作流程以及具体的功能
>1. measure：测量——确定View的测量宽/高
>2. layout：布局——确定View的最终宽/高和四个顶点的位置
>3. draw：绘制——将View绘制到屏幕上

11、View工作流程的入口
>1. Activity调用startActivity方法，最终会调用ActivityThread的handleLaunchActivity方法
>2. handleLaunchActivity会调用performLauchActivity方法(会调用Activity的onCreate，并完成DecorView的创建)和handleResumeActivity方法
>3. handleResumeActivity方法会做四件事：performResumeActivity(调用activity的onResume方法)、getDecorView(获取DecorView)、getWindowManager(获取WindowManager)、WindowManager.addView(decor, 1)
>4. WindowManager.addView(decor, 1)本质是调用WindowManagerGlobal的addView方法。其中主要做两件事：1、创建ViewRootImpl实例 2、root.setView(decor, ....)将DecorView作为参数添加到ViewRoot中，这样就将DecorView加载到了Window中
>5. ViewRootImpl还有一个方法performTraveals方法，用于让ViewTree开始View的工作流程：其中会调用performMeasure/Layout/Draw()三个方法,分别对应于View的三大流程。

### 1-measure过程
12、View的measure过程及要点
>1. View的measure方法是final类型方法——表明该方法无法被重载
>2. View的measure方法会调用onMeasure方法，onMeasure会调用setMeasuredDimension方法设置View宽/高的测量值

13、View的onMeasure源码要点
```Java
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //1. setMeasuredDimension方法设置View宽/高的测量值
        setMeasuredDimension(
                //2. 第一个参数是获得的测量宽/高(通过getDefaultSize获取)
                getDefaultSize(getSuggestedMinimumWidth(),  //3. 获取的建议最小的宽/高
                                    widthMeasureSpec),
                getDefaultSize(getSuggestedMinimumHeight(),
                                    heightMeasureSpec));
}
```
>1. setMeasuredDimension方法设置View宽/高的测量值（测量值通过getDefaultSize获取）
>2. getDefaultSize用于获取View的测量宽/高

14、View的getDefaultSize源码要点
>```java
>    //1. 获取View宽和高的测量值
>    public static int getDefaultSize(int size, int measureSpec) {
>        int result = size;
>        int specMode = MeasureSpec.getMode(measureSpec);
>        int specSize = MeasureSpec.getSize(measureSpec);
>
>        switch (specMode) {
>        //2. UNSPECIFIED模式时，宽/高为第一个参数也就是getSuggestedMinimumWidth()获取的建议最小值
>        case MeasureSpec.UNSPECIFIED:
>            result = size;
>            break;
>        //3. AT_MOST(wrap_content)和EXACTLY(match_parent/具体值dp等)这两个模式下，View宽高的测量值为当前View的MeasureSpec(测量规格)中指定的尺寸specsize
>        case MeasureSpec.AT_MOST:
>        case MeasureSpec.EXACTLY:
>            result = specSize;
>            break;
>        }
>        return result;
>    }
>```

15、View的getSuggestedMinimumWidth/Height()源码要点
```java
//获取建议的最小宽度
protected int getSuggestedMinimumWidth() {
        return (mBackground == null) ? mMinWidth : max(mMinWidth, mBackground.getMinimumWidth());
}
```
>1. 如果View没有背景，View的最小宽度就为`android:minWidth`这个参数指定的值(mMinWidth),没有指定则默认为0
>2. 如果View有背景，会从mMinWidth和背景的最小宽度中取最大值。
>3. 背景的最小宽度(getMinimumWidth())本质就是Drawable的原始宽度(ShapeDrawable无原始宽度,BitmapDrawable有原始宽度——图片的尺寸)

16、View的wrap_content和match_parent效果一致的原因分析
>1. 根据View的onMeasure方法中的getDefaultSize方法，我们可以发现在两种模式下，View的测量值等于该View的测量规格MeasureSpec中的尺寸。
>2. View的MeasureSpec本质是由自身的LayoutParams和父容器的MeasureSpec决定的。
>3. 当View为wrap_content时，该View的模式为AT_MOST，且尺寸specSize为父容器的剩余空间大小。
>4. 当View为match_parent时，该View的模式跟随父容器的模式(AT_MOST/EXACTLY), 且尺寸specSize为父容器的剩余空间大小。
>5. 因此getDefaultSize中无论View是哪种模式，最终测量宽/高均等于尺寸specSize，因此两种属性效果是完全一样的(View的大小充满了父容器的剩余空间)
>6. 除非给定View固定的宽/高，View的specSize才会等于该固定值。

17、自定义View需要重写onMeasure方法，并写明两种模式的处理方法
```java
    //1. 重写onMeasure，特殊处理wrap_content的情况
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize =  MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize =  MeasureSpec.getSize(heightMeasureSpec);

        if(widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST){
            //2. 均为wrap_content时, 将值设置为android:minWidth/Height属性指定的值
            setMeasuredDimension(mWidth, mHeight);
        }else if(widthSpecMode == MeasureSpec.AT_MOST){
            //3. 哪个为wrap_content哪个就用android:minXXX属性给定的最小值
            setMeasuredDimension(mWidth, heightSpecSize);
        }else if(heightSpecMode == MeasureSpec.AT_MOST){
            setMeasuredDimension(widthSpecSize, mHeight);
        }
    }
```

18、ViewGroup(抽象类)的measure流程
>1. ViewGroup没有onMeasure方法，只定义了measureChildren方法(onMeasure根据不同布局难以统一)
>2. measureChildren中遍历所有子元素并调用measureChild方法
>3. measureChild方法中会获取子View的MeasureSpec，然后调用子元素View的measure方法进行测量

19、getChildMeasureSpec获取子元素MeasureSpec的要点
>1. 子View的MeasureSpec是根据自身的LayoutParams和父容器SpecMode生成
>2. 当子View的布局参数为wrap_content，且父容器模式为AT_MOST时，效果与子元素布局为match_parent是一样的。因此当子View的布局参数为wrap_content时，需要指定默认的宽/高

20、LinearLayout的onMeasure()分析
>1. ViewGroup因为布局的不同，无法统一onMeasure方法，具体内容根据布局的不同而不同，这里直接以LinearLayout进行分析
>2. onMeasure会根据`orientation`选择measureVertical或者measureHorizontal进行测量
>3. measureVertical本质是遍历子元素，并执行子元素的measure方法，并获得子元素的总高度以及子元素在竖直方向上的margin等。
>4. 最终LinearLayout会测量自己的大小，在orientation的方向上，如果布局是match_parent或者具体数值，测量过程与View一致(高度为specSize)；如果布局是wrap_content，高度是所有子元素高度总和，且不会超过父容器的剩余空间，最终高度需要考虑在竖直方向上的padding

21、如何获取View的测量宽/高
>1. 在measure完成后，可以通过getMeasuredWidth/Height()方法，就能获得View的测量宽高
>2. 在一定极端情况下，系统需要多次measure，因此得到的值可能不准确，最好的办法是在onLayout方法中获得测量宽/高或者最终宽/高

22、如何在Activity启动时获得View的宽/高
>1. Activity的生命周期与View的measure不是同步运行，因此在onCreate/onStart/onResume均无法正确得到
>2. 若在View没有测量好时，去获得宽高，会导致最终结果为0
>3. 有四种办法去正确获得宽高

21、onWindowFocusChanged获得View的宽/高
```java
//1. View已经初始化完毕，可以获得宽高
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//2. Activity得到焦点和失去焦点均会调用一次(频繁onResume和onPause会导致频繁调用)
        if(hasFocus){
            int width = view.getMeasuredWidth();
            int height = view.getMeasuredHeight();
        }
    }
```

22、view.post(runnable)获得View的宽/高
```java
//1. 通过post将一个runnable投递到消息队列尾部
        view.post(new Runnable() {
            @Override
//2. 等到Looper调用次runnable时，View已经完成初始化
            public void run() {
                int width = view.getMeasuredWidth();
                int height = view.getMeasuredHeight();
            }
        });
```

23、ViewTreeObserver获得View的宽/高（Kotlin版）

``` Kotlin
    val observer = imageView.viewTreeObserver
	//1. 使用ViewTreeObserver的接口，可以再View树状态改变或者View树内部View的可见性改变时，onGlobalLayout会被毁掉
    observer.addOnGlobalLayoutListener(object :ViewTreeObserver.OnGlobalLayoutListener {
            //2. 能正确获取View宽/高
			override fun onGlobalLayout() {
			//3. 随着View树状态改变，会多次调用。因此需要移除监听器
                imageView.viewTreeObserver.removeGlobalOnLayoutListener(this)
                val width = imageView.measuredWidth
                val height = imageView.measuredHeight
            }
        })
```

---

24、view.measure()获得View的宽/高(Kotlin)
>1. mathc_parent的情况下是不可以的，因为需要知道parent的size，这里无法获取。
>2. 具体数值
``` Kotlin
        //1. 具体数值时(dp/px),让View重新测量
        val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(100, View.MeasureSpec.EXACTLY)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(100, View.MeasureSpec.EXACTLY)
        imageView.measure(widthMeasureSpec, heightMeasureSpec)
        //2. 完成后就可以获得宽/高
        val width = imageView.width
        val height = imageView.height
```
>3. wrap_content
``` Kotlin
        //1. wrap_content,将specSize设置为30位二进制的最大值 (1 << 30) - 1,让View重新测量(在AT_MOST情况下是合理的)
        val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec((1 shl 30) - 1, View.MeasureSpec.AT_MOST)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec((1 shl 30) - 1, View.MeasureSpec.AT_MOST)
        imageView.measure(widthMeasureSpec, heightMeasureSpec)
        //2. 完成后就可以获得宽/高
        val width = imageView.width
        val height = imageView.height
```

### 2-layout过程
25、View的layout过程
>1. 使用`layout`方法确定View本身的位置
>2. `layout`中调用`onLayout`方法确定所有子View的位置

26、View的layout()源码分析
>1. 调用setFrame()设置View四个定点位置(即初始化mLeft,mRight,mTop,mBottom的值)
>2. 之后调用onLayout确定子View位置，该方法类似于onMeasure，View和ViewGroup中均没有实现，具体实现与具体布局有关。

27、LinearLayout的onLayout方法
>1. 根据orientation选择调用layoutVertical或者layoutHorizontal
>2. layoutVertical中会遍历所有子元素并调用setChildFrame(里面直接调用子元素的layout方法)
>3. 层层传递下去完成了整个View树的layout过程
>4. setChildFrame中的宽/高实际就是子元素的测量宽/高(getMeasure...后直接传入)

28、View的测量宽高和最终宽高有什么区别？
>1. 等价于getMeasuredWidth和getWidth有什么区别
>2. getWidth = mRight - mLeft，结合源码测量值和最终值是完全相等的。
>3. 区别在于：测量宽高形成于measure过程，最终宽高形成于layout过程(赋值时机不同)
>4. 也有可能导致两者不一致：强行重写View的layout方法，在传参方面改变最终宽/高（虽然这样毫无实际意义）
>5. 某些情况下，View需要多次measure才能确定自己的测量宽高，在前几次测量中等到的值可能有最终宽高不一致。但是最终结果上，测量宽高=最终宽高

### 3-draw过程
29、draw的步骤
>1. 绘制背景(drawBackground(canvas))
>2. 绘制自己(onDraw)
>3. 绘制children(dispatchDraw)-遍历调用所有子View的draw方法
>4. 绘制装饰(如onDrawScollBars)

30、View特殊方法setWillNotDraw
>1. 若一个View不绘制任何内容，需要将该标志置为true，系统会进行相应优化
>2. 默认View不开启该标志位
>3. 默认ViewGroup开启该标志位
>4. 如果我们自定义控件继承自ViewGroup并且本身不进行绘制时，就可以开启该标志位
>5. 当该ViewGroup明确通过onDraw绘制内容时，就需要显式关闭WILL_NOT_DRAW标志位。

## 4-自定义View

31、自定义View的分类

|分类|实现方法|备注|
|---|---|---|
|1.继承View|重写onDraw()方法|需要支持`wrap_content`和`padding`|
|2.继承ViewGroup|需要处理ViewGroup的`测量`和`布局`|需要处理子元素的`测量`和`布局`过程|
|3.继承特定的View(TextView等)|扩展较容易实现|不需要支持`wrap_content`和`padding`|
|4.继承特定的ViewGroup(LinearLayout等)|方法2能实现的效果方法4都能实现|___|

32、自定义View要点
>1. View需要支持wrap_content
>2. View需要支持padding
>3. 尽量不要再View中使用Handler，View已经有post系列方法
>4. View如果有线程或者动画，需要及时停止(onDetachedFromWindow会在View被remove时调用)——避免内存泄露
>5. View如果有滑动嵌套情形，需要处理好滑动冲突

33、直接继承自View的实现步骤和方法：
>1. 重写onDraw，在onDraw中处理`padding`
>2. 重写onMeasure，额外处理`wrap_content `的情况
>3. 设定自定义属性attrs(属性相关xml文件，以及在onDraw中进行处理)

``` Kotlin
class CustomViewByView(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int):
        View(context, attrs, defStyleAttr, defStyleRes){
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int):this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet):this(context, attrs, 0, 0)
    constructor(context: Context): this(context, null, 0, 0)

    var mColor = Color.RED

    init {
        //3. 自定义attrs中属性的获取
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomViewByView)
        mColor = typedArray.getColor(R.styleable.CustomViewByView_circle_color, Color.RED)
        typedArray.recycle()
    }

    //1. 重写onDraw方法
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = mColor //属性attrs给定的颜色
        //2. 需要处理padding
        val width = width - paddingLeft - paddingRight
        val height = height - paddingTop - paddingBottom
        canvas.drawCircle(paddingLeft + width.toFloat() / 2, paddingTop + height.toFloat() / 2,
                Math.min(width, height).toFloat() / 2, paint)
    }

    //3. 特别处理wrap_content的情况，给定一个最小值
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        when{
            // 为wrap_content的边均使用最小值mMinWidth/mMinHeight
            widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST -> {
                setMeasuredDimension(minimumWidth, minimumHeight)
            }
            widthSpecMode == MeasureSpec.AT_MOST -> {
                setMeasuredDimension(minimumWidth, heightSpecSize)
            }
            heightSpecMode == MeasureSpec.AT_MOST -> {
                setMeasuredDimension(widthSpecSize, minimumHeight)
            }
        }
    }
}
```

34、自定义属性实现的步骤和源码
>1. 在values目录下定义一个属性文件`attrs_circle_view`，文件名可任意
>2. 在控件的布局中使用该属性（需要添加`xmlns:app="http://schemas.android.com/apk/res-auto"`）
>3. 在自定义View中处理自定义的属性

```xml
<com.example.a6005001819.androiddeveloper.CustomViewByView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:background="@color/colorPrimary"
    android:padding="30dp"
    android:minWidth="100dp"
    android:minHeight="100dp"
    app:circle_color="@color/colorAccent"/>
```
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <declare-styleable name="CustomViewByView">
        <attr name="circle_color" format="color"/>
    </declare-styleable>
</resources>
```

35、自定义View：继承自ViewGroup
>1. 需要重写onMeasure方法，进行测量(测量子元素，测量自身-需要处理margin和padding)
>2. 必须实现onLayout方法，并且处理margin和padding属性
>3. 要支持margin功能，需要重写LayoutParmas相关方法
```Kotlin
class CustomViewByViewGroup(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int):
        ViewGroup(context, attrs, defStyleAttr, defStyleRes){

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int):this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet):this(context, attrs, 0, 0)
    constructor(context: Context): this(context, null, 0, 0)

    /**
     * 1. 继承ViewGroup必须实现onLayout方法
     */
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var childLeft = paddingLeft //需要处理padding
        for(i in 0 until childCount){
            val childView = getChildAt(i)
            if(childView.visibility != View.GONE){
                val childWidth = childView.measuredWidth

                //2. 额外处理margin属性
                val childLayoutParams = childView.layoutParams as MarginLayoutParams
                childLeft += childLayoutParams.leftMargin
                childView.layout(childLeft,
                        childLayoutParams.topMargin + paddingTop,
                        childLeft + childWidth,
                        childLayoutParams.topMargin  + paddingTop + childView.measuredHeight) //一定要根据margin处理好四个顶点坐标
                childLeft += childWidth + childLayoutParams.rightMargin
            }
        }
    }

    /**
     * 2. 定义ViewGroup的布局测量过程(也需要额外处理margin)
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)

        var measureWidth = 0
        var measureHeight = 0

        //2. 需要测量所有子View!
        measureChildren(widthMeasureSpec, heightMeasureSpec)

        //3. 本身宽高的模式均为wrap_content, 需要根据子View来获得
        if(widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST){
            for(i in 0 until childCount){
                val childView = getChildAt(i)
                measureWidth += childView.measuredWidth //测量出总宽度

                //6. 处理marigin
                val childLayoutParams = childView.layoutParams as MarginLayoutParams
                measureWidth += childLayoutParams.leftMargin + childLayoutParams.rightMargin

                val totalCurChildHeight = childView.measuredHeight + childLayoutParams.topMargin + childLayoutParams.bottomMargin
                if(totalCurChildHeight > measureHeight){
                    measureHeight = totalCurChildHeight //选取子View中高度最大的
                }
            }
            //7. 处理padding
            measureWidth += paddingLeft + paddingRight
            measureHeight += paddingTop + paddingBottom
            setMeasuredDimension(measureWidth, measureHeight)
        }
        //4. 仅有高度是wrap_content
        else if(heightSpecMode == MeasureSpec.AT_MOST){
            //获取所有子View最大的高度，宽度直接用给定的尺寸
            for(i in 0 until childCount){
                val childView = getChildAt(i)

                // 处理高度(wrap_content)上marigin
                val childLayoutParams = childView.layoutParams as MarginLayoutParams

                val totalCurChildHeight = childView.measuredHeight + childLayoutParams.topMargin + childLayoutParams.bottomMargin
                if(totalCurChildHeight > measureHeight){
                    measureHeight = totalCurChildHeight //选取子View中高度最大的
                }
            }
            measureHeight += paddingTop + paddingBottom //处理高度的padding
            setMeasuredDimension(widthSpecSize, measureHeight)
        }
        //5. 仅有宽度是wrap_content
        else if(widthSpecMode == MeasureSpec.AT_MOST){
            for(i in 0 until childCount){
                val childView = getChildAt(i)
                measureWidth += childView.measuredWidth

                //  处理宽度(wrap_content)上marigin
                val childLayoutParams = childView.layoutParams as MarginLayoutParams
                measureWidth += childLayoutParams.leftMargin + childLayoutParams.rightMargin
            }
            measureWidth += paddingLeft + paddingRight            //  处理宽度的padding
            setMeasuredDimension(measureWidth, heightSpecSize)//高度直接用给定的尺寸
        }
    }

    /**
     * 3. 要支持Margin功能，必须要重写方法，并实现自己LayoutParams
     */
    override fun generateDefaultLayoutParams() = MyLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    override fun generateLayoutParams(attrs: AttributeSet) = MyLayoutParams(context, attrs)
    override fun generateLayoutParams(p: LayoutParams): MyLayoutParams{
        when(p){
            is LayoutParams -> return MyLayoutParams(p)
            is MarginLayoutParams ->  return MyLayoutParams(p)
            else -> return MyLayoutParams(p)
        }
    }

    open class MyLayoutParams : MarginLayoutParams {
        constructor(c: Context, attrs: AttributeSet) : super(c, attrs)
        constructor(width: Int, height: Int) : super(width, height)
        constructor(p: ViewGroup.LayoutParams) : super(p) {}
        constructor(source: ViewGroup.MarginLayoutParams) : super(source)
    }
}
```

36、自定义View的思想
>面对陌生的自定义View的时候，需要掌握基本功：View的弹性滑动、滑动冲突、绘制原理。个人理解就是处理好三大流程：测量、布局和绘制。

# EventBus要点和源码分析(21题)

>总结EventBus的知识点
>分析EventBus源码的要点，包括：
>1. EventBus的构造源码
>2. 订阅者注册的源码
>3. 事件发送的源码
>4. 订阅者取消注册的源码

## 1-EventBus要点

1、事件总线的作用
>简化Activity、Fragment、Thread和Service之前的通信并且有更高的质量

2、EventBus作用和优缺点
>1. 针对android优化的发布-订阅事件总线。
>2. 开销小，相比于广播效率高(广播如果传递实体数据，需要序列化)
>3. 将发送者和接受者解耦

3、EventBus三要素
>1. Event事件
>2. SubScriber：事件订阅者。EventBus3.0开始可以指定任意事件处理方法，只需要添加一个注解@Subscribe并且指定线程模型(默认为POSTING)
>3. Publisher：事件发布者，直接调用EventBus的post(Object)方法

4、EventBus的四种线程模型
|ThreadMode|作用|备注|
|---|---|---|
|POSTING（默认）|哪个线程发布事件，处理函数就在哪个线程处理|事件处理时要避免执行耗时操作，会阻塞事件的传递，甚至导致ANR|
|MAIN|事件在UI线程中处理|避免耗时操作|
|BACKGROUND|若事件在UI线程发布，则事件在新线程处理；若事件在子线程发布，则直接在该线程处理|禁止UI操作|
|ASYNC|无论事件在哪发布，事件都在新子线程中处理|禁止UI操作|

5、EventBus3.0前的只能使用规定的消息处理方法(对应线程模型)
>1. onEvent
>2. onEventMainThread
>3. onEventBackgroundThread
>4. onEventAsync

6、EventBus的使用
>1. 自定义一个事件类，如：`class MsgEvent`
>2. 在需要订阅事件的地方注册事件：`EventBus.getDefault().register(this)`
>3. 发送事件：`EventBus.getDefault().post(msgEvent)`
>4. 处理事件
>```java
>    @Subscribe
    public void onEventMainThread(MsgEvent event)
    {
        ...
    }
    //Since EventBus 3.0
    @Subscribe (threadMode = ThreadMode.MAIN)
    public void customEventHandler(MsgEvent event)
    {
        ...
    }
>```
>5. 取消事件订阅：`EventBus.getDefault().unregister(this)`

7、ProGuard需要加入EventBus相关的混淆规则

8、EventBus的粘性事件
>是指发送事件后，再订阅该事件也可以接收到该事件(类似于粘性广播)

9、EventBus粘性事件的处理和发送
>```java
@Subscribe (threadMode = ThreadMode.MAIN, sticky = true)
public void customStickyEventHandler(MsgEvent event)
{
     ...
}
>```
>发送：
>
>```java
EventBus.getDefault().postSticky(new MsgEvent("粘性事件"));
>```

## 2-EventBus的构造

10、EventBus的构造方法
>```java
>    //1. 单例模式，双重检查
    public static EventBus getDefault() {
        if(defaultInstance == null) {
            Class var0 = EventBus.class;
            synchronized(EventBus.class) {
                if(defaultInstance == null) {
                    defaultInstance = new EventBus();
                }
            }
        }
        return defaultInstance;
    }
    //2. 建造者模式
    public EventBus() {
        this(DEFAULT_BUILDER);
    }
    private static final EventBusBuilder DEFAULT_BUILDER = new EventBusBuilder();
>```
>1. getDefault采用单例模式，使用双重检查(DLC)
>2. EventBus的构造方法里面，通过默认EvenBusBuilder进行构造(建造者模式)
>3. 我们可以通过构造一个EvenBusBuilder对EventBus进行配置

## 3-订阅者注册

11、EventBus的注册源码要点：
```java
    //EventBus的订阅者注册
    public void register(Object subscriber) {
        Class subscriberClass = subscriber.getClass();
        //1. 获取订阅者所有的需要订阅的方法(SubscriberMethod中保存了订阅方法的Method对象、线程模式、事件类型、优先级、是否粘性事件等属性)
        List subscriberMethods = this.subscriberMethodFinder.findSubscriberMethods(subscriberClass);
        synchronized(this) {
            Iterator var5 = subscriberMethods.iterator();

         //2. 遍历所有需要订阅的方法，并进行注册
            while(var5.hasNext()) {
                SubscriberMethod subscriberMethod = (SubscriberMethod)var5.next();
                this.subscribe(subscriber, subscriberMethod);
            }

        }
    }
```

12、EventBus注册的findSubscriberMethods源码要点
```java
    /**
     * 获取订阅者的所有订阅方法(onEventMainThread等等)
     */
    List<SubscriberMethod> findSubscriberMethods(Class<?> subscriberClass) {
        //1. 查找是否有缓存的订阅方法的集合
        List subscriberMethods = (List)METHOD_CACHE.get(subscriberClass);
        //2. 找到方法立即返回
        if(subscriberMethods != null) {
            return subscriberMethods;
        } else {
            //3. 选择采取何种方法查询订阅方法的集合(ignoreGeneratedIndex指是否忽略注解器生成的MyEventBusIndex，默认值false)
            if(this.ignoreGeneratedIndex) {
                subscriberMethods = this.findUsingReflection(subscriberClass);
            } else {
                //4. 默认通过单例模式获取默认的EventBus对象(ignoreGeneratedIndex=false)
                subscriberMethods = this.findUsingInfo(subscriberClass);
            }

            //5.获取订阅方法集合后，放入缓存中
            if(subscriberMethods.isEmpty()) {
                throw new EventBusException("Subscriber " + subscriberClass + " and its super classes have no public methods with the @Subscribe annotation");
            } else {
                //放入缓存
                METHOD_CACHE.put(subscriberClass, subscriberMethods);
                return subscriberMethods;
            }
        }
    }
```

13、EventBus注册的findUsingInfo源码要点
```
    private List<SubscriberMethod> findUsingInfo(Class<?> subscriberClass) {
        SubscriberMethodFinder.FindState findState = this.prepareFindState();
        findState.initForSubscriber(subscriberClass);

        for(; findState.clazz != null; findState.moveToSuperclass()) {
            //1. 获取订阅者信息(默认没有忽略注解器生成的MyEventBusIndex，下面会进行判断)
            findState.subscriberInfo = this.getSubscriberInfo(findState);
            //2. 判断是否配置了MyEventBusIndex，若配置了Info不为空
            if(findState.subscriberInfo != null) {
                //4. 通过订阅者信息获得订阅方法的相关信息
                SubscriberMethod[] array = findState.subscriberInfo.getSubscriberMethods();
                SubscriberMethod[] var4 = array;
                int var5 = array.length;

                for(int var6 = 0; var6 < var5; ++var6) {
                    SubscriberMethod subscriberMethod = var4[var6];
                    if(findState.checkAdd(subscriberMethod.method, subscriberMethod.eventType)) {
                        findState.subscriberMethods.add(subscriberMethod);
                    }
                }
            } else {
                //3. 没有配置MyEventBusIndex，会将订阅方法保存到findState中
                this.findUsingReflectionInSingleClass(findState);
            }
        }
        //5. 对findState进行回收处理并且返回订阅方法的List集合
        return this.getMethodsAndRelease(findState);
    }

```

14、EventBus注册的findUsingReflectionInSingleClass源码要点

```
    private void findUsingReflectionInSingleClass(SubscriberMethodFinder.FindState findState) {
        Method[] methods;
        try {
            //1. 通过反射来获得订阅者中的所有方法
            methods = findState.clazz.getDeclaredMethods();
        } catch (Throwable var12) {
            methods = findState.clazz.getMethods();
            findState.skipSuperClasses = true;
        }
        ...
        //2. 根据方法的类型、参数和注解找到订阅方法
        ...
        //3. 将找到的订阅方法的相关信息保存到findState中
        if(findState.checkAdd(method, eventType)) {
            ThreadMode threadMode = methodName1.threadMode();
            findState.subscriberMethods.add(new SubscriberMethod(method, eventType, threadMode, methodName1.priority(), methodName1.sticky()));
        }
        ...
    }
```

15、EventBus注册的subscribe(订阅者注册)源码要点
```java
    /**======================================================
     *  订阅者的注册
     *  @位于： EventBus的register()
     *  @本质思想：
     *    1. 将订阅者对象添加到[订阅者对象集合]中(根据订阅方法的优先级)-进行注册
     *        [订阅者对象集合]需要根据[事件类型]添加到[按事件类型分类的总订阅者对象集合]中(subscriptionsByEventType)
     *    2. 将事件类型添加到[事件类型集合]中
     *        [事件类型集合]需要根据[订阅者]添加到[按订阅者分类的总事件类型集合]中(typesBySubscriber)
     *    3. 对粘性事件进行额外处理
     *=======================================================*/
    private void subscribe(Object subscriber, SubscriberMethod subscriberMethod) {
        Class eventType = subscriberMethod.eventType;
        //1. 根据订阅者(subscriber)和订阅方法(subscriberMethod)创建一个订阅对象(Subscription)
        Subscription newSubscription = new Subscription(subscriber, subscriberMethod);
        //2. 根据事件类型(EventType)获取订阅对象集合
        CopyOnWriteArrayList subscriptions = (CopyOnWriteArrayList)this.subscriptionsByEventType.get(eventType);
        //3. 订阅对象集合为空，则重新创建集合，并将subscriptions根据事件类型eventType保存到subscriptionsByEventType集合中
        if(subscriptions == null) {
            subscriptions = new CopyOnWriteArrayList();
            this.subscriptionsByEventType.put(eventType, subscriptions);
        } else if(subscriptions.contains(newSubscription)) {
            //4. 判断订阅者是否已经被注册
            throw new EventBusException("Subscriber " + subscriber.getClass() + " already registered to event " + eventType);
        }

        int size = subscriptions.size();

        //5. 将订阅者对象添加到订阅者对象集合中
        for(int subscribedEvents = 0; subscribedEvents <= size; ++subscribedEvents) {
            if(subscribedEvents == size || subscriberMethod.priority > ((Subscription)subscriptions.get(subscribedEvents)).subscriberMethod.priority) {
                // 根据订阅方法的优先级进行注册
                subscriptions.add(subscribedEvents, newSubscription);
                break;
            }
        }

        //6. 通过subscriber获取事件类型集合(subscribedEvents)
        Object subscribedEvents = (List)this.typesBySubscriber.get(subscriber);
        if(subscribedEvents == null) {
            subscribedEvents = new ArrayList();
            //7. 事件类型集合为null，则新建，并根据订阅者subscriber将subscribedEvents存储到typesBySubscriber(Map集合)中
            this.typesBySubscriber.put(subscriber, subscribedEvents);
        }

        //8. 将eventType添加到subscribedEvent中
        ((List)subscribedEvents).add(eventType);

        //9. 如果是粘性事件，从stickyEvents事件保存队列中取出该事件类型的事件发送给当前订阅者
        if(subscriberMethod.sticky) {
            if(this.eventInheritance) {
                Set stickyEvent = this.stickyEvents.entrySet();
                Iterator var9 = stickyEvent.iterator();

                while(var9.hasNext()) {
                    Entry entry = (Entry)var9.next();
                    Class candidateEventType = (Class)entry.getKey();
                    if(eventType.isAssignableFrom(candidateEventType)) {
                        Object stickyEvent1 = entry.getValue();
                        this.checkPostStickyEventToSubscription(newSubscription, stickyEvent1);
                    }
                }
            } else {
                Object var14 = this.stickyEvents.get(eventType);
                this.checkPostStickyEventToSubscription(newSubscription, var14);
            }
        }

    }
```

## 4-事件的发送

16、EventBus的post方法的源码要点
```java
    public void post(Object event) {
        //1. PostingThreadState保存事件队列和线程状态信息
        EventBus.PostingThreadState postingState = (EventBus.PostingThreadState)this.currentPostingThreadState.get();
        //2. 获取事件队列
        List eventQueue = postingState.eventQueue;
        //3. 将当前事件插入事件队列
        eventQueue.add(event);
        if(!postingState.isPosting) {
            postingState.isMainThread = Looper.getMainLooper() == Looper.myLooper();
            postingState.isPosting = true;
            if(postingState.canceled) {
                throw new EventBusException("Internal error. Abort state was not reset");
            }

            try {
                //4. 处理事件队列中所有事件, 并移除该事件
                while(!eventQueue.isEmpty()) {
                    this.postSingleEvent(eventQueue.remove(0), postingState);
                }
            } finally {
                postingState.isPosting = false;
                postingState.isMainThread = false;
            }
        }
    }
```

17、EventBus事件发送的postSingleEvent源码要点
```java
    private void postSingleEvent(Object event, EventBus.PostingThreadState postingState) throws Error {
        Class eventClass = event.getClass();
        boolean subscriptionFound = false;
        //1. 表示是否向上查找事件的父类，默认为true(可以通过EventBuilder配置)
        if(this.eventInheritance) {
            //2. 找到所有父类事件，保存在List中
            List eventTypes = lookupAllEventTypes(eventClass);
            int countTypes = eventTypes.size();

            for(int h = 0; h < countTypes; ++h) {
                Class clazz = (Class)eventTypes.get(h);
                //3. 通过postSingleEventForEventType对事件逐一处理
                subscriptionFound |= this.postSingleEventForEventType(event, postingState, clazz);
            }
        } else {
            //4. 没有查找父类事件, 直接处理该事件
            subscriptionFound = this.postSingleEventForEventType(event, postingState, eventClass);
        }

        if(!subscriptionFound) {
            if(this.logNoSubscriberMessages) {
                Log.d(TAG, "No subscribers registered for event " + eventClass);
            }
            if(this.sendNoSubscriberEvent && eventClass != NoSubscriberEvent.class && eventClass != SubscriberExceptionEvent.class) {
                this.post(new NoSubscriberEvent(this, event));
            }
        }
    }
```

18、EventBus事件发送的postSingleEventForEventType源码要点
```java
    /**==============================
     *  按照事件类型post事件
     *==============================*/
    private boolean postSingleEventForEventType(Object event, EventBus.PostingThreadState postingState, Class<?> eventClass) {
        CopyOnWriteArrayList subscriptions;
        synchronized(this) {
            //1. 从[按事件类型分类的总订阅对象集合]中获取订阅对象集合(与该事件对应)
            subscriptions = (CopyOnWriteArrayList)this.subscriptionsByEventType.get(eventClass);
        }

        if(subscriptions != null && !subscriptions.isEmpty()) {
            Iterator var5 = subscriptions.iterator();

            //2. 遍历订阅对象集合，分别处理
            while(var5.hasNext()) {
                Subscription subscription = (Subscription)var5.next();
                //3. postingState获得事件和订阅对象
                postingState.event = event;
                postingState.subscription = subscription;
                boolean aborted = false;

                try {
                    //4. 对事件进行处理
                    this.postToSubscription(subscription, event, postingState.isMainThread);
                    aborted = postingState.canceled;
                } finally {
                    postingState.event = null;
                    postingState.subscription = null;
                    postingState.canceled = false;
                }

                if(aborted) {
                    break;
                }
            }

            return true;
        } else {
            return false;
        }
    }
```

19、EventBus事件发送的postToSubscription源码要点
```java
    /**==================================
     *        发送给订阅对象
     *  @要点：
     *     1. invokeSubscriber()-通过反射直接运行订阅的方法
     *     2. mainThreadPoster.enqueue()-是将订阅事件添加到主线程队列中
     *           类型为HandlerPoster，继承自Handler，通过Handler将订阅方法却环岛主线程执行。
     *     3. backgroundPoster.enqueue()-新开子线程处理
     *     4. asyncPoster.enqueue()-新开子线程处理
     *==================================*/
    private void postToSubscription(Subscription subscription, Object event, boolean isMainThread) {
        //1. 取出订阅方法的线程模式[subscription.subscriberMethod.threadMode]
        switch(subscription.subscriberMethod.threadMode) {
            //2. 根据模式分别处理
            case POSTING:
                //3. 与事件发布处在同一线程
                this.invokeSubscriber(subscription, event);
                break;
            case MAIN:
                //4. 在UI线程
                if(isMainThread) {
                    this.invokeSubscriber(subscription, event);
                } else {
                    this.mainThreadPoster.enqueue(subscription, event);
                }
                break;
            case BACKGROUND:
                //5. 事件发布处若在UI线程，则新开子线程处理。若在子线程，则在该线程处理
                if(isMainThread) {
                    this.backgroundPoster.enqueue(subscription, event);
                } else {
                    this.invokeSubscriber(subscription, event);
                }
                break;
            case ASYNC:
                //6. 无论是否在UI线程，均新开子线程处理
                this.asyncPoster.enqueue(subscription, event);
                break;
            default:
                throw new IllegalStateException("Unknown thread mode: " + subscription.subscriberMethod.threadMode);
        }

    }
```

## 5-订阅者取消注册

20、订阅者取消注册的unregister源码要点
```java
    public synchronized void unregister(Object subscriber) {
        //1. 通过订阅者(subscriber)从[按订阅者分类的 事件类型集合的总集合中]中获取相应[事件类型集合]
        List subscribedTypes = (List)this.typesBySubscriber.get(subscriber);
        if(subscribedTypes != null) {
            Iterator var3 = subscribedTypes.iterator();

            while(var3.hasNext()) {
                Class eventType = (Class)var3.next();
                //2. 通过[事件类型]在[订阅对象集合]中移除该订阅者的订阅对象
                this.unsubscribeByEventType(subscriber, eventType);
            }
            //3. [事件类型集合的总集合]中移除与订阅者相关的事件类型集合
            this.typesBySubscriber.remove(subscriber);
        } else {
            Log.w(TAG, "Subscriber to unregister was not registered before: " + subscriber.getClass());
        }
    }
```

21、订阅者取消注册的unsubscribeByEventType源码要点

```java
    private void unsubscribeByEventType(Object subscriber, Class<?> eventType) {
        //1. 通过事件类型获取订阅对象集合
        List subscriptions = (List)this.subscriptionsByEventType.get(eventType);
        if(subscriptions != null) {
            int size = subscriptions.size();

            for(int i = 0; i < size; ++i) {
                //2. 移除与订阅者(subscriber)相关的订阅对象
                Subscription subscription = (Subscription)subscriptions.get(i);
                if(subscription.subscriber == subscriber) {
                    subscription.active = false;
                    subscriptions.remove(i);
                    --i;
                    --size;
                }
            }
        }
    }
```

# IPC-进程间通信(62题)

>选取总结自《Android开发艺术探索》。
>文本包含两部分，1、对知识点就行归纳汇总 2、讲解IPC知识点

问题汇总：
1、什么是IPC？
>Inter-Process Communication(进程间通信)

2、进程间通信是什么？
>两个进程之间进行数据交换的过程

3、进程是什么？
>一般指一个执行单元,也是系统分配资源的最小单位。

4、线程是什么？
>是CPU调度的最小单元，而且是有限的系统资源。一个进程可以包含多个线程。

5、ANR导致的原因？如何避免？
>ANR-application not respongding是因为UI线程内部的耗时操作导致界面无响应。应该将耗时操作移到非UI线程即可。

6、什么时候需要用到多进程？
>比如：当前应用需要从其他应用获取数据

7、开启多进程模式的方法
>1. 给四大组件添加属性`android:process`
>2. 特殊方法：通过JNI在native层去fork一个新的进程。

8、activity的`android：process`属性`=":remote"`和`"com.example.remote"`的区别
>1. `:remote`是指在当前进程名前面加上当前的包名`com.example:remote`，且该进程是当前应用的私有进程，其他应用的组件不能和该进程跑在同一个进程内
>2. 后者是属于全局进程，其他应用可以通过ShareUID的方式和它跑在同一个进程中。

9、多进程会造成的问题：
>1. 静态成员和单例模式完全失效
>2. 线程同步机制完全失效
>3. SharedPreferences的可靠性下降(不支持两个进程同时读写)
>4. Application会多次创建

10、Serializable和Parcelable接口作用
>1. 可以完成对象的序列化过程
>2. 使用Intent和Binder传输数据时就需要Serializable或Parcelable
>3. 需要把对象持久化到存储设备，或者通过网络传给其他客户端。

11、Serializable接口的作用和使用
>1、Serializable接口为对象提供了标准的序列化和反序列化操作。
>1. 这个类实现Serializable接口
>2. 该类声明一个serialVersionUID(`private static final long serialVersionUID=8711368828010083044L`)。 甚至可以不申明ID，但是这个ID会对反序列化产生影响。

12、serialVersionUID的作用
>1、序列化后的数据的ID只有和当前类的ID相同才能正常被反序列化。
>2、可以手动设置ID为1L，这样会自动根据当前类结构去生成它的hash值

13、两个特别注意点：
>1、静态成员变量不属于对象，不会参与序列化过程
>2、用`transient`关键字标记的成员变量不会参与序列化过程。

14、java.io.ObjectOutputStream和ObjectInputStream用于对象序列化

15、系统中实现了Parcelable接口的类
>Intent、Bundle、Bitmap、List、Map
>里面的每个元素也都要可序列化

16、Parcelable和Serializable
>1. Serializable是java中的序列化接口，简单，但开销很大(需要大量IO操作)
>2. Parcelable是Android首推方法，使用麻烦，效率很高
>3. Parcelable主要用于内存序列化上
>4. Serializable适用于将对象序列化到存储设备或通过网络传输(Parcelable也可以只是较复杂)

17、Binder是什么？
>1. Binder是android的一个类，实现了IBinder接口
>2. IPC角度：Binder是android的一种跨进程通信方式
>3. Binder也可以看做一种虚拟的物理设备，设备驱动是/dev/binder，Linux中没有这种通信方式
>4. Android Framework角度：Binder是ServiceManager连接各种Manager(ActivityManager,WindowManager等)和相应ManagerService的桥梁
>5. Android应用层：Binder是客户端和服务端进行通信的媒介，当bindService的时候，服务端会返回一个包含了服务端业务调用的Binder对象，通过该对象，客户端可以获取服务端提供的服务和数据，服务包括普通服务和基于AIDL的服务。

18、Binder主要用在哪？
>1. Service
>2. AIDL
>3. Messenger(底层AIDL)

19、AIDL文件的本质作用
>AIDL文件的本质就是系统提供了一种快速实现Binder的工具。

20、通过AIDL快速实现Binder的步骤
>1. 新建Book.java(简单的类，没有实际功能，实现Parcelable接口)
>2. 新建Book.aidl需要有parcelable Book;
>3. 新建IBookManager.aidl,里面需要导入Book类`import xxx.Book;`
>4. 选择android studio的build中make project

21、AIDL工具快速实现的Binder中的四个要点
>1. 继承`IInterface`接口，本身也为接口
>2. 声明了两个IBookManager.aidl中定义的getBookList和addBook方法(并且用两个id标识这两个方法，用于标识在transact中客户端请求的是哪个方法)
>3. 声明一个内部类Stub，该Stub就是Binder类
>4. Stub的内部代理类Proxy，用于处理逻辑-客户端和服务端都位于一个进程时，方法调用不会走跨进程的transact过程，当位于不同进程时，方法调用走transact过程。

22、Binder注意点
>1. 客户端发起远程请求后，当前线程会被挂起直到服务器返回结果，因此不要在UI线程发起远程请求。
>2. 服务端的Binder方法运行在Binder的线程池中，所以Binder方法是否耗时都要采用同步方法实现。

23、Binder的工作流程：
>1. Client向Binder发起远程请求，Client同时挂起
>2. Binder向data(输入端对象)写入参数，并且通过Transact方法向服务端发起远程调用请求(RPC)
>3. Service端调用onTransact方法(运行在服务端线程池中)向reply(输出端对象)写入结果
>4. Binder获取reply数据，返回数据并且唤起Client

23、Android中的IPC方法(6种)
>1-Bundle
>2-文件共享
>3-Messenger
>4-AIDL
>5-ContentProvider
>6-Socket

24、通过AIDL自动生成Binder的java文件
1. 新建Book.java(简单的类，没有实际功能，实现Parcelable接口)
```java
public class Book implements Parcelable{
    public int bookId;

    public Book(int bookId){
        this.bookId = bookId;
    }
    private Book(Parcel in){
        bookId = in.readInt();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(bookId);
    }
}
```
2. 新建Book.aidl
```java
package com.example.administrator.featherdemos.aidl;

parcelable Book;
```
3. 新建IBookManager.aidl
```java
package com.example.administrator.featherdemos.aidl;

//关键：导入Book.java
import com.example.administrator.featherdemos.aidl.Book;

interface IBookManager {
    List<Book> getBookList();
    void addBook(in Book book);
}
```
4. 选择android studio的build中make project
>系统就会自动生成对应java文件`IBookManager`，位于目录`app\build\generated\source\aidl\debug\包下`

25、Binder所在java文件要点如下:
>1. 继承`IInterface`接口，本身也为接口
>2. 声明了两个IBookManager.aidl中定义的getBookList和addBook方法(并且用两个id标识这两个方法，用于标识在transact中客户端请求的是哪个方法)
>3. 声明一个内部类Stub，该Stub就是Binder类
>4. Stub的内部代理类Proxy，用于处理逻辑-客户端和服务端都位于一个进程时，方法调用不会走跨进程的transact过程，当位于不同进程时，方法调用走transact过程。

26、Stub(Binder)解析
>1. DESCRIPTOR
>Binder的唯一标识,一般用类名表示
>2. asInterface(android.os.IBinder obj)
>将服务端Binder对象转换成客户端所需AIDL接口类型对象(xxx.aidl.IBookManager)。如果客户端和服务端位于同一进程，此方法返回就是服务端的Stub对象本身，否则返回系统封装后的Stub.proxy
>3. asBinder
>返回当前Binder对象
>4. onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags)
>(1)运行在服务端的Binder线程池。
>(2)通过code确定Client请求的目标方法，从data中取得方法所需参数，执行目标方法。执行完毕后就向reply中写入返回值。
>(3)如果此方法返回false客户端就请求失败，我们可以用此来进行权限验证。
>5. Proxy的getBookList
>(1)运行在客户端。
>(2)创建方法所需的data(输入)、reply(输出)和list(返回)对象。把该方法的参数信息写入data，调用transact方法发起RPC远程过程调用请求，同时当前线程挂起。服务端的onTransact会被调用，到RPC过程返回后，当前线程继续执行，并从reply中取出RPC过程的返回结果，最后返回reply中的数据。
>6. Proxy的addBook
>运行在客户端。过程和getBookList类似，但是没有返回值。

27、Binder注意点
>1. 客户端发起远程请求后，当前线程会被挂起直到服务器返回结果，因此不要在UI线程发起远程请求。
>2. 服务端的Binder方法运行在Binder的线程池中，所以Binder方法是否耗时都要采用同步方法实现。

28、AIDL文件的本质就是系统提供了一种快速实现Binder的工具，仅此而已。

29、Binder的两个重要方法
>1. linkToDeath和unlinkToDeath。用于解决: 如果服务端异常终止，而会导致客户端调用失败，甚至可能客户端都不知道binder已经死亡，就会产生问题。
>2. linkToDeath作用给Binder设置一个死亡代理，当Binder会收到通知，还可以重新发起连接请求从而恢复连接。
>3. binder的isBinderAlive也可以判断Binder是否死亡。

## Android中的IPC方法
### 1-Bundle
30、Bundle的作用
>Bundle能携带数据-实现了Parcelable接口，常用于传递数据，如Acitivity、Service和Receiver都支持在Intent中通过Bundle传递数据。

31、Bundle在直接传递数据外的一个特殊使用场景。
> 场景：A进程在完成计算后需要启动B进程的一个组件并且将结果传递给B进程，但是这个结果不支持放入Bundle，因此无法通过Intent传输。
> 方案：A进程通过Intent启动进程B的service组件(如IntentService)进行计算，因为Service也在B进程中，目标组件就可以直接使用计算结果。

### 2-文件共享
32、文件共享的作用
>两个进程通过读/写同一个文件进行数据交换。也可以通过序列化在进程间传递对象。

33、文件共享的特点：
1. 通过序列化在进程间传递对象
2. 只适合同步要求不高的进程间通信
3. 要妥善处理并发读写问题，高并发情况下很容易出现数据丢失。

### 3-Messenger
34、Messenger是什么？
>1. 轻量级的IPC方案
>2. 底层实现是AIDL
>3. 一次处理一个请求，因此在服务端不考虑线程同步问题。

35、 Messenger的使用
>通过messenger在两个进程之间互相发送消息。
>客户端:
>1. 绑定并启动位于新进程的服务，通过msg发送消息
>2. 设置接受新进程服务发送来的消息
```java
public class MessengerActivity extends Activity {

    private static final String TAG = "MessengerActivity";

    private Messenger mMessenger;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mMessenger = new Messenger(iBinder); //
            Message msg = Message.obtain(null, Constant.MSG_FROM_CLIENT);
            //bundle携带消息
            Bundle data = new Bundle();
            data.putString("msg", "This is Client!");
            //给msg绑定bundle
            msg.setData(data);

            //**将用于服务端回复的msger发送给服务端**
            msg.replyTo = mGetReplyMessenger;
            try {
                //发送消息
                mMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        //绑定并启动服务
        Intent intent = new Intent(this, MessengerService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        //解除服务
        unbindService(mServiceConnection);
        super.onDestroy();
    }

    /**-------------------
     *  接受Service回复消息
     * ------------------*/
    private final Messenger mGetReplyMessenger = new Messenger(new MessengerHandler());
    private static class MessengerHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Constant.MSG_FROM_SERVICE:
                    Log.i(TAG, "recv msg from service:" + msg.getData().getString("reply"));
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }
}
```
>服务器端:
>接收消息，并通过client传送来的messenger回复消息。
```java
public class MessengerService extends Service {

    private static final String TAG = "MessengerService";
    private final Messenger mMessenger = new Messenger(new MessengerHandler());

    private static class MessengerHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Constant.MSG_FROM_CLIENT:
                    //接收到Client消息
                    Log.i(TAG, "receive msg from client: " + msg.getData().getString("msg"));
                    /*---------------------------------------------
                    * 回复数据给Client
                    * --------------------------------------------*/
                    Messenger clientMessenger = msg.replyTo; //获取到服务器传来的msger
                    Message message = Message.obtain(null, Constant.MSG_FROM_SERVICE); //设置msg
                    Bundle bundle = new Bundle();
                    bundle.putString("reply", "This is Service!");
                    message.setData(bundle);
                    //发送消息
                    try {
                        clientMessenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    public MessengerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
}
```
>AndroidManifest中注册Service:
>`android:process=":remote"`代表另开一个Service进程。
```xml
<service
            android:name=".MessengerService"
            android:enabled="true"
            android:exported="true"
            android:process=":remote">
        </service>
```
### 4-AIDL
36、Messenger缺点：
>1. 以串行的方式处理客户端发送的消息，如果大量的消息同时发送到服务端，服务端仍然只能一个个处理，如果有大量的并发请求，Messenger就无法胜任。
>2. 如果需要跨进程调用服务端的方法，这种情形Messenger就无法做到。

37、 AIDL进程间通信流程
>1-服务端
>>1. 创建一个Service来监听客户端的连接请求。
>>2. 创建一个AIDL文件。
>>3. 将暴露给客户端的接口在该AIDL文件中声明。
>>4. 最后在Service中实现这个AIDL接口即可。
>
>2-客户端
>>1. 绑定服务端的Service
>>2. 将服务端返回的Binder对象转成AIDL接口所属的类型
>>3. 最后就可以调用AIDL中的方法。

38、AIDL实例
>1. 创建你需要的接口文件：ITuringManager.aidl(这里功能就是获取图灵机列表，以及增加一个图灵机)
```aidl
package com.example.administrator.featherdemos;

import com.example.administrator.featherdemos.TuringMachine;

interface ITuringManager {
    List<TuringMachine> getTuringMachineList();
    void addTuringMachine(in TuringMachine machine);
}
```
>2. 实现TuringMachine.java(也就是接口文件导入的类，需要实现Parcelable接口)：
```java
package com.example.administrator.featherdemos;
//import ....需要的包
public class TuringMachine implements Parcelable{
    int machineId;
    String description;

    protected TuringMachine(Parcel in) {
        machineId = in.readInt();
        description = in.readString();
    }

	public TuringMachine(int id, String description){
        this.machineId = id;
        this.description = description;
    }

    public static final Creator<TuringMachine> CREATOR = new Creator<TuringMachine>() {
        @Override
        public TuringMachine createFromParcel(Parcel in) {
            return new TuringMachine(in);
        }

        @Override
        public TuringMachine[] newArray(int size) {
            return new TuringMachine[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(machineId);
        parcel.writeString(description);
    }
}
```
>3. 使用到的类(TuringMachine.java)需要一个对应aidl文件-TuringMachine.aidl:
```aidl
package com.example.administrator.featherdemos;

parcelable TuringMachine;
//ITuringMachine.aidl和TuringMachine.aidl需要在aidl文件夹下的包内
//TuringMachine.java要在java文件夹下的包内。
```
>4. 远程服务端-ITuringMachineManagerService.java
```java
public class ITuringMachineManagerService extends Service {

    /**
     * CopyOnWriteArrayList支持并发读/写：
     * 1. AIDL在服务端的Binder线程池中执行，因此当多个客户端同时连接的时候，会存在多个线程同时访问的情况。
     * 2. CopyOnWriteArrayList能进行自动的线程同步。
     */
    private CopyOnWriteArrayList<TuringMachine> mTuringMachineList = new CopyOnWriteArrayList<>();

    private Binder mBinder = new ITuringManager.Stub(){

        @Override
        public List<TuringMachine> getTuringMachineList() throws RemoteException {
            return mTuringMachineList;
        }

        @Override
        public void addTuringMachine(TuringMachine machine) throws RemoteException {
            mTuringMachineList.add(machine);
        }
    };

    public ITuringMachineManagerService() {
        mTuringMachineList.add(new TuringMachine(1, "Machine 1"));
        mTuringMachineList.add(new TuringMachine(2, "Machine 2"));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
```
>5. AndroidManifest中注册Service
```xml
<service
            android:name=".ITuringMachineManagerService"
            android:enabled="true"
            android:exported="true"
            android:process=":remote">
```
>6. 本地客户端
```java
public class TuringActivity extends AppCompatActivity {

    private static final String TAG = TuringActivity.class.getName();

    //Service连接：从服务端获取本地AIDL接口对象，并调用远程服务端的方法。
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //Binder的asInterface()将binder对象转换为客户端需要的AIDL接口对象
            ITuringManager iTuringManager = ITuringManager.Stub.asInterface(iBinder);
            try {
                //获取服务端的List
                ArrayList<TuringMachine> turingMachineArrayList
                        = (ArrayList<TuringMachine>) iTuringManager.getTuringMachineList();

                for(TuringMachine machine : turingMachineArrayList){
                    Log.i(TAG, "onServiceConnected: "+machine.getMachineId() + "-" + machine.getDescription());
                }
            } catch (RemoteException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turing);

        //绑定远程服务端的 Service 并启动
        Intent intent = new Intent(this, ITuringMachineManagerService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        unbindService(mServiceConnection); //解绑
        super.onDestroy();
    }
}
```

39、AIDL支持的数据类型
* 基本数据类型(int、long、char、boolean、double等)
* String和CharSequence
* List：只支持ArrayList，且里面所有元素必须是AIDL支持的数据。
* Map：只支持HashMap，且里面所有元素必须是AIDL支持的数据，包括key和value
* Parcelable：所有实现Parcelable接口的对象
* AIDL：所有AIDL接口都可以在AIDL中使用

40、AIDL中List只能用ArrayList，远程服务端为何使用了CopyOnWriteArrayList(并非继承自ArrayList)：
>Binder会根据List规范去访问数据，并且生成一个新的ArrayList传给客户端，因此没有违反数据类型的规定。
>ConcurrentHashMap也是类似功能

41、AIDL实例：如何使用观察者模式
>在AIDL基础上有如下步骤：
>1. 建立观察者接口(Observer)-ITMachineObserver.aidl
>2. 在ITuringManager.aidl中增加注册和解注册功能(register\unregister)
>3. 在服务端ITuringMachineManagerService中的binder对象里实现额外增加的注册和解注册功能。
>4. 在客户端中的binder对象里实现观察者接口中的更新方法。
>
>使用：
>1. 客户端中通过从服务端获得的Binder对象，调用register/unregister等方法
>2. 服务端中通过Client客户端注册的Observer去调用客户端Binder中的更新方法

42、AIDL观察者模式源码：
>1. ITMachineObserver.aidl
```aidl
package com.example.administrator.featherdemos;

import com.example.administrator.featherdemos.TuringMachine;

interface ITMachineObserver {
    void inform(in TuringMachine machine);
}
```
>2. ITuringManager.aidl
```aidl
package com.example.administrator.featherdemos;

import com.example.administrator.featherdemos.TuringMachine;
import com.example.administrator.featherdemos.ITMachineObServer;

interface ITuringManager {
    List<TuringMachine> getTuringMachineList();
    void addTuringMachine(in TuringMachine machine);
    void registerListener(in ITMachineObserver observer);
    void unregisterListener(in ITMachineObserver observer);
}
```
>3. ITuringMachineManagerService:
>只修改了private Binder mBinder = new ITuringManager.Stub()的内容
```java
public class ITuringMachineManagerService extends Service {

    /**
     * CopyOnWriteArrayList支持并发读/写：
     * 1. AIDL在服务端的Binder线程池中执行，因此当多个客户端同时连接的时候，会存在多个线程同时访问的情况。
     * 2. CopyOnWriteArrayList能进行自动的线程同步。
     */
    private CopyOnWriteArrayList<TuringMachine> mTuringMachineList = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<ITMachineObserver> mObserverList = new CopyOnWriteArrayList<>();

    private Binder mBinder = new ITuringManager.Stub(){

        @Override
        public List<TuringMachine> getTuringMachineList() throws RemoteException {
            return mTuringMachineList;
        }

        @Override
        public void addTuringMachine(TuringMachine machine) throws RemoteException {
            mTuringMachineList.add(machine);
            for(ITMachineObserver observer : mObserverList){
                observer.inform(machine);
            }
        }

        @Override
        public void registerListener(ITMachineObserver observer) throws RemoteException {
            mObserverList.add(observer);
        }

        @Override
        public void unregisterListener(ITMachineObserver observer) throws RemoteException {
            mObserverList.remove(observer);
        }
    };

    public ITuringMachineManagerService() {
        mTuringMachineList.add(new TuringMachine(1, "Old Machine 1949"));
        mTuringMachineList.add(new TuringMachine(2, "Old Machine 1949-2"));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}

```
>4. TuringActivity:
>>1. private ITMachineObserver mITMachineObserver获得观察者的Binder对象，并实现接口方法
>>2. 调用iTuringManager.registerListener(mITMachineObserver)在服务端进行注册
```java
public class TuringActivity extends AppCompatActivity{

    private static final String TAG = TuringActivity.class.getName();

    //Service连接：从服务端获取本地AIDL接口对象，并调用远程服务端的方法。
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //Binder的asInterface()将binder对象转换为客户端需要的AIDL接口对象
            ITuringManager iTuringManager = ITuringManager.Stub.asInterface(iBinder);
            try {
                iTuringManager.addTuringMachine(new TuringMachine(10, "Machine 2008"));

                //获取服务端的List
                ArrayList<TuringMachine> turingMachineArrayList
                        = (ArrayList<TuringMachine>) iTuringManager.getTuringMachineList();

                for(TuringMachine machine : turingMachineArrayList){
                    Log.i(TAG, "onServiceConnected: "+machine.getMachineId() + "-" + machine.getDescription());
                }

                iTuringManager.registerListener(mITMachineObserver);

                iTuringManager.addTuringMachine(new TuringMachine(3, "New Machine 2018!"));
            } catch (RemoteException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turing);

        //绑定远程服务端的 Service 并启动
        Intent intent = new Intent(this, ITuringMachineManagerService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        unbindService(mServiceConnection); //解绑
        super.onDestroy();
    }

    private ITMachineObserver mITMachineObserver = new ITMachineObserver.Stub() {
        @Override
        public void inform(TuringMachine machine) throws RemoteException {
            Log.i(TAG, "inform: get a new machine-"+machine.getDescription());
        }
    };

}
```

43、AIDL观察中解除注册引发问题(RemoteCallbackList)
>1. 在onDestory时，我们可以解除在服务端的注册：
```java
@Override
    protected void onDestroy() {
        //确保远程服务的Binder任然存活，就进行unregister
        if(mRemoteITuringManager != null && mRemoteITuringManager.asBinder().isBinderAlive()){
            try {
                mRemoteITuringManager.unregisterListener(mITMachineObserver);
                Log.i(TAG, "onDestroy: unregister!");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(mServiceConnection); //解绑
        super.onDestroy();
    }
```
>2. 但在服务端却无法在列表中找到该`Observer`：因为已经是两个不同的对象了
>3. 对象的跨进程传输本身是反序列化的过程，而对象在服务端和客户端早已不是同一个对象。
>4. RemoteCallbackList
>系统专门提供用于删除跨进程Listener的接口。该类本身是一个泛型，支持任何AIDL接口。

44、RemoteCallbackList工作原理
>1. 内部有一个Map结构，key是Ibinder，value是Callback类型。该结构能保存所有AIDL回调。将Listener的信息存入CallBack：
```java
IBinder key = listener.asBinder();
Callback value = new Callback(key, cookie);
```
>2. 虽然每次跨进程Client的同一个对象，会在服务端生成多个对象。但是这些对象本身的Binder都是同一个。
>以Binder作为Key，这样Listener对应着唯一Binder。

45、使用RemoteCallbackList的流程和特点
>1. 客户端解注册，服务端会遍历所有listener，找出那个和解注册的listener具有相同Binder的listener，并删除。
>2. 客户端终止后，会自动移除客户端注册的所有listener
>3. 自动实现线程同步，无需额外操作。

46、RemoteCallbackList的使用
>1. Service服务端
```java
private RemoteCallbackList<ITMachineObserver> mObserverList = new RemoteCallbackList<>();
```
>2. 注册/解除注册
```java
@Override
        public void registerListener(ITMachineObserver observer) throws RemoteException {
            mObserverList.register(observer);
        }

        @Override
        public void unregisterListener(ITMachineObserver observer) throws RemoteException {
            Log.i("ITuringService", "unregisterListener: size-"+mObserverList.getRegisteredCallbackCount());
            mObserverList.unregister(observer);
            Log.i("ITuringService", "unregisterListener: size-"+mObserverList.getRegisteredCallbackCount());
        }
```
>3. 需要按照RemoteCallbackList的方法进行遍历。
```java
        @Override
        public void addTuringMachine(TuringMachine machine) throws RemoteException {
            mTuringMachineList.add(machine);

            final int count = mObserverList.beginBroadcast();
            //进行通知
            for (int i = 0; i < count; i++){
                mObserverList.getBroadcastItem(i).inform(machine);
            }
            mObserverList.finishBroadcast();
        }
```

47、AIDL的注意点
>1. Client客户端调用远程方法，该方法运行在服务端的Binder线程池中，Client会被挂起。
>2. 服务端的方法执行过于耗时，会导致Client长时间阻塞，若该线程是UI线程，会导致ANR。
>3. 客户端的onServiceConnected和onServiceDisconnected方法都运行在UI线程中，不可以进行耗时操作
>4. 服务端的方法本身就在服务端的Binder线程池中，所以服务端方法本身就可以执行大量耗时操作，不要再开线程进行异步操作。
>5. 远程方法因为运行在Binder线程池中，如果要操作UI要通过Handler切换到UI线程(服务端通过远程方法去操作Cilent客户端的控件)

48、Binder意外死亡的两种解决方法：
>1. 给Binder设置DeathRecipient监听，Binder死亡时回调binderDied
>2. 在onServiceDisconnected中重连远程服务。

49、Binder意外死亡的解决方法的区别
>1. onServiceDisconnected在客户端UI线程中被回调
>2. binderDied在客户端的Binder线程池中被回调(无法操作UI)

50、DeathRecipient处理Binder意外死亡的实现：
```java
//设置Binder死亡代理
    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient(){

        @Override
        public void binderDied() {
            //远程服务端die，就不重新连接
            if(mRemoteITuringManager == null){
                return;
            }
            mRemoteITuringManager.asBinder().unlinkToDeath(mDeathRecipient, 0);
            mRemoteITuringManager = null;
            //重新绑定远程Service
            //绑定远程服务端的 Service 并启动
            Intent intent = new Intent(TuringActivity.this, ITuringMachineManagerService.class);
            bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
    };
```
>客户端的onServiceConnected中:
```java
//Binder的asInterface()将binder对象转换为客户端需要的AIDL接口对象
ITuringManager iTuringManager = ITuringManager.Stub.asInterface(iBinder);

try {
       //设置死亡代理
       iBinder.linkToDeath(mDeathRecipient, 0); //这里！
} catch (RemoteException e) {
       e.printStackTrace();
}
```

51、onServiceDisconnected解决Binder死亡问题：
>onServiceDisconnected是ServiceConnection的内部方法，在服务端Service断开后就会调用。

52、AIDL中进行权限验证的方法
>1. 直接在onBind中进行权限验证，比如可以使用permission验证等等。如果验证不通过则bind服务就失败。
>2. 在服务端onTransact中验证
>验证失败直接返回false，可以通过uid和pid验证，这样就可以验证包名也可以和第一种方法一样，验证permission。

### 5-ContentProvider
53、ContentProvider是什么
>1. ContentProvider是Android中提供的专门用于不同应用间数据共享的方式。
>2. 底层实现是Binder，但是使用比AIDL简单很多，系统进行了封装。

54、ContentProvider的使用
系统预置了许多ContentProvider，比如通讯录信息、日程表信息等。访问这些数据，只需要通过`ContentResolver`的query、update、insert和delete方法。

55、自定义ContentProvider的步骤
>1. 继承ContentProvider
>2. 实现：onCreate-创建的初始化工作
>3. getType-返回url请求对应的MIME类型(媒体类型)，比如图片、视频等。不关注该类型，可以返回null或者`“*/*”`
>4. query-查数据
>5. insert-插入数据
>6. update-更新数据
>7. delete-删除数据

56、ContentProvider六种方法原理：
>1. 六种方法均运行在ContentProvider的进程中
>2. onCreate由系统回调并运行在主线程里
>3. 其余五种方法由外界回调，并运行在Binder线程池中

57、ContentProvider存储方式
>底层很像SQLite数据库，但是存储方式没有限制，可以使用数据，也可以使用普通文件，甚至可以采用内存中的对象存储数据。

58、ContentProvider实例
>1. 第一个app用于提供Provider功能，主要包含两个文件：DbOpenHelper和PetProvider两个文件（底层使用数据库存储数据）。
>2. 第二个app使用Provider提供的数据
>DbOpenHelper:
```java
public class DbOpenHelper extends SQLiteOpenHelper{

    private static final String DB_NAME = "pet_provider.db";
    public static final String PET_TABLE_NAME = "pet";
    public static final String MASTER_TABLE_NAME = "master";

    private static final int DB_VERSION = 1;

    private static final String CREATE_PET_TABLE = "create table if not exists "
            + PET_TABLE_NAME + "(_id integer primary key, name text)";
    private static final String CREATE_MASTER_TABLE = "create table if not exists "
            + MASTER_TABLE_NAME + "(_id integer primary key, name text, sex int)";

    public DbOpenHelper(Context context) {
        //创建数据库(name和版本)
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //创建数据库
        sqLiteDatabase.execSQL(CREATE_MASTER_TABLE);
        sqLiteDatabase.execSQL(CREATE_PET_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
       //TODO ignored
    }
}
```
>PetProvider:
>继承自ContentProvider：将表的uri和uricode相绑定，overide五种方法并底层使用数据库实现。
```java
public class PetProvider extends ContentProvider{
    private static final String TAG = PetProvider.class.getSimpleName();

    private SQLiteDatabase mDb;
    /**
     * privoder的数据访问通过uri来实现，因此自定义Provider也采用此方法：
     *   用UriMatcher将Content_Uri和code相关联,这样通过uri就能知道访问哪个数据库表
     */
    private static final String AUTHORITIES = "customPrivoderName";
    private static final String PET_CONTENT_URI = "content://"+AUTHORITIES+"/pet";
    private static final String MASTER_CONTENT_URI = "content://"+AUTHORITIES+"/master";
    private static final int PET_CONTENT_CODE = 0;
    private static final int MASTER_CONTENT_CODE = 1;
    private static final UriMatcher sUrilMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static{
        sUrilMatcher.addURI(AUTHORITIES, "pet", PET_CONTENT_CODE); //指明authorites+路径：pet和CODE对应
        sUrilMatcher.addURI(AUTHORITIES, "master", MASTER_CONTENT_CODE);
    }
    /**
     *  根据uri获得相应的table name
     */
    private String getTableName(Uri uri){
        String tableName = null;
        switch (sUrilMatcher.match(uri)){
            case PET_CONTENT_CODE:
                tableName = DbOpenHelper.PET_TABLE_NAME;
                break;
            case MASTER_CONTENT_CODE:
                tableName = DbOpenHelper.MASTER_TABLE_NAME;
                break;
            default:break;
        }
        return tableName;
    }
    @Override
    public boolean onCreate() {
        Log.i(TAG,"onCreate()");
        //创建数据库
        mDb = new DbOpenHelper(getContext()).getReadableDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.i(TAG,"query():"+uri);
        String tableName = getTableName(uri);
        if(tableName == null){
            throw new IllegalArgumentException("Unsupported URI:" + uri);
        }
        return mDb.query(tableName, projection, selection, selectionArgs, null, null, sortOrder, null);
    }

    @Override
    public String getType(Uri uri) {
        Log.i(TAG,"getType()");
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        Log.i(TAG,"insert()");
        String tableName = getTableName(uri);
        if(tableName == null){
            throw new IllegalArgumentException("Unsupported URI:" + uri);
        }
        mDb.insert(tableName, null, contentValues);
        getContext().getContentResolver().notifyChange(uri, null);
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.i(TAG,"delete()");
        String tableName = getTableName(uri);
        if(tableName == null){
            throw new IllegalArgumentException("Unsupported URI:" + uri);
        }
        int count = mDb.delete(tableName, selection, selectionArgs);
        if(count > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        Log.i(TAG,"update()");
        String tableName = getTableName(uri);
        if(tableName == null){
            throw new IllegalArgumentException("Unsupported URI:" + uri);
        }
        int row = mDb.update(tableName, contentValues, selection, selectionArgs);
        if(row > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return row;
    }
}
```
>AndroidMainifest权限:
```xml
        <!--自定义ContentProvider-->
        <provider
            android:authorities="customPrivoderName"
            android:name=".PetProvider"
            android:exported="true"
            >
        </provider>
```
>在客户端使用ContentProvider(增删改查):
>增：
```java
ContentValues values = new ContentValues();
values.put("_id", 1);
values.put("name", "dog");
getContentResolver().insert(uri, values);
```
>删：
```java
//删除
getContentResolver().delete(uri, "_id=?", new String[]{"2"});
```
>改：
```java
//更新
ContentValues values = new ContentValues();
values.put("_id", 3);
values.put("name", "fox");
getContentResolver().update(uri, values, "_id=?",new String[]{"1"});
```
>查找：
```java
Cursor petCursor = getContentResolver().query(uri, new String[]{"_id", "name"}, null, null, null);
if(petCursor != null){
   while(petCursor.moveToNext()){
       Log.i("MainActivity", ""+petCursor.getInt(0));
       Log.i("MainActivity", ""+petCursor.getString(1));
   }
   petCursor.close();
}
```


### 7、Socket

59、Sokcet就可以进行进程间通信

## Binder连接池

60、AIDL使用流程
>1. 创建一个Service和一个AIDL接口
>2. 创建一个Binder(自定义)继承自AIDL接口中的Stub类，并实现其中抽象方法
>3. 在Service的onBind方法中返回该Binder类的对象
>4. 客户端绑定Service
>5. 建立连接后就可以访问远程服务端的方法

61、大量业务模块都需要使用AIDL进行进程间通信，如何在不创建大量Service的情况下解决。
>1. 可以将所有的AIDL放在一个Service中管理。
>2. 服务端Service提供一个queryBinder接口，根据不同业务返回相应的Binder对象。
>3. 就是使用Binder池

## 选用合适的IPC方式

62、IPC各种方法的优缺点和适用场景：
|名称|优点|缺点|适用场景|
|---|---|---|---|
|Bundle|简单易用|只能传输Bundle支持的数据类型|四大组件间的进程间通信|
|文件共享|简单易用|不适合高并发场景，无法做到进程间的即时通信|无并发访问情形，交换简单数据实时性不高的场景|
|AIDL|功能强大，支持一对多并发通信，支持实时通信|适用稍复杂，需要处理好线程同步|一对多通信且有RPC需求|
|Messenger|功能一般，支持一对多串行通信，支持实时通信|不能很好处理高并发情形，不支持RPC，数据通过Message传输因此只能传输Bundle支持的数据类型|低并发的一对多即时通信，无RPC需求，或者无须要返回结果的RPC需求|
|ContentProvider|在数据源访问方面功能强大，支持一对多并发数据共享，可通过Call方法扩展其他操作|可以理解为受约束的AIDL，主要提供数据源的CRUD操作|一对多的进程间的数据共享|
|Socket|功能强大，可以通过网络传输字节流，支持一对多并发实时通信|实现细节稍微繁琐，不支持直接的RPC|网络数据交换|
