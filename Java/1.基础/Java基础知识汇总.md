转载请注明链接: https://blog.csdn.net/feather_wch/article/details/50470939

>介绍了java基础知识，包括基本数据类型，封装，集成，多态，以及各种闲散知识点。

# Java基础知识汇总

版本：2018/8/28-1(22:53)

---

[TOC]

## Java平台

Java平台详解：https://blog.csdn.net/feather_wch/article/details/82114270

## 琐碎知识点

1、位运算
> 1. ! 非
> 1. ~ 按位取反

2、如何从命令行输入值？
```java
try{
    InputStreamReader isr = new InputStreamReader(system.in);
    BufferedReader br = new BufferedReader(isr);
    String s = br.readLine();        //只读取一行
    float  f = Float.parsefloat(s);  //转换
}catch(Exceptionn e){
    e.printStackTrace();
}
```

3、如何访问类中的static字段
> 1. 类.total;
> 1. 对象.total;

4、静态类方法
> 不能访问非静态变量

5、如何获得系统时间
```java
Calender cal = Calender.getInstance();
cal.getTime();
```


6、如何读取文件
```java
FileReader fr = new FileReader("d:\\txt.c");
```

7、System.exit()
> 1. System.exit(0): 正常终止JVM
> 1. System.exit(-1): 非0 异常中止JVM

8、网络相关
> 1. tracert www.baidu.com
> 1. router 连接自己的最近的网关
> 1. netstat -an 所有连接自己的人


## 封装
1、封装体现在哪里？
> 1. 将具体实现保护在类内部
> 1. 访问控制修饰符

### 访问修饰符
2、访问修饰符的作用

|修饰符|同类|同包|子类|不同包|
|---|---|---|---|---|
|public| √| √| √| √|
|protected| √| √| √|
|默认|√|√|
|private|√|

3、包的功能
> 1. 同名类区分开
> 1. 方便管理
> 1. 控制访问范围

4、包的使用方法
```java
package com.xiaoqiang;
import  com.xiaoqiang;
```

## 继承

1、继承
> 1. 子类最多继承一个父类
> 1. jdk所有类为object子类
> 1. 方法重载：子类不能缩小父类方法的访问范围

## 多态
1、什么是多态？
> 1-一个引用（类型）在不同情况的多种状态
```java
Cat cat = new Cat();
Dog dog = new Dog();
Animal a = cat;
a.cry(); //Animal 必须有 cry
```
> 2-如果没有cry则使用父类cry
> 3-父类在一定情况下可以转换为子类

## 抽象类

1、什么是抽象类
```java
abstract class Animal(){};
```
> 1. 不一定有抽象方法
> 2. 不能被实例化
> 3. 子类必须实现抽象方法
> 4. 可以有变量
> 5. 抽象方法不能被实现

## 接口
1、接口的使用
```java
interface Usb{
  public void start();
  public void stop();
}
class pc implements Usb{...}
void useUsb(Usb usb){
  usb.start();
  usb.stop();
}
```
> 1. 接口中变量为 static,final int a = 0。常用作全局变量。例如：Usb.a
> 1. 接口可以继承接口
> 1. 体现了多态，以及高内聚低耦合的特点

### 接口与抽象类区别
2、接口与抽象类区别
> 1. 接口是对继承的一种补充
> 1. 接口能继承接口，不能继承类
> 1. 接口的所有方法不能实现。是更为抽象的抽象类。
> 1. 能实现多个接口，只能继承一个父类

3、接口会继承吗？
> 1. 子类会继承父类关于接口的实现


## 数组,多维数组

1、数组的长度可以用变量吗？
```java
int x = 1;
int []a = new int[x]; //不会出错，但是不行。
```
2、数组的定义方法
```java
int a[] = {1,2,3};
int a[] = new int[3];
int []a = new int[3];
```

3、数组的长度
> a.length

4、对象数组
```java
Dog dogs[] = new Dog[4];
dogs[0] = new Dog();//必须要分配
```
## 二进制

1、计算机中均用补码显示
2、0的反码，补码均为0
3、>>,<< 算数 补符号 >>> 逻辑 补0
4、^ 异或
5、80G中，1m=1024k，销售时 1m=1000k

## 泛型
1、泛型好处
> 1. 安全
> 2. 提高重用率
