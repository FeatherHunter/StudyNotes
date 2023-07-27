# ARouter

## 组件化基本使用
1、早期单一分包模式有哪些问题？
1. 无论如何分包，项目增大，项目失去层次感：层次混乱
1. 包名约束力太差，很容易不同业务包中互相调用，代码耦合度高：高度耦合，难以重用
1. 多人开发版本管理中，容易代码覆盖冲突

2、组件化中的结构
1. app壳
1. 业务组件化：Home、产品、订单、个人
1. 公共基础库：ARouter、Utils、Http、Base

3、组件化中业务模块之间如何交互？
1. 首页Home去访问基础库的ARouter
1. ARouter去路由，获取其他组件的服务。

4、正式环境和测试环境如何区分？
1. 测试环境：模块要能独立运行
1. 正式环境：App壳集成化运行

5、项目中有N个模块，都有build.gradle，很重复，需要抽理出来
1. 新增app.gradle, 内部用ext进行扩展,ext是特殊的属性，用于定义额外属性
```
// 把一些公用的，共用的，可扩展的，加入到这里面来
// 整个App项目的Gradle配置文件
// ext 自定义增加我们的内容
ext {
    usename="Derry"

    // 开发环境 / 生产环境（测试/正式）
    isRelease = true

    // 建立Map存储，对象名、key都可以自定义，groovy糖果语法，非常灵活
    app_android = [
            compileSdkVersion: 33,
            buildToolsVersion: "33.0.0",

            applicationId: "com.personal.tax",
            minSdkVersion: 29,
            targetSdkVersion: 33,
            versionCode: 1,
            versionName: "1.0",
            testInstrumentationRunner: "androidx.test.runner.AndroidJUnitRunner"
    ]

    appId = ["app"     : "com.xiangxue.new_modular_interaction",
             "order"   : "com.xiangxue.order",
             "personal": "com.xiangxue.personal"]

    //  测试环境，正式环境 URL
    url = [
            "debug"  : "https://11.22.33.44/debug",
            "release": "https://11.22.33.44/release"
    ]

    // 依赖相关的
    app_implementation = [
            "appcompat": "androidx.appcompat:appcompat:1.6.1",
            "junit": "junit:junit:4.13.2",
            "runner": "androidx.test:runner:1.2.0",
            "espresso": "androidx.test.espresso:espresso-core:3.4.0",
            "material": "com.google.android.material:material:1.9.0",
            "ktx":"androidx.core:core-ktx:1.8.0"
    ]
}
```
2. 引入自定义文件
```
apply from : "app_config.gradle"
```
3. 使用扩展内容 => 这样定义好可以提升构建性能
```
// 定义变量
def app_android = /*this.*/getRootProject().ext.app_android;
// 定义变量
def app_implementation = rootProject.ext.app_implementation;
// 定义变量
def url = this.getRootProject().ext.url;
```
4. 一行代码引入所有implementation
```
// 可以遍历map
app_implementation.each {
    k, v -> implementation v
}
```

6、生成类: 在buildTypes中生成字段
```
defaultConfig {
    // 这个方法接收三个非空的参数，第一个：确定值的类型，第二个：指定key的名字，第三个：传值（必须是String）
    // 为什么需要定义这个？因为src代码中有可能需要用到跨模块交互，如果是组件化模块显然不行
    // 切记：不能在android根节点，只能在defaultConfig或buildTypes节点下
    buildConfigField("boolean", "isRelease", String.valueOf(isRelease))
}
buildTypes {
    debug {
        // 增加服务器URL地址---是在测试环境下
        buildConfigField("String", "SERVER_URL", "\"${url.debug}\"")
    }
    release {
        // 增加服务器URL地址---是在正式环境下
        buildConfigField("String", "SERVER_URL", "\"${url.release}\"")
    }
}
```

### 交互

1、如何跳转到其他模块的Activity？
1. 类加载方式，找到目标Activity并且进行跳转
1. Map方式，在基础公共库用Map对Activity进行保存，可以查找到后跳转
> 每个Activity都通过Manager管理类，group+path，是对类加载的封装

2、组件化和模块化的区别？
1. 组件化为了复用
1. 模块化为了业务

2、ARouter方式
1.
