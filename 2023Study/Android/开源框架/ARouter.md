# ARouter

[本文链接，点击这里]()

核心点：
1. Apt + JavaPoet, EventBus是传统的一行行写入
1. Apt生成Group类和Path类，Group中存放group和生成Path类映射关系，Path类中存入时path和Activity.class的映射关系。
1. 

[TOC]

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
```groovy
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
```groovy
apply from : "app_config.gradle"
```
3. 使用扩展内容 => 这样定义好可以提升构建性能
```groovy
// 定义变量
def app_android = /*this.*/getRootProject().ext.app_android;
// 定义变量
def app_implementation = rootProject.ext.app_implementation;
// 定义变量
def url = this.getRootProject().ext.url;
```
4. 一行代码引入所有implementation
```groovy
// 可以遍历map
app_implementation.each {
    k, v -> implementation v
}
```

6、生成类: 在buildTypes中生成字段
```groovy
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



## Apt

eventbus apt 一行一行生成的传统方式

arouter javapoet

butterkinife升级为

【项目】apt，也升级过。

javapoet oop思想

传统是：package，class，method

javapoeat：method，class，package

apt生成代码，用path“”，返回对应的类.class。用于跳转

# ARouter原理解析

ARouter的组成部分
1. Root、Group和Path类的生成和管理
1. 如何判断@ARouter注解用到了不符合要求的类上面？



## Group和Path

每个module使用annotationProcess注解处理器，每个module apt都会执行一次，存在生成类名冲突问题，需要动态传入project名字，生成不同类。

1、Group分组的好处是什么？
1. 提高查询效率
1. 一个Group对应很多小弟Path, 先生成Path再生成Group
**生成代码后的类：**
```java
// Group类
ARouter$$Group$$personal.java implements ARouterGroup 实现方法 getGroupMap()
// 内部HashMap，key=组名，value=生成的Personal Path类
Map<String, Class<ARouter$$Path$$personal>> groupMap = new HashMap<>();
// put存入
groupMap.put("personal", ARouter$$Path$$personal.class);
return groupMap;

// Path类
ARouter$$Path$$personal.class implements ARouterPath 实现方法 ARouterPath()
Map<String, RouterBean> pathMap = new HashMap<>();
// key=路径名，value=Activity的class
pathMap.put("/personal/Personal_MainActivity", RouterBean(MainActivity.class);
pathMap.put("/personal/Personal_LoginActivity", RouterBean(LoginActivity.class);
return pathMap; // 返回pathMap
```

2、ARouter为什么要增加配置项  arguments = [AROUTER_MODULE_NAME: project.getName()] ？
1. 要根据不同Module生成类，不然会重名。processer初始化
```java
// 初始化获得
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        Map<String, String> options = processingEnv.getOptions();
        moduleName = options.get("AROUTER_MODULE_NAME");
    }
// 可以根据project名生成专属的Root类：ARouter$$Root$$app、ARouter$$Root$$login 等等
// 生成专属providers类：ARouter$$Providers$$app、ARouter$$Providers$$login
String rootFileName = NAME_OF_ROOT + SEPARATOR + moduleName;
            JavaFile.builder(PACKAGE_OF_GENERATE_FILE,
                    TypeSpec.classBuilder(rootFileName)
                            .addJavadoc(WARNING_TIPS)
                            .addSuperinterface(ClassName.get(elementUtils.getTypeElement(ITROUTE_ROOT)))
                            .addModifiers(PUBLIC)
                            .addMethod(loadIntoMethodOfRootBuilder.build())
                            .build()
            ).build().writeTo(mFiler);
```

## TypeMirror 确定ARouter注解的类 必须满足的类型


TypeMirror是什么？
1. Java编译时注解处理器（Annotation Processing）的基础API之一。
1. 通过`TypeMirror`接口，注解处理器可以获取和操作源代码中的类型信息。
1. `javax.lang.model.element.Element`接口表示Java源代码中的元素（如类、方法、字段等），通过`Element`对象可以获取其对应的类型信息（即`TypeMirror`对象）

```java
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

TypeMirror type_Activity = elementUtils.getTypeElement("android.app.Activity").asType();
TypeMirror type_Service = elementUtils.getTypeElement("android.app.Service").asType();
TypeMirror fragmentTm = elementUtils.getTypeElement("android.app.Fragment").asType();
TypeMirror fragmentTmV4 = elementUtils.getTypeElement("android.support.v4.app.Fragment").asType();

// 判断注解的类必须是Activity
for (Element element : routeElements) { //Element可以是MainActivity、LoginFragment 等等
    TypeMirror tm = element.asType();
    // Activity or Fragment
   if (types.isSubtype(tm, type_Activity) || types.isSubtype(tm, fragmentTm) || types.isSubtype(tm, fragmentTmV4)) {
        // 省略一万行
   }
}
```

## 集成IARouterGroup
```java
// Interface of ARouter
// com.alibaba.android.arouter.facade.template.IRouteGroup
TypeElement type_IRouteGroup = elementUtils.getTypeElement(IROUTE_GROUP);
// com.alibaba.android.arouter.facade.template.IProviderGroup
TypeElement type_IProviderGroup = elementUtils.getTypeElement(IPROVIDER_GROUP);

// 生成Group java文件：ARouter$$Group$$app.java
String groupFileName = NAME_OF_GROUP + groupName;
JavaFile.builder(PACKAGE_OF_GENERATE_FILE,
                        TypeSpec.classBuilder(groupFileName) // 文件名
                                .addJavadoc(WARNING_TIPS)
                                .addSuperinterface(ClassName.get(type_IRouteGroup)) // 实现IRouteGroup接口
                                .addModifiers(PUBLIC)
                                .addMethod(loadIntoMethodOfGroupBuilder.build())
                                .build()
                ).build().writeTo(mFiler);
// 添加到rootMap中 Map<String, String> rootMap = new TreeMap<>();
rootMap.put(groupName, groupFileName);
```

### JavaPoet构造Group类中的代码
1. $S 字符串
1. $N 变量
1. $T 类


1、构造方法
```java
ParameterSpec groupParamSpec = ParameterSpec.builder(inputMapTypeOfGroup, "atlas").build();
MethodSpec.Builder loadIntoMethodOfGroupBuilder = MethodSpec.methodBuilder(METHOD_LOAD_INTO)
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC)
        .addParameter(groupParamSpec);
```
2、构造方法中的语句
```java
/**==============================================
 * Group构造loadInto()方法的构造器, 中添加语句:
 *   1. atlas.put("/app/HostActivity", RouteMeta.build(xxx, HostActivity.class, "/app/hostactivity", xxx));
 *   2. atlas.put("/app/MainActivity", RouteMeta.build(xxx, MainActivity.class, "/app/hostactivity", xxx));
 *==============================================*/
loadIntoMethodOfGroupBuilder.addStatement(
        "atlas.put($S, $T.build($T." + routeMeta.getType() + ", $T.class, $S, $S, " + (StringUtils.isEmpty(mapBodnull : ("new java.util.HashMap<String, Integer>(){{" + mapBodyBuilder.toString() + "}}")) + ", " + routegetPriority() + ", " + routeMeta.getExtra() + "))",
        routeMeta.getPath(),
        routeMetaCn,
        routeTypeCn,
        className,
        routeMeta.getPath().toLowerCase(),
        routeMeta.getGroup().toLowerCase());
```
3、构造Group类,for循环构造
```java
for (Map.Entry<String, Set<RouteMeta>> entry : groupMap.entrySet()) {
    // 1. 构造方法
    // 2. 构造语句
    for(xxx){
        //xxx
    }
    // 3. ARouter$$Group$$【GroupName】.java
    JavaFile.builder(xxx).build().writeTo(mFiler);
}
```

4、构造Root文件
```java
//ARouter$$Root$$【ModuleName】.java
// 把生成的Group类的class放入到routes集合中
for (Map.Entry<String, String> entry : rootMap.entrySet()) {
    loadIntoMethodOfRootBuilder.addStatement("routes.put($S, $T.class)", entry.getKey(), ClassName(PACKAGE_OF_GENERATE_FILE, entry.getValue()));
}
// 构建Root类
String rootFileName = NAME_OF_ROOT + SEPARATOR + moduleName;
JavaFile.builder(PACKAGE_OF_GENERATE_FILE,
        TypeSpec.classBuilder(rootFileName)
                .addJavadoc(WARNING_TIPS)
                // 指定父类接口 xxx.IRouteRoot
                .addSuperinterface(ClassName.get(elementUtils.getTypeElement(ITROUTE_ROOT)))
                .addModifiers(PUBLIC)
                .addMethod(loadIntoMethodOfRootBuilder.build())
                .build()
```

**和传统，方法-类-包，顺序不同，有implements需要直接生成JavaFile**

## 注解处理器：思路总结
1. 注解处理器出好了每个Element（判断好类型）
1. 构造出数据集合用于后面构建Group类和Root类
    1. 构造出RouteMeta(Class、path、group等)，存放到所属的`Set<RouteMeta>`中(add)
    1. 把PathGroup放入到GroupMap中 `Map<String, Set<RouteMeta>>` key=group，value=set
    1. GroupMap存放到RootMap中`rootMap.put(groupName, groupFileName);`
1. 构造Group类文件 * N `ARouter$$Group$$【GroupName】` // atlas.put(xxx, xxx);
1. 构造Provider文件
1. 构造Root类文件 `ARouter$$Root$$【ModuleName】` //routes.put(xxx, xx)
