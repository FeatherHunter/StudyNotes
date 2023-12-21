# Lifecycle原理和机制

本文链接：https://blog.csdn.net/feather_wch/article/details/131797308

[toc]

要点:
1. 观察者模式
1. 状态机/状态模式
1. 包装模式/装饰者模式
1. 空白Fragment机制
1. ContentProvider
1. Map缓存反射
1. 反射的性能优化

## Lifecyle的使用
1、使用方法一：Activity中通过内部类实现LifecycleObserver（kt中是inner class）
1. 好处是分层结构化，更清晰
1. 将耦合业务聚集在一块

2、使用方法二：
1. 接口继承接口，封装一个类似DefaultLifecycleObserver的类

## 相关所有类
3、LifecycleOwner是被观察者 => 用于标记具有生命周期的组件
4、Lifecycle是内部的生命周期对象 => 管理生命周期状态、派发生命周期事件
5、LifeCycleRegister => Lifecycle的具体实现类
1. 状态管理：用状态机管理状态转换，枚举类STATE表示生命周期状态 INITIALIZED、CREATED、STARTED、RESUMED、DESTORYED，内部变量int mState表示当前状态
1. 事件派发：EVENT枚举类，六种事件，ON_CREATE、ON_START、ON_RESUME、ON_PAUSE、ON_STOP、ON_DESTORY
1. 维护观察者列表：列表存储LifecycleObserver实现者，提供add和remove方法
1. 生命周期事件调度：disptachEvent派发给观察者

6、ProcessLifecycleOwner => 监控整个App的生命周期
1. 单例类，可以监控应用进入前台或者后台

7、ProcessLifecycleOwnerInitializer => ContentProvider
1. 要使用ProcessLifecycleOwner，需要通过该CP进行设置
1. 将applicaiton标签的name设置为："androidx.lifecycle.ProcessLifecycleOwnerInitializer"

8、ReportFragment：空白Fragment和Activity进行绑定
1. injectIfNeededIn() (1)>= 29, 直接注册到LifecycleCallbacks中 (2) <29 作为fragment添加到Activity中
1. onActivityCreated()
```java
#ReportFragment
onActivityCreated()
->dispatchCreate(mProcessListener) => 通知到ProcessLifecycleOwner
->dispatch(EVENT.ON_CREATE)
  #LifeCycleRegister
    ->getActivity().handleLifecycleEvent(event)
        ->State next = getStateAfter(event) // 获取下一步状态
        ->moveToState(next)
            ->mState=next
            ->sync()
                ->while()
                    ->forwardPass()
                    ->backwardPass()
                       #LifeCycleRegister.ObserverWithState
                        ->dispatchEvent()
                       #LifecycleEventObserver
                        ->dispatchEvent(lifecycleOwner, downEvent(observer.mState))
                           #ReflectiveGenericLifecycleObserver
                            ->onStateChanged()
                               #ClassesInfoCache.CallbackInfo
                                ->invokeCallbacks() // 反射调用
```
9、LifecycleEventObserver-接口，提供onStageChanged
10、ReflectiveGenericLifecycleObserver
1. 内部获取到我们实现的Observer的class
1. 用于反射拿到我们的method

11、ObserverWithState
1. LifeCycleRegister的静态内部类
1. 增加mState状态
1. 在观察者状态改变后更新mState状态(observer.onStageChanged())

12、ClassesInfoCache
1. 作用：map缓存，性能优化, framework中有大量的map缓存，比如setContentView中map缓存反射
1. 缓存了所有需要反射的Method
```java
Map<class<?>, CallbackInfo> mCallbackMap = new HashMap<>();
Map<class<?>, Boolean> mHashLifecycleMethods = new HashMap<>();
```

13、CallbackInfo
1. 反射调用Event事件对应列表中的所有方法
```java
Map<Event, List> mCallbackMap = new HashMap<>();
// MethodReference内部是Method
```

14、addObserver流程
```java
#LifeCycleRegister
addObserver(observer)
  #ObserverWithState
  ->ObserverWithState(observer)
    ->Lifecycling.lifecycleEventObserver(observer)
      ->ReflectiveGenericLifecycleObser(observer)
  ->while() // 状态同步，避免错过之前的Event
    ->sync()
```

15、Lifecyling => 静态方法的辅助类，提供构造出

## 状态机

16、Lifecycle的状态机
1. 五种状态，六种事件
1. 状态前进和状态回退
```java
【INITIALIZED】 --ON_CREATE--> 【CREATED】 --ON_START--> 【STARTED】 --ON_RESUME--> 【RESUMED】
【DESTORYED】 <--ON_DESTORY-- 【CREATED】 <--ON_STOP-- 【STARTED】 <--ON_PAUSE-- 【RESUMED】
// 事件和状态
LifecycleRegister#getStateAfter()
    switch(state){
        case ON_CREATE:
        case ON_STOP:
            return CREATED;
        case ON_START:
        case ON_PAUSE:
            return CREATED;
        case ON_RESUME:
            return RESUMED;
        case ON_DESTORY:
            return DESTORYED;
}
```

## 收获

### 设计模式

17、LifecycleEventObserver、ReflectiveGenericLifecycleObserver、ObserverWithState采用了什么设计模式？
1. 包装设计模式
1. LifecycleEventObserver-基本接口
1. ReflectiveGenericLifecycleObserver-在自定义的Observer上，使用反射机制来自动调用[观察者]和生命周期事件对应的方法，简化了观察者的实现
1. ObserverWithState-增加了mState，给观察者提供了状态，提供状态管理

## 注意点
1、在onResume注册会怎么办？(不推荐)
```java
onResume(){
addObserver(xxx);
}
CREATE->START->RESUME
LifecycleRegister中while()循环做状态同步
```

2、基本流程（onCreate）
Activity.onCreate->Fragment.onActivityCreated() -> 触发ON_CREATE事件
-> 进入CREATED状态（前进）-> 状态同步sync -> 观察者会调用CREATE注解方法

3、onStart()
onStart事件->进入STARTED状态->状态同步 调用START注解方法

4、为什么在ComponentActivity中写那么多逻辑代码，是为了降低耦合吗？
1. 为了无论是继承自AppCompatActivity还是其他Activity都一份代码
1. 封装思想，较少冗余代码

5、为什么需要状态机这么复杂？
1. 状态机可以给一切框架使用

6、onStop中注册会打印什么？
1. 执行create和destory的注解
1. 初始化--->CREATE状态
1. 紧接着调用了onDestory: CREATE--->DESTORY状态

7、为什么会执行create/为什么在onCreate中addObserver还会收到ON_CREATE事件？
1. addObserver，会进行状态同步。

