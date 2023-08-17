
# Binder

## Binder IPC概念
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
 
## 性能
6、Binder和Socket性能？
1. 经过测试两者差别很小
2. 针对Unix domain Socket有优化  ========> 腾讯音视频里面测试1MB 5MB 10MB数据
 
## 安全
7、Binder和Socket稳定性都是CS结构
8、共享内存需要并发控制，稳定性，安全性都不可以，有复杂度问题
9、Binder运行在内核有PID UID，保证安全性
10、安全性，共享内存和Socket别人可以根据协议进行伪造
 
1、Binder可以传输数据大小：1024-8K
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

## AIDL

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
## ServiceManager#setTheContextObject
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
## Binder整体结构

Binder客户端：
BinderProxy
andorid_util_binder.cpp AndroidRuntime
BpBinder

Binder服务端：
Binder
andorid_util_binder.cpp AndroidRuntime
JavaBinder
BBinder

## 自我检测Quesiton

1. JNI什么时候注册的？
2. Binder驱动什么时候初始化的？
3. Zygote的Scoket服务是什么时候初始化的？如何处理Socket请求的？
4. App客户端的Binder是什么时候创建的？
5. Binder为什么要在App启动第一时间初始化？
6. Zygote进程有Binder吗？没有，职责是fork，不能多线程操作。不需要Binder。===> 死锁

