转载请注明链接:https://blog.csdn.net/feather_wch/article/details/82564060

# JVM中的方法调用

版本：2018/9/9-1(23:59)

---

[TOC]

## 问题汇总

1. 可变长参数方法的重载
1. 官方文档建议避免重载可变长参数方法
1. 什么是重载？
1. 如何绕开多个方法名字相同、参数类型相同的限制？
1. 当一个类(具有多个方法，方法名相同、参数类型相同、但是返回值类型不同)出现在Java编译器的用户路径上时，如何确定应该调用哪个方法？
1. 重载方法的识别是在哪个阶段完成的？
1. Java编译器如何根据传入参数的声明类型来选取重载方法的?
1. 声明类型和实际类型的区别？
1. Java编译器在同一个阶段中找到了多个适配的方法，如何选择一个最合适的方法？
1. 重载对于从父类继承来的非私有同名方法有效吗?
1. JVM中不存在重载这一概念
1. 如果子类定义的方法和继承自父类的方法同名，但是参数类型相同，这两个方法有什么关系？
1. 父类和子类具有两个同名方法，都是静态方法，都是public。子类中的方法会隐藏了父类中的方法。
1. 重写是什么?
1. 方法重写是如何体现了Java的多态？
1. JVM是如何识别方法的？关键在于三部分
1. 方法描述符是什么?
1. 如果一个类中出现了方法名相同，方法描述符相同的方法，会在类加载的什么阶段报错？
1. JVM和Java语言规范对于方法的限制有哪些不同处？
1. JVM的重写和Java语言的重写并不完全相同
1. 什么是桥接方法？编译器如何通过生成桥接方法解决了Java和JVM重写语义不同的问题？
1. 什么是静态绑定?什么是编译时多态？
1. 什么是动态绑定？
1. 对于JVM，什么是真正的静态绑定？
1. 对于JVM，什么是真正的动态绑定？
1. Java字节码中与调用相关的指令一共有五种
1. invokeinterface实例
1. invokestatic实例
1. invokevirtual实例
1. invokespecial实例
1. invokeastatic和invokespecial，JVM能直接识别具体的目标方法.
1. invokevirtual和invokeinterface，JVM需要在执行中，根据调用者的动态类型，来确定目标方法。
1. 符号引用的作用？
1. 符号引用存储在哪里？
1. 符号引用的分类
1. 实例中的符号引用
1. 符号引用什么时候需要被替换为实际引用？
1. JVM如何解析`非接口符号引用`，并替换为实际引用?
1. 非接口符号引用的解析过程所得到的结论
1. 隐藏和重写的区别
1. JVM如何解析`接口符号引用`，并替换为实际引用?
1. 实际引用是什么？
1. 如何将java文件翻译成字节码文件

---

## 可变长参数的方法重载

1、可变长参数方法的重载
```java
void invoke(Object obj, Object... args) { ... }
void invoke(String s, Object obj, Object... args) { ... }

invoke(null, 1);// 调用第二个 invoke 方法
invoke(null, 1, 2);// 调用第二个 invoke 方法 invoke(null, 1, 2);
invoke(null, new Object[]{1});// 调用第一个 invoke 方法 ,只有手动绕开可变长参数的语法糖，才能调用第一个 invoke 方法
```
> 1. 在具有Object和String参数的情况下，默认都会去调用第二个方法。

2、官方文档建议避免重载可变长参数方法

## 重载

1、什么是重载？
> 1. Java中，如果同一个类中有多个方法：名字相同，参数类型相同。会无法通过编译。
> 2. 如果想要在同一个类中定义名字相同的方法，参数类型必须不同。
> 3. Java根据参数类型的不同，去选择对应的方法，称之为重载。

2、如何绕开多个方法名字相同、参数类型相同的限制？
> 1. 可以通过字节码工具绕开
> 1. 编译完成后,再向class文件中添加方法名相同、参数类型相同、但是返回值类型不同的方法。

3、当一个类(具有多个方法，方法名相同、参数类型相同、但是返回值类型不同)出现在Java编译器的用户路径上时，如何确定应该调用哪个方法？
> 1. 目前java编译器会直接选取第一个方法名以及参数类型匹配的方法
> 1. 并且根据返回值类型，判断是否可以通过编译，是否需要进行值转换

4、重载方法的识别是在哪个阶段完成的？
> 编译阶段

5、Java编译器如何根据传入参数的声明类型来选取重载方法的?
> 1. 第一阶段：在不考虑对基本类型自动装拆箱，以及可变长参数的情况下选取
> 1. 第二阶段：没有找到，在允许自动装拆箱，不允许可变长参数的情况下选取
> 1. 第三阶段：还没找到，在允许自动装拆箱，允许可变长参数的情况下选择

6、声明类型和实际类型的区别？
> 1. 声明类型：(Object)str，声明类型就是Object类型。
> 1. 实际类型：(Object)str，实际类型是String
```java
String str = "str";
// 声明类型为String
invoke(str, 1);
// 声明类型为Object
invoke((Object)str, 2);
```

7、Java编译器在同一个阶段中找到了多个适配的方法，如何选择一个最合适的方法？
> 1. 这个符合程度的决定性因素就是`形式参数类型的继承关系`
> 1. 比如传入参数null，可以是Object，也可以是String。因为String是子类，所以Java编译器认为String更符合。
```java
public static void invoke(Object obj) {
        System.out.println("Object");
}
public static void invoke(String s) {
        System.out.println("String");
}
// null可以是Object，也可以是String
invoke(null);
```

8、重载对于从父类继承来的非私有同名方法有效吗?
> 1. 子类具有一个方法，和继承自父类的方法，名称相同，参数类型不同。这就属于重载。

9、JVM中不存在重载这一概念
> 1. 重载方法的区分处于编译阶段

## 重写

1、如果子类定义的方法和继承自父类的方法同名，但是参数类型相同，这两个方法有什么关系？
> 1. 如果这两个方法是静态方法，也不是私有的，子类中的方法隐藏了父类中的方法
> 1. 如果都不是静态方法，也不是私有的，子类就是重写了父类的方法。

2、父类和子类具有两个同名方法，都是静态方法，都是public。子类中的方法会隐藏了父类中的方法。
> 1-父类和子类
```java
public class People {
    public static void print(){
        System.out.println("People");
    }
}
public class Student extends People{
    public static void print(){
        System.out.println("Student");
    }
}
```
> 2-无法通过子类去调用到父类的方法。
```java
Student.print();
```
> 3-如果子类没有print()方法，可以通过子类类名来调用父类的静态方法
```java
Student.print();
// 打印
People
```

3、重写是什么?
> 1. 子类和父类具有两个方法名相同，参数类型相同，并且都是非静态方法，非私有方法。这就是重写。
> 1. 无法通过子类对象去调用到父类该方法，只能在子类内部，通过super.xxx()来调用。

4、方法重写是如何体现了Java的多态？
> 允许子类在继承父类部分功能的同时，拥有自己独特的行为

## 静态绑定和动态绑定

1、JVM是如何识别方法的？关键在于三部分
> 1. 类名
> 1. 方法名
> 1. 方法描述符(method descriptor)

2、方法描述符是什么?
> 1. 由方法的类型参数，以及返回类型，所构成。
> 1. 如果同一个类中出现了名字相同、方法描述符相同的方法，JVM会在类的验证阶段报错

3、如果一个类中出现了方法名相同，方法描述符相同的方法，会在类加载的什么阶段报错？
> 类的验证阶段

4、JVM和Java语言规范对于方法的限制有哪些不同处？
> 1. Java不允许方法名、参数类型同时都相同。不能通过返回值类型进行区分。
> 1. JVM允许通过返回类型不同来区分方法。(字节码中的方法描述符，具有返回类型)

5、JVM的重写和Java语言的重写并不完全相同
> 在子类和父类中具有同名的方法，并且非静态，非私有的前提下。
> 1. JVM需要参数类型和返回类型都一致
> 1. Java语言只需要参数类型一致。
> 1. 这些区别，编译器会通过生成桥接方法来实现Java中的重写语义

6、什么是桥接方法？编译器如何通过生成桥接方法解决了Java和JVM重写语义不同的问题？

7、什么是静态绑定?什么是编译时多态？
> 1. 一般认为重载，就是静态绑定，也成为编译时多态。
> 1. 并不准确：某个类中的重载方法可能被子类所重写。
> 1. 此时Java编译器会将，所有，对实例的非私有方法的调用编译为需要动态绑定的类型。

8、什么是动态绑定？
> 1. 重写被称为动态绑定。

9、对于JVM，什么是真正的静态绑定？
> 1. 在解析时便能够直接识别目标方法的情况
> 1. 对于静态绑定的方法调用而言，实际引用是一个指向方法的指针。

10、对于JVM，什么是真正的动态绑定？
> 1. 在运行过程中，根据调用者的动态类型来识别目标方法的情况。
> 1. 对于动态绑定的方法调用而言，实际引用是一个方法标的索引。


## 调用指令

1、Java字节码中与调用相关的指令一共有五种
> 1. invokestatic: 调用静态方法
> 1. invokespecial：调用实例的私有方法、构造器、以及super关键字所调用的父类的实例方法或者构造器，和所实现接口的默认方法
> 1. invokevirtual：调用实例的public方法
> 1. invokeinterface：调用接口方法
> 1. invokedynamic：调用动态方法

2、invokeinterface实例
> 调用接口的方法
```java
    interface Listener{
        void onClick();
    }
    Listener listener = new Listener() {xxx};
    // invokeinterface	InterfaceMethod Main$Listener.onClick:"()V",  1;
    listener.onClick();
```

3、invokestatic实例
> 调用static方法
```java
public class Main {
    public static void main(String args[]) {
      // invokestatic	Method printHello:"()V";
        printHello();
    }
    public static void printHello(){
        System.out.println("Hello World!");
    }
}
```

4、invokevirtual实例
> 调用对象的public方法
```java
public class Main {
    public static void main(String args[]) {
        Main main = new Main();
        // invokevirtual	Method printHello:"()V";
        main.printHello();
    }
    public void printHello(){
        System.out.println("Hello World!");
    }
}
```

5、invokespecial实例
> 1. 调用父类的构造方法
> 1. 调用父类的实例方法
> 1. 调用父类所实现的接口方法
> 1. 调用子类的构造方法
> 1. 调用子类的private实例方法
```java
public class Student extends People{
    public Student(){
        // 1、调用父类的构造器
        super();
        // 2、调用父类的实例方法
        super.parentMethod();
        // 3、调用父类实现的接口方法
        super.run();
    }

    public Student(String name){
        // 4、子类的构造器
        this();
        // 5、子类的私有实例方法
        this.childMethod();
    }

    private void childMethod(){
    }
}
```

6、invokeastatic和invokespecial，JVM能直接识别具体的目标方法.

7、invokevirtual和invokeinterface，JVM需要在执行中，根据调用者的动态类型，来确定目标方法。

### 符号引用

8、符号引用的作用？
> 1. 编译过程中，不知道目标方法的具体内存地址
> 1. 编译器会暂时用符号引用来表示该目标方法
> 1. 该符号引用会包括：目标方法所在类或者接口的名字、目标方法的方法名、方法描述符

9、符号引用存储在哪里？
> 1. class文件的常量池中
> 1. javap -v xxx.class可以打印出常量池.
```java
// Main.java文件
public class Main {
    interface Listener{
        void onClick();
    }
    public static void main(String args[]) {
        Listener listener = new Listener() {xxx};
        listener.onClick();
    }
}
```
```
// Main.class文件的常量池
Constant pool:
   #1 = Methodref          #6.#18         // java/lang/Object."<init>":()V
   #2 = Class              #19            // Main$1
   #3 = Methodref          #2.#18         // Main$1."<init>":()V
   #4 = InterfaceMethodref #7.#20         // Main$Listener.onClick:()V
   #5 = Class              #21            // Main
   #6 = Class              #22            // java/lang/Object
   #7 = Class              #23            // Main$Listener
   #8 = Utf8               Listener
   #9 = Utf8               InnerClasses
  #10 = Utf8               <init>
  #11 = Utf8               ()V
  #12 = Utf8               Code
  #13 = Utf8               LineNumberTable
  #14 = Utf8               main
  #15 = Utf8               ([Ljava/lang/String;)V
  #16 = Utf8               SourceFile
  #17 = Utf8               Main.java
  #18 = NameAndType        #10:#11        // "<init>":()V
  #19 = Utf8               Main$1
  #20 = NameAndType        #24:#11        // onClick:()V
  #21 = Utf8               Main
  #22 = Utf8               java/lang/Object
  #23 = Utf8               Main$Listener
  #24 = Utf8               onClick
```

10、符号引用的分类
> 1-接口符号引用
```
 #4 = InterfaceMethodref #7.#20         // Main$Listener.onClick:()V
```
> 2-非接口符号引用
```
   #3 = Methodref          #2.#18         // Main$1."<init>":()V
```

11、实例中的符号引用
> 1. `#3`就是符号引用，`invokespecial #3`就是调用`#3`所表示的方法
```
public static void main(java.lang.String[]);
    xxx
         4: invokespecial #3                  // Method Main$1."<init>":()V
         7: astore_1
         8: aload_1
         9: invokeinterface #4,  1            // InterfaceMethod Main$Listener.onClick:()V
        14: return
    xxx
}
```

12、符号引用什么时候需要被替换为实际引用？
> 1. 执行使用了符号引用的字节码前,JVM需要解析符号引用，并替换为实际引用

13、JVM如何解析`非接口符号引用`，并替换为实际引用?
> 1. 在指向的类C中查找名字符合、描述符符合的方法
> 1. 如果没有找到，在类C的父类中继续搜索，直至Object类。
> 1. 如果还是没有找到，会在类C直接或者间接实现的接口中搜索。
> 1. 第三步搜索到的目标方法必须是public、非static的方法。
> 1. 第三步如果是间接实现的接口中，则需要满足类C和该接口之间没有其他符合条件的目标方法。(比如类C实现了接口1，接口1继承接口2，接口2继承接口3.那么类C和接口3之间，就隔着接口1)。如果有多个符合条件的目标方法，则返回其中任意一个。

14、非接口符号引用的解析过程所得到的结论
> 1. 静态方法也可以通过子类来调用
> 1. 子类的静态方法会隐藏父类中同名、同描述符的静态方法。

15、隐藏和重写的区别
> 1. 隐藏的方法：都需要是static、public的同名、同描述符方法。
> 1. 重写的方法：都需要是非static、public的同名、同描述符方法。

16、JVM如何解析`接口符号引用`，并替换为实际引用?
> 1. 在接口I中查找名字符合、描述符符合的方法
> 1. 如果没有找到，在Object类中的public、非static方法(实例方法)中搜索
> 1. 如果没有找到，则在接口I的超接口中搜索。
> 1. 第三步搜索到的目标方法必须是public、非static的方法。

17、实际引用是什么？
> 1. 对于静态绑定的方法调用而言，实际引用是一个指向方法的指针。
> 1. 对于动态绑定的方法调用而言，实际引用是一个方法标的索引。

## 知识扩展

1、如何将java文件翻译成字节码文件
```
// 生成class文件
javac Student.java
// 将class文件转换成字节码文件
java -cp .\asmtools.jar org.openjdk.asmtools.jdis.Main Student.class >Student.jasm
```
