# ARouter

[本文链接，点击这里]()

==>【ARouter优化】：不进行全局加载，用到再加载。
==> 提高启动速度

核心点：
1. Apt + JavaPoet, EventBus是传统的一行行写入
1. Apt生成Group类和Path类，Group中存放group和生成Path类映射关系，Path类中存入时path和Activity.class的映射关系。

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

    // 为了拼接，例如:ARouter$$Group$$personal
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
                            context.startActivity(intent, bundleManager.getBundle());
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

# ARouter插件提高速度

```groovy
classpath "com.alibaba:arouter-register:1.0.2"
```
可以提升800ms到1.1s
根据字节码插桩找到类似于register的类，然后向该类的 loadRouteMap 方法中插入逻辑即可

Arouter自动加载路由表的插件是使用的通过gradle插桩技术在编译期插入代码来达到自动加载路由表信息。那么在 ARouter 初始化的时候就不会再去查找过滤相应的以 com.alibaba.android.arouter.routes开头的类名了，从而达到减少初始化时间的目的。

会自动加载所有的插件