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
