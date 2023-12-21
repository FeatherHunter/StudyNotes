# 自定义View
看老师写的流式布局代码，写一遍。
1、自定义View有哪种？
1. 完全自定义View、ViewGroup
2. 继承已有的TextView、布局进行扩展
3. 继承子ViewGroup、Layout，利用现有组件根据特定布局方式来组成新的组件
2、自定义View包含什么？
1. 布局: onLayout、onMeasure，Layout，ViewGroup
2. 显示: onDraw
3. 交互: onTouchEvent
3、onDraw相关知识点
1. canvas
2. paint
3. matrix
4. clip
5. rect
6. animation
7. path（贝塞尔曲线）
8. line
9. text绘制
4、xml解析原理
5、插件化换肤
6、View层次结构
7、自定义View流程
1. 自定义属性
2. 测量
3. 布局
4. 绘制
8、自定义View绘制流程
1. 构造函数-View初始化
2. onMeasure-测量view大小
3. onSizeChanged-确定View大小
4. 确定子View布局
5. onDraw()---实际绘制内容
6. 视图状态改变-invalidate
9、自定义流式布局
1. 继承自ViewGroup，实现onMeasure（除非一个大房间，不然都需要测量）
2. onLayout:测量加布局
3. 不用实现onDraw：onDraw是对每个房间的装饰
1、自定义View
1. 最小单位才需要draw
2. 测量+绘制
10、View为什么是三个构造函数？
1. 一参数，context，代码中new
2. 二参数，context，attrs：在xml中的键值对在LayoutInfalter中解析，用parse解析成AttributeSet
3. 三参数，主题style
4. 四参数，imageview就有自定义属性
11、测量一定要先测量自己的孩子吗？
1. ViewPager先测量自己，setMeasureDimession设置自己的大小 ==== 课程，自定义View - 4
# Fragment
1、Fragment是如何添加到视图上的？
1. 内部mView保存了onCreateView构造的视图
2. 在FragmentManager.addViewToContainer中，将mView添加到容器中。
## Fragment笔记
2、将所有的add、replace、show、hide转为Op.ADD
commit:前进+1，后退-1，比如showhideshow，会优化之前的show和hide，提高性能
根据不同状态，执行不同生命周期。while（）循环直到状态同步
# ViewPager =====> 适配器模式
1、以前为什么需要预加载？现在为什么不好？
1. 以前页面都是图片文本，需要提高响应速度
2. 现在页面非常复杂，超清大图，视频等等内容，性能消耗大
3. 预加载5个页面，一个5MB，什么都没做就25MB了
4. Activity启动速度变慢
2、生产环境后台数据和线上环境数据有差异 ===========================> 适配器
3、ViewPager如何加载Fragment
```
// 当前Fragment
curView = adpater.instantiateItem
// 其他Fragment
add、remove思路进行添加和删除
```
4、ViewPager中适配器原理 =====> 适配器模式
1. startUpdate
2. instantiateItem
3. destoryItem
4. setPrimaryItem
5. finishUpdate
## 懒加载
1、懒加载方案
1. onCreateView：加载UI，但不加载数据
2. setUserVisibleHint：当前可见时请求数据，显示
3. ViewPager2：默认懒加载
2、ViewPager2
1. 基于RV，只有一个Adapter
2. 横向、纵向滑动
3. DiffUtil实现局部刷新 =======> DiifUtil
4. 支持RTL（阿拉伯）
5. 默认懒加载，性能好
6. UI效果好，容易实现各种
7. 内部有Lifecycle进行管理
# setContentView
1、为什么在setContentView之后不可以requestWindowFeature？只能在上面？
1. Feature在setContentView中被处理
2、PhoneWindow会被谁创建？
1. Activity
2. Dialog
3. PopupWindow
4. Toast
   
3、PhoneWindow的类型
1. 应用
2. 子
3. 系统
4、DecorView中ViewStub是用来做什么的？
5、继承自AppCompatActivity用requestWindowsFeature无效
1. 需要使用supportRequestWIndowsFeature，为什么？
1. 需要delegate去请求
6、setContentView核心   ========> 外观模式、门面模式，对外不变，对内随便修改
1. 构造出View放到DecorView内部的content中
2. contentparent.addView
7、LayoutInflater
1. LayoutInflate.inflate merge标签没有父，报错
2. 其他createViewFromTag
3. 根据是不是SDK控件走不同流程
4. LayoutInflater的实现类是PhoneLayoutInflater.onCreateView
    1. 创建ViewRoot
    2. 创建子View
8、View view = inflater.inflate(layout, root, true)
1. 将layout的view添加到root中
2. root.addView(view)反复添加会有问题
9、面试：LayoutInflate参数的作用
1. 如果加载merge，root不能传入null
2. merge时，root存在，attachToParent需要shitrue
   
10、merge、ViewStub、include的区别
1. merge：优化布局，减少层级，不能用于子布局，必须rootView
2. inlucde：写了，id，用id也是找不到的。include merge布局，不会产生额外的层级
3. ViewStub：和include一样，影藏作用，懒加载
11、ViewStub的布局如何加载
1. setVisible
2. inflate 都可以
12、为什么系统View不需要加包名？
1. inflate的会根据是否是SDK控件进行补全
2. 再去反射创建 ===============> 反射，缓存
## 异步加载布局
在Android中，`setContentView`方法是用于设置Activity的布局的。默认情况下，它是同步执行的，也就是说，当调用`setContentView`时，它会阻塞当前线程，直到布局加载完成并显示在屏幕上。
然而，你可以通过异步加载布局来避免阻塞UI线程。下面是一种实现方式：
1. 创建一个自定义的`AsyncTask`类，用于在后台线程中加载布局文件。
```java
private class LoadLayoutTask extends AsyncTask<Integer, Void, View> {
    @Override
    protected View doInBackground(Integer... params) {
        int layoutResId = params[0];
        LayoutInflater inflater = getLayoutInflater();
        return inflater.inflate(layoutResId, null);
    }
    @Override
    protected void onPostExecute(View view) {
        setContentView(view);
    }
}
```
2. 在需要异步加载布局的地方，调用这个自定义的`AsyncTask`类。
```java
int layoutResId = R.layout.activity_main; 
new LoadLayoutTask().execute(layoutResId);
```
这样，布局文件将在后台线程中加载，并在加载完成后在UI线程上显示在屏幕上。
然而，需要注意的是，使用异步加载布局也存在一些限制：
1. 异步加载布局可能会导致布局文件的加载时间变长，这可能会在某些情况下影响用户体验。
2. 在布局文件加载完成之前，可能会出现屏幕上什么都没有或者空白的情况，这也可能会给用户带来困扰。
3. 如果在布局文件加载完成之前，尝试使用布局中的视图组件，可能会导致空指针异常或其他问题。
因此，在使用异步加载布局时，需要根据具体情况进行权衡和适当的处理。
### 实战
需要注意，在有些机型上，异步加载布局会在主线程的looper创建完成之前调用，所以依赖与handler和looper的view，会出现异常，比如SurfaceView，TextClock等等。对于这些view，可以这样处理，先使用ViewStub占位，等view异步加载完成后，再调用ViewStub的inflate方法来初始化。布局文件占位。
## 换肤
官方AppCompatActivity借助的Factory2实现的换肤 ===> 看源码
LayoutInflater.from this.setFactory会提示设置过，需要反射先设置
### ASM
不要BaseActivity，AOP思想，可以ASM插入到我们的代码前面。Lifecycle
侵入性是什么？
1. 框架给别人还要操作，就是侵入性
Application的onCreate中调用了onActivityCretated，dispatchActivityCreated遍历所有callback去掉用
### 插件化
皮肤包就是apk，用插件化技术的res即可
### 预测量
wrap_content会预测量
measureHierarchy
UI刷新只能在主线程进行吗？requestLKayotu，checkThread，哪个线程创建VIewrOOT那个就是uI
1、ViewGroup为什么不会执行onDeaw？没有这个需求
canvas。restore、canvas.retsore\savce
