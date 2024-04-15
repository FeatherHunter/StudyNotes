# 注解Annotation

## 注解的相关问题

1、注解的定义？（注解是什么）
> Java的标注，本身没有任何意义，需要结合反射、插桩等技术才有意义
> 帮助：解耦、减少代码量、简化代码
> 
> 1. JDK1.5引入，是元数据的一种形式
> 2. 提供了有关程序但不属于程序本身的数据
> 3. 注解对注解的代码没有直接影响

2、为什么需要注解？
> 能够在运行时和编译时，获取额外的信息、帮助生成额外的代码

3、注解有什么用？
> 1. buffterknife等开源框架，可以借助注解，知道哪些方法需要反射调用，哪些地方可以用ASM去动态生成代码
> 2. ARouter借助注解，生成路由代码，用于从Root找到Group找到目标，增加处理逻辑

4、怎么使用注解？
> 1. 编译时注解
> 2. JVM层面注解
> 3. 运行时注解
> 4. 包含两大重要的信息：RetentionPolicy（存在时期）、ElementType

## 注解基础概念

1、元注解是什么？
> 注解的注解

### RetentionPolicy

1、存在时期


### ElementType

1、元素类型
```java
// TYPE: 类、接口、注解、枚举
public enum ElementType {
    TYPE,    /** Class, interface (including annotation type), or enum declaration */
    FIELD,    /** Field declaration (includes enum constants) */
    METHOD,    /** Method declaration */
    PARAMETER,    /** Formal parameter declaration */
    CONSTRUCTOR,       /** Constructor declaration */
    LOCAL_VARIABLE, /** Local variable declaration */
    ANNOTATION_TYPE,/** Annotation type declaration */
    PACKAGE,    /** Package declaration */
    TYPE_PARAMETER,    /**Type parameter declaration @since 1.8*/
    TYPE_USE    /**Use of a type @since 1.8*/
}
```

2、用于Class应该用哪个？
> ElementType.TYPE

### 接口Annotation

1、Annotation是注解的基类

2、继承自Annotation的接口有哪些
1. Override
2. Retention
3. Target
4. Inherited
5. Documented
6. Deprecated


## 自定义注解

1、自定义注解
1. 在interface基础上加@
```java
@Retention(RetentionPolicy.SOURCE) // 保留
@Target(ElementType.METHOD) // 目标
public @interface MyAnnotation{
    String value() default "xxx"; // value数值为String类型
}
// default为默认值
```

2、注解的使用
```java
@MyAnnotation("feather")
class Code24 {}

@MyAnnotation(value = "feather")
class Code24 {}

// 情况二：同时有value和其他key，使用时，必须key-value形式使用
@Retention(RetentionPolicy.SOURCE) // 保留
@Target(ElementType.TYPE) // 目标
public @interface MyAnnotation{
    String value() default "xxx"; // value数值为String类型
    int id();
}
@MyAnnotation(value = "feather", id = 10)
```
> 1. value是特殊的情况，其他的都要写key-value


## 注解保留期
注解的保留：
SOURCE-源码级别
适合技术：APT，在编译时生成辅助类。如ARouter、ButterKnife、EventBus等
CLASS-字节码级别
适合技术：字节码增强技术，ASM，新增或者更改class文件的逻辑等
RUNTIME-运行时
适合技术：反射，运行时根据标志进行反射，实现相关逻辑
### SOURCE

#### APT2
编写注解处理器：Java工程
注解处理运行在什么阶段？
1. 编译阶段
是如何运行的？
> .java文件编译成.class，javac会解析处理java文件
> 会将采集的所有注解信息，封装成节点，注解处理
#### 实战 => 看之前博客
1、Android创建module，名为compiler
2、resources -> META-INF.services -> javax.annotation.processing.Processor
```
com.feather.compiler.Myprocessor
```
3、Java类：com.feather.compiler 下MyProcessor
```java
public class MyProcessor extends AbstractProcessor{
    // 
}
```
如何引用注解处理器？
```gradle
annotationProcessor project(':compiler')
```
注解很多如何拿到我们需要的注解？
1. 指定想要处理的注解
```java
@SupportedAnnotationTypes("com.feather.compiler.MyProcessor")
// 会在Build Output的Task中输出
```

#### LINT检查

source阶段的注解，可以用于IDE语法检查，例如：
1. `@IntDef`：自制开源库时，参数需要限定范围，例如int参数，需要在枚举类A和B中二选一


##### IntDef

1. 是注解的注解：元注解
2. 提供语法检查：由谁实现的？IDE在编辑时进行语法检查


1、如何限定参数的类型？
1. 枚举
2. IntDef等注解

2、枚举限定参数的类型有哪些问题？
```java
    // 枚举的每一个元素都是对象，一个对象占用多少字节？（12字节（对象头）+对象体+8byte字节对齐）
    enum WeekDay{
        MONDAY,SUNDAY
    }
    WeekDay field;
    // 枚举占用内存
    public void setWeekDay(WeekDay weekDay){
        field = weekDay;
    }
```

3、IntDef如何限定int参数必须为哪些指定参数？
```java
    /**
     * 常量代替枚举类型, 定义
     */
    public static final int SUNDAY = 0;
    public static final int MONDAY = 1;
    static int day;
    @IntDef({SUNDAY, MONDAY})
    @interface WEEKDAY{}
    public static void setDay(@WEEKDAY int newday){
        day = newday;
    }

    // @IntDef({SUNDAY, MONDAY}) 不可以用于参数
//    public static void setDay2(@IntDef({SUNDAY, MONDAY}) int newday){
//        day = newday;
//    }

// 使用
    public void test(){
        setDay(Test.SUNDAY); // 必须使用@IntDef限定的参数
    }
```

4、DrawableRes
```java
    // AndroidX定义好的语法检查规则
    public void setDrawable(@DrawableRes int drawable){

    }
```

5、Kotlin实现的DrawableRes
```kotlin
@MustBeDocumented
@kotlin.annotation.Retention(AnnotationRetention.BINARY) //BINARY二进制
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FIELD,
    AnnotationTarget.LOCAL_VARIABLE
)
public annotation class DrawableRes
```

### CLASS-字节码增强
aspectj
AOP
插桩
应用场景：
1. 热修复
2. 用@InjectTime注释方法，在.class前后增加打印方法执行时间的代码
1、什么是字节码增强/字节码插桩？
> 本质：在字节码中写代码
> 原理：将源码编译成class文件后，在Android生成dex文件前，修改class文件
> 修改、增强原来的代码逻辑
2、class文件有特定格式，就可以修改
3、流程
```java
.class -> IO -> byte[] -> 按照格式修改 -> .class
```
javap：反编译、查看字节码
### RUNTIME-反射-IOC注入
1、注解+反射：才能在运行时，反射相关标注的代码
2、IOC是什么意思？
> 1. Inversion of Control 控制反转技术，也叫做依赖注入
> 2. 将代码中“主动获取的资源”变成运行时，动态的将某种依赖关系注入到对象中 ==> 依赖注入

Field：自己+父类的成员，不包括private
DeclaredField：只能获取自己的成员（不包括父类）（全部的）
```java
    Class clazz = Code24.class;
    Field field = clazz.getDeclaredField("age");
    // 获取字段
    int age = (int) field.get(new Code24()); // 获取到名称
    // 调用方法
    Method method = clazz.getDeclaredMethod("setAge");
    method.invoke(new Code24(), 24);
```
#### 实战：注入TextView
**注解**
```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectView {
    @IdRes int value();
}
```
**使用**
```java
    @InjectView(R.id.tv_name)
    TextView tv;
// 注入-activity中
    InjectUtils.injectView(this);
```
**注入**
```java
    public static void injectView(Activity activity){
        Class cls = activity.getClass();
        // 1、遍历activity的所有字段
        Field[] fields = cls.getDeclaredFields();
        for (Field field:fields){
            // 2、找到InjectView注入的Field
            if(field.isAnnotationPresent(InjectView.class)){
                InjectView injectView = field.getAnnotation(InjectView.class);
                // 拿到资源ID
                @IdRes int id = injectView.value();
                // 获取View
                View view = activity.findViewById(id);
                // 设置访问权限（用于private、以及提高性能）
                field.setAccessible(true);
                // 设置，注入成功
                try {
                    field.set(activity, view);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
```

#### 反射+泛型：Type体系
Type：本身是接口
```java
public interface Type {
    // 本身用到了JDK1.8的默认方法，为接口提供有实现的方法
    default String getTypeName() {
        return toString();
    }
}
```
Type的四个实现接口：
```java
TypeVariable // 泛型类型变量，包含泛型上下限等信息
ParameterizedType // 具体的泛型类，可以获得元数据中泛型的签名类型（泛型的真实类型）
GenericArrayType // 泛型类的数组，如List[] Map[] 会用到该Type
WildcardType // 通配符类型，获得上下限信息
```
2、Type的实现类是什么？
> Class类
3、真实类型的使用场景
> Gson中传入的是泛型，在反序列化时如何获取到泛型的真实类型（泛型擦除了）
```java
// ERROR!!!代码会报错
Response<Data> res = gson.fromJson(json, Response.class); // 如何将Response传入的泛型，在json中正确解析呢？
// 解决办法,Gson提供的TypeToken
Response<Data> res = gson.fromJson(json, new TypeToken<Response<Data>>(){}.getType());
```
##### TypeToken原理
1、TypeToken源码解析
```java
public class TypeToken<T> {
  final Class<? super T> rawType;
  final Type type;
  protected TypeToken() {
    this.type = getSuperclassTypeParameter(getClass()); // 获取到Response<Data>的类型class
    this.rawType = (Class<? super T>) $Gson$Types.getRawType(type);
  }
  static Type getSuperclassTypeParameter(Class<?> subclass) { // 传入的是TypeToken<Response<Data>>(){}构造的匿名内部类的class文件
    Type superclass = subclass.getGenericSuperclass();
    ParameterizedType parameterized = (ParameterizedType) superclass; // 获取到实际的泛型参数数组，[0]下标0拿到第一个，也就是Response<Data>，此时class的签名中已经明确了是Response<Data>
    return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
  }
  public final Class<? super T> getRawType() {return rawType;}
  public final Type getType() {return type;}
}
```
> 总结：生成的匿名内部类的signature中会有实际类型
>
> 为什么一定要包裹Response<\Data>?
> 通过Response<\Data>并调用parameterized.getActualTypeArguments()[0]只能拿到Data.class而不是Response<\Data>的class
```java
Response<Data>.class // 不允许这样写，因为泛型擦除，不可以给泛型类调用.class
```
2、TypeToken有无花括号的区别
1. 有花括号：匿名内部类，能存储Reponse<\Data>的信息。等效于：
```java
class ChildTypeToken{
    Response<Data> data; // 等效于这种，已经写明了
}
```
2. 无花括号：只是TypeToken对象，因为泛型擦除只知道Object（signature中是Object），无法知道Response<\Data>的存在
> TypeToken为了避免该情况，会将构造方法设置为protected,不让外部构造该对象
#### 实战：参数自动注入（注解+反射）


### 问题
1、APK构建流程
2、


## 知识地图

### APT

1、APT是什么？
1. Annotation Process Tools
2. 注解处理工具，根据注解自动生成代码
3. 使用框架：EventBus、Dagger2、ButterKnife


2、
