
# NavigationView
1、NavigationView的作用
>1. 配合`DrawerLayout`使用用于实现其中的`左侧菜单效果`
>1.  Google在5.0之后推出NavigationView，
>3. `左侧菜单效果`整体上分为两部分，上面一部分叫做`HeaderLayout`，下面的那些点击项都是`menu`。

2、NavigationView的使用
>1- 添加依赖
>2- 布局中使用Navigation
```xml
<android.support.v4.widget.DrawerLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <android.support.design.widget.NavigationView
        android:background="#ffffff"
        android:id="@+id/navigation_view"
        android:layout_width="320dp"
        android:layout_height="match_parent"
        android:layout_gravity="start" //指明菜单位置
        android:fitsSystemWindows="true"
        app:headerLayout="@layout/activity_star_menu_head" //菜单中头部样式
        app:menu="@menu/activity_star_menu">  //菜单中选项样式
    </android.support.design.widget.NavigationView>

    <LinearLayout>xxx主体xxx</LinearLayout>

</android.support.v4.widget.DrawerLayout>
```
>3- 菜单头部布局(activity_star_menu_head)
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <ImageView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:src="@drawable/cat"/>

</LinearLayout>
```
>4- 菜单内容(res/menu目录中-activity_star_menu)
```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <group android:id="@+id/item_one">
        <item
            android:id="@+id/home"
            android:checkable="true"
            android:icon="@mipmap/ic_launcher"
            android:title="首页"
            />
    </group>
    <group android:id="@+id/item_two">
        <item
            android:id="@+id/news"
            android:checkable="true"
            android:icon="@mipmap/ic_launcher"
            android:title="新闻"
            />
    </group>
    <group android:id="@+id/item_three">
        <item
            android:id="@+id/favorite"
            android:checkable="true"
            android:icon="@mipmap/ic_launcher"
            android:title="收藏"
            />
    </group>
    <group android:id="@+id/item_four">
        <item
            android:id="@+id/settings"
            android:checkable="true"
            android:icon="@mipmap/ic_launcher"
            android:title="设置"
            />
    </group>
</menu>
```
>5- Java中进行使用
```java
NavigationView navigationView = findViewById(R.id.navigation_view);

navigationView.setItemIconTintList(null);//恢复item icon的彩色
navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.home:
                        break;
                    case R.id.news:
                        break;
                    default:break;
                }
                drawerlayout.closeDrawer(navigationView);
                return true;
            }
});
```

3、NavigationView如何取消标题栏阴影
```xml
app:insetForeground="@android:color/transparent"
```

4、NavigationView的Menu问题
> 1. `group标签`会增加`横线效果`
