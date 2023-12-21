
# Java基础

[本文链接，请点击这里↓](https://blog.csdn.net/feather_wch/article/details/132052566)

关键点：
String（immutable、性能、intern、StringBuilder、StringBuffer）

HashMap（散列表-哈希算法、ArrayMap、SparseMap、HashTable、HashSet）

哈希算法

\* JVM（intrinsic、String特殊优化的本地代码）

[TOC]

## OOP ==> Profile
1、面向对象思想在实战中的体现是什么？
1. 每个页面都需要的功能，抽取为抽象类
2. 不是每个页面都需要的功能，提取为接口

2、抽象类和接口的选用
1. 能用抽象类就不要用接口
2. 接口是对抽象类的补充
3. 抽象类：对类属性和行为的抽象
4. 接口：对类行为的抽象

3、依赖倒置原则是什么？
1. 上层和下层，都依赖于抽象接口
2. 面向接口编程（这里接口指的是抽象）

4、抽象类：具有属性、方法、抽象方法

5、接口中已经有了默认方法，为什么还要用抽象类？
> 接口中变量是public static final的，不符合一些场景

## 序列化
1、什么是序列化？
1. 序列化就是一个流程，不是简单的接口
2. 数据转为二进制数据(序列化) -> 传输 -> 反序列化
3. 最重要的就是打标记，帮助反序列化(Serializable)
4. 序列化方案有很多：JSON、xml、protobuf

### Serializable
2、Serializable
1. 打上标记，数据才可以写入流，不然报错
2. ObjectOutputStream可以writeObject，通过反射实现，性能不好
3. 适合IO：网络、硬盘
4. 性能：性能差，会创建大量临时对象，导致频繁GC =====> GC

3、Serializable为什么性能不好？
1. 底层用反射实现 ====> 反射性能优化 ====> getMethods ===> Lifecycle对反射Map缓存

### Parcelable
1、Parcelable的特点
1. 没办法持久化，不保存类的信息，没办法从IO设备上反序列化出来
2. 内存层面的序列化，适合IO以外一切场景
3. 实现原理：int写入是4个byte，直接传输，按照顺序取出。反序列化需要调用目标类方法。
4. 举例：存的是时候是4,8,8.读取的时候是4,8,8 =====> JNI/NDK ===> 共享内存

### Kotlinx Serialization
1、Kotlinx Serialization是什么？
1. 支持多种格式，包括 JSON 和二进制
2. 更高效：根据 Kotlin 类型信息进行编解码，避免了使用反射
3. 性能对比：对于复杂的数据结构和大型对象，性能更好

2、Kotlinx Serialization的使用
```kotlin
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
@Serializable
data class Person(val name: String, val age: Int)
fun main() {
    val person = Person("John", 30)
    // 1、序列化
    val json = Json.encodeToString(Person.serializer(), person)
    println(json) // Output: {"name":"John","age":30}
    // 2、反序列化
    val restoredPerson = Json.decodeFromString(Person.serializer(), json)
    println(restoredPerson) // Output: Person(name=John, age=30)
}
```
3、Kotlinx Serialization是如何提升性能的？底层是怎么实现的？
1. 在编译时生成序列化和反序列化的代码，避免了运行时的反射操作


## 对象创建
分配内存
地址空间初始化
设置对象头
初始 => 场景<init>和父类<init>顺序，子类对象的成员变量初始化在父类<init>后执行，父类init中调用了某方法，子类实现该方法并且使用了成员变量，会导致使用在初始化之前。 => 实战空指针
引用入栈，指向该对象

## 值传递
Java方法调用无论参数是基本数据类型还是引用，都是值传递。
```java
Person a = new Person();
change(a);
public void change(Person p){
    p = new Person(); // 不会改变a的内存地址
}
```



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

6、扩容规则，超过负载因子，且发生哈希冲突。
7、转为红黑树条件：达到8个节点，且数组大小>64，<=64会扩容。

### HashMap存储空间效率问题  =============> 内存优化

1、`HashMap<Long, Long>`存储数据文件空间效率太低
1. 加载上百MB数据到内存中分析时，会很快占满Eden空间，从而引发Minor GC。但GC后大部分对象仍存储，会导致复制到Survivor区。
1. 标记-复制会影响性能（新生代算法，是标记复制，思想是存活对象少）

2、`HashMap<Long, Long>`有效数据分析
1. 结论：有效数据占比 18%
1. 有效数据：long 8byte，value 8byte = 16byte
1. 总共内存空间：long转为对象 = 对象头（8byte Markword + 8byte class指针）+ 实际long值 8byte => 24byte.
1. 2个Long组成Map.Entry = 24 * 2 = 48 byte
1. Map.Entry其他部分：16byte 对象头 + 实例数据（8byte next字段 + 4byte int类型hashcode + 24*2 ） + 4byte 对齐填充 + 8byte HashMap对Entry的引用 共88byte。
1. 实际占比：18%


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

#### 一致性哈希算法
1、是什么？
1. hash上引入环形
2. 虚拟节点
3. 核心：对2^32取模
2、解决：分布式hash表中动态伸缩的问题
1. 所有缓存都失效->雪崩，都去后端请求
2. 优化后：服务器增加减少（少部分缓存失效）
3、虚拟节点是为了解决什么问题？
1. 环倾斜/环偏移

#### 布隆过滤器
1、是什么？
1. 概率数据结构
2. 只判断key是否存在，不存储具体数据，占用空间小
3. 有误差
4. 不支持删除
2、场景：巨大文件、巨大数据库、缓存系统、爬虫，寻找key是否存在
3、适合条件：（1）判定一定不在集合中 （2）判定可能在集合中
4、元素
```
K：hash函数个数
m：布隆过滤器长度
n：插入元素个数
p：误报率
```
5、复杂度：
>插入、查询O(k)
>空间O(m)
6、如何选择k和m？
```
m=-(n*lnp)/(ln2)^2
k=m/n*ln2
```
==>Google Guava提供BloomFilter



### ConcurrentHashMap
1.7：segment数据结构+HashEntry数组组成，分段锁，ReentrantLock
1.8：粒度变大，不再分段，数据结构简单，但是实现更复杂。基于synchronized

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
1、特点：
1. 语言类型 => 动态、静态、强、弱
2. 反射 => setAccessible
3. 场景：AOP、框架
   1. Retrofit：用于创建网络请求的代理类，用来发送请求
   2. Dagger2：动态代理来生成依赖注入的代码
   3. EventBus：生成订阅者的代理类，事件发生时，通过代理类调用其方法
4. 实现：cglib和JDK，JDK在重构后不再采用反射实现，ASM实现，性能一致

2、使用：Proxy.newInstance
3、代理模式是什么？优点和缺点
4、动态代理是什么？
5、动态代理场景
> Hook


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

Java用基本数据类型的好处：
1. 占用内存小
2. 可以借助CPU缓存机制
3. 数组，可以直接取值，地址连续

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

关键点：
1. 实现：在同步代码块前后生成字节码指令monitorEnter和monitorExit(该指令需要引用reference类型参数，用于lock和unlock)
2. 指明对象：对象加锁
3. 不指明对象：实例方法（实例对象）、类方法（类对象）
4. 可重入锁 => 避免死锁
5. 无法中断等待（因synchronized等待，其他线程执行了interupt也不能中断）、无法超时退出、无法强制有锁线程释放锁
5. 重量级操作（锁升级到重量级锁后）
   1. 阻塞和唤醒由操作系统完成，涉及用户态和内核态 切换
   2. 简单方法，会出现切换消耗比代码执行还多
6. 锁的升级降级：不支持

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

1. 可重入、可中断、非阻塞、公平锁
2. ReentrantLock可以绑定多个对象 ==> Condition配合使用
3. 要确保finally中释放锁 ==> try-with-resources LockHelper()自动释放，不需要手动释放
4. 性能：优化后性能不是考虑因素

### ReentrantLock实现原理
1. 基于AQS = LockSupport + CAS
2. AQS作用：竞争锁，等待锁基于AQS
3. 公平、非公平：AQS
4. 重入：AQS中state，并且isHeldByCurrentThread判断谁独占

1、自己如何实现ReentrantLock
1. 实现AQS
2. xxx ==> 忘了，后面敲代码，试下

2、CLH思想、AQS思想，实现公平锁和非公平锁
1. 阻塞的线程LockSupoort.park()
2. 运行完的线程，发现

3、公平锁加锁：tryAcquire()实现公平锁和非公平锁 ==> 源码再看一遍
1. state = 0，没有线程获得锁：1. CAS操作成功 2.setExclusiveThread()自己独占锁
2. state > 0，发现是自己占有锁，state++
3. CAS失败，发现有队列。
   1. 用while-CAS，加入到队列尾部。并且将前一个节点waitStatus设置为-1
   2. LockSupport.park() 休眠

4、公平锁解锁
1. 线程执行完后，检查自己的状态
2. 0：无需要unpark的线程
3. -1：需要唤醒下一个节点

5、非公平锁加锁
1. 先CAS竞争，竞争失败了再加入队列
2. 队列中线程会按顺序唤醒，可能会饿死

6、非公平锁解锁
1. 唤醒队列的下一个线程：伪非公平

7、ReentrantReadWriteLock => 锁降级(写将为读)

## AQS
AQS是CLH的变体：虚拟双向队列FIFO

1、AQS唤醒为什么从后往前找？
1. 新节点，pre节点指向之前的tail
2. 之前的tail的next = null
3. tail = 新节点
4. 唤醒从前往后，极大可能，会丢失某节点。锁饥饿
5. 节点插入、取消也都是先处理pre，再处理next。因此从后往前更好


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

## JUC架构
操作系统：Mutex、Condition
基础工具：synchronized、CAS、LockSupport
AQS: CAS(资源竞争) + LockSupport(阻塞) + 条件队列（虚拟双向队列，CLH变体）
ReentrantLock：AQS
BlockingQueue：ReentrantLock
ThreadPoolExecutor：BlockingQueue + ReentrantLock + CAS
CopyOnWriteArrayList：
ConcurrentHashMap：synchronized + xxx
CountDownLatch: AQS
CyclicBarrier: AQS
AtomicInteger、AtomicRefrence、LongAddr

### LongAddr
分段CAS


## 线程
1、多线程上下文切换中，上下文是指什么？切换是指什么？
1. 上下文：某一时间点CPU寄存器和PC的内容
2. 切换：线程通过【时间片轮转】算法执行任务，切换上下文 => 20000个时钟周期，约0.01ms


2、绿色线程是什么？用于JVM调度，JDK1.3后废弃


3、Thread start做了什么？
> 一言以蔽之，JVM层面JavaThread->OS层面的OSThread->pthread->JavaCalls->Thread.run
```c++
start0() // native
->thread.c#JVM_startThread()
  ->jvm.c#JavaThread(&thread_entry, xxx) // JVM层面的Thread对象，传入创建后需要执行的方法
    thread.cpp#
      ->属性保存
      ->os::create_thread ===> JVM跨平台核心，看JVM在OS目录下，有windows、linux等目录
     os_linux.cpp#
        ->创建OSThread对象
        ->(JavaThread)thread->set_osThread(osThread) 建立联系
        ->pthread_create               // ========> mmkv
        ->父线程while()等待子线程初始化完成
            //子线程
             -> 将创建的内核线程 和 OSThread(父线程) 关联
             -> 初始化操作
             -> while()中wait等待父线程 // wait  ======> mmkv、Linux
        ->帮助子线程prepare，
          ->将JVM的JavaThread和上层线程对象（我们的）互相关联
          ->设置优先级
        ->父线程OSThread执行start() // 将状态改为Runnable  
          ->将状态改为Runnable  
          ->notify()子线程  // notify =====>linux 、mmkv
            // 子线程
            thread.cpp#
             ->JavaThread::run()
             ->取出属性的方法并且执行
             ->JavaCalls() // 访问Java方法的大门
               ->执行到Thread.run()
```


### 死锁
1、什么是死锁？
> 一组线程竞争资源，并且相互等待，导致永久阻塞的情况 => JVMTI => 有向无环图 => 深度遍历

2、死锁的原因
1. 互斥条件：共享资源xy只能一个线程占有
2. 占有且等待：占有资源，且等待时不会释放
3. 不可抢占：不能强行获取线程的资源
4. 循环等待：t1 t2 互相等待占有的 x和y

3、解决方案
1. 等待资源时，释放自己的资源
2. 一次性请求所有资源
3. 按照顺序申请

4、解决死锁相关算法
=>有效资源分配算法
=>银行家算法


### 两次start
1、只能调用一次，调用两次会出现异常illegalThreadStateException
2、Java线程的六种状态
1. new
2. running
3. blocked
4. waiting
5. time-waiting
6. terminated

3、posix线程库，线程有11种状态，从-1~9
=> KOOM dump => fork => suspendAllThread => suspend状态
4、无论是安全角度还是底层逻辑都不应该start两次
=> Java Thread 和 Native Thread 源码剖析

### 安全
#### 互斥同步
1. 互斥是手段，通不是目的
2. 互斥是实现方法，同步是并发时共享数据只能被一个线程使用
3. 方法：
   1. 临界区
   2. 互斥量Mutex => LockSupport  => mmkv多线程安全
   3. 信号量Semaphore

#### 非阻塞同步
1、核心思想：先处理，有冲突再补偿
2、实现基础：依靠硬件指令集发展，保证多个操作的行为可以在一个CPU指令完成
3、相关指令
1. 比较并交换CAS
2. 加载链接LL/条件存储SC == CAS


4、CAS特点
1. 适合写少读多，吞吐量高 => AtomicInteger(while(CAS))实现
2. JDK 1.6后自适应

5、CAS = V A B
1. 内存地址，旧值，新值
2. xxx

6、加载链接LL/条件存储SC == CAS
1. 一对原子指令，用于实现乐观并发控制
2. 加载链接（Load-Link）指令用于将指定内存位置的值加载到寄存器中，并在加载过程中创建一个链接标记（Link）。
3. 条件存储（Store-Conditional）指令用于将寄存器中的值存储回指定内存位置，但仅当加载链接指令之后，没有其他线程对该内存位置进行修改的情况下才会成功存储，即链接标记没有被破坏。
4. 用于实现无锁数据结构和并发算法，如无锁队列、无锁哈希表等。

7、问题
1. ABA：加版本号，但没实际意义。
2. 自旋时间过长：自适应自旋转 or 锁升级 =>JVM

#### 无同步方案
1、ThreadLocal
1. 每个线程都有ThreadLocalMap对象，以key=ThreadLocal(会算出哈希值)，value=变量，存储
2. 获取当前的ThreadLocalMap后进行存储、读写，获得线程独占变量的效果

2、ThreadLocalMap的Entry继承自WeakReference<ThreadLocal<?>>

3、ThredLocalMap中为什么ThreadLocal使用弱引用？
1. 外部使用ThreadLocal已经释放了强引用
2. 但是Thread的ThreadLocalMap中，ThreadLocal还是强引用，必然导致内存泄漏

4、ThreadLocal中value是强引用会存在内存泄漏
1. Entry数组中会存储，key=null，value=强引用的Entry。

5、ThreadLocal的清理机制
1. get、put、remove，会对key=null的Entry进行清理
2. 但是这种清理不及时（如果一直不调用）

6、线程池结合ThreadLocal容易出现内存泄漏
==> 哈希冲突，开放寻址法，线性探测（+1）



## 线程池
1、线程组ThreadGroup：构成树形结构，方便管理（如统一中断）
> 没有指明线程组，就都是main线程组

2、重要元素
1. corePoolSize 核心线程数
2. maximumPoolSize 最大线程数
3. keepAliveTime：
4. 队列
5. 工厂
6. 拒绝策略

3、为什么一定是阻塞队列？
1. 让核心线程在取任务处，阻塞等待。（空闲时）

4、核心线程，在没任务时干什么？
1. 保活：任务执行完后，while循环会去取阻塞队列的下一个任务，无任务阻塞
2. 回收：超过核心线程数的线程执行完任务后，回收
3. 实现：当线程数 > core, 队列中取任务会用workQueue.poll(keepAliveTime, Unit)

5、线程池调度线程执行的例子
1. 11个线程都空闲，要取任务，core = 10， 因为 11 > 10，都会超时等待poll
2. 超时后，11个线程都会退出while()，调用processWorkerExit() // 没有该方法会导致11个线程都停止
3. 会和核心线程数比较，多的return（消失），核心的调用addWorker()，会换一个新的Thread对象执行

6、CTL什么意思？Control，控制

### Executor
1、Executor的优点和缺点
1. 性能：减少创建和销毁的开销
2. 解耦：将任务的提交和任务处理想分离，方便管理

2、Executor是顶级接口
3、Executors是工具类

### 五种状态
1、线程池的五种状态
1. Running （new出来就是）
2. ShutDown（shutdown）剩余任务还会执行，
3. Stop（shutdownNow）剩余的也不执行
4. Tidying 清理中
5. Terminated terminated()

2、线程池如何回收阻塞中的线程池？
1. 中断
2. 中断后，还会getTask-判断状态去return，不拿任务就stop，还拿任务就shutdown

3、onShutDown()和terminated()空方法给子类去实现

4、中断只是信号，不一定要停止。
1. Thread.interrupted()返回值决定要做什么 // 会恢复标志位


# JVM

## 锁优化

### 锁粗化
零碎操作反复加锁、解锁，将锁的范围扩展至整个操作之外，如循环体

### 锁消除
JIT将不存在数据竞争的锁去除 ==> 逃逸分析（不逃逸出线程）

### 轻量级锁
1、性能
1. 无锁竞争情况下，性能>重量级锁
2. 锁激烈竞争，多了CAS操作，性能<重量级锁

2、加锁流程
1. 进入同步代码块，检查对象头
2. 未获得锁：在栈帧中创建LockRecord。用CAS将Markword字段更新为指向LockRecord
3. 成功：无竞争，进入轻量级锁状态，执行代码
4. 失败：
   1. Markword指向了当前线程的栈帧中LockRecord，继续执行（可重入特性）
   2. MarkWord指向其他，代表有竞争，进入【重量级锁】
3、解锁流程
1. CAS操作MarkWord，失败代表有其他线程在竞争锁
2. 释放并唤醒其他挂起的线程

#### 自选锁
1、自选锁和自适应自旋
2、自适应自旋的时间要怎么选择？ ==> 自旋失败一次，且不是自己获得锁，升级
1.  上一次同一个锁的自旋转时间和调用者状态决定

### 偏向锁
1. 无竞争时，整个同步都消除，锁对象第一次获得锁的时候，进入偏向模式
2. 有其他线程请求锁，立马退出偏向模式
3. 偏向模式：1 锁状态： ===> 多少？


