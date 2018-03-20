转载请注明链接：http://blog.csdn.net/feather_wch/article/details/51089115

主要记录Android目前为止最优质的开源库(不包括那些曾经火热但被废弃的开源库-如SlidingMenu等)。
第一部分是知名开源库以及大公司的开源库。
第二部分是介绍剩余部分的开源库。

#Android最优质开源库大全

版本:2018/3/20-1

[TOC]

#矢志不渝的坚持权威道路

知名框架：
1. xutils
2. eventbus
3. Realm

##公司开源库
###Google

|序号|开源库名称|简介|教程|
|---|---|---|---|
|1|[glide](https://github.com/bumptech/glide)|Android的图片加载缓存库||
|2|[zxing](https://github.com/zxing/zxing)|二维码||
|3|Dagger2|Dagger2是Dagger的升级版，是一个依赖注入框架，现在由Google接手维护。|[Dagger2从入门到放弃再到恍然大悟](https://www.jianshu.com/p/39d1df6c877d)|
|4|[gson](https://github.com/google/gson)|Java对象和JSON互相转换||
|5|[Flutter](https://github.com/flutter/flutter)|用于快速构建移动APP，使用`Dart`语言。||
|6|[Volley](https://github.com/google/volley)|Android的HTTP库||
|7|[ExoPlayer](https://github.com/google/ExoPlayer)|开源播放器||
|8|[Guava](https://github.com/google/guava)|谷歌JAVA核心库||
|9|[Guetzli](https://github.com/google/guetzli)|高质量的JPEG编码器(C++)||
|10|[error-prone](https://github.com/google/error-prone)|在编译阶段捕获Java错误||
|11|[battery-historian](https://github.com/google/battery-historian)|安卓电量分析工具||
|12|[material-design-icons](https://github.com/google/material-design-icons)|该系统图标包含常用的700个图标，如用于媒体播放、通讯、内容编辑、连接等等。在 Web 应用，安卓和 iOS 设计均适用。||
|13|[android-architecture](https://github.com/googlesamples/android-architecture)|Android 架构的官方指导，涉及 mvp、mvp-loaders、databinding、mvp-clean、mvp-dagger、mvp-contentproviders、mvp-rxjava 等，分别在各自指定的分支下，有非常大的参考意义。||

###Square
|序号|开源库名称|简介|教程|
|---|---|---|---|
|1|okio|不像Java的输入输出流那样复杂啰嗦，尤其擅长支持二进制数据。||
|2|okhttp|Http请求库||
|3|retrofit|Square 是最早开源项目之一， Retrofit是目前 Android 最流行的 Http Client 库之一||
|4|Picasson|图片缓存库- picasso 功能单一，没有缓存过期，同androidQuery一样链式调用，载入本地文件速度慢。(作者:JakeWharton)||
|5|leakcanary|检查内存泄露|
|6|RxAndroid|由 JakeWharton 大神主导开发的项目， RxAndroid 是 RxJava 的一个针对 Android 平台的扩展， 主要用于 Android 开发|
|7|Otto|一个事件库 (pub/sub 模式)， 用来简化应用程序组件之间的通讯， otto 修改自Google 的 Guava 库， 专门为 Android 平台进行了优化， 与上面介绍的 EventBus 相比， 两个库各有各的优点，完全取决于我们自己项目的需求来选择它们哪一个|
|8|android-times-square|TimesSquare 是 Square 公司出品的一款显示日历选择日期的控件， 可以让用户选择多个日期||

###Facebook

|序号|开源库名称|简介|教程|
|---|---|---|---|
|1|Fresco|Fresco 是一个强大的图片加载组件， 支持加载 Gif 图和 WebP 格式， 支持 Android2.3(API level 9) 及其以上系统||
|2|React Native||
|3|Stetho|一个强大的 Android 调试工具，使用该工具你可以在 ChromeDeveloper Tools 查看 App 的布局， 网络请求(仅限使用 Volley， okhttp 的网络请求库)， sqlite,preference, 一切都是可视化的操作，无须自己在去使用 adb, 也不需要 root 你的设备||
|4|Rebound|Facebook弹性动画库, 可以让动画看起来真实自然， 像真实世界的物理运动， 带有力的效果， 使用的参数则是 Facebook 的 origami 中使用的||
|5|Buck|Buck是Facebook开发的一款开源Android Build工具，基于Apache License 2.0协议发布，可以通过独立构建并行来发挥多核的性能，加速开发者的Android应用构建流程。Buck基于单一的库构建，能够以最小的资源集重建，非常适用于Android项目组织与管理。||
|6|Conceal|Conceal是一套用于Android平台上进行文件加密和鉴权的Java API，专为速度设计，小巧而高速。它使用了OpenSSL算法的子集和一些预先定义的选项，能够让库保持在较小的体积。通过它，开发者可以实现对手机、平板电脑SD卡中的数据以及大型文件进行加密和存储。||
|7|Bolts|Bolts是一个面向iOS和Android的底层库集合，分别为Bolts-iOS和Bolts-Android，由Facebook和Parse共同设计完成，于2014年1月基于BSD许可协议开源，其所有源码均托管到GitHub上。Bolts能够让移动应用开发变得更加简单，其组件与Parse及Facebook服务完全无关，因此，开发者无需拥有Parse或Facebook开发者账户即可直接使用。||
|8|Infer|Infer是一个静态分析工具，用来检测安卓和苹果系统应用发布前的缺陷。如果你给Infer一些Objective-C，Java或C代码，它会生成一个潜在的缺陷列表。Infer工具也有助于防止系统崩溃和性能下降。Infer的目标是空指针异常、资源漏洞、内存溢出之类的致命缺陷。||
|9|shimmer-android|可以用一个简单灵活的方法在你的安卓 APP 上做出闪光效果。|

###Yalantis
|序号|开源库名称|简介|教程|
|---|---|---|---|
|1|uCrop|uCrop 是 Yalantis 推出的又一款力作, 用于裁剪 Android 系统上的图片, 致力于打造最佳的图片裁剪体验。||
|2| ContextMenu|动画体验很棒的 Context Menu|Android 版：https://github.com/Yalantis/Context-Menu.Android
|3|Side Menu|动画体验很棒的 Side Menu|Android 版：https://github.com/Yalantis/Side-Menu.Android |
|4|Phoenix|下拉刷新，带 Header 渐渐凸显效果|Android 版：https://github.com/Yalantis/Phoenix |
|5|Taurus|另一种下拉刷新效果|Android 版：https://github.com/Yalantis/Taurus |
|6|FlipViewPager.Draco|提供 ListView Item 翻页效果|Android 版：https://github.com/Yalantis/FlipViewPager.Draco |
|7|FoldingTabBar|可展开收缩的 TabBar| iOS 版：https://github.com/Yalantis/FoldingTabBar.iOS |
|8| CameraModule|简单的 Android 拍照模块|GitHub：https://github.com/Yalantis/CameraModule |
|9| Euclid|这个项目目前还只是不错的动画跳转效果 Demo|Android 版：https://github.com/Yalantis/Euclid |

###Twiter
1、Android-Bootstrap
> http://blog.csdn.net/dsc114/article/details/50432798

###Uber
|序号|开源库名称|简介|教程|
|---|---|---|---|
|1|NullAway|Uber空指针库|

###Airbnb
|序号|开源库名称|简介|教程|
|---|---|---|---|
|1|Lottie|动画开源库|

###阿里巴巴
|序号|开源库名称|简介|教程|
|---|---|---|---|
|1|阿里巴巴Java、Android手册|||
|2|Sophix|阿里巴巴热修复框架||
|3|Atlas|阿里巴巴组件化框架||

##开源大神
###JakeWharton
|序号|开源库名称|简介|教程|
|---|---|---|---|
|1|Butterknife|不多解释||
|2|Hugo|JakeWharton 大神推出的一个用于打印 Log, hugo 是基于注解被调用的， 引入相关依赖后， 在方法上加上 @DebugLog 即可输出 Log, 使用非常简单||

#最优质的开源库

###知名
xBus - xBus - 简洁的EventBus实现。
Small - 做最轻巧的跨平台插件化框架，目前已支持Android、iOS以及html5插件。并且三者之间可以通过同一套javascript接口进行通信。

###UI
1. awesome-android-ui: 里面网罗了所有你见过的、没见过的各种 UI 效果，涉及 Material、Layout、Button、List、ViewPager、Dialog、Menu、Parallax、Progress 等等，而且有相对应的截图、gif 展示，以后应对设计师各种效果的时候有很大的参考帮助作用。

1. SlidingMenu: Google的侧滑菜单`NavigationDrawer`出来之后就被放弃了。
1. MPAndroidChart：图表库
1. ViewPagerIndicator：一个ViewPager指示器， 使用起来简单方便， 可高度定制， 开发出各种各样动画效果。作者：JakeWharton 大神
1. PhotoView: https://github.com/chrisbanes/PhotoView
1. SmartRefresh: https://segmentfault.com/a/1190000010066071
1. Material Dialogs： 是一个可高度定制易用， 符合 Material Design 风格的 Dialogs, 兼容 Android API8 以上版本， 个人使用感觉它完全可替代 Android 原生那个， 比原生那个更加简单易用
1. MaterialDesignLibrary： 这个库控件都是遵循了 Google Material Design 设计规范开发出来， 例如有: Flat Button,Rectangle Button, CheckBox， Switch, Progress bar circular indeterminate等等

1. Android-Ultra-Pull-to-Refresh：拉动刷新
Android-ObservableScrollView

1. ObservableScrollView: 是一款用于在滚动视图中观测滚动事件的 Android 库， 它能够轻而易举地与 Android 5.0 Lollipop 引进的工具栏 (Toolbar) 进行交互， 还可以帮助开发者实现拥有 Material Design 应用视觉体验的界面外观， 支持 ListView， ScrollView， WebView，RecyclerView, GridView 组件

###多媒体
1. Vitamio 是一款 Android 与 iOS 平台上的全能多媒体开发框架
2. ijkplayer - B站开源的视频播放器，支持Android和iOS。

###数据库
1. greenDAO：是一个可以帮助 Android 开发者快速将 Java 对象映射到 SQLite 数据库的表单中的 ORM解决方案， 通过使用一个简单的面向对象 API, 开发者可以对 Java 对象进行存储， 更新， 删除和查询， greenDAO 相对OrmLite, AndrORM 这两个 ORM 开源库， 性能是最高的

###热更新
1. code-push -大微软推出的一套可以为用 React Native 和 Cordova 开发的 App 提供代码热更新的方案。
###其他
1. AndroidAnnotations：一个能让你进行快速开发的开源框架
2. Fastjson : 是一个 Java 语言编写的高性能功能完善的 JSON 库。它采用一种“假定有序快速匹配”的算法，把 JSONParse 的性能提升到极致，是目前 Java 语言中最快的 JSON 库。Fastjson接口简单易用，已经被广泛使用在缓存序列化、协议交互、Web 输出、Android 客户端等多种应用场景
3. android-common： android-common-lib 是 Trinea 大神收集的一些开发通用的缓存， 公共 View 以及一些常用工具类
4. dynamic-load-apk：开发者是 singwhatiwanna (任玉刚)， 是《Android 开发艺术探索》书籍的作者， 这个是作者联合另两位开发者啸(时之沙)和宋思宇花了几个月时间研究出来的 Apk 动态加载框架， 想了解更多关于这框架可到作者博客看这篇文章有详细介绍
5. logger: Logger 是一个简单， 漂亮， 强大 Android 打印日志库

###网络
1. OkhttpUtils
2. android-volley： 这个框架把AsyncHttpClient 和 Universal-Image-Loader 的优点集于了一身，既可以像 AsyncHttpClient一样非常简单地进行 HTTP 通信，也可以像 Universal-Image-Loader 一样轻松加载网络上的图片， 这个库并不是官方的，只是托管同步在 Maven, 官方只提供的 Jar 包


###开发工具库

1. [AndroidUtilsCode](https://www.jianshu.com/p/677856023d0c)
2. [Codota](https://www.codota.com/)-搜索最好的 Android 代码

#参考资料
1. [github中排名前100的Android库](https://www.cnblogs.com/Free-Thinker/p/7423033.html)
1. [Android开源库和资料汇总](http://blog.csdn.net/wu996489865/article/details/53585433)
1. [Google最热门60款开源项目](https://www.cnblogs.com/svili/p/7889436.html)
1. [Facebook移动开源项目大合集](http://blog.csdn.net/hyugahinat/article/details/50949084)
1. [github: 最常见开源库汇总](http://blog.csdn.net/wu996489865/article/details/53585433)
1. [阿里巴巴、百度等公司开源](http://blog.csdn.net/wh211212/article/details/70147572)
1. [Yalantis 酷炫开源UI项目](http://blog.csdn.net/u013812939/article/details/45718431)
