# 并发

[toc]

## 问题自我检测

1、synchronized修饰普通方法和静态方法的区别是什么？
1. 底层monitorenter指令，参数需要传入对象。
2. 对象和类对象

2、什么是可见性？
1. JMM构建的目的是在并发情况下构建HB关系避免数据竞争
2. JMM是建立在原子性、有序性、可见性之上的
3. 可见性：前面的操作对后面的操作是可见的
4. 可见性由volatile和synchronized保证
5. JMM定义了有且仅有八种HB规则
```
1. 线程内程序次序规则：在控制流顺序上，书写在前的HB书写在后面的。
1. 线程间有6种：volatile之前HBvolatile之后的
1. 解锁操作，HB，之后对同一把锁的上锁操作
1. 线程start HB，线程执行的第一行代码
1. 线程最后一行代码，HB，线程销毁代码
1. 线程的中断消息，HB，对中断状态的查询
1. 对象的构造方法，HB，finalize
1. HB传递性，A HB B,B HB C，A HB C
```

3、锁的分类
4、CAS原理
5、ReentrantLock原理
6、AQS原理
7、synchronized原理
8、volatile能否保证线程安全？在DCL上的作用是什么？
9、volatile和synchronized的区别是什么？
10、什么是守护线程？
11、如何退出一个线程？
12、sleep、wait、yield的区别？wait线程如何唤醒？sleep可以中断吗？
13、线程生命周期
14、ThreadLocal是什么？
15、线程池的基本原理？
1. 减少消耗，提高响应速度，提供管理能力
2. 参数的作用，工作机制
3. 流程、线程数量如何选择？拒绝策略。

16、线程数量限制
1. 操作系统线程数量限制
2. 文件描述符数量限制
3. 句柄数量限制
4. 资源限制：内存，一个线程1MB

17、线程有哪些状态？
18、线程启动方式

## 线程
1、线程是什么？
1. 最轻量级，最基本的调度单元
2. 可以共享进程资源（内存地址、文件IO）
3. 又可以独立调度

2、Java线程实现分为三种
1. 内核线程 1:1
2. 用户线程 1:N
3. 混合模式 N:M

### 内核线程
1、内核线程是什么？1:1是什么意思？
1. KLT：kernel-level thread
2. LWP：light-Weight thread 轻量级进程/线程
3. LWP和KLT是1:1的关系

2、内核线程结构是什么？
1. P：LWP、LWP、LWP，进程有多个LWP
2. LWP和KLT：一一对应
3. KLT通过Thread Scheduler：调度器，将线程任务映射到CPU上
4. Thread Scheduler：CPU、CPU、CPU

3、内核线程实现的问题是什么？
1. 基于KLT实现，线程操作需要系统调用，涉及到用户态和内核态切换，代价高
2. 会消耗内核资源：KLT内核线程数量有限

### 用户线程 1:N 弃用
1、用户线程结构
1. CPU：P、P、P CPU资源分配到进程，内核无感知
2. P：UT、UT、UT 进程有多个用户线程

2、问题
1. 内核无感知，导致无法帮助处理阻塞
2. 无法在多CPU情况下帮助映射线程到其他CPU
3. Java弃用

### 混合模式 N:M
1、混合模式结构
1. Thread Schduler 调度多个CPU：和内核线程模式一样
1. 多个LWP和KLT一一对应：和内核线程模式一样
1. LWP和进程内多个UT，交叉对应or一一对应

2、LWP和UT共存，LWP是UT和KLT的桥梁

3、优点
1. UT操作廉价，可以大规模并发
2. 内核线程用调度器利用多CPU资源调度问题
3. 内核线程可以处理阻塞等问题

### 线程调度
1、抢占式：系统分配
2、协同式：线程工作完成后，通知系统切换

### 线程状态
1、Java定义了六种线程状态
1. new
2. running start notify、notifyall
3. block synchronized，等待获得一个排他锁
4. waiting wait、join
5. timed-waiting wait、join、sleep
6. terminated run结束

2、释放锁的情况
1. sleep不会释放锁
2. wait释放锁
3. join不会释放锁
4. yield释放线程锁，不释放对象锁

## 协程
1、协程概念
1. 协同式调度的用户线程
2. 协程会完整的进行栈的保护和恢复

2、线程和协程比较
1. 线程资源有限，调度成本高，数以百万级的请求往几十~200的线程池塞，切换损耗很大
1. 轻量，协程，几百byte~几KB，并存数量数十万。

3、协程缺点
1. 需要在应用层实现调用栈、调度器
2. kotlin 协程 synchronized会阻塞整个线程

4、oracle fiber纤程，是在JVM共存的新并发编程模型

5、Oracle fiber介绍
1. 相比于传统线程池，响应速度有50~100倍提高
2. 共用基类
3. fiber并发分为：
   1. continuation：维护执行现场，保护，切换上下文
   2. 调度器：编排代码执行顺序


## 线程安全
### 互斥同步
#### synchronized
#### Lock
### 非阻塞同步
#### CAS
Atomic类
ABA
自旋时间过长
### 无同步手段
#### ThreadLocal
### 锁优化
偏向锁
轻量级锁-LockRecord
自旋-自适应
锁消除
锁粗化

## synchronized

1、synchronized底层是什么？
1. 底层是monitorenter和monitorexit指令 ==> 体现了JVMM处理原子性时，提供了更高层面的字节码指令

2、synchronized和底层monitorenter需要一个对象参数
1. 当前对象
2. 指定对象
3. 类对象

### 管程

3、synchronized内部加锁真正的是Monitor对象
1. 操作系统中的对象

4、Monitor对象的原理
1. ObjectMonitor:
1. EntryList：阻塞队列，存储获得了Monitor对象锁的线程
2. WaitSet：等待队列，存储调用了wait()而阻塞的线程，会释放锁
3. 1-线程加锁不成功会加到等待队列中（等待队列非EntryList，是另一个数据结构）

5、为什么会有EntryList？同时获得到锁的线程不是只有一个吗？
1. 在某些情况下，多个线程可以同时获得同一个锁
2. 例如：在可重入锁的情况下，同一个线程可以多次获取同一个锁。EntryList以便释放时可以顺序正确。
3. 例如：读写锁时，多个线程持有读锁

## 原子类

1、原子类的性能，最少高一倍多

### CAS

1、原子类借助while+CAS实现 = (自旋)
2、CAS自称无锁是指真的没有锁吗？在CPU层面也是有锁的
3、CompareAndSet源码
```
->Unsafe.java
->Unsafe.cpp
->1. LOCK_IF_MP: 多核CPU返回lock指令 // 加锁，保证多CPU并行安全
->2. cmpxchg: // 原子比较和交换指令 Compare and Exchange
```

4、lock是什么？
1. 缓存行锁
2. 若超过64byte(跨缓存行)会加总线锁

5、lock cmpxchg的解析，为什么原子指令还需要lock？
1. lock前缀时，在执行cmpxchg，会对总线上的其他处理器进行锁定，以防止其他线程对同一共享变量进行并发的修改
2. cmpxchg的操作在多核情况下会有问题，需要lock

6、CAS具有的问题
1. ABA: 加版本号 ===> AtomicStampRefrence
2. 原子性: 

## 锁升级
1、锁升级的流程，以及锁各状态之间如何切换？
```
无状态-001（默认4秒）
|-线程id写入markword（启用偏向锁）
偏向锁-101
|-多个线程轻量竞争，CAS轻量竞争
轻量锁-00
|-自旋不成功，自适应自旋也不行，重度竞争
重量级锁-10 // markword指向monitor
```
```
无状态-001
|-未启用偏向锁
轻量锁-00
```
```
偏向锁-101
|-调用wait，直接进入重量级锁。重量级锁才有的状态。  ====> wait
重量级锁-10
```

2、JVM默认4秒后自动开启偏向锁
1. 4S后new的所有对象都是101，而不是无锁的001
2. 未开启时，加锁，直接到轻量级锁

3、偏向锁或者无锁进入轻量级锁的检查流程
1. 检查对象头，无锁(非重量级锁)，在栈帧中创建LockRecord，CAS将对象头的MarkWord更新为LockRecord指针
2. 成功：进入轻量级锁状态
3. 失败：检查1-MW指向自己的栈帧，代表已经拥有锁，执行（可重入）
4. 检查2-MW指向其他线程的栈帧中LockRecord，代表有竞争，用重度锁

4、锁升级流程
1. 默认无锁，4s后进入偏向锁状态
2. 偏向锁释放时，不作任何操作，方便再次进入时比较threadid
3. 拿锁时，发现有其他线程拿过锁(有竞争)，进入轻量级锁
4. 有竞争（CAS失败n次-代表起码2个线程在竞争），进入重量级锁。指向Monitor对象。

5、CAS自旋10次，或者可能自适应自旋2~3次，进入重锁

6、分代年龄等信息暂存在其他地方，会恢复。

## LongAdder
1、LongAdder用于高并发下替换Atomic类, 高并发计数器

2、LongAdder原理
1. 采用分段CAS
2. 只有一个线程：CAS实现，有base数值
3. 多个线程：cell数组
4. 1-各个线程处理自己的cell1、cell2、cell3
5. 2-最后会求和，得到最终计数（无锁，每个线程负责自己的计数，不会有冲突，求和也不会冲突）
6. 3-数组会根据实际情况增减-有扩容、缩容机制

## QUESTION

1、双重检查加锁的对象半始化问题
1. 对象创建过程中：类加载检查、分配空间、初始化零值、设置对象头、调用init、引用指向该对象(putStatic)
2. 不使用volatile的instance，会导致在init初始化和引用指向该对象重排序的情况下，拿到还未初始化的实例。
3. 不会违背as-if-serial原则和HB原则
4. volatile禁止指令重排序和保证可见性


