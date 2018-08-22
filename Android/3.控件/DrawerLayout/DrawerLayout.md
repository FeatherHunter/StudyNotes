

# DrawerLayout-侧滑菜单
版本：2018/5/12-1(11:00)

1、DrawerLayout是什么？
>1. `Google`推出的`侧滑菜单`。
>2.

2、DrawerLayout的使用
>1. `侧滑菜单`的布局需要用`layout_gravity`属性指定。
>2. `主体View`的布局中`宽高`需要为`match_parent`且`不能有layout_gravity属性`
```xml
//布局文件
<?xml version="1.0" encoding="utf-8"?>
<android.support.v4.widget.DrawerLayout
    android:id="@+id/md_drawerlayout"
    xxx>
    <Button
        android:id="@+id/md_slidemenu_text"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        xxx
        android:layout_gravity="start"/>
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">
        xxx主体xxx
    </LinearLayout>
</android.support.v4.widget.DrawerLayout>
```
```java
DrawerLayout drawerLayout = findViewById(R.id.md_drawerlayout);
Button button = findViewById(R.id.md_slidemenu_text);
button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(button); //关闭侧滑菜单
            }
        });
```

3、DrawerLayout中`android:layout_gravity`属性
>1. `left/start`：菜单位于左侧
>2. `top/bottom`：菜单位于右侧

4、DrawerLayout的方法
>1-打开
```java
drawerLayout.openDrawer(button);
```
>2-关闭
```java
drawerLayout.closeDrawer(button);
```
>3-设置监听器(DrawerListener)
```java
drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
  //滑动时
    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }
//打开时
    @Override
    public void onDrawerOpened(View drawerView) {

    }
//关闭时
    @Override
    public void onDrawerClosed(View drawerView) {

    }
//状态改变时：{@link #STATE_IDLE}, {@link #STATE_DRAGGING} or {@link #STATE_SETTLING}.
    @Override
    public void onDrawerStateChanged(int newState) {

    }
});
```
>4-设置监听器(SimpleDrawerListener)
```java
//可以选择性实现其中的部分回调接口
drawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        super.onDrawerSlide(drawerView, slideOffset);
    }
});
```
