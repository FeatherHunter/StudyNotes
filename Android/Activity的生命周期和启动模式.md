转载请注明链接：http://blog.csdn.net/feather_wch/article/details/79279307

# Activity的生命周期和启动模式

版本:2018/2/8-1

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
```java
@Override
public Parcelable onSaveInstanceState() {
    Parcelable superState = super.onSaveInstanceState();
    ...
    //1. 保存了选中状态
    if (save) {
        SavedState ss = new SavedState(superState);
        ss.selStart = start;
        ss.selEnd = end;
        //2. 保存文本
        if (mText instanceof Spanned) {
            ......
            ss.text = sp;
        } else {
            ss.text = mText.toString();
        }
        if (isFocused() && start >= 0 && end >= 0) {
            ss.frozenWithFocus = true;
        }
        //3. 保存错误信息
        ss.error = getError();
        //4. 保存编辑器状态
        if (mEditor != null) {
            ss.editorState = mEditor.saveInstanceState();
        }
        return ss;
    }
    return superState;
}
```

16、TextView的onRestoreInstanceState()
```java
    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState)state;
        super.onRestoreInstanceState(ss.getSuperState());
        //1. 读取保存的文本
        if (ss.text != null) {
            setText(ss.text);
        }
        //2. 读取保存的状态
        if (ss.selStart >= 0 && ss.selEnd >= 0) {
            ......
        }
        //3. 保存的错误信息
        if (ss.error != null) {
            ......
        }
        //4. 读取保存的编辑状态
        if (ss.editorState != null) {
            createEditorIfNeeded();
            mEditor.restoreInstanceState(ss.editorState);
        }
    }
```

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

18、资源内存不足导致低优先级的Activity被杀死
>场景：系统内存不足时，会按照优先级去杀死目标Activity所在的进程
>Activity优先级-从高到低：
>1. 前台Activity——正在和用户交互的Activity
>2. 可见但非前台Activity——比如对话框弹出导致Activity可见但不可交互
>3. 后台Activity——已经被暂停的Activity，已经执行了`onStop`，优先级最低

19、Activity禁止横竖屏切换的方法
>1. 在`AndroidManifest`中给相应的`Activity`添加上属性`android:screenOrientation="portrait"`
>2.  `portrait`为竖屏
>3. `landscape`为横屏
>4. 或者可以在`onCreate`中添加代码`setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT/LANDSCAPE);`
>5. 最终会禁止横竖屏切换，也不会触发`保存数据/恢复数据`的回调

20、Activity在横竖屏切换时，禁止Activity的重建——通过configChanges
>1. 给`Activity`添加属性：`android:configChanges="orientation|screenSize"`
>2. `Activity`不会再销毁和重建，只会调用`onConfigurationChanged()`方法，可以进行特殊处理。

21、configChanges包含的系统配置
|条目|含义|
|---|---|
|orientation|屏幕方向改变, 如横竖屏切换|
|screenSize|屏幕的尺寸信息改变, 旋转屏幕时, 屏幕尺寸会发生变化(`API 13新增`)——因此13以及以上该选项都会导致`Activity`重启|
|locale|一般指切换了系统语言|
|keyboard|键盘类型改变, 如使用了外插键盘|
|keyboardHidden|键盘可访问性改变, 如用户调出键盘|
|fontScale|系统字体缩放比例改变, 如选择新字号|
|uiMode|用户界面模式改变, 如开启夜间模式(`API 8新增`)|
|smalletScreenSize|设备物理屏幕尺寸改变, 如用户切换到外部的显示设备(`API 13新增`)|
|mcc|SIM卡唯一标识IMSI(国际移动用户标识码)中的`国家代码`, 三位, `中国为460`|
|mnc|SIM卡唯一标识IMSI(国际移动用户标识码)中的`运营商代码`, 两位, `中国移动为00`, `中国联通为01`, `中国电信为03`|
|【不重要】layoutDirection|布局方向改变, 正常情况下无需修改布局的`layoutDirection`属性(`API 17`新增)|
|【不重要】touchscreen|触摸屏发生改变, 正常情况下不会发生|
|【不重要】navigation|系统导航方式发生改变, 比如采用轨迹球导航|

## Activity的启动模式
22、Activity的启动模式的由来和作用？
>1. 默认情况下，多次启动同一个`Activity`的时候，系统会创建多个实例并且全部放入`任务栈`中
>2. 系统提供`启动模式`就是用于修改系统的`默认行为`
>3. `启动模式`分为四种：`standard`、`singleTop`、`singleTask`和`singleInstance`

23、Activity的启动模式
|启动模式|作用|补充|
|---|---|---|
|standard(默认)|该模式创建的`新Activity`会放置于`原Activity`之上——`新Activity`位于`原Activity`的任务栈中|注意：如果使用`ApplicationContext`去启动`standard`模式的`Activity`会报错, 因为`非Activity`的`Context`并没有`任务栈`, 所以需要给`新Activity`指定`FLAG_ACTIVITY_NEW_TASK`标记位，用于启动时创建新的任务栈。这也就是`singleTask`模式|
|singleTop|栈顶复用模式——解决`栈顶`多个重复`Activity`的问题。|启动时，会判断`栈顶是否是该`Activity`，如果是就不会重新创建，同时会回调`Activity`的`onNewIntent()`方法，此`Activity`的`onCreate`和`onStart`不会被系统调用。例如任务栈中`A-B-C-D(栈顶)`, 此时启动`D`不会出现`ABCDD`的情况。但是如果是启动`B`, 则任务栈中为`ABCDB`(创建了B)|
|singleTask|栈内复用模式——在`任务栈`中寻找该`Activity`, 不存在则新建Activity, 如果请求的是`新栈`就创建`新栈`并放入`新栈`中, 如果请求的是`原栈`, 就将其放入原栈中; 存在则将该`Activity`之上的所有Activity出栈，最终该`Activity`处于栈顶, 并调用其`onNewIntent()`方法|通过该方法重复创建，生命周期顺序`onPause()->onNewIntent()->onResume()`|
|singleInstance|单实例模式——拥有`singleTash`的所有特性，额外增加的是该`Activity`只能单独位于一个`任务栈`中，后续的请求均不会再创建`新的Activity`||

24、singleTask的特殊实例
>1. 存在两个任务栈：栈1-AB，栈2-CD(singleTask)。如果请求启动D，整个后台CD的任务栈会切换到前台，形成任务栈ABCD，之后的`back`会按照D-C-B-A
>2. 如果是请求启动C，那么栈会变成ABC, 出栈顺序C-B-A

25、任务栈的要点
>1. 参数`TaskAffinity(任务相关性)`-标识一个Activity所需的任务栈名字，默认时为应用的`包名`
> 2. `TaskAffinity`属性主要和`singleTask`或者`allowTaskReparenting`属性配对使用
> 3. 任务栈分为`前台任务栈`和`后台任务栈`，后台任务栈中的`Activity`属于`暂停状态`, 用户可以通过切换将`后台任务栈`再次调到`前台`
> 4. `TaskAffinity`和`singleTask`配对使用时，表示`运行的任务栈`名字，启动的`Activity`会运行在该指定的`任务栈`中
> 5. `TaskAffinity`和`allowTaskReparenting`配对使用时, `应用A`启动了`应用B`的一个`Activity C`(allowTaskReparenting属性为true), 此时去打开应用B，会发现出现的是`Activity C`，因为`Activity C`从`A的任务栈`过渡到了`B的任务栈`中

26、Activity指定启动模式的两种方法和区别
>1. 通过`AndroidManifest`中的`android:lauchMode=“singleTask”`指定
> 2. 在`Intent`中设置标志位来指定：`intent.addFlags(Intent.FlAG_ACTIVITY_TASK)`
> 区别：后者优先级高于前者；第一种方法无法为`Activity`设定`FLAG_ACTIVITY_CLEAR_TOP`标识，第二种方法无法为`Activity`指定`singleInstance`模式

27、实例：standard模式下重复打开一个Activity
>1. 会有前台任务栈，`TaskAffinity`值为包名，会包含多个Activity实例
> 2. 会有后台任务栈，`TaskAffinity`值为`com.android.laucher`，只有一个`Activity`也就是`桌面`
> * `adb shell dumpsys activity`能看到activity



## Activity的Flags标志位

28、标志位有什么用？
>1. 可以指定`Activity的启动模式`：`FLAG_ACTIVITY_NEW_TASK`和`FLAG_ACTIVITY_SINGLE_TOP`
> 2. 可以影响`Activity的运行状态`:`FLAG_ACTIVITY_CLEAR_TOP`和`FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS`
> 注意：一般不需要我们指定标志位，有些标志位用于系统内部-我们不要使用。


29、主要标志位含义

| 标志位 | 作用 |
| ------------- | ---------------------------- |
| FLAG_ACTIVITY_NEW_TASK   | 为`Activity`指定`singleTask`启动模式, 效果和在`XML`中指定该启动模式相同 |
| FLAG_ACTIVITY_SINGLE_TOP | 指定Activity为`singleTop`模式 |
| FLAG_ACTIVITY_CLEAR_TOP  | 启动时，清除所有该`Activity`之上的`Activity`。如果是`singleTask`模式，则会调用该`Acitivity`的`onNewIntent()`; 如果是`standard`模式，则连同该`Activity`以及之上的所有`Activity`全部出栈，并创建新的该`Activity`入栈 |
| FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS|该`Acitivity`不会出现在历史`Activity`的列表中，等同于在XML指定属性`android:excludeFromRecents="true"`|

## IntentFilter的匹配机制















https://www.cnblogs.com/a284628487/p/3361555.html Orientation
http://www.cnblogs.com/a284628487/p/3187320.html#3708566 Binder架构
