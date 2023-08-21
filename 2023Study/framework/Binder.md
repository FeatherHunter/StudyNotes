

# Binder


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

### fork

1、Linux中为什么不允许在多线程环境下fork？
> 会死锁

2、zygote为什么用socket方式？
1. binder底层针对每次请求都会有独立的线程去完成任务>binder线程池  ====> 线程池
2. zygote不允许有binder，会死锁，只能用socket

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


### Binder IPC概念
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
 
### 性能
6、Binder和Socket性能？
1. 经过测试两者差别很小
2. 针对Unix domain Socket有优化  ========> 腾讯音视频里面测试1MB 5MB 10MB数据
 
### 安全
7、Binder和Socket稳定性都是CS结构
8、共享内存需要并发控制，稳定性，安全性都不可以，有复杂度问题
9、Binder运行在内核有PID UID，保证安全性
10、安全性，共享内存和Socket别人可以根据协议进行伪造
 
1、Binder可以传输数据大小：1024-8K


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

## AIDL

### 结构

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

5、cpp版本AIDL
> AIDL可以用aidl.exe生成cpp版本的文件

6、AIDL的设计模式
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

#### IPCThreadState

ProcessState是每个进程有一个

IPCThreadState是IPC线程: 每次IPC通信需要用该线程。Binder线程



#### 通信源码

app：Service.xxx()->IMyAidlInterface.Stub.Proxy.xxxx()
->BinderPorxy.transact()
    ->transactNative

##### BinderPorxy
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

##### IPCThreadState
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
    ioctl(mProcess->mDriverFD, BINDER_WRITE_READ, &bwr)；
}
```



#### transact流程
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

## Binder与系统服务
 
1、Framework服务结构
1. App拿到ServiceManager的Binder，再拿到对应服务的Binder对象
1. ServiceManager是注册的第一个服务
2. Service是在SystemServer中启动后注册到ServiceManager的
3. SystemServer内部用SystemServiceManager进行管理的SystemService（双重继承）=======> 双重继承
 
2、每个binder都有handle，handle=0的就是ServiceManager的Binder
 
3、绘图
1. SystemServer[SystemServiceManager]---管理--->SystemService(AMS ATMS WMS)等
2. ServiceManager --- 存放 各个服务Binder
 
### ActivityTaskServiceManager启动
 
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
### Binder创建
  
2、每个Binder通信都需要通过Binder驱动
3、Client端要通过Binder和Server进行通信，用的是Server还是Client的Binder？
1. Server端的
 
4、Binder驱动（C/S都需要ioctl交互）是什么时候初始化的？
1. ServiceManager进行初始化的`/dev/binder`
2. 并且将自己作为第一个服务添加
3. 利用Linux Looper的epoll机制，进行while()循环处理Binder事物
 
#### App Binder
1、App Binder是在进程创建时创建的
1. fork出进程之后
2. 初始化ProcessState
3. open_driver
4. mmap

## ZygoteServer
1、ZygoteServer源码      ==========>Poll
1. poll机制，多路复用
2. 处理客户端连接请求
3. 处理数据处理请求：processOneCommand
```c
  Runnable runSelectLoop(String abiList) {
        //mZygoteSocket 是socket通信中的服务端，即zygote进程。保存到socketFDs[0]
        socketFDs.add(mZygoteSocket.getFileDescriptor());
        while (true) {
            // Step 1：Poll机制，IO多路复用。没有事件到来就阻塞
            // 1. 每次循环，都重新创建需要监听的pollFds。并且关注事件：POLLIN
            StructPollfd[] pollFDs;
            for (FileDescriptor socketFD : socketFDs) {
                pollFDs[pollIndex].events = (short) POLLIN; // 关注事件的到来
            }
            // 2. Poll机制：处理轮询状态，当pollFds有事件到来则往下执行，否则阻塞在这里
            pollReturnValue = Os.poll(pollFDs, pollTimeoutMs);
            while (--pollIndex >= 0) {
                // Step 2：处理 客户端发出连接请求
                if (pollIndex == 0) { // 意味着有客户端连接请求
                    ZygoteConnection newPeer = acceptCommandPeer(abiList);// 则创建ZygoteConnection对象,并添加到socketFDs。
                    peers.add(newPeer); //  加入到peers和socketFDs，下一次也开始监听
                    socketFDs.add(newPeer.getFileDescriptor());
                }
                // Step 3：处理 数据处理请求
                else if (pollIndex < usapPoolEventFDIndex) {
                    //pollIndex>0，则代表通过socket接收来自对端的数据，并执行相应操作
                    ZygoteConnection connection = peers.get(pollIndex);
                    ///////////////////////////重要细节///////////////////////////
                    //  进行进程的处理:创建进程
                    final Runnable command = connection.processOneCommand(this);
                    // TODO (chriswailes): Is this extra check necessary?
                    if (mIsForkChild) {
                        return command; // child
                    } else {
                        socketFDs.remove(pollIndex);//处理完则从socketFDs中移除该文件描述符
                    }
                    
                }
            }
        }
    }
```
2、ZygoteServer和select、poll、epoll
1. select <= Andorid 5.0，使用复杂，最大1024FD
2. poll >= Android 6.0，取消FD数量限制

3、为什么使用poll，不使用epoll？
1. fork进程频率远不如Handler的Looper，epoll在低并发场景下并没有特别优势
2. epoll还需要维护事件队列，没有必要

### poll
[Title](https://www.cnblogs.com/s2603898260/p/14624187.html)
[死磕epoll](https://zhuanlan.zhihu.com/p/63179839)

## bindService
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
            return Binder.allowBlocking(rawGetService(name)); // Binder.allowBlocking没什么特别的店
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

