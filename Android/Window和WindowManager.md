关键字：Window WindowManager Android

>1. 以面试题形式总结Window、WindowManager所有知识点
>2. 总结Window和WindowManager的基本使用以及概念
>3. 分析Window的内部机制
>4. 分析Activity、Dialog、Toast的Window创建过程

#Window和WindowManager

[TOC]

1、Window是什么？
> 1. 表示一个窗口的概念，是所有`View`的直接管理者，任何视图都通过`Window`呈现(单击事件由Window->DecorView->View; Activity的`setContentView`底层通过`Window`完成)
> 2. `Window`是一个抽象类，具体实现是`PhoneWindow`
> 3. 创建`Window`需要通过`WindowManager`创建
> 4. `WindowManager`是外界访问`Window`的入口
> 5. `Window`具体实现位于`WindowManagerService`中
> 6. `WindowManager`和`WindowManagerService`的交互是通过`IPC`完成

2、如果通过`WindowManager`添加`Window`(代码实现)？
```java
        //1. 控件
        Button button = new Button(this);
        button.setText("Window Button");
        //2. 布局参数
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT, 0, 0, PixelFormat.TRANSPARENT);
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.x = 100;
        layoutParams.y = 300;
        // 必须要有type不然会异常: the specified window type 0 is not valid
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        //3. 获取WindowManager并添加控件到Window中
        WindowManager windowManager = getWindowManager();
        windowManager.addView(button, layoutParams);
```
> 1. 注意一定要指定布局类型`layoutParams.type`
> 2. 需要动态申请`Draw over other apps`权限：http://blog.csdn.net/feather_wch/article/details/79185045

3、LayoutParams的`flags`属性
| Flags                 | 解释                                                                                                                         |
| --------------------- | ---------------------------------------------------------------------------------------------------------------------------- |
| FLAG_NOT_FOCUSABLE    | 表示`Window`不需要焦点，会同时启用`FLAG_NOT_TOUCH_MODAL`, 最终事件会直接传递到下层具有焦点的`Window`                         |
| FLAG_NOT_TOUCH_MODEL  | 将当前`Window`区域以外的单击事件传递给底层`Window`，当前区域内的单击事件自己处理(如果不开启，其他`Window`会无法收到单击事件) |
| FLAG_SHOW_WHEN_LOCKED | 可以让`Window`显示在锁屏的界面上                                                                                             |

4、LayoutParams的`type`属性
| Window类型 | 含义                                                               | Window层级            | Type参数                                |
| ---------- | ------------------------------------------------------------------ | --------------------- | --------------------------------------- |
| 应用Window | 对应着一个Activity                                                 | 1~99(视图最下层)      |                                         |
| 子Window   | 不能单独存在，需要附属在特定的父`Window`之中(如Dialog就是子Window) | 1000~1999             |                                         |
| 系统Window | 需要声明权限才能创建的`Window`，比如`Toast`和`系统状态栏`          | 2000~2999(视图最上层) | TYPE_SYSTEM_OVERLAY / TYPE_SYSTEM_ERROR |
>需要在AndroidManifest中声明权限：`SYSTEM_ALERT_WINDOW`

5、WindowManager的三个主要功能：添加、更新、删除View
```java
public interface ViewManager
{
    public void addView(View view, ViewGroup.LayoutParams params); //添加View
    public void updateViewLayout(View view, ViewGroup.LayoutParams params); //更新View
    public void removeView(View view); //删除View
}
```

6、通过WindowManager实现拖动View的效果
>1. 给`View`设置`onTouchListener`监听器
> 2. 在`onTouch`方法中根据当前坐标，来更新View`updateViewLayout`
```java
mButton.setOnTouchListener(new View.OnTouchListener() {
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();
        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE:
                mLayoutParams.x = rawX;
                mLayoutParams.y = rawY;
                mWindowManager.updateViewLayout(mButton, mLayoutParams);
                break;
        }
        return false;
    }
});
```

## Window的内部机制
7、Window概念解析
> 1. Window和View通过`ViewRootImpl`建立联系
> 2. `Window`并不是实际存在的，而是以`View`的形式存在
> 3. `WindowManager`的三个接口方法也是针对`View`的
> 4. 实际使用中无法直接访问Window，必须通过`WindowManager`
> 5. View是视图的呈现方式，但是不能单独存在，必须依附在`Window`这个抽象的概念上

8、Window的添加过程`addView`
>1. `WindowManager`是一个接口，真正实现类是`WindowManagerImpl`，并最终以代理模式交给`WindowManagerGlobal`实现
```java
//WindowManagerImpl中的三大方法都交给WindowManangerGlobal实现
        public void addView(View view, ViewGroup.LayoutParams params) {
            applyDefaultToken(params);
            mGlobal.addView(view, params, mContext.getDisplay(), mParentWindow);
        }
        public void updateViewLayout(View view, ViewGroup.LayoutParams params) {
            applyDefaultToken(params);
            mGlobal.updateViewLayout(view, params);
        }
        public void removeView(View view) {
            mGlobal.removeView(view, false);
        }
```
>2. `WindowManagerGlobal`的`setView`会通过`ViewRootImpl`的`setView`建立`View`和`Window`的联系
```java
public void addView(View view, ViewGroup.LayoutParams params, Display display, Window parentWindow) {
        //1. 检查参数是否合法
        if (view == null) { throw new IllegalArgumentException("view must not be null");}
        if (display == null) { throw new IllegalArgumentException("display must not be null");}
        if (!(params instanceof WindowManager.LayoutParams)) {throw new IllegalArgumentException("Params must be WindowManager.LayoutParams");}
        //2. 如果是子Window会调整布局参数
        final WindowManager.LayoutParams wparams = (WindowManager.LayoutParams) params;
        if (parentWindow != null) {
            parentWindow.adjustLayoutParamsForSubWindow(wparams);
        } else {
            //3. 如果没有父Window则根据该app的硬件加速设置，设置给该View
            ...
        }
        synchronized (mLock) {
            //...省略....
            //4. 创建Window所对应的ViewRootImpl
            ViewRootImpl root = new ViewRootImpl(view.getContext(), display);
            //5. 将Window对应的ViewRootImpl、View、布局参数添加到列表中
            view.setLayoutParams(wparams);
            mViews.add(view);//将Window对应的View添加到列表中
            mRoots.add(root);//将ViewRootImpl添加到列表中
            mParams.add(wparams);//将Window对应的布局参数添加到列表中
            //6. 通过ViewRootImpl完成View的绘制过程，以及Window的添加过程
            try {
                root.setView(view, wparams, panelParentView);
            } catch (RuntimeException e) {
                //...
            }
        }
    }
```
>3. `ViewRootImpl`的`setView`会通过`requestLayout()`完成异步刷新请求(绘制View)
```java
    public void requestLayout() {
        if (!mHandlingLayoutInLayoutRequest) {
            checkThread();
            mLayoutRequested = true;
            //1. 实际是View绘制的入口(测量、布局、重绘)
            scheduleTraversals();
        }
    }
```
>4. `ViewRootImpl`的`setView`最后会通过`WindowSession`完成Window的添加过程(一次IPC调用)
```java
    try {
        ...
        //1. WindowSession的类型是IWindowSession，Binder对象(IPC调用)，真正实现类是Session
        res = mWindowSession.addToDisplay(mWindow, mSeq, mWindowAttributes,
                getHostVisibility(), mDisplay.getDisplayId(),
                mAttachInfo.mContentInsets, mAttachInfo.mStableInsets,
                mAttachInfo.mOutsets, mInputChannel);
    } catch (RemoteException e) {
        ...
        unscheduleTraversals();
        ...
    }
```
>5. `WindowSession`的实现类`Session`会在`addToDisplay`中完成Window的添加
```java
//通过`WindowManagerService`实现Window的添加
mService.addWindow(...,window,...)
```

9、Window的删除过程`removeView`
>1. 如同`addView`一样最终由`WindowManagerGlobal`实现
```Java
    public void removeView(View view, boolean immediate) {
            ...
            synchronized (mLock) {
                //1. 查询待删除的View的索引
                int index = findViewLocked(view, true);
                View curView = mRoots.get(index).getView();
                //2. 进行进一步删除
                removeViewLocked(index, immediate);
                ...
            }
    }
    private void removeViewLocked(int index, boolean immediate) {
        ViewRootImpl root = mRoots.get(index);
        View view = root.getView();
        ...
        //1. ViewRoot调用die，发送删除请求后立即返回，此时View并没有完成删除
        boolean deferred = root.die(immediate);
        if (view != null) {
            view.assignParent(null);
            //2. 将View添加到等待删除的列表中
            if (deferred) {
                mDyingViews.add(view);
            }
        }
    }
```
>2. `WindowManager`中提供了两种删除接口：`removeView`异步删除、`removeViewImmediate`同步删除(不建议使用)
>3. `die()`中如果是异步删除，会发送`MES_DIE`小心，`ViewRootImpl`的Hnalder会处理此消息，并调用`doDie()`方法(均为`ViewRootImpl`的方法)
>4. `die()`中如果是同步删除, 不会发送消息，直接调用`doDie()`方法
>5. `doDie()`会调用`dispatchDetachedFromWindow`真正删除View

10、`ViewRootImpl`的`dispatchDetachedFromWindow`方法的主要操作
>1. 垃圾回收相关操作
>2. 通过`Session`的`remove()`删除Window, 这也是IPC操作，最终会调用`WindowManagerService`的`removeWindow`方法
>3. 调用View的`dispacthDetachedFromWindow`，内部会调用`onDetachedFromWindow()`以及`onDetachedFromWindowInternal()`方法：onDetachedFromWindow在View被移除时调用，可以进行一些终止动画、线程之类的操作
>4. 调用`WindowManagerGlobal`的`doRemoveView`刷新数据，将Window对应的所有对象从列表中删除

11、Window的更新过程`updateViewLayout`
```java
    public void updateViewLayout(View view, ViewGroup.LayoutParams params) {
        ...
        final WindowManager.LayoutParams wparams = (WindowManager.LayoutParams)params;
        //1. 给View设置新布局
        view.setLayoutParams(wparams);
        //2. 按照index从列表中删除旧布局，并添加新布局
        synchronized (mLock) {
            int index = findViewLocked(view, true);
            ViewRootImpl root = mRoots.get(index);
            mParams.remove(index);
            mParams.add(index, wparams);
            //3. 更新ViewRootImpl中的布局(会对View进行测量、布局、重绘，)
            root.setLayoutParams(wparams, false);
        }
    }
```
>1. 和添加删除类似，最终调用`WindowManagerGlobal`的`updateViewLayout`方法
>2. `root.setLayoutParams`会对View进行重新布局——测量、布局、重绘
>3. `root.setLayoutParams`还会通过`WindowSession`更新`Window`的视图——最终通过`WindowManagerService`的`relayoutWindow()`实现(IPC)

## Window的创建过程
12、Activity的启动过程
>1. 最终会由`ActivityThread`中的`performLauchActivity`来完成整个启动过程
>2. `performLauchActivity`内部会通过`类加载器`创建Activity的实例对象
>3. 并为Activity的实例对象调用`attach`方法，为其关联运行过程中所以来的上下文环境变量
>4. `attch`方法中，系统会创建Activity所属的`Window对象`，并为其设置回调接口
>5. `Window`对象的创建是通过`PolicyManager`的`makeNewWindow`方法实现。
>6. `Activity`实现了`window`的`callback接口`，因此外界状态改变时会回调Activity的方法(onAttachedToWindow、dispatchTouchEvent等等)

13、PolicyManager是什么
>1. 是一个策略类
>2. Activity的`Window`就是通过`PolicyManager`的一个工厂方法创建
>3. `PolicyManager`实现的工厂方法全部在策略接口`IPolicy`中声明
>4. `PolicyManager`的实现类是`Policy`类
```java
//Window的实现类就是`PhoneWindow`
public Window makeNewWindow(Context context){
  return new PhoneWindow(context);
}
```

14、Activity的视图如何附加到Window上？
>1. `Activity`的视图由`setContentView`方法提供
>2. `setContentView`中直接将实现交给`Window`处理`getWindow().setContentView(layoutResID)`, 因此只需要看`PhoneWindow`的`setContentView`方法
>3. `PhoneWindow`中的`setContentView`最终将View视图附加到Window上

15、`PhoneWindow`的`setContentView`方法中创建`DecorView`的原理
>1. `DecorView`是一个FrameLayout
> 2. `DecorView`是Activity中的顶级View，内不包含标题栏(根据主题可以没有)和内部栏(id是android.R.id.content)
> 3. `DecorView`的创建由`installDecor()`中的`generateDecor()`方法完成
> 4. `installDecor()`中`PhoneWindow`还需套通过`generateLayout`方法加载具体的布局文件到`DecorView`中

16、`PhoneWindow`的`setContentView`方法中添加View到DecorView的原理
>1. 将`Activity`的视图添加到`DecorView`的`mContentParent`中
>2. `mContentParent`就是`DecorView`中的内容栏(android.R.id.content)
```java
mLayoutInflater.inflate(layoutResID, mContentParent);
```

17、`PhoneWindow`的`setContentView`方法中回调`Activity的onContentChanged()`方法
>1. 用于通知Activity视图已经发生改变
> 2. 表示`Activity`的布局文件已经添加到`DecorView`的`mContentParent`中
> 3. `Activity`中`onContentChanged()`是空实现，可以自定义处理该回调
```java
final Callback cb = getCallback(); //Activity实现了Window的Callback接口
if (cb != null && !isDestroyed()) {
      cb.onContentChanged();
}
```

18、`DecorView`何时才被`WindowManager`真正添加到`Window`中？
>1. 即使Activity的布局已经成功添加到`DecorView`中，`DecorView`此时还没有添加到`Window`中
>2. `ActivityThread`的`handleResumeActivity`方法中，首先会调用`Activity`的`onResume`方法，接着调用`Activity`的`makeVisible()`方法
>3. `makeVisible()`中完成了`DecorView`的添加和显示两个过程

```java
    void makeVisible() {
      //1. 将`DecorView`添加到`Window`中(通过WindowManager)
        if (!mWindowAdded) {
            ViewManager wm = getWindowManager();
            wm.addView(mDecor, getWindow().getAttributes());
            mWindowAdded = true;
        }
      //2. 将DecorView显示出来
        mDecor.setVisibility(View.VISIBLE);
    }
```

19、 Dialog的Window创建过程
>1. 创建Window——同样是通过`PolicyManager`的`makeNewWindow`方法完成，与Activity创建过程一致
> 2. 初始化`DecorView`并将`Dialog`的视图添加到`DecorView`中——和Activity一致(setContentView)
> 3. 将`DecorView`添加到`Window`中并显示——在`Dialog`的`show`方法中，通过`WindowManager`将`DecorView`添加到`Window`中(mWindowManager.addView(mDecor, 1))
> 4. `Dialog`关闭时会通过`WindowManager`来移除`DecorView`：`mWindowManager.removeViewImmediate(mDecor)`
> 5. `Dialog`必须采用`Activity`的`Context`，因为有应用`token`(`Application`的`Context`没有应用token)，也可以将`Dialog`的`Window`通过`type`设置为系统Window就不再需要token。

20、Toast的内部机制介绍
>1. `Toast`也是基于`Window`来实现的
> 2. `Toast`具有定时取消功能，系统采用`Handler`实现
> 3. `Toast`内部有两类IPC过程：1-Toast访问NotificationManagerService；2-NotificationManagerService回调`Toast`的`TN`接口
