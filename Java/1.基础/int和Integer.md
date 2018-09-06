转载请注明链接:https://blog.csdn.net/feather_wch/article/details/50402042

# int和Integer

版本：2018/9/6-1(20:00)

---

[TOC]

## 问题汇总

1. int和Integer有什么区别？


## int

1、int和Integer有什么区别？
> 1. int是原始数据类型
> 1. Integer是引用类型
> 1. int不是对象
> 1. Integer是对象，内部有一个int字段来存储数据

2、Java的原始数据类型
> 1. 原始数据类型(Primitive Types)一共有8种。
> 1. Java语言中一切都是对象，但是`原始数据类型`除外
> 1. boolean、byte、char、short、int、long、float、double

3、为什么需要原始数据类型？Java的对象不就可以了吗？
> 1. 原始数据类型、`数组`、`本地代码实现`在性能方面都有巨大的优势
> 1. 包装类、集合(ArrayList)等，性能就会较低。
> 1. 但是开发效率作为产品开发的重要因素之一，并不会过多的去追求性能上的极致。

4、包装类和原始数据类型实现线程安全计数器
>1-原始数据类型
```java
public class Counter {
    private volatile long counter;
    private static final AtomicLongFieldUpdater<Counter> updater
            = AtomicLongFieldUpdater.newUpdater(Counter.class, "counter");
    public void increase(){
        updater.incrementAndGet(this);
    }
}
```
>2-包装类
```java
public class Counter {
    private final AtomicLong counter = new AtomicLong();
    public void increase(){
        counter.incrementAndGet();
    }
}
```

## Integer

1、Integer是什么?
> 1. 是int对应的包装类
> 1. 内部有一个int字段来存储数据
> 1. 提供了基本操作：数学运算、int和String间的转换等。

### 缓存机制

2、Integer的值缓存是什么？
> 1. Java 5中进行改进
> 1. 传统构建Integer的方式是直接new一个对象。
> 1. Java 5开始，新增了静态工厂方法`valueOf`
> 1. Integer大部分数据操作都集中在较小的范围内，因此Java将`-128 ~ 127`的数值进行了缓存。

3、Integer的缓存机制是什么？
> 1. `Integer.valueOf()`能进行缓存, 获取到Integer。
> 1. `Integer.intValue()`，取出缓存，获取到Int。

4、Integer的值缓存范围
> `-128 ~ 127`

5、包装类的缓存机制表
|包装类|缓存内容|备注|
|---|---|---|
|Integer|-128~127||
|Boolean|Boolean.TRUE/Boolean.FALSE||
|Short|-128~127||
|Byte|全部缓存|Byte的数值范围比较少，因此全部缓存，效率也较高。|
|Character|'\u0000'~'\u007F'||

6、如何增加Integer的缓存范围？
> 通过虚拟机参数`-XX:AutoBoxCacheMax=N`

### Integer的源码

6、Integer的构造方法
> 1. 内部存储的是int字段
```java
private final int value;
public Integer(int value) {
    this.value = value;
}
```

7、String是不可变的, 但是Integer是可变的？
> 错误!
> 1. Integer内部的value是`final int `，因此也是`immutable`

#### IntegerCache
```java
private static class IntegerCache {
    static final int low = -128;
    static final int high;
    static final Integer cache[];

    static {
        // high value may be configured by property
        int h = 127;
        String integerCacheHighPropValue =
            sun.misc.VM.getSavedProperty("java.lang.Integer.IntegerCache.high");
        if (integerCacheHighPropValue != null) {
            try {
                int i = parseInt(integerCacheHighPropValue);
                i = Math.max(i, 127);
                // Maximum array size is Integer.MAX_VALUE
                h = Math.min(i, Integer.MAX_VALUE - (-low) -1);
            } catch( NumberFormatException nfe) {
                // If the property cannot be parsed into an int, ignore it.
            }
        }
        high = h;

        cache = new Integer[(high - low) + 1];
        int j = low;
        for(int k = 0; k < cache.length; k++)
            cache[k] = new Integer(j++);

        // range [-128, 127] must be interned (JLS7 5.1.7)
        assert IntegerCache.high >= 127;
    }

    private IntegerCache() {}
}
```

### String缓存机制对比
5、String的缓存机制和Integer的缓存机制的相同和不同？

## 自动拆箱、装箱


1、什么是自动装箱和自动拆箱?
> 1. boxing和unboxing是在Java 5中提出
> 1. Java可以根据上下文，自动进行转换
> 1. 实际上是一种语法糖。（Java平台）会自动做一些转换。


2、Java具有编译阶段、运行时，自动装箱/拆箱发生在什么阶段？
> 1. boxing是Java自动做一些转换，用于保证不同的写法在`运行时是等价的`
> 1. boxing发生在`编译阶段`
> 1. 从而保证生成的`字节码`是一致的。

3、valueOf的缓存机制，对于boxing有效吗？
> 1. boxing就是`javac`自动转换为了`Integer.valueOf()`和`Integer.intValue()`
> 1. 因此缓存机制是生效的。

4、自动装箱/拆箱的注意点?
> 1. 要避免自动装箱和拆箱。
> 1. 大量的Java对象会产生内存和处理速度上的开销。
