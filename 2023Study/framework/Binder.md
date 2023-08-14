
## Binder IPC概念-7
1、系统服务和应用分不同进程的好处？
1. 安全
2. 稳定
3. 内存回收和分配
////
2、客户端app几个进程？ =======> fork
1. push
2. 浏览相册图片
3. webview
4. dump时fork
////
3、多进程好处
1. 内存突破：一个进程522MB ========>为什么
////
4、IPC方式中共享内存使用场景
1. WM和SFlinger有大块内存
2. WX图片传递 用 共享内存
////
5、共享内存是如何实现的？
1. 两个进程都有共享虚拟地址，映射到物理内存上同一块共享内存
////
## 性能
6、Binder和Socket性能？
1. 经过测试两者差别很小
2. 针对Unix domain Socket有优化  ========> 腾讯音视频里面测试1MB 5MB 10MB数据
////
## 安全
7、Binder和Socket稳定性都是CS结构
8、共享内存需要并发控制，稳定性，安全性都不可以，有复杂度问题
9、Binder运行在内核有PID UID，保证安全性
10、安全性，共享内存和Socket别人可以根据协议进行伪造
////
1、Binder可以传输数据大小：1024-8K
## Binder与系统服务
////
1、Framework服务结构
1. App拿到ServiceManager的Binder，再拿到对应服务的Binder对象
1. ServiceManager是注册的第一个服务
2. Service是在SystemServer中启动后注册到ServiceManager的
3. SystemServer内部用SystemServiceManager进行管理的SystemService（双重继承）=======> 双重继承
////
2、每个binder都有handle，handle=0的就是ServiceManager的Binder
////
3、绘图
1. SystemServer[SystemServiceManager]---管理--->SystemService(AMS ATMS WMS)等
2. ServiceManager --- 存放 各个服务Binder
////
### ActivityTaskServiceManager启动
////
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
//// 
2、每个Binder通信都需要通过Binder驱动
3、Client端要通过Binder和Server进行通信，用的是Server还是Client的Binder？
1. Server端的
////
4、Binder驱动（C/S都需要ioctl交互）是什么时候初始化的？
1. ServiceManager进行初始化的`/dev/binder`
2. 并且将自己作为第一个服务添加
3. 利用Linux Looper的epoll机制，进行while()循环处理Binder事物
////
#### App Binder
1、App Binder是在进程创建时创建的
1. fork出进程之后
2. 初始化ProcessState
3. open_driver
4. mmap
