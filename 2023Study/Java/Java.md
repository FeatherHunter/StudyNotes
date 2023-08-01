
# Java基础

[本文链接，请点击这里↓](https://blog.csdn.net/feather_wch/article/details/132052566)

关键点：
String（immutable、性能、intern、StringBuilder、StringBuffer）

HashMap（散列表-哈希算法、ArrayMap、SparseMap、HashTable、HashSet）

哈希算法

\* JVM（intrinsic、String特殊优化的本地代码）

[TOC]


## String

特点、性能、优点、intern、StringBuilder、StringBuffer

1、String的不变性
1. 线程安全
2. 常量池
   1. final char[] value -> final byte[] value (拉丁语不需要这么char宽)
   2. str = new String("TODAY") "TODAY"会加入到常量池，str在堆中

2、性能
1. 增删频繁的情况下需要使用StringBuffer
2. StringBuffer
   1. 线程安全
      1. synchronized实现
   2. 适合场景
      1. Http请求参数拼接
      2. xml解析

3、优点
1. 可读性好

4、StringBuilder => 内存抖动
1. 拼接，在编译器里面会变成StringBuilder
2. 线程不安全
3. 性能高
4. 性能优化：for循环中做String的+=操作，会新建StringBuilder对象，再toString。导致内存抖动

5、intern
1. 常量池中找到直接返回，不存在就存储在常量池并且返回其引用
2. JDK随着版本变化，字符串常量池从JDK6永久代(方法区) -> JDK7堆 -> JDK8元空间(方法区的新实现)
3. 字符串排重？JDK8。相同的String只会在字符串常量池有一个
4. intern大量使用，可能导致OOM

## HashMap

HashMap（特点、容量、负载因子、扩容、散列函数）=> ArrayMap、SparseMap

1、HashMap
1. 特点：O(1)
2. 实现自Map => 和Collections无关(Queue、Set、List)
3. 容量：16
4. 负载因子：0.75
5. 扩容：2n
6. Entry：key、value、next、hash 四个字段
7. Composable：性能提升
8. 实现：get、put（key无，插入新值；key存在，新值替换旧值，返回旧值）
9. 哈希算法：>= 8 采用红黑树，核心本质是抵抗散列碰撞攻击，导致性能衰退。即使退化，性能也是Ologn <8 链表，避免树转换的开销
10. hashCode(): 将内部内存地址转为整数（地址唯一）

2、HashMap哈希函数的技巧
1. 高位和低位，异或，保证敏感性，随机性，均匀
2. 除留余数法，可以理解为低位掩码，也就是达到取模的效果。

3、HashMap-fast-fail机制
1. 内部有modCount
2. 非线程安全
3. hashNext()遍历时，检查modCount是否和期望值一样，不一样就报错

4、LinkedHashMap
1. 双向链表：保证有序性
2. 散列：高效无序
3. 方便实现LruCache和DiskLruCache

5、WeakHashMap
1. 内部集合了WeakReference和引用队列
2. 可以在连续两次GC后，检查是否还存在，存在则有内存泄漏

### 散列表

1、如何获取到高质量的散列函数
1. 不可推导
2. 敏感
3. 冲突少
4. 高效

2、Hash算法的场景
1. 安全加密 => AES、DES、SHA => 字典攻击(加盐) => 区块链（头、体）SHA256
2. 唯一标识 => MD5找图
3. 数据校验 => BT算法
4. 散列函数
5. 负载均衡 => 同一个客户的所有请求路由到同一个Server上
6. 数据分片 => (1)统计搜索关键字出现的次数 1TB 数据 => MapReduce (2)快速判断图片是否在图库中（一亿图片）
7. 分布式存储 => 数据用多设备存储 => 扩容 => 雪崩
   1. 一致性哈希函数 => 环、环偏斜、虚拟节点 => 场景：网络CPC、git commit id

### ArrayMap
key和value都是对象，稀疏数组

### SparseArray

SparseArray
1. 节约了27%内存
2. key = int， value = Object
3. 稀疏数组 => 插入，二分法。删除，标记。
4. 不是原型模式：原型模式更多的侧重于通过克隆来创建独立的对象，并且可以灵活地修改对象的属性。

#### 性能优化

场景：profile火焰图，GC 5s，MemoryAllocate定位，内存抖动

解决：byte[]很多->Glide内部的LruMap->Integer很多->SparseArray优化

## HashTable

@Depricate，性能很差，synchronized实现

## HashSet

HashSet
1. 只存储元素(对象)
2. 非线程安全
3. 底层用HashMap实现

## ConcurrentHashMap

## final关键字
1. 方法内联
2. 实现不可变类和集合
3. finally => try-with-resources
4. finalize => 性能有问题 => 守护线程Finalizer处理
5. Cleaner机制，也不推荐，无法及时执行

## 动态代理
1. 语言类型 => 动态、静态、强、弱
2. 反射 => setAccessible
3. 场景：AOP、框架
   1. Retrofit：用于创建网络请求的代理类，用来发送请求
   2. Dagger2：动态代理来生成依赖注入的代码
   3. EventBus：生成订阅者的代理类，事件发生时，通过代理类调用其方法
4. 实现：cglib和JDK，JDK在重构后不再采用反射实现，ASM实现，性能一致

## 接口
1. 设计原则：接口隔离、依赖倒置
2. 存储区域
3. 标记接口Marker Interface => Annotation
4. 函数式接口：一个方法
5. default method：接口也允许有默认实现的方法，方便扩展不修改所有实现者

## 抽象类
1. 类的访问权限修饰符
2. 特点
3. 抽象方法
4. 抽象

## 原始数据类型

优点：
1. 性能高
2. 数组，地址连续
隐式转换

## 包装类
1. 数组，地址不连续
2. 线程安全：
   1. AtomicLongFieldUpdater 保护long字段安全
   2. AtomicXXX
3. 货币：BigDicimal

### Integer
1. 自动装箱/拆箱 => 反射性能
2. Integer.valueOf int转为包装类

优点：

缺点：

缓存机制
1. IntegerCache 缓存-128~127
2. Byte
3. Character
4. Boolean
5. Short
6. Integer
String缓存机制：常量池，不变性，安全

## 引用类型
可达性：强、软、弱、虚、不可达

### 引用队列
虚引用

### 可达性栅栏
```java
        // 在可达性栅栏之前的操作对垃圾回收器可见
        Reference.reachabilityFence(sharedObject);
        // 在可达性栅栏之后的操作
```
1. 避免对象实例方法在执行完成前，对象已经被GC。某些属性还需要使用 => 没有强引用，也先不GC
2. Reference.reachabilityFence(excutor) 线程池等经常需要异步调用的，需要**可达性栅栏**

## IO
BIO
NIO
NIO2/AIO

### BIO
特点
1. 流
2. 带缓冲区IO
3. JDK 1.4后底层用NIO 重构
   
BIO方式
1. 字符流 I/O
2. 字节流 R/W-有缓冲区flush/close
3. RandomAccessFile-随机文件访问
   
File：本质是文件路径，叫FilePath更准确
BIO服务器结构：
> 1个Thread -> 1个Socket -> 1个Channel

### NIO

1、NIO组成部分
1. Channel-OS底层机制-性能优化-DMA(Direct Memory Access)
2. Buffer-NIO操作数据的基本工具
3. Selector-多路复用（一个线程处理多个连接）（单线程轮询，不适合大量耗时操作）
4. Scatter/Gather-分散/聚集，将消息拆分为消息头和消息体

2、NIO服务器结构
> keys -> Selector -> N个SockectChannel(N个客户端)

3、NIO和BIO的区别
1. NIO，多个请求顺序处理，耗时操作会阻塞。
2. BIO，适合大量耗时操作

4、NIO节省了线程切换的开销

5、DougLeo推荐多个Slector，在多个线程，并发监听Socket

#### ByteBuffer
1. HeapByteBuffer
2. flip 翻转
3. DirectByteBuffer

#### DirectByteBuffer

1、关键词
1. 堆外内存
2. Unsafe API提供
3. 不受堆大小限制，受到实际内存大小限制
4. 底层unsafe_allocatememory
5. 性能高：避免了用户空间和内核空间，data传输的消耗

2、如何创建堆外内存
> allocateDirect()
=>COW 写时拷贝技术

3、DirectBuffer优点
1. 适合长期使用
2. 适合数据量大

4、HeapBuffer优点
1. 短期使用
2. 数据量小

##### DirectBuffer的GC
* GC时机无法预测
* 一般在full gc
* 基于Cleaner机制和虚引用

## 文件拷贝
BIO => FileSystemProvider
NIO => 零拷贝技术 `srcFileChannel.transferTo(dstFileChannel)`

零拷贝技术：4次 copy 下降到 2次Copy

4次copy：
1. 磁盘A->内核
2. 内核->B用户空间
3. B用户空间->内核
4. 内核->磁盘B

2次copy：
1. 磁盘A->内核缓存
2. 内核缓存->磁盘B1

## 异常

1、Throwable-Exception-Error
2、Error-JVM错误，无法恢复
3、Exception-RuntimeException
4、throws异常声明
5、throw抛出异常
6、ClassNotFoundException(异常) => ARouter => 插件优化查找类的开销(ASM)
1. 类加载阶段，找不到Class
2. 例如：一个类被某个ClassLoader加载到内存中，另一个ClassLoader也尝试加载，会报错
```java
class.forName
classLoader.findSystemClass
classLoader.loadClass
```
7、NoClassDefFoundError（LinkageError）
1. 类的链接阶段，找不到Class（运行时，内存中找不到）
2. Android的编译环境和运行时环境不一样
   1. 插件化、使用第三方SDK、动态加载或实例化类，失败
   2. 【so中找不到，armabi、v7、v8 中缺少了so容易出现】=> NDK
   3. 手机系统版本低，class在低版本系统中不存在
   4. 分dex，dex中删除了该类（同一）
   5. 系统资源紧张，需要大量加载class，需要竞争，加载失败
   6. 【类初始化失败，静态变量顺序要保证，初始化了才能使用】静态代码块抛出ExceptionInitialError后，继续引用该变量 => 类初始化，静态代码块顺序
   7. 类依赖的class.jar不存在 => 什么情况下会出现？

# Java并发

## synchronized
1. 关键字
2. 释放锁（自动）
3. 方法和代码块
4. 公平

## Lock
1. isHeldByCurrentThread
3. intercept可中断
4. hasQueuedThread 获取等待的线程
5. tryLock 尝试获得锁/非阻塞
6. 读写锁
7. 释放锁（手动）
8. 任何地方，不可以加给方法
9. 可重入 => 文件锁
10. 公平/非公平 => CLH => AQS

## Condition
await
signal/signalAll

## CyclicBarrier
1. 所有线程都执行完成后，才继续执行
1. 基于ReentrantLock

## CountDownLatch
门栓：
1. => DAG启动框架
2. ARouter => Interceptor拦截器的处理
3. 基于AQS实现

## 多线程
1、多线程上下文切换中，上下文是指什么？切换是指什么？
1. 上下文：某一时间点CPU寄存器和PC的内容
2. 切换：线程通过【时间片轮转】算法执行任务，切换上下文 => 20000个时钟周期，约0.01ms


2、绿色线程是什么？用于JVM调度，JDK1.3后废弃

## Executor
1、Executor的优点和缺点
1. 性能：减少创建和销毁的开销
2. 解耦：将任务的提交和任务处理想分离，方便管理

2、Executor是顶级接口
3、Executors是工具类
