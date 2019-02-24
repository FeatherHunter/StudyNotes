转载请注明链接:https://blog.csdn.net/feather_wch/article/details/87903926

# Android 热修复框架: Sophix基本使用

版本号:2019/2/24-15:38

---

[TOC]

---


## 框架集成

### 添加依赖

1、build.gradle文件中添加热修复相关的依赖
> 根目录: build.gradle
```java
buildscript {
    repositories {
        maven {
            url 'http://maven.aliyun.com/nexus/content/repositories/releases/'
        }

    }
}

allprojects {
    repositories {
        maven {
            url 'http://maven.aliyun.com/nexus/content/repositories/releases/'
        }
    }
}
```
> module app: build.gradle
```gradle
dependencies {
    // Sophix-阿里热修复
    api 'com.aliyun.ams:alicloud-android-hotfix:3.2.8'
}
// implementation、compile都可以，看实际情况。
```

### 添加SophixStubApplication

2、添加热修复需要的SophixStubApplication，不要添加任何内容，除非有特殊需要。
> `@SophixEntry(BaseApplication.class)`其中的Application就是原来自定义的Application(实现业务逻辑)。SophixStubApplication中绝对不要增加任何逻辑代码。
```java
package com.hao.lib_base.base;

import android.content.Context;
import android.util.Log;

import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixApplication;
import com.taobao.sophix.SophixEntry;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;

import androidx.annotation.Keep;

/**
 * Sophix入口类，专门用于初始化Sophix，不应包含任何业务逻辑。
 * 此类必须继承自SophixApplication，onCreate方法不需要实现。
 * 此类不应与项目中的其他类有任何互相调用的逻辑，必须完全做到隔离。
 * AndroidManifest中设置application为此类，而SophixEntry中设为原先Application类。
 * 注意原先Application里不需要再重复初始化Sophix，并且需要避免混淆原先Application类。
 * 如有其它自定义改造，请咨询官方后妥善处理。
 */
public class SophixStubApplication extends SophixApplication {
    private final String TAG = "SophixStubApplication";
    // 此处SophixEntry应指定真正的Application，并且保证RealApplicationStub类名不被混淆。
    @Keep
    @SophixEntry(BaseApplication.class)
    static class RealApplicationStub {}
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//         如果需要使用MultiDex，需要在此处调用。
//         MultiDex.install(this);
        initSophix();
    }
    private void initSophix() {
        String appVersion = "0.0.0";
        try {
            appVersion = this.getPackageManager()
                    .getPackageInfo(this.getPackageName(), 0)
                    .versionName;
        } catch (Exception e) {
        }
        final SophixManager instance = SophixManager.getInstance();
        instance.setContext(this)
                .setAppVersion(appVersion)
                .setSecretMetaData(null, null, null)
                .setEnableDebug(true)
                .setEnableFullLog()
                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                    @Override
                    public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
                        if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                            Log.i(TAG, "sophix load patch success!");
                        } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                            // 如果需要在后台重启，建议此处用SharePreference保存状态。
                            Log.i(TAG, "sophix preload patch success. restart app to make effect.");
                        }
                    }
                }).initialize();
    }
}

```

### 修改AndroidManifest

#### 替换原Application为SophixStubApplication

3、替换原Application为SophixStubApplication
> 1. 系统会先加载`SophixStubApplication`，内部再加载我们自定义的Application
> 1. 要避免原Application被混淆，不然找不到！看后面的混淆部分。
```xml
    <application
        android:name="xxx.SophixStubApplication">

        // xxx
    </application>
```

#### 添加必备权限

4、添加必备权限(高版本注意动态权限申请需要自己处理)
```xml
    <!-- 网络权限 -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <!-- 外部存储读权限，调试工具加载本地补丁需要 -->
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
```

#### 添加Sophix的APP ID、APP Secret、RSA密钥

5、在application中添加meta-data
```xml
    <application
        android:name="com.hao.lib_base.base.SophixStubApplication"
        xxx>

        <meta-data
            android:name="com.taobao.android.hotfix.IDSECRET"
            android:value="2568......" />
        <meta-data
            android:name="com.taobao.android.hotfix.APPSECRET"
            android:value="b8c8d31de746b9a......" />
        <meta-data
            android:name="com.taobao.android.hotfix.RSASECRET"
            android:value="MIIEvQIBADANBgk......" />
    </application>
```

##### 注册阿里云开发者

6、要使用Sophix需要登录阿里云开发平台，开通热修复的功能。
> 1. [阿里云移动热修复官网](https://www.aliyun.com/product/hotfix?spm=5176.8142029.digitalization.21.16836d3ecUvR2k)
> 1. 具体如何开通相关应用，自行百度。

#### 完整 AndroidManifest.xml

7、修改AndroidManifest
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    package="com.hao.iday">

    <!-- 网络权限 -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <!-- 外部存储读权限，调试工具加载本地补丁需要 -->
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>

    <application
        android:name="com.hao.lib_base.base.SophixStubApplication"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme"
        tools:ignore="GoogleAppIndexingWarning">
        <activity
            android:name=".MainActivity"
            android:launchMode="singleTask">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <meta-data
            android:name="com.taobao.android.hotfix.IDSECRET"
            android:value="25681463" />
        <meta-data
            android:name="com.taobao.android.hotfix.APPSECRET"
            android:value="b8c8d31de746b9a097a4027c093c07e7" />
        <meta-data
            android:name="com.taobao.android.hotfix.RSASECRET"
            android:value="MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCBtW8OWQ/sF3TCEYOgMkFa7UaL4XUJvDWZdEB3Y+jNT5xJyn7n6QJ9oTEelqUxRmH9D+O//ZLvMNOYHnYaXnU+UuNcoGePLixluzl4ShEfHLtXHylt95q2Xa4KDfiTpZUcp93d3iLDEODMxHHYn2eirDPtMGVHvpkjngtIuSmgEThCUq0eBvRy9R6RIIqS1+dkG9ekTp11WZYVfmJgbCMnaDkl4H7Vro5bsPF8N06ZbBfAxvvZcTKQ1Vse8iRJ2Iytzseb7scuunjYqZtIGvh7Fxs9ZAEZn9XyrK0vu5JAwUkvHwpMW/qUux59zK89Tbza/+aFYX8nB7XKsZQdmB/xAgMBAAECggEAATEDdiRHSynj/qxegygzBXzMexh1FMPP0jsJJI+cYdZrXxonFEZFqZXDd752gf9dgw+UTf0sTloqd2lAOHnJpero/77RdLU93ylbG8nCYoDHD8PPg50UpLwzjxAVynVo6zx3g0Q5GYWcKwVa7lx4fTwLthANvhz4FyWqgN6CjV/XwxBeAxVvylD0NM7QDtBFrT3vYy09wV3cU4yV+8SW1i4ZUsilbKe1W+3nwRcD9hblxeoFtzC+3Lg39w3aROV+pm6KuZvvjfXqWivK4R3Sl+W+CxF6FvUw7QLm4PbRn1P2QQONCNlXRDn00NSQotswURkhGa2UytoMIMir8I0MAQKBgQDESaornNPkQQahsR2vWl+89hKyM9UixGgB4vCeZACrta7IkrkhL/GMqRYYiST0VjkU+uqfrsZ3jlPkJ3n5YfFCq1a0bw0JJfokaX+v6fVhd5Am71VfG6WGfHlmbMaW9TP8rbgqv7L8YzaWxzMOyZz/wBWAe9Q60c9KV/GkMqpcEQKBgQCpKsheBGkfm+1qQ+/G97DwzXTkVCQId0XopdVn+1NlQbLYz41WSkDT7MBnaPawiyBbuuQsBeY+NsbqF92qKX4FJoiXyfFoLYxQ/tYftkzWEdfG4j94IgEp9CW3FZV59PpXJaGJeqPXQSg7rzV+rZlGw4zCZfdhENLLM3kJw2Ll4QKBgQCeC/ZiEW+njRgq3zLYu3r06mnZZNTLnrtkMXevATRtDegDBejcqP7kVE0/SkYgGsDxfKuoEZEqOMahoYub2lnpZY510Cj94b9MzEmMumPiq+O9Bd6GqvsXk2goqZMpf5vxa2sruJkEkVomA5S/5mRspRxrDMmw5rjy0mb13/m+EQKBgHqb85WRBNBQJ4d5rfQ72hbuEubaxTMNn7G7YC15Tzx4nbPe2sXME6iGU/2fag0TCWTy1CXEMNiuwwUwPwzx/dCl2SKKz1l+idC6o19gtdgCHq6blPzxSH6r4hoMnsZB9J/tOmN2bAG7y/lWSCOEly9e1EqhtOa5Vs7Ig9W7s5aBAoGAL/yzBGGzmM9Mya3iEqID3LVbcwFUA0xzvOyTeUMAlLdwmGdmAckR/Z6/+1dnGBtbQU1MkkD4K3MwIpBY6WCPy0hwgh/OmcGfxIa6a5X97CFn/0j1ntSl5WSvPXcdRC7+G5S2hi/accATWU0sp13Jx8ncb0gtEWedmUVf69Oa4/0=" />
    </application>

</manifest>
```

### 混淆配置

8、进行混淆配置，文件是`proguard-rules`
```
#基线包使用，生成mapping.txt
-printmapping mapping.txt
#生成的mapping.txt在app/build/outputs/mapping/release路径下，移动到/app路径下
#修复后的项目使用，保证混淆结果一致
#-applymapping mapping.txt
#hotfix
-keep class com.taobao.sophix.**{*;}
-keep class com.ta.utdid2.device.**{*;}
-dontwarn com.alibaba.sdk.android.utils.**
#防止inline
-dontoptimize

-keepclassmembers class com.my.pkg.MyRealApplication {
    public <init>();
}
# 如果不使用android.support.annotation.Keep则需加上此行
# -keep class com.my.pkg.SophixStubApplication$RealApplicationStub
```

## 基本使用

### 查询并加载新补丁: queryAndLoadNewPatch

1、在BaseApplication(自定义的)中进行补丁的获取和更新
```java
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 1. 查询并且加载补丁。queryAndLoadNewPatch不可放在attachBaseContext 中，否则无网络权限，建议放在后面任意时刻，如onCreate中。
        SophixManager.getInstance().queryAndLoadNewPatch();
    }
}
```

### SophixPatchTool生成补丁: sophix-patch.jar

2、使用阿里提供的SophixPatchTool工具，通过新旧apk差异生成补丁包。
> 1. 具体教程可以看参考资料的 1和2, [Sophix-阿里第三代非侵入式热修复](https://www.jianshu.com/p/8ea4d653a53e)
> 1. SophixPatchTool工具在参考资料: 3-[Windows版本打包工具地址](http://ams-hotfix-repo.oss-cn-shanghai.aliyuncs.com/SophixPatchTool_windows.zip)

### 生成并且下发补丁

3、在阿里云开发者平台的[移动热修复-管理控制台](https://emas.console.aliyun.com/?spm=5176.131995.673114.con1.54b669fdYKG7eH#/productList)中可以
> 1. `移动热修复->补丁管理->上传补丁->发布`

## 问题汇总

## 参考资料

1. [Sophix官方文档: 移动热修复-快速开始](https://help.aliyun.com/document_detail/93825.html?spm=a2c4g.11186623.6.580.5de05482VLP2Uf)
1. [Sophix-阿里第三代非侵入式热修复](https://www.jianshu.com/p/8ea4d653a53e)
1. [Windows版本打包工具地址](http://ams-hotfix-repo.oss-cn-shanghai.aliyuncs.com/SophixPatchTool_windows.zip)
1. [调试工具地址](http://ams-hotfix-repo.oss-cn-shanghai.aliyuncs.com/hotfix_debug_tool-release.apk)
