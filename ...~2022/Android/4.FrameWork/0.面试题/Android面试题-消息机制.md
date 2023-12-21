转载请注明链接：https://blog.csdn.net/feather_wch/article/details/81136078

总结所有Handler消息机制相关的面试题。在其他博文已经有答案的会提供[猎羽的博文链接](https://blog.csdn.net/feather_wch)进行参考。没有答案的会在文章内给出。

# Android面试题-Handler消息机制(50题)

版本号：2018/09/02-1(14:30)

---

## 注意
 * [☆] 标记的题目，是额外补充的题目，会直接给出答案。
 * 没有标记的题目，详细答案请参考：[答案参考-Handler消息机制详解](https://blog.csdn.net/feather_wch/article/details/79263855)

[TOC]

---

## Handler(17)
1. Handler是什么？
1. 消息机制是什么？
2. 为什么不能在子线程中访问UI？
3. 在子线程中创建Handler报错是为什么?
4. 如何在子线程创建Looper？
    > `Looper.prepare();`

1. 为什么通过Handler能实现线程的切换？
2. [☆] Handler.post的逻辑在哪个线程执行的，是由Looper所在线程还是Handler所在线程决定的？
    > 1. 由Looper所在线程决定的
    > 1. 最终逻辑是在Looper.loop()方法中，从MsgQueue中拿出msg，并且执行其逻辑，这是在Looper中执行的，因此有Looper所在线程决定。

1. [☆] Looper和Handler一定要处于一个线程吗？子线程中可以用MainLooper去创建Handler吗？
    > 1. 可以的。
    > 1. 子线程中`Handler handler = new Handler(Looper.getMainLooper());`，此时两者就不在一个线程中。

1. Handler的post/send()的原理
    > 1. 通过一系列sendMessageXXX()方法将msg通过消息队列的enqueueMessage()加入到队列中。

1. Handler的post方法发送的是同步消息吗？可以发送异步消息吗？
    > 1. 用户层面发送的都是同步消息
    > 1. 不能发送异步消息
    > 1. 异步消息只能由系统发送。

1. [☆]Handler的post()和postDelayed()方法的异同？
    > 1. 底层都是调用的sendMessageDelayed()
    > 1. post()传入的时间参数为0
    > 1. postDelayed()传入的时间参数是需要的时间间隔。

1. Handler的postDelayed的底层机制
2. MessageQueue.next()会因为发现了延迟消息，而进行阻塞。那么为什么后面加入的非延迟消息没有被阻塞呢？
1. Handler的dispatchMessage()分发消息的处理流程？
2. Handler为什么要有Callback的构造方法？
    > 不需要派生Handler

1. Handler构造方法中通过`Looper.myLooper();`是如何获取到当前线程的Looper的？
    > myLooper()内部使用ThreadLocal实现，因此能够获取各个线程自己的Looper
1. 主线程如何向子线程发送消息？
## MessageQueue(9)

1. MessageQueue是什么？
1. MessageQueue的主要两个操作是什么?有什么用？
1. [☆] MessageQueue中底层是采用的队列？
    > 错误！
    > 采用`单链表`的数据结构来维护消息队列，而不是采用队列

1. MessageQueue的enqueueMessage()方法的原理，如何进行线程同步的？
    > 1. 就是单链表的插入操作
    > 1. 如果消息队列被阻塞回调用`nativeWake`去唤醒。
    > 1. 用synchronized代码块去进行同步。

1. MessageQueue的next()方法内部的原理？
    > 分为三种情况进行处理。

1. next()是如何处理一般消息的？
2. next()是如何处理同步屏障的？
3. next()是如何处理延迟消息的额？
4. [☆]Looper.loop()是如何阻塞的？MessageQueue.next()是如何阻塞的？
    > 通过native方法：nativePollOnce()进行精准时间的阻塞。

## Looper(17)

1. Looper是什么？
2. [☆] 如何开启消息循环？
    > `Looper.loop();`
1. Looper的构造内部会创建消息队列。
1. 主线程ActivityThread中的Looper的创建和获取
1. Looper的两个退出方法？
1. quit和quitSafely有什么区别
2. 子线程中创建了Looper，在使用完毕后，终止消息循环的方法？
1. Looper.loop()的源码流程?
      > 1. 获取到Looper和消息队列
      > 1. for无限循环，阻塞于消息队列的next方法
      > 1. 取出消息后调用`msg.target.dispatchMessage(msg)`进行消息分发

1. [☆] Looper.loop()在什么情况下会退出？
      > 1. next方法返回的msg == null
      > 1. 线程意外终止


1. MessageQueue的next方法什么时候会返回null？
2. [☆] Looper.quit/quitSafely的本质是什么？
      > 1. 让消息队列的next()返回null，依次来退出Looper.loop()

1. Looper.loop()方法执行时，如果内部的myLooper()获取不到Looper会出现什么结果?
1. 主线程是如何准备消息循环的？
2. ActivityThread中的Handler H的作用？
3. 如何获取主线程的MainLooper
1. Android如何保证一个线程最多只能有一个Looper？如何保证只有一个MessageQueue
1. Handler消息机制中，一个looper是如何区分多个Handler的？

## ThreadLocal(7)

1. ThreadLocal是什么？
1. ThreadLocal的作用？
1. ThreadLocal的两个应用场景？
1. ThreadLocal的使用
    > 同一个ThreadLocal调用set(xxx)和get()

1. ThreadLocal的原理
    >1. `thread.threadLocals`就是当前线程thread中的`ThreadLocalMap`
    >2. ThreadLocalMap中有一个table数组，元素是Entry。根据`ThreadLocal`(需要转换获取到Hash Key)能get到对应的Enrty。
    >3. Entry中`key`为ThreadLocal, `value`就是存储的数值。

1. [☆]如何获取到当前线程
    > `Thread.currentThread()`就是当前线程。

1. [☆]如何在ThreadLocalMap中，ThreadLocal如何作为键值对中的key？
    > 通过ThreadLocal计算出Hash key，通过这个哈希值来进行存储和读取的。
