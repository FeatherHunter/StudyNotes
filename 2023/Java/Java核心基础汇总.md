关键字：抽象类、接口、重载、重写

转载请注明：http://blog.csdn.net/feather_wch/article/details/52043153

> 最近重新整理以前的笔记资料，打造夯实基础。

# Java核心基础汇总
版本：2023/6/14(6:52)

## Java中提供了抽象类和接口，开发中如何进行选择呢？

抽象类和接口的区别：
1. 抽象类的目的是：代码复用【重用】；接口的目的是：对类行为进行约束【解耦】
2. 抽象类是类属性和行为的抽象集合；接口是行为的抽象集合；
3. 抽象类可以有成员变量，可以是protected的；接口的成员变量一定是public static final的
4. 抽象类的方法 public、protected、default；接口的方法 public
5. 抽象类的方法可以有默认实现，子类可以选择实现还是不实现；接口的方法不能有实现，除了后续Java推出了默认方法
6. 抽象类有构造方法；接口没有
7. 抽象类只能单继承；接口可以实现多重继承
8. 开发中最顶级是接口，然后抽象类实现接口，最后具体实现类


多重继承会产生什么问题？
1. 产生二义性问题
2. 父类A、B都有方法C，子类调用方法C是调用谁的呢？

### AMS中如何实现多重继承

1. 系统服务中很多服务需要能跨进程传输，因此需要继承自Binder
2. SystemServer为了方便管理服务，要求所有服务继承自SystemService，使用SystemServiceManager进行管理
3. 系统服务使用静态内部类Lifecycle extends SystemService进行管理
4. 这里采用抽象类为什么不是接口？
> 1. 只有public abstract void onStart();是抽象方法，强制子类需要实现。
> 2. 其他字段有protected、private，方法有public、private，都按照需要实现了内部需求。接口没有办法实现这些需求。


## 重载和重写是什么意思？

重载：
1. overload
2. Java层面同名方法根据参数的不同选择不同的方法。JVM层面还根据方法返回值确定签名
3. LayoutInflater的inflate方法加载布局，涉及到inflate方法的重载
4. 自定义View，构造方法的重载。


重写：
1. override
2. 覆盖父类完全相同的方法，可以修改和扩展
3. 例如 Activity的生命周期

### JVM方法调用

1、JVM中方法调用是什么意思？
> 方法调用不是指方法执行，而是只确定目标方法

2、方法调用分为哪几类
> 1. 解析调用
> 2. 静态分派
> 3. 动态分派

### LayoutInflater和换肤

1、LayoutInflate的inflate方法参数解析
> 1. merge：需要有parent，attachToRoot = true 直接建立联系；= false 需要调用addView。否则报错。
> 2.include：需要有父标签？


## Java静态内部类是什么？和非静态内部类有什么区别？

## Java传递参数是值传递还是引用传递

## equals和==进行比较区别

## String a = new String("xxx")创建了几个String对象？

## finally中代码一定会执行吗？try中有return一定会执行吗？

## 异常中Exception和Error的区别

## Parcelable和Serializable区别？

## 为什么Intent传递对象需要序列化？
