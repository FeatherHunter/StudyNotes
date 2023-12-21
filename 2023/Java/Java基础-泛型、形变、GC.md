# Java基础-泛型、形变和GC

本文链接：https://blog.csdn.net/feather_wch/article/details/131948105

## 泛型、形变

1、Gson中TypeToken的作用是什么？是如何获得泛型的类型信息的？
1. TypeToken利用匿名内部类会持有泛型的类型信息，在signature中。进行解析。
1. new TypeToken<Furit>{}.getType();// 生成内部类的class文件，里面明确知道了里面T的类型是Fruit

2、数组 用GenricArrayType  代码是field.getGenericType()方法
3、非数组 用ParameterizedType，举例Map<String, Integer>
1. getActualTypeArugments泛型实际类型-获取数组放的是String、Integer
1. getRawType原始类型

4、泛型变量 TypeVariable getName名称 如K，获取上界和下界
5、通配符 WildcardType 获取到上界和下界

6、形变是什么？（不变、协变、逆变）
1. 协变：extends，父类引用 = 子类对象
1. 逆变：super，子类引用 = 父类对象
1. 协变：生产者 out T，读取泛型数据
1. 逆变：消费者 in T，写入泛型数据
1. 不变：普通的泛型
1. 协变、只读、生产者在RxJava中大量使用，用协变把List传入方法中，避免内鬼修改
1. 协变、修改模式、消费者，修改List中数据后交给外界去打印

7、? extends Animal 和 ? extends T 和 T exntends Animal的区别
1. ? extends Animal和? extends T 都是通配符类型参数，都是生产者消费者场景下用到的
1. ? extends Animal：生产者，可以保证读取出来的是Animal或者T的子类：只能按照Animal和父类读取
1. T exntends Animal：限定泛型需要是Animal的子类

8、逆变? super Dog，只可以按照Dog子类和Object存入数据，但没办法读取
1. 无法确定具体是哪个类，只知道是Dog的父类，
1. 读取时可以按照Object读取(特例)，其他情况不能读取

9、上界通配符和下界通配符是指什么？
1. ? extends Animal
1. ? super Dog

10、Kotlin支持在声明处限定范围
1. interface Callback
1. Java不可以，只可以声明T

11、Kotlin中协变、逆变一起使用
> <in INPUT, out OUTPUT>

### 形变和泛型擦除
12、T extends Animal、T super Animal会如何泛型擦除？
1. 前者擦除为Animal，后者擦除为Object

13、泛型为什么不允许调用构造方法来实例化 new T()?
1. 无法确定T的具体类型的构造方法有多少个参数

14、泛型擦除导致不可以用instanceOf

15、Java中的？等于Kotlin中的*
> 意义和作用一致

16、List<?> 等价于 List<? extends Object>

17、数组默认支持协变，在运行时才检查，容器集合会在编译时检查，使用泛型时优先用集合
## GC

1、对象被标记为垃圾后，不可能再被标记为存活，因为不可达。已经找不到地址了。

2、reinterpret(重新解释)
3、为什么Java不允许直接操作内存？为了垃圾回收机制。
4、为什么C/C++不能有垃圾回收机制？因为可以直接访问内存地址
> C、C++中可以用long保存内存地址，在GC后，还可以继续用，完蛋！

5、为什么JVM不去管理C/C++分配的内存空间？不好管，要出事。

6、创建对象
> 指针碰撞>有多线程问题>CAS+失败重试机制 or TLAB thread local allocate buffer
