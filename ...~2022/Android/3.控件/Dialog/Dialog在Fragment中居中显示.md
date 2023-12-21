
转载请注明链接：https://blog.csdn.net/feather_wch/article/details/88042877

> 如今一个需求Dialog正好处于一个播放窗口(Fragment)的正中央，原始Dialog的Center属性只能位于窗口的中央，无法满足需求。

# Dialog在Fragment中居中显示

版本：2019/2/28-20:02

---

## 源码

1. Dialog的代码自己实现。
2. 通过DialogUtils在show之前进行调整。
```java
// 1、传入Dialog、Fragment、true
DialogUtils.setDialogInFragmentsCenter(mDialog, MyFragment.this, true);
// 2、展示Dialog
mDialog.show();
```

### 状态栏的处理

1、具有状态栏，且状态为全屏模式。
```java
DialogUtils.setDialogInFragmentsCenter(mDialog, MyFragment.this, true);
```

2、没有状态栏(如手机横屏进行全屏播放)、有状态栏但是不会使用到状态栏的区域
```java
DialogUtils.setDialogInFragmentsCenter(mDialog, MyFragment.this, false);
```


### DialogUtils

1、DialogUtils.java源码
```java
/**=======================================
 * @author 猎羽
 * @function 让Dialog处于指定Fragment的正中间
 *=======================================*/

public class DialogUtils
{

    private static final String TAG = DialogUtils.class.getName();

    /**
     *
     * @param dialog
     * @param fragment
     * @param hasStatusBar 该页面是否具有statusBar。有，在不包括状态栏的区域居中；没有，在该整个区域居中。
     */
    public static void setDialogInFragmentsCenter(Dialog dialog, Fragment fragment, final boolean hasStatusBar){

        // 1、获取Dialog所述的Window，以及LayoutParams(布局参数)
        final Window dialogWindow = dialog.getWindow();
        dialogWindow.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        final WindowManager.LayoutParams lp = dialogWindow.getAttributes();

        // 2、获取到Fragment所处的View
        final View fragmentView = fragment.getView();
        // 3、获取到Dialog的DecorView
        final View dialogView = dialogWindow.getDecorView();
        dialogView.setVisibility(View.INVISIBLE);
        dialogView.post(new Runnable()
        {
            @Override
            public void run()
            {
                // 4、获取到Fragment左上角点的距离整个屏幕的距离
                int[] location = new int[2];
                fragmentView.getLocationOnScreen(location);
                int fragmentWidth = fragmentView.getMeasuredWidth();
                int fragmentHeight = fragmentView.getMeasuredHeight();


                // 5、获取到Dialog的宽高
                int dialogWith = dialogView.getMeasuredWidth();
                int dialogHeight = dialogView.getMeasuredHeight();

                // 6、获取到标题栏的宽高
                int mStatusBarHeight = 0;

                if(hasStatusBar){
                    // 有状态栏，需要去除状态栏的影响。
                    mStatusBarHeight = StatusBarCompat.getStatusBarHeight();
                }


                // 7、Dialog从左上角开始排
                dialogWindow.setGravity(Gravity.START | Gravity.TOP);
                // x = fragment的x + fragment宽度/2 - Dialog宽度/2
                lp.x = location[0] + fragmentWidth / 2 - dialogWith / 2;
                // y = fragment的y + fragment高度/2 - Dialog高度/2 - 状态栏高度(Gravity.TOP会自动偏移状态栏的高度)
                lp.y = location[1] + fragmentHeight / 2 - dialogHeight / 2 - mStatusBarHeight;

                // 8、设置Window的属性
                dialogWindow.setAttributes(lp);
                // 9、展示出Dialog
                dialogView.setVisibility(View.VISIBLE);

                LogEx.d(TAG, "x = " + location[0] + " y = " + location[1] + " width = " + fragmentWidth + " height = " + fragmentHeight);
                LogEx.d(TAG, "lp.x = " + lp.x + " lp.y = " + lp.y);
                LogEx.d(TAG, "dialogWith = " + dialogWith + " dialogHeight = " + dialogHeight);
            }
        });
    }
}
```

2、获取状态栏高度
```java
public static int getStatusBarHeight()
{
    /**
     * 获取状态栏高度——方法2
     * */
    int statusBarHeight = -1;
    try
    {
        Class<?> clazz = Class.forName("com.android.internal.R$dimen");
        Object object = clazz.newInstance();
        int height = Integer.parseInt(clazz.getField("status_bar_height")
                .get(object).toString());
        statusBarHeight = BaseApp.getInstance().getApplicationContext().getResources().getDimensionPixelSize(height);
    }
    catch (Exception e)
    {
        e.printStackTrace();
    }
    LogEx.e(tag, "状态栏-方法2:" + statusBarHeight);
    return statusBarHeight;
}
```
