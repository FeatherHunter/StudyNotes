转载请注明链接：http://blog.csdn.net/feather_wch/article/details/79279307

# Activity的生命周期和启动模式

版本:2018/8/30-1(15:15)

---

[TOC]

## 四种状态(1)

1、Activity的四种状态
|形态|介绍|
|---|---|
|`active`/`running`|`Activity`处于`栈顶`, `可见`, 与用户交互|
|`pasued`|另一个全新`Activity`置于栈顶(该新Activity`非全屏`或者`透明`), `可见`, `不可交互`——内存不足时可以被释放|
|`stopped`|被另一个`Activity`完全覆盖, `不可见`, 保持状态信息和成员变量|
|`killed`|被`System`回收|

## 生命周期(14)

1、Activity生命周期
![Activity典型生命周期](https://images0.cnblogs.com/i/426802/201406/060009291302389.png)
|生命周期|说明|是否可杀死|源码位置|
|---|---|---|---|
|onCreate| 初始化工作, setContentView|不可以|ActivityThread.handleLauchActivity()->performLauchActivity1()->最后一步|
|onStart| 可见, 不可交互|不可以|ActivityThread.handleResumeActvity()->performResumeActivity()|
|onResume| 可见，可交互|不可以|ActivityThread.handleResumeActvity()->performResumeActivity()|
|onPause| 可见, 不可交互，可以存储数据、停止动画，但不能耗时-会影响到新Activity的显示(新ActivityonResume在旧ActivityonPause后才执行)|可以|
|onStop| 不可见, 可以进行轻量级的回收工作—不能耗时|可以|
|onRestart| 回到Activity时调用|不可以|ActivityThread.handleResumeActvity()->performResumeActivity()|
|onDestory| Activity即将被销毁，需要进行回收工作和资源释放|可以|

2、什么情况下会执行：onPause()->onResume()
> 1. 在当前Activity上面有个Dialog，并且退出Dialog
> 1. Activity先执行`onPause()`处于可见、不可交互，然后回到`onResume()`进入可交互状态。

3、什么情况下会执行：onStop()->onRestart()->onResume()
> 1. 从新的Activity返回到当前Activity时
> 1. 切换到桌面，又切换回当前Activity。

4、什么情况下执行: onPause()->onCreate() 或者 onStop()->onCreate()
> 1. 系统内存不足，导致Activity在对应周期被杀死。然后重建该Activity


5、打开新Activity的流程图
```sequence
Note left of activity1: 1.onCreate
Note left of activity1: 2.onStart
Note left of activity1: 3.onResume
activity1->activity2: 4.onPause
Note right of activity2: 5.onCreate
Note right of activity2: 6.onStart
Note right of activity2: 7.onResume
Note left of activity1: 8.onStop
activity2->activity1: 9.onPause
Note left of activity1: 10.onRestart
Note left of activity1: 11.onStart
Note left of activity1: 12.onResume
Note right of activity2: 13.onStop
Note right of activity2: 14.onDestroy
```

6、打开Dialog、窗口Activity或者透明主题的新Activity的流程图
> 窗口Activity：Activity布局中加上属性

```xml
Android:theme="@android:style/Theme.Dialog"
```

```sequence
Note left of activity1: onCreate
Note left of activity1: onStart
Note left of activity1: onResume
activity1->activity2: onPause
Note right of activity2: onCreate
Note right of activity2: onStart
Note right of activity2: onResume
Note left of activity1: 没有执行onStop()
activity2->activity1: onPause
Note left of activity1: 直接执行onResume()
```


7、Activity生命周期的要点
>1. `onCreate`和`onDestory`配对，只执行一次，表示创建和销毁
>2. `onStart`和`onStop`配对，决定是否可见，伴随`用户操作`或者`设备屏幕的点亮和熄灭`，会执行多次
>3. `onResume`和`onPause`配对，决定是否可交互，伴随`用户操作`或者`设备屏幕的点亮和熄灭`，会执行多次

8、onCreate和onStart的区别
>1. 执行1次/执行多次
>2. onCreate能做的, onStart都能做, 除非只适合执行一次的逻辑
>3. onCreate处于不可见，onStart可见但不可交互

9、onStart和onResume的区别
>1. 均可见，前者不可交互，后者可交互
>2. 官方建议`onResume`中可以做开启动画和独占设备的操作

10、onPause和onStop的区别
>1. 前者可见，后者不可见
>2. 内存不足时一定会执行`onPause`,`onStop`不一定执行
>3. 保存数据最好在`onPause`中，但不能太耗时
>4. 尽量将重量级操作放在`onStop`中能够提高打开新Activity的速度(onPause会影响新Activity的创健)

11、onStop和onDestory的区别
>1. `onStop`时，`Activity`依旧在内存中，可以切换回该`Activity`。
>2. `onDestory`时，已经被系统释放了。

### 源码分析

12、Activity和AMS之间关于生命周期的机制
>1. 启动`Activity`的请求会由`Instrumentation`处理，会通过`Binder`向`AMS`发送请求
> 2. `AMS`内部维护着一个`ActivityStack`并负责栈内的`Activity`的状态同步
> 3. `AMS`通过`ActivityThread`去同步`Activity`的状态从而完成`生命周期`方法的调用。

13、ActivityStack的resumeTopActivityInnerLocked()
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

14、启动新Activity的大致流程
> 1. `ActivityStack`会先执行栈顶Activity的`onPause`，之后才会执行启动新Activity
> 2. 在`ActivityStackSupervisor`的`realStartActivityLocked()`会调用`app.thread.scheduleLauchActivity()`进行新Activity的启动
> 3. 最终会调用`ActivityThread`中的`handleLauchActivity()`
> 4. `handleLauchActivity`中执行`performLauchActivity()`调用`onCreate`和`onStart`方法
> 5. 之后`handleLauchActivity`中执行`handleResumeActivity()`调用`onResume`方法
> * 生命周期调用顺序：旧onPause->新onCreate->新onStart->新onResume->旧onStop

## 异常生命周期(8)

1、Activity异常生命周期
>是指`Activity`被系统回收或者由于当前设备的`Configuration(方向)`发生改变从而导致`Activity`被销毁重建

|生命周期|说明|
|---|---|
|☆onSaveInstanceState(Bundle outState)|Activity被动销毁时调用，保存重要信息，用于恢复|
|☆onRestoreInstanceState(Bundle savedInstanceState|重建时调用，会将Bundle传递到onCreate()中|
> 1. 用户主动销毁Activity不会触发`onSaveInstanceState()`如finish()、按下回退键退出Activity
> 1. 按下HOME键、按下电源键、启动其他Activity、横竖屏切换: 会触发onSaveInstanceState()

2、资源相关的系统配置改变导致Activity的杀死并重建
>场景： Activity的横竖屏切换
> 1. 系统配置改变后，`Activity`会被销毁，其`onPause`、`onStop`、`onDestory`均会被调用
> 2. 系统会额外调用`onSaveInstanceState`来保存当前`Activity`的状态, 调用时机在`onPause`前后(onStop之前)
> 3. `Activity`重新创建后，会调用`onRestoreInstanceState()`并且把`onSaveInstanceState`保存的`Bundle`对象作为参数同时传递给`onRestoreInstanceState()和onCreate()`, 其调用时机在`onStart`之后
> 4. 系统会默认保存当前Activity的视图结构并且恢复一定数据。根据每个View的`onSaveInstanceState()`和`onRestoreInstanceState()`可以知道系统能自动恢复哪些数据。

3、系统保存和恢复View层次结构的工作流程
>1. `Activity`被意外终止时，会调用`onSaveInstanceState()`去保存数据
> 2. `Activity`去委托`Window`保存数据
> 3. `Window`再委托顶层容器去保存数据(ViewGroup：一般是DecorView)
> 4. `顶层容器`最后一一通知其子元素保存数据

### View的保存和恢复

4、TextView的onSaveInstanceState()
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

5、TextView的onRestoreInstanceState()
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

### Activity的保存和恢复

6、onSaveInstanceState、onRestoreInstanceState、onCreate的数据存储与恢复
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


### 进程优先级

7、资源内存不足导致低优先级的Activity被杀死
>场景：系统内存不足时，会按照优先级去杀死目标Activity所在的进程
>Activity优先级-从高到低：
>1. 前台Activity——正在和用户交互的Activity
>2. 可见但非前台Activity——比如对话框弹出导致Activity可见但不可交互
>3. 后台Activity——已经被暂停的Activity，已经执行了`onStop`，优先级最低

8、Android进程优先级
> 1. 前台: 正在交互的Activity，或者和前台Activity交互的Service
> 1. 可见: 可见，但是不可以交互的Activity
> 1. 服务：Service
> 1. 后台：处于后台，不可见，不可交互的Activity。
> 1. 空：没有任何活动组件，用于提高组件的响应速度。随时可以被销毁。

## 横竖屏切换(3)

1、Activity禁止横竖屏切换的方法
>1. 在`AndroidManifest`中给相应的`Activity`添加上属性`android:screenOrientation="portrait"`
>2.  `portrait`为竖屏
>3. `landscape`为横屏
>4. 或者可以在`onCreate`中添加代码`setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT/LANDSCAPE);`
>5. 最终会禁止横竖屏切换，也不会触发`保存数据/恢复数据`的回调

2、Activity在横竖屏切换时，禁止Activity的重建——通过configChanges
>1. 给`Activity`添加属性：`android:configChanges="orientation|screenSize"`
>2. `Activity`不会再销毁和重建，只会调用`onConfigurationChanged()`方法，可以进行特殊处理。

### configChanges

3、configChanges包含的系统配置
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

## 启动模式(21)

1、任务栈是什么?
> 1. Android采用任务栈来管理`Activity`
> 1. 任务栈---具有先进后出原则
> 1. 一个App可以有多个任务栈。

2、为什么要有启动模式？
>1. 默认情况下，多次启动同一个`Activity`的时候，系统会创建多个实例并且全部放入`任务栈`中
>2. 系统提供`启动模式`就是用于修改系统的`默认行为`

3、启动模式分类
|启动模式|作用|补充|
|---|---|---|
|standard(默认)|不会复用任务栈中的Activity，而是新建Activity实例添加到栈顶。`新Activity`位于启动者的任务栈中||
|singleTop|栈顶复用模式——解决`栈顶`多个重复`Activity`的问题。|启动时，会判断`栈顶是否是该`Activity`，如果是就不会重新创建，同时会回调`Activity`的`onNewIntent()`方法，此`Activity`的`onCreate`和`onStart`不会被系统调用。例如任务栈中`A-B-C-D(栈顶)`, 此时启动`D`不会出现`ABCDD`的情况。但是如果是启动`B`, 则任务栈中为`ABCDB`(创建了B)|
|singleTask|栈内复用模式——在`任务栈`中寻找该`Activity`, 不存在则新建Activity, 如果请求的是`新栈`就创建`新栈`并放入`新栈`中, 如果请求的是`原栈`, 就将其放入原栈中; 存在则将该`Activity`之上的所有Activity出栈，最终该`Activity`处于栈顶, 并调用其`onNewIntent()`方法|通过该方法重复创建，生命周期顺序`onPause()->onNewIntent()->onResume()`|
|singleInstance|单实例模式——拥有`singleTask`的所有特性，额外增加的是该`Activity`只能单独位于一个`任务栈`中，后续的请求均不会再创建`新的Activity`||


4、onNewIntent()生命周期出现在哪些场景？
> 1. singleTop模式：栈顶找到了该Activity，只回调onNewIntent()方法
> 1. singleTask模式：找到任务栈中的该Activity，调用其onNewIntent()

5、可以通过Application的Context去启动standard模式的Activity吗？可以用Service的Context去启动Activity吗？
> 不可以！会报错
> 1. 因为`非Activity`的`Context`并没有`任务栈`
> 1. 所以需要给`新Activity`指定`FLAG_ACTIVITY_NEW_TASK`标记位，用于启动时创建新的任务栈。这也就是`singleTask`模式


6、singleTask的特殊实例
>1. 存在两个任务栈：栈1-AB，栈2-CD(singleTask)。如果请求启动D，整个后台CD的任务栈会切换到前台，形成任务栈ABCD，之后的`back`会按照D-C-B-A
>2. 如果是请求启动C，那么栈会变成ABC, 出栈顺序C-B-A

7、Activity指定启动模式的两种方法和区别
>1. 通过`AndroidManifest`中的`android:lauchMode=“singleTask”`指定
> 2. 在`Intent`中设置标志位来指定：`intent.addFlags(Intent.FlAG_ACTIVITY_TASK)`
> 区别：后者优先级高于前者；第一种方法无法为`Activity`设定`FLAG_ACTIVITY_CLEAR_TOP`标识，第二种方法无法为`Activity`指定`singleInstance`模式

8、实例：standard模式下重复打开一个Activity
>1. 会有前台任务栈，`TaskAffinity`值为包名，会包含多个Activity实例
> 2. 会有后台任务栈，`TaskAffinity`值为`com.android.laucher`，只有一个`Activity`也就是`桌面`
> * `adb shell dumpsys activity`能看到activity

### 任务栈的管理

9、任务栈的管理
> 1. xml中Activity存在如下管理任务的属性
>      1. taskAffinity
>      1. launchMode
>      1. allowTaskReparenting
>      1. clearTaskOnLaunch
>      1. alwaysRetainTaskState
>      1. finishOnTaskLaunch
> 1. Intent中包含下面几种Flag来管理任务
>      1. FLAG_ACTIVITY_NEW_TASK
>      1. FLAG_ACTIVITY_SINGLE_TOP
>      1. FLAG_ACTIVITY_CLEAR_TOP

10、任务栈相关API
> 1-获取到App任务栈
```java
ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
List<ActivityManager.AppTask> appTasks = activityManager.getAppTasks();
for (ActivityManager.AppTask task : appTasks) {
    Log.d(MainActivity.class.getName(), task.getTaskInfo().topActivity.getClassName()+"");
}
```
> 2-获取到任务栈ID
```java
getTaskId();
```

### taskAffinity属性

11、 android:taskAffinity的作用
> 1. taskAffinity(任务相关性)用于Activity-标识一个Activity所需的任务栈名字，默认时为应用的`包名`
> 1. 决定了Activity和任务栈的依附关系
> 1. 有两个应用场景：`android:launchMode="singleTask"`以及`android:allowTaskReparenting="true"`

```xml
<activity
    android:taskAffinity="com.example.包名或者任何String"
    android:launchMode="singleTask"
    android:allowTaskReparenting="true">
</activity>
```


12、allowTaskReparenting
> 1. 成true时，Activity就拥有了更改所在任务栈的能力。
> 1. allowTaskReparenting默认是继承至application中的allowTaskReparenting=false。


13、 taskAffinity和allowTaskReparenting配合使用
> 1. 作用：根据`taskAffinity`重新为`Activity`选择宿主。
> 1. 一个Activity处于某个Task中，本身具有和另一个Task相同的`taskAffinity`，启动时该Activity会切换到目标Task(任务栈)中，
> 1. `应用A`启动了`应用B`的一个`Activity C`(allowTaskReparenting属性为true), 此时去打开应用B，会发现出现的是`Activity C`，因为`Activity C`从`A的任务栈`过渡到了`B的任务栈`中


14、taskAffinity配合singleTask使用
> 1. 作用：启动的`Activity`会运行在该指定的`任务栈`中
> 1. taskAffinity中的内容是`任务栈`名字。
> 1. 会先查找目标Activity的`taskAffinity`和当前`Task`的`taskAffinity`是否一致，一致就加入到当前`Task任务栈`中
> 1. 如果不同，会去查找是否存在着`taskAffinity`相同的Task，存在就将其`切换到前台`，并将Activity加入其中。
> 1. 如果依旧没有，会创建一个新的`Task任务栈`，并将Activity加入其中。

15、任务栈的要点
> 1. 任务栈分为`前台任务栈`和`后台任务栈`
> 1. 后台任务栈中的`Activity`属于`暂停状态`,
> 1. 用户可以通过切换将`后台任务栈`再次调到`前台`，利用`taskAffinity`

#### 对launchMode的影响

16、android:launchMode="standard"
> 1. ActivityA和ActivityB的taskAffinity不相同。
> 1. A启动B。
> 1. 结果：A和B都加入到同一个任务栈中，taskAffinity无效。

17、android:launchMode="singleTop"
> 1. ActivityA和ActivityB的taskAffinity不相同。
> 1. 结果：和standrad一样，A和B都加入到同一个任务栈中，taskAffinity无效。

18、android:launchMode="singleInstance"
> 1. singleInstance会导致创建新Task(任务栈)，并且该栈中只有一个Activity实例。且该Activity也是唯一实例。
> 1. 结果：B会创建新栈，并且加入其中。

### Flags标志位

19、标志位有什么用？
>1. 可以指定`Activity的启动模式`：`FLAG_ACTIVITY_NEW_TASK`和`FLAG_ACTIVITY_SINGLE_TOP`
> 2. 可以影响`Activity的运行状态`:`FLAG_ACTIVITY_CLEAR_TOP`和`FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS`
> 注意：一般不需要我们指定标志位，有些标志位用于系统内部-我们不要使用。


20、主要标志位含义

| 标志位 | 作用 |
| ------------- | ---------------------------- |
| FLAG_ACTIVITY_NEW_TASK   | 为`Activity`指定`singleTask`启动模式, 效果和在`XML`中指定该启动模式相同 |
| FLAG_ACTIVITY_SINGLE_TOP | 指定Activity为`singleTop`模式 |
| FLAG_ACTIVITY_CLEAR_TOP  | 启动时，清除所有该`Activity`之上的`Activity`。如果是`singleTask`模式，则会调用该`Acitivity`的`onNewIntent()`; 如果是`standard`模式，则连同该`Activity`以及之上的所有`Activity`全部出栈，并创建新的该`Activity`入栈 |
| FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS|该`Acitivity`不会出现在历史`Activity`的列表中，等同于在XML指定属性`android:excludeFromRecents="true"`|
| FLAG_ACTIVITY_NO_HISTORY| 当该`Activity`启动其他`Activity`后, 该`Activity`就会消失了|


21、Activity属性中的标志
|Activity属性|作用|
|---|---|
|android:clearTaskOnLaunch="true"| 每次返回该`Activity`都会清除之上所有`Activity`|
|android:finishOnTaskLaunch="true"| 作用于本身，返回时，该`Activity`会被`finish`|
|android:alwaysRetainTaskState="true"| 免死金牌，不会被清理|

## 面试题

1、静态和动态设置启动模式，来启动Activity的区别？
> 1. 静态通过在xml中设置`android:lauchMode`属性来设置
> 1. 动态通过调用`intent.addFlags()`来设置Flags
> 1. 静态设置无无法为Activity设定`FLAG_ACTIVITY_CLEAR_TOP`标识
> 1. 动态设置无法为Activity指定`singleInstance`模式

## 参考资料
1. [Android的taskAffinity对四种launchMode的影响](https://www.cnblogs.com/yyz666/p/4674173.html)
1. [Android任务栈的完全解析](https://blog.csdn.net/qq_31860607/article/details/51956239)
1. [android 获取栈顶activty的方法总结（兼容API 5.0）](https://blog.csdn.net/djy1992/article/details/51728301)
