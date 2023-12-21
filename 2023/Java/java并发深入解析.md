
转载请注明：https://blog.csdn.net/feather_wch/article/details/131198444

>本文介绍Java并发相关所有主要技术点。

---
>本文包含：
> volatile、ReentrantLock、synchronized、CountDownlatch、Semaphore、线程池、阻塞队列、Exchanger、ForkJoinPool等

[202306132346]

@[toc]
# Java并发基本概念
1. CPU耗时时间
> 1.6GHZ CPU 执行1个指令0.6ns，线程上下文切换需要20000个CPU周期 3-5ms

2. 进程是分配资源的基本单位，执行还是靠线程

3. 并行和并发是指什么？
> 并行2个CPU同时干2件事情> 并发1个CPU来回切换干2件事情

4. 时间片轮转是什么？
> 用于支持并发的算法

5. 高并发编程的意义和好处
> 提高响应速度，不浪费CPU资源

6. 线程数量的限制取决于哪几个方面？
> 1. 操作系统限制
> 2. 文件描述符/句柄数量限制
> 3. 资源限制(一个线程约1MB)

1. ThreadMXBean是什么？
> 虚拟机线程系统的管理接口：查询线程信息、dumpAllThreads

1. Thread和Runnable是什么?
> 对线程的抽象；对任务的抽象

1. 阻塞和挂起的区别？
> 1. 挂起是非阻塞的
> 1. 实际开发中并发的核心问题是不变性
> 没有共享需求的类模仿String保证不变性即可，写时copy，类为final不让继承，核心字段private
1. 线程启动有几种方式？
> 1. 表层2种Thread、Runnable 底层是1种
> 2. start0->JavaThread->OSThread->pthread_create->内核线程和OSThread建立联系->OSThread和JavaThread建立联系->调用最上层Thread的run方法

## 线程状态
1. 线程状态有哪些?
> 1. new
> 2. running,ready->在系统层面分的更细
> 3. blocked
> 4. waiting
> 5. timed-waiting
> 6. terminated2. new->running> start

3. running->ready
> yield

4. running->blocked
> synchronized

5. running->waiting/timedWaiting
> 进入：sleep/wait/join/LockSupport.park/LockSupport.parkUntil/LockSupport.parkNanos
> 退出：notify/notifyAll/LockSupport.unpark(thread)

6. ReentrantLock的lock方法会进入阻塞状态吗?
> 不会，只有syncrhonized才会进入。会进入waiting状态/挂起> 底层是LockSupport park、parkUntil、parkNanos
> 更底层是Posix线程库 pthread的互斥量和条件变量对_count变量的保护，park=0，unpark=1

## 死锁/活锁
1. 死锁是怎么发生的？
> 死锁需要满足四个条件
> 1. 互斥条件
> 2. 持有保持
> 3. 不可抢占
> 4. 循环等待

2. 如何避免死锁？
> 需要从条件的破坏其一即可
> 1. 资源不互斥
> 2. 按照顺序申请资源
> 3. 一次性申请资源
> 4. 尝试获得锁tryLock，获取不到先释放自身资源。用while循环重试。

3. 活锁是怎么产生的？
> 线程A和B一直互相拿到锁、释放锁，看似很忙碌啥都没干

## 中断和停止
1. interrupt、interrupted、isInterrupted区别？
> 1.  interrupt 用中断处理器增加中断标志位
> 2. interrupted 判断当前线程是否被中断，并将中断标志位true改为false
> 3. isInterrupted 判断当前线程是否被中断

1. 线程如何停止?
> 建议使用中断机制，而不是标志变量控制run结束

1. 为什么不能使用标志变量结束线程？
> 没办法处理sleep和wait的情况

1. JDK中线程是抢占式还是协作式？
> 协作式：通知你停止
> 不是抢占式：直接立即停止

1. 线程捕捉到InterruptedException后该怎么处理？
> 根据我们的需求处理，并且调用interrupt把中断标志位还原为false(可以不)

## 守护线程
1. 守护线程是什么？
> 1. 用户进程启动时，new Thread()启动的线程都是非守护线程，JDK内部启动的线程都是守护线程     
>    主线程运行结束后，守护线程都会结束。
> 2. thread.setDaemon(true)设置守护线程，如果特殊设置，都是非守护线程
> 3. 守护线程finally是否执行看运气，非守护线程finally一定执行
> * 场景：Netty需要线程来管理自己，就可以设置守护线程

# 锁
1. 不能给Integer等基本类型包装类加synchronized
> 1. 自身++后会缓存新的数值成为新对象
> 2. i++ == Ineteger.valueOf(i + 1) == new Integer() 导致加锁失败
> 3. 解决办法：用额外Object obj去加锁

1. 线程的协作模板
```java
// 等待模板sync(对象){
      while(条件不满足){
               对象.wait();    
      }
}
// 通知模板sync(对象){
      业务
      对象.notify/notifyAll();
}
```

1. notify和notifyAll会释放锁吗？
> 1. 不会释放锁，需要放到同步代码块最后一行

1. yield()对锁有什么影响？有什么用？
> 1. 放弃当前的CPU资源，但可能立马又被分配时间片
> 2. 不会释放锁，细化状态变化：线程从 running(运行状态)->ready(就绪)
> 3. ConcurrentHashMap, 多个线程第一次用的时候只有一个线程可以初始化，其他线程就yield让出时间片

1. 等待超时和连接池
> 1. 连接池的实现```sync(pool){   if(mills < 0) pool为空，wait等待。等待到连接后，removeFirst()   else while()中不断计算出remainTime，然后wait(remainTime) //Handler的实现思想}```

## ThreadLocal
1. ThreadLocal是怎么实现的？
> 1. 各个线程内部有独立ThreadLocalMap里面以ThreadLocal为key，变量值为value进行存储
> 2. 内部Entry[] table 大小为16
> 3. 出现hash冲突怎么解决？再哈希

2. ThreadLocal导致内存泄漏的原因？
> 1. key=threadlocal是weakRefrence不会导致泄露，可以回收
> 2. value=object，强引用，Thread->Map-><key,value> 线程不死value一直存在
> 3. 线程池中的线程会一直存在

3. 怎么解决ThreadLocal内存泄露？
> 1. run方法结束时，调用ThreadLocal的remove方法，清除掉key=null的value
> 2. ThreadLocal的get、set方法的调用会进行一部分清除，但是不及时。

4. 为什么ThreadLocal的key使用WeakReference？
> 1. 不适用弱引用，内存泄漏是必然发生的。
> 1. 一定会持有ThreadLocal，导致set、get时没有办法通过key=null对value进行一定概率的回收。     
> 一个线程存放的变量5M，循环后直接浪费了100MB空间。

5. ThreadLocal一定是线程安全的吗？
> 一定不能使用static、共享的value

6. ThreadLocal和Handler中的内存泄漏有什么关联？
>  1、ThreadLocal采用key为虚引用，避免了一定程度上的内存泄漏。但是有缺陷。
>       子线程创建Handler但是子线程没有结束，Handler持有Context一定是泄露的
>  2、
## 并发工具类
### Fork/Join
1. Fork/Join是什么？
> 1. 利用分而治之思想，屏蔽了Thread和Runnable只是
> 2. 工作密取: 线程池中线程1 2，线程1任务执行完了很闲，去拿线程2任务队列中任务来处理，处理完放回去
> 3. 为什么我们自己分配的任务会不平均？1+...+100和100亿-100+..._100亿，强度完全不一样

2. ForkJoin的使用
```
ForkJoinPool pool = new ForkJoinPool();        
// 支持泛型        
RecursiveTask<Integer> task = new RecursiveTask<Integer>() {            @Override            protected Integer compute() {                if(get() <= 0 ){                    return 0;                }else{                    RecursiveTask<Integer> left;                    RecursiveTask<Integer> right;                    return left.join() + right.join();                }            }        };        pool.invoke(task); // 同步        pool.submit(task); // 异步        pool.execute(task);// 异步        Integer result = task.join(); // 获得结果        // 没有返回值        RecursiveAction action = new RecursiveAction() {            @Override            protected void compute() {                // 计算处理            }        };
```

### Semaphore
1. Semaphore是什么？注意点
> 1. 许可证，实现流量控制
> 2. 如果不acquire直接release会导致许可证突破上限，容量会越来越大
> 3. 基于AQS实现

### Exchanger
1. Exchanger是什么？
> 1. 两个线程之间交换数据和协作
> 2. 线程A exchanger 线程B，会阻塞等待线程B，线程B数据准备好后。进行数据交换## FutureTask

1. 需要线程执行任务后有返回值怎么办?
> 1. Callable的call()有返回值
> 2. FutureTask<V> implements RunnableFuture<V> 实现了Future和Runnable接口>  构造时传入Callable，run时调用Callable的call获取返回值并且存储返回值。
> 3. task.get();获取到返回值
> 4. task.cancel(); 终止任务

# 锁深入太久

## CAS机制
1. 有CAS为什么还需要Synchronized?
> 1. CAS只可以保证一个变量的原子性，加锁可以保证多个变量
> 2. AtomicReference可以解决多个变量的原子性问题，将其组合为一个对象## volatile

1. volatile适合什么场景？
> 1. 一写多读

## synchronized

## ReentrantLock

# 线程池
1. 为什么需要用线程池？
> 1. 减少资源消耗
> 2. 提高响应速度
> 3. 集中管理

2. 拒绝策略/缺省策略有哪几种？
> 4种
> 抛出异常、抛弃最早执行的任务、抛弃当前任务、谁提交任务谁去做

3. 提交任务有哪几种方法？
> 1. execute(runnable): 不关心返回结果
> 2. submit(runnable,callable): 关心返回结果，Future可以取消线程

4. shutdown shutdownNow的作用> 取消剩下任务/取消所有任务，中断当前任务。

5. 如何合理配置线程数？
> 1. CPU密集型(计算)：CPU核心线程数 + 1，Runtime.getRuntime().availableProcessors()>    + 1 解决【页缺失】情况，更多线程数 速度会变慢，徒增上下文切换
> 2. IO密集型：CPU核心线程数 x 2，IO速度低于CPU（研究出的结果）
> 3. 混合型：1）两种任务时间相差不大，拆分为两个线程池 2）相差很大，哪个更耗时就认为是哪种任务

6. 核心线程数如何判断
> 根据业务需求

7. 页缺失是什么？
> 线程使用的数据可能部分在内存中、部分在虚拟内存中。线程进入页缺失状态。
> 需要等待数据调度到内存中，再处理

1. OkHttp线程池中线程数
> 最大为Integer.MAX但是内部有条件判断是否是64

## 阻塞队列

# 自我检查
1. notify会释放锁吗?
2. 实现一个生产者/消费者模型(wait+notify)
