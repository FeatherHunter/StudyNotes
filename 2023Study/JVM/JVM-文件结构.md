
# JVM

本文链接：https://blog.csdn.net/feather_wch/article/details/132116849

## 类文件结构

[文件结构-脑图](https://note.youdao.com/s/MnEjJB9P)

1、class文件的组成
1. 无符号数：基本数据类型 u1 u2 u3 u4 描述
   1. 数字
   2. 字符串
   3. 索引引用
2. 表：复合数据类型，无符号数 + 表组， _info结尾

2、class文件是大段字节序
> 方便阅读

3、class文件的组成部分
1. 魔数
2. 版本号
3. 常量池
4. 访问标志
5. 类索引
6. 父类索引
7. 接口索引集合
8. 字段表集合
9. 方法表集合
```java
// 魔数 咖啡
// 版本号
// 常量池
// 访问标志
// 类索引，父类索引，接口索引集合
final class Man extends Person implements IEat, ISleep{
    public static final String YEAR = "2023"
    String name; // 字段表集合
    public void talk(){ // 方法表集合
        //
    }
}
```

4、魔数是什么？
1. **0xCAFEBABY** 咖啡宝贝
2. 4byte

5、版本号
1. 次版本号：儿子在前 // 质子
2. 主版本号：爸爸在后 // 纣王

### 常量池
6、常量池 = 入口 + 常量池
1. 入口：u2，= 多少个常量
2. 常量池：1个常量 = 1个表，
   1. 17种表 = U1 tag + 独立的结构

7、常量池中存放的内容
1. 字面量（常量值，String）
2. 符号引用
   1. 类和接口的名称
   2. 方法的名称和描述符，描述符()Ljava/lang/String;
   3. 字段的名称和描述符，描述符Ljava/lang/Object;
   4. 方法句柄和方法类型
   5. 动态调用点和动态常量

8、方法的描述符  ==> JNI方法描述符
> 方法的参数类型和返回类型

9、JNI中方法描述符（Method Descriptor）
1. 唯一地标识一个方法
2. 一个方法描述符为：(Ljava/lang/String;I)V 表示该方法有两个参数，分别为String类型和int类型，返回值类型为void。

10、JVM中方法句柄和方法类型是指什么？  ===> 反射机制
1. 方法句柄（MethodHandle）：可以看作是一个轻量级的函数指针，用于表示对方法的调用。
2. 方法类型（MethodType）描述了方法的参数类型和返回类型。
3. 两者通常一起使用：
```java
Person person = new Person();
MethodType methodType = MethodType.methodType(void.class);
MethodHandle methodHandle = MethodHandles.lookup().findVirtual(Person.class, "sayHello", methodType);
methodHandle.invokeExact(person);
```

11、JVM中的动态调用点
1. 指的是在程序运行时根据实际对象类型来确定要调用的方法。这种调用方式称为【动态分派】 ===> JVM 方法调用
2. 动态调用点通常发生在针对多态类型的方法调用中。
3. 动态调用点的确定是在运行时发生的，使用了虚方法表（virtual method table）来存储对象的方法信息，以便在运行时进行动态分派。
   1. 在虚方法表中，每个方法对应一个偏移量，通过偏移量可以找到对应的方法实现。 ===> 虚方法表

12、JVM中的动态常量
1. 运行时才确定常量的数值
2. 有性能开销
```java
public class Main {
    public static final int MAX_VALUE = calculateMaxValue();
    public static int calculateMaxValue() {
        // 这里可以是一些复杂的逻辑来计算最大值
        return 100;
    }
    public static void main(String[] args) {
        System.out.println("最大值：" + MAX_VALUE);
    }
}
```
### 访问标志-access_flags
1. class是类、接口、注解还是枚举
2. 是否是public、abstract等

### 类索引
1、类索引是什么？
1. 用于到常量池中寻找：类的全限定名

### 父类索引
1. U2
2. 用于到常量池中寻找：父类的全限定名

### 接口索引集合
1. 入口 + 索引表集合(U2集合)
2. 用于到常量池中寻找：接口的全限定名

### 字段表集合
1. 入口 + 字段表
2. 描述类和接口中声明的字段
3. 字段表中每一项包含的内容
   1. 访问标志
   2. 名称索引 -> 常量池中
   3. 描述符索引 -> 常量池中
   4. 属性表集合 = 入口 + 属性表
      1. 举例：final static int a = 123, 在属性表中会有COnstantValue属性 ===> 类加载阶段

### 方法表集合
1. 入口 + 方法表
2. 访问标志 + 名称索引 + 描述索引 + 属性表集合
 
2、特征签名是什么？
1. 方法重载的时候，Java中特征签名 = 方法参数在常量池中符号引用的集合
1. class中特征签名 = 返回值 + 方法参数在常量池中符号引用的集合

### 属性表
1、属性表的主要属性 ===> APT ===> ASM
1. Code
2. Exception
   1. 和异常表不同 ===> ?
   2. 展示方法需要检查哪些异常
3. ConstantValue ===> 类加载机制，准备阶段
    ```java
    int x = 123; // <init()>实例构造方法中赋值
    static int x = 123; // <clinit>类构造器中赋值
    final static int x = 123; //ConstantValue属性，在【准备阶段】赋值
    ```

2、Code属性的结构
1. 属性名
2. 属性长度
3. 操作数栈最大深度
4. 局部变量表的存储空间（slot为单位）：不等于局部变量表最大值，会复用
5. 字节码长度：总长度
6. 字节码指令：多个指令
   1. aload_0、invokespecial、init等指令
7. 异常表
   1. start_pc
   2. end_pc
   3. catch_type
   4. handler_pc
