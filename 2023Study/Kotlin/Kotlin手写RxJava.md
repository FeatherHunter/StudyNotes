

# Kotlin手写RxJava变换符

[本文链接，点击这里进入]()

## 使用create、map、observer
```kotlin
fun main() {
    // create构造出RxJavaCore存放，lambda执行完的结果
    create{
        "WCH"
    }.map{ // 扩展出map方法，接收原先RxJavaCore的value数据，lambda方法执行完后，构造出RxJavaCore继续保存结果
        this + ":Hello world!"
    }.observer{ // 进行消费
        println(this)
    }
}
```

## 自定义操作符
```kotlin
class RxJavaCore<T>(var value:T)
// action()执行完的结果，用RxJavaCore保存起来
inline fun<O> create(action:()->O) : RxJavaCore<O>{
    return RxJavaCore(action())
}
// map
inline fun<I, O> RxJavaCore<I>.map(action: I.() -> O) : RxJavaCore<O>{
    return RxJavaCore(action(value))
}
// 监听者
inline fun<I> RxJavaCore<I>.observer(action:I.() -> Unit){
    action(value)
}
```