# Java基础题目汇总（一）

本文链接：https://blog.csdn.net/feather_wch/article/details/131948032

关键词：函数式接口、Supplier、类型擦除、HB、动态代理、StackOverFlow、OOM、注解、类加载机制、类加载器、JUC、Phaser、函数式编程、方法引用、equals、值传递、finally、异常、序列化、锁升级、Intent

什么是 Java 中的函数式接口？请列举几个常用的函数式接口。
1. 函数式接口是指只定义了一个抽象方法的接口。
1. Java 8 引入了函数式编程的概念，函数式接口可以作为 Lambda 表达式的类型。
1. 常用的函数式接口有：Supplier、Consumer、Function、Predicate、Runnable 等。
练习题：请写一个函数式接口 `Converter`，定义一个抽象方法 `convert` ，实现一个 Lambda 表达式来将一个字符串转换为大写。
```
@FunctionalInterface
public interface Converter{
  T convert(T in);
}
// 使用
Converter ctr = (in) -> in.toUpperCase();
String upResult = ctr.convert("feather");
```

Java 8 引入了函数式编程的概念，函数式接口有哪些？
1. Supplier（供给型接口）：
```java
Supplier supplier = () -> "Hello World";
String result = supplier.get();
System.out.println(result); // 输出：Hello World
```
2. Consumer（消费型接口）：
```java
Consumer consumer = (num) -> System.out.println("Number: " + num);
consumer.accept(10); // 输出：Number: 10
```
3. Function（函数型接口）：
```java
Function<Integer, String> function = (num) -> "Result: " + (num * 2);
String result = function.apply(5);
System.out.println(result); // 输出：Result: 10
```
4. Predicate（断言型接口）：
```java
Predicate predicate = (num) -> num > 0;
boolean result = predicate.test(10);
System.out.println(result); // 输出：true
```
5. Runnable（运行接口）：
```java
Runnable runnable = () -> System.out.println("Hello World");
runnable.run(); // 输出：Hello World
```

Java 中的类型擦除是什么？它对泛型的使用有什么影响？
1. Java低版本中对泛型的处理采用类型擦除，编译后的字节码中只有Object类型，没有原本的类型信息
1. 为了兼容低版本和高版本，高版本中虽然会有类型擦除，但会将类型信息保存到signature中
1. 运行时丢失泛型的具体类型信息，导致List和List会认为是同一个类型，不能重载方法
1. 解决办法：
```
ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
Type actualTypeArgument = parameterizedType.getActualTypeArguments()[0];
```
getGenericSuperClass().getActualTypeArguments()[0] == 第一个泛型参数的类型

Java 中的内存模型中，什么是 happens-before 原则？它在多线程环境下的作用是什么？
1. 在多线程环境下，保证前面的操作的结果对后续的操作可见。
1. 是JMM中重要组成部分，用于保证多线程并发下的安全问题

Java 的动态代理是什么？如何使用动态代理实现 AOP（面向切面编程）？
1. 是一种在运行时动态生成代理类的机制。
1. 通过动态代理，可以在不修改原始类的情况下，为原始类的方法添加额外的逻辑。
1. 使用动态代理实现 AOP 可以将横切逻辑（如日志记录、事务管理）与业务逻辑解耦，并实现代码的重用性和灵活性。
1. 练习题：请实现一个动态代理，为一个接口添加日志记录的功能。
```java
class LogInvocationHandler implements InvocationHandler {
  private Object target;
  public LogInvocationHandler(Object target) {
      this.target = target;
  }
  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    System.out.println("调用方法：" + method.getName());
    Object result = method.invoke(target, args);
    return result;
  }
}
UserService userService = new UserServiceImpl();
UserService proxy = (UserService) Proxy.newProxyInstance(
userService.getClass().getClassLoader(),
userService.getClass().getInterfaces(),
new LogInvocationHandler(userService));
proxy.addUser("Alice");
```

什么是 Java 中的虚拟机栈溢出和堆溢出？分别说明它们发生的原因和如何避免。
1. 栈溢出：StackOverFlow，线程的栈空间不足，递归调用、方法嵌套调用过深(超过虚拟机栈的最大深度)
1. 堆溢出：OOM，内存不够，内存优化

Java 中的注解是什么？如何自定义一个注解，并在程序中使用它？
1. 是一种元数据，可以在类、方法、字段上添加额外信息
1. 可以在编译时、运行时结合注解实现功能，如APT、依赖注入
1. @interface

Java 中的类加载机制是什么？请解释类加载器的作用和不同的类加载器。
1. 类加载机制是运行时将类的字节码加载到虚拟机中，Class对象作为模板用于创建类的对象
1. 类加载机制，具有步骤：加载、链接(校验)、初始化
1. 类加载器的作用：负责查找类的字节码，并通过字节码创建对应的类对象
1. 采用双亲委派模型，保证了系统的核心类库不会被修改，没办法自定义Object类
PathClass APP默认的类加载器，运行时从头APK中加载类，并添加到APP的类路径中。每个APP都有独立的PathClas是
Dex 特殊的类加载器，加载APLK之外的Dex文件。可以加载存储在设备外部存储上的Dex
pathclassloader dexclassloader

Android中的类加载器有哪些？
1. **BootClassLoader**：Android 系统的根加载器，负责加载核心库和 Android 运行时所需的类。
1. 它是最顶层的类加载器，由 C++ 代码实现。
2. **PathClassLoader**：APP默认的类加载器。在运行时从APK 文件中加载类，并将其添加到应用程序的类路径中。
1. 每个应用程序都有一个独立的 PathClassLoader 实例。
1. 也可以加载外部存储上的DEX文件
3. **DexClassLoader**：特殊的类加载器，用于加载 APK 文件之外的 DEX 文件。DexClassLoader 可以加载存储在设备外部存储器上的 DEX 文件。
4. **URLClassLoader**：URLClassLoader 可用于加载位于设备外部存储器或网络上的类文件。
5. **BaseDexClassLoader**：DexClassLoader 和 PathClassLoader 的基类，封装了加载 DEX 文件的通用逻辑。

BootClassLoader负责加载核心库和 Android 运行时所需的类有哪些？
1. **核心库**：
- `core-libart.jar`：如 Java 核心类、集合类、输入输出类等。
- `ext.jar`：如 XML 解析、正则表达式、加密算法等。
- `framework.jar`：如 Activity、Service、BroadcastReceiver 等。
- `android.policy.jar`：包如窗口管理、输入法管理等。
- `services.jar`：如电源管理、网络管理、传感器管理等。
2. **Android 运行时所需的类**：`Dalvik/ART 运行时库`、`Zygote`、`SystemServer`、`Binder`、`Android 接口库`：如 ActivityManager、ContentProvider

解释 Java 中的并发包（java.util.concurrent）中的锁和同步器的使用场景和区别。
1. ReentrantLock+Condition：
1. CountDownLatch: count=0时，开门栓。基于DAG有向无环图的启动框架中可以使用。
1. CyclicBarrier：控制一组线程互相等待，barrier.await() 达到屏障处，等待其他线程执行完。
1. Semaphore: 计数信号量，实现连接池，控制同时访问资源的线程数。acquire/require
1. ForkJoinTask 分而治之

JUC中常见的工具类，有哪些分类？
1. Lock 和 Condition：ReentrantLock、ReentrantReadWriteLock、StampedLock、Condition 等。
2. 同步队列：ArrayBlockingQueue、LinkedBlockingQueue、SynchronousQueue、PriorityBlockingQueue、DelayQueue 等。
3. 阻塞队列：LinkedTransferQueue、LinkedBlockingDeque、ConcurrentLinkedQueue、ConcurrentLinkedDeque 等。
4. 同步容器：ConcurrentHashMap、CopyOnWriteArrayList、CopyOnWriteArraySet 等。
5. 原子类：AtomicInteger、AtomicLong、AtomicBoolean、AtomicReference、AtomicReferenceArray、AtomicIntegerArray 等。
6. 路障类：CountDownLatch、CyclicBarrier、Phaser 等。
7. 并发工具类：Semaphore、Exchanger、CompletableFuture、FutureTask、LockSupport 等。
8. 并发执行框架：ThreadPoolExecutor、ScheduledThreadPoolExecutor、ForkJoinPool 等。

StampedLock是什么?有什么作用？
1. Java 8 中引入的一种新的读写锁机制，它针对读多写少的场景进行了优化。StampedLock 通过引入“标记（stamp）”的概念，使得读操作可以在不阻塞写操作的情况下进行，并且提供了乐观读模式，进一步提升读操作的性能。
1. 读写分离：StampedLock 采用了读写分离的思想，读操作不会阻塞其他读操作，读写操作可以并发进行，从而提高了读操作的并发性能。
2. 乐观读模式：StampedLock 提供了乐观读（Optimistic Read）的模式，即通过 tryOptimisticRead() 方法获取一个标记（stamp），然后进行读操作。在进行完读操作后，可以使用 validate() 方法验证标记的有效性。如果标记无效，说明期间有写操作，需要以悲观读模式重试。
3. 读写互斥：当进行写操作时，其他读(readLock)写操作都会被阻塞，以保证写操作的原子性和一致性。

Phaser是什么？
1. 阶段器，每个阶段等待其他线程都达到
```java
class Worker implements Runnable {
  private final Phaser phaser;
  public Worker(Phaser phaser) {
    this.phaser = phaser;
  }
  @Override
  public void run() {
    // 阶段1
    phaser.arriveAndAwaitAdvance();
    // 阶段2
    phaser.arriveAndAwaitAdvance();
    // 阶段3
    phaser.arriveAndDeregister(); // 注销线程
  }
}
```

什么是 Java 的函数式编程？列举一些支持函数式编程的新特性和相关的函数式编程库。
1. 函数式编程是一种编程范式，强调使用纯函数（执行不依赖于除输入参数之外的任何状态或数据）和不可变数据(不会修改输入参数或修改外部状态)来进行编程。
1. Java 8 引入了函数式编程的支持，包括 Lambda 表达式、方法引用、流式 API（Stream API）、函数式接口和默认方法等。
1. Java 8 还提供了一些函数式编程库，如 Guava、RxJava、Vavr 等。

Java8方法引用是什么？
1. 可以直接传递方法作为参数，而不必编写冗长的 Lambda 表达式。
2. 方法引用：
```java
// 引用 Integer 类的静态方法 parseInt()
Function<String, Integer> parser = Integer::parseInt;
Integer result = parser.apply("10"); // 调用 parseInt("10") 返回整数 10
// 引用 String 的实例方法 length()
Function<String, Integer> lengthGetter = String::length;
Integer length = lengthGetter.apply("Hello"); // 调用 "Hello".length() 返回整数 5
```
3. 特定对象的实例方法引用：
```java
// 引用对象的实例方法
List list = Arrays.asList("A", "B", "C");
list.forEach(System.out::println); // 输出 A B C
```
4. 构造方法引用：
```java
// 引用 ArrayList 的构造方法
Supplier<List> listSupplier = ArrayList::new;
List newList = listSupplier.get(); // 创建一个新的 ArrayList 实例
```

Java 中的序列化和反序列化的机制是什么？如何处理序列化版本不一致的问题？
1. 在类中显式声明一个 `serialVersionUID` 字段，以确保序列化版本的兼容性。



-------------------------------------


Java静态内部类是什么？和非静态内部类有什么区别？
1. 静态内部类又称为静态嵌套类，不持有外部类引用，能用名称表现出业务关系，如RV
1. 非静态内部类：成员内部类，不可以有private修饰符。创建方法outer.new InnerClass()

区别：
1. 和外部类的关系
1. 创建方式
1. 访问权限
1. 内存占用：静态内部类不会初始化外部类

Java参数传递是值传递还是引用传递？
1. 基本数据类型值传递
1. 对象是引用传递

equals和==进行比较的区别
1. equals默认比较引用，根据equals的实现逻辑，比较内容
1. ==比较引用

String a = new String("xxx")会创建几个对象？
1. “xxx”在字符串常量池创建一个对象
1. 创建一个指向“xxx”对象的String对象，返回给a
1. 结果：两个

finally中的代码一定会执行吗？try中有return也是一定会执行吗？
1. 都会执行，但不一定
1. 系统强行停止等情况

异常中Exception和Error的区别
1. 都是Throwable的子类
1. Excpeiton：程序可以处理
1. Error：严重的系统问题

Parcelable和Serializable区别？

为什么Intent传递对象需要序列化？
1. Intent在不同组件传递，这些组件可能位于不同进程

锁的状态有哪些？
||偏向锁标志|锁标志|MarkWord|
|---|---|---|---|
|无锁|0|01||
|偏向锁|1|01|第一个访问锁的线程|
|轻量级锁|x|00|栈中RecordWord的指针|
|重量级锁|x|10|指向Mutex互斥量的指针|
|GC标记||11||
1. 无锁->偏向锁，会用CAS操作替换MW，原有内容存在线程1的堆栈中
1. 偏向锁的撤销，出现STW，线程2替换线程1的偏向锁状态为轻量级锁
1. 偏向锁的撤销，会出现STW，高并发场景下要禁止偏向锁
