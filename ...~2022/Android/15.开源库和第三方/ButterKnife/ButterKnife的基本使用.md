转载请注明链接：

# ButterKnife基本使用

版本：2018/9/3-1

---

[TOC]

## 项目中集成

1、ButterKnife的集成：8.8.1(在AS3.0以上不可以使用)
> 1-build.gradle(Module:app): 配置butterknife和插件
```xml
apply plugin: 'com.android.library'
apply plugin: 'com.jakewharton.butterknife'

dependencies {
  implementation 'com.jakewharton:butterknife:8.8.1'
  annotationProcessor 'com.jakewharton:butterknife-compiler:8.8.1'
}
```
> 2-build.gradle(Project:xxx)
```xml
buildscript {
  repositories {
    mavenCentral()
   }
  dependencies {
    classpath 'com.jakewharton:butterknife-gradle-plugin:8.8.1'
  }
}
```

2、ButterKnife的集成：8.4.(在AS3.0以上可以使用)
> 1-build.gradle(Module:app): 配置butterknife和插件
```xml
dependencies {
    implementation 'com.jakewharton:butterknife:8.4.0'
    annotationProcessor 'com.jakewharton:butterknife-compiler:8.4.0'
}
```
> 2-build.gradle(Project:xxx)
```xml
buildscript {
  repositories {
    mavenCentral()
   }
  dependencies {
      classpath 'com.jakewharton:butterknife-gradle-plugin:8.4.0'
  }
}
```

## 异常

1、Android Gradle plugin 3.1.4 must not be applied to project 'xxx' since version 3.1.4 was already applied to this project
> 1. Gradle插件会和ButterKnife 8.7+版本有冲突
> 1. 需要使用8.4版本
