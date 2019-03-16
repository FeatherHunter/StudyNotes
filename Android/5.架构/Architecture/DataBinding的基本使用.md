
转载请注明链接: https://blog.csdn.net/feather_wch/article/details/88597266

# DataBinding的基本使用

版本号:2019-03-16(12:00)

---

[toc]
## 集成DataBinding

### SDK Manager中进行下载

1、SDK Manager的Tools中下载`Android Support Repository`

### build.gradle中开启

2、根目录build.gradle中增加能下载的仓库
```
        maven { url 'http://maven.aliyun.com/nexus/content/groups/public/' }
        maven { url 'http://maven.aliyun.com/nexus/content/repositories/jcenter' }
        maven { url 'http://maven.aliyun.com/nexus/content/repositories/google' }
        maven { url 'http://maven.aliyun.com/nexus/content/repositories/gradle-plugin' }
```

3、app的build.gradle中开启databinding
```
    dataBinding{
        enabled = true
    }
```

4、AS 3.1开始使用新的Databinding 增加编译器
> gradle.properties
```
android.databinding.enableV2=true
```

## 基础知识

### import

1、import用于导入需要用的类，避免在xml布局中报错
```xml
<data>
    <import type="android.view.View"/>
</data>
```
> 就可以使用View了
```xml
<TextView
   android:text="@{user.lastName}"
   android:layout_width="wrap_content"
   android:layout_height="wrap_content"
   android:visibility="@{user.isAdult ? View.VISIBLE : View.GONE}"/>
```

### 类型别名

2、如果导入的两个类名称一致，避免混乱，应该使用别名
```xml
// View
<import type="android.view.View"/>
// 还是View
<import type="com.example.real.estate.View"
        alias="Vista"/>
```
> 用Vista就能代指`com.example.real.estate.View`

### 导入其它类

3、导入其它类
```xml
<data>
    <import type="com.example.User"/>
    <import type="java.util.List"/>
    <variable name="user" type="User"/>
    <variable name="userList" type="List<User>"/>
</data>
```

4、也可以进行类型转换
```xml
<TextView
   android:text="@{((User)(object)).lastName}"
   android:layout_width="wrap_content"
   android:layout_height="wrap_content"/>
```

#### context

5、布局中有个特殊的变量为context，是根View的getContext()获得的对象
> 该名`context`通过显式的variable，可以被重写掉。

### include

1、databinding支持从父布局将变量传递到内部include包含的布局中
```xml
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:bind="http://schemas.android.com/apk/res-auto">
   <data>
       <variable name="user" type="com.example.User"/>
   </data>

<!-- WORK! -->
   <LinearLayout
       android:orientation="vertical">

       <include layout="@layout/name"
           bind:user="@{user}"/>
       <include layout="@layout/contact"
           bind:user="@{user}"/>

   </LinearLayout>
</layout>
```
> 但是不支持`merge标签`
```xml
<layout xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:bind="http://schemas.android.com/apk/res-auto">
   <data>
       <variable name="user" type="com.example.User"/>
   </data>

<!-- Doesn't work -->
   <merge>
       <include layout="@layout/name"
           bind:user="@{user}"/>
       <include layout="@layout/contact"
           bind:user="@{user}"/>
   </merge>

</layout>
```

### 基本使用

1、Databinding的使用是在布局中通过标签`layout、data、variable`实现
> 1-使用如下: 将用户类User，作为别名user。将账号和密码和对应控件绑定。
```xml
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android">
    <data>
        <variable
            name="user"
            type="com.hao.architecture.User"/>
    </data>
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">

        <Button
            android:id="@+id/account_btn"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@{user.account}"/>

        <Button
            android:id="@+id/password_btn"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@{user.password}"/>

    </LinearLayout>
</layout>

```
> 2-User类
```java
package com.hao.architecture;

public class User {
    String account;
    String password;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

```

### 报错: 找不到android.databinding

1、报错的原因是，后来更改了整体的包名，导致生成的databiding类出错
> 将整个项目clean，重新生成文件

## 数据绑定和注入

1、因为布局是`activity_databinding_layout.xml`因此生成的类是`ActivityDatabindingLayoutBinding`

2、Activity的onCreate中使用DataBinding
```java
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  下面的这句话已经废弃，不再使用
        //setContentView(R.layout.activity_databinding_layout);
        // 1、使用该方法
        ActivityDatabindingLayoutBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_databinding_layout);
        User user = new User("feather", "123456");
        // 2、绑定数据
        binding.setUser(user);
    }
```

### Fragment、RecyclerView中使用数据绑定

3、Fragment、RecyclerView中使用数据绑定，可以使用下面的方法
```java
ListItemBinding binding = ListItemBinding.inflate(layoutInflater, viewGroup, false);
```
```java
ListItemBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.list_item, viewGroup, false);
```

## 表达式

1、DataBinding在xml的布局中还可以使用表达式

2、使用字符串拼接
> 显示效果为: 账号:xxxxx
```xml
        <Button
            xxx
            android:text='@{@string/account+ ":" + user.account}'/>
```
> 1. String.xml需要有对应词条: `<string name="account">账号</string>`
> 1. 外层一定要是单引号，不然内层的`"文本"`会导致编译报错。

3、数学运算、位运算、位移、比较都可以使用
```
+ - / * %
&& ||
& | ^
+ - ! ~
>> >>> <<
== > < >= <= (Note that < needs to be escaped as &lt;)
```

4、instanceof的使用

5、控制控件可见还是不可见
> 1-设置visibility
```xml
<Button
    android:visibility="@{user.age >= 18 ? View.VISIBLE : View.GONE}"/>
```
> 2-引入View
```xml
<data>
    <variable
        name="user"
        type="com.hao.architecture.User"/>
    <import type="android.view.View" />
</data>
```

6、如果User的account为null的处理
> 1. 如果不做任何处理，会导致显示文本为`null`
> 1. 使用三元操作符进行处理
```xml
android:text='@{user.account != null ? user.account : ""}'
```
> 3-使用聚合的null判断，和上面等效
```xml
android:text='@{user.account ?? ""}'
```
### DataBinding对于空指针异常的处理

1、DataBinding会自动处理空指针异常
> 1. 如果String为null，就赋值为`null`
> 1. 如果int为空，就赋值为`0`

### 报错:Identifiers must have user defined types from the XML file. View is missing it

1、解决原因：在设置控件的显示状况时，使用了View，却没有引入
> 引入View
```xml
    <data>
        <variable
            name="user"
            type="com.hao.architecture.User"/>
        <import type="android.view.View" />
    </data>
```

## 集合的使用

1、Databinding中还可以使用集合，如: List、Map、SparseArray等等
> 1-布局中引入。`记住<这个符号需要使用&lt;来表示`
```xml
    <data>
        <import type="android.util.SparseArray"/>
        <import type="java.util.Map"/>
        <import type="java.util.List"/>

        <variable name="list" type="List&lt;String>"/>
        <variable name="sparse" type="SparseArray&lt;String>"/>
        <variable name="map" type="Map&lt;String, String>"/>
        <variable name="index" type="int"/>
        <variable name="key" type="String"/>
    </data>

```
> 2-布局中使用
```
android:text="@{list[index]}"
…
android:text="@{sparse[index]}"
…
android:text="@{map[key]}"
```
> 3-Activity中注入数据
```java
// HashMap，其他的同理
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("password", "1234567890");

        binding.setMap(hashMap); // HashMap
        binding.setKey("password"); // Key值
```


## 资源的使用

#### plural

1、plural复数形式字符串的使用
```
// 复数形式字符串
  Have an orange
  Have %d oranges

// 使用
android:text="@{@plurals/orange(orangeCount, orangeCount)}"
```

#### 需要改写形式的资源

1、需要改写形式的资源
|Type|	Normal reference|	Expression reference|
|---|---|---|
|String[]|	@array|	@stringArray|
|int[]|	@array|	@intArray|
|TypedArray|	@array|	@typedArray|
|Animator|	@animator|	@animator|
|StateListAnimator|	@animator|	@stateListAnimator|
|color int|	@color|	@color|
|ColorStateList|	@color|	@colorStateList|

## 事件处理

1、View的事件处理对应的方法有哪些
|Class|	Listener setter|	Attribute|
|---|---|---|
|View| setOnClickListener(View.OnClickListener)| android:onClick|
|SearchView|	setOnSearchClickListener(View.OnClickListener)|	android:onSearchClick|
|ZoomControls|	setOnZoomInClickListener(View.OnClickListener)|	android:onZoomIn|
|ZoomControls|	setOnZoomOutClickListener(View.OnClickListener)|	android:onZoomOut|

### 方法引用

2、方法引用的好处
> 1. 可以在编译时间被检查，如果方法不存在，或者签名错误
> 1. 绑定的监听器是在数据被绑定之后才真正创建了监听器的内部实现，而不是事件触发的时候。
> 1. 如果你希望在事件发生时，使用当时动态绑定的新数据, 以及任意使用各种表达式，应该用`监听器绑定`的方式

3、方法引用的使用方法
> 1-布局中引入
```xml
<variable name="handlers" type="com.hao.architecture.DataBindingActivity.ClickHandlers"/>
```
> 2-布局中使用该监听器
```xml
<Button
      android:onClick="@{handlers::onClickPassword}"/>
```
> 3-代码中定义监听器
```java
public class DataBindingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // xxx
    }

    public class ClickHandlers{
        public void onClickPassword(View view){
            Toast.makeText(DataBindingActivity.this, "密码已经验证", Toast.LENGTH_SHORT).show();
        }
    }
}

```
> 4-设置监听器
```java
binding.setHandlers(new ClickHandlers());
```


### 绑定监听器

3、绑定监听器和方法引用的区别在于，绑定监听器可以执行任意的表达语句

4、监听器绑定的基本用法
> 1-xml, theView代表当前的View控件
```xml
<variable name="handlers" type="com.hao.architecture.DataBindingActivity.ClickHandlers"/>

<Button
    android:onClick="@{(theView)->handlers.onClickPassword(theView)}"/>
```
> 2-Java
```java
binding.setHandlers(new ClickHandlers());
```

5、使用当前绑定的数据的内容
> 1-监听器, 使用了用户的数据。
```java
    public class ClickHandlers{
        public void onClickPassword(User user){
            Toast.makeText(DataBindingActivity.this, user.password + ": 密码已经验证", Toast.LENGTH_SHORT).show();
        }
    }
```
> 2-xml中进行指定
```xml
<Button
    android:onClick="@{()->handlers.onClickPassword(user)}"/>
```
> 3-Java中设置
```java
binding.setHandlers(new ClickHandlers());
```

6、利用监听器绑定使用复杂的表达式
> 1. View可见，做一些事。
> 1. View不可见，什么都不做。
```xml
android:onClick="@{(v) -> v.isVisible() ? doSomething() : void}"
```

## Binding adapters


## 知识扩展

### @plurals

1、android中的Plurals（Quantity Strings）类型的作用
> 在不同语言中，可能出现单复数的情况，根据不同的数量，选择不同的语句是很重要的
> 例如: 1 device 和 2 devices 的区别

2、@plurals的语法
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <plurals
        name="plural_name">
        <item
            quantity=["zero" | "one" | "two" | "few" | "many" | "other"]
            >text_string</item>
    </plurals>
</resources>
```

3、使用
> 1-xml中定义资源
```xml
<！--定义到资源文件即可 -->
<plurals name="subtitle_plural">
    <!--在使用时，可以根据数量来选择不同的字符串-->
    <!--还有zero、few等其它选项-->
    <item quantity="one">%s crime</item>
    <item quantity="other">%s crimes</item>
</plurals>
```
> 2-Java使用
```java
String subtitle = getResources().getQuantityString(
        R.plurals.subtitle_plural,  // 1. 文本id
        1,   // 2. 数量，对应于"zero"、"two"等等
        1);  // 3. 填充占位符的内容
```


## 问题汇总

## 参考资料
