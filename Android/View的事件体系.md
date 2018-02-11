转载请注明链接：http://blog.csdn.net/feather_wch/article/details/78955689
>内容学习总结自《Android开发艺术探索》
>代码用Java/Kotlin方式实现

#View的事件体系(49题)
版本:2018.1.30-4

---

[TOC]

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
//Kotlin
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
```kotlin
//Kotlin
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

12、Android坐标系
>1. Android坐标系以屏幕左上角为原点，向右X轴为正半轴，向下Y轴为正半轴
>2. 触摸事件中getRawX()和getRawY()获得的就是Android坐标系的坐标
>3. 通过`getLocationOnScreen(intlocation[])`能获得当前视图的左上角在Andriod坐标系中的坐标。

13、视图坐标系(View坐标系)
>1. View坐标系是以当前视图的父视图的左上角作为原点建立的坐标系，方向和Android坐标系一致
>2. 触摸事件中getX()和getY()获得的就是视图坐标系中的坐标

## 2-View的滑动

14、View滑动的7种方法：
>1. layout：onTouchEvent()方法中获得控件滑动前后的偏移。通过layout方法重新设置。
>2. offsetLeftAndRight和offsetTopAndBottom:系统提供上下/左右同时偏移的API，onTouchEvent()中调用
>3. LayoutParams: 更改自身布局参数
>4. scrollTo/scrollBy: 本质是移动View的内容，需要通过父容器的该方法来滑动当前View
>5. Scroller: 平滑滑动，通过重载`computeScroll()`，使用`scrollTo/scrollBy`完成滑动效果。
>6. 属性动画: 动画对View进行滑动
>7. ViewDragHelper: 谷歌提供的辅助类，用于完成各种拖拽效果。

15、Layout实现滑动
```java
/*================================*
* onTouchEvent-进行偏移计算，之后调用layout
*================================*/
 public boolean onTouchEvent(MotionEvent event) {
     float curX = event.getX(); //手指实时位置的X
     float curY = event.getY(); //Y
     switch(event.getAction()){
        case MotionEvent.ACTION_MOVE:
           int offsetX = (int)(curX - downX); //X偏移
           int offsetY = (int)(curY - downY); //Y偏移
    /**=============================================
     * 变化后的距离=getLeft(当前控件距离父控件左边的距离)+偏移量——调用layout重新布局
     *============================================*/
           layout(getLeft() + offsetX, getTop() + offsetY, getRight() + offsetX, getBottom() + offsetY);
           break;
        case MotionEvent.ACTION_DOWN:
           downX = curX; //按下时的坐标
           downY = curY;
           break;
     }
     return true;
 }
```

16、offsetLeftAndRight和offsetTopAndBottom实现滑动
```java
/*================================*
* onTouchEvent-进行偏移计算，直接调用
*================================*/
 public boolean onTouchEvent(MotionEvent event) {
     float curX = event.getX(); //手指实时位置的X
     float curY = event.getY(); //Y
     switch(event.getAction()){
        case MotionEvent.ACTION_MOVE:
           int offsetX = (int)(curX - downX); //X偏移
           int offsetY = (int)(curY - downY); //Y偏移
     /**=============================================
      * 对left和right, top和bottom同时偏移
      *============================================*/
           offsetLeftAndRight(offsetX);
           offsetTopAndBottom(offsetY);
           break;
        case MotionEvent.ACTION_DOWN:
           downX = curX; //按下时的坐标
           downY = curY;
           break;
     }
     return true;
 }
```

17、LayoutParams实现滑动：
>1. 通过父控件设置View在父控件的位置，但需要指定父布局的类型，不好
>2. 用ViewGroup的MariginLayoutParams的方法去设置margin
```java
//方法一：通过布局设置在父控件的位置。但是必须要有父控件, 而且要指定父布局的类型，不好的方法。
RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
layoutParams.leftMargin = getLeft() + offsetX;
layoutParams.topMargin = getTop() + offsetY;
setLayoutParams(layoutParams);

/**===============================================
 * 方法二：用ViewGroup的MarginLayoutParams的方法去设置marign
 * 优点：相比于上面方法, 就不需要知道父布局的类型。
 * 缺点：滑动到右侧控件会缩小
 *===============================================*/
ViewGroup.MarginLayoutParams mlayoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
mlayoutParams.leftMargin = getLeft() + offsetX;
mlayoutParams.topMargin = getTop() + offsetY;
setLayoutParams(mlayoutParams);
```

18、scrollTo\scrollBy实现滑动
>1. View提供的scrollTo方法，实现了基于参数的绝对滑动——直接到新的x,y坐标处
>2. scrollBy内部是调用scrollTo，实现了基于当前位置的相对滑动
>3. 这两个方法是移动View的内容，因此需要在View的父控件中调用。
```java
//scrollBy是父容器进行滑动，因此偏移量需要取负
((View)getParent()).scrollBy(-offsetX, -offsetY);
```

19、scrollTo/By内部的mScrollX和mScrollY的意义
>1. mScrollX的值，相当于手机屏幕相对于View左边缘向右移动的距离，手机屏幕向右移动时，mScrollX的值为正；手机屏幕向左移动(等价于View向右移动)，mScrollX的值为负。
>2. mScrollY和X的情况相似，手机屏幕向下移动，mScrollY为+正值；手机屏幕向上移动，mScrollY为-负值。
>3. mScrollX/Y是根据第一次滑动前的位置来获得的，例如：第一次向左滑动200(等于手机屏幕向右滑动200)，mScrollX = 200；第二次向右滑动50, mScrollX = 200 + （-50）= 150，而不是（-50）。

20、动画实现滑动的方法
>1. 可以通过传统动画或者属性动画的方式实现
>2. 传统动画需要通过设置fillAfter为true来保留动画后的状态(但是无法在动画后的位置进行点击操作，这方面还是属性动画好)
>3. 属性动画会保留动画后的状态，能够点击

21、ViewDragHelper实现侧滑菜单：
```java
public class DragViewGroup extends FrameLayout {
    private ViewDragHelper mViewDragHelper;//侧滑类
    private View mMenuView,mMainView; //菜单和主界面
    private int mWidth;
    // 构造
    public DragViewGroup(Context context) {
        super(context);
        initView();
    }
    public DragViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }
    public DragViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }
    /**-------------------------------------------
     * 1、初始化数据：调用ViewDragHelper.create方法
     * ------------------------------------------*/
    private void initView() {
        mViewDragHelper = ViewDragHelper.create(this,callback); //需要监听的View和回调callback
    }
    /**-------------------------------
     * 2、事件拦截和触摸事件全部交给ViewDragHelper进行处理
     * ------------------------------*/
    //事件拦截
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }
    //触摸事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //将触摸事件传递给ViewDragHelper
        mViewDragHelper.processTouchEvent(event);
        return true;
    }
    /**--------------------------------------------
     * 3、也需要重写computeScroll()
     *    内部也是通过scroller来进行平移滑动, 这个模板可以照搬
     * -------------------------------------------*/
    @Override
    public void computeScroll() {
        if(mViewDragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }
    /**------------------------------
     * 4、处理的回调：侧滑回调
     * ----------------------------*/
    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        /*-------------------------------
        * 何时开始触摸:
        *  1.指定哪一个子View可以被移动.
        *  2.如果直接返回true，在该布局之内的所有子View都可以随意划动
        * ------------------------------*/
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            //如果当前触摸的child是mMainView开始检测
            return mMainView == child;
        }
        /*-------------------------------
        * 处理水平滑动:
        *  1. 返回值默认为0，如果为0则不处理该方向的滑动。
        *  2. 一般直接返回left，当需要精准计算pading等值时，可以先对left处理再返回
        * ------------------------------*/
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return left;
        }
        /*-------------------------------
        * 处理垂直滑动:
        *  1. 返回值默认为0，如果为0则不处理该方向的滑动。
        *  2. 一般直接返回top，，当需要精准计算pading等值时，可以先对left处理再返回
        * ------------------------------*/
        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return 0;
        }
        /*---------------------------------------------
        *  拖动结束后调用，类似ACTION_UP。
        *   这里是实现侧滑菜单，一般滑动可以不用这段代码
        * ---------------------------------------------*/
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            //手指抬起后缓慢的移动到指定位置
            if(mMainView.getLeft() <500){
                //关闭菜单
                mViewDragHelper.smoothSlideViewTo(mMainView,0,0);
                ViewCompat.postInvalidateOnAnimation(DragViewGroup.this);
            }else{
                //打开菜单
                mViewDragHelper.smoothSlideViewTo(mMainView,300,0);
                ViewCompat.postInvalidateOnAnimation(DragViewGroup.this);
            }
        }
    };
    /**---------------------------------------------------
     * 5、获取子控件用于处理
     *  1. 上面完成了滑动功能，这里简单的按照第1、2的顺序指定子控件View的内容
     *  2. onSizeChanged能够获得menu等子控件的宽度等信息，有需求可以后续处理
     * ----------------------------------------------*/
    //XML加载组建后回调
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mMenuView = getChildAt(0);
        mMainView = getChildAt(1);
    }
    //组件大小改变时回调
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = mMenuView.getMeasuredWidth();
    }
}
```
使用(作为父控件，里面依次放menu和main)：
```xml
    <com.example.xxxx.DragViewGroup
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <TextView
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="@color/colorAccent"/>

        <TextView
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="@color/colorPrimary"/>

    </com.example.xxxx.DragViewGroup>
```

22、7种滑动方式的优缺点：
>1. layout/offsetLeftAndRight和offsetTopAndBottom: 简单，本质是重新布局
>2. scrollTo/scrollBy/Scroller: 操作简单，适合对View内容的滑动
>3. 动画：操作简单，主要适用于没有交互的View和实现复杂的动画效果
>4. 改变参数布局：操作稍微复杂，适合有交互的View
>5. ViewDrawHelper: 便捷而且功能强大，系统提供的侧边栏滑动效果相关的`SlidingPaneLayout和DrawerLayout`使用该辅助类完成

## 3-弹性滑动
23、Scroller的用法：
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

24、Scroller方法要点解析
>1. 调用startScroll方法时，Scroller只是单纯的保存参数
>2. 之后的invalidate方法导致的View重绘
>3. View重绘之后draw方法会调用自己实现的computeScroll()，才真正实现了滑动

25、Scroller工作原理
>1. Scroller本身不能实现View的滑动，需要配合View的computeScroll方法实现弹性滑动
>2. 不断让View重绘，每一次重绘距离华东的开始时间有一个时间间隔，通过该时间可以得到View当前的滑动距离
>3. View的每次重绘都会导致View的小幅滑动，多次小幅滑动就组成了弹性滑动

26、通过动画可以实现弹性滑动

27、通过延时策略实现弹性滑动。
>1. 通过handler、View的postDelayed、或者线程的sleep方法。

## 4-View的事件分发机制

28、事件分发
>1. 点击事件的对象就是MotionEvent，因此事件的分发，就是MotionEvent的分发过程，
>2. 点击事件有三个重要方法来完成：dispatchTouchEvent、onInterceptTouchEvent和onTouchEvent

29、dispatchTouchEvent的作用
>1. 用于进行事件的分发
>2. 只要事件传给当前View，该方法一定会被调用
>3. 返回结果受到当前View的onTouchEvent和下级View的dispatchTouchEvent影响
>4. 表示是否消耗当前事件

30、onInterceptTouchEvent的作用
>1. 在dispatchTouchEvent的内部调用，用于判断是否拦截某个事件

31、onTouchEvent的作用
>1. 在dispatchTouchEvent的中调用，用于处理点击事件
>2. 返回结果表示是否消耗当前事件

32、事件分发伪代码:
```java
public boolean dispatchTouchEvent(MotionEvent ev){
  boolean consume = false;
  if(onInterceptTouchEvent(ev)){
    consume = onTouchEvent(ev);
  }else{
    consume = child.dispatchTouchEvent(ev);
  }
  return consume;
}
```

33、事件的传递规则：
>1. 点击事件产生后，会先传递给根ViewGroup，并调用dispatchTouchEvent
>2. 之后会通过onInterceptTouchEvent判断是否拦截该事件，如果true，则表示拦截并交给该ViewGroup的onTouchEvent方法进行处理
>3. 如果不拦截，则当前事件会传递给子元素，调用子元素的dispatchTouchEvent，如此反复直到事件被处理

34、View处理事件的优先级
>1. 在View需要处理事件时，会先调用OnTouchListener的onTouch方法，并判断onTouch的返回值
>2. 返回true，表示处理完成，不会调用onTouchEvent方法
>3. 返回false，表示未完成，调用onTouchEvent方法进行处理
>4. 可见，onTouchEvent的优先级没有OnTouchListener高
>5. 平时常用的OnClickListener优先级最低，属于事件传递尾端

35、点击事件传递过程遵循如下顺序：
>1. Activity->Window->View->分发
>2. 如果View的onTouchEvent返回false，则父容器的onTouchEvent会被调用，最终可以传递到Activity的onTouchEvent

36、事件传递规则要点
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

37、Activity事件分发的过程
>1. 事件分发过程：Activity->Window->Decor View(当前界面的底层容器，setContentView的View的父容器)->View
>2. Activity的dispatchTouchEvent，会交给Window处理(`getWindow().superDispatchTouchEvent()`)，
>3. 返回true：事件全部结束
>4. 返回false：所有View都没有处理(onTouchEvent返回false)，则调用Activity的onTouchEvent

38、Window事件分发
>1. Window和superDispatchTouchEvent分别是抽象类和抽象方法
>2. Window的实现类是PhoneWindow
>3. PhoneWindow的`superDispatchTouchEvent()`直接调用`mDecor.superDispatchTouchEvent()`,也就是直接传给了DecorView

39、DecorView的事件分发
>1. DecorView继承自FrameLayout
>3. DecorView的`superDispatchTouchEvent()`会调用`super.dispatchTouchEvent()`——也就是`ViewGroup`的`dispatchTouchEvent`方法，之后就会层层分发下去。

40、根View的事件分发
>1. 顶层View调用dispatchTouchEvent
>2. 调用onInterceptTouchEvent方法
>3. 返回true，事件由当前View处理。如果有onTouchiListener，会执行onTouch，并且屏蔽掉onTouchEvent。没有则执行onTouchEvent。如果设置了onClickListener，会在onTouchEvent后执行onClickListener
>4. 返回false，不拦截，交给子View重复如上步骤。

41、ViewGroup的dispatchTouchEvent事件分发解析
```java
public boolean dispatchTouchEvent(MotionEvent ev) {
    boolean handled = false;
    //1. 过滤掉不符合安全策略的事件
    if (onFilterTouchEventForSecurity(ev)) {
        final boolean intercepted;
        /**============================================
         * 2. 一旦一系列事件中的某个事件被拦截，后续的事件都会直接拦截，不会再判断
         *  情景1: 为MotionEvent.ACTION_DOWN，等式为true，进入判断是否拦截
         *  情景2：不为ACTION_DOWN, (mFirstTouchTarget!=null)系列事件都没有被拦截, 等式为true，进入判断是否拦截
         *  情景3：不为ACTION_DOWN, (mFirstTouchTarget=null)前面事件被拦截, 等式为false
         *=============================================*/
        if (actionMasked == MotionEvent.ACTION_DOWN || mFirstTouchTarget != null) {
            /**===========================================
             *3. 由子View请求ViewGroup不要拦截该事件
             * 1-子View会通过`requestDisallowInterceptTouchEvent`设置FLAG_DISALLOW_INTERCEPT标志位
             * 2-ACTION_DOWN会重置FLAG_DISALLOW_INTERCEPT标志位，因此无法被子View影响
             *=============================================*/
            final boolean disallowIntercept = (mGroupFlags & FLAG_DISALLOW_INTERCEPT) != 0;
            if (!disallowIntercept) {
                //4. 判断ViewGroup是否拦截该事件
                intercepted = onInterceptTouchEvent(ev);
                ev.setAction(action); // restore action in case it was changed
            } else {
                //5. 由子View控制不拦截该事件(前提是DOWN没有被拦截)
                intercepted = false;
            }
        } else {
            //6. ACTION_UP\MOVE等系列事件被拦截过，因此后续的全部拦截，不会重新判断
            intercepted = true;
        }
        ......
        //7. 没有被拦截，交给子View处理
        if (!canceled && !intercepted) {
            //8. 遍历所有子元素，并判断是否能接受点击事件，以及点击事件坐标是否在子元素内。
            for (int i = childrenCount - 1; i >= 0; i--) {
                //判断是否能接受点击事件, 不能就直接continue
                if (childWithAccessibilityFocus != null) {
                    if (childWithAccessibilityFocus != child) {
                        continue;
                    }
                }
                //判断点击事件坐标是否在子元素内, 不在就直接continue
                if (!canViewReceivePointerEvents(child) || !isTransformedTouchPointInView(x, y, child, null)) {
                    continue;
                }
                //分发给子View处理，内部就是调用子元素的`dispatchTouchEvent`方法
                if (dispatchTransformedTouchEvent(ev, false, child, idBitsToAssign)) {
                    //子View消耗并且处理该事件
                    alreadyDispatchedToNewTouchTarget = true;
                    break;
                }
            }
        }
        //9. 事件被拦截或者子View未消耗该事件: 自己处理该事件
        if (mFirstTouchTarget == null) {
            handled = dispatchTransformedTouchEvent(ev, canceled, null, TouchTarget.ALL_POINTER_IDS);
        }
        .......
    }
    return handled;
}
```

42、View对点击事件的处理过程(不包括ViewGroup)
```java
/**=========================
 * 1. 事件分发(OnTouchListener或者onTouchEvent直接处理)
 *=========================*/
public boolean dispatchTouchEvent(MotionEvent event) {
    boolean result = false;
    ......
    //1. 采用安全策略过滤事件
    if (onFilterTouchEventForSecurity(event)) {
        ListenerInfo li = mListenerInfo;
        //2. 判断是否有OnTouchListener，返回true，则处理完成
        if (li != null && li.mOnTouchListener != null
                && (mViewFlags & ENABLED_MASK) == ENABLED
                && li.mOnTouchListener.onTouch(this, event)) {
            result = true;
        }
        //3. 如果OnTouch返回true, 不会调用onTouchEvent
        if (!result && onTouchEvent(event)) {
            result = true;
        }
    }
    ......
    return result;
}
/**=========================
 * 2. 事件处理onTouchEvent
 *=========================*/
public boolean onTouchEvent(MotionEvent event) {
    //0. 获取点击状态
    final boolean clickable = ((viewFlags & CLICKABLE) == CLICKABLE
            || (viewFlags & LONG_CLICKABLE) == LONG_CLICKABLE)
            || (viewFlags & CONTEXT_CLICKABLE) == CONTEXT_CLICKABLE;
    //1. View不可用状态下(可点击状态-会消耗该事件,不可点击不消耗)
    if ((viewFlags & ENABLED_MASK) == DISABLED) {
        if (action == MotionEvent.ACTION_UP && (mPrivateFlags & PFLAG_PRESSED) != 0) {
            setPressed(false);
        }
        return clickable;//根据是否可点击决定是否消耗
    }
    //2. 如果有代理，会执行代理的onTouchEvent方法（会消耗该事件）
    if (mTouchDelegate != null) {
        if (mTouchDelegate.onTouchEvent(event)) {
            return true;
        }
    }
    //3. View可点击(消耗该事件)
    if (clickable || (viewFlags & TOOLTIP) == TOOLTIP) {
        switch (action) {
            case MotionEvent.ACTION_UP:
                  ......
                //5. 如果设置了`OnClickListener`，performClick内部会调用onClick方法
                  performClick();
                  ......
                break;
        }
        return true;
    }
    //4. 可用状态&&没有代理&&不可点击：不消耗该事件
    return false;
}
```

## 5-View的滑动冲突
43、滑动冲突的三种场景
>1. 内层和外层滑动方向不一致：一个垂直，一个水平
>2. 内存和外层滑动方向一致：均垂直or水平
>3. 前两者层层嵌套

44、 滑动冲突处理原则
>1. 对于内外层滑动方向不同，只需要根据滑动方向来给相应控件拦截
>2. 对于内外层滑动方向相同，需要根据业务来进行事件拦截
>3. 前两者嵌套的情况，根据前两种原则层层处理即可。

45、 滑动冲突解决办法
>1. 外部拦截：在父容器进行拦截处理，需要重写父容器的onInterceptTouchEvent方法
>2. 内部拦截：父容器不拦截任何事件，事件都传递给子元素。子元素需要就处理，否则给父容器处理。需要配合`requestDisallowInterceprtTouchEvent`方法。

46、外部拦截法要点
>1. 父容器的`onInterceptTouchEvent`方法中处理
>2. ACTION_DOWN不拦截，一旦拦截会导致后续事件都直接交给父容器处理。
>3. ACTION_MOVE中根据情况进行拦截，拦截：return true，不拦截：return false（外部拦截核心）
>4. ACTION_UP不拦截，如果父控件拦截UP，会导致子元素接收不到UP进一步会让onClick方法无法触发。此外UP拦截也没什么用。

47、内部拦截法要点
>1. 子View的`dispatchTouchEvent`方法处理
>2. ACTION_DOWN，让父容器不拦截(也不能拦截，否则会导致后续事件都无法传递到子View)-`parent.requestDisallowInterceptTouchEvent(true)`
>3. ACTION_MOVE,如父容器需要该事件，则父容器拦截requestDisallowInterceptTouchEvent(false)
>4. ACTION_UP，无操作，正常执行

48、内部拦截Kotlin代码
```kotlin
//Kotlin
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

49、外部拦截，自定义ScrollView
```kotlin
//Kotlin
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
