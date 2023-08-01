# 重学ARouter

[本文链接，点击这里](https://blog.csdn.net/feather_wch/article/details/132006639)

==>【ARouter优化】：不进行全局加载，用到再加载。
==> 提高启动速度
==> 支持到ANdroid 13，没有任何额外API
==> EventBus优化，大量反射注册类中的Method拿到注解并且缓存起来，用Apt帮助生成中间类加快启动速度

核心点：
1. Apt + JavaPoet, EventBus是传统的一行行写入
1. Apt生成Group类和Path类，Group中存放group和生成Path类映射关系，Path类中存入时path和Activity.class的映射关系。

组件化的意义：不相互依赖，可以交互，任意组合，高度解耦，自动拆卸，重复利用，分层独立化

缺少点：build(Personal.PersonalMainActivity) 直接.出来 => 思路，自动生成类
拦截器 => AOP 思想，不需要AspectJ

ARouter拦截器如何实现？【不是责任链模式】
1. 跳转到Activity时，除了绿色通道，会先交给拦截器进行拦截判断
2. CountDownLaunch根据拦截器数量建立，执行拦截器方法，并且await进行等待
```java
CancelableCountDownLatch interceptorCounter = new CancelableCountDownLatch(Warehouse.interceptors.size());
_excute(0, interceptorCounter, postcard);
//阻塞线程直到超时，或者计数归0
interceptorCounter.await(postcard.getTimeout(), TimeUnit.SECONDS);
```
3. 执行完一个拦截器，计数-1，并且执行下一个拦截器
```java
counter.countDown();
_excute(index + 1, counter, postcard); 
```
4. 顺利通过到拦截器底层，再startActivity

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

### TypeMirror 确定ARouter注解的类 必须满足的类型


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

### 生成IARouterGroup
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

#### JavaPoet构造Group类中的代码
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

#### 实战生成Root文件
```java
            /***======================================================================
             * @目标代码:
             * public class ARouter$$Root$$app implements IRouteRoot{
             *     @Override
             *     public void loadInto(Map<String, Class<? extends IRouteGroup>> routes) {
             *         routes.put("/app/MainActivity", ARouter$$Group$$login.class);
             *     }
             * }
             *=====================================================================*/
            /**==================================================
             * （一）构造出方法
             *==================================================*/
            // 1. 构造参数 Map<String, Class<? extends IRouteGroup>>
            ParameterizedTypeName inputRoutesType = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(
                        ClassName.get(Class.class),
                        // TypeElement type_IRouteGroup = elementUtils.getTypeElement("xxx.IRouteGroup");
                        // IRouteGroup的子类 === ? extends IRouteGroup
                        WildcardTypeName.subtypeOf(ClassName.get(type_IRouteGroup))
                )
            );
            // 参数类型名 + 变量名
            ParameterSpec inputRoutesSpec = ParameterSpec.builder(inputRoutesType, "routes").build();
            // 2、构造出方法
            MethodSpec.Builder loadIntoMethodBuilder = MethodSpec.methodBuilder("loadInto") // 方法名
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .addParameter(inputRoutesSpec);
            /**==================================================
             * （二）构造出语句
             *==================================================*/
            for (Map.Entry<String, String> entry : rootMap.entrySet()) {
                loadIntoMethodBuilder.addStatement("routes.put($S, $T.class)",
                        entry.getKey(), // "/app/MainActivity"
                            // packageName + simpleName // ARouter$$Group$$login
                        ClassName.get(PACKAGE_OF_GENERATE_FILE, entry.getValue()));
            }
            /**==================================================
             * （三）构造出类文件
             *==================================================*/
            JavaFile.builder("com.alibaba.android.arouter.routes", // 包名
                    TypeSpec.classBuilder("ARouter$$Root$$app") // Java文件名
                            .addJavadoc(WARNING_TIPS)
                            // 父类接口 xxx.IRouteRoot
                            .addSuperinterface(ClassName.get(elementUtils.getTypeElement(ITROUTE_ROOT)))
                            .addModifiers(PUBLIC)
                            // 加入方法
                            .addMethod(loadIntoMethodOfRootBuilder.build())
                            .build()
            ).build().writeTo(mFiler);
```

**MethodSpec.Builder中要生成返回值，需要returns(xxx)方法**

### WildcardTypeName

作用是什么？
1. 用于表示通配符类型的名称。通配符类型是一种特殊的泛型类型。
1. 在泛型和注解处理器等场景中起着重要的作用。
```java
//Class<? extends IRouteGroup>>
WildcardTypeName.subtypeOf(ClassName.get(type_IRouteGroup))
```

### ClassName.get(xxx)

作用：
1. `javax.lang.model.element`包中的静态方法，用于获取给定元素（`Element`）的完全限定类名（Fully Qualified Class Name）。
1. 多用于APT, 可获取`类、方法、字段`

### 注解处理器：构造Root和Group思路总结
1. 注解处理器出好了每个Element（判断好类型）
1. 构造出数据集合用于后面构建Group类和Root类
    1. 构造出RouteMeta(Class、path、group等)，存放到所属的`Set<RouteMeta>`中(add)
    1. 把PathGroup放入到GroupMap中 `Map<String, Set<RouteMeta>>` key=group，value=set
    1. GroupMap存放到RootMap中`rootMap.put(groupName, groupFileName);`
1. 构造Group类文件 * N `ARouter$$Group$$【GroupName】` // atlas.put(xxx, xxx);
1. 构造Provider文件
1. 构造Root类文件 `ARouter$$Root$$【ModuleName】` //routes.put(xxx, xx)

## Autowired依赖注入

### 参数传递

1、ARouter传递参数需要借助ButterKnife思想
1. 注解处理器源码文件：`AutowiredProcessor.java`


2、需要制动注入规范AutowiredGet和自动生成的类(实现依赖注入)
```kotlin
// 定义规范直接注入数据
interface AutowiredGet {
    fun autowired(instance: Any)
}
```
```kotlin
// 自动生成的类，从Intent中拿出数据，再放回。
class `MainActivity$$Autowired` : AutowiredGet{
    override fun autowired(instance: Any) {
        if(instance is Activity){
            instance.name = instance.getIntent().getStringExtra("name")
            instance.sex = instance.getIntent().getStringExtra("sex")
        }
    }
}
```
```kotlin
onCreate(){
    autowired(this) // 传入Activity，然后内部注入数据。
}
```

3、注解处理器process()：动态为Autowired注解的类生成，自动注入的类(Helper辅助类)
1. 将@Autowired字段分类
1. 生成能注入数据的Helper类
```java
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        try {
            // 1. Autowired注册元素，所有被Autowired注解的元素，加入到HashMap中。
            categories(roundEnvironment.getElementsAnnotatedWith(Autowired.class));
            // 2. 处理器？
            generateHelper();
        } catch (Exception e) {
            logger.error(e);
        }
        return true;
    }
```

2、注解处理器一：使用@AutoWired字段分类：
```java
    private void categories(Set<? extends Element> elements) throws IllegalAccessException {
        if (CollectionUtils.isNotEmpty(elements)) {
            for (Element element : elements) {
                // enclosingElement: 对于字段就是类的信息，比如MainActivity等等
                TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
                
                if (element.getModifiers().contains(Modifier.PRIVATE)) { // 不能为private
                    throw new IllegalAccessException("The inject fields CAN NOT BE 'private'!!! please check field ["
                            + element.getSimpleName() + "] in class [" + enclosingElement.getQualifiedName() + "]");
                }

                // key = 外部的类的信息，value = List<Elements> 存放子元素
                if (parentAndChild.containsKey(enclosingElement)) { // Has categries
                    parentAndChild.get(enclosingElement).add(element);
                } else {
                    List<Element> childs = new ArrayList<>();
                    childs.add(element);
                    parentAndChild.put(enclosingElement, childs);
                }
            }
        }
    }
```

3、动态生成处理注解的类: MainActivity$$Autowired implements AutowiredGet
```kotlin

```

注解的服务类：AutowiredService
```java
public interface AutowiredService extends IProvider {
    /**
     * Autowired core.
     * @param instance the instance who need autowired.
     */
    void autowire(Object instance);
}
```

#### 自定义Kotlin实现依赖注入逻辑

**需要kapt才可以，很多注意事项 =>未完待续**

**该代码感受一下意思，并不能运行**
```kotlin
@AutoService(Processor::class)
@SupportedAnnotationTypes("com.alibaba.android.arouter.compiler.processor.feather.Autowired")
class MyAutowiredProcessor: BaseProcessor() {

    var elementUtils:Elements? = null
    var mFiler: Filer? = null

    override fun init(processingEnv: ProcessingEnvironment?) {
        super.init(processingEnv)
        elementUtils = processingEnv?.elementUtils
        mFiler = processingEnv?.filer
    }

    /**
     * TIPS:
     * return true: 执行一次，检测一次
     * return false: 不会再检测了
     */
    override fun process(set: MutableSet<out TypeElement>?, roundEnvironment: RoundEnvironment?): Boolean {

        // 0. 没有使用注解的。可能是第二次检测进入的，可能有新增的也可能没有了。
        if (CollectionUtils.isNotEmpty(set)) {
            return false // 不会再检测了
        }

        val type_Annotation = elementUtils?.getTypeElement("com.alibaba.android.arouter.compiler.processor.feather.Autowired")
        // 1. 同Activity、Fragment的数据分类到一起
        categories(roundEnvironment?.getElementsAnnotatedWith(type_Annotation))
        // 2. 产生辅助类
        generateHelper()
        return true // 执行一次，检测一次
    }

    /**==============================================
     * 1、同Activity、Fragment字段分类
     *==========================================*/
    val parentAndChild: MutableMap<TypeElement, MutableList<Element>> = HashMap()
    // 分类
    fun categories(elements: Set<out Element>?){

        elements?.forEach{element->
            if(element.modifiers.contains(Modifier.PRIVATE)){
                throw IllegalAccessException("不能为private")
            }
            val enclosingElement:TypeElement = element.enclosingElement as TypeElement
            if(parentAndChild.containsKey(enclosingElement)){
                (parentAndChild[enclosingElement])?.add(element)
            }else{
                val childs: MutableList<Element> = ArrayList()
                childs.add(element)
                parentAndChild.put(enclosingElement, childs)
            }
        }
    }

    /**===========================================
     * 2、生成辅助类，针对每个Activity、Fragment生成辅助类
     *
     *==========================================*/
    private fun generateHelper() {


        parentAndChild.forEach { typeElement, elements ->

            // （一）生成参数 instance: Any
            // override fun autowired(instance: Any)
            var inputInstanceType = ParameterizedTypeName.get(Any::class.java)
            // （二）生成方法
            val parameterSpec = ParameterSpec.builder(inputInstanceType, "instance").build()
            val autowiredMethodSpec = MethodSpec.methodBuilder("autowired")
                .addAnnotation(ClassName.get("java.lang", "Override"))
                .addModifiers(Modifier.PUBLIC)
                .addParameter(parameterSpec)

            // （三）添加语句
            elements.forEach {
                //  instance.name = instance.getIntent().getStringExtra("name")
                autowiredMethodSpec.addStatement("xxxxxxxxxxxxx", it.simpleName)

                // @ 未来扩展：图片共享
                // autowiredMethodSpec.addStatement(xxx)里面，实现类似如下话语
                // a.setOrderDrawable(RouterMmanager.getInstance().build("/order/getDrawable").navigation) // 可以导航出目标资源文件的R.drawable.xxx
            }

            // （四）生成文件, 每个Activity、Fragment都生成一个文件。该代码无法测试稳定性
            JavaFile.builder(
                "com.alibaba.android.arouter.routes",  // 包名
                TypeSpec.classBuilder("MainActivity\$\$Autowired") // Java文件名
                    .addJavadoc(Consts.WARNING_TIPS) // 父类接口 xxx.IRouteRoot
                    .addSuperinterface(ClassName.get(elementUtils!!.getTypeElement("xxx.AutowiredGet")))
                    .addModifiers(Modifier.PUBLIC) // 加入方法
                    .addMethod(autowiredMethodSpec.build())
                    .build()
            ).build().writeTo(mFiler)
        }
    }
}
```

#### getStringExtra("name") JavaPoet + Apt 如何生成？

```java
activity.getIntent().getStringExtra("name")
```
1. 根据字段类型的不同，选择getString、Boolean等方法

核心点：
```java
// 字段的类型，如Int、Boolean
int type = element.getKind().ordinal();
// 字段名， name、age、sex
String fieldName = element.getSimpleName().toString();

// 假如有注解给定的默认值，使用默认值，不从Intent中获取
String annotationValue = element.getAnnotation(Autowired.class).name()

// 拼接成 activity.getIntent().getStringExtra("name")
// 省略一万行
```

#### 数据注入：ParameterManager 

1、数据注入的逻辑
1. 查找生成的`MainActivity$$Autowired`类
1. 使用生成的类

2、ParameterManager的实现
1. 单例模式
1. `LruCache<String, AutowiredGet> caches`
1. public void loadParameter(Activity activity); // 从caches中查找到对应的`MainActivity$$Autowired.java`
1. 查找到后调用AutowiredGet.autowired(activity) // 内部会去赋值
```java
class ParameterManager{
    LruCache<String, AutowiredGet> caches;
    public void loadParameter(Activity activity){
        // 1、找到生成的类
        AutowiredGet auto = caches.get(activity.name);
        // 2、使用
        auto.autowired(activity); 
        // 内部代码赋值, 类似下面代码：
        // activity.name = activity.getIntent().getStringExtra("name")
        // activity.age = activity.getIntent().getIntExtra("age")
        // activity.sex = activity.getIntent().getStringExtra("sex")
    }
}
```

## Navigation():二次封装

### RouteManager

RouteManager代码
```java
/**
 * 整个目标
 * 第一步：查找 ARouter$$Group$$personal ---> ARouter$$Path$$personal
 * 第二步：使用 ARouter$$Group$$personal ---> ARouter$$Path$$personal
 */
public class RouterManager {

    private String group; // 路由的组名 app，order，personal ...
    private String path;  // 路由的路径  例如：/order/Order_MainActivity

    /**
     * 上面定义的两个成员变量意义：
     * 1.拿到ARouter$$Group$$personal  根据组名 拿到 ARouter$$Path$$personal
     * 2.操作路径，通过路径 拿到  Personal_MainActivity.class，就可以实现跳转了
     */

    // 单例模式
    private static RouterManager instance;

    public static RouterManager getInstance() {
        if (instance == null) {
            synchronized (RouterManager.class) {
                if (instance == null) {
                    instance = new RouterManager();
                }
            }
        }
        return instance;
    }

    // 提供性能  LRU缓存
    private LruCache<String, ARouterGroup> groupLruCache;
    private LruCache<String, ARouterPath> pathLruCache;

    // 拼接为了查找，例如:ARouter$$Group$$personal
    private final static String FILE_GROUP_NAME = "ARouter$$Group$$";

    private RouterManager() {
        groupLruCache = new LruCache<>(100);
        pathLruCache = new LruCache<>(100);
    }

    /***
     * @param path 例如：/order/Order_MainActivity
     *      * @return
     */
    public BundleManager build(String path) {
        if (TextUtils.isEmpty(path) || !path.startsWith("/")) {
            throw new IllegalArgumentException("不按常理出牌 path乱搞的啊，正确写法：如 /order/Order_MainActivity");
        }

        // 同学可以自己增加
        // ...

        if (path.lastIndexOf("/") == 0) { // 只写了一个 /
            throw new IllegalArgumentException("不按常理出牌 path乱搞的啊，正确写法：如 /order/Order_MainActivity");
        }

        // 截取组名  /order/Order_MainActivity  finalGroup=order
        String finalGroup = path.substring(1, path.indexOf("/", 1)); // finalGroup = order

        if (TextUtils.isEmpty(finalGroup)) {
            throw new IllegalArgumentException("不按常理出牌 path乱搞的啊，正确写法：如 /order/Order_MainActivity");
        }

        // 证明没有问题，没有抛出异常
        this.path =  path;  // 最终的效果：如 /order/Order_MainActivity
        this.group = finalGroup; // 例如：order，personal

        // TODO 走到这里后  grooup 和 path 没有任何问题   app，order，personal      /app/MainActivity

        return new BundleManager(); // Builder设计模式 之前是写里面的， 现在写外面吧
    }

    // 真正的导航
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public Object navigation(Context context, BundleManager bundleManager) {
        // 例如：寻找 ARouter$$Group$$personal  寻址   ARouter$$Group$$order   ARouter$$Group$$app
        String groupClassName = context.getPackageName() + "." + FILE_GROUP_NAME + group;
        Log.e("derry >>>", "navigation: groupClassName=" + groupClassName);


        try {
            // TODO 第一步 读取路由组Group类文件
            ARouterGroup loadGroup = groupLruCache.get(group);
            if (null == loadGroup) { // 缓存里面没有东东
                // 加载APT路由组Group类文件 例如：ARouter$$Group$$order
                Class<?> aClass = Class.forName(groupClassName);
                // 初始化类文件
                loadGroup = (ARouterGroup) aClass.newInstance();

                // 保存到缓存
                groupLruCache.put(group, loadGroup);
            }

            if (loadGroup.getGroupMap().isEmpty()) {
                throw new RuntimeException("路由表Group报废了..."); // Group这个类 加载失败
            }

            // TODO 第二步 读取路由Path类文件
            ARouterPath loadPath = pathLruCache.get(path);
            if (null == loadPath) { // 缓存里面没有东东 Path
                // 1.invoke loadGroup
                // 2.Map<String, Class<? extends ARouterLoadPath>>
                Class<? extends ARouterPath> clazz = loadGroup.getGroupMap().get(group);

                // 3.从map里面获取 ARouter$$Path$$personal.class
                loadPath = clazz.newInstance();

                // 保存到缓存
                pathLruCache.put(path, loadPath);
            }

            // TODO 第三步 跳转
            if (loadPath != null) { // 健壮
                if (loadPath.getPathMap().isEmpty()) { // pathMap.get("key") == null
                    throw new RuntimeException("路由表Path报废了...");
                }

                // 最后才执行操作
                RouterBean routerBean = loadPath.getPathMap().get(path);

                if (routerBean != null) {
                    switch (routerBean.getTypeEnum()) {
                        case ACTIVITY:
                            Intent intent = new Intent(context, routerBean.getMyClass()); // 例如：getClazz == Order_MainActivity.class
                            intent.putExtras(bundleManager.getBundle()); // 携带参数
                            // context.startActivity(intent, bundleManager.getBundle()); // 大部分手机有问题，没有任何反应
                            context.startActivity(intent);
                            break;
                        //同学们可以自己扩展 类型
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
```

BundleManager:帮助跳转时参数传递（支持链式调用）
```java
/**
 * 跳转时 ，用于参数的传递
 */
public class BundleManager {

    // Intent传输  携带的值，保存到这里
    private Bundle bundle = new Bundle();

    public Bundle getBundle() {
        return this.bundle;
    }

    // 对外界提供，可以携带参数的方法
    public BundleManager withString(@NonNull String key, @Nullable String value) {
        bundle.putString(key, value);
        return this; // 链式调用效果 模仿开源框架
    }

    public BundleManager withBoolean(@NonNull String key, @Nullable boolean value) {
        bundle.putBoolean(key, value);
        return this;
    }

    public BundleManager withInt(@NonNull String key, @Nullable int value) {
        bundle.putInt(key, value);
        return this;
    }

    public BundleManager withBundle(Bundle bundle) {
        this.bundle = bundle;
        return this;
    }

    // Derry只写到了这里，同学们可以自己增加 ...

    // 直接完成跳转
    public Object navigation(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return RouterManager.getInstance().navigation(context, this); // 把自己传入，就能传递参数了
        }
        return null;
    }
}
```

最终跳转效果：
```java
RouterManager.getInstance()
    .build("/app/LoginActivity")
    .withInt("age", 22)
    .withString("name", "feather")
    .navigation();
```

#### 初步：总结

Navigation：
1. group、path合法性检查
1. 构造出用于携带参数的BundleManager(),放入参数
1. navigation()-缓存中查找Root(Group)类，不存在就Class.forName(),newInstance()创建并且缓存
1. 缓存或者Root类的内部中找到Group(Path)类的名称，不存在就newInstance构造，并缓存
1. Group内部的集合中，查找到MainActivity对应的RouteMeta，里面有MainActivity.class
1. startActivity()跳转

## 图片路由/共享

路由Navigation+依赖注入：两种方法结合1，自动化，高效

需要实现部分：
1、arouter-api: Call 跨业务毁掉
```java
/**
 * 跨模块业务回调，空接口可集成拓展/实现
 *
 * 图片共享   组件 和 组件之间
 * Bean共享  组件 和 组件之间
 * ....
 */
public interface Call {
}
```
2、common公共模块：OrderDrawable 用于各个模块查询到
```kotlin
public interface OrderDrawable extends Call {
    int getDrawable();
}
```
3、order订单模块：OrderDrawableImpl 实现，真正提供Drawable的资源ID
```java
// order 自己决定 自己的暴漏
@ARouter(path = "/order/getDrawable")
// 【会被扫描到，构建出RouteMeta并且存储】
public class OrderDrawableImpl implements OrderDrawable {
    @Override
    public int getDrawable() {
        return R.drawable.ic_ac_unit_black_24dp; // 自己决定暴露
    }
}
```

4、arouter-compiler: **RouteProcessor.java**-为了能路由出来数据
```java
// 在生成的类似 ARouter$$Group$$order 文件中，添加逻辑。
// * 添加RouterBean.TypeEnum.CALL 的 RouterBean

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        // 【TODO 新增点1】
        TypeElement callType = elementTool.getTypeElement(ProcessorConfig.CALL);
        TypeMirror callMirror = callType.asType(); // 自描述 callMirror

        // xxxxxxxxxxxxxxxxxxxxxxxxxxxxx

        // TODO  一系列的检查工作
        RouterBean routerBean = new RouterBean.Builder()
                    .addGroup(aRouter.group())
                    .addPath(aRouter.path())
                    .addElement(element)
                    .build();
        TypeMirror elementMirror = element.asType();
        if (typeTool.isSubtype(elementMirror, activityMirror)) { // android.app.Activity
            routerBean.setTypeEnum(RouterBean.TypeEnum.ACTIVITY);
        } else if (typeTool.isSubtype(elementMirror, callMirror)) { // 【TODO 新增点2】
            routerBean.setTypeEnum(RouterBean.TypeEnum.CALL);
        }

        // xxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    }
```

5、arouter-api: **RouteManager.java**-navigation路由到该图片的资源ID
```java
RouterBean routerBean = loadPath.getPathMap().get(path);
if (routerBean != null) {
    switch (routerBean.getTypeEnum()) {
        case ACTIVITY:
            Intent intent = new Intent(context, routerBean.getMyClass()); // 例如：getClazz == Order_MainActivity.class
            intent.putExtras(bundleManager.getBundle()); // 携带参数
            // context.startActivity(intent, bundleManager.getBundle()); // 大部分手机有问题，没有任何反应
            context.startActivity(intent);
            break;

    /**===========================================
     *       navigation()中增加对Call的处理
     * ==========================================*/
        case CALL: 
            // OrderAddressImpl.class  OrderBean getOrderBean
            Class<?> clazz = routerBean.getMyClass(); // OrderUserImpl BaseUser实体
            Call call = (Call) clazz.newInstance();
            bundleManager.setCall(call);
            return bundleManager.getCall();

        //同学们可以自己扩展 类型
    }
}
```

6、arouter-compiler: **MyAutowiredProcessor.java**-自动依赖注入
```java
// autowired()生成的参数依赖注入文件中，添加类似代码：
if (typeUtils.isSubtype(typeMirror, callMirror)) { // 实现了Call接口
    // t.orderDrawable = (OrderDrawable) RouterManager.getInstance().build("/order/getDrawable").navigation(t);
    methodContent = "t." + fieldName + " = ($T) $T.getInstance().build($S).navigation(t)";
    method.addStatement(methodContent,
            TypeName.get(typeMirror),
            ClassName.get(ProcessorConfig.AROUTER_API_PACKAGE, ProcessorConfig.ROUTER_MANAGER),
            annotationValue);
    return;
} else { // 对象的传输
    methodContent = "t.getIntent().getSerializableExtra($S)";
}
```

### 实战角度

#### 公共基础库

新增暴露接口OrderDrawable
```java
public interface OrderDrawable extends Call {
    int getDrawable();
}
```

#### 暴露图片方

实现OrderDrawable
```java
// order 自己决定 自己的暴漏
@ARouter(path = "/order/getDrawable")
public class OrderDrawableImpl implements OrderDrawable {
    @Override
    public int getDrawable() {
        return R.drawable.ic_ac_unit_black_24dp; // 自己决定暴露
    }
}
```

#### 使用图片方
依赖注入实现
```java
@AutowiredGet("/order/getDrawable")
OrderDrawable orderDrawable; // 公共基础库common
```


## 实体Bean共享

实现部分：
1. common模块：IUser集成Call
```java
// IUser等价于OrderDrawable
public interface IUser extends Call {
    BaseUser getUserInfo(); //根据不同子模块的具体实现，调用得到不同的结果
}
/**
 * 例如：用户实体父类
 */
public class BaseUser implements Serializable {
    private String name;
    private String account;
    private String password;
    private String phoneNumber;
    private int gender;
}

```
2. order模块：UserInfo实现自IUser
```java
// 扩展BaseUser
public class UserInfo extends BaseUser {
    private String token;
    private int vipLevel;
}
```

3. order模块：UserInfoImpl实现UserInfo,打上注解@ARouter // APT会根据这个类的父类是Call，特殊处理采用Call类型
```java
@ARouter(path = "/order/getUserInfo")
public class OrderUserImpl implements IUser {
    @Override
    public BaseUser getUserInfo() {
        // 我order模块，具体的Bean，由我自己
        UserInfo userInfo = new UserInfo();
        userInfo.setName("Derry");
        userInfo.setAccount("154325354");
        userInfo.setPassword("1234567890");
        userInfo.setVipLevel(999);
        return userInfo;
    }
}
```
4. app/使用者模块:UserInfo，打上注解@AutowiredGet
```java
#MainActivity.java
    @Parameter(name = "/order/getUserInfo")
    IUser iUser; // 公共基础库common
```

## 方法互调：网络请求功能

personal模块想要调用order订单模块的网络请求要怎么做？

1. common模块：IRequestOrder
2. common模块：OrderBean
3. order模块：RequestImpl implements IRequestOrder
4. personal模块：依赖注入(注解)、请求

1、common模块：IRequestOrder
```java
/**
 * 订单模块对外暴露接口，其他模块可以获取返回业务数据
 */
public interface IRequestOrder extends Call {
    OrderBean getOrderBean(String key, String ip) throws IOException;
}
```
2、common模块：OrderBean
```java
/**
 * {"resultcode":"200","reason":"查询成功",
 * "result":{"Country":"美国","Province":"加利福尼亚","City":"","Isp":""},"error_code":0}
 */
public class OrderBean {
    private String resultcode;
    private String reason;
    private Result result;
    private int error_code;

    public static class Result {
        private String Country;
        private String Province;
        private String City;
        private String Isp;
    }
}
```
3、order模块：RequestOrderImpl implements IRequestOrder
```java
// Retrofit相关
public interface OrderServices {
    @POST("/ip/ipNew")
    @FormUrlEncoded
    Call<ResponseBody> get(@Field("ip") String ip, @Field("key") String key);
}
```
```java
@ARouter(path = "/order/getOrderBean") // /order/getOrderBean
public class RequestOrderImpl implements IRequestOrder {

    private final static String BASE_URL = "http://apis.juhe.cn/";

    // 暴漏给 各个模块使用
    @Override
    public OrderBean getOrderBean(String key, String ip) throws IOException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .build();
        OrderServices host = retrofit.create(OrderServices.class);

        // Retrofit GET同步请求
        Call<ResponseBody> call = host.get(ip, key);

        retrofit2.Response<ResponseBody> response = call.execute();
        if (response != null && response.body() != null) {
            JSONObject jsonObject = JSON.parseObject(response.body().string());
            OrderBean orderBean = jsonObject.toJavaObject(OrderBean.class);
            System.out.println("order订单组件中独有的网络请求功能：解析后结果 >>> " + orderBean.toString());
            return orderBean;
        }
        return null;
    }
}
```
4、personal模块：
```java
    // 拿order模块的 网络请求功能
    @AutowiredGet(name = "/order/getOrderBean")
    IRequestOrder requestOrder;

    new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OrderBean orderBean = requestOrder.getOrderBean("aa205eeb45aa76c6afe3c52151b52160", "144.34.161.97");
                    Log.e(Cons.TAG, "从Personal跨组件到Order，并使用Order网络请求功能：" + orderBean.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }).start();
```

## 对象传输：ARouter支持

1. common模块：自定义Bean如Student，传入参数
1. compiler模块：AutowiredProcessor中，getIntent.getSerializableExtra("student")
1. 使用者模块：@AutowiredGet注解标记字段。

## ARouter源码全流程打通

## ARouter插件提高速度

```groovy
classpath "com.alibaba:arouter-register:1.0.2"
```

性能：可以提升800ms到1.1s

原理：根据字节码插桩找到类似于register的类，然后向该类的 loadRouteMap 方法中插入逻辑即可

Arouter自动加载路由表的插件是使用的通过gradle插桩技术在编译期插入代码来达到自动加载路由表信息。那么在 ARouter 初始化的时候就不会再去查找过滤相应的以 com.alibaba.android.arouter.routes开头的类名了，从而达到减少初始化时间的目的。

会自动加载所有的插件

### 原有性能损耗

1、遍历app的dex，找到其中包名“com.alibaba.android.arouter.routes”下所有的类名，并且打包成集合返回
2、ARouter的init做的工作就是把所有找到的类，调用loadInfo方法，调用routes.put("service", ARouter$$Group$$Service.class)
3、性能损耗很大
```java
Set<String> routerMap;
routerMap = ClassUtils.getFileNameByPackageName(mContext, ROUTE_ROOT_PAKCAGE);

// 双重循环，查找Dex文件
for (final String path : paths) {
    // xxx
    //NOT use new DexFile(path), because it will throw "permission error in /data/dalvik-cache"
    dexfile = DexFile.loadDex(path, path + ".tmp", 0);
    // xxx
    while (dexEntries.hasMoreElements()) {
        String className = dexEntries.nextElement();
        if (className.startsWith(packageName)) {
            classNames.add(className);
        }
    }
}
```

### 性能优化

项目名：arouter-gradle-plugin

1. 耗时代码进行优化
1. 可以提升800ms到1.1s

### 实战plugin进行初始化

ASM字节码插桩，不需要再去遍历Dex后拿到生成的**class**并且加入到Caches缓存中。

留下字节码插桩空壳方法：ASM去调用生成ARouter$$Root$$ModuleName.java的loadInto
```java
    private static void loadRouterMap() {
        registerByPlugin = false;
        // auto generate register code by gradle plugin: arouter-auto-register
        // looks like below:
        // registerRouteRoot(new ARouter..Root..modulejava()); // module 1
        // registerRouteRoot(new ARouter..Root..modulekotlin()); // module 2
    }

    private static void registerRouteRoot(IRouteRoot routeRoot) {
        markRegisteredByPlugin();
        if (routeRoot != null) {
            routeRoot.loadInto(Warehouse.groupsIndex);
        }
    }
```


## 参考资料

Github的ARouter地址：https://github.com/alibaba/ARouter

中文ARouter使用API：https://github.com/alibaba/ARouter/blob/master/README_CN.md

官方解释，最全的：https://developer.aliyun.com/article/71687

可能是最详细的ARouter源码分析：https://www.jianshu.com/p/bc4c34c6a06c

通俗易懂的文章：https://blog.csdn.net/CodeFarmer__/article/details/102762029
