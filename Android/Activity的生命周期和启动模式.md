# Activity的生命周期和启动模式

版本:2018/2/7-1

[TOC]

1、Activity的四种形态
|形态|介绍|
|---|---|
|`active`/`running`|`Activity`处于`栈顶`, `可见`, 与用户交互|
|`pasued`|另一个全新`Activity`置于栈顶(该新Activity`非全屏`或者`透明`), `可见`, `不可交互`——内存不足时可以被释放|
|`stopped`|被另一个`Activity`完全覆盖, `不可见`, 保持状态信息和成员变量|
|`killed`|被`System`回收|

2、Activity典型生命周期
>1. onCreate：初始化工作/`setContentView`
>2. onStart：正在启动, `可见`, `不可交互`
>3. onResume：`可见`，`可交互`
>4. onPause: `可见`, `不可交互`，可以存储数据、停止动画，但不能耗时-会影响到`新Activity`的显示(新Activity`onResume`在旧Activity`onPause`后才执行)
>5. onStop: `不可见`, 可以进行轻量级的回收工作——不能耗时
>6. onRestart: 回到`Activity`时调用，从`onStop()`->`onRestart`->`onStart()`
>7. onDestory: `Activity`即将被销毁，需要进行`回收工作`和最终的`资源释放`
>* `onPause->onResume`：极少情况下`用户迅速返回原Activity`
>* `onStop->onRestart->onStart`: `用户返回原Activity`
>* `onStop->应用被杀死->用户返回原Activity->onCreate`: 因为高优先级应用需要内存，导致应用被释放。需要重建`Activity`

3、Activity异常生命周期
>是指`Activity`被系统回收或者由于当前设备的`Configuration`发生改变从而导致`Activity`被销毁重建

4、不同情况下的生命周期
|情况|Activity生命周期调用顺序|
|---|---|
|用户打开新`Activity`或者切换到桌面时|`onPause`->`onStop`|
|用户打开新`Activity`且其采用`透明主题`|`onPause`|
|用户再次回到原`Activity`|`onRestart`->`onStart`->`onResume`|
|back键回退时|`onPause`->`onStop`->`onDestory`|
|原`Activity`被系统回收后再次打开|`onCreate`->`onStart`->`onResume`——注意: 顺序和第一次启动一样，但是内部过程却有区别|

5、Activity生命周期的要点
>1. `onCreate`和`onDestory`配对，只执行一次，表示创建和销毁
>2. `onStart`和`onStop`配对，决定是否可见，伴随`用户操作`或者`设备屏幕的点亮和熄灭`，会执行多次
>3. `onResume`和`onPause`配对，决定是否可交互，伴随`用户操作`或者`设备屏幕的点亮和熄灭`，会执行多次

6、onCreate和onStart的区别
>1. 执行1次/执行多次
>2. onCreate能做的, onStart都能做, 除非只适合执行一次的逻辑
>3. onCreate处于不可见，onStart可见但不可交互

7、onStart和onResume的区别
>1. 均可见，前者不可交互，后者可交互
>2. 官方建议`onResume`中可以做开启动画和独占设备的操作

8、onPause和onStop的区别
>1. 前者可见，后者不可见
>2. 内存不足时一定会执行`onPause`,`onStop`不一定执行
>3. 保存数据最好在`onPause`中，但不能太耗时
>4. 尽量将重量级操作放在`onStop`中能够提高打开新Activity的速度(onPause会影响新Activity的创健)

9、onStop和onDestory的区别
>1. `onStop`时，`Activity`依旧在内存中，可以切换回该`Activity`。
>2. `onDestory`时，已经被系统释放了。

## Activity源码分析

10、Activity和AMS之间关于生命周期的机制
>1. 启动`Activity`的请求会由`Instrumentation`处理，会通过`Binder`向`AMS`发送请求
> 2. `AMS`内部维护着一个`ActivityStack`并负责栈内的`Activity`的状态同步
> 3. `AMS`通过`ActivityThread`去同步`Activity`的状态从而完成`生命周期`方法的调用。

11、ActivityStack的resumeTopActivityInnerLocked()
```java
    //ActivityStack.java
    private boolean resumeTopActivityInnerLocked(ActivityRecord prev, ActivityOptions options) {
        ......
        //1. 栈顶的Activity先onPause后
        boolean pausing = mStackSupervisor.pauseBackStacks(userLeaving, next, false);
        if (mResumedActivity != null) {
            //2. 新Activity才会启动
            pausing |= startPausingLocked(userLeaving, false, next, false);
        }
        ......
    }
```

12、启动新Activity的大致流程
> 1. `ActivityStack`会先执行栈顶Activity的`onPause`，之后才会执行启动新Activity
> 2. 在`ActivityStackSupervisor`的`realStartActivityLocked()`会调用`app.thread.scheduleLauchActivity()`进行新Activity的启动
> 3. 最终会调用`ActivityThread`中的`handleLauchActivity()`
> 4. `handleLauchActivity`中执行`performLauchActivity()`调用`onCreate`和`onStart`方法
> 5. 之后`handleLauchActivity`中执行`handleResumeActivity()`调用`onResume`方法
> * 生命周期调用顺序：旧onPause->新onCreate->新onStart->新onResume->旧onStop

## 异常情况下的生命周期分析
13、资源相关的系统配置改变导致Activity的杀死并重建
>场景： Activity的横竖屏切换
> 1. 系统配置改变后，`Activity`会被销毁，其`onPause`、`onStop`、`onDestory`均会被调用
> 2. 系统会额外调用`onSaveInstanceState`来保存当前`Activity`的状态, 调用时机在`onPause`前后(onStop之前)
> 3. `Activity`重新创建后，会调用`onRestoreInstanceState()`并且把`onSaveInstanceState`保存的`Bundle`对象作为参数同时传递给`onRestoreInstanceState()和onCreate()`, 其调用时机在`onStart`之后
> 4. 系统会默认保存当前Activity的视图结构并且恢复一定数据。根据每个View的`onSaveInstanceState()`和`onRestoreInstanceState()`可以知道系统能自动恢复哪些数据。

14、系统保存和恢复View层次结构的工作流程
>1. `Activity`被意外终止时，会调用`onSaveInstanceState()`去保存数据
> 2. `Activity`去委托`Window`保存数据
> 3. `Window`再委托顶层容器去保存数据(ViewGroup：一般是DecorView)
> 4. `顶层容器`最后一一通知其子元素保存数据

15、TextView的onSaveInstanceState()

16、TextView的onRestoreInstanceState()

17、onSaveInstanceState、onRestoreInstanceState、onCreate的数据存储与恢复
```java
    //存储数据：
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("saved_data", "保存的数据");
    }

    //恢复数据：
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String data = savedInstanceState.getString("saved_data");
    }

    //恢复数据：有额外判断
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //onCreate中需要判断是`正常启动` 还是 `异常恢复`
        if(savedInstanceState != null){
            String data = savedInstanceState.getString("saved_data");
        }
    }
```
> 因为`onCreate`中恢复数据需要额外判断，官方建议在`onRestoreInstanceState`中恢复数据。
