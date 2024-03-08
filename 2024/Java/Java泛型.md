
# Java泛型

[toc]

知识分为哪些方面？
1、泛型是什么？泛型的默认值
2、泛型的三种类型（泛型方法和普通方法）
3、泛型的限定/约束（多重限定）
4、泛型的限制点（实例化、不可以用泛型的场景、泛型数组、异常、泛型擦除、instanceof、静态字段/方法和JVM类加载机制）


## 思考问题
1. 为什么需要泛型？
> // Java语言的 封装、继承、多态
> 1. 代码复用，提高效率。
> 2. 类型安全+自动转换+提高性能(避免装箱、拆箱)：编码时指明数据的类型，编译时检查出错误（List指明了是Integer的就不能传入其他无关的类型数据），并且后续使用不需要强制类型转换

2. 泛型类、泛型接口、泛型方法
```java
// 1、泛型类
class Person<T>{
T info;
public void showInfo(){
System.out.println(info);
}
}

// 不指明泛型，就是Object类型的
Person<Long> p1 = new Person<Long>();
Person p2 = new Person(); // 不指明泛型，就是Object的

// 2、泛型接口
public interface GenericInterface<T>{
public T next();
}

// 3、泛型方法，可以独立于泛型类和泛型接口
static class Person{
// 普通类即可
public <T> void showInfo(T info){
System.out.println(info);
}

// 可以变长参数info
public <T> void showInfo2(T... info){
System.out.println(info[info.length/2]); // info作为数组使用
}
}
// showInfo2使用：
Person p = new Person();
p.showInfo2(10, 20, 30);
p.showInfo2("a", "b", "c");
//p.<Integer>showInfo2("a", "b", "c"); // 错误！用泛型指明参数是Integer，但是参数的类型是String
p.showInfo2(10, "b", "c"); // Integer和String混用，作为Object解析，本身toString方法实现没有问题就会展示OK
```
3. 如何限定类型变量？
```java
class Person<T extends Animal>{}
class Person<T super Animal>{}
```
4. 泛型使用中的约束和局限性？
> extends 是 协变，只读模式，生产者（父类引用 = 子类对象）
> super 是 逆变，只写模式，消费者（子类引用 = 父类对象）
5. 泛型类型可以继承吗？
```java
class Person<T>{
T info;
public void showInfo(){
System.out.println(info);
}
}
// 可以继承
class Man extends Person{}
```
6. 泛型中通配符类型
```java
Person<Long> p = new Person<>();
Person<? extends Long> p2 = new Person<>();
```
7. 虚拟机是如何实现泛型的？

## 自定义问题

1、泛型是什么？
> 参数化的类型（定义），将类型参数化
> 可以理解为：类型变量

2、泛型有哪几种？
> 泛型类、泛型接口、泛型方法

3、泛型接口的作用是什么？
> 在需要使用接口时，数据类型不确定，需要参数化的类型时，使用泛型接口
> 什么时候使用接口？=>要看接口和抽象类的区别
> 1. 抽象类的目的是代码复用【重用】，接口的目的是对类行为进行约束【解耦】
> 2. 抽象类是类属性和类行为的抽象，接口是类行为的抽象
> 3. 抽象类可以有抽象方法和普通方法（可以有实现，子类选择实现还是不实现），接口必须是抽象方法（public abstract）不能有实现（Java后续提供了默认方法）
> 4. 抽象类可以有protected、default、public的属性，接口必须是public static final 的静态常量
> 5. 抽象类有构造方法，接口没有
> 6. 抽象类单继承，接口多重继承

4、AMS实现多重继承为什么使用抽象类而不是接口？
1. 有默认方法，由子类根据情况选择是自己实现还是不实现
2. 有protected的变量

5、抽象类和接口的选用？
1. 能用抽象类就不要用接口（系统源码最顶层是接口，然后是抽象类，最后是具体类）
2. 接口是对抽象类的补充
3. 接口有了默认方法为什么还要用抽象类？（接口中变量是public static final的，不符合一些场景的需求）

6、泛型接口的实现类是怎么实现的？
```java
// 1、不用泛型，是Object类型
public class GA implements GenericInterface{
@Override
public Object next() {
return null;
}
}
// 2、使用泛型 （后续作为一般的泛型类去使用）
public class GB<T> implements GenericInterface<T>{
@Override
public T next() {
return null;
}
}
// 3、指明泛型的具体类型，如String
public class GC implements GenericInterface<String>{
@Override
public String next() {
return "new data";
}
}
```

7、泛型方法和使用了泛型的普通方法的区别
> 不是声明在泛型类中的就是泛型方法
```java
static class Person<T>{
// 是泛型方法吗？不是，是普通方法
public T showInfo(T info){
System.out.println(info);
return info;
}
}

class Animal{
// 普通方法：使用了泛型类的作为参数而已
public void showInfo(Person<String> p){
// XXX
}
}
```

8、泛型方法的泛型和外部泛型类的泛型是完全不一样的
```java
class Animal<T>{
public <E> void showInfo1(E info){
// XXX
}
// 此处T和上面的T是完全独立的
public <T> void showInfo2(T info){
// XXX
}
}
```

9、泛型类的普通方法（泛型会被编译器替换为具体类型）
```java
class Fruit{}
class Apple extends Fruit{}

class Animal<T>{
public void showInfo(T info){
// xxx
}
public <E> void showInfo1(E info){
// XXX
}
public <T> void showInfo2(T info){
// XXX
}
}
public void test(){
Animal<Fruit> animal = new Animal();
// 不会报错，为什么？
// 因为编译器，会将T直接替换为类型Fruit，Apple作为Fruit的子类，肯定可以使用
animal.showInfo(new Apple());
//animal.showInfo(new Person()); // 不可以，泛型保证了类型安全，会在编译期间进行类型检查
}
```

2、泛型中为什么需要通配符类型？
3、通配符类型是干什么的？

## 泛型和列表

1、List不指明泛型时，是什么类型的？
```java
// 有什么问题？List存放的是Object类型的
// 下面代码可以正常运行
List list = new ArrayList();
list.add("A");
list.add("B");
list.add(100);

// 按照Object可以读取
for (Object o : list) {
System.out.println(o);
}
```

## 泛型/类型变量中的限定/约束

1、T extends Fruit
```java
// 1、限定了T只能是Fruit的子类
class Animal<T extends Fruit>{
public void showInfo(T info){}
}

Animal<Apple> animal = new Animal();
animal.showInfo(new Apple());
// animal.showInfo(new Fruit()); 错误！不可以用！

// 2、泛型的限定，用于方法
static class Animal<T extends Fruit>{
public <E extends Comparable>void showInfo(E a, E b){
a.compareTo(b); // 一定有compareTo方法
}
}
```
> 什么时候用到？
> 1. 需要确保泛型T是某类型的类，才能调用到特定的方法

2、泛型限定的注意点
1. `T extends XXX`` 可以是类，可以是接口
2. 类和接口，可以混用
3. 类需要在接口前面
4. 类只能有一个
5. `T,E extends A&B`，限定类型可以是多个（必须是同时实现类、接口、类&接口的类型）

## 泛型的约束和局限性

1. 不能实例化泛型。为什么不可以？
```java
T t = new T(); // 为什么不可以？构造方法可能是有参的，可能是无参的，没有办法确定是哪一个构造方法。
```
2. 静态Field字段、静态方法中不可以引用泛型（类型变量）。为什么不可以？
```java
class Person<T>{
//1、错误！静态字段/静态field不可以用泛型
//public static T instance = null;
}
// 2、静态方法中不可以使用泛型
//public static T showInfo(T info){
//    System.out.println(info);
//    return info;
//}
// 为什么？
// 静态字段:类变量，在类加载的准备阶段，分配内存并且设置零值，需要有明确的类型 ===> 类加载机制 // 补全：见下面类加载机制分为哪些阶段？
// 静态方法:类方法，
// 泛型在运行时才知道具体类型，在类加载的准备阶段，没办法知道具体类型。

// 3、静态方法本身可以是泛型方法！
public static <T> T showInfo(T info){
System.out.println(info);
return info;
}
```
3. 泛型不可以传入基本数据类型。为什么？ ===> 看深层次JVM是如何处理基本数据类型和引用类型的 ==> Kotlin中万物都是对象
> 泛型必须是引用类型，不可以用基本数据类型。必须要用对象
4. 泛型不可以使用 instanceof，编译器现在很智能，会提示错误。
```java
Person<String> p = new Person();
// 1、可以判断，一定是一样的
if(p instanceof Person<String>){
System.out.println("true");
}
// 2、报错，可以这么写
//if(p instanceof Person<Integer>){
//    System.out.println("false");
//}
```
5. 泛型擦除
```java
// 3、输出same。为什么？泛型擦除了，输出的都是`class Main$Person`，输出的都是原生类型 Person就是原生类型。
Person<String> p = new Person();
Person<Boolean> p2 = new Person();
if(p.getClass() == p2.getClass()){
System.out.println("same");
System.out.println(p.getClass());
System.out.println(p2.getClass());
}
```
6. 泛型数组，不可以定义
```java
// 1、泛型数组，可以声明
Person<String>[] personArray;
// 2、不可以！报错！不可以定义泛型数组
// Person<String>[] personArray = new Person<String>[10];
```
7. 泛型类不可以继承自Exception、Throwable
```java
// 泛型类 继承自 其他类
static class Animal<T> extends Fruit{
}
// 泛型类，不可以继承自Exception和Throwable！！
//static class Animal2<T> extends Exception{ }
```
8. try-catch不可以捕获继承自Exception、Throwable异常的泛型
```java
public <T extends Exception> void showInfo(){
try{
}catch (T exception){ // 不可以！！报错
}
}
```
9. 泛型可以作为异常抛出（继承自Exception）
```java
public <T extends Exception> void showInfo(T t) throws T {
try{

}catch (Exception exception){ // 可以将T作为异常抛出
throw t;
}
}
```


**类加载机制分为哪些阶段？**
> 加载
> 验证
> 准备：为类变量分配内存，并且设置初始值=零值（该阶段不包括实例变量 => 实例变量 在 对象实例化时 分配在堆内存中的）
> 解析
> 初始化
> 使用
> 卸载

## 通配符类型


# Java泛型补充问题

1、静态方法可以使用泛型吗？不可以使用属于泛型类的泛型，但是本身可以是泛型方法
2、泛型和异常有哪些知识点？【为什么？】 ===> 感觉涉及到JVM中关于异常和异常表相关的处理，异常的具体类型需要提前知道
1. try-catch不可以捕获extends Exception的泛型。为什么不可以？
> 泛型在运行时指定，catch捕获的异常，需要编译时就知道，并且生成在class文件中。
2. 可以把泛型异常，throw抛出。为什么可以？
```java
// throw抛出
    public static <T extends Exception> void showInfo(T t) throws T {
        try{
            throw new Exception();
        }catch (Exception e){
            throw t;
        }
    }
// 使用静态方法
    public static void main(String[] args) {
        try {
            showInfo(new MyException());
        } catch (MyException e) {
            e.printStackTrace();
        }
    }
    static class MyException extends Exception{}
// 为什么可以？
// 静态泛型方法，调用时需要指明具体类型，编译时编译器用具体类型进行了替换
```


# 泛型类型的继承规则

1、泛型类传入父类和子类，两者是独立的类型
```java
// P1和P2的关系是什么？完全是两个独立的类型，没有任何关系
Person<Fruit> p1 = new Person<>();
Person<Apple> p2 = new Person<>();
```

2、泛型类具有继承关系，参考ArrayList和List


# 通配符

1、在需要`Person<Fruit>`和`Person<Apple>`都可以使用的时候应该怎么办？
> 使用通配符
```java
// 注意！两者是不同的类型
//        Person<Fruit> p = new Person<Apple>();
// 用通配符，来适配。可以传入任何类
Person<?> all = new Person<Apple>();
all = new Person<Fruit>();

// 可以是Person<Fruit>也可以是Person<Apple>
Person<? extends Fruit> p = new Person<Apple>();
p = new Person<Fruit>();

// 可以传入Apple和Apple的父类
Person<? super Apple> a = new Person<Apple>();
a = new Person<Fruit>();
```

2、通配符的作用是什么？
> 解决，泛型类，传入父类和子类，会认为是不同类型的问题
> 类具有继承关系，不代表带入泛型类还有继承关系


3、使用这种方式能实现什么效果？
```java
// 问题：代码无法复用
// 可以打印水果列表
showInfo(new ArrayList<Fruit>());
// 不可以打印苹果列表
//        showInfo(new ArrayList<Apple>());
public static void showInfo(List<Fruit> list){
for (Fruit fruit : list) {
System.out.println(fruit);
}
}
// 解决一：通配符，作为Object进行打印
public static void showInfo(List<?> list){
for (Object o : list) {
System.out.println(o);
}
}

// 解决二：可以打印水果和所有子类的列表（可以确保是Fruit类，调用Fruit的相关方法）
public static void showInfo(List<? extends Fruit> list){
for (Fruit fruit : list) {
System.out.println(fruit);
}
}
```

4、通配符的限定/约束
> 需要使用通配符的场景（实参），但是需要打印
> extends限定上界
> super限定下界


5、`T extends Fruit`和`? extends Fruit`区别？
1. 前者用于泛型的限定/约束。指明泛型必须是某些类的子类或者父类。保证可以调用到需要的方法
2. 后者是通配符，用于泛型类的使用处，解决泛型传入父类和子类会认为是不同的类型的问题，实现代码的复用


6、`T super Fruit`是什么？
> 错误！不存在这种写法，只有`T extends Fruit`

7、Java设计时为什么设计泛型传入父类和子类会是不同的类型？


### 协变、逆变

1、`? extends Fruit`协变
1. 父类引用 = 子类对象
2. 只读，不可写
3. 生产者模式
```java
// 只读模式
List<? extends Fruit> list = new ArrayList<Apple>();
//        list.add(new Apple());
//        list.add(new Fruit());
//        list.add(new Object()); //可以是ArrayList<Apple>()、ArrayList<Fruit>、ArrayList<Orange>，添加的时候，如何知道具体是哪种列表呢？
// 如果是ArrayList<Apple>()，加进入的数据是Fruit()，肯定是不合适的
Fruit f = list.get(0);
//        Apple a = list.get(0); // 只能作为Fruit和Object读取
Object o = list.get(0);
```


2、`? super Apple`逆变
1. 子类引用 = 父类对象
2. 只写，不可读
3. 消费者模式
```java
// 只写模式
List<? super Apple> list = new ArrayList<Fruit>();
list.add(new Apple());
//        list.add(new Food());// 只能作为Apple和子类添加 // 假如是ArrayList<Fruit>()列表，添加Food()是有问题的，避免该场景，所以不能添加Apple父类
//        list.add(new Object());

// 不可以读取，特例作为Object读取
//        Fruit f = list.get(0);
//        Apple a = list.get(0);
Object o = list.get(0);
```


3、自己写类感受List和逆变、协变的关系
```java
// Person类

static class Person<T extends Apple>{
T data;
// 写入：参数是T
public void setData(T info){
System.out.println(info);
data = info;
}
// 读取：返回值是T
public T getData(){
return data;
}
}

// 使用：
// 只读
Person<? extends Apple> all = new Person<Apple>();
//        all.setData(new Apple());
//        all.setData(new Fruit());
//        all.setData(new Object());
Apple a = all.getData();
//        ChinaApple ca = all.getData(); // 只可以作为父类读取
Fruit f = all.getData();
Object o = all.getData();
```

4、为什么有协变、逆变的这些概念？为什么需要这些？
1. 协变，只读，保证可以安全的访问数据，不会被内鬼修改
2. 逆变，只写，


## 泛型擦除

1、T都会擦除为Object

2、T extends Number，都会擦除为 Number

3、T super Number，会擦除为什么？？

## JVM如何实现泛型？

1、JVM如何实现泛型？
> 泛型擦除
> C#中是真的泛型


