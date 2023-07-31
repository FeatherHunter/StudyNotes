# MVX总结

[本文链接,点击这里↓](https://blog.csdn.net/feather_wch/article/details/132029789)
[TOC]
扩展关键词：
moshi(gson解析)，支持polymorphic(多态数据)
compose glide
Accompanist组件库
Recompose
@Composable和suspend都是语言级别的
Snapshot机制

## 无架构

Model：数据/模型，对数据获取不依赖View
View：视图，不同架构中充当角色不同 xml(java)/composable/kotlin
C/P/VM：处理View和Model之间的交互逻辑

无架构适合的场景？

1. 页面简单
1. 长期不变动
1. 举例：帮助、隐私声明等等

## MVC

优点：
在无架构的基础上分理出Model层

缺点：
Android平台的实现中，有大量的View的操作在COntroller(Activity)中

适合场景：

* 只需要剥离Model的场景，如设置页面，View的操作少

## MVP

Presenter：处理View和Model的交互
View：Activity、Fragment、xml

思考：如果Presenter可以操作View和Model，不就等于是之前的Activity吗？

1. 用View的Interface，让P层操作View，无法拿到实例（解耦/限制权力/灵活可替换）
1. 用Model的Interface，解耦P和Model

组成部分：

1. IView的接口
1. Activity implements IView
1. Prsenter：拿到IView和Model
1. Model：处理数据

优点：充分解耦、逻辑清晰
缺点：接口过多

## MVVM

V： Activity、Fragment、xml => 不再需要IView接口 => 双向绑定 => Google实现 DataBinding
VM：ViewModel + LiveData
ViewBinding和DataBinding区别：

1. 缩水版，轻量级
1. 不需要改XML代码（方便老项目加东西）
ViewModel和Jetpack ViewModel区别？
DataBinding：引入可观察的数据，如LiveData
优点：充分解耦、逻辑清晰、可读性、可维护性、减少了接口
缺点：xml有代码容易报错(以来DataBinding)、编译慢
**LiveData如何保证对外暴露的MutableLiveData不可变？**

```kotlin
private val _pageState: MutableLiveData<PageState> = MutableLiveData()
val pageState:LiveData<PageState> = _pageState // 对外使用
```

## MVI

```kotlin
mViewModel.doLogin() // mvvm形式
mViewModel xxx {
    Intent(DoLogin, xxx, xxx)
}
```

I: 将VM编程意图Intent
强调数据单向流动：
MVI流程：ui，intent，viewmodel，uistate，ui

1. 用户 Intent 通知 Model
1. Model基于Intent更新State
1. State导致UI刷新显式
**UiState为什么要进行拷贝？**
观察者模式，观察的是uistate而不是里面的属性。copy一份修改属性，才能观察到变化。
优点：
1. 可重用，可直接把View照搬，不再需要去除各种绑定
1. 解耦
mvvm（ui和数据双向绑定）：
缺点：复用UI难，只复用UI还要剔除绑定关系
mvi（data改变UI）：
优点：直接复用。

## MVVM+Compose+Kotlin

Model
View：Activity、Fragment、composable
ViewModel：LiveData => stateOf

* 声明式UI
* 数据驱动
* 不需要adapter => RV

### SnapShot

Snapshot是什么？

1. Jetpack Compose中，Snapshot（快照）是指保存UI层次结构和状态的一种机制。
1. 记录了Compose UI界面的当前状态，包括各种组件和布局的属性、状态、以及用户交互等信息。
1. 通过Snapshot，可以在需要的时候保存和恢复UI的状态，以实现一些特定的场景需求。
Recompose是什么？
1. Compose的快照机制是基于Compose的Recompose（重组）过程来实现的。
1. Compose使用了一种基于函数响应式编程的模型，每当某个相关状态发生变化时，Compose会自动重新计算和重绘相应的UI部分。
1. 在Recompose过程中，Compose会将当前UI层次结构的状态保存为一个快照，并在重绘时使用该快照来恢复UI的状态，从而提高性能和效率。

具体来说，Compose的快照机制涉及以下几个主要步骤：

1. 初始化：当Compose创建UI时，会初始化一个初始的UI状态，并创建一个与之对应的快照。
2. 数据更新：当与UI相关的数据发生变化时，Compose会触发Recompose过程，重新计算需要重绘的UI部分。
3. 快照更新：在Recompose过程中，Compose会将当前的UI状态保存为一个新的快照，覆盖之前的快照。
4. 重绘：根据新的快照，Compose会更新和重绘相应的UI部分，以反映最新的状态。

Snapshot快照机制的优点

1. 可以更好地管理和控制UI的状态
1. 减少不必要的重绘和渲染操作，提高应用程序的性能和效率。
1. 方便地实现一些场景，比如保存和恢复用户的操作、实现撤销和重做功能等。

# MVVM+Compose+Jetpack+kotlin+协程+retrofit+okhttp+moshi(Json)实战

## 模块分类

**要点**
App页面：首页、服务、我的
首页：Tab列表+RV(item = 简单文字 or 图片 + 文件)
Fragment + Adapter(需要把数据适配到Fragment和View上) => Composable
1、传统RV+RV.adapter中写onBindViewHolder有什么问题吗？

1. 违背了**单一职责原则**（初中级开发方式）
1. Adapter目的是做适配，结果把View的创建(ViewHolder)加入了
1. 会进一步违背**开闭原则**

### 六大原则

汽车负责开车：单一职责原则
电车/油车继承汽车，不能直接改造汽车：开闭原则
电车要能继续跑，儿子需要像爸爸：里式替换原则
汽车要和路/人交互，要依赖接口：依赖倒置原则
汽车和每个交互目标，都有一个个接口：接口隔离原则
汽车要运载人，人还要携带物品，汽车和人直接交互==要有限的和其他对象交互(不要了解其内部细节，不和不直接对象交互)：迪米特原则，最少知识原则

### 为什么会有Compose？会有声明式UI？

原来Fragment、RV代码有太多的适配器代码，各种if判断等模板代码

* Compose支持Android所有版本
* SwiftUI并不能

### Accompanist组件库

```groovy
implementation "com.google.accompanist:accompanist-navigation-compose:0.21.0" //用于在 Android Jetpack Compose 中集成导航组件（Navigation Component）。
implementation "com.google.accompanist:accompanist-navigation-animation:0.28.0" // Navigation动画
implementation "com.google.accompanist:accompanist-pager:0.21.0" // ViewPager
implementation "com.google.accompanist:accompanist-swiperefresh:0.21.0" // 添加下拉刷新功能
implementation "com.google.accompanist:accompanist-systemuicontroller:0.21.0" // 设置应用顶部状态栏和底部导航栏的颜色。
implementation "com.google.accompanist:accompanist-insets:0.21.0" //【已经弃用】foundation中// 用来调整系统状态栏、导航栏等的padding以更加友好的适配屏幕内的组件
implementation "com.google.accompanist:accompanist-pager-indicators:0.21.0" // 添加页面指示器
implementation "com.google.accompanist:accompanist-permissions:0.28.0" // 权限申请
implementation "com.google.accompanist:accompanist-flowlayout:0.28.0" // 流式布局（可以自动换行）
implementation "com.google.accompanist:accompanist-webview:0.28.0" // WebView
implementation "com.google.accompanist:accompanist-glide:0.28.0" // 【已经弃用】支持glide，建议用coil
implementation "com.google.accompanist:accompanist-coil:0.28.0" // 支持kotlin
// 其他单独库
implementation "androidx.constraintlayout:constraintlayout-compose:1.0.1"
// Coil图片加载库
implementation "io.coil-kt:coil-compose:2.2.2"
implementation "io.coil-kt:coil-svg:2.2.2" 
// Landscapist：使用coil 在svg放大和缩小时有问题，不是矢量图
implementation "com.github.skydoves:landscapist-coil:2.0.3"
// Landscapist：结合 Glide 和 Fresco 一起使用
implementation "com.github.skydoves:landscapist-glide:2.1.0"
// Cloudy 是一个专门处理Jectpack Compose中的Blur高斯模糊效果的支持库
implementation "com.github.skydoves:cloudy:0.1.1"
```

## lazycolumn

根据数据类型自动加载Composable，而且新增一个样式，完全不需要修改之前的代码。（RV是需要修改的）
实现类：
> lazycolumn + TitleComposable自定义标题Item + TitleImageComposable自定义图片标题Item(NetWorkImage(Glide/Coil加载图片))
**@Composable相当于suspend语言级别**
**Recompose不是重新执行一边**
kotlin秒杀Java（默认参数）

Fragment兼容Compose

```kotlin
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        原先的Fragment代码
//        return inflater.inflate(R.layout.fragment_blank, container, false)
//        Compose兼容版本
        return ComposeView(requireContext()).apply {
            setContent {
                // UI代码
            }
        }
    }
```

加载列表数据：

```kotlin
val contentList = mutableStateListOf<Any>() // 存放列表数据
LazyColumn{
    items(contentList.size){
        if(contentList[it] is TitleModel){
            TitleComposable(model = contentList[it] as TitleModel)
        }else if(contentList[it] is TitleImageModel){
            TitleImageComposable(model = contentList[it] as TitleImageModel)
        }
    }
}
TitleModel:存放数据
TitleComposable:视图
// 数据转换
// 网络请求后，数据转换为Model并添加到List中即可
```