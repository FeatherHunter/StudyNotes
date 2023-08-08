

# Kotlin手写RxJava变换符

[本文链接，点击这里进入]()

1、核心点：中转站存储之前的数据
2、三行代码实现RxJava

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

## 简化版本(一)
```kotlin
class RxJavaCore<T>(var value:T)
inline fun<O> create(action:()->O):RxJavaCore<O> = RxJavaCore(action())
inline fun<I,O> RxJavaCore<I>.map(action: (I) -> O):RxJavaCore<O> = RxJavaCore(action(value))
inline fun<I> RxJavaCore<I>.observer(action:(I) -> Unit) = action(value)
```
## 简化版本(二)
1、将Helper转换为Any(泛型) ====> 扩展函数的传递过程
```kotlin
inline fun<R> create(action:()->R):R = action() // 保存到泛型中，R中
inline fun<I,R> I.map(action: (I) -> R):R= action(this) // 给I扩展，自动拿到上一步骤，泛型里面R的数据
inline fun<I> I.observer(action:(I) -> Unit) = action(this)
```
