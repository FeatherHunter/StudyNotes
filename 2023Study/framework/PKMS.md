# PKMS

## SystemServer启动流程(PKMS)相关

PKMS相关流程分为7步骤

引导服务-四步骤

* onlycore写死为false就不需要关注非预制app，比如不让安装 qq 微信 等
* 三步骤后：有ota升级相关。项目的ota是我做的！！！

其他服务-三步骤

* dex优化是systemserver交给pkms去做的
* 第六步 磁盘维护 做安防避免性能问题可以直接干掉。
* 第七步 PKMS systemReady 。只要PKMS失败了，后面事情也不用做了。会一直停留在开机画面。

还会检测packages.xml进行扫描，不需要扫描全部，但是默认是扫描全部。

**如何看rom日志？**

> adb shell
>
> logcat -b events | grep pms 可以看第几阶段
>
> logcat -b events 过滤系统事件。

### PKMS构造函数-五阶段

**pkms构造方法1000多行，不抽取函数两万行。为什么这么大？**

构造函数有五个阶段。

1. 第一阶段，开始阶段

   > settings类 package.xml记录应用安装进度，比如一半断电了，下次启动 谷歌会直接干掉，小米会继续安装
   >
2. 第二阶段，扫描阶段

   > system/app vendor/apps等。pkms要知道所有系统应用。保存到内存中。扫描所有系统目录apk，把清单文件扫描解析到pkms内存中。
   >
3. 第三阶段，**Data扫描【重点！】**

## 动态权限申请的流程

## 实战：无侵入权限框架

aspectj对javac的二次封装。

java文件到class时注入了代码。

字节码插壮升级版本，融入了aop思想。

注解作为切点，函数作为切面。



无侵入式权限框架

不用反射，不用apt

无侵入式是模仿Sring


思路：

1. 以注解作为切点
2. 以函数作为切面
3. 用透明不可见Activity去申请权限，并且回调结果。将结果通过回调返回，并且通过切面函数的继续调用，继续去做获取到权限后的任务。
4. 对于权限取消和永久拒绝，通过切面方法里面可以获取到Activity或者Fragment的对象，通过反射获得注解信息的方式，进行反射的方法调用，以通知上层。

## 思考题

**PackageManagerService类图关系**

**launcher3应用是怎么展示App的？**

> 从PKMS中获取到应用信息

**手机开机很慢，大概是什么原因？**

如果开机时间30s

1. 第三步，pkms构造耗时（很重要）15s
2. 第五步，dex优化操作 耗时 10s

**安卓安装apk的原理**

1. copy apk到指定目录
2. 扫描指定目录的apk

> adb push xxapk data/app/ **copy**
>
> adb reboot **触发data扫描阶段**
>
> * 1copy 2扫描+扫描流程（函数调用细节）

**Activity A跳转到Activity B，launchmode=singleinstance等，launchmode是什么时候解析的？**

> 是手机开机流程里面SystemServer run方法中，七步曲里面第四步PKMS的构造函数里面的第三阶段，Data扫描阶段，会将应用的清单文件进行解析，并且保存结果到内存中，这样实际跳转时不需要解析，效率更高。

* 扩展，Activity启动模式

**手机一开机为什么可以收到开机广播？**

> 手机开机时【systemserver关于pkms的七部曲里面 pkms构造函数的第三阶段】已经全部扫描了所有apk清单文件，进行了广播注册（必须是静态广播）
>
> * data解析里面parseBaseApplication解析清单。
>
> android11 parsingpackage类保存所有清单信息

**好处是什么？**

> 每次跳转activity或者launcher打开app的时候都不需要再解析了，直接向内存中的parsingPackage读取。

* 为什么不可以启动后并行扫描解析???

**SystemServer main.run()中主要七步曲，构造函数五阶段，dex优化**

**动态权限申请的流程**

1. 15步
2. app ipc到pkms, ipc到pms申请权限, 再到pkms，再到settings写入文件runtime-permissions.xml（这个文件有权限限制 不能随便修改）
