# Framework

本文链接：

# Handler

1、Handler是什么？（总分总）
1. Android的跨线程机制
1. 核心在于，利用线程间内存共享的原理，进行消息的存放和取出
1. 关键知识点：MessageQueue、Looper、内存泄漏

## Message

1、存消息
2、取消息

### 同步消息
1、同步消息
1. 正常消息都是同步消息
1. aysnc = false
### 异步消息
1、异步消息
1. UI刷新等操作都是异步消息
### 同步屏障
1、同步屏障
1. 是target = null的特殊消息
2. 会插入到消息队列头部
3. Looper遇到同步屏障，找到并且执行异步消息，直到同步屏障被移除，否则一直while

#### 刷新机制
1、performTraversals
1. 编舞者去注册对VSYNC信号监听的时候
2. 会post同步屏障
3. VSYNC信号到来时，doTraversal
4. 会remove同步屏障
```
-postSyncBarrier
-Chorographer(控制UI刷新)
    -doTraversal
        -removeSynBarrier
```

### 为什么还会卡
1、有异步消息和同步屏障机制为什么还会卡？怎么解决
1. 当异步消息投递到队列时，正在执行一个很耗时的消息
2. 该消息处理完成后，才能第一次遇到同步屏障，才会按照机制取优先处理
3. 日志角度：
```
skipped 30 frames
```
2、如何解决和定位监控？
1. Looper日志接口加日志打印
2. ASM字节码插桩到各个业务入口处，要进行优化

### 享元模式 ===> 设计模式

1、Handler的Message采用享元模式
1. 取
1. 回收

2、obtain从对象池获取Message对象
3、采用单链表实现：表头取 + 表头 存放

4、recycleUnchecked-置空

5、线程安全问题：为什么要加synchronized，next明明只有一个地方调用？
> 存数据和取数据，会存在线程安全问题
> 变种问题：next只有一个地方调用，为什么要给next加上synchronized锁

6、enqueueMsg

### 内存抖动 ===> Bitmap导致的内存泄漏

1、16.7ms一个信号，需要两个消息
1. 刷新消息（同步消息）
1. 同步屏障（异步消息）
1. 去除异步消息

2、1s需要120~180个对象

3、Message上限50个
1. 突破了会new对象
1. ===================> 给notification发送消息，3h后App一些功能开始卡顿

### Looper

1、Looper中死循环为什么不会导致ANR？   =======> ANR
2、点击事件谁处理的？需要主线程处理吗？

3、Service的ANR流程？
1. Service启动时:App->system_server(埋入炸弹)->Service进程（处理生命周期）->system_server(done消息)->拆除炸弹

4、Android是事件驱动

======> ThreadLocal
1. ThreadLocalMap(key = Entry,WeakReferecne,ThreadLocal,value = )
1. value可以用WeakReference吗？
1. 开放寻址法
1. 线程隔壁变量，不同步手段
1. getEntry-除留余数法

## IdleHandler
1、是什么？
1. 消息队列中没有消息时才会执行

2、有什么用？场景
1. onCreate、onStart、onCreate中执行短暂且不重要的任务
2. 性能优化：LeakCanary(空闲时进行内存泄漏检测，对内存快照分析)  ===> LeakCanary
3. Framework: Activity A启动B，Activity A的onStop是在IdleHandler中进行的。提升用户体验。 ======> 动画
  1. 不要在onStop中取消动画，动画的频繁刷新操作会导致不执行。
  2. 不要在onVisibilityChanged(int visibility)，取消动画，假如有透明页面，会导致动画一直执行
  3. 在onPause中取消。
  4. 内存泄漏问题：ActivityB的销毁，因为动画一直执行，导致没办法执行。

### queueIdle
返回值：
true: 每次都会调用
false: 调用一次
## HandlerThread
1. 内部有创建Handler

1、为什么synchronized锁住this，而不是Looper？
1. 两者共同关系是同一线程
1. 对线程加锁更好

2、为什么用notifyAll而不是notify？
1. 多个地方在等待

## 内存泄漏    ====> 内存泄漏
1、Handler的GC Root是谁？
1. Looper中有static Looper sMainLooper， 持有了Looper是永久的，强引用，一定会泄露


2、Handler内存泄漏的持有路径
1. 匿名内部类持有外部类引用
1. Activity.this->Handler->Message->MessageQueue->Looper

3、匿名内部类和静态内部类的区别？
1. 静态内部类，按原生翻译出来应该是，静态嵌套类。

4、如何避免Handler导致内存泄漏
1. Handler使用完记得调用removeMsg
1. Handler持有的Context引用，用WeakReference包裹

5、为什么ThreadLocal在get和set时进行清理工作，可能不完全，不及时
1. 不可能扫描整个散列表进行清理
1. 在寻找目标的过程中，正好遇到key = null的情况，才会清理Entry
1. 假如导致出现泄露的value所在的桶，没有被找到过，不会被清理

6、ThreadLocal与内存泄漏
1. ThreadLocal中Thread内部的ThreadLocalMap，key=ThreadLocal,value=引用，Entry继承自WeakReference
    1. 如果Activity结束，key = ThreadLocal = null，value=引用
    2. 后续有set、get等操作，会导致清理key = null的Entry，value持有的就会释放。不会内存泄露
1. 线程如果结束，ThreadLocalMap就被释放，里面的所有东西也会被GC

7、如何避免ThreadLocal的内存泄漏？
1. remove()清理Thread中ThreadLocalMap中当前ThreadLocal对应的Entry
```java
public void remove() {
    ThreadLocalMap m = getMap(Thread.currentThread());
    if (m != null)
        m.remove(this);
}
```

## 源码剖析

### Handler

1、Handler的构造
```java
    public Handler(@Nullable Callback callback, boolean async) {
        // 1、拿到当前线程专属的Looper
        mLooper = Looper.myLooper(); // sThreadLocal.get()
        // 2、不存在就会抛出异常  ===> 为什么子线程不创建Looper会报错
        if (mLooper == null) {
            throw new RuntimeException(
                "Can't create handler inside thread " + Thread.currentThread()
                        + " that has not called Looper.prepare()");
        }
        // 3、获取到Looper的MessageQueue
        mQueue = mLooper.mQueue;
        mCallback = callback;
        mAsynchronous = async;
    }
```

2、发送消息
```kotlin
// 交给delayed
    public final boolean post(@NonNull Runnable r) {
       return  sendMessageDelayed(getPostMessage(r), 0);
    }
// 交给sendMessageAtTime
    public final boolean sendMessageDelayed(@NonNull Message msg, long delayMillis) {
        if (delayMillis < 0) {
            delayMillis = 0;
        }
        return sendMessageAtTime(msg, SystemClock.uptimeMillis() + delayMillis);
    }
    public boolean sendMessageAtTime(@NonNull Message msg, long uptimeMillis) {
        MessageQueue queue = mQueue;
        return enqueueMessage(queue, msg, uptimeMillis);
    }
// 交给MessageQueue
    private boolean enqueueMessage(@NonNull MessageQueue queue, @NonNull Message msg,
            long uptimeMillis) {
        msg.target = this;
        msg.workSourceUid = ThreadLocalWorkSource.getUid();

        if (mAsynchronous) {
            msg.setAsynchronous(true); // 默认异步消息
        }
        return queue.enqueueMessage(msg, uptimeMillis);
    }
```

### Looper

1、Looper.myLooper():获取当前线Looper
```java
    public static @Nullable Looper myLooper() {
        return sThreadLocal.get();
    }
```

#### ThreadLocal

1、ThreadLocal的get()
```java
    public T get() {
        // 1、获取线程
        Thread t = Thread.currentThread();
        // 2、获取线程ThreadLocalMap
        ThreadLocalMap map = getMap(t);
        if (map != null) {
          // 3、以自己为key，查询到Entry
            ThreadLocalMap.Entry e = map.getEntry(this);
            if (e != null) {
                @SuppressWarnings("unchecked")
                // 4、返回value
                T result = (T)e.value;
                return result;
            }
        }
        return setInitialValue();
    }
```

2、ThreadLocal.ThreadLocalMap是什么时候创建的？ThreadLocal第一次set时，没有就会创建
```java
    public void set(T value) {
        Thread t = Thread.currentThread();
        ThreadLocalMap map = getMap(t);
        if (map != null)
            map.set(this, value);
        else
            createMap(t, value); // 创建
    }

    void createMap(Thread t, T firstValue) {
        t.threadLocals = new ThreadLocalMap(this, firstValue);
    }
```

3、ThreadLocal.ThreadLocalMap.set()
```java
// 1、散列表
        private void set(ThreadLocal<?> key, Object value) {

            Entry[] tab = table;
            int len = tab.length;
          // 2、除留余数法
            int i = key.threadLocalHashCode & (len-1);

            for (Entry e = tab[i];
                 e != null;
                 e = tab[i = nextIndex(i, len)]) { // 3、开放寻址法：线性寻址 + 1
                ThreadLocal<?> k = e.get();

                if (k == key) { // 4、找到合适位置存入数据
                    e.value = value;
                    return;
                }

                if (k == null) {
                  // 5、key = null的进行清理
                    replaceStaleEntry(key, value, i);
                    return;
                }
            }

            tab[i] = new Entry(key, value);
            int sz = ++size;
            if (!cleanSomeSlots(i, sz) && sz >= threshold)
                rehash();
        }
```

#### loop()

```java
    public static void loop() {
        final Looper me = myLooper();
        final MessageQueue queue = me.mQueue;

        for (;;) {
          // 1、阻塞取出消息
            Message msg = queue.next(); // might block
            if (msg == null) {
                // No message indicates that the message queue is quitting.
                return;
            }

            // 2、LOG日志 ===> 卡顿

            final Printer logging = me.mLogging;
            if (logging != null) {
                logging.println(">>>>> Dispatching to " + msg.target + " " +
                        msg.callback + ": " + msg.what);
            }
            
            // 3、设置给进程的所有Looper
            final Observer observer = sObserver;

            try {
              // 4、调用dispatchMessage
                msg.target.dispatchMessage(msg);
                if (observer != null) {
                  // 5、进程内部用的
                    observer.messageDispatched(token, msg);
                }
            } 

// 6、享元模式回收
            msg.recycleUnchecked();
        }
    }
```

### MessageQueue

1、MessageQueue的next是如何等待的？
1. 调用nativePollOnce(time)去休眠
1. 底层调用到epoll_wait

2、消息队列的唤醒
1. nativeWake 在放入消息时调用该方法会进行唤醒

1、MessageQueue的构造
```java
    MessageQueue(boolean quitAllowed) {
        mQuitAllowed = quitAllowed;
        mPtr = nativeInit();
    }

// andorid_os_MessageQueue.cpp
    static jlong android_os_MessageQueue_nativeInit(JNIEnv* env, jclass clazz) {
      // 1. 创建Native层MQ对象，并且返回
    NativeMessageQueue* nativeMessageQueue = new NativeMessageQueue();
    if (!nativeMessageQueue) {
        jniThrowRuntimeException(env, "Unable to allocate native queue");
        return 0;
    }

    nativeMessageQueue->incStrong(env);
    // 2. 重新解释为jlong
    return reinterpret_cast<jlong>(nativeMessageQueue);
}
```

2、消息发送
```java
   boolean enqueueMessage(Message msg, long when) {
      msg.markInUse();
      msg.when = when; // 时间

      // 省略，队列操作，放入Message
      // 根据when的数值，插入到合适位置
      
      // 唤醒nativePollOnce等待中的线程
      nativeWake(mPtr); // mPtr ===> NDK
      // mPtr = nativeInit();
    }
```

#### next：取消息


3、取消息
```java
    Message next() {
        final long ptr = mPtr;

        int nextPollTimeoutMillis = 0;
        for (;;) {

            // 1. 阻塞指定时间，-1代表一直阻塞直到唤醒
            nativePollOnce(ptr, nextPollTimeoutMillis);

            synchronized (this) {
                
                // 同步屏障 + 异步消息处理

                // 计算当前时间和需要处理的消息时间 差值，并且睡眠
                nextPollTimeoutMillis = (int) Math.min(msg.when - now, 
                // or
                nextPollTimeoutMillis = -1;

            }

            // IdleHandler处理- queueIdle
            for (int i = 0; i < pendingIdleHandlerCount; i++) {
                boolean keep = idler.queueIdle();
                if (!keep) {
                    synchronized (this) {
                        mIdleHandlers.remove(idler);
                    }
                }
            }
        }
    }
```

##### nativePollonce

```java
private native void nativePollOnce(long ptr, int timeoutMillis);
```

1、nativePollonce的作用是什么？
1. 会睡眠指定时间
1. 传入-1 epoll会无限等待

## 问题
1、子线程到主线程通讯方式有哪些？子线程到主线程的原理？
1. Handler、EventBus
1. Handler内部是MessageQueue + Looper
1. 主线程启动时ActivityThread的main方法中，prepareMainLooper后，会startLooper无限循环。
1. 等待MessageQueue中的消息，没有消息就等待。底层是nativePollOnce，再到底层就是epoll_wait  =======>

2、handler内存泄漏的原因
1. Handler持有外部Activity.this

3、MessageQueue中Message数量有上限吗？为什么这么设计？能不能用阻塞队列？
1. 没有上限，不然会丢失消息
1. 阻塞队列会阻塞卡顿

4、Handler如何处理发送延时消息？
1. 非延时消息和延时消息调用的都是sendMessageAtTimes方法，参数是延时时间
1. 会根据参数when决定插入队列中的哪个位置
1. MessageQueue获取消息时，会根据when和当前时间，决定nativePollOnce的睡眠时间

5、使用Message时应该如何创建？
1. obtain
1. 享元设计模式

6、Handler没有消息处理时是阻塞的还是非阻塞的？为什么不会有ANR？
1. 阻塞的
1. 不会ANR, 无论是点击事件，还是其他其中情况代码的执行都是围绕消息机制执行的，点击事件会被投递到MessageQueue中并且做后续的处理
```java
//ViewRootImpl#处理事件时
mHandler.post(xxx);
```

7、子线程如何创建Handler？
1. 准备Looper
1. 再创建Handler

------------------------------------------------

# Binder
1、app binder和framework service binder注册有什么区别？
1. app binder存放在`system_service`进程的`AMS`中
2. AMS等系统服务的binder存放在`SystemServer进程`的`ServiceManager`中
```
ServiceManager(SystemServer进程)
|AMS Binder|
|WMS Binder|
|  ...     |
```
2、app binder具体存放在AMS的哪里？
1. AMS中有`ProcessRecord`---对应了唯一进程，存放了binder变量
3、AMS和App通信的binder是阻塞的还是非阻塞的？
1. 一般要等待结果的是阻塞的
2. 部分是非阻塞oneway的

## Linux IPC

1、IPC方式介绍
|方式|介绍|应用|
|---|---|---|
|管道|fd[1]读端 <br> fd[0]写端 <br> 基于特殊文件 <br> 1. 命名管道 <br> 2. 无名管道||
|消息队列|类型和消息对应。约定好取哪个消息||
|共享内存||1. WMS和SurfaceFlinger需要大量渲染 <br> 2. 图库|
|文件|||
|Sokcet|Local Socket|Zygote|
|信号量|IPC同步手段|mmkv|
|信号|||

## Android IPC
1、IPC的场景
1. App之间需要IPC
2. Framework之间需要IPC
3. App和Framework需要IPC
4. Framework和native层需要JNI+IPC
5. Native层到Linux Kernel是SystemCall，系统调用

2、多进程有哪些使用场景？
1. 突破单进程限制，分配更多资源（浏览器+播放器，给更多“CPU”执行机会和“内存”）
2. 图库（内存）
3. 稳定、风险隔离， ===> WX消息push在独立进程
4. 性能监控 ===> APM，fork子进程，dump信息




## Binder概念

1、Binder为什么需要第一时间创建？
1. Activity等组件都需要Binder

2、Zygote有自己的Binder吗？
>没有，zygote主要任务是fork进程
>1. ServiceManager会初始化系统Binder调用
>1. 其他进程在fork后会在nativeZygoteInit中调用native方法，里面会在ProcesseState对象创建的时候，open driver，创建binder，并且mmap进行映射

3、Binder是什么？
1. 应用层面：Android IPC机制，在进程间提供跨进程通信的CS的架构
1. Java层面：一个类，继承自IBinder接口，提供了跨进程能力
1. 驱动层面：Binder Driver，虚拟物理设备驱动
1. Linux层面：/dev/binder文件

## Binder驱动初始化流程

### ServiceManager的Binder

1、ServiceManager是什么？
1. 独立进程，init进程创建，比zygote更早
1. Android10后有servicemanager.rc文件

2、ServiceManager和SystemServer关系
1. ServiceManager是服务的注册表，用于注册、查找、访问
1. ServiceManager比zygote早(也就比SystemServer早)
1. 会初始化Binder驱动，并将自己作为第一个服务注册
1. 用于处理Binder事务，用Linux Looper Epoll机制 while

3、SystemServer
1. 运行了AMS、WMS等服务
1. 会将服务注册到ServiceManager中

2、ServiceManager的启动流程
系统启动过程中的main()中会解析`init.rc`并且启动ServiceManager进程。执行main()
main.cpp frameworks\native\cmds\servicemanager\main.cpp
```c++
int main(int argc, char** argv) {

    //1. 初始化binder驱动 /dev/binder
    sp<ProcessState> ps = ProcessState::initWithDriver(driver);
    ps->setThreadPoolMaxThreadCount(0);
    ps->setCallRestriction(ProcessState::CallRestriction::FATAL_IF_NOT_ONEWAY);

    //2. 实例化ServiceManager，将自身作为服务addsevice注册
    sp<ServiceManager> manager = new ServiceManager(std::make_unique<Access>());
    if (!manager->addService("manager", manager, false /*allowIsolated*/, IServiceManager::DUMP_FLAG_PRIORITY_DEFAULT).isOk()) {
        LOG(ERROR) << "Could not self register servicemanager";
    }
    //设置服务端BBinder
    IPCThreadState::self()->setTheContextObject(manager);
    //设置自己为binder驱动的上下文管理者
    ps->becomeContextManager(nullptr, nullptr);
    //3. 利用Looper epoll机制处理Binder事务
    sp<Looper> looper = Looper::prepare(false /*allowNonCallbacks*/);

    BinderCallback::setupTo(looper);
    ClientCallbackCallback::setupTo(looper, manager);

    while(true) {
        looper->pollAll(-1);
    }

}
```

### SystemServer
在SystemServer启动流程中ZygoteInit.nativeZygoteInit()会进行Binder初始化
```c++
ZygoteInit.nativeZygoteInit()
```
对应于：AndroidRuntime.cpp ->目录frameworks\base\core\jni\AndroidRuntime.cpp
```c++
AndroidRuntime.cpp
static void com_android_internal_os_ZygoteInit_nativeZygoteInit(JNIEnv* env, jobject clazz)
{
    //gCurRuntime 是app_main.cpp 中 AppRuntime 的一个对象
    gCurRuntime->onZygoteInit();
}
```
app_main.cpp ->目录frameworks\base\core\cmds\app_process\
```c++
    virtual void onZygoteInit()
    {
        sp<ProcessState> proc = ProcessState::self();
        ALOGV("App process: starting thread pool.\n");
        proc->startThreadPool();
    }
```
ProcessState.cpp ->目录frameworks\native\lib\binder\ProcessState.cpp
```c++
ProcessState::ProcessState(const char *driver)
    : mDriverName(String8(driver))
    , mDriverFD(open_driver(driver))
    , mVMStart(MAP_FAILED)
    , mThreadCountLock(PTHREAD_MUTEX_INITIALIZER)
    , mThreadCountDecrement(PTHREAD_COND_INITIALIZER)
    , mExecutingThreadsCount(0)
    , mMaxThreads(DEFAULT_MAX_BINDER_THREADS)
    , mStarvationStartTimeMs(0)
    , mBinderContextCheckFunc(nullptr)
    , mBinderContextUserData(nullptr)
    , mThreadPoolStarted(false)
    , mThreadPoolSeq(1)
    , mCallRestriction(CallRestriction::NONE)
  {

// TODO(b/139016109): enforce in build system
#if defined(__ANDROID_APEX__)
    LOG_ALWAYS_FATAL("Cannot use libbinder in APEX (only system.img libbinder) since it is not stable.");
#endif

    if (mDriverFD >= 0) {

// Step2 重要函数
        //调用mmap接口向Binder驱动中申请内核空间的内存
        // mmap the binder, providing a chunk of virtual address space to receive transactions.
        mVMStart = mmap(nullptr, BINDER_VM_SIZE, PROT_READ, MAP_PRIVATE | MAP_NORESERVE, mDriverFD, 0);
        if (mVMStart == MAP_FAILED) {
            // *sigh*
            ALOGE("Using %s failed: unable to mmap transaction memory.\n", mDriverName.c_str());
            close(mDriverFD);
            mDriverFD = -1;
            mDriverName.clear();
        }
    }

#ifdef __ANDROID__
    LOG_ALWAYS_FATAL_IF(mDriverFD < 0, "Binder driver '%s' could not be opened.  Terminating.", driver);
#endif
} 

// Step1 重要函数
static int open_driver(const char *driver)
{

    //打开binder驱动
    int fd = open(driver, O_RDWR | O_CLOEXEC);
    if (fd >= 0) {
        int vers = 0;
        //验证binder版本
        status_t result = ioctl(fd, BINDER_VERSION, &vers);
        if (result == -1) {
            ALOGE("Binder ioctl to obtain version failed: %s", strerror(errno));
            close(fd);
            fd = -1;
        }
        if (result != 0 || vers != BINDER_CURRENT_PROTOCOL_VERSION) {
          ALOGE("Binder driver protocol(%d) does not match user space protocol(%d)! ioctl() return value: %d",
                vers, BINDER_CURRENT_PROTOCOL_VERSION, result);
            close(fd);
            fd = -1;
        }
        //设置binder最大线程数：DEFAULT_MAX_BINDER_THREADS 15
        size_t maxThreads = DEFAULT_MAX_BINDER_THREADS;
        result = ioctl(fd, BINDER_SET_MAX_THREADS, &maxThreads);
        if (result == -1) {
            ALOGE("Binder ioctl to set max threads failed: %s", strerror(errno));
        }
    } else {
        ALOGW("Opening '%s' failed: %s\n", driver, strerror(errno));
    }
    return fd;
}
```

## fork

1、Linux中为什么不允许在多线程环境下fork？
> 会死锁

2、zygote为什么用socket方式？
1. binder底层针对每次请求都会有独立的线程去完成任务>binder线程池  ====> 线程池
2. zygote不允许有binder，会死锁，只能用socket

## AIDL

1. AIDL可以用aidl.exe生成cpp版本的文件
1. 典型【代理模式】       ================> 代理模式

### 通信流程

1、Client发起服务请求
1. BinderProxy nativeTransact()
1. android_util_binder BinderPorxy_transact
1. BpBinder.transact
1. IPCThreadState.transact
    1. write数据到Parcel mOut中，不会立即发起请求
    1. waitForResponse()->talkWithDriver()拿出mOut中数据放到bwr中(binder_write_read)


2、获取到ServiceManager的Binder的流程
1. IServiceManager.getXXX
1. BinderInternel.getTheContextObject->ProcessState::getContextObject // 会拿到ServiceManager
    1. 从0位置拿到Binder，没有拿到就新建BpBinder
    1. 将native BpBinder用反射构造出BinderProxy

## mmap
内存读写，代替IO读写。

# Android系统启动流程

## JNI注册

AndroidRuntime.cpp中注册了所有JNI
```c++
    if (register_jni_procs(gRegJNI, NELEM(gRegJNI), env) < 0) {
        env->PopLocalFrame(NULL);
        return -1;
    }

// 数组存放了所有JNI对应关系
RegJNIRec gRegJNI[] = {....}
```

## 源码：Zygote启动全流程
1、app_main.cpp 启动Zygote或者正常app流程 ->目录frameworks\base\cmds\app_process\
```c++
main(){
    //   启动zygote的java层调用
    if (zygote) {
        ////zygote 为true 表示正在启动的进程为zygote进程
        //由此可知app_main.cpp在zygote启动其他进程的时候都会通过main()方法
        //这里启动的是zygote进程调用runtime start()方法 传入参数
        runtime.start("com.android.internal.os.ZygoteInit", args, zygote);
    } else if (className) {  //这个地方是用于启动app的
        runtime.start("com.android.internal.os.RuntimeInit", args, zygote);
    } 
}
```

2、AndroidRuntime.cpp ->目录frameworks\base\core\jni\
```c++
void AndroidRuntime::start(const char* className, const Vector<String8>& options, bool zygote)
{

    // 创建虚拟机
    if (startVm(&mJavaVM, &env, zygote, primary_zygote) != 0) { 
        return;
    }    
    // 注册JNI
    if (startReg(env) < 0) {
        return;
    }
    // 进入zygoteInit.main,将zygoteinit带入java世界
    char* slashClassName = toSlashClassName(className != NULL ? className : "");
    jclass startClass = env->FindClass(slashClassName);

    jmethodID startMeth = env->GetStaticMethodID(startClass, "main",
"([Ljava/lang/String;)V");

    env->CallStaticVoidMethod(startClass, startMeth, strArray);
}
```

3、ZygoteInit.java ->目录frameworks\base\cor\java\com\android\internal\os\
```c++

public static void main(String argv[]) {
    //step1 重要的函数 preload
    preload(bootTimingsTraceLog);
    //Step2 重要函数 创建socket服务
    zygoteServer = new ZygoteServer(isPrimaryZygote);  

   if (startSystemServer) {
        //Step3 重要函数 Zygote Fork出的第一个进程 system_server
        Runnable r = forkSystemServer(abiList, zygoteSocketName, zygoteServer);
        r.run(); // r: ZygoteInit.zygoteInit
        return;
   }
   //Step4 重要函数 循环等待fork出其他的应用进程，比如Launcher，比如app
   caller = zygoteServer.runSelectLoop(abiList);
   //执行返回的Runnable对象，进入子进程
   caller.run();
      
}
```

### preload
```c++
    static void preload(TimingsTraceLog bootTimingsTraceLog) {
        preloadClasses();// 加载系统类
        preloadResources();// 加载系统资源
        nativePreloadAppProcessHALs();
        maybePreloadGraphicsDriver();
        preloadSharedLibraries();// 加载一些共享so库，其实就三个：android、compiler_rt、jnigraphics
        preloadTextResources();// 加载字体资源
        WebViewFactory.prepareWebViewInZygote();// 加载webview相关资源
        warmUpJcaProviders();// 初始化JCA安全相关的参数
    }

```

### SystemServer

1、fork出SystemServer
ZygoteInit.java ->目录frameworks\base\core\java\com\android\internal\os\
```c++
    private static Runnable forkSystemServer(String abiList, String socketName,
            ZygoteServer zygoteServer) {
        int pid = Zygote.forkSystemServer(
                    parsedArgs.mUid, parsedArgs.mGid,
                    parsedArgs.mGids,
                    parsedArgs.mRuntimeFlags,
                    null,
                    parsedArgs.mPermittedCapabilities,
                    parsedArgs.mEffectiveCapabilities);

        // 子进程继续处理
        if (pid == 0) { 
            if (hasSecondZygote(abiList)) {
                waitForSecondaryZygote(socketName);
            }
            zygoteServer.closeServerSocket();
            return handleSystemServerProcess(parsedArgs);
        }
        // 父进程什么也不做
        return null;
    }
```

2、Zygote.java frameworks\base\core\java\com\android\internal\os\Zygote.java
```java
    static int forkSystemServer(int uid, int gid, int[] gids, int runtimeFlags, int[][] rlimits, long permittedCapabilities, long effectiveCapabilities) {

        int pid = nativeForkSystemServer(
                uid, gid, gids, runtimeFlags, rlimits,
                permittedCapabilities, effectiveCapabilities);

        return pid;
    }

// 核心作用：调用底层fork()
    private static native int nativeForkSystemServer(int uid, int gid, int[] gids, int runtimeFlags,
            int[][] rlimits, long permittedCapabilities, long effectiveCapabilities);
```

3、ZygoteInit.java frameworks\base\core\java\com\android\internal\os\
```java
private static Runnable handleSystemServerProcess(ZygoteArguments parsedArgs) 
    
    ClassLoader cl = createPathClassLoader(systemServerClasspath, parsedArgs.mTargetSdkVersion); ===> PathClassLoader

    return ZygoteInit.zygoteInit(parsedArgs.mTargetSdkVersion,
                    parsedArgs.mDisabledCompatChanges,
                    parsedArgs.mRemainingArgs, cl);
}
```

4、ZygoteInit.java frameworks\base\core\java\com\android\internal\os\
```c++
    public static final Runnable zygoteInit(xxx) {
        //1. 初始化运行环境
        RuntimeInit.commonInit();//初始化运行环境 
        //2. 启动Binder ，方法在 androidRuntime.cpp中注册       
        ZygoteInit.nativeZygoteInit();
        //3. 通过反射创建程序入口函数的 Method 对象，并返回 Runnable 对象
        // ActivityThread.main
        return RuntimeInit.applicationInit(targetSdkVersion, disabledCompatChanges, argv, classLoader);
    }

    protected static Runnable applicationInit(xxx, ClassLoader classLoader) {
        // startClass: 如果AMS通过socket传递过来的是 ActivityThread
        return findStaticMain(args.startClass, args.startArgs, classLoader);
    }
```

#### RuntimeInit.commonInit

framworks\base\core\java\com\android\internal\os\
```java
    protected static final void commonInit() {
        if (DEBUG) Slog.d(TAG, "Entered RuntimeInit!");

//1、设置异常处理器：
// 1. 设置未捕获异常的预处理器（pre-handler）为 LoggingHandler，用于处理未捕获的异常日志。
// 2. 设置默认的未捕获异常处理器为 KillApplicationHandler，用于处理应用程序崩溃并终止应用。 ===> ANR KillApplicationHandler
        LoggingHandler loggingHandler = new LoggingHandler();
        RuntimeHooks.setUncaughtExceptionPreHandler(loggingHandler);
        Thread.setDefaultUncaughtExceptionHandler(new KillApplicationHandler(loggingHandler));

//2、设置日志管理器
        LogManager.getLogManager().reset();
        new AndroidConfig();

//3、关联套接字标记和流量统计：
        NetworkManagementSocketTagger.install();
// 省略
        initialized = true;
    }
```

## SystemServer
### 创建Context
1、SystemServer的启动流程
```
run()
->createSystemContext()
  ->1. 创建ActivityThread，并且activityThread.attach(true, 0)
    -> 把自己的ApplicationThread（binder对象）交给了AMS
  ->2. 创建ContextImpl并且设置
```



# AMS
## Service
1、bindService流程
```
App:bindService()
      --------> 1.获取ServiceManager的Binder 
ServiceManager:          
      --------> 2.获取到AMS的Binder
      --------> 3.AMS.bindSerivce 
AMS:
      --------> 4.activitythread.scheduleBindService
目标Service:
      --------> 5. 拿到ServiceManager
ServiceManager:
      --------> 6. 拿到AMS
AMS:
      --------> 7. AMS.publish
App:
      --------> 8.c.conn.connected  
```