>记录了将公司大项目，完整从Android Studio 2.3迁移到 Android Studio 3.0的全部过程。
>按照步骤迁移，遇到的所有报错和解决办法都在后面。

# Android项目从2.3迁移到3.0、3.1

版本：2018/8/14-1(1456)

---

[TOC]

## 迁移到AS3.0

### 0-下载Android Studio 3.0
官网(可以直接下载)：https://developer.android.google.cn/studio/index.html#downloads

* 需要在网站中的"下载选项"中，选择`android-studio-ide-171.4443003-windows.zip`下载。

>注意！下载无SDK的压缩包，而不是`exe`文件。因为通过解压缩的方式，可以让Android Studio 3.0和Android Studio 2.3共存。

* 本地解压缩zip压缩包，在as目录下的bin中studio64就是运行的exe文件，可以创建快捷方式放置于桌面。

### 1-gradle下载4.1
请去官网或搜索下载

1. gradle-wrapper.properties
-将`gradle-2.10-all.zip`更改为`gradle-4.1-all.zip`

2. AS的settings中搜索`gradle`
-在`use local gradle`中设置本地gradle的路径，例如：`D:/gradle-4.1-all/gradle-4.1`

### 2-Build tools 下载 26.0.2
请去官网或搜索下载

1. 将下载的`26.0.2`的tools文件夹添加到本地sdk目录中的build-tools内，例如“D:\android-sdk\build-tools”

2. 公司网络限制下的备选方法：经测试在as3.0里面的SDK manager可以直接更新，需要在settings->proxy->选择'auto-detect proxy'，之后就可以在`SDK Manager`中更新需要API/Build-Tools等。

### 3-SDK API26(android 8.0)
请去官网或搜索下载

1. 将下载的`android-26`添加到本地SDK目录中的platforms目录下

2. SDKManager中直接更新

### 4-更改build.gradle里面的设置
```
dependencies {
        classpath 'com.android.tools.build:gradle:2.3.0'
}
```
需要将2.3更改为3.0.1(as3的版本)：
```
dependencies {
        classpath 'com.android.tools.build:gradle:3.0.1'
}
```

### 5-gradle报错处理

#### 1-gradle打包，自定义apk名称
报错：
`Error:(58, 0) Cannot set the value of read-only property 'outputFile' for ApkVariantOutputImpl_Decorated{apkData=Main{type=MAIN, fullName=AHMobileReleaseUnsigned, filters=[]}} of type com.android.build.gradle.internal.api.ApkVariantOutputImpl.
<a href="openFile:E:\Projects\AS3.0projects\v2.x\src\mobile\iptvclient\build.gradle">Open File</a>`

原内容如下：
```
applicationVariants.all { variant ->
  variant.outputs.each { output ->
    def outputFile = output.outputFile
    if (outputFile != null && outputFile.name.endsWith('.apk')) {
      //这里修改apk文件名
      def fileName = "${variant.productFlavors[0].name}.apk"
      output.outputFile = new File(outputFile.parent, fileName)
    }
  }
}
```

处理办法(直接用下列代码替换)：
```
android.applicationVariants.all { variant ->
    variant.outputs.all {
      outputFileName = "${variant.productFlavors[0].name}.apk"
    }
  }
```

`${variant.productFlavors[0].name}.apk`就是之前写法中`def fileName = `后面的内容。

#### 2-Build Tools需要使用26.0.2或者更高版本
报错：
```Warning:The specified Android SDK Build Tools version (25.0.0) is ignored, as it is below the minimum supported version (26.0.2) for Android Gradle Plugin 3.0.1.
Android SDK Build Tools 26.0.2 will be used.
To suppress this warning, remove "buildToolsVersion '25.0.0'" from your build.gradle file, as each version of the Android Gradle Plugin now has a default version of the build tools.
```
直接选择下方的内容：
```
Update Build Tools version and sync project
```

或者直接在所有`build.gradle`中，将 `buildToolsVersion`后面更新为`'26.0.2'`。
结果：`buildToolsVersion '26.0.2'`

#### 3-维度统一
报错：
`Error:All flavors must now belong to a named flavor dimension. Learn more at https://d.android.com/r/tools/flavorDimensions-missing-error-message.html`

首先，在项目build.gradle的defaultConfig添加内容：

```
defaultConfig {
	...省略...
	//最后添加如下内容
    flavorDimensions "versionCode"
  }
```

其次，在配置编译时需要编译的版本的配置中添加内容：
```
productFlavors {
    AHMobile {
      applicationId "com.ahmobile.android.tvclient" //com.zte.iptvclient.android.ahmobile
      signingConfig signingConfigs.ahmobile_release
      versionCode 11
      versionName "1.1.2.3"
      manifestPlaceholders = [APP_MARKET_VALUE: "Https-market-Android"]

   //添加下列两行内容
      dimension "versionCode"
      matchingFallbacks = ['versionCode']
    }
    ...
}
```

##### 分析：
>项目中用了多渠道，3.0之前配置多渠道：productFlavors配置不同的渠道包，3.0 新增了flavorDimensions的配置。
>报错的大致原因是：
>Android Plugin3.0的依赖机制：在使用library时会自动匹配variant（debug, release)，就是说app的debug会自动匹配library的debug，相信大多数人也像我一样，当library多了，不会手动选择每个Library的variant。现在好了，它会自动匹配了。同样如果使用flavor的时候，比如app的redDebug同样会自动匹配library的readDebug。虽然有这样的优势，但是在使用flavor时，必须定义flavor dimension,否则会提示错误

#### 4-Unable to resolve dependency
`Error:Unable to resolve dependency for ':mobile:iptvclient@AHMobileReleaseUnsigned/compileClasspath': Could not resolve project :uisdk:baseclient.
<a href="openFile:E:/Projects/AS3.0projects/v2.x/src/mobile/iptvclient/build.gradle">Open File</a><br><a href="Unable to resolve dependency for &#39;:mobile:iptvclient@AHMobileReleaseUnsigned/compileClasspath&#39;: Could not resolve project :uisdk:baseclient.">Show Details</a>`

build.gradle的buildTypes内修改如下：
```
buildTypes {
    release {
      minifyEnabled true
      shrinkResources true
      zipAlignEnabled true
      proguardFiles 'proguard.cfg'

/*
 *   因为只有release，没有debug，新增如下内容
 *   如果有debug内容，需要在其中添加：matchingFallbacks = ['release']
 */
      matchingFallbacks = ['debug']
    }

    releaseUnsigned.initWith(buildTypes.release)
    releaseUnsigned {
      signingConfig null
    }
}
```

原因：
local与debug不兼容。原因是在主项目中有一个变种local，而在library中是没有对应的。

#### 5-AAPT2 编译报错 AAPT2 error
报错
`Error:java.util.concurrent.ExecutionException: com.android.tools.aapt2.Aapt2Exception: AAPT2 error: check logs for details`

解决：在gradle.properties中关闭APPT2 编译
```
android.enableAapt2=false
```
注：如果是eclipse转到as上的项目，可能没有gradle.properties文件，请在项目根目录中手动创建

#### 6-Error:Removing unused resources requires unused code
`Error:Removing unused resources requires unused code shrinking to be turned on. See http://d.android.com/r/tools/shrink-resources.html for more information.`


```
android {
    ...
    buildTypes {
        release {
            shrinkResources true
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android.txt'),
                    'proguard-rules.pro'
        }
    }
}
```

在android 3.0需要android profile时都false

#### 7-Error:Unable to resolve dependency for ':mobile:iptvclient@debug/compileClasspath'-multidex:1.0.0

老版本如果遇超过方法数超过65535的解决办法一般是defaultConfig 中添加multiDexEnabled true

现在AS3.0好像没有这个限制，需要将`gradle`中` multiDexEnabled true`去掉:
```xml
    defaultConfig {
        applicationId "com.zte.iptvclient.android.armenia"
        signingConfig signingConfigs.release
//        multiDexEnabled true
    }
```
在`Application`中将` MultiDex.install(this)`删除：
```
    /**
     * 兼容5.0以下系统支持MultiDex分包
     * @param base
     */
    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
//        MultiDex.install(this);
    }
```

#### 8-....省略...Error while executing process ...省略.../aapt.exe with arguments...
需要在SDK Manager的SDK Tools中选择Tools和Platform-Tools进行更新，此外更新SDK到Android 8(api-26)

将项目中compilesdk替换为26

#### 9-error: style attribute '@android:attr/windowEnterAnimation' not found.
提示我们找不到@android:attr/windowEnterAnimation，因为已经不支持@开头使用android自带的属性，我们只要把@符号删掉就可以了。

>全局搜索`@android:attr/windowEnterAnimation`找到相应的地方，把@去除

修改前：

```xml
<style name="remnote_play_time_animation">
        <item name="@android:windowEnterAnimation">@anim/remote_play_popup_show</item>
        <item name="@android:windowExitAnimation">@anim/remote_play_popup_hide</item>
</style>
```

修改后：

```xml
<style name="remnote_play_time_animation">
        <item name="android:windowEnterAnimation">@anim/remote_play_popup_show</item>
        <item name="android:windowExitAnimation">@anim/remote_play_popup_hide</item>
</style>
```

#### 10-Error:(20, 36) 错误: 找不到符号 符号:   变量 mAvailIndices 位置: 类型为FragmentManagerImpl的变量 fragmentManagerImpl

原因是support-v4版本问题。
gradle中添加如下内容`implementation 'com.android.support:support-v4:26.1.0'`
有网络可以直接更新，也可以使用缓存的support v4(找我下载)


### 6-添加google()
有些内容需要从google上面更新(公司的项目没加这个也没影响)
```
buildscript {
    repositories {
		...
        google()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.0.0'
    }
}

allprojects {
    repositories {
		...
        google()
    }
}
```

### 7-项目转为kotlin项目
在加入Kotlin文件后，as会提示自动添加kotlin相关配置。但是此时在你的build.gradle文件最下方会被系统自动加入
```java
    repositories {
        mavenCentral()
    }
```
一定要删掉！！会导致你一直去下载一些依赖，而这些依赖是你通过aar添加的。

#### 1-异常
`java.lang.IllegalArgumentException: Parameter specified as non-null is null: method kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull, parameter animation`

表示null值给了不能为null的变量。kotlin中严格区分Int和Int？这种前者绝对不可为null和后者可为null的情况。

### 8-其他
#### 1-可能会自动更新espresso
#### 2-gradle下载的依赖包路径
```
C:\Users\6005001819\.gradle\caches\modules-2\files-2.1
```
用阿里仓库
```java
 maven{ url 'http://maven.aliyun.com/nexus/content/groups/public'}
```
在as设置中搜gradle，在android studio中disable embedded maven repository

## 迁移到AS3.1

### 1-下载AS3.1

### 2-下载Gradle4.4

### 3-下载Build Tools 27.0.3

### 4-Gradle异常

#### The SourceSet 'instrumentTest' is not recognized by the Android Gradle Plugin. Perhaps you misspelled something?

```java
instrumentTest.setRoot('tests')
```
解决办法：在gradle文件中搜索instrumentTest，该内容已经被弃用。用 androidTest 替换 instrumentTest，编译运行即可。
```java
androidTest.setRoot('tests')
```

#### All flavors must now belong to a named flavor dimension. Learn more at https://d.android.com/r/tools/flavorDimensions-missing-error-message.html

解决办法：参考上面3.0中Gradle错误中关于维度统一的部分。

#### Unable to resolve dependency for ':xxx/compileClasspath': Could not resolve project :xxx.

build.gradle的buildTypes内修改如下：
```
buildTypes {
    release {
      minifyEnabled true
      shrinkResources true
      zipAlignEnabled true
      proguardFiles 'proguard.cfg'

/*
 *   因为只有release，没有debug，新增如下内容
 *   如果有debug内容，需要在其中添加：matchingFallbacks = ['release']
 */
      matchingFallbacks = ['debug']
    }

    releaseUnsigned.initWith(buildTypes.release)
    releaseUnsigned {
      signingConfig null
    }
}
```

## 9-参考资料
http://blog.csdn.net/chenlin1989/article/details/78415873

http://www.jianshu.com/p/55efb9407fa2

http://www.jianshu.com/p/15afb8234d19
AS2.3项目迁移到AS3.0
