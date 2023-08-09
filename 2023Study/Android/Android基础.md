# Android
## 编码
1、编码类型
1. ASCII
2. Unicode 符号集
   1. UTF-8（互联网）-变长(1~4byte) ===》 protobuf
   2. UTF-16/32 很少用
3. GBK
2、UTF-8和protobuf的区别
1. UTF-8，字符编码，用于表示Unicode集中的字符
2. protobuf，序列化和反序列化结构化数据，用于传输
3、大端、小端字节序
1. 来源于格列夫游记
2. 小人国，因为鸡蛋是从大端打开，还是小端打开，爆发了六次战争
3. 小端：适合计算机，低位数据在低地址  ===> protobuf
4. 大端：适合人类，高位数据在低地址
## RecyclerView
1、RecyclerView的优点
1. 回收，复用提高性能，减少内存抖动，减少GC，减少卡顿
2. 灵活可扩展性高，有布局管理器LayoutManager
3. item增减动画：ItemAnimator
4. 定向刷新：Adapter
5. 增加修饰：ItemDecoration
2、RecyclerView的缺点
1. 点击事件要自己写
3、RV核心Api
1. onCreateViewHolder
2. onBindViewHolder
4、ItemTouchHelper
1. 拖拽 onMove
2. 滑动删除 onSwipe
3. 开关：getMovementFlags
## 缓存原理
1、缓存原理本质
1. 离屏如何处理
2. 入屏缓存如何处理
### ListView
1、ListView：二级缓存和RecycleBin
1. RecycleBin，进行布局，makeAndAddView，（1）obtainView （2）layout，layoutChildren
2. RecycleBin内部有：
3. ActivViews: 不会创建view，不会绑定view
4. ScrapViews：根据ViewType缓存，对应的View。不存在：创建View，绑定View；存在：不会创建，会绑定
   
### RV
1、RV的四级缓存
1. 屏幕内缓存 - changedScrap、attachedScrap
2. 屏幕外缓存 - cachedView - 数量 2
3. ViewCacheExtension - 
4. RecycledViewPool - SparseArray（key = ViewType，value = ArrayList）-数量=ViewType数量 * 5 ===> SparseArray
2、ViewHolder数量可以是多少？N + 2 + 扩展缓存数量 + ViewType数量 * 5
3、RV的管理核心：Recycler，如何管理缓存？
1. 存缓存：1. LFU最少使用原则，放入CachedView中 2. 满了放入Pool中
2. 取缓存：内、外、自、池，只有池Pool需要执行onBindViewHolder，其他的不crate，也不bind
4、RV采用的设计模式 ===> 观察者模式
1. RecyclerView是观察者
2. Adapter是被观察者，调用notifyItemChanged进行通知
5、RV的创新点是什么？
1. UpdateOp标志：
2. 定向刷新指定Item
3. 通过payload进行局部刷新
6、RV如何实现定向刷新的？
1. 无论全部刷新、定向刷新都会触发measure、layout
2. AdapterHlper内部存有UpdateOp列表，保存更新标志、起点、数量（范围）和payload（四个内容）
1. notifyItemChanged时，会在layout阶段，根据标志，执行onBindViewHolder
7、局部刷新原理 ==> 再研究下吗？
1. 在layout时，根据updateOp状态去bindViewHolder
2. 执行payload参数的onBinderViewHolder（notifyxxx调用的时候，也会执行具有payload参数的）
8、布局原理
1. 布局分为step1、step2、step3
2. step1：Adapter数据更新，根据UpdateOp标志，更新ViewHolder的标志
3. step2：实际布局是layoutChildren-fill方法-(获取四级缓存、measure、layout)  
4. step3：动画相关
9、ChildHelper用于帮助LayoutManager
## URI
URL
-分层URI
-转义URLEncoder、URLDecoder
URN：name
-不透明URI
Uri：Android的URI类
## WebView-JS
1、JS如何调用Android代码？
1. addJavascriptInterface
2. JS中调用Android的对话框，shouldOverideUrlLoading
3. WebViewClient的onJsAlert、onConfirm、onPrompt拦截JS的alert、confirm、prompt
   
2、Android调用JS
1. loadUrl-会刷新
2. evaluateJavascript-不会刷新
3、内存泄漏
1. WebView持有Context用WeakReference ====> JVM GC
2. 将WebView从父容器移除，然后再调用WebView的removeAllViews()和destory()
3. 终极方案：单独一个进程
## Context
### 结构
1、Context结构
-ContextImpl
-ContextWrapper-内部持有Impl
--Application
--Service
--ContextThemeWrapper
---Activity
2、一个App有几个context？
1. Application 1个 + N个Activity + N个Service
### 装饰者模式
1、采用了装饰者模式，为什么？
1. ContextWrapper持有了Impl的引用mBase
2. 目的：在ContextWrapper的基础上，添加了UI相关功能
3. 符合开闭原则，对扩展开放，对修改关闭
### 功能
1、Context提供了系统级别的操作
1. 资源类（String、Drawable、Theme、颜色、AssetManager）
2. 数据类（数据库、文件、目录、SP）
3. 系统服务（权限、四大组件、Application、Handler、电量、wifi、蓝牙、通知栏、AMS、PKMS、WMS、GPS、震动）
#### 深入
2、ContextImpl持有的成员变量和作用
1. ActivityThread
2. LoadedApk
3. IBinder mActivityToken -> 1. 跨进程通信 2.标识了一个唯一的ActivityRecord对象，也就简洁的标识了一个Activity对象
4. ClassLoader
5. Resources
### 何时创建的
1、Context什么时候创建的？（Activity）
1. handleLaunchActivity->performLaunchActivity->createContextForActivity()---对五个变量赋值，并且将Activity引用保存在ContextImpl内部
2. ->activity.attach()->attachBaseContext()，将Context引用保存到Activity'内部
2、getApplication和getApplicationContext区别
1. activity、service：两者都一样
2. 广播onReceive中，只能getBaseContetx().getApplicationContext()
3. ContentPriover中：getContext().getApplicationContext()
## Activity
1、为什么Service启动Activity，需要NEW_TASK？
1. Service没有Activity栈，需要创建新的栈
### Activity栈

### 黑白屏
1、Launcher点开某个应用需要十秒，疯狂点击会出现ANR吗？
1. 不会，会启动黑白屏去接收点击事件
2. 事件传不到Launcher，也不会触发ANR
2、黑白屏是哪个阶段启动的？
1. ActivityStarter：
2. 1-执行所有待启动的Activity
3. 2-处理启动模式：计算出在哪个栈
4. 3-启动黑白屏
3、处理启动模式的本质是什么？
1. 计算出在哪个栈
## IPC
1、Socket和Binder性能相差多少？
1. 相差不多？
2. Socket有安全问题
2、Binder都是阻塞的吗？
1. 拥有异步的方式，oneway
3、AIDL的in、out、inout是什么？
1. 服务端 《--in-- Client
2.  --out--》Client
3.  《--inout--》

## NDK
### Parcelable
1、Parcelable的使用
1. 核心：通过Parcel对象，读写数据
1. writeToParcel
2. MyObject(Parcel in)
```java
public class MyObject implements Parcelable {
    // 构造方法、成员变量等
    // 写入对象数据到Parcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(age);
        // 其他成员变量的写入
    }
    
    // 描述内容类型，默认返回0即可
    @Override
    public int describeContents() {
        return 0;
    }
    
    // Parcelable的构造器
    public static final Parcelable.Creator<MyObject> CREATOR = new Parcelable.Creator<MyObject>() {
        public MyObject createFromParcel(Parcel in) {
            return new MyObject(in);
        }
        
        public MyObject[] newArray(int size) {
            return new MyObject[size];
        }
    };
    
    // 反序列化构造方法
    private MyObject(Parcel in) {
        name = in.readString();
        age = in.readInt();
        // 其他成员变量的读取
    }
}
```
2、共享内存技术
1. 将native层对象的地址，作为long值交给Java层保存
2. 后续操作，用该long值传递到native层，拿到native层对象操作
3、好处
1. 不需要每次都new对象进行操作
2. 性能提升
4、自己实现Parcel
```
Parcel p = Parcel.obtain();
->单例new Parcel()
 ->init(nativePtr)
  ->nativeCreate()拿到对象首地址
   ->nativePtr = Parcel.cpp对象
// 转换为long，使用了reinterpret_cast强制类型转换：
// 1. 根据平台，底层对数据重新解释，将引用转为指针
```
===> OpenCv: (jlong)(new 对象)直接转换，不严谨
5、nativeCreate是如何注册的好处是什么？
1. android_os_Parcel.cpp动态注册了android_os_Pacel_create()方法
2. 动态注册：性能好，静态注册：性能差
6、底层数据操作
1. long转为Parcel()对象
1. writeInt等操作：在指针位置写入数据
2. flushWrite：移动指针，指针移动距离等于sizeof(数据类型)
3. 读取数据时：要重置指针，这就是为什么使用Parcelable有重置指针操作
   
7、Serializable和Parcelable区别
1. P核心：C++层，指针和long相互转换(共享内存)，移动指针读写数据(对数据顺序要求严格)



## Intent
### Intent传递数据为什么要序列化？
启动Activity等涉及到AMS的操作，需要跨进程通信，只能序列化。
=> AMS流程
=> Binder
=> 进程隔离 => 零拷贝 => mmap
## 主线程
判断当前是否是主线程 => Handler
```
Looper.getMainLooper().getThread() == Thread.currentThread()
Looper.getMainLooper() == Looper.myLooper()
```

## Lifecycle
=> AtomicRefrence

## onTrimMemory
1、Activity得到内存情况的通知：
```
onTrimMemory(int level){
    // level 
}
// 
```

2、运行时回调：App在前台
1. 内存低、系统已经在杀死App

3、缓存时回调：App在后台
1. 不同回调标识在LRU缓存中的位置，越靠前越不会被杀死。

## 依赖注入

1. 将实例化的权利交给外界去做。也就是将创建依赖的权利交给外部。外部注入到内部。
1. dagger2是最常见的注入框架。

## Bundle
1、Bundle内部为什么不用hashmap？
1. 内部使用了ArrayMap，适合小数据量。
1. 符合bundle场景。
1. bundle用parcelable序列化。

## Bitmap优化

1、BitmapFactoryOptions
1. 像素格式
1. 采样率。设置2。宽高为1/2，总共1/4。必须是2 4 8

2、Bitmap的三级缓存
> 分配了图片a，a不需要用了。需要图片b，会去和a比较size，小于等于就用a的内存空间。

3、大图加载
> gradle的缓存缩放后的图片。

