

# ARouter基本教程

版本：2018/8/10-1

----

[阿里巴巴ARouter-Github链接点击这里](https://github.com/alibaba/ARouter)

[TOC]

## 依赖添加

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

## SDK初始化
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

## 路径统一管理
>路径统一管理, 也可以不用该方式。
```java
public class ARouterConstants {
  //路径必须要最少是/xx/xx
    public static final String ACTIVITY_URL_LOGIN= "/app/LoginActivity";
}
```

## 增加注解
```java
//使用该注解，并且指明path。
@Route(path = ARouterConstants.ACTIVITY_URL_LOGIN)
public class LoginActivity extends AppCompatActivity{
}
```

## 发起路由操作
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


## 混淆处理
>添加混淆规则(如果使用了Proguard)
```xml
-keep public class com.alibaba.android.arouter.routes.**{*;}
-keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}

# 如果使用了 byType 的方式获取 Service，需添加下面规则，保护接口
-keep interface * implements com.alibaba.android.arouter.facade.template.IProvider

# 如果使用了 单类注入，即不定义接口实现 IProvider，需添加下面规则，保护实现
-keep class * implements com.alibaba.android.arouter.facade.template.IProvider
```
## 路由表自动加载
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
