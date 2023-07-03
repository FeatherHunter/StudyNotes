本文链接: https://blog.csdn.net/feather_wch/article/details/131526269

## startCoroutine
传统startCoroutine是无Receiver版本
```java
public fun <T> (suspend () -> T).startCoroutine(
    completion: Continuation<T>
) {
    createCoroutineUnintercepted(completion).intercepted().resume(Unit)
}
```
代码实例：startCoroutine
```java
// 参数为 suspend () -> T
suspend {
    // 运行在default线程 DefaultDispatcher-worker-1
    println("${Thread.currentThread().name}")
    queryInfo()
}.startCoroutine(object : Continuation<Any> {
    override val context: CoroutineContext get() = Dispatchers.IO
    override fun resumeWith(result: Result<Any>) {
        // 运行在default线程 DefaultDispatcher-worker-1
        println("${Thread.currentThread().name}")
        println(result.getOrNull())
    }
})
```

### Receiver详解

startCoroutine具有 Receiver 版本的定义
```java
public fun <R, T> (suspend R.() -> T).startCoroutine(
    receiver: R,
    completion: Continuation<T>
) {
    createCoroutineUnintercepted(receiver, completion).intercepted().resume(Unit)
}
```

```java
@RestrictsSuspension
class MyScope
suspend fun MyScope.suspendAction():Int = 200
// MyScope.() 限定范围作用域
fun test(lambda: suspend MyScope.() -> Int){
}

suspend fun MyScope.suspendAction():Int = 200

fun main() {
    test{
        suspendAction() // 只有MyScope扩展的才能看见
    }
}
```
**@RestrictsSuspension**:对于限定了MyScope作用域方法，1-非挂起方法都可以 2-suspend的都需要时MyScope作用越

Receiver版本代码实例：
1. 用该方式才可以使用到Receiver版本，第一个参数为Receiver。必须使用EmptyCoroutineContext
```kotlin
@RestrictsSuspension
class MyScope

fun test(lambda: suspend MyScope.() -> Int){
  lambda.createCoroutine(MyScope(), object : Continuation<Int>{
      override val context: CoroutineContext
          get() = EmptyCoroutineContext

      override fun resumeWith(result: Result<Int>) {
          println("${Thread.currentThread().name}")
          println(result.getOrNull())
      }

  }).resume(Unit) // resume触发
}
```
2. 第二种版本不使用注解，就可以使用Dispatchers.IO
```kotlin
class MyScope // 不使用注解
lambda.createCoroutine(MyScope(), object : Continuation<Int>{
    override val context: CoroutineContext
        get() = Dispatchers.IO

    override fun resumeWith(result: Result<Int>) {
        println("${Thread.currentThread().name}")
        println(result.getOrNull())
    }

}).resume(Unit)
```


## 手写挂起和恢复
1、消费者执行并不会动
2、
```java
/**==============================
 * 手写挂起和恢复
 *===============================*/
// 冷流，消费的一瞬间才会触发上游，生产者才会工作
fun main() = runBlocking{
    // 生产者
    val setObj = setData{
        for(i in 0..5){
            val r = send(i) // send后就会挂起
        }
    }

    val items = setObj()
    // 消费者
    for (i in items){
        delay(500)
        println("消费了: $i")
    }
}

/**=================================================*
 * 生产者模型
 *==============================================*/

// 内部send想要挂起，自己需要suspend,
fun setData(lambda: suspend Helper.() -> Unit): () -> SetGetImpl{
    return {
        SetGetImpl(lambda, 10) // 起始数值
    }
}
// 中转站
// 好处
// 1. Helper.() 增加this = Helper，匿名函数扩展
abstract class Helper {
    abstract suspend fun Helper.send(item:Int)
}
// fun send(value:Int, result:Int){} 保证不会找到该方法
class SetGetImpl(lambda: suspend Helper.() -> Unit, startValue: Int) : Helper(), Iterator<Int>{

    private var state:State
    private val startValue = startValue
    // 先保存lambda，保存了实现区域，消费时再执行；符合冷流
    init {
        // 初始化状态将整个协程本地保存到State内部
        // 第一步：保存协程本体
        state = State.NoStart(
            // 需要知道挂起方法设么时候执行完
            // 为什么要创建Coroutine？协程体是为了之后执行，切换线程执行好后，resumeWith恢复
            lambda.createCoroutine(this, object: Continuation<Unit>{
                override val context: CoroutineContext
                    get() = EmptyCoroutineContext

                override fun resumeWith(result: Result<Unit>) {
                    state = State.Close // 协程体全部执行完，直接Close
                    println("协程体执行完成后的最后一步 Resuem")
                }

            })
        )
    }

    // 生产者: 生产一个后挂起，消费后再恢复
    override suspend fun Helper.send(item: Int) = suspendCoroutine<Unit>{
        state = when(state){
            // 没有启动，更改为启动标记
            // it为suspend的continuation
            is State.NoStart -> State.Start(continuation = it, startValue + item)
            is State.Start -> throw IllegalStateException("准备就绪时，无法生成值")
            is State.Close -> throw IllegalStateException("完成时，无法生成值")
        }
    }

    private fun resume(){
        when(val curState = state){
            is State.NoStart->{
                curState.continuation?.resume(Unit) // 如果没有启动，直接恢复
                println("恢复了 = ${curState.continuation}")
            }
            is State.Start -> TODO()
            is State.Close -> TODO()
        }
    }

    // 消费者：循环遍历时触发
    override fun hasNext(): Boolean {
        resume()
        var r = state != State.Close // 不等于Close时都true代表有数据
        println("hasNext r = $r")
        return r
    }

    override fun next(): Int {
        return when(val curState = state){
            is State.NoStart -> {
                resume()
                return next() // 没有启动时，resume触发
            }
            is State.Start -> {
                // 启动就不启动
                state = State.NoStart(curState.continuation)
                return curState.nextValue + 1000
            }
            is State.Close -> {
                throw IndexOutOfBoundsException("没有数值")
            }
        }
    }

}

// 挂起、恢复、结束、状态 采用枚举升级版密封类
sealed class State{
    // continuation只是为了协程的恢复
    class NoStart(continuation: Continuation<Unit>? = null) : State(){
        val continuation = continuation
        init {
            println("NoStart#")
        }
    }
    class Start(continuation: Continuation<Unit>? = null, val nextValue: Int) : State(){
        val continuation = continuation
        init {
            println("Start#")
        }
    }
    object Close : State()
}
```


## 问题汇总
1. Continuation实现中context: CoroutineContext有什么用？传入EmptyCoroutineContext和其他的区别是什么?
1. Receiver版本有什么用？
