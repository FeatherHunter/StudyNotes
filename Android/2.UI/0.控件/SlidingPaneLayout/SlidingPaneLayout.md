

# SlidingPaneLayout

1、SlidingPaneLayout是什么
>1. 提供一种类似于`DrawerLayout`的侧滑菜单效果，“效果并不好”
>2. `xml`布局中第一个`ChildView`就是`左侧菜单的内容`，第二个`ChildView`就是`主体内容`

2、SlidingPaneLayout的使用
```xml
<android.support.v4.widget.SlidingPaneLayout
    android:id="@+id/md_slidingpanelayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <Button
        android:id="@+id/md_slidemenu_text"
        android:layout_width="150dp"
        android:layout_height="match_parent"
        xxx左侧内容xxx/>
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        xxx主体内容xxx
    </LinearLayout>

</android.support.v4.widget.SlidingPaneLayout>
```
```java
SlidingPaneLayout slidingPaneLayout = findViewById(R.id.md_slidingpanelayout);
Button button = findViewById(R.id.md_slidemenu_text);

button.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      //关闭Pane
        slidingPaneLayout.closePane();
    }
});
```

3、SlidingPaneLayout的方法
```java
// 1. 打开Pane
slidingPaneLayout.openPane();
// 2. 关闭Pane
slidingPaneLayout.closePane();
// 3. 右侧主体页面缩进去的阴影渐变色
slidingPaneLayout.setSliderFadeColor(Color.BLUE);
// 4. 左侧面板缩进去的阴影渐变色
slidingPaneLayout.setCoveredFadeColor(Color.GRAY);

// 5. 监听器
slidingPaneLayout.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {
    /**
     * 左侧面板在滑动
     * @param panel 被移走的主体View
     * @param slideOffset 滑动的百分比(0~1)
     */
    @Override
    public void onPanelSlide(View panel, float slideOffset) {

    }
    //左侧Pane已经打开
    @Override
    public void onPanelOpened(View panel) {

    }
    //左侧Pane已经关闭
    @Override
    public void onPanelClosed(View panel) {

    }
});
```
