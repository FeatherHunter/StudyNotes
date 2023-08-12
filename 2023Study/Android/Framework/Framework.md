# Framework
## Handler
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
#### 为什么还会卡
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
### IdleHandler
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
#### queueIdle
返回值：
true: 每次都会调用
false: 调用一次
### HandlerThread
1. 创建后
### 内存泄漏    ====> 内存泄漏
Handler的GC Root是谁？
1. Looper中有static Looper sMainLooper， 持有了Looper是永久的，强引用，一定会泄露
2. ThreadLocal中Thread内部的ThreadLocalMap，key=ThreadLocal,value=Looper持有，Entry继承自WeakReference
   1. 如果Activity结束，key = ThreadLocal = null，value=Looper
   2. 后续有set、get等操作，会导致清理key = null的Entry，value持有的就会释放。不会内存泄露
## Binder
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
### IPC
4、IPC的场景
1. App之间需要IPC
2. Framework之间需要IPC
3. App和Framework需要IPC
4. Framework和native层需要JNI+IPC
5. Native层到Linux Kernel是SystemCall，系统调用
5、多进程有哪些使用场景？
1. 突破单进程限制，分配更多资源（浏览器+播放器，给更多“CPU”执行机会和“内存”）
2. 图库（内存）
3. 稳定、风险隔离， ===> WX消息push在独立进程
4. 性能监控 ===> APM，fork子进程，dump信息
### SystemServer
#### 启动流程
1、SystemServer的启动流程
```
run()
->createSystemContext()
  ->1. 创建ActivityThread，并且activityThread.attach(true, 0)
    -> 把自己的ApplicationThread（binder对象）交给了AMS
  ->2. 创建ContextImpl并且设置
```
2、
## mmap
内存读写，代替IO读写。
## AMS
### Service
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