
# Gradle
## 基本概念
1、Gradle是自动化构建工具
### 支持的语言
2、Java、Groovy、Kotlin
3、Groovy是基于JVM动态语言
### DSL
4、提供了基于Groovy的两种DSL
5、解决特定领域问题的语言，非编程语言
6、Gradle的DSL作用是什么？描述构建过程和项目结构的语言
#### Groovy DSL
#### Kotlin DSL
## Artifactory
可以自己搭建仓库
## 生命周期、Task
## 断点调试
Configutation -> 新建Task(VM Options设置) -> 运行
## Trasnform、ASM
1、class->dex期间用来修改class文件的标准API
2、可以做什么？
1. 插入字节码
2. 修改注解
3. 替换资源
3、可以做到的高级功能
1. 热修复
2. 代码注入
3. AOP
### Gradle构建阶段
#### 初始化阶段
根据依赖关系创建任务图
#### 编译阶段
java->class
资源 appt->二进制
#### Trasform阶段
资源来源：class、jar、resources(Java的如META-INF)
#### Dex阶段
DEX
#### 打包阶段
Dex * N + 资源 = APK
签名
对齐
#### 安装和运行阶段
安装到目标设备上并且运行
UP-TO-DATE代表没有新变化
### 实战
#### xxxPlugin#registerTrasnform
#### xxxTrasnform
1. 任务名称
2. 资源类型：class等
3. 生效的模块
4. transform
##### transform
1. 拿到源目录
2. 拿到目标目录
3. 遍历文件，找到需要插桩的ASM处理
4. 输出到目标目录
#### ASM
##### ClassVisitor
##### AdviceAdapter-onMethodEnter/Exit
## Android Gradle插件开发
### 方式一：build script脚本
build.gradle文件中
apply plugin:MyPlugin
MyPlugin implements Plugin<Project>
### 方式二：buildSrc
/buildSrc/src/main/groovy
#### xxxPlugin
1. 创建名为releaseHelper的任务，建立Extension配置项和Task的关联：配置项关联
2. 创建任务一、二，建立xxxTask.class和任务的关联
3. 构建task间依赖，assembleRelease依赖clean，task1依赖assembleRelease，task2依赖task1
#### xxxTask 上传
1. 拿到app的build.gradle中andorid标签下输出目录，拿到里面app
2. Retrofit上传app到平台
#### xxxTask2 发送钉钉
1. 按照钉钉API要求填写信息(从配置获取)
2. Retrofit发送消息
### 方式三：独立项目
#### Module Java library
#### 修改build.gradle
##### gradleApi
##### publishing: 用于发布
#### groovy
##### xxxPlugin
1. 创建task分类
2. project.afterExvaluate
3. 遍历两个任务
4. 构建Task
##### xxxTask
commandLine模拟jar代码
##### Bean：配置项
用于用在build.gradle中
#### resources META-INF gradle-plugins
##### xxx.properties => xxxPlugin
### 发布：点击发布任务
### 使用
#### classpath
#### mavenLocal
#### plugins使用，配置项设置
#### gradle Task中找到任务，点击运行
### kotlin插件
