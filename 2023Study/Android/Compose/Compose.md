# Compose

本文链接：

Compose核心点：

1. 重组
2. remember
3. LaunchedEffect

1、Compose重组是什么？
1. 数据变化 => 视图的数据或者结构 改变
2. 编译器会给Composable函数，添加参数（最起码增加两三个参数）
3. 重组的时候不会重新调用整个Composable函数，而是调用部分代码

2、remember{}作用是避免loadMore状态，由于重组发生改变

## remmember

1、remmember是什么
1. remember是一个函数，用于在组件中存储和记住一个可变的值。
2. 类似于旧版Android中的savedInstanceState   ===> savedInstanceState
3. 会创建一个类似于ViewModel的实例，用于存储和管理组件的状态。 ===> ViewModel
4. 每当组件重新构建时，它都会使用上一次保存的状态来恢复数据。这使得在屏幕方向变化、组件重建等情况下，数据仍然保持一致。
5. 每当组件重建时，remember都会返回上一次保存的值，而不是使用新的初始化值。

## LaunchedEffect ==> 最有价值的部分
1、示例代码：
```kotlin
            // 加载更多的状态变量
            var loadMore = remember{
                derivedStateOf {
                }
            }
            LaunchedEffect(loadMore) {
                snapshotFlow { loadMore.value }
                    .distinctUntilChanged()
                    .collect{
                        onLoadMore() // 回调
                    }
            }
```
2、LaunchedEffect是什么？
1. 数据驱动
2. Composable
3. 可以用来做“加载更多” - 耗时操作 - 协程(异步线程) - 组合项的作用域内 运行挂起函数
4. 不要更新UI（异步）
3、LaunchedEffect源码
```kotlin
@Composable // 
@NonRestartableComposable
fun LaunchedEffect(
    key1: Any?,
    block: suspend CoroutineScope.() -> Unit // 挂起函数
) {
    val applyContext = currentComposer.applyCoroutineContext
    remember(key1) { LaunchedEffectImpl(applyContext, block) }
}
```
4、Composable一定是UI组件吗？
5、组件化 => 模块 / Compose = lazyColumn
6、ComposableManager
7、SPI（service provider interface）
app初始化
所有Composable去实现ItemComposable接口（lazyColumn）
ServiceLoader，自动找出所有实现者，找到对应的composable ===》 面向服务编程
item as？ T 是什么？
