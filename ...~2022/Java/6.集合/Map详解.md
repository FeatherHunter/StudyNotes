转载请注明链接:https://blog.csdn.net/feather_wch/article/details/82500561

# Map详解

版本号: 2018/9/7-1(18:18)

---

[TOC]

## 问题汇总

### Hashtable和HashMap

11、Hashtable和HashMap的区别
>1. 作者：`HashMap`的作者比`Hashtable`的作者多了一个人：`Doug Lea`写了`util.concurrent`包并且著有`并发编程圣经：Concurrent Programming in Java`
>2. 诞生时间：`HashMap`产生于`JDK1.2`相比于`Hashtable`更晚。
>3. 弃用状况：`Hashtable`基本上已经被弃用：1-`Hashtable`是`线程安全`，效率比较低。2-`Hashtable`没有遵循`驼峰命名法`
>4. 父类：`HashMap`继承自`AbstractMap`,`Hashtable`继承自`Dictionary`
>5. 接口数量：`Hashtable`比`HashMap`多剔红了`两个接口`：elements和contains
>6. key和value是否为null：`Hashtable`不允许`key和value`为`NULL`，`HashMap`支持：`key=null的键只能有一个`，`get()返回为null，可能是value为null，也可能是没有该key，需要通过containKey来判断是否具有某个key`
>7. 线程安全性-`Hashtable`是`线程安全(每个方法都加入Synchronized)`，`HashMap`是`非线程安全`的。`HashMap`效率比`Hashtable`高很多，而且需要自己进行同步处理。如果需要`线程安全`可以使用`ConcurrentHashMap`，也比`Hashtable`效率高很多倍。
>8. 遍历方式-`Hashtable`使用老旧的`Enumeration`的方式，`HashMap`使用`Iterator迭代器`
>9. 初始容量和扩容方式：`Hashtable初始为11`，扩容是`2 * n + 1`，`HashMap初始为16`，扩容是`2 * n`
>10. 计算`hash值`的方式不同：`HashMap`比`Hashtable`的计算效率更高。（涉及到位运算，以及通过额外计算打散数据来减少hash冲突的问题）
>相同1： 两者都实现了：Cloneable(可复制)、Serializable(可序列化)
