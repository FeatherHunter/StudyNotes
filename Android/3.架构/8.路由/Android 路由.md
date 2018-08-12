转载请注明链接：https://blog.csdn.net/feather_wch/article/details/81605300

>可以从文章末尾的参考资料中进行学习

# Android 路由

版本：2018/8/12-1

---

[TOC]

1、什么是路由？
>1. 根据路由表将页面请求分发到指定页面
>1. 映射页面跳转关系，也包含跳转相关的一切功能。

2、使用场景
>1. App接收到一个通知，点击通知打开App的某个页面
>1. 浏览器App中点击某个链接打开App的某个页面
>1. 运营活动需求，动态把原生的页面替换成H5页面
>1. 打开页面需要某些条件，先验证完条件，再去打开那个页面
>1. 不合法的打开App的页面被屏蔽掉
>1. H5打开链接在所有平台都一样，方便统一跳转
>1. App存在就打开页面，不存在就去下载页面下载,只有Google的App Link支持

3、为什么要有路由？
>1. 显示Intent：项目庞大以后，类依赖耦合太大，不适合组件化拆分
>1. 隐式Intent：协作困难，调用时候不知道调什么参数
>1. 每个注册了Scheme的Activity都可以直接打开，有安全风险
>1. AndroidMainfest集中式管理比较臃肿
>1. 无法动态修改路由，如果页面出错，无法动态降级
>1. 无法动态拦截跳转，譬如未登录的情况下，打开登录页面，登录成功后接着打开刚才想打开的页面
>1. H5、Android、iOS地址不一样，不利于统一跳转
>1. 复杂的业务场景下（比如电商），灵活性比较强，很多功能都是运营人员动态配置的，比如下发一个活动页面，事先并不知道具体的目标页面，提前做好页面映射，便可以自由配置。
>1. App一般都会走向组件化、插件化的道路，而组件化、插件化的前提就是解耦，首先要做的就是解耦页面之间的依赖关系。
>1. 简化代码。数行跳转代码精简成一行代码。

4、怎样是好的路由库
> 1. 使用简单、入侵低、便于维护
> 1.

5、路由库的本质
>1. 注册再转发，围绕着转发可以进行各种操作，拦截，替换，参数获取等等
>2. 其他Apt、Rxjava都只是为了方便使用

6、应用场景
> 1. 从外部URL映射到内部页面，以及参数传递与解析
> 1. 跨模块页面跳转，模块间解耦
> 1. 拦截跳转过程，处理登陆、埋点等逻辑
> 1. 跨模块API调用，通过控制反转来做组件解耦

## 路由库

1、目前市面上路由库有哪些？
> 1. 阿里 ARouter
> 1. Airbnb DeepLinkDispatch
> 1. 天猫 统跳协议
> 1. ActivityRouter
> 1. OkDeepLink

![路由库](https://user-gold-cdn.xitu.io/2018/2/13/1618ebc579ef37b6?w=2862&h=1496&f=png&s=804308)


### 设计方案

1、路由的设计方案需要涉及到哪些方面？
> 1. 路由注册
> 1. 路由查找
> 1. 路由分发
> 1. 动态替换
> 1. 动态拦截
> 1. 安全拦截
> 1. 方法调用
> 1. 结果返回
> 1. Module接入不同app

#### 路由注册

1、AndroidManifest的缺点
>AndroidManifest里面的acitivity声明scheme码是不安全的(所有App都可以打开这个页面)

2、三种路由注册方式：
> 1. 注解产生路由表，通过DispatchActivity转发
> 1. AndroidManifest注册，将其export=fasle，但是再通过DispatchActivity转发Intent(天猫的做法)，好处是路由查找都是系统调用，省掉了维护路由表的过程，但是AndroidManifest配置还是比较不方便
> 1. 注解自动修改AndroidManifest，可以避免路由表汇总的问题：用自定义Lint扫描出注解相关的Activity，然后在processManifestTask后面修改Manifest

#### 汇总路由表

1、APT存在的问题
>1. 使用Apt会造成每个module都要手动注册
>2. 因为APT是在javacompile任务前插入了一个task，所以只对自己的moudle处理注解

## ARouter


1、ARouter的特点
> 1. 支持直接解析标准URL进行跳转，并自动注入参数到目标页面中
> 1. 支持多模块工程使用
> 1. 支持添加多个拦截器，自定义拦截顺序
> 1. 支持依赖注入，可单独作为依赖注入框架使用
> 1. 支持InstantRun
> 1. 支持MultiDex(Google方案)
> 1. 映射关系按组分类、多级管理，按需初始化
> 1. 支持用户指定全局降级与局部降级策略
> 1. 页面、拦截器、服务等组件均自动注册到框架
> 1. 支持多种方式配置转场动画
> 1. 支持获取Fragment
> 1. 完全支持Kotlin以及混编


----

[阿里巴巴ARouter-Github链接点击这里](https://github.com/alibaba/ARouter)

### 基础功能

#### 依赖添加

>build.gradle(Module:app)
```xml
android {
    defaultConfig {
        ....
        javaCompileOptions {
            annotationProcessorOptions {
                arguments = [AROUTER_MODULE_NAME: project.getName()]
            }
        }
    }
}

dependencies {
    ....
    // 替换成最新版本, 需要注意的是api要与compiler匹配使用，均使用最新版可以保证兼容
    implementation 'com.alibaba:arouter-api:1.4.0'
    annotationProcessor 'com.alibaba:arouter-compiler:1.2.0'
}
```

#### SDK初始化
>在Application中进行ARouter SDK初始化，并且控制调试信息。
```java
public class BaseApplication extends Application{
    //ARouter debug开关：true-open;false-close
    private boolean isDebugARouter = true;
    @Override
    public void onCreate() {
        super.onCreate();
        // 1.必须在init之前调用
        if (isDebugARouter) {           // These two lines must be written before init, otherwise these configurations will be invalid in the init process
            ARouter.openLog();     // Print log
            ARouter.openDebug();   // Turn on debugging mode (If you are running in InstantRun mode, you must turn on debug mode! Online version needs to be closed, otherwise there is a security risk)
        }
        // 2.初始化
        ARouter.init(BaseApplication.this); // As early as possible, it is recommended to initialize in the Application
    }
}
```

#### 路径统一管理
>路径统一管理, 也可以不用该方式。
```java
public class ARouterConstants {
  //路径必须要最少是/xx/xx
    public static final String ACTIVITY_URL_LOGIN= "/app/LoginActivity";
}
```

#### 增加注解
```java
//使用该注解，并且指明path。
@Route(path = ARouterConstants.ACTIVITY_URL_LOGIN)
public class LoginActivity extends AppCompatActivity{
}
```

#### 发起路由操作
>直接跳转或者携带参数。
```java
// 1. 应用内简单的跳转(通过URL跳转在'进阶用法'中)
ARouter.getInstance().build(ARouterConstants.ACTIVITY_URL_LOGIN).navigation();

// 2. 跳转并携带参数
ARouter.getInstance().build(ARouterConstants.ACTIVITY_URL_LOGIN)
                        .withLong("key1", 666L)
                        .withString("key2", "888")
                        .withSerializable("key3", new ProfileBean())
                        .navigation();
```


#### 混淆处理
>添加混淆规则(如果使用了Proguard)
```xml
-keep public class com.alibaba.android.arouter.routes.**{*;}
-keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}

# 如果使用了 byType 的方式获取 Service，需添加下面规则，保护接口
-keep interface * implements com.alibaba.android.arouter.facade.template.IProvider

# 如果使用了 单类注入，即不定义接口实现 IProvider，需添加下面规则，保护实现
-keep class * implements com.alibaba.android.arouter.facade.template.IProvider
```
#### 路由表自动加载
>使用 Gradle 插件实现路由表的自动加载
```xml
apply plugin: 'com.alibaba.arouter'

buildscript {
    repositories {
        jcenter()
    }

    dependencies {
        classpath "com.alibaba:arouter-register:1.0.2"
    }
}
```
>1. 可选使用
>1. 该插件必须搭配 api 1.3.0 以上版本使用！
>1. 通过 ARouter 提供的注册插件进行路由表的自动加载(power by AutoRegister)， 默认通过扫描 dex 的方式 进行加载通过 gradle 插件进行自动注册可以缩短初始化时间解决应用加固导致无法直接访问 dex 文件，初始化失败的问题，

### 进阶用法

#### 跳转到Uri指定的目标

1、Activity在AndroidManifest中设置
```xml
        <activity
            android:name=".LoginActivity"
            xxx>

            <intent-filter>
                <category android:name="android.intent.category.DEFAULT"/>
                <data
                    android:scheme="feather"
                    android:host="www.feather.com"
                    android:port="1226" />
            </intent-filter>
        </activity>
```

2、Activity中指定Path
```java
@Route(path = ARouterConstants.ACTIVITY_URL_LOGIN)
public class LoginActivity{
  xxx
}
```

3、通过Uri进行跳转
```java
// 1. 默认路径
Uri uri = Uri.parse(ACTIVITY_URL_LOGIN);
ARouter.getInstance().build(uri).navigation();

// 2. 完整路径
Uri uri = Uri.parse("feather://www.feather.com:1226" + ACTIVITY_URL_LOGIN);
ARouter.getInstance().build(uri).navigation();
```

#### 带参数跳转并获取到参数
1、携带参数跳转
```java
ARouter.getInstance().build(ARouterConstants.ACTIVITY_URL_LOGIN)
                        .withString("name", "feather")
                        .withInt("age", 18)
                        .withBoolean("male", true)
                        .navigation();
```

2、获取到参数(名字对应)
```java
@Route(path = ARouterConstants.ACTIVITY_URL_LOGIN)
public class LoginActivity extends AppCompatActivity{

    @Autowired
    public String name;
    @Autowired
    public int age;
    @Autowired
    public boolean male;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ARouter.getInstance().inject(this);
        //显示
        Logger.getLogger("Login").info("name = " + name + " age = " + age + " sex(is male) = " + male);
    }
}
```

2、获取到参数(变量名和参数名一致)
```java
@Route(path = ARouterConstants.ACTIVITY_URL_LOGIN)
public class LoginActivity extends AppCompatActivity{

    @Autowired
    public String name; //变量名必须要和参数名一致
    @Autowired
    public int age;
    @Autowired
    public boolean male;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //需要注入
        ARouter.getInstance().inject(this);
        //显示
        Logger.getLogger("Login").info("name = " + name + " age = " + age + " sex(is male) = " + male);
    }
}
```
> 变量必须要为Public修饰

3、获取到参数(变量名和参数名不一致)
```java
@Route(path = ARouterConstants.ACTIVITY_URL_LOGIN)
public class LoginActivity extends AppCompatActivity{

    @Autowired(name = "name")
    public String mName;
    @Autowired(name = "age")
    public int mAge;
    @Autowired(name = "male")
    public boolean mIsMale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //需要注入
        ARouter.getInstance().inject(this);
        //显示
        Logger.getLogger("Login")
             .info("name = " + mName +
                "\n age = " + mAge +
                "\n sex(is male) = " + mIsMale);
    }
}
```

#### 转场动画

##### 低版本动画

>使用withTransition方法，添加 进入动画，退出动画即可
```java
ARouter.getInstance()
       .build(ARouterConstants.ACTIVITY_URL_PROFILE)
       //参数1为打开的Activity的进入动画，参数2为当前的Activity的退出动画
       .withTransition(R.anim.router_scale_in, R.anim.router_scale_out)
       .navigation(StarActivity.this);
```

##### 高版本动画(API >=16)
>withOptionsCompat添加ActivityOptionsCompat对象
```java
// 设置动画效果


if (Build.VERSION.SDK_INT >= 16) {
    ActivityOptionsCompat compat = ActivityOptionsCompat.
            makeScaleUpAnimation(v, v.getWidth() / 2, v.getHeight() / 2, 0, 0);

    ARouter.getInstance()
            .build(ARouterConstants.ACTIVITY_URL_PROFILE)
            .withOptionsCompat(compat)
            .navigation(StarActivity.this);
} else {
    Toast.makeText(this, "API < 16,不支持新版本动画", Toast.LENGTH_SHORT).show();
}
```

###### 共享元素动画
>共享元素动画：需要在navigation中传入当前Activity
> 1-StarActivity(当前Activity)
```java
// 1、共享元素(三个元素)
Pair<View, String> pairOne = new Pair<View, String>(holder.mHeadImg, "stars_head_img");
Pair<View, String> pairTwo = new Pair<View, String>(holder.mNameTxt, "stars_name_txt");
Pair<View, String> pairThreee = new Pair<View, String>(holder.mAgeTxt, "stars_age_txt");

// 2. API23才有共享元素动画
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

// 3. 进入和退出动画
  getWindow().setEnterTransition(new Explode().setDuration(600).setInterpolator(new BounceInterpolator()));
  getWindow().setExitTransition(new Explode().setDuration(600));
// 4. 共享元素动画
  ActivityOptionsCompat compat = ActivityOptionsCompat.
      makeSceneTransitionAnimation(StarActivity.this, pairOne, pairTwo, pairThreee);
// 5. ARouter进行跳转：带入参数、进行共享元素动画
  ARouter.getInstance()
        .build(ARouterConstants.ACTIVITY_URL_PROFILE)
        .withOptionsCompat(compat)
        .withString("headImg", datas.get(position).getHeadImg())
        .withString("name", datas.get(position).getName())
        .withString("age", datas.get(position).getAge())
        .withString("profile", datas.get(position).getProfile())
        .navigation(StarActivity.this);
} else {
    Toast.makeText(StarActivity.this, "API < 23,不支持共享元素动画", Toast.LENGTH_SHORT).show();
}

```
> 2-ProfileActivity: 目标Activity
```java
@Route(path = ACTIVITY_URL_PROFILE)
public class ProfileActivity extends AppCompatActivity {

    @Autowired
    public String headImg;
    @Autowired
    public String name;
    @Autowired
    public String age;
    @Autowired
    public String profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ARouter.getInstance().inject(this);

        //设置内容
        setContentView(R.layout.activity_md);

        //获取控件
        ImageView imageView = findViewById(R.id.md_imageview);
        TextView nameTextView = findViewById(R.id.md_name_text);
        TextView ageTextView = findViewById(R.id.md_age_text);

        // 共享元素
        ViewCompat.setTransitionName(imageView, "stars_head_img");
        ViewCompat.setTransitionName(nameTextView, "stars_name_txt");
        ViewCompat.setTransitionName(ageTextView, "stars_age_txt");

        /**
         * 设置头像、姓名和年龄
         */
        Glide.with(this).load(headImg).into(imageView);
        nameTextView.setText(name);
        ageTextView.setText(age);
    }
}
```

#### Url跳转

##### 网页url跳转Activity

1、网页Url跳转Activity是什么？
>1. 效果：点击一个web页面上面的链接，就能跳转到我们的APP页面。

2、网页url跳转Activity的思路
> 1. 创建中转Activity(SchemeFilterActivity)
> 2. AndroidManifest中对中转Activity进行设置
> 3. 在中转Activity中进行对应页面的跳转。

3、功能实现
>1-中转Activity(SchemeFilterActivity.java)
```java
public class SchemeFilterActivity extends AppCompatActivity{
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. 获取到Uri
        Uri uri = getIntent().getData();
        // 2. 进行跳转
        ARouter.getInstance()
                .build(uri)
                .navigation();
        // 3.销毁该Activity
        finish();
    }
}
```
>2-AndroidManifest.xml对SchemeFilterActivity进行配置
```xml
        <activity android:name=".SchemeFilterActivity">
            <intent-filter>
                <data
                    android:host="host"
                    android:scheme="scheme"/>
                <action android:name="android.intent.action.VIEW"/>
                <category android:name="android.intent.category.DEFAULT"/>
                <category android:name="android.intent.category.BROWSABLE"/>
            </intent-filter>
        </activity>
```
>3-目标页面
```java
@Route(path = ARouterConstants.ACTIVITY_URL_LOGIN)
public class LoginActivity{
  xxx
}
```
>4-测试的web页面，将该html文件在手机浏览器中打开, 并且点击跳转链接。
```html
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title></title>
  </head>

  <body>
    <h2>跳转测试</h2>
    <h2>自定义Scheme[通常来说都是这样的]</h2>
    <p><a href="scheme://host/app/LoginActivity">scheme://host/app/LoginActivity</a></p>

  </body>
</html>

```

##### 网页url跳转-携带常用类型参数

>在Html页面的跳转中给Url拼接参数
```html
<p><a href="scheme://host/app/LoginActivity?name=feather&age=18&male=true">scheme://host/app/LoginActivity?name=feather&age=18&male=true</a></p>
```
>目标Activity接收参数。
```java
    @Autowired(name = "name")
    public String mName;
    @Autowired(name = "age")
    public int mAge;
    @Autowired(name = "male")
    public boolean mIsMale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ARouter.getInstance().inject(this);
        //xxx
    }
```

##### 网页url跳转-传递json自动转为自定义对象

### 总结
1、href跳转到app的流程
> 1. 系统浏览器发现要加载一个连接，是以arouter开头的协议，但是其自身无法处理该协议，此时就会把此协议往系统层抛
> 2. 系统会根据所有安装的app的清单文件决定要将此协议交给哪个app来处理。
> 3. 如果要在浏览器上访问，那么浏览器就要自己处理此协议，否在就会交给某个具体app处理

2、ARouter解决了WebView中URL跳转问题
> 1. 如果在WebView中需要跳转到一个Android页面，需要在shouldOverrideUrlLoading中拦截。但是业务的增多，会导致逻辑比较臃肿。
> 1. 如果要从其他App跳转到自己APP的某个页面，webView的方法也无法实现了。
> 1. ARouter通过在一个中转Activity统一调度，能简单安全的实现这种需求。

3、ARouter如何实现重定向？
> 1. 实现`PathReplaceService接口`
> 1. 对URL按照一定规则进行处理。

4、ARouter全局降级策略是什么？
> 1. 如果出错或者页面不存在，不会出错，而是可以进行降级处理。
> 1. 通过`DegradeService`接口来实现该功能

## 知识储备

1、AOP是什么？
> 1. AOP:面向切面编程(Aspect-Oriented Programming)。AOP是把涉及到众多模块的某一类问题进行统一管理。而OOP如果是把问题划分到单个模块。
> 1. Android AOP就是通过预编译方式和运行期动态代理实现程序功能的统一维护的一种技术。
> 1. 利用AOP可以对业务逻辑的各个部分进行隔离，从而使得业务逻辑各部分之间的耦合度降低，提高程序的可重用性，提高开发效率。

2、AOP三剑客
![AOP三剑客](https://upload-images.jianshu.io/upload_images/751860-0641778f0bc265ad.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/540)

3、APT是什么？
> 1. APT(Annotation Processing Tool 的简称)，可以在代码编译期解析注解，并且生成新的 Java 文件，减少手动的代码输入。
> 2. 现在很多主流库都使用APT，比如 Dagger2, ButterKnife, EventBus3 等

## 参考资料
1. [Android跳转-ARouter详细使用教程](https://blog.csdn.net/niubitianping/article/details/77982033)
1. [Android 路由框架ARouter最佳实践](https://blog.csdn.net/zhaoyanjun6/article/details/76165252)
1. [ARouter解析三：URL跳转本地页面源码分析](https://www.jianshu.com/p/2628bb5dda7a)
1. [Android 组件化 —— 路由设计最佳实践](https://www.jianshu.com/p/8a3eeeaf01e8)
2. [Android 路由框架ARouter最佳实践](https://blog.csdn.net/zhaoyanjun6/article/details/76165252)
1. [谈谈App的统一跳转和ARouter](https://www.jianshu.com/p/c0eecbbf1481)
1. [安卓AOP三剑客:APT,AspectJ,Javassist](https://www.jianshu.com/p/dca3e2c8608a)
