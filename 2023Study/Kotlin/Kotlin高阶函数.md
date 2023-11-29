

Lambda和高阶函数
---

本文链接：https://blog.csdn.net/feather_wch/article/details/132351175

[toc]


# lambda


1、lambda的由来
1. 单词"lambda"源于希腊字母λ（小写lambda）
1. "Lambda"一词最早起源于数学中的λ演算（lambda calculus），它是一种函数定义和函数应用的形式系统，由逻辑学家阿隆佐·邱奇（Alonzo Church）在20世纪30年代发明。
1. 邱奇使用λ作为演算中的一个操作符，用于表示匿名函数。他选择使用希腊字母λ的原因是因为它在字母表中的位置不太常见，这样可以避免与其他符号混淆。

2、函数的声明
```kotlin
    // 函数的声明
    val method01:()->Unit
    val method02:(Int, Int)->Unit
    val method03:(String, Double)->Any // 相当于Object
    val method04:(String, Double, Float)->Boolean

```

3、kotlin中Any和Java的Object有什么区别吗？
1. Any是所有非空类型的超类型，类似于Java中的Object。Any不能持有null。
1. Any?是所有类型的超类型，包括可空类型。
Any?可以持有null值

3*【补充】Kotlin中Any和Java中Object的区别是什么？
```java
1.类型系统的差异： 
// Kotlin 中的 Any
val anyValue: Any = "Hello, World!" //非空

// Java 中的 Object
Object obj = "Hello, World!"; // 可以为null

2.空安全性：
// Kotlin 中的 Any 不包括 null
val anyValue: Any = "Hello, World!"

// Kotlin 中的 Any? 可以包括 null
val nullableAny: Any? = null

3.类型检查和转换：
// Kotlin 中的类型检查和转换
val anyValue: Any = "Hello, World!"

if (anyValue is String) { // is进行类型检查和转换
    val length: Int = anyValue.length
}

// Java 中的类型检查和转换
Object obj = "Hello, World!";

if (obj instanceof String) {
    String str = (String) obj; // 需要强制类型转换
    int length = str.length();
}
```

4、函数如何调用/函数的实现（lambda）？invoke是什么？
```kotlin
// 函数变量通过invoke()调用
// ()是运算符重载
// 函数的实现
val method01:()->Unit
method01 = { println("我实现了method01") }
method01() // 调用函数：操作符重载
method01.invoke() // 调用函数：真实方法


// 方法三的实现
val method03:(String, Double)->Any // 相当于Object
method03 = {name, number ->
    println("$name $number")
    name + number
}
println(method03("wch", 1234567.89))
```

5、函数的实现（传入函数）
```kotlin
val method04:(String, Double, Float)->Boolean
    = fun(name:String, number:Double, age:Float):Boolean = (name+number+age).isEmpty()
method04("wch", 376.23, 1234.5f)
```

6、单一参数的lambda默认持有it ===> 函数式接口 ===> SAM
```kotlin
    val method05:(Int)->Unit = {
        print("$it")
    }
```

7、下划线可以拒收 ===> 拒收
```kotlin
    val method06:(Int, Int)->Unit = { _, number->
            println("$number")
    }
```

8、作用是什么？
1. 节省空间
1. 接口版本变化，有的参数没用了

9、想要允许参数为null，需要用可空类型如String?
```kotlin
val method07:(String?,String)->Unit = {
        sex, name -> println("$sex,$name")
    }

method07(null, "wch")
```

10、Lambda不可以使用泛型作为参数类型
11、Lambda参数不可以给默认值 ===> 默认参数
12、Lambda Any->Any
```kotlin
    // 传入什么，打印什么，还可以返回任何东西
    val method18:(Any)->Any={
        println("$it")
        it // 还可以返回自己
    }
```

13、Lambda配合扩展方法 ===> 扩展方法 ===> 官网写的Funciton，但是接收receiver
```kotlin
    val method19: String.()->Unit = {
        // this = String本身 == 调用者本身
        println("你是$this")
    }
    "WCH".method19()
```

14、为什么method19可以成为String的扩展方法？
1. 代码`val method19: String.()->Unit = { ... }`表示定义了一个接收者类型为`String`的扩展函数类型。
1. `String.()->Unit`表示该函数类型接收一个`String`作为接收者，并返回`Unit`类型（即没有返回值）。
1. 进一步理解：==> 匿名扩展函数
```kotlin
val method18: ()->Unit; // 类型是 函数
val method19: String.()->Unit // 类型是 String的扩展函数
val method20: (String)->Unit // 类型是 函数，该函数的参数是String
```

15、进一步融合this和it，区分扩展函数 和 参数的区别
```kotlin
val method20: Int.(Int) -> String = {"两数相加的结果:${this+it}"}
println(1.method20(10))

println(method20(1, 10)) // 1, 可以自动插为第一个参数
```
函数的形式：
```kotlin
fun Int.method21(n:Int):String{
    return "两数相加的结果:${this+n}"
}
println(2.method21(22))
// println(method21(2, 22)) // 不可以这样写了
```

## 输出（返回类型）

1、Lambda的返回类型：函数
```kotlin
    /**============================
     * 函数
     *============================*/
    // 默认Unit
    fun t01(){ println() }
    // 默认Unit
    fun t02(){4652342.5f}
    // 默认Unit
    fun t03(){"Hello!"}
    // String：显式指明返回值
    fun t04():String{return "feather"} // return 还不支持自动推断类型
```

2、函数返回函数
```kotlin
    /**==============================
     * 函数返回函数
     *=============================*/
    // ()->Unit
    fun s01() = {}
    fun s02() = { println("Haha") }
    s02()() // 输出

    // Boolean
    fun s03() = run{ true }// 返回的是代码块的最后一行
    s03()
    // ()->String
    fun s04():()->String = {"Hello"}
    println(s04()) // Function0<java.lang.String>
    println(s04()())
```

3、Java中Lambda是假的

## 深入探究
4、Lambda深入探究
```kotlin
    // k01()返回的类型是: (Int)->Unit
    fun k01() = {n:Int -> println(n) }
    k01()(123)

    // lambda使用，第一种用的多
    val methodx2 = {str:String -> str.length}
    val methodx2s:(String)->Int = {it.length}
```

5、Kotlin的Lambda如何和Java兼容？源码机制
1. kotlin编译器实现(很强大) -> JVM字节码
1. `package kotlin.jvm.functions`中定义了Function系列
1. 最多Funciton22，高版本编译器可以处理>22个参数的情况，低版本会出错
```java
// val methodx2 = {str:String -> str.length}
Function1 methodx2 = (Function1)null.INSTANCE;
```
```kotlin
// (String)->Unit
val method3:Function1<String, Unit> = { println(it) }
```

6、Lambda考题
```kotlin
    //(Int,Int) -> (Int,Int) ->String
    val funX10 = fun(n1:Int,n2:Int):(Int,Int)->String={n1,n2 -> "两个数相加:${n1 + n2}"}
    println(funX10(10, 10)(20, 20))

    // 迷惑点，最外层的n1和n2和内层的n1 n2没关系
```
```kotlin
// 函数的函数的函数的函数
    val k01: (String) -> (Boolean) -> (Int) -> (Float) -> Double =
        { it ->
            { it ->
                { it ->
                    { it ->
                        123.456
                    }
                }
            }
        }
    println(k01("AAA")(true)(45)(67.89f))
```

7、下面的study02()返回的类型是什么？
```kotlin
fun study02() = {lambda:(Int, Int) -> String, studyInfo: String ->
    lambda(1, 99)
}
// 答案：((Int, Int) -> String, String) -> Unit 

// 使用：
study02()({n1, n2-> "$n1 + $n2 = ${n1 + n2}" }, "wch")
```
8、下面study04()返回的类型是什么？
```kotlin
fun study04() = {str:String, num:Int, lambda1:(String)->Unit, lambda2:(Int)->Boolean->
    lambda1(str)
    lambda2(num)
}
// 答案：(String,Int,(String)->Unit,(Int)->Unit)->Boolean

// 使用：
println(study04()("wch", 123, { println("$it lambda1") }, { it > 99}))
```

### 泛型

9、下面返回的类型是什么？
```kotlin
fun <T1, T2, R1, R2> study05() = {str:T1, num:T2, lambda1:(T1)->R1, lambda2:(T2)->R2 ->
    lambda1(str)
    lambda2(num)
}
// (T1, T2, (T1)->R1, (T2)->R2) -> R2

// 使用：
study05<String, Int, Boolean, Float>()("wch", 22, {it.isEmpty()}, {it.toFloat()})
```

10、下面的study06不是lambda，是函数。他们的类型是什么？
```kotlin
fun study05() = {Str:String, num: Int, lambda:(Int)->Boolean ->
    lambda(num)
}
// (String, Int, (Int)->Boolean)->Boolean

fun study06() = fun(Str:String, num: Int, lambda:(Int)->Boolean):Boolean{
    return lambda(num)
}
// (String, Int, (Int)->Boolean)->Boolean
```
1. Lambda表达式，最后一行作为返回值
1. 函数，最后一行不能作为返回值
  1. 必须显式return
  2. 必须显式指定

**函数有隐式的Unit类型返回值**

## inline原理探究

1、Lambda为什么要内联？
1. 不使用内联，会构造出Function0对象，作为参数传入
1. 代码内联，减少方法调用开销，不再需要创建Function0对象 ===> 内存抖动
```kotlin
fun main() {
    show{
        println("Hello Kotlin!")
    }
}
fun show(lambda:()->Unit){
    lambda()
}
// 不使用内联生成代码：
show((Function0)null.INSTANCE);
public static final void show(@NotNull Function0 lambda) {
    Intrinsics.checkNotNullParameter(lambda, "lambda");
    lambda.invoke();
}
// 使用内联：
int $i$f$show = false;
int var1 = false;
String var2 = "Hello Kotlin!";
System.out.println(var2);
```


# 高阶函数

===> Compose内部实现，学习

1、高阶函数是什么？高阶函数 = lambda + 函数
```kotlin
fun a() {}
val a1 = {} // 函数引用，接收，匿名函数
val a2 = a1 // 函数引用
val a3 = ::a // 将函数变成函数引用
```

2、高阶函数就是函数的函数，函数中有lambda
```kotlin
// Lambda开胃菜
    // 返回String
    fun show01(number:Int, lambda:(Int)->String) = lambda.invoke(number)
    // 调用函数
    var r01 = show01(99){
        it.toString()
    }

    // return Int
    fun show02(n1:Int,n2:Int,n3:Int, lambda: (Int,Int,Int)->Int) = lambda(n1, n2, n3)
    show02(10, 20, 30){
        i, i2, i3 ->
            i + i2 + i3
    }
```

3、高阶函数例子
```kotlin
// 第一版，高阶函数应用
fun main() {
    loginEngine("wch", "123456")
}

private fun loginEngine(name:String, pwd:String){
    loginServer(name, pwd){
        code, msg->
            print("错误码$code 错误信息$msg")
    }
}

private fun loginServer(name:String, pwd:String, lambda:(Int, String)->Unit){
    if(name.isEmpty() || pwd.isEmpty()){
        lambda(-1, "Empty")
        return
    }
    lambda(1, "Success")
}
// 第二版
```

4、高阶函数
```kotlin
// （一）给泛型增加匿名扩展函数
fun<T> T.myRun01(block: T.(Float) -> Boolean) = block(123.45f)
// this = T本身 = 调用者本身 == Derry

// 使用
"Derry".myRun01 {
    isEmpty()
}

// （二）
fun<T> T.derry4(number:Double, mm: T.(Double) -> Unit){
    mm(number)
}
"Derry".derry4(123.456){
    // this = 调用者
    println(this)
}

// （三）
fun<T> T.myRunPlus(block: T.(T, T) -> Boolean) = block(this,this)
```

5、T.() -> Boolean s是什么意思？
1. 对T扩展出匿名函数
1. 匿名函数是 ()->Boolean
1. 该匿名扩展函数只有这个高阶函数可以使用，其他地方用不出来

6、多个lambda调用
```kotlin
// (一)
show2(lambda1 = {}, lambda2 = {})
//（二）
show2({}, {})
// (三）
show2({println("Hello")}){
    println("World!")
}
```

7、源码使用高阶函数，利用函数引用
```kotlin
show(::lambdaImpl)

fun lambdaImpl(){
    println("HAHA")
}

// 函数引用场景
fun lambdaImpl(name:String):Unit{
    println(name)
}
var r1:Function1<String, Unit> = ::lambdaImpl
var r2:(String)->Unit = ::lambdaImpl
var r3:String.()->Unit = ::lambdaImpl // (String)等价于String.()
```

## 集合、泛型

1、**Lambda+集合+泛型**
```kotlin
class AndroidClickListener<T>{
    // 1. 集合的元素类型是Lambda，并且Lambda输入参数的类型是 T
    val actions = arrayListOf<(T)->Unit>()
    val actions2 = arrayListOf<(T?)->Unit>() // 可空
    
    // 2. 集合的元素类型为泛型
    val values = arrayListOf<T?>()

    // 3、设置监听
    fun addListener(value:T?, lambda:(T?)->Unit){
        values += value // 运算符重载
        actions2 += lambda
    }
    // 4、通知观察者
    fun notify(value:T?){
        val index = values.indexOf(value)
        actions2[index].invoke(value) // 执行方法
        actions2[index](value) // 执行方法二
    }
    // 5. 模拟点击事件，通知所有观察者
    fun touchListeners(){
        actions2.forEachIndexed{
            index, function ->  
                function(values[index])
        }
    }
}

// 使用，测试
    val click = AndroidClickListener<String>()
    click.addListener("HaHa"){
        println("接收到数据：$it")
    }
    click.addListener("WCH"){
        println("$it 在吃饭中...")
    }
    click.addListener("Feather"){
        println("百万博客主：$it")
    }
    click.touchListeners()


// 函数引用版本
    fun method(value:String?){
        println(value)
    }
    click.addListener("Hello", ::method)
```

2、如何用变量接收类型中包含泛型的函数？
```kotlin
fun<T> method(value: T?):Unit{
    println(value)
}
    // 不可以用泛型
//    val error:(T?)->Unit = ::method
    // Any来代表T?
    val m1:(Any)->Unit = ::method
    m1("Hello")
    // 具体类型也可以
    val m2:(Int)->Unit = ::method
    m2(123)
```

## 自己实现Kotlin内置函数

1、forEach
```kotlin
// 定义
inline fun<E> Iterable<E>.mForEach(lambda:(E)->Unit){
    for(item in this)lambda(item)
}

// 使用
listOf("AAA", "BBB", "CCC")
    .mForEach{
        println(it)
    }
```

2、let和run，为什么会有this，为什么会有it
1. this，it
1. T.()->R, this是对T的匿名扩展函数，拥有this
1. (T)->R，it是参数类型，SAM单一抽象接口，函数式接口，it
```kotlin
public inline fun <T, R> T.run(block: T.() -> R): R {
    return block()
}
public inline fun <T, R> T.let(block: (T) -> R): R {
    return block(this)
}
```

3、thread
1. 注意点: corssinline
```kotlin
// 自己实现thread，特定lambda不能inline
inline fun thread(start:Boolean = true,
                  name:String?=null,
                  crossinline runAction:()->Unit // 限制不能inline，会copy大量代码
):Thread{
    val thread = object:Thread(){
        override fun run() {
            super.run()
            runAction()
        }
    }
    if(start) thread.start() // 
    name?.let { thread.name = it } // kotlin形式
    return thread
}
```

# 扩展函数原理

1、扩展函数就是构造static final方法，将对象作为返回值
```kotlin
class MyKt {}
fun MyKt.show() = println(this)
```
```java
public final class MyKt {}
// 构造出Kt类，添加扩展函数名一样的static final方法，将类的对象作为参数
public final class MyKtKt {
   public static final void show(@NotNull MyKt $this$show) {
      Intrinsics.checkNotNullParameter($this$show, "$this$show");
      System.out.println($this$show);
   }
}
```

2、思考：叫做扩展函数是否是因为没有receiver？

# companion object 原理 ==> 静态内部类

1、为什么叫伴生对象？
1. 生成静态内部类
1. 生成静态成员变量（类变量）
1. 通过伴生对象实例，调用方法和获取字段

2、使用处
```kotlin
class MyKt {
    companion object{
        fun show(){
            println(this)
        }
        val name = "wch"
    }
}

fun main() {
    MyKt.show()
    MyKt.name
}
```

3、反编译，生成源码
1. 生成静态内部类，Companion，并且有show()方法和字段name
1. 类中Companion实例，是static final变量
1. 像Java一样调用类方法和获取类属性，本质通过Companion伴生对象，调用方法和get字段
```java
public final class MyKt {
   private static final String name = "wch";
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   public static final class Companion {
      public final void show() {
         System.out.println(this);
      }

      public final String getName() {
         return MyKt.name;
      }

      private Companion() {
      }
      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}

// 使用处
MyKt.Companion.show();
MyKt.Companion.getName();
```

# 函数式编程

Kotlin函数式编程
1. 链式调用
1. 非常丰富的函数库
1. 模仿RxJava的响应式编程
