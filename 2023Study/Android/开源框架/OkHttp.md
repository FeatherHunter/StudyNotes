
# OkHttp原理和机制讲解

本文链接：https://blog.csdn.net/feather_wch/article/details/131767285

1、OkHttp的原理和机制包括哪些部分？

1. 设计模式的运用：建造者模式、外观模式、责任链模式
2. 整体流程
3. 分发器(调度机制)
4. TCP链接复用(复用机制)
5. 拦截器流程
6. 缓存机制

2、Http 1.1和Http2.0的区别
1. 1.1 keep-alive、串行、有序 -> 有序源于基于文本，必须有序
2. 2.0 头部压缩(更小)，ServerPush(服务器主动Push)、多路复用、并行、源于采用二进制数据帧(存储了顺序标志)

3、OkHttp请求整体流程

```
flowchart TD
    OkHttpClient --newCall 参数Request--> RealCall --execute/enqueue--> Dispatcher --> Intercepter --> Response
```

4、分发器（调度机制）
1. 同步：直接执行拦截器流程
2. 异步：提交到线程池中execute
3. 调度器内部成员：
    1. 异步请求同时存在最大数，64
    2. 异步请求同一个host同时存在的数量，5
    3. 线程池
    4. 异步等待队列
    5. 异步执行队列
    6. 同步执行队列
3. 线程池：core=0，max=65535，waitTime=60s，队列=无容量，等效于CacheThreadPool
    1. 有请求时创建线程，60s过期
    2. 最大线程数是无限 & 64 = 64
4. 执行完任务后，finish()做清理工作
    1. 同步/异步都要出队列
    2. 异步：要根据64和5进行计算
    3. promoteCalls()方法重新调度请求

5、调度机制，异步请求流程
1. 异步请求同时存在不超过64并且同一host的请求数量不超过5，则发起请求，此时在异步执行队列中
2. 不满足条件，则放入到异步等待队列中进行等待
3. 异步请求完成后，执行finish()清理工作，会对64和5这两个条件进行检查，满足条件会调用promoteCalls()重新进行调度请求

6、拦截器+责任链
1. *自定义用户拦截器：可打印日志，addInterceptor
2. 重试重定向拦截器：1.协议 2.超时 3.IO异常 4.SSL异常(SecureSocketLayer安全协议)
3. 桥接拦截器：Gzip，Cookie
4. 缓存拦截器：缓存机制
5. 连接拦截器：打开与目标的连接，RealConnection（封装了Socket和Socket连接池）
6. *自定义网络拦截器：addNetworkInterceptor
7. 请求服务拦截器:write数据 + flushRequest(真正发送请求)

## TCP连接复用机制

8、OkHttp连接池和连接复用机制
1. 连接复用机制是基于ConnectionPool类实现的。
2. ConnectionPool类维护了一个双端队列，用来存储空闲的RealConnection对象。
3. RealConnection对象代表了一个TCP连接，它可以被多个请求共享。³⁴
4. 当用户发起一个请求时，OKHTTP会先在连接池中查找是否有符合要求的空闲连接，如果有，就直接使用该连接发送请求，如果没有，就创建一个新的连接，并将其加入到连接池中。
5. 清理机制：OKHTTP使用了一个后台线程来定期清理闲置的连接。清理的条件是：
    1. 如果一个连接空闲时间超过了5分钟
    2. 或者连接池中的空闲连接数超过了5个

## 缓存机制

9、缓存机制
1. 强缓存：不发送请求，expires字段
2. 协商缓存：304表示缓存可用
3. Datat、Expires、Last-Modified、ETag

10、缓存机制详细解析
1. OkHttp 的缓存机制是按照 HTTP 的缓存机制实现的。OkHttp 具体的数据缓存逻辑封装在 **Cache** 类中，它利用 **DiskLruCache** 实现。¹³
1. 默认情况下，OkHttp 不进行缓存数据。可以在构造 **OkHttpClient** 时设置 **Cache** 对象，在其构造函数中指定缓存目录和缓存大小。¹³
1. OkHttp 提供了一批可以选择的缓存策略，通过 **CacheControl** 进行统一配置，通过构造函数我们就可以看出缓存策略。²
1. HTTP 的缓存机制分为 **强缓存** 和 **协商缓存** 。而这两种缓存的实现，均是通过 HTTP 协议的 **头部信息** 的字段来进行控制的，然后会根据过期时间来判断是否直接从本地缓存加载数据。⁴
