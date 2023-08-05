委托
委托 == 代理
方法内的成员永远拿不到thisRef：官方委托和自定义委托-》方法里面没办法使用反射
委托只能类委托和属性委托

# Kotlin委托

本文链接：https://blog.csdn.net/feather_wch/article/details/132095759

## 类委托

1、类委托
1. 委托的是接口的方法
```kotlin
// 只能用于接口
interface DB{
    fun save()
}
// 类CreateDBAction实现了接口DB，参数db是DB类型，类的实现委托给参数db。
// 目的：啥也不想干
class CreateDBAction(db: DB):DB by db

```

2、类委托的原理是什么？生成了什么代码？
1. 成员变量：$$delegate_0 = 参数db
1. 实现方法：委托给$$delegate_0调用save()
```java
public final class CreateDBAction implements DB {
   // $FF: synthetic field
   private final DB $$delegate_0;

   public CreateDBAction(@NotNull DB db) {
      Intrinsics.checkNotNullParameter(db, "db");
      super();
      this.$$delegate_0 = db;
   }

   public void save() {
      this.$$delegate_0.save();
   }
}
```

3、类委托有什么用？
1. 减少委托的代码
1. Compose是重委托
```kotlin
CreateDBAction(SqlDB()).save()
CreateDBAction(OracleDB()).save()
```



## 属性委托

1、属性委托，委托的是 属性的 set和get
```kotlin
class MyKt{
    var value = 1314
    var number by ::value // 两个属性公用get和set
}
// number -> getNumber() -> getValue()
// number = 10 -> setNumber(10) -> setValue(10)
```


2、委托属性有什么用？
1. 字段升级，老字段适配老用户，新字段用于新用户。共用一个get、set
```kotlin
class Database{
    var data = 941226 // 1.0
    var newData by ::data // 2.0
}
```

3、懒加载委托也就是属性委托
1. 第一次获取时，才会获取，下面例子第一次获取耗时2秒，其他都立马获得
```kotlin
fun requestDownload(): String{
    Thread.sleep(2000L)
    return "sucess"
}

// 懒加载，
// 属性委托，委托给
val responseData : String by lazy {
    requestDownload()
}
// val responseData : String = SynchronizedLazyImpl(requestDownload())
// 借助了SynchronizedLazyImpl的get方法

fun main(){
    println("startloading...")
    println(responseData)
    println(responseData)
    println(responseData)
}
```

## 自定义属性委托

1、完全自己实现属性委托
```kotlin
// 自定义委托，定义好get和set之后，属性可以用该类实现属性委托
class Custom{
    operator fun getValue(owner: Owner, property: KProperty<*>) : String{
        return "AAA"
    }
    operator fun setValue(owner: Owner, property: KProperty<*>, value :String){

    }
}
class Owner{
    val responseData : String by Custom()
}
```

2、利用模板实现属性委托:ReadWriteProperty
```kotlin
// 自定义委托
class Custom2 : ReadWriteProperty<Owner, String>{
    var str = "default"
    override fun getValue(thisRef: Owner, property: KProperty<*>): String {
        return str
    }
    override fun setValue(thisRef: Owner, property: KProperty<*>, value: String) {
        str = value
    }
}
```

### 提供委托/暴露者委托

1、provideDelegate
1. 额外的属性初始化逻辑：在属性被委托对象初始化之前进行一些额外的操作，例如数据验证、计算或日志记录等。
1. 针对不同属性的不同行为：通过在不同的委托对象的provideDelegate方法中实现不同的逻辑，可以根据属性的不同需求，为每个属性提供不同的行为。
1. 属性访问的可扩展性：可以为属性访问添加自定义的行为，例如缓存、延迟加载、权限控制等。
```kotlin
class Owner{
    val responseData : String by Custom()
}
// 自定义委托
class Custom(var str: String = "Default") : ReadWriteProperty<Owner, String>{
    override fun getValue(thisRef: Owner, property: KProperty<*>): String {
        return str
    }
    override fun setValue(thisRef: Owner, property: KProperty<*>, value: String) {
        str = value
    }
}
// provideDelegate,暴露者委托，== 选择器
class SmartDelegator{
    operator fun provideDelegate(thisRef:Owner, property: KProperty<*>):ReadWriteProperty<Owner, String>{
        return if(property.name.isEmpty()){
            Custom("empty")
        }else{
            Custom("normal")
        }
    }
}
```

### 实战场景

#### 自己实现by lazy

```kotlin
class LazyInitDelegate<T> {
    private var initializer: (() -> T)? = null
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return initializer?.invoke() ?: throw IllegalStateException("Property not initialized")
    }
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: (() -> T)) {
        initializer = value
    }
}

class Example {
    val lazyProperty: String by LazyInitDelegate {
        // 在第一次访问属性时执行初始化逻辑
        println("Initializing lazy property")
        "Lazy Initialized"
    }
}

fun main() {
    val example = Example()
    println(example.lazyProperty) // 输出：Initializing lazy property \n Lazy Initialized
}

```

#### 属性委托的日志记录

```kotlin
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty

class LoggingDelegate<T> {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val value = property.getter.call()
        println("Property ${property.name} is accessed, value: $value")
        return value as T
    }
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        println("Property ${property.name} is set with value: $value")
        (property as KMutableProperty).setter.call(thisRef, value)
    }
}

class Example {
    var property: String by LoggingDelegate()
}

fun main() {
    val example = Example()
    example.property = "New value" // 输出：Property property is set with value: New value
    println(example.property) // 输出：Property property is accessed, value: New value \n New value
}

```


#### viewmodel

1、如何做到属性内部可以修改，外部不可以修改？
```kotlin
class Data{
    var data:String = ""
        private set

    private void changeData(value:String){
        data = value
    }
}
val data = Data()
data.data = "" // xxx 不可以
println(data.data) // 可以
```

2、如何做到kotlin的list集合，对内可以修改，对外界不可以修改
```kotlin
class MyKt{
    // 内部可以修改
    private val _data : MutableList<String> = mutableListOf()
    // 外部不可以修改
    val data : List<String> by :: _data
}
```

3、使用::用官方自定义委托，不使用需要自定义委托

4、用委托实现ViewModel的自动构造
```kotlin
class MyViewModel : ViewModel() {
    
}

fun main() {
    // 委托实现
    val mainViewModel : MyViewModel by viewModels()
}

private fun MainActivity.viewModels() : ReadOnlyProperty<MainActivity?, MyViewModel> =
    object : ReadOnlyProperty<MainActivity?, MyViewModel>{
        override fun getValue(thisRef: MainActivity?, property: KProperty<*>): MyViewModel {
            // thisRef永远为null
            return ViewModelProvider(this@viewModels).get(MyViewModel::class.java)
        }

    }
```

#### 委托TextView：类似DataBinding

```kotlin ==> 注入JS
//  
operator fun TextView.provideDelegate(value: Any?, property: KProperty<*>) =
    object: ReadWriteProperty<Any?, String?>{
        override fun getValue(thisRef: Any?, property: KProperty<*>): String? {
            return text as String
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) {
            text = value
        }

    }

// 创建TextView控件，双向绑定
var textView : TextView = findViewById(R.id.tv)
var message:String ? by textView

textView.text = "更改了控件的text -> message中的数值也会变"

message = "更改了数据 -> 更新UI"

```

双向绑定，多个控件操作数据
```kotlin
var data1 : String by textView1
var data2 : String by textView2
var data3 : String by textView3

data3 = data2
data2 = data1
data1 = "我在吃饭哦"
// 操作数据，View就会变，不用管UI刷新数据
```

出题目：如何手动实现String的代理（局部变量）？(用扩展函数)
```kotlin
var s1 = "wch"
var s2 : String by ::s1 // 类的成员变量才可以
var s3 : String by s1 // 不用官方的::

fun main() {
    var s1 = "wch"
    var s2:String by s1 // 报错
}
```
```kotlin
// Kotlin反射机制
operator fun String.setValue(item: Any?, property: KProperty<*>, value:String){
    // import kotlin.reflect.jvm.javaField, 已经被移除
    // property.javaField?.isAccessible = true
    // property.javaField?.set(item, value)
}
operator fun String.getValue(item: Any?, property: KProperty<*>) = this
```