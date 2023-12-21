Derry主动给大家录制的协程全套

todo t01 -- 01-Android传统异步请求服务器写法
    https://www.wanandroid.com/blog/show/2

    违背人类的串行思维
     1.起床
     2.刷牙
     3.洗脸
     4.坐车
     5.来公司
     6.打开电脑

todo t02 -- 02-Android协程请求服务器写法

    OkHttp可以不可以切换线程
    Retrofit可以切换线程
    RxJava可以切换线程

    协程就是 线程的封装框架

    协程的特点 去 和 RxJava 其他优秀框架

    协程的魅力：用 【最简单的同步代码 写出最复杂的异步效果】
    遵循人类的串行思维

    Retrofit 2.6 以上 一旦 Retrofit遇上 suspend 关键子，内部会自动
    第一件事情：自动切换到 IO异步线程执行耗时操作 20耗时操作
    第二件事情：20耗时操作完成后，恢复到 当前协程所在的线程 == Main 安卓主线程

todo t03 -- 03-Android传统异步请求复杂业务的痛点

做过银行项目的话
   复杂银行业务
     1.请求加载[用户数据]  先 异步请求  onSuccess成功后
        2. 再 切换到 主线程 更新部分UI --  请求加载[用户资产数据]  先 异步请求  onSuccess成功后
            3.再 切换到 主线程 更新部分UI -- 请求加载[用户资产详情数据]   先 异步请求  onSuccess成功后
                 再 切换到 主线程 更新最后的UI

     有几次线程切换？ 答：六次线程切换

     请求加载[用户数据]         异步请求    1
     切换到 主线程 更新部分UI    主线程      2

     请求加载[用户资产数据]      异步请求    3
     切换到 主线程 更新部分UI    主线程      4

     请求加载[用户资产详情数据]   异步请求    5
     切换到 主线程 更新部分UI    主线程      6

     回调地狱的问题 == 异步代码复杂度带来的问题

     用 异步复杂代码，写出，复杂异步效果

todo t04 -- 04-Android协程解决复杂业务的痛点

   GlobalScope.launch (Dispatchers.Main) {
        var serverResponseInfo = requestLoadUser()
  }

  1.处于协程块依附的 安卓Main线程
  2.当你执行 requestLoadUser()
      【第一讲事情：会挂起到外面去，执行30秒钟耗时操作，
        第二件事情：30秒钟耗时操作完成后 会恢复当前协程块所在的 Main 线程   左边的代码var serverResponseInfo】
  3.由于此时当前所属安卓主线程，所以可以更新UI
   tv?.text = serverResponseInfo // 更新UI
   tv?.setTextColor(Color.GREEN) // 更新UI

todo t05 -- 05-Android协程的挂起与恢复

    1.学习Java，学习C++等 在常规操作中，有 调用，回调，返回，来做业务的

    2.挂起 与 恢复 ，来做业务的

todo t06 -- 06-Android大白话细讲挂起与恢复流程

    在协程中， 最重要的概念，先[suspend挂起] 和 再[resume恢复]

    suspend挂起：有人称为 暂停当前协程/挂起当前协程，然后在保存当前协程中所有局部信息（为了后面以后的恢复做支持）

    resume恢复：上面的挂起完成后， 从 当前 协程挂起点哪里 开始恢复 然后继续执行

todo t07 -- 07-Android阻塞与挂起大道至简讲解

    挂起 -- 非阻塞  一号人物，被拎出去，睡觉20秒，不会影响后面的 2 3 4人物，更加不会影响阻塞 安卓主线程的

    阻塞           一号人物，直接原地睡觉，影响 阻塞 2 3 4人物，会影响阻塞 安卓主线程的，所以安卓Button白了，多次点击会ANR一次 未响应

todo t08 -- 08-Android协程语言基础层和官方框架层

    官方框架层: Retrofit框架，         kotlinx包框架层 官方提供的协程框架

    语言基础层：动态代理，反射，注解 ...  kotlin包基础层，提供了协程 最基础的API

todo t09 -- 09-Android协程调度器

    Dispatchers.Main
        Android上的主线程
            用来处理UI交互和一些轻量级任务
                例如：更新UI控件，更新LiveData 等等

    Dispatchers.IO
        非主线程 - 异步
            专门为磁盘和网络IO进行了优化
                例如：数据库耗时操作，文件读写耗时操作，网络处理耗时操作 等等

    Dispatchers.Default
       非主线程 - 异步
            专门为CPU密集型任务进行了优化
                例如：数组大量排序，JSON数据大量解析操作，计算大量密集型任务 等等

    JVM程序，用不了，安卓的Main的，会报错，       安卓平台的可以直接用 安卓的Main的


todo t10 -- 10-Android协程CoroutineScope魅力

    当某个协程任务 出现了 丢失，无法追踪，会导致内存,CPU,磁盘资料的浪费，或者 发送一个无用的网络请求，这种情况都属于 【协程任务的不可控】
    在Kotlin协程为了解决，【协程任务的不可控】，出现了一个协程机制，就是 【结构化并发】

    Thread.start  stop弃用  Derry 1

    结构化并发的职责是 == 协程作用域 == CoroutineScope  所以你会发现一个现象（所有协程块里面  都会持有this == CoroutineScope）
        1.可以取消，协程任务，
        2.协程任务正在执行中，任务的情况，任务的状态，我们是可以追踪
        3.协程任务正在执行中，如果出现了什么意外，出现什么异常错误等等，可以发出信息 给我们

    TODO 既然说，所有的 协程体里面都有     CoroutineScope

    定义协程的时候，必须指定 是 CoroutineScope，他会追踪所有协程，他也可以 取消 由 CoroutineScope 管理的 协程

    CoroutineScope 常用的子类
        GlobalScope：生命周期是进程级别的，绑定APP进程的， 哪怕你的Activity、Fragment被销毁了，这个协程作用域还在【开发者不常用】
        MainScope：在Activity中使用，我们可以在onDestroy中取消协程
        viewModelScope：绑定ViewModel的生命周期的，ViewModel被销毁，他会自动销毁
        lifecycleScope：因为Lifecycle是绑定 Activity/Fragment生命周期的，会跟着销毁


todo t11 -- 11-Android企业级别项目MainScope应用


todo t12 -- 12-Android协程+JetPack全家桶+MVVM设计架构

todo t13 -- 13-Android协程launch与async比较区别
    他到底是不是第一次打印，就会非常纠结，因为以前说的是 协程非阻塞 而现在 runBlocking是阻塞的
    答： 说的是 runBlocking会阻塞 main thread，为什么要阻塞 main thread，是为了 一个目的 等待所有的子协程 完成后，才能结束 main thread

    Derry告诉大家，你永远要记住一句话，协程块 永远是 非阻塞的

todo t14 -- 14-Android协程join与await挂起工作流程

   书籍 网上  join等待 任务执行  其实准确来说，是 一次 挂起 与 恢复   Derry 1

todo t15 -- 15-Android协程async并发流程细讲

todo t16 -- 16-Android协程史上最详细CoroutineStart

    开发中常用的模式：Default，LAZY

    1.Default启动模式：协程体被创建后，协程立即开始调度，在"调度前" 协程被取消，协程会进入"协程取消响应状态" 然后才会取消协程
    2.ATOMIC启动模式： 协程体被创建后，协程立即开始调度，在"调度前" 协程被取消， 协程体里面会不响应取消的状态(不理你) 直到第一个挂起点（才理你）才能取消
    3.LAZY启动模式：   协程体被创建后，不会调度，会一直等 我们来手动调度（start非挂起，join挂起，await挂起），才开始调度，
            在调度前 协程被取消，协程会进入"协程取消响应状态" 然后才会取消协程 == Default模式
    4.UNDISPATCHED启动模式：协程体被创建后，（立即 在当前调用栈线程main线程中 执行），没有调度，  协程体里面{一直执行到  第一个挂起点， 然后再执行 父协程的代码}

TODO t17 -- 17-Android协程的作用域构建器

    // TODO coroutineScope 和 runBlocking 很相似（他们两者都会等待 协程体里面的任务全部执行完成，才会结束）
    //  runBlocking阻塞 main thread 来等待 (协程体里面的任务全部执行完成，才会结束)
    //  coroutineScope挂起 直到 (协程体里面的任务全部执行完成，才会恢复)

    // TODO coroutineScope 协程体里面，只有有一个协程失败，其他的兄弟协程，全部跟着失败

    // TODO supervisorScope 协程体里面，有一个协程失败，其他的兄弟协程，不会有任何影响

TODO t18 -- 18-Android协程Job生命周期

TODO t19 -- 19-Android取消协程当前的作用域

TODO 20-Android Job取消兄弟的协程

企业开发过程中，有20个网络请求 协程来做的，20个请求，使用 结构化并发
想具体结束，某一个 网络请求的协程？ 答：用Job

TODO t21 -- 21-Android协程取消的异常

TODO t22 -- 22-Android协程CPU密集型运算时取消之isActive

TODO t23 -- 23-Android协程CPU密集型运算时取消之ensureActive

TODO t24 -- 24-Android协程CPU密集型运算时取消之yield

TODO t25 -- 25-Android协程取消的影响

TODO t26 -- 26-Android企业中use函数释放

TODO t27 -- 27-Android无法取消的协程任务

TODO t28 -- 28-Android协程超时的任务

TODO t29 -- 29-Android协程CoroutineContext -- Derry老师  一定是 史上最详细 最完整 最透彻的教程
     // 协程必须有协程上下文 CoroutineContext
     Job : 控制协程的生命周期  协程协程行为的元素--CoroutineContext
     CoroutineDispatcher：向合适的线程分发协程任务--CoroutineContext
     CoroutineName：协程的名称，测试调试的时候，非常有用--CoroutineContext
     CoroutineExceptionHandler：处理未被捕捉的异常--CoroutineContext

TODO t30 -- 30-Android组合协程上下文元素

TODO t31 -- 31-Android协程上下文的继承

TODO t32 -- 32-Android协程上下文的继承公式

TODO t33 -- 33-Android协程自动传播异常与向用户暴露异常

TODO t34 -- 32-Android非根协程的异常传播

TODO t35 -- 35-Android协程传播的补充

TODO t36 -- 36-Android协程supervisorJob

TODO t37 -- 37-Android协程supervisorScope

TODO t38 -- 38-Android协程异常捕获时机

TODO t39 -- 39-Android异常捕获常见错误分析

TODO t40 -- 40-Android协程异常捕获解决App奔溃

TODO t41 -- 41-Android协程中全局异常处理

TODO t42 -- 42-Android协程取消时异常处理

TODO t43 -- 43-Android协程的异常聚合

TODO t44 -- 44-Android协程为了引出Flow登场

TODO t45 -- 45-Android协程Flow异步返回多值

TODO t46 -- 46-Android协程Flow的特点
     1.发起区域 有 Flow类型构建器的函数 flow()
     2.flow{里面可以挂起与恢复}
     3.getFlow不需要是 suspend 挂起函数，意味着 没有任何限制，更自由

TODO t47 -- 47-Android企业级Flow项目的开发案例编写

TODO t48 -- 48-Android协程Flow是冷流的概念

TODO t49 -- 49-Android协程Flow简化发射源

TODO t50 -- 50-Android协程Flow仿listOf构建

TODO t51 -- 51-Android协程Flow上下文保存机制

TODO t52 -- 52-Android协程Flow的launchIn收集流

TODO t53 -- 53-Android协程Flow流取消和检测

TODO t54 -- 54-Android协程Flow缓冲和flowOn处理背压

TODO t55 -- 55-Android协程Flow合并和处理最新值

TODO t56 -- 56-Android协程Flow的transform

TODO t57 -- 57-Android协程Flow的take

TODO t58 -- 58-Android协程Flow的reduce

TODO t59 -- 59-Android协程Flow的zip

TODO t60 -- 60-Android协程Flow的flatMap

TODO t61 -- 61-Android协程Flow的exception

TODO t62 -- 62-Android协程Flow的completion

TODO t63 -- 63-Android协程Channel通道介绍

TODO t64 -- 64-Android协程Channel进行协程通信

TODO t65 -- 65-Android协程Channel的capacity

TODO t66 -- 66-Android协程Channel让send不挂起

TODO t67 -- 67-Android协程的快捷方式

TODO t68 -- 68-Android协程Channel的结束

TODO t69 -- 69-Android协程BroadcastChannel

TODO t70 -- 70-Android协程select择优选择数据

select * from Student where xxx

TODO t71 -- 71-Android协程select与channel

TODO t72 -- 72-Android协程select与onJoin-上

TODO t72 -- 72-Android协程select与onSend-下

TODO t73 -- 73-Android协程手写Flow合并

TODO t74 -- 74-Android协程并发安全问题

TODO t75 -- 75-Android协程并发安全解决方案

---

TODO t76 -- 76-深刻理解-互相协作的程序

TODO t77 -- 77-深刻理解-互相协作的程序之Channel

TODO t78 -- 78-深刻理解-线程与协程
    协程 与 协程框架
    1.协程框架：是 Google 几十万人研发的 基于协程的概念 研发的框架（https://github.com/Kotlin/kotlinx.coroutines/tree/master/kotlinx-coroutines-debug）
    2.协程：我们在开发的时候，创建的协程 runBlocking，launch，async ...，协程的概念

TODO t79 -- 79-脑海中建立协程切换思维图

TODO t80 -- 80-launch的delay & delay & sleep

TODO t81 -- 81-协程执行流程研究

TODO t81 -- 81-协程执行流程研究-作业与补充

TODO t82 -- 82-协程挂起函数筑基
    82-协程挂起函数筑基-上部分
    82-协程挂起函数筑基-中部分
    82-协程挂起函数筑基-下部分

TODO t83 -- 83-结构化并发与Job

TODO t84 -- 84-CoroutineContext的理解题

TODO t85 -- 85-回顾挂起函数
            86-真正挂起状态机
            87-未真正挂起状态机

TODO t88 -- 88-协程基础操作1

TODO t89 -- 89-协程基础操作2

TODO t90 -- 90-协程基础操作3

TODO t91 -- 91-协程基础操作4

TODO t92 -- 92-协程基础操作5

TODO t93 -- 93-协程基础操作6
