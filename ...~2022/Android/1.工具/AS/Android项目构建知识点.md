转载请注明链接: https://blog.csdn.net/feather_wch/article/details/82706937

# Android项目构建知识点总结

版本：2018/9/16-1(0:35)

---

[TOC]

---


## Android Studio目录结构
1. build.gradle
> 相关的配置，类似于Linux下的makefile

1. gradle.properties
> gradle的属性文件，适用于所有module

1. gradlew
> 可执行文件，Linux平台。

1. gradlew.bat
> 可执行文件，Windows平台。可以直接运行，去执行gradle的操作

1. local.properties
> 本地配置，不建议上传仓库

1. settings.gradle
> 设置相关的，gradle脚本在这里配置

1. proguard.cfg
    > 混淆配置，减少体积，安全防护

1. gitgnore： 配置不需要上传的文件

## Android构建流程

4、APK打包流程(7个工具)
> 1. AAPT工具: 对资源文件进行打包(AndroidManifest.xml、布局等)生成R.java文件;
> 1. AIDL工具: 处理AIDL文件，生成对应的Java文件。
> 1. Javac工具: 对R.java、项目源码、aidl对应的Java文件这三部分进行编译，生成class文件
> 1. Dex工具: 将所有class文件转换为DEX文件：该过程进行将Java字节码转换为Dalvik字节码、压缩常量池、清除冗余信息等工作。
> 1. ApkBuilder工具: 将资源文件、Dex文件打包成APK文件
> 1. KeyStore: 对APK文件进行签名。
> 1. ZipAlign工具: 正式版APK，需要用ZipAlign工具进行对齐处理：过程中是将APK中所有资源文件的起始地址都偏移4字节的整数倍，通过内存映射访问APK文件的速度会更快

5、Android构建的流程
> 1. AAPT：资源打包工具，生成R.java
> 1. AIDL: AIDL文件生成Java文件。
> 1. Javac：编译成.class文件
> 1. Dex: 将class文件转换为Dex文件。Dex是Davlik可以执行的文件
> 1. ApkBuilder：打包成APK
> 1. KeyStore：签名
> 1. ZipAlign：对齐处理，偏移4字节整数倍，提高APK访问速度。


## jenkins持续集成构建
1、Jenkins的作用？
> 1. 能持续构建我们的APK
> 1. 开发迭代速度很快，手动去构建比较繁琐。
> 1. Jekins能持续自动构建APK，提高效率。
