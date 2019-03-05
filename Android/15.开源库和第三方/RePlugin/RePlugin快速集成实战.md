转载请注明链接:

> 快速集成RePlugin以简单实例，讲解内置插件、外置插件、等众多用法。

# RePlugin快速集成实战

版本号:2019-03-03

---

[TOC]

## 宿主App

### 根目录build.gradle

1. 使用阿里的代理库，不然更新太慢
1. 这里AS3.1配合的gradle-4.5，如果是AS3.3必须要gradle-4.10 会出现不兼容的地方。
```xml
// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        maven { url 'http://maven.aliyun.com/nexus/content/groups/public/' }
        maven { url 'http://maven.aliyun.com/nexus/content/repositories/jcenter' }
        maven { url 'http://maven.aliyun.com/nexus/content/repositories/google' }
        maven { url 'http://maven.aliyun.com/nexus/content/repositories/gradle-plugin' }

    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.1.0'
        classpath 'com.qihoo360.replugin:replugin-host-gradle:2.3.1'
    }
}

allprojects {
    repositories {
        maven { url 'http://maven.aliyun.com/nexus/content/groups/public/' }
        maven { url 'http://maven.aliyun.com/nexus/content/repositories/jcenter' }
        maven { url 'http://maven.aliyun.com/nexus/content/repositories/google' }
        maven { url 'http://maven.aliyun.com/nexus/content/repositories/gradle-plugin' }

    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}

```

### app的build.gradle


1. apply plugin: 'replugin-host-gradle'要在applicationId后面
2. 根据情况进行配置：repluginHostConfig
3. 不要使用androidx，app运行后会报错
4. 依赖`implementation 'com.qihoo360.replugin:replugin-host-lib:2.3.1'`
```xml
apply plugin: 'com.android.application'

android {
    defaultConfig {
        applicationId "com.hao.repluginhost"
        // xxx
    }
}

// ATTENTION!!! Must be PLACED AFTER "android{}" to read the applicationId
apply plugin: 'replugin-host-gradle'

repluginHostConfig {
    /**
     * 是否使用 AppCompat 库
     * 不需要个性化配置时，无需添加
     */
    useAppCompat = true
}

dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    // 不能使用androidx
    implementation 'com.android.support:appcompat-v7:28.0.0'
    implementation 'com.android.support.constraint:constraint-layout:1.1.3'

    // Replugin
    implementation 'com.qihoo360.replugin:replugin-host-lib:2.3.1'
}


```

### RePluginApplication

1. 自定义application继承自RePluginApplication
2. AndroidManifest中进行注册
```java
public class BaseApplication extends RePluginApplication{
}
```
```xml
    <application
        android:name=".BaseApplication">
    </application>
```


## 问题汇总

## 参考资料
