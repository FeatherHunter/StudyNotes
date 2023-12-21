

# Binder

## IPC


### Linux IPC

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

#### fork

1、Linux中为什么不允许在多线程环境下fork？
> 会死锁

2、zygote为什么用socket方式？
1. binder底层针对每次请求都会有独立的线程去完成任务>binder线程池  ====> 线程池
2. zygote不允许有binder，会死锁，只能用socket

### Android IPC

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


3、Messager特点
1. 一对多，串行
1. 无RPC

4、开启多进程的方式：android:process
1. :remote 当前应用私有进程
1. com.example.remote 全局进程，其他应用可以用ShareUID和其跑在同一个进程 ====> App属于系统级别，需要ShareUid

#### Binder IPC概念
1、系统服务和应用分不同进程的好处？
1. 安全
2. 稳定
3. 内存回收和分配
 
2、客户端app几个进程？ =======> fork
1. push
2. 浏览相册图片
3. webview
4. dump时fork
 
3、多进程好处
1. 内存突破：一个进程522MB ========>为什么
 
4、IPC方式中共享内存使用场景
1. WM和SFlinger有大块内存
2. WX图片传递 用 共享内存
 
5、共享内存是如何实现的？
1. 两个进程都有共享虚拟地址，映射到物理内存上同一块共享内存


6、Binder为什么需要第一时间创建？
1. Activity等组件都需要Binder

7、Zygote有自己的Binder吗？
> 没有，zygote主要任务是fork进程
> 1. ServiceManager会初始化系统Binder调用
> 1. 其他进程在fork后会在nativeZygoteInit中调用native方法，里面会在ProcesseState对象创建的时候，open driver，创建binder，并且mmap进行映射

8、Binder是什么？
1. 应用层面：Android IPC机制，在进程间提供跨进程通信的CS的架构
1. Java层面：一个类，继承自IBinder接口，提供了跨进程能力
1. 驱动层面：Binder Driver，虚拟物理设备驱动
1. Linux层面：/dev/binder文件

9、linkToDeath是什么？死亡代理

10、Binder服务端是线程池，是非线程安全的
11、Bidner客户端发起请求会阻塞线程，不要在UI线程发起操作。

12、ContentProvider的增删改查运行在Binder线程池中
1. 属于被限制的AIDL
1. 适合一对多并发数据共享、CRUD
 
#### 性能
6、Binder和Socket性能？
1. 经过测试两者差别很小
2. 针对Unix domain Socket有优化  ========> 腾讯音视频里面测试1MB 5MB 10MB数据
 
#### 安全
7、Binder和Socket稳定性都是CS结构
8、共享内存需要并发控制，稳定性，安全性都不可以，有复杂度问题
9、Binder运行在内核有PID UID，保证安全性
10、安全性，共享内存和Socket别人可以根据协议进行伪造
 
1、Binder可以传输数据大小：1024-8K

### AIDL

AIDL的设计模式
1. 典型【代理模式】       ================> 代理模式

1、AIDL可以使用的数据类型
1. 适合场景：服务端有同步需求，可以支持并发读写：
1. ConcurrentHashMap
1. CopyOnWriteArrayList：AIDL只支持ArrayList，系统内部会Copy出ArrayList返回

2、AIDL中的观察者模式：RemoteCallbackList
1. 解除注册时，callback已经不是同一个了（序列化导致）
1. 没办法解除注册
1. 内部是《IBinder，Callback》：可以用Binder对象找到Callback接口
1. 可以自动线程同步，用来放观察者Observer比较好

#### 结构

1、Binder结构

2、Stub
1. 构造：attachInterface(this, DESCRIPTOR); 将自己存储到Binder内部
2. asInterface(IBinder obj) 
   1. obj.queryLocalInterface(DESCRIPTOR) // 查询到IInterface，代表是同一个进程，直接返回IInterface实现者
   2. Proxy(obj) // 返回Proxy代理
3. onTransact(int code, Parcel data, Parcel reply, int flags) //根据code判断具体是什么服务，从data读取数据，处理结果写入reply

3、Proxy
1. 构造，拿到远端Binder对象：mRemote
2. 业务方法：底层调用mRemote.transact(code, data, reply, flags)

4、AIDP实现流程
1. 服务端Service，onBind()被调用，返回AIDL文件中构造出来的Binder对象
2. 客户端：bindService，在onServiceConnected中拿到Binder对象并且asInterface处理后，可以发起服务请求。
```java
myService = IMyAidlInterface.Stub.asInterface(service)
```

##### asInterface

1、asInterface
1. 在同一个进程，直接返回服务端Binder对象
1. 不同进程，返回Proxi

2、如何判断是否在同一个进程？
1. 服务端启动构造Stub时，会调用attachInterface，会以key=DESCRIPTOR作为标识，value=自己，保存在Binder内部
1. asInterface会调用queryLocalInterface查询，查询到，就代表在同一进程，直接使用

#### aidl生成java
```
aidl IMyService.aidl
```

#### aidl生成cpp

1、cpp版本AIDL
> AIDL可以用aidl.exe生成cpp版本的文件
```
aidl -b cpp -p <package_name> -o <output_directory> src/main/aidl/MyInterface.aidl
```


## Binder：驱动初始化

### ServiceManager：初始化Binder驱动并管理事务

1、ServiceManager是什么？
1. 独立进程，init进程创建，比zygote更早
1. Android10后有servicemanager.rc文件
```c
// servicemanager.rc
service servicemanager /system/bin/servicemanager
    class core animation
    user system // 系统服务出问题，系统就会重启
    group system readproc 
    critical
    onrestart restart healthd //系统重启，就会重启onrestart的所有进程
    onrestart restart zygote
    onrestart restart audioserver
    onrestart restart media
    onrestart restart surfaceflinger
    onrestart restart inputflinger
    onrestart restart drm
    onrestart restart cameraserver
    onrestart restart keystore
    onrestart restart gatekeeperd
    onrestart restart thermalservice
    writepid /dev/cpuset/system-background/tasks
    shutdown critical
```

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
    //设置服务端的BBinder, 用于executeCommand中BR_TRANSACTION，the_context_object->transact(tr.code, buffer, &reply, tr.flags)
    IPCThreadState::self()->setTheContextObject(manager); // 用于处理服务请求
    //设置自己为binder驱动的上下文管理者
    ps->becomeContextManager(nullptr, nullptr);
    //3. 利用Looper epoll机制处理Binder事务
    sp<Looper> looper = Looper::prepare(false /*allowNonCallbacks*/);

    BinderCallback::setupTo(looper); // 设置BinderCallback，以前是死循环，现在是设置监听
    ClientCallbackCallback::setupTo(looper, manager);

    while(true) {
        looper->pollAll(-1);
    }

}
```

#### ServiceManager#setTheContextObject
初始化时，创建Binder驱动，并且设置自己为ContextObject
```c
// main.cpp frameworks/native/cmds/servicemanager/main.cpp
int main(int argc, char** argv) {
    // 创建Binder驱动后，自己设置为ContextObject
    sp<ServiceManager> manager = new ServiceManager(std::make_unique<Access>());
    if (!manager->addService("manager", manager, false /*allowIsolated*/, IServiceManager::DUMP_FLAG_PRIORITY_DEFAULT).isOk()) {
        LOG(ERROR) << "Could not self register servicemanager";
    }
    IPCThreadState::self()->setTheContextObject(manager);
}

// sp<BBinder> obj, ServiceManager最上层是BBinder
void IPCThreadState::setTheContextObject(sp<BBinder> obj)
{
    the_context_object = obj;
}
```

### SystemServer：App初始化Binder
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

### ServiceManager：层次结构
```c++
class ServiceManager : BnServiceManager;
class BnServiceManager : BnInterface<IServiceManager>;
// IInterface.h
// INTERFACE: IServiceManager
class BnInterface : public INTERFACE, public BBinder; //BBinder
class BBinder : public IBinder;
class IServiceManager : IInterface;
```


## Binder：获取系统服务

### getService源码
```java
/**======================================================
 *     整体流程:
 * 总：构造BpBinder，用反射构造BinderPorxy，构造ServiceManagerProxy，getService获取到ActivityManagerProxy
 * ======================================================*/
ActivityManager.getService()
->ServiceManager.getService(xxx)
  ->getIServiceManager().getService()
    ->ServiceManagerNative.asInterface(BinderInternal.getContextObject())
        ->jni层转到ProcessState.getContextObject()
            ->getStrongProxyForHandle(0)
                ->BpBinder::create 构造BpBinder或者handle=0的BpBinder
        ->反射调用BinderProxy的getInstance，构造，内部存放BpBinder的地址，long值
    ->new ServiceManagerProxy(obj)
  ->ServiceMap.get(String) // value = IBinder
->IActivityStubManager.Stub.asInterface(b) => IActivityManager

// 1、ActivityManager的getService()方法
    public static IActivityManager getService() {
        return IActivityManagerSingleton.get();
    }
    private static final Singleton<IActivityManager> IActivityManagerSingleton = new Singleton<IActivityManager>() {
// 2、单例调用ServiceManager的getService，可以看出ServiceManager内部有<String, IBinder>的存储形式
                protected IActivityManager create() {
                    final IBinder b = ServiceManager.getService(Context.ACTIVITY_SERVICE); // 获取AMS Binder对象并且，转为Porxy代理
                    final IActivityManager am = IActivityManager.Stub.asInterface(b);
                    return am;
                }
            };
// ServiceManager是什么？方便调用addService等API进行的封装
    private static IBinder rawGetService(String name) throws RemoteException {
// 3、获取ServiceManager的Binder对象
        final IBinder binder = getIServiceManager().getService(name);
        return binder;
    }
    private static IServiceManager getIServiceManager() {
        // 返回全局的系统的Context对象，通常实现了IServiceManager
// 4、调用BinderInternal的native方法，并且转为ServiceManagerProxy
        sServiceManager = ServiceManagerNative.asInterface(BinderInternal.getContextObject()); // 基于BpBinder封装的BinderProxy
        return sServiceManager;//ServiceManagerProxy
    }
// 5、native方法
public static final native IBinder getContextObject();
```
ServiceManagerNative.java frameworks\base\core\java\android\os\ServiceManagerNative.java
```java
// 6、将获取的底层转为IServiceManager服务接口
    public static IServiceManager asInterface(IBinder obj) {
        return new ServiceManagerProxy(obj); //ServiceManager的BinderProxy
    }
```
```c++
// android_util_Binder.cpp frameworks/base/core/jni/android_util_Binder.cpp
static jobject android_os_BinderInternal_getContextObject(JNIEnv* env, jobject clazz)
{
    // 7、获取底层服务
    sp<IBinder> b = ProcessState::self()->getContextObject(NULL); // 一个进程一个ProcessState
    // >>>>>>>>>>>>>>>>>>>>>>>>>>> 需要关注会回到这里 >>>>>>>>>>>>>>>>>
    return javaObjectForIBinder(env, b); // 基于BpBinder封装的BinderProxy
}
// ProcessState.cpp
sp<IBinder> ProcessState::getContextObject(const sp<IBinder>& /*caller*/)
{
    // 8、handle句柄0代表的就是ServiceManager，所以这里调用getStrongProxyForHandle函数的参数为0
    sp<IBinder> context = getStrongProxyForHandle(0); // 这里context是什么？BpBinder
    return context;
}
// 9、getStrongProxyForHandle: 第一次会构建BpBinder（用handle）
sp<IBinder> ProcessState::getStrongProxyForHandle(int32_t handle)
{
    sp<IBinder> result;
    AutoMutex _l(mLock);
	//查找或建立handle对应的handle_entry
    handle_entry* e = lookupHandleLocked(handle);
	//创建BpBinder并保存下来以便后面再次查找
    b = BpBinder::create(handle);
    e->binder = b; // 设置给handle_entry
    result = b;

    return result;
}
// 10、BpBinder.cpp frameworks/native/libs/binder/BpBinder.cpp
BpBinder* BpBinder::create(int32_t handle) {
    int32_t trackedUid = -1;
    return new BpBinder(handle, trackedUid); // 创建native层面BpBinder
}
```
javaObjectForIBinder:android_util_Binder.cpp frameworks\base\core\jni\andorid_util_Binder.cpp
```kotlin
// 为什么要调用？
// >>> 将BpBinder对象(native中的类型)转换成java中的类型
jobject javaObjectForIBinder(JNIEnv* env, const sp<IBinder>& val)
{
	//JavaBBinder返回true，其他类均返回flase(用于服务端)
    if (val->checkSubclass(&gBinderOffsets)) {
        jobject object = static_cast<JavaBBinder*>(val.get())->object();
        return object;
    }

// 构造BinderProxyNativeData
    BinderProxyNativeData* nativeData = new BinderProxyNativeData();
    nativeData->mOrgue = new DeathRecipientList;
    nativeData->mObject = val; // val就是BpBinder对象

// 反射构造 android/os/BinderProxy,调用其构造方法，传入BinderProxyNativeData，并且将BpBinder作为参数 ========================> 反射
    jobject object = env->CallStaticObjectMethod(gBinderProxyOffsets.mClass,
            gBinderProxyOffsets.mGetInstance, (jlong) nativeData, (jlong) val.get()); // 将Bi

    BinderProxyNativeData* actualNativeData = getBPNativeData(env, object);
	//如果object是刚刚新建出来的BinderProxy
    if (actualNativeData == nativeData) {
		 //处理proxy计数
        // Created a new Proxy
        uint32_t numProxies = gNumProxies.fetch_add(1, std::memory_order_relaxed);
        uint32_t numLastWarned = gProxiesWarned.load(std::memory_order_relaxed);
        if (numProxies >= numLastWarned + PROXY_WARN_INTERVAL) {
            // Multiple threads can get here, make sure only one of them gets to
            // update the warn counter.
            if (gProxiesWarned.compare_exchange_strong(numLastWarned,
                        numLastWarned + PROXY_WARN_INTERVAL, std::memory_order_relaxed)) {
                ALOGW("Unexpectedly many live BinderProxies: %d\n", numProxies);
            }
        }
    }

    return object;
}
```
BinderProxy.java frameworks/base/core/java/android/os/BinderPorxy.java
```java
// 把BpBinder的地址，作为long，存放到BinderProxy中。
    private static BinderProxy getInstance(long nativeData, long iBinder) {
        BinderProxy result;
        synchronized (sProxyMap) {
            result = sProxyMap.get(iBinder);
            if (result != null) {
                    return result;
            }
            result = new BinderProxy(nativeData); // 缓存没有，构造BinderProxy

            sProxyMap.set(iBinder, result); // 缓存
        }
        return result;
    }
```
IServiceManager.cpp getService()
```c++
sp<IBinder> ServiceManagerShim::getService(const String16& name) const
{
    sp<IBinder> svc = checkService(name); // 在ServiceMap <string, Service>，中找到目标Service
    if (svc != nullptr) return svc;
}
// ServiceManager.cpp
using ServiceMap = std::map<std::string, Service>;
ServiceMap mNameToService;
```

**客户端：BpBinder 服务端：BBinder**
1. 前者为了获取服务
```c++
virtual status_t    transact();
virtual status_t    linkToDeath();
virtual status_t    unlinkToDeath();
```
1. 后者提供服务

1、ServiceManagerNative是什么？
> aidl接口
> 提供asInterface，将IBinder转为ServiceManagerProxy

2、IServiceManager是什么？
```c
// 有个文件IServiceManager.aidl
// 定义了ServiceManager应该提供的服务：
1. IBinder getService(@utf8InCpp String name);
1. void addService(@utf8InCpp String name, IBinder service, boolean allowIsolated, int dumpPriority);
```

3、JNI反射思路
1. 要构造Java层对象，需要反射构造
1. 传入native层参数

#### ProcessState

1、ProcessState作用是什么？
1. 进程创建
1. Binder创建
1. 都需要这个类
1. 初始化Binder驱动，initWithDriver("/dev/binder")

#### BinderProxyOffsets

```c++
//gBinderProxyOffsets实际上是一个用来记录一些java中对应类、方法以及字段的结构体，
//用于从native层调用java层代码
//int_register_android_os_BinderProxy()函数说明了这一点
static struct binderproxy_offsets_t
{
    jclass mClass; // BinderProxy类
    jmethodID mGetInstance; // getInstance
    jmethodID mSendDeathNotice;
    // Object state.
    jfieldID mNativeData;  // Field holds native pointer to BinderProxyNativeData.
} gBinderProxyOffsets;
```


### Service：bindService，服务发布和绑定
1、bindService全流程
```java
// ContextImpl.java frameworks/base/core/java/android/app/
    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        return bindServiceCommon(service, conn, flags, xxx);
    }
    private boolean bindServiceCommon(Intent service, ServiceConnection conn, int flags, xxx) {
        int res = ActivityManager.getService().bindIsolatedService(
            mMainThread.getApplicationThread(), 
            getActivityToken(),  // IBinder mToken; 是构建ContextImpl时传入的activityToken
            service, xxx);
    }
// ActivityManager.java frameworks/base/core/java/android/app/  ====> 系统单例类Singleton
    public static IActivityManager getService() {
        return IActivityManagerSingleton.get();
    }
    private static final Singleton<IActivityManager> IActivityManagerSingleton = new Singleton<IActivityManager>() {
                @Override
                protected IActivityManager create() {
                    final IBinder b = ServiceManager.getService(Context.ACTIVITY_SERVICE); // 获取AMS Binder对象并且，转为Porxy代理
                    final IActivityManager am = IActivityManager.Stub.asInterface(b);
                    return am;
                }
            };
//=============获取到ServiceManager的Binder，再获取AMS的 Bidner=========
// ServiceManager.java framworks/base/core/java/android/os/
    public static IBinder getService(String name) {
        IBinder service = sCache.get(name);
        if (service != null) {
            return service;
        } else {
            return Binder.allowBlocking(rawGetService(name)); // Binder.allowBlocking没什么特别的点
        }
    }
    private static IBinder rawGetService(String name) throws RemoteException {
        final IBinder binder = getIServiceManager().getService(name);
        return binder;
    }
    private static IServiceManager getIServiceManager() {
        // 返回全局的系统的Context对象，通常实现了IServiceManager
        sServiceManager = ServiceManagerNative.asInterface(BinderInternal.getContextObject());
        return sServiceManager;//ServiceManagerProxy
    }
//=======================================================
// ActivityManagerService
    bindIsolatedService()
// ActiveServices
    bindServiceLocked()
    -->retriveServiceLocked() // 创建Service
    -->requestServiceBindingLocked() // bindService
      -->r.app.thread.scheduleBindService(); // 客户端的Binder对象 IApplicationThread
//====Service进程=========
//ActivityThread.java
scheduleBindService()
sendMessage(H.BIND_SERVICE, s);
handleBindService((BindServiceData)msg.obj);
    -->IBinder binder = s.onBind(data.intent);  // 拿到Servcie 的binder
    -->ActivityManager.getService().publishService(data.token, data.intent, binder);//获取服务，并且发布
//AMS
publishService(data.token, data.intent, binder);
//ActiveService frameworks/base/services/core/java/com/android/server/am/xxx
mServices.publishServiceLocked((ServiceRecord)token, intent, service);
-> ArrayMap<IBinder, ArrayList<ConnectionRecord>> connections = r.getConnections(); // 从ServiceRecord中，拿到ArrayMap ====> ArrayMap
   ArrayList<ConnectionRecord> clist = connections.valueAt(conni);
   ConnectionRecord c = clist.get(i);  // 查询到ConnectionRecord
-> c.conn.connected(r.name, service, false); // 调用c.coon.connected，到目标客户端，完成绑定
// IServiceConnection conn; // 是AIDL
    -> onServiceConnected() // 绑定回调，我们可以获取Service Binder对象
```

2、为什么客户端和服务端不直接通信，还需要借助ServiceManager？
1. 安全问题

### Binder与系统服务
 
1、Framework服务结构
1. App拿到ServiceManager的Binder，再拿到对应服务的Binder对象
1. ServiceManager是注册的第一个服务
2. Service是在SystemServer中启动后注册到ServiceManager的
3. SystemServer内部用SystemServiceManager进行管理的SystemService（双重继承）=======> 双重继承
 
2、每个binder都有handle，handle=0的就是ServiceManager的Binder
 
3、绘图
1. SystemServer[SystemServiceManager]---管理--->SystemService(AMS ATMS WMS)等
2. ServiceManager --- 存放 各个服务Binder
 
#### ActivityTaskServiceManager启动
 
1、SystemServer中ATMS启动流程
```java
传入Lifecylce的名字，再反射调用startService，到SystemServiceManager中。
startService()
->mServices.add(service) // 保存到SystemServiceManager内部集合中
->onStrat()
  ->publishBinderService() // ATMS保存到ServiceManager中
    ->ServiceManager.addService()
  ->mService.start()
```
#### Binder创建
  
2、每个Binder通信都需要通过Binder驱动
3、Client端要通过Binder和Server进行通信，用的是Server还是Client的Binder？
1. Server端的
 
4、Binder驱动（C/S都需要ioctl交互）是什么时候初始化的？
1. ServiceManager进行初始化的`/dev/binder`
2. 并且将自己作为第一个服务添加
3. 利用Linux Looper的epoll机制，进行while()循环处理Binder事物

## Binder：客户端通信流程

1、Client发起服务请求
```c
/**============================================
 * 通信流程：
 * 自定义Proxy，BinderProxy，BinderPorxyNative，BpBinder，IPCThreadState.transact, bwr(mOut\mIn), ioctl
 *=========================================================*/
MyService.xxx()->IMyAidlInterface.Stub.Proxy.xxxx()
->remote.transact()
  ->BinderPorxy.transact()
    ->transactNative()
      ->android_os_BinderPorxy_transact(code, data, reply)
        ->native层Parcel对象，data和reply
        ->BinderProxy转换为BinderPrxoyNative
        ->获取到内部BpBinder（long值还原为BpBinder）
        ->BpBinder.transact()
          ->IPCThreadState::self() // 从ThreadLocalStorage获取，没有就在Mutex保护下创建。 ===> 互斥量、ThreadLocal
          ->transact()
            ->writeTransactionData写入到mOut // 不会立即发起请求
            ->waitForResponse
              ->while()+talkWithDriver()
                ->PorcessState的mDriverFd
                ->mOut、mIn设置给bwr(binder_write_read)
                ->ioctl进行读写
```

### 通信源码

app：Service.xxx()->IMyAidlInterface.Stub.Proxy.xxxx()
->BinderPorxy.transact()
    ->transactNative

#### BinderProxy
frameworks/base/core/java/android/os/BinderPorxy.java
```java
    public boolean transact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        // AndroidRuntime.cpp # start()->startReg() 注册
        return transactNative(code, data, reply, flags);
    }
```
```c
// JNI注册相关流程 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
// framworks/base/core/jni/AndoridRuntime.cpp
REG_JNI(register_android_os_Binder) // 注册Binder
```
frameworks/base/core/jni/android_util_Binder.cpp
```c
int register_android_os_Binder(JNIEnv* env)
{
    if (int_register_android_os_Binder(env) < 0) // Binder
        return -1;
    if (int_register_android_os_BinderInternal(env) < 0)
        return -1;
    if (int_register_android_os_BinderProxy(env) < 0)
        return -1;

    return 0;
}
// BinderProxy
static int int_register_android_os_BinderProxy(JNIEnv* env)
{
    return RegisterMethodsOrDie(
        env, kBinderProxyPathName,
        gBinderProxyMethods, NELEM(gBinderProxyMethods)); // 动态注册
}
static const JNINativeMethod gBinderProxyMethods[] = {
     /* name, signature, funcPtr */
    {"getInterfaceDescriptor", "()Ljava/lang/String;", (void*)android_os_BinderProxy_getInterfaceDescriptor},
    {"transactNative",      "(ILandroid/os/Parcel;Landroid/os/Parcel;I)Z", (void*)android_os_BinderProxy_transact}, //transactNative
    {"linkToDeath",         "(Landroid/os/IBinder$DeathRecipient;I)V", (void*)android_os_BinderProxy_linkToDeath},
    {"unlinkToDeath",       "(Landroid/os/IBinder$DeathRecipient;I)Z", (void*)android_os_BinderProxy_unlinkToDeath}
};
// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< JNI注册相关流程
```
android_util_Binder.cpp
```c++
static jboolean android_os_BinderProxy_transact(JNIEnv* env, jobject obj,
        jint code, jobject dataObj, jobject replyObj, jint flags) // throws RemoteException
{
    // 1、发送数据Parcel
    Parcel* data = parcelForJavaObject(env, dataObj);
    // 2、接收数据Parcel
    Parcel* reply = parcelForJavaObject(env, replyObj);
    // 3、获取BpBinder的Native对象
    IBinder* target = getBPNativeData(env, obj)->mObject.get();
    // 4、BpBinder发起transact
    status_t err = target->transact(code, *data, reply, flags);
    //if (reply) printf("Transact from Java code to %p received: ", target); reply->print();
}
// 3、获取BpBinder的Native对象：将原先的long数据还原成BinderProxyNative对象
// 【反射】 =======================> JNI层反射，拿到mNativeData字段
BinderProxyNativeData* getBPNativeData(JNIEnv* env, jobject obj) {
    return (BinderProxyNativeData *) env->GetLongField(obj, gBinderProxyOffsets.mNativeData);
}
```
BpBinder.cpp frameworks/native/libs/binder/BpBinder.cpp
```c++
status_t BpBinder::transact(
    uint32_t code, const Parcel& data, Parcel* reply, uint32_t flags)
{
    // IPCThreadState构建Binder线程，去和服务端通信
    status_t status = IPCThreadState::self()->transact(
            mHandle, code, data, reply, flags);
}
```

#### IPCThreadState
IPCThreadState.cpp framworks/native/libs/binder/
```c++
IPCThreadState* IPCThreadState::self() // IPCThreadState是IPC线程: 每次IPC通信需要用该线程。
{
    // ThreadLocalStorage,拿到之前的IPCThreadState
    if (gHaveTLS.load(std::memory_order_acquire)) {
restart:
        const pthread_key_t k = gTLS;
        IPCThreadState* st = (IPCThreadState*)pthread_getspecific(k); // 尝试取值 ==========> ThreadLocal类似
        if (st) return st;
        return new IPCThreadState; // 没有就实例化
    }

    // Racey, heuristic test for simultaneous shutdown.
    if (gShutdown.load(std::memory_order_relaxed)) {
        ALOGW("Calling IPCThreadState::self() during shutdown is dangerous, expect a crash.\n");
        return nullptr;
    }

    // 互斥量，上锁
    pthread_mutex_lock(&gTLSMutex);
    if (!gHaveTLS.load(std::memory_order_relaxed)) {
        int key_create_value = pthread_key_create(&gTLS, threadDestructor);
        gHaveTLS.store(true, std::memory_order_release); // TLS存储
    }
    // 互斥量，解锁
    pthread_mutex_unlock(&gTLSMutex);
    goto restart;
}
```
> 发起通信
```c++
status_t IPCThreadState::transact(int32_t handle,
                                  uint32_t code, const Parcel& data,
                                  Parcel* reply, uint32_t flags)
{
    // 1、写入数据到mOut：writeTransactionData函数用于传输数据，
    //      其中第一个参数BC_TRANSACTION代表向Binder驱动发送命令协议，向Binder设备发送的命令协议都以BC_开头，而Binder驱动返回的命令协议以BR_开头
    err = writeTransactionData(BC_TRANSACTION, flags, handle, code, data, nullptr);

    // 2、等待响应：真正发起请求
    err = waitForResponse(reply);

    return err;
}
// 1、写入数据到mOut
status_t IPCThreadState::writeTransactionData(int32_t cmd, uint32_t binderFlags,
    int32_t handle, uint32_t code, const Parcel& data, status_t* statusBuffer)
{
    binder_transaction_data tr;//binder_transaction_data结构体(tr结构体）是向Binder驱动通信的数据结构

    tr.target.ptr = 0; /* Don't pass uninitialized stack data to a remote process */
    tr.target.handle = handle; //将handle传递给target的handle，用于标识目标，这里的handle的值为0，代表了ServiceManager
    tr.code = code;
    tr.flags = binderFlags;
    tr.cookie = 0;
    tr.sender_pid = 0;
    tr.sender_euid = 0;

    const status_t err = data.errorCheck();//对数据data进行错误检查，如果没有错误就将数据赋值给对应的tr结构体
    if (err == NO_ERROR) {
        tr.data_size = data.ipcDataSize();
        tr.data.ptr.buffer = data.ipcData();
        tr.offsets_size = data.ipcObjectsCount()*sizeof(binder_size_t);
        tr.data.ptr.offsets = data.ipcObjects();
    } else if (statusBuffer) {
        tr.flags |= TF_STATUS_CODE;
        *statusBuffer = err;
        tr.data_size = sizeof(status_t);
        tr.data.ptr.buffer = reinterpret_cast<uintptr_t>(statusBuffer);
        tr.offsets_size = 0;
        tr.data.ptr.offsets = 0;
    } else {
        return (mLastError = err);
    }

    mOut.writeInt32(cmd);
    mOut.write(&tr, sizeof(tr));

    return NO_ERROR;
}
// 2、等待响应：真正发起请求
status_t IPCThreadState::waitForResponse(Parcel *reply, status_t *acquireResult)
{
    uint32_t cmd;
    int32_t err;

    while (1) {
        talkWithDriver() // 循环
        // xxx
    }
}
status_t IPCThreadState::talkWithDriver(bool doReceive)
{
    // 0、mDriverFD是创建进程是，ProcessState::self()初始化的 mDriverFD = open_driver(xxx)
    if (mProcess->mDriverFD < 0) {
        return -EBADF;
    }

    // 1、bwr
    binder_write_read bwr; 

    // 2、数据来源
    bwr.write_size = outAvail; // 剩下可以获取的数据
    bwr.write_buffer = (uintptr_t)mOut.data(); // mOut中数据取出

    // 3、目标
    bwr.read_size = mIn.dataCapacity();
    bwr.read_buffer = (uintptr_t)mIn.data();

    // 4、ioctl，驱动读写
    ioctl(mProcess->mDriverFD, BINDER_WRITE_READ, &bwr)； // 将bwr数据，用命令BINDER_WRITE_READ，写入到驱动mDriverFD中
}
```

* Binder Driver底层有execTransact()方法调用



### transact流程
[参考文章链接](https://blog.csdn.net/Yellow_Matrix/article/details/103918841)
1、transact流程
```
BinderProxy.transact
BpBinder.transact => remote()->transact
IPCThreadState.transact
-write参数 =====> Parcel
-waitForResponse：等待结果才是真正发送数据
--talkWithDriver
```
2、BinderProxy：aidl文件生成的java类
3、BpProxy：Binder驱动生成的C++类
4、binder_write_read:记录了mOut和mIn信息，Binder驱动借助该信息实现数据的读写

## Binder：服务端事务处理

### 监听流程

1、设置监听
```c++
/*================================
 * 设置监听流程
 *================================*/
main()创建ServiceManager
-> 自己作为BBinder的实现者，设置到IPCThreadState中 // 会在处理事务是ServiceManager和其他的Binder服务端是分开处理的
-> Looper.prepare
-> BinderCallback::setupTo(looper) 设置Looper监听接口
    -> IPCThreadState::self()->setupPolling(&binder_fd): 获取Binder驱动的文件描述符
    -> looper->addFd: 监听Binder驱动的输入事件EVENT_INPUT
        -> epoll_ctl(EPOLL_CTL_ADD)


int main(int argc, char** argv) {

    // 初始化Binder驱动
    const char* driver = argc == 2 ? argv[1] : "/dev/binder";
    sp<ProcessState> ps = ProcessState::initWithDriver(driver);
    // 初始化ServiceManager
    sp<ServiceManager> manager = new ServiceManager();
    // 设置服务端BBinder
    IPCThreadState::self()->setTheContextObject(manager);
    ps->becomeContextManager(nullptr, nullptr);
    // Looper
    sp<Looper> looper = Looper::prepare(false /*allowNonCallbacks*/);
    // 设置callback
    BinderCallback::setupTo(looper);
    ClientCallbackCallback::setupTo(looper, manager);
    while(true) {
        looper->pollAll(-1);
    }
    return EXIT_FAILURE;
}
```
```c++
// BinderCallback::setupTo(looper);
class BinderCallback : public LooperCallback {
public:
    static sp<BinderCallback> setupTo(const sp<Looper>& looper) {
        sp<BinderCallback> cb = new BinderCallback;
        int binder_fd = -1;
        // 向Binder驱动发送BC_ENTER_LOOPER事务请求，并且获得Binder设备的文件描述符
        IPCThreadState::self()->setupPolling(&binder_fd);
        // 写缓存如果有可写数据，发送给Binder驱动
        IPCThreadState::self()->flushCommands();
        // 监听Binder的文件描述符
        int ret = looper->addFd(binder_fd,
                                Looper::POLL_CALLBACK,
                                Looper::EVENT_INPUT,
                                cb,
                                nullptr /*data*/);
        return cb;
    }

// handleEvent处理收到的Binder驱动的消息
    int handleEvent(int /* fd */, int /* events */, void* /* data */) override {
        IPCThreadState::self()->handlePolledCommands();
        return 1;  // Continue receiving callbacks.
    }
};
```
扩展：接口， =====================> 接口
```c++
class LooperCallback : public virtual RefBase {
    virtual ~LooperCallback();
    virtual int handleEvent(int fd, int events, void* data) = 0; // 定义了接口handleEvent
};
```

#### looper->addFd和epoll
```c++
int Looper::addFd(int fd, int ident, int events, const sp<LooperCallback>& callback, void* data) {

// epoll_ctl进行监听
        ssize_t requestIndex = mRequests.indexOfKey(fd);
        if (requestIndex < 0) {
            int epollResult = epoll_ctl(mEpollFd.get(), EPOLL_CTL_ADD, fd, &eventItem);
            mRequests.add(fd, request);
        } else {
            int epollResult = epoll_ctl(mEpollFd.get(), EPOLL_CTL_MOD, fd, &eventItem);
        }
}
```

### 事务处理

1、ServiceManager和一般Server端区别在于
1. ServiceManager作为framework，在native层面就结束了
1. 一般Server会有Java层的Binder


2、BnServiceManager就是类似于java aidl提供的Stub类
> BnServiceManager.cpp需要自己去生成


3、事务处理流程分析
```c++
/**==============================================
 * 事务处理流程
 *============================================*/
 binder驱动ioctl
 ->LooperCallBack(BinderCallback)
   ->IPCThreadState::self()->handlePolledCommands() // 循环处理mIn中数据，涉及Parcel序列化
     ->BBinder.transact()
       ->BnServiceManager.onTrasact()
         ->ServiceManager.xxx()服务
            ->addService()/getService() //操作ServiceMap <String,Binder>


//frameworks/native/cmds/servicemanager/main.cpp
// BinderCallback
// Binder驱动发来消息，会回调handleEvent
int handleEvent(int /* fd */, int /* events */, void* /* data */) override {
    //1、Binder驱动收到消息并且处理
    IPCThreadState::self()->handlePolledCommands();
    return 1;  // Continue receiving callbacks.
}

// === IPCThreadState ========================================
// 1、Binder驱动收到消息并且处理
 status_t IPCThreadState::handlePolledCommands()
{
    status_t result;
    // 2、读缓存中数据未消费完时，持续读取
    do {
        result = getAndExecuteCommand();
    } while (mIn.dataPosition() < mIn.dataSize());
    // 3、处理完所有任务后，处理BR_DECREFS BR_RELEASE
    processPendingDerefs();
    flushCommands();
    return result;
}
// 2、读缓存中数据未消费完时，持续读取
status_t IPCThreadState::getAndExecuteCommand()
{
    status_t result;
    int32_t cmd;

// 4、从Binder Write Read中获取数据，但mOut写缓存size=0，因此只是从mIn中读取信息
    result = talkWithDriver(); // 从mIn中获取数据
    if (result >= NO_ERROR) {
        size_t IN = mIn.dataAvail();

        // 5、读取BR响应码
        cmd = mIn.readInt32();
        // mutex相关操作

        // 6、执行命令
        result = executeCommand(cmd);

        // condition相关操作
        // mutex相关操作
    }

    return result;
}
// 6、执行命令调用，transact
status_t IPCThreadState::executeCommand(int32_t cmd)
{
    switch ((uint32_t)cmd) {
    case BR_TRANSACTION:
        {
            Parcel reply;
            status_t error;
            // 7、非ServiceManager
            if (tr.target.ptr) {
                // tr.cookie是本地BBinder对象指针
                error = reinterpret_cast<BBinder*>(tr.cookie)->transact(tr.code, buffer, &reply, tr.flags);

            } 
            // 8、ServiceManager用自己的BBinder对象，执行transact
            else {
                error = the_context_object->transact(tr.code, buffer, &reply, tr.flags);
            }
        }
        break;

    return result;
}

// === BBinder ========================================
// ServiceManager.cpp：BnServiceManager：BnInterface<IServiceMaanager> : IserviceManager & BBinder
// 本身就是BBinder对象
// frameworks/native/libs/binder/Binder.cpp
status_t BBinder::transact(
    uint32_t code, const Parcel& data, Parcel* reply, uint32_t flags)
{
    data.setDataPosition(0);

    status_t err = NO_ERROR;
    switch (code) {
        case PING_TRANSACTION:
            err = pingBinder();
            break;
        case EXTENSION_TRANSACTION:
            err = reply->writeStrongBinder(getExtension());
            break;
        case DEBUG_PID_TRANSACTION:
            err = reply->writeInt32(getDebugPid());
            break;
        default:
        // 通信
            err = onTransact(code, data, reply, flags);
            break;
    }

    // In case this is being transacted on in the same process.
    if (reply != nullptr) {
        reply->setDataPosition(0);
    }

    return err;
}
// IServiceManager.cpp 实现了BBinder的onTransact
switch(code): 选择不同服务
->addService()
->getService()
->checkService()
// ServiceManager.cpp实现
// 例如:
Status ServiceManager::getService(const std::string& name, sp<IBinder>* outBinder) {
    *outBinder = tryGetService(name, true);
    // returns ok regardless of result for legacy reasons
    return Status::ok();
}
// ServiceManager.h 维护了一个表
ServiceMap<string, Service> mNameToService;
// addService()：添加服务
Status ServiceManager::addService(const std::string& name, const sp<IBinder>& binder, bool allowIsolated, int32_t dumpPriority) {

    // 将服务添加到ServiceMap中
    mNameToService[name] = Service {
        .binder = binder,
        .allowIsolated = allowIsolated,
        .dumpPriority = dumpPriority,
        .debugPid = ctx.debugPid,
    };

    return Status::ok();
}
```


## IPCThreadState

ProcessState是每个进程有一个

每一个参与Binder通信的线程，都会有一个IPCThreadState与之关联。
定义了：
1. trasact
1. writeTransactionData
1. waitForResponse
1. talkWithDriver

### joinThreadPool


## mmap
内存读写，代替IO读写。


## 自我检测Quesiton

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

1. JNI什么时候注册的？
2. Binder驱动什么时候初始化的？
3. Zygote的Scoket服务是什么时候初始化的？如何处理Socket请求的？
4. App客户端的Binder是什么时候创建的？
5. Binder为什么要在App启动第一时间初始化？
6. Zygote进程有Binder吗？没有，职责是fork，不能多线程操作。不需要Binder。===> 死锁


1. 为什么要调用javaObjectForIBinder？
1. 获取ServiceManager的Binder对象的流程
1. 获取ActivityManagerService的Binder对象的流程
```c
/**======================================================
 *     整体流程:
 * 总：构造BpBinder，用反射构造BinderPorxy，构造ServiceManagerProxy，getService获取到ActivityManagerProxy
 * ======================================================*/
```
1. 客户端发起通信流程
```c
/**============================================
 * 通信流程：
 * 自定义Proxy，BinderProxy，BinderPorxyNative，BpBinder，IPCThreadState.transact, bwr(mOut\mIn), ioctl
 *=========================================================*/
MyService.xxx()->IMyAidlInterface.Stub.Proxy.xxxx()
->remote.transact()
  ->BinderPorxy.transact()
    ->transactNative()
      ->android_os_BinderPorxy_transact(code, data, reply)
        ->native层Parcel对象，data和reply
        ->BinderProxy转换为BinderPrxoyNative
        ->获取到内部BpBinder（long值还原为BpBinder）
        ->BpBinder.transact()
          ->IPCThreadState::self() // 从ThreadLocalStorage获取，没有就在Mutex保护下创建。 ===> 互斥量、ThreadLocal
          ->transact()
            ->writeTransactionData写入到mOut // 不会立即发起请求
            ->waitForResponse
              ->while()+talkWithDriver()
                ->PorcessState的mDriverFd
                ->mOut、mIn设置给bwr(binder_write_read)
                ->ioctl进行读写
```

1. ServiceManager初始化Binder驱动处理Binder事务流程
1. 系统服务创建Binder驱动，注册服务流程
1. 自定义服务创建Binder，注册服务流程
1. Binder可以处理的数据大小是多少？1024kb-8kb
1. 服务端注册监听Binder事件
```c
/*================================
 * 设置监听流程
 *================================*/
main()创建ServiceManager
-> 自己作为BBinder的实现者，设置到IPCThreadState中 // 会在处理事务是ServiceManager和其他的Binder服务端是分开处理的
-> Looper.prepare
-> BinderCallback::setupTo(looper) 设置Looper监听接口
    -> IPCThreadState::self()->setupPolling(&binder_fd): 获取Binder驱动的文件描述符
    -> looper->addFd: 监听Binder驱动的输入事件EVENT_INPUT
        -> epoll_ctl(EPOLL_CTL_ADD)
```
1. 服务端处理服务请求
```c
/**==============================================
 * 事务处理流程
 *============================================*/
 binder驱动ioctl
 ->LooperCallBack(BinderCallback)
   ->IPCThreadState::self()->handlePolledCommands() // 循环处理mIn中数据，涉及Parcel序列化
     ->BBinder.transact()
       ->BnServiceManager.onTrasact()
         ->ServiceManager.xxx()服务
            ->addService()/getService() //操作ServiceMap <String,Binder>
```

