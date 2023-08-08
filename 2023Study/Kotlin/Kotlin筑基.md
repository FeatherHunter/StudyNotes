
# Kotlin筑基

[本文链接](https://blog.csdn.net/feather_wch/article/details/131972206)

核心思路：每个知识点都要和源码结合起来讲。

[TOC]

## 函数和方法
1、inline fun是什么？
> 内联函数

2、inline fun<reified T> ===> Java泛型擦除
1. 内联函数 + 具体化的泛型
2. reified可以解决泛型擦除问题，拿到泛型的class对象
3. 原理：方法内联，编译器直接拿到目标的class对象

3、泛型擦除是什么？
1. 低版本泛型被擦除
2. 高版本泛型没有擦除，可以通过signature拿到泛型类型（多一层转换）

4、函数和方法的区别？
1. 函数：根据输入，执行任务，输出结果
2. 方法：类和对象的行为

5、函数不是方法
1. 他们的类型是完全不同的
2. 函数没有receiver
3. 方法有receiver，是第一个参数

6、函数和方法在Kotlin中的比较  ===> Java
```kotlin
// 函数的类型
fun func1():Unit{}
val val1: ()->Unit = ::func1
// 方法的类型
class Student{
    fun func2():Unit{} // 无参数
    fun func3(name:String):Unit{} // 有参数
}
// 都有receiver
val val2:Student.()->Unit = Student::func2
val val2_2:(Student)->Unit = Student::func2 // Student可以自由进入
val val2_3 = Student::func2 // KFunction1<Student, Unit> // 1 = 实际数量2 - 1
// 都有receiver
val val3:Student.(String) -> Unit = Student::func3
val val3_2:(Student, String) -> Unit = Student::func3
val val3_3 = Student::func3 // KFunction2<Student, String, Unit>
// 函数，携带参数
fun func4(name:String):Unit{}
val val4:(String)->Unit = ::func4
val val4_2 = ::func4 // KFunction1(String, Unit) 2 - 1 = 1
```
7、Java中不存在函数，只有方法
1. Java和Kotlin的本质区别
2. Java对象中的方法，都有this = Kotlin中receiver

8、Java中类方法/静态方法是函数吗？
1. 是方法，Receiver是class对象
2. 不是函数，没有函数这个概念

9、函数本身是对象吗？
1. 函数引用就是函数对象
2. 函数是一等公民

### KFunction
1、KFunction是什么？
1. 是函数的抽象表示。
2. Kotlin 反射库的一部分，用于在运行时操作和访问函数的信息。
3. 通过 KFunction 类型，可以使用反射来调用函数、获取函数的签名信息等操作。
4. KFunction 类型是泛型的，具体取决于函数的参数和返回类型。例如，KFunction1 表示具有一个参数的函数，KFunction2 表示具有两个参数的函数，以此类推。

2、KFuncton的使用
```kotlin
fun greet(name: String) {
    println("Hello, $name!")
}
fun main() {
    val function: KFunction<Unit> = ::greet
    function.call("John")
}
```

### 匿名函数

1、匿名函数
```kotlin
    // 匿名函数
    val mx1 = fun(str:String):Int{
        return str.length
    }
```

## 属性和变量

1、属性和变量也有Receiver之分
```kotlin
// 变量
var name = "Derry"
// 属性
class Student{
    var name = "Derry"
}
//
fun main() {
    // 变量，无receiver
    ::name.set("John")
    ::name.get()
    // 属性，有receiver
    Student::name.set(Student(), "John")
    Student::name.get(Student())
}
```

2、::代表引用，invoke代表()括号


## 编译时常量

1、const val PI = 45 编译时常量不可以用于局部变量,为什么？
>函数之内必须在运行时赋值，不符合编译时常量

2、编译时常量（compile-time constants）有什么用？
1. 提高性能：编译时进行常量折叠（constant folding），避免在运行时进行重复计算。
2. 减少错误和提高可读性：减少人为错误的可能性，会在编译时验证。可以提高代码的可读性和可维护性。
3. 编译时配置：定义应用程序的版本号、构建类型或其他配置参数，从而在编译期间对应用程序进行不同的处理。
4. 优化资源使用：可以在代码中指定资源的路径，避免在运行时进行资源查找和加载的开销。

## 基本类型
3、Kotlin基本类型也是引用类型，会不会耗费性能？不会，都转为基本类型了
1. Int Double 引用类型 ，反编译后，Java的基本类型 int double
1. Int? Double? = null，反编译后，Java的包装类型 Integer Double

## range
4、range表达式
```kotlin
n in 0..10
n !in 0..10
```

## 访问权修饰符
5、函数默认public，其他修饰符
1. private、protected
1. internal：同一module可见
1. protected internal：同一模块或者子类可见
1. internal可以提高代码封装性

## Unit
6、默认返回Unit可以不写，kt中Unit是单例类

## Nothing
7、Nothing类型是什么？
1. TODO("抛出异常，结束当前程序")，这不是注释
1. TODO()返回的类型是Nothing

## 反引号
8、反引号是什么？
```kotlin
private fun `登录功能20230727环境测试功能`(name:String){
    println(name)
}
```

9、kt中in和is是关键字，想要函数名为in，is怎么办？反引号
```kotlin
fun `in`(){}
`in`() // 调用
```

10、反引号可以用于函数加密,公司内部有文档
```kotlin
private fun `9867693746293234`(name:String){
    // xxx
}
```

## 函数内联
11、函数内联 - 有lambda作为参数就需要内联inline
1. 不内联，在调用端会生成函数对象（Function2等）来调用
1. 使用内联，不会有额外性能损耗，但是代码量很大的函数复制多处会导致代码体积增大
1. 内联相当于C++的#define 宏替换
```kotlin
fun main(args: Array<String>) {
    login("wch"){
        println(it)
    }
}

// 此处lambda是一个参数，因此生成Function1的对象
// （String,String）-> Unit 会生成Function2
inline fun login(name:String, lambda:(String)->Unit){
    lambda(name)
}
```

### noinline

1、noinline的作用
1. 内联会导致参数不再是对象，要当做对象使用，使用noinline避免参与内联
1. 可以作为对象返回
```kotlin
inline fun fun01(noinline lambda:(Int)->Unit):(Int)->Unit{
    return lambda
}
```

### crossinline

1、return难题，不内联的函数不允许直接return
```kotlin
fun method(lambda:(Int)->Unit){
    lambda(123)
}

fun main() {
    method { 
        return // 不可以
        return@method // return method
    }
}
```

2、内联函数的return，因为平铺会直接return外层的方法
1. 可以直接return，会导致外层的main直接结束
```kotlin
inline fun method(lambda:(Int)->Unit){
    lambda(123)
}
fun main() {
    method {
        return@method // return method
        return // 会return main()
    }
}
```

3、内联函数调用函数，无法保证内部都是内联函数，增加crossinline
1. 告诉IDE强制检查，有没有return，有return会直接不让编译
```kotlin
fun main() {
    method {
        return // 会return main()
    }
}
inline fun method(lambda:(Int)->Unit){
    methodNoInline{
//        lambda(123) // 无法确保。methodNoInline是内联函数。
    }
}

fun methodNoInline(lambda:(Int)->Unit){
    lambda(456)
}
```
1. 增加corssinline，不允许调用return
```kotlin
fun main() {
    method {
//        return // xxxxxxxx会报错：无法这里调用return
    }
}

inline fun method(crossinline lambda:(Int)->Unit){
    methodNoInline{
        lambda(123) // 无法确保。methodNoInline是内联函数。
    }
}

fun methodNoInline(lambda:(Int)->Unit){
    lambda(456)
}
```

## 函数引用
12、函数引用的例子
```kotlin
fun main(args: Array<String>) {
    var method = ::login
    method("wch"){
        println(it)
    }
}

inline fun login(name:String, lambda:(String)->Unit){
    lambda(name)
}
```

13、函数可以作为返回值，有什么用？
1. **延迟计算**：
```kotlin
fun calculateResult(): () -> Int {
    val result = 10 // 假设这是一个复杂的计算过程
    return { result }
}

val delayedResult = calculateResult()
val result = delayedResult() // 只有在需要时才执行计算
```
2. **策略模式**：
```kotlin
enum class SortOrder {
    ASCENDING, DESCENDING
}

fun getSortComparator(order: SortOrder): (List<Int>) -> List<Int> {
    return when (order) {
        SortOrder.ASCENDING -> { list -> list.sorted() }
        SortOrder.DESCENDING -> { list -> list.sortedDescending() }
    }
}

val numbers = listOf(4, 2, 7, 1, 5)
val sortOrder = SortOrder.ASCENDING
val sortedNumbers = getSortComparator(sortOrder)(numbers) // 根据排序顺序返回不同的函数实现
```
3. **装饰器模式**：
```kotlin
fun performOperation(): () -> Unit {
    return {
        // 执行原始操作
        println("Performing the original operation")
    }
}

fun withLogging(originalFunction: () -> Unit): () -> Unit {
    return {
        println("Logging before operation")
        originalFunction()
        println("Logging after operation")
    }
}

val operation = performOperation()
val decoratedOperation = withLogging(operation) // 包装原始函数，添加日志记录的功能
decoratedOperation() // 执行装饰后的操作，会在控制台输出相关日志
```

## 具名函数
14、具名函数有什么用？
```kotlin
fun printResult(result:String){
    println(result)
}
login("wch", ::printResult) //用符号引用调用具名函数
```

## 判空和安全调用
15、安全调用操作符
```kotlin
    var name: String? = "feather"
    name = null
    name?.capitalize()
```
!!
> 确保是有数值的才能用，这个和java一样，有风险


16、let的安全调用
```kotlin
    name?.let{
        it.xxx() //不为null才会执行
    }
```

## 断言操作、空合并

17、非空断言、if判断
1. 为null也会执行，和Java一样。百分百确定有值，可以使用断言。
```kotlin
name!!.capitalize()
//or
if(name != null){
   name.capitalize() 
}
```

18、空合并 ?:
```kotlin
var name:String? = "李小龙"
name = null
println(name ?: "原来你是鼎鼎大名null")

// ?: 前面为空，才执行
// 如果name = null会执行 ?: 后面的
```
let + ?:
```kotlin
//let + ?:
var name:String? = "李小龙"
name = null
println(name?.let { "我的名字是$it" } ?: "原来我是鼎鼎大名null")
```

## 异常处理

19、自定义异常
```kotlin
fun main(args: Array<String>) {
    throw CustomException()
}
// 自定义异常
class CustomException : IllegalArgumentException("illegal")
```

## 先决条件
```kotlin
checkNotNull(value)
requireNotNull(value)

var value = false
require(value) // false 会抛出异常
```

## subString
```kotlin
subString(0, index)
subString(0 until 10)
// 两者等价
```

## split
```kotlin
val list = text.split(",") // 分割成List
println(list)
// C++ 和 Kt都有解构
val (v1, v2, v3, v4) = list

// 不接受第一个数据，反编译不会get(0)，节省性能
val (_, v2, v3, v4) = list
```

### 解构
在 Kotlin 中，解构（Destructuring）是一种将复合数据结构（如类、数据类、数组、集合等）的多个成员拆分为单个变量的技术。：
1. **解构声明**：解构一个数据类的属性：
```kotlin
data class Point(val x: Int, val y: Int)

val point = Point(10, 20)
val (x, y) = point // 解构 Point 对象为两个独立的变量
println("x: $x, y: $y") // 输出 x: 10, y: 20
```
2. **解构函数返回值**：一个函数返回多个值：
```kotlin
fun getUser(): Pair<String, Int> {
    return Pair("John Doe", 25)
}

val (name, age) = getUser() // 解构函数返回的 Pair 对象
println("Name: $name, Age: $age") // 输出 Name: John Doe, Age: 25
```
3. **解构数组和集合**：对于数组和集合，解构可以将其元素拆分为单独的变量。
```kotlin
val numbers = arrayOf(1, 2, 3, 4, 5)
val (first, second, *rest) = numbers // 解构数组元素为单独的变量
println("First: $first, Second: $second, Rest: $rest") // 输出 First: 1, Second: 2, Rest: [3, 4, 5]

val list = listOf("apple", "banana", "cherry")
val (fruit1, fruit2) = list // 解构列表元素为单独的变量
println("Fruit 1: $fruit1, Fruit 2: $fruit2") // 输出 Fruit 1: apple, Fruit 2: banana
```

#### 高级技巧

1. **解构 lambda 参数**：我们可以在 lambda 表达式中使用解构来获取函数参数的多个部分。例如：

```kotlin
val person = mapOf("name" to "John", "age" to 30)

person.forEach { (key, value) ->
    println("Key: $key, Value: $value")
}
```

2. **解构过滤和映射**：我们可以在解构过滤器和映射函数中使用解构来处理集合元素。例如：

```kotlin
data class Product(val name: String, val price: Double)

val products = listOf(
    Product("Apple", 1.99),
    Product("Banana", 0.99),
    Product("Cherry", 2.49)
)

val (expensiveProducts, cheapProducts) = products.partition { (_, price) -> price > 1.0 }

println("Expensive Products: $expensiveProducts")
println("Cheap Products: $cheapProducts")
```

3. **解构数据结构嵌套**：如果数据结构嵌套，我们可以使用嵌套的解构来访问内部对象的属性。例如：

```kotlin
data class Person(val name: String, val age: Int, val address: Address)
data class Address(val street: String, val city: String)

val person = Person("John Doe", 30, Address("123 Main St", "City"))

val (name, _, (street, city)) = person

println("Name: $name")
println("Street: $street, City: $city")
```

#### partition

`partition` 是用于将集合元素拆分为满足某个条件和不满足该条件的两个集合的函数。它返回一个包含两个集合的 `Pair` 对象，其中一个集合包含满足条件的元素，另一个集合包含不满足条件的元素。
```kotlin
val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

val (evenNumbers, oddNumbers) = numbers.partition { it % 2 == 0 }

println("Even Numbers: $evenNumbers") 
println("Odd Numbers: $oddNumbers")

输出结果为：
Even Numbers: [2, 4, 6, 8, 10]
Odd Numbers: [1, 3, 5, 7, 9]
```

## replace
```kotlin
val r1 = sourcePwd.replace(Regex("[AKMNO]")){ //Regex 中 写正则表达式
    it.value // 啥也没做
    // 可以加逻辑，把AKMNO替换成需要的内容
}

// 用正则表达式Regex进行替换，可以加密和解密
val newName = name.replace(Regex("[ADG]")){
    when(it.value){
        "A"->"1"
        "D"->"2"
        "G"->"3"
        else->it.value
    }
}
```

## == 和 === 区别
```kotlin
== 内容的比较，等价于 equals
=== 引用的比较
val name1 = "Derry"
val name2 = "Derry"
name1 === name2 
结果：
true // 字符串常量池同一个，是true
```
```kotlin
val name1 = "Derry"
val name2 = "derry".capitalize() // "Derry"
name1 === name2 
结果：
false // name2会生成另一个Derry，和常量池原先的Derry是两个对象
```

## 数字安全转换
```kotlin
val number:Int = "666".toInt()
val number:Int = "666.6f".toInt() // 会报错
val number:Int? = "666.6f".toIntOrNull() // 不会报错为Null
```

## Double 转为 Int
```kotlin
64.55.toInt() // 四舍五入
64.55.roundToInt() // 四舍五入

// 保留小数点
"%.3f".format(65.742355) // %是格式化字符串的标志，%.3f小数点后三位
```

## 内置函数

### apply

```kotlin
"feather".apply{
    // 直接this拿到 feather，可以直接调用 String的方法
    toLowerCase()
    this[length - 1]
}
```
适合链式调用: 例如文件解析
```kotlin
file.apply{
    setExecutable(true)
}.apply{
    setReadable(true)
}.apply{
    // xxx
}
```
apply设置对象的成员变量，run也可以这么做（都有this）
```kotlin
class Configuration {
    var host: String = ""
    var port: Int = 0
    var timeout: Int = 0
}

val config = Configuration().apply {
    host = "example.com"
    port = 8080
    timeout = 5000
}

```
apply源码：T.() 拥有this
```kotlin
public inline fun <T> T.apply(block: T.() -> Unit): T {
    block()
    return this // return this，用于链式调用
}
```

## let
let源码： (T) 拥有it
```kotlin
public inline fun <T, R> T.let(block: (T) -> R): R {
    return block(this)
}
```
使用：
```kotlin
val r = a?.let {
     // 能执行到这里it一定不为null
    if(it.isBlank()){
        "DEFAULT"
    }else{
        it
    }
}
```

### run
```kotlin
info.run{ // this
    "hello" // 最后一行返回
}
```
run的源码: T.() 拥有this
```kotlin
@kotlin.internal.InlineOnly
public inline fun <T, R> T.run(block: T.() -> R): R {
    return block()
}
```

### with
with和run的使用方法不同，其他一模一样
```kotlin
with("wch"){      
}
// T.() 拥有this
public inline fun <T, R> with(receiver: T, block: T.() -> R): R {
    return receiver.block()
}
```

#### run和with区别

1. with适用于在特定上下文对对象进行操作
1. run适合链式操作


### also
also源码：(T) 拥有it
```kotlin
public inline fun <T> T.also(block: (T) -> Unit): T {
    block(this)
    return this
}
```

### takeif
true时，返回对象本身，否则返回null
```kotlin
public inline fun <T> T.takeIf(predicate: (T) -> Boolean): T? {
    return if (predicate(this)) this else null
}
```


### takeUnless
false时，返回对象本身，否则返回null
```kotlin
public inline fun <T> T.takeUnless(predicate: (T) -> Boolean): T? {
    return if (!predicate(this)) this else null
}
```

### 总结
```kotlin
let = block(this) // it
apply block() = this   // this // 返回自身，链式
also block(this) = this // it // 返回自身，链式
with = receiver(this)  // this
run = block() // this
takeIf = precidate(this) // it
takeUnless = !precidate(this) // it
```

## 集合
### listOf

list的注意点
1. listOf(xxx)可以获取list
1. list[0] 正常取值
1. kt特点：取值避免异常
```kotlin
    list.getOrElse(index){"数组越界了"}
    list.getOrNull(index)
```

为什么是不可变的？==> 协变
```
List<out E>
是生产者，只能取数值。
```

### mutableListOf

```kotlin
val list = mutableListOf(1, 2, 3, 4, 5)
list.toList() // 转为不可变集合

val list = listOf(1, 2, 3, 4, 5)
list.toMutableList() // 转为可变集合

list += 99 // 运算符重载，+= -=
list.removeIf{ true } // 自动遍历全部移除
list.removeIf { it%2 == 0 } // 移除符合条件的数据
```

### 遍历
```kotlin
for(i in list){
    //111
}
list.forEach{
    //222
}
// 第三种方式
list.forEachIndexed{ index: Int, item: Int ->  
    print("$index $item,")
}
```

### Set：元素不重复
```kotlin
val set = setOf("a", "cd", "xsw")
set.elementAt(3) // 可能越界
set.elementAtOrElse(3, {"Set越界咯"})
set.elementAtOrNull(3)
// 配合 空合并
println(set.elementAtOrNull(3) ?: "为null")
```
```kotlin
// 可变Set
val set = mutableSetOf<String>("a", "cd", "xsw")
```

### 集合转换(去重)、快捷函数
```kotlin
val list = mutableSetOf<String>("a", "cd", "xsw")
list.toSet() // 去重
list.toSet().toList() // 去重
list.distinct() // 快捷去重，内部先转为Set再转为List
```

### 数组

数组的创建
1. intArrayOf，xxx，arrayOf 对象数组
1. list.toCharArray list转为数组

### Map
创建、读取、遍历
```kotlin
    // 创建Map，两者等价
    val map = mapOf("Derry" to (13.4), "wch" to (38.9)) // 前者key，后者value
    val map2 = mapOf(Pair("wch", 20), Pair("feather", 37)) // 源码中

    // 读取Map，五中方式
    map["wch"] // kt方式，返回null
    map.get("xwd") // java方式，返回null
    map.getOrDefault("feather", 37.8) // 默认值保护
    map.getOrElse("khw") { 98.2 }
    map.getValue("zas") // 会崩溃！！！不要用

    // 遍历
    // 第一种
    map.forEach{ // it = Entry
        it.key
        it.value
    }
    // 第二种
    map.forEach{ key,value -> println("$key $value") }
    // 第三种: 解构
    map.forEach{ (k,v) -> println("$k $v") }
    // 第四种:in
    for(item in map){

    }
```
可变Map: getOrPut
```kotlin
// 存在取出，不存在存入
    val map = mutableMapOf("Derry" to (13.4), "wch" to (38.9)) // 前者key，后者value
    map.getOrPut("fff") { 10.9 }
```

## 类

1、类中的字段默认的访问权限修饰符是什么？为什么外界可以访问？
1. 默认是private
1. 提供了set和get
```kotlin
class MyKt{
    var name = "wch"
        get() = field // 默认有get
        set(value) { // 默认有set
            field = value
        }
}
```

### 计算属性
```kotlin
// 在使用时随机获取了值
    val name:Int // field已经失效了 // 没有name这个成员变量了
        get() = (1..1000).shuffled().first() // 随机排序后，取第一个数据
// int getName(){return (1..1000).shuffled().first()}
```

### 防范竞态条件
```kotlin
var name:String ? = null
```
* 以后使用name时，都要判断name是否为空

### 主构造函数
```kotlin
class MyKt(){ // MyKt()主构造函数，可以不写
}

// _name _age 都是临时输入类型，不可以直接使用
class MyKt(_name:String, _age:Int){
}

// 使用输入数据一
class MyKt(_name:String, _age:Int){
    val name = _name
    // 可以额外修改
    get() = "My name is $_name"
}
// 使用输入数据二
class MyKt(val name:String, _age:Int){
}
```
* get 不允许私有化

### 次构造
```kotlin
    // 次构造，必须调用主构造
    constructor(name: String, sex: Char):this(name){

    }
```

### 构造函数：默认参数

1、所有的构造函数都是用了默认参数，调用MyKt()应该是哪个构造函数？
* 主构造



## init代码块
1. 可以使用临时参数
1. 可以require检查合法性
1. init代码块前后代码按顺序合并

## 懒加载 lateinit
1. lateinit懒加载，未初始化时判空和使用都会抛出异常
1. 需要用::name.isInitialized 进行检查
1. 需要手动加载！

```kotlin
class MyKt{
    lateinit var name:String
    fun initName(){
        name = "feather"
    }
    fun getNameUnsafe(){
        if(name != null){
            println(name)
        }
    }
    fun getNameSage(){
        if(::name.isInitialized){
            println(name)
        }else{
            println("你还没有初始化哦")
        }
    }
}
```
## 惰性初始化 by lazy
1. 自动加载，使用到的时候加载。
```kotlin
class MyKt{
    // 普通方式，非懒加载
    val name:String = requestName()
    // 懒加载
    val name2  by lazy { requestName() }
    fun requestName() = "feather"
}
```
## 初始化陷阱
1、init代码块前后具有顺序
////
```kotlin
// Error!
init{
    name = "feather"
}
var name:String
```
2. init代码中变量初始化前，使用变量，后面调用getNameInfo()报错

```kotlin
class MyKt{
    val name:String
    init{
        getNameInfo()
        name = "feather"
    }
    fun getNameInfo() {
        println("${name[0]}")
    }
}
fun main() {
    MyKt().getNameInfo()
}
```
3. 对象构造时，非空成员变量，利用调用方法饿汉加载，导致拿到了null。修改方法：临时参数赋值到成员变量要及时。

```kotlin
// 1. 主构造函数
class MyKt(_name:String){
    // 2. info = name = null // 一定拿到null
    val info = getNameInfo()
    // 3. name才赋值
    val name = _name
    fun getNameInfo() = name
}
fun main() {
    MyKt("feather").info.length
}
```
## open
1. Kt中所有类默认final不允许继承，需要open
1. Kt中所有方法默认final，需要open才可以overide（重写）
1. Kt中类主构造方法中成员变量如果可以被overide也需要open。或者用不同名字。

```kotlin
open class Peron(open val name:String){
    open fun printName(){}
}
class Student(override val name:String):Peron(name){
    override fun printName(){}
}
```
## 类型转换
1. is 可以检测类型
1. as 转换
```kotlin
if(p is Student){
    p as Student
}
```
### 智能类型转换
```kotlin
val p:Person = Student("wch")
// is后自动转换，可以直接调用
if(p is Student)
{
    p.printStudentName()
}
```
## Any
1、Any是什么？相当于Object
1. 所有类都隐式继承Any
1. Any有equals、hashCode、toString方法，但都看不到实现 ==> Java Object有
1. Any交到不同平台有不同实现：Linux、Windows等等，只提供了标准

## 对象声明，object
1、object是什么有什么用？
1. 关键字，既是单例的对象又是类名
1. MyKt可以作为类名，也可以作为单例对象
```kotlin
object MyKt{
    init {
        println("init初始化")
    }
}
// 单例类的内部默认生成了INSTANCE，调用show()等价于
// MyKt.INSTANCE.show()
MyKt.show()
```
MyKt生成的代码如下：
1. object的init代码生成在static代码块中
1. 正常class的init代码生成在主构造器中。
```java
// final类
// INSTANCE在static时初始化，
public final class MyKt {
    @NotNull
    public static final MyKt INSTANCE;
    public final void show() {}
    private MyKt() {}
    static {
        MyKt var0 = new MyKt();
        INSTANCE = var0; // 唯一
        String var1 = "init初始化";
        System.out.println(var1);
    }
}
```
## 匿名对象
1. java接口：匿名对象和lambda 两种方式
1. kt接口：匿名对象 一种
```kotlin
// 具名对象，接收匿名对象
val obj = object : OnDialogClickListener {
    override fun onCancelClick() {
        TODO("Not yet implemented")
    }
    override fun onConfimrClick() {
        TODO("Not yet implemented")
    }
}
// Runnable
val obj = object : Runnable {
    override fun run() {
        TODO("Not yet implemented")
    }
}
// lambda方式 - 一个方法的函数式接口
val obj = Runnable {
    println("lambda方式")
}
```
## 伴生对象
1、为什么会有伴生对象？
1. 在Kotlin中没有类似Java的static
2、为什么伴生对象的字段可以直接访问？
1. 背后代码 MyKt.Companion类 访问的info是MyKt的static字段info，info是private的。但是通过Companion可以访问
1. 伴生对象永远只会初始化一次
```kotlin
class MyKt{
    companion object{
        const val info = "info"
    }
}
MyKt.info
```
## 嵌套类和内部类
1、Kt中默认内部类不可以访问外部类，需要增加修饰符 => 默认是嵌套类
1. 需要加上inner修饰符
```kotlin
class MyKt{
    inner class InnerClass{}
}
```
## 数据类
1、数据类的定义，和普通类的区别是什么？
1. 数据类内部会增加：set、get、构造、解构函数、copy、toString、hashCode
1. 普通类 == ，内容一样会认为是两个对象。数据类 == 可以正确判断。
```kotlin
// 普通类 只要get、set
class ResponseBean(var code:Int, var msg:String, var data:String)
// 数据类
data class ResponseBean2(var code:Int, var msg:String, var data:String)
```
2、数据类使用场景和注意点？
1. 网络请求返回数据时用
1. 需要比较、copy、toString、解构时用
1. 必须要有主构造函数，必须要有参数
1. 不可以abstract、sealed、open、inner 专注于数据
## copy
1. copy方法主管主构造
## 解构
自定义解构功能：
1. 需要operator 运算符重载
1. 需要名字严格按照component1增加
```kotlin
// 普通类
class ResponseBean(var code:Int, var msg:String, var data:String){
    operator fun component1() = code
    operator fun component2() = msg
    operator fun component3() = data
    // xxx
}
```
## 运算符重载
operator进行重载
```kotlin
data class Data(var number1:Int, var number2:Int){
    operator fun plus(data: Data): Data {
        number1 += data.number1
        number2 += data.number2
        return this
    }
}
fun main() {
// C++中重载 + - 就可以了
// Kt中需要关键字 plus
    val result = Data(1, 1) + Data(2, 2)
    println(result)
}
```

**查看Kt支持的所有运算符：点号，IDE会有提示**

```kotlin
operator fun Data.xx()
```

## 枚举类
1、Kt中枚举类为什么和Java不同还有个class？
1. 想要表达和Java的不同，枚举也是类
```kotlin
// 1、基本的枚举类
enum class Week{
    星期一,
    星期二,
    星期三,
    星期四,
    星期五,
    星期六,
    星期日
}
fun main() {
    println(Week.星期一)
    println(Week.星期日)
    // 枚举的值 等价于 枚举本身
    println(Week.星期四 is Week)
}
```
2、枚举类中传入对象+定义方法
```kotlin
// 枚举类中存储对象
class Weather(val info:String)
enum class Week(var weather: Weather){
    星期一(Weather("晴朗")),
    星期二(Weather("小雨")),
    星期三(Weather("中雨")),
    星期四(Weather("暴雨")),
    星期五(Weather("阴")),
    星期六(Weather("雨夹雪")),
    星期日(Weather("大雪"));
    // 枚举中定义方法
    fun show() = println("$name:${weather.info}")
    // 更新属性值
    fun updateWeather(weather: Weather){
    this.weather = weather
}
}
fun main() {
    println(Week.星期日)
    println(Week.星期一.weather.info)
    Week.星期四.show()
    Week.星期四.updateWeather(Weather("大雪"))
    Week.星期四.show()
}
```
3、枚举类中定义抽象方法
1. 方法和枚举之间需要封号；
```kotlin
enum class Week {
    星期一 {
        override fun printInfo() {
            println(this.name)
        }
    },
    星期二 {
        override fun printInfo() {
            println(this.name)
        }
    };
    abstract fun printInfo()
}
fun main() {
    var day = Week.星期一
    day.printInfo()
    day = Week.星期二
    day.printInfo()
}
```
### 代数数据类型
什么是代数数据类型？
> when配合枚举，数据非常明确，不需要else
## 密封类
1、密封类是什么？
1. 一种特殊的类，用于表示有限的类继承结构。
1. 用于限制类的继承，并且只允许定义一些特定的子类。
1. 可以对密封类的子类做模式匹配，不需要else。
```kotlin
sealed class Result {
    class Success(val data: String) : Result()
    class Error(val message: String) : Result()
}
```
```kotlin
// 密封类的子类进行模式匹配，使用 `when` 表达式来处理不同的子类情况
fun handleResult(result: Result) {
    when (result) {
        is Result.Success -> println("Success: ${result.data}")
        is Result.Error -> println("Error: ${result.message}")
    }
}
```
2、密封类的使用例子
1. 表示不同类型的网络请求结果：
```kotlin
sealed class NetworkResult {
    class Success(val data: String) : NetworkResult()
    class Error(val message: String) : NetworkResult()
    object Loading : NetworkResult()
}
fun handleNetworkResponse(result: NetworkResult) {
    when (result) {
        is NetworkResult.Success -> println("成功: ${result.data}")
        is NetworkResult.Error -> println("错误: ${result.message}")
        NetworkResult.Loading -> println("加载中...")
    }
}
```
2. 表示不同类型的颜色：
```kotlin
sealed class Color {
    object Red : Color()
    object Green : Color()
    object Blue : Color()
    class Custom(val hexCode: String) : Color()
}
fun printColor(color: Color) {
    when (color) {
        Color.Red -> println("红色")
        Color.Green -> println("绿色")
        Color.Blue -> println("蓝色")
        is Color.Custom -> println("自定义颜色: ${color.hexCode}")
    }
}
```
3、密封类中object和class的区别
1. 密封类只允许两种子类object和class
1. object，没有自己的属性，单例对象，始终如一
1. class，可以为子类创建实例，并且传入属性值

## 接口和默认实现

1. interface关键字
1. 接口是可以通过get()进行动态实现的，但是没有实际价值

## 抽象类

abstract class 抽象类

## 泛型类
```kotlin
class KtClass<T>{
    xxx
}
```

## vararg 动态参数

```kotlin
// 动态参数的泛型，可以传入null
class MyKt<T>(vararg objects: T) {
    // 用于输出的T，生产者，只读。---规定。
    val objectArray: Array<out T> = objects
}
```
## out in

**为什么需要协变和逆变？**
> 默认，List<Person>和List<Man>是两个类型

```kotlin
// out 生产者，只可以作为返回值
interface Producer<out T>{
    fun producer() : T
   // fun comsume(item : T)
}
// out
// List<Person> = List<Man>

// in 生产者，可以修改值，但不可以作为方法返回出去
interface Comsumer<in T>{
    fun comsume(item : T)
   // fun producer() : T
}
// List<Person> = List<Animal>
```
* 默认，泛型子类对象，不可以赋值给泛型的父类引用 
> 类似Java中，List<Person>和List<Man>是两个类型
* out，泛型的子类对象可以赋值给泛型的父类引用
* int, 泛型的父类对象可以赋值给泛型的子类引用


### 应用

**什么时候需要out 和 in？**
1. 想要保护数据不被修改，用out T
1. 只能修改，不允许获取 in T。指的是不能够把数据作为方法返回值，提供出去。
1. Kotlin允许声明处泛型，<out T><in T>

## reified

1、reified的作用是什么？
`reified`关键字用于在具有`inline`函数修饰符的函数中获取“泛型参数的实际类型”。以下是使用`reified`的基本语法：
```kotlin
inline fun <reified T> functionName() {
    // 在函数体中可以使用 T 的实际类型
    T::class.java
}
```

2、要使用`reified`关键字，需要注意以下几点：
1. `reified`只能在具有`inline`修饰符的函数中使用，因为它需要在编译时对函数的字节码进行改变，并保留类型信息。
2. `reified`只能用于泛型类型参数，即在尖括号`< >`中的泛型参数前面使用`reified`关键字。
3. 在使用`reified`的函数体内，可以直接使用泛型类型参数`T`的实际类型，如`T::class`、`T::class.java`等。

3、让is和T配合使用
```kotlin
class Person(val name: String)
class Producer{
    inline fun<reified T> test(obj: Person){
        obj.takeIf { it is T } as T
    }
}
```

## 扩展函数


扩展方法：增强类、第三方库等类
```kotlin
class MyKt(val name:String)
fun MyKt.showName(){
    println("name:$name")
}
// 扩展函数生成的代码
// 调用时会自动把对象传入该方法，进行调用
public static final void show(MyKt $this$show){ 
    System.out.println("name:" + $this$show.getName());
}
```

**扩展同名函数会覆盖原先的函数，子类(Int)的扩展函数优先于父类(Any)的扩展函数**



### 超类扩展函数

1、对Any类扩展会导致所有类都有该方法
1. 影响范围大
1. 可以链式调用

### 内置函数源码解析：let

let源码：
```kotlin
@kotlin.internal.InlineOnly // 注解主要用于标记 Kotlin 内部的函数，以指示编译器将函数的实现内联到调用点
public inline fun <T, R> T.let(block: (T) -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(this)
}

// 最后一行
public inline fun <T, R> T.let(block: (T) -> R): R { // it，最后一行作为返回值
    return block(this)
}
public inline fun <T,R> with(receiver:T, block: T.() -> R):R{ // this，最后一行
    return receiver.block()
}
// 自身
public inline fun <T> T.apply(block: T.() -> Unit): T { // this，自身（链式调用）
    block()
    return this
}
public inline fun <T> T.also(block: (T) -> Unit): T{ // it，自身（链式调用）
    block(this)
    return this
}
// it，自身或者null
public inline fun <T> T.takeIf(predicate: (T)->Boolean): T?{ // it，自身或者null
    return if(predicate(this)) this else null
}
public inline fun <T> T.takeUnless(predicate: (T)->Boolean): T?{ // it，自身或者null
    return if(!predicate(this)) this else null
}
```

## 扩展属性

扩展属性:val+get()只读不可以修改
```kotlin
val String.info
    get() = "MyInfo"
```
生成的Java代码：
```java
   @NotNull
   public static final String getInfo(@NotNull String $this$info) {
      Intrinsics.checkNotNullParameter($this$info, "$this$info");
      return "MyInfo";
   }
```

## infix 中缀表达式

infix是什么？有什么好处？
1. infix代表中缀表达式，模仿C++。支持`"wch" to 10`的效果，而不需要`"wch".to(10)`
```kotlin
val map = mapOf("wch" to 10, "cyz" to 20)
println(map)
```
2. 自定义中缀表达式
```kotlin
// 需要成为A的扩展函数
public infix fun<A, B> A.to(that: B): Pair<A, B> = Pair(this, that)
```

扩展文件：将相关扩展函数写到一个文件中，需要时引入文件。

## 重命名

将方法名重命名：缩减代码量
```kotlin
import kotlin.let as l
fun main() {
    "feather".l {
        println("it = $it")
    }
}

// 第二个例子：短了很多
import com.personal.tax.compose.IWantToDoMyLicense as p
fun IWantToDoMyLicense(){
    println("Hello World!")
}
fun main() {
    p()
}
```

## map

1. map{it == 每一个元素} 元素类型 String Int Boolean等等，最终返回集合`List<XXX>`
```kotlin
    val list = listOf("wch", "cyz", "xxx")
    println(list)
    // 经过变化后，最后一行，作为新元素加入新集合
    var list2 = list.map {
        when(it){
            "wch" -> 1
            "cyz" -> 2
            "xxx" -> 3
            else -> -1
        }
    }
    println(list2)
```

## flatMap

1. `flatMap`用于对集合进行转换和扁平化操作。
1. flatMap{it == 每个元素} 每个元素返回集合`List<XXX>`，最终结果 `List<List<XXX>>`
```kotlin
fun main() {
    val numbers = listOf(listOf(1, 2), listOf(3, 4), listOf(5, 6))

    // 使用flatMap将多个列表扁平化为一个列表
    val flattenedNumbers = numbers.flatMap { it }
    println(flattenedNumbers) // 输出: [1, 2, 3, 4, 5, 6]

    // 使用flatMap进行转换和扁平化操作
    val squaredNumbers = numbers.flatMap { it.map { number -> number * number } }
    println(squaredNumbers) // 输出: [1, 4, 9, 16, 25, 36]
}
```

## filter

符合条件的添加到新集合中(true)
```kotlin
// 过滤掉int类型的list元素
    val list = listOf(listOf(1,2,3), listOf("a","b","c"), listOf(1.1, 2.2, 3.3))
    println(list) // [[1, 2, 3], [a, b, c], [1.1, 2.2, 3.3]]

    val list2 = list.filter {
        it.getOrNull(0) !is Int // 将Int集合全部去除
    }
    println(list2) // [[a, b, c], [1.1, 2.2, 3.3]]
// 全部扁平化展开后，去除Int
    val list3 = list.flatMap { sublist->
        sublist.filter { item->
            item !is Int
        }
    }
    println(list3) // [a, b, c, 1.1, 2.2, 3.3]
```

## zip

合并集合
```kotlin
    // 以Pair形式合并元素
    val list = listOf(1,2,3)
    val list2 = listOf("a","b","c")
    var zipList = list.zip(list2)
    println(zipList)
    //输出结果：[(1, a), (2, b), (3, c)]
```

## Kotlin和Java交互

### 可空性

Kotlin调用Java要注意为kong陷阱
```java
// Java类
public class Person {
    String mName;
    public Person(){
    }
    public Person(String name){
        mName = name;
    }
}
```
**错误交互：空指针**
```kotlin
    // Kt和Java错误交互
    // name类型为 String！ 可能为空
    val name = Person("wch").mName
    val name2 = Person().mName
    println(name.length)
    println(name2.length)
```
**正确交互：规则一 小心使用**
```kotlin
    val name = Person("wch").mName
    val name2 = Person().mName
    println(name?.length)
    println(name2?.length)
```
**正确交互：规则二 强制判断**
```kotlin
    val name:String? = Person("wch").mName
    val name2:String? = Person().mName
    println(name?.length)
    println(name2?.length)
```

### @JvmName

改变Kotlin生成类的名称，方便Java调用，需要在kt文件最开头
```kotlin
@file:JvmName("Stu")
```

### @JvmField

Java可以直接访问Kt字段，不再需要getXXX()

### @JvmStatic

Java可以直接访问Kt Companion中静态字段和静态方法，而不需要MyKt.Companion.xxx

### @JvmOverloads

Kt中默认参数，需要生成对应于Java的多个重载方法。

## 单例

饿汉式
```kotlin
object Singleton0
```
懒汉式(安全)
```kotlin
class Singleton private constructor(){
    companion object{
        // 底层采用双重检查加锁
        val instance:Singleton by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED){Singleton()}
    }
}
```
mode属性：
- `LazyThreadSafetyMode.NONE`: 这是默认的模式，懒加载属性是非线程安全的。
- `LazyThreadSafetyMode.SYNCHRONIZED`: 懒加载属性是线程安全的。在多线程环境中，`by lazy` 会使用 `synchronized` 来确保只有一个线程可以初始化属性。这会导致多个线程在访问该属性时进行阻塞等待。
- `LazyThreadSafetyMode.PUBLICATION`: 懒加载属性是线程安全的。在多线程环境中，`by lazy` 使用一种更高效的并发控制来进行初始化，可以在多个线程同时访问属性时进行初始化。这种模式通常比 `SYNCHRONIZED` 模式具有更好的性能。

**LazyThreadSafetyMode.PUBLICATION不适合单例模式，可能创建更多的实例**

## actual


===> Compsoe mutableStateOf()

当在多个平台上使用 Kotlin 编写代码时，可以在共享模块中声明一个 "expect" 函数，然后在各个平台的实际模块中使用 "actual" 关键字来提供该函数的具体实现。
1. 关键字 "actual" 用于在类、接口或函数中标记一个实际的实现。
1. 与 "expect" 关键字搭配使用，用于实现多平台共享代码的功能。
1. "expect" 关键字用于声明一个预期的抽象类、接口或函数，而 "actual" 关键字则用于提供该预期实体的实际实现。

