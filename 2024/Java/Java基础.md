

# 泛型

## Gson中TypeToken

1、Type是什么？
1. Java中所有类型的公共高级接口，代表了Java中所有类型
2. 参数化类型ParameterizedType 泛型List、泛型Map
3. 数组类型 GenericArrayType，不是String[]等数组，而是泛型数组 T[]
4. 原始类型 平常的类， 还包括 枚举、数组、注解
5. 基本类型 int、float、double等
6. 通配符类型 WildcardType 指的是<?><? extends T>等等

2、泛型有哪些问题？
1. 无法直接获取类型：`.class和getClass()`都不可以
2. Gson中需要知道泛型的类型，才能进行解析

3、自己实现TypeToken
```java
public abstract class MyTypeToken{
    private final Type type;
    public MyTypeToken(){
        ParameterizedType parameterizedType = (ParameterizedType)getClass().getGenericSuperClass();
        Type[] typeArguments = parameterizedType.getActualTypeArguments();
        type = typeArguments[0];
    }

    public Type getType(){
        return type;
    }
}
```
1. TypeToken为抽象类，因此需要具体实现类
```java
public class Response<T>{
    public T data; // json中返回的数据，为了复用不知道具体类型，需要泛型
}
Type type = new TypeToken<Response<String>>(){}.getType();
Response<String> result = new Gson().fromJson(json, type);
// new TypeToken<Response<String>>(){}等效于
class TypeToken$0 extends TypeToken<Response<String>>{
    // 匿名内部类
}
```
2. 在匿名内部类的class文件中，里面明确知道了T的类型。这些泛型的类型信息会存储在signature中

4、getActualTypeArguments返回的是什么？
> 实际类型的Type对象数组，对于TypeToken只有一个泛型参数，因此取结果的下标0即可


# Java通配符
1、Java中？是什么？
1. Java中 ？ 是通配符，只能读取，不能添加。唯一例外是NULL
```java
List<?> list = new ArrayList<>();
list.get(0); // 可以读取
list.add("1"); // 不可以写入
list.add(null); // 特例，可以写入
```

2、Kotlin中 * 是什么？
1. 通配符，又称为星号投射。代表所有类型，相当于Any?
```kotlin
val data:ArrayList<*> = arrayListOf("String", 1, 1.2f);
data.add("111") // 错误，不可以写入
val item = data[0] // 可以读取
```

3、Java和Kotlin的有界编码(泛型的上界)
```java
public class FatherClass{}
public class SonClass extends FaterClass{}

// Java单个上界
public class JavaOne<T extends FatherClass>{}
// Java多个上界
public class JavaTwo<T extends FatherClass & Serilizable>{}

// Kotlin单个上界
class KotlinOne<T:FatherClass>{}
// Kotlin多个上界
class KotlinTwo<T> where T:FatherClass, T:Serilizable{}
```

4、Java和Kotlin读写权限编程
```java
// Java只读
List<? extends FatherClass> list = new ArrayList<SonClass>();
list.get(0); // 可以读取
list.add(sonclass/fatherClass); // 报错，不可以写入
list.add(null); // 可以添加null

// Java只写
List<? super SonClass> list = new ArrayList<FatherClass>();
list.get(0); // 报错，不可以读取，除非当做Object
list.add(sonClass); // 可以写入

// Kotlin只读
val list:MutableList<out FatherClass> = ArrayList<SonClass>()
list.add(sonClass/fatherClass) // 不可以添加
val item = list[0] // 可以读取

// Kotlin只写
val list:MutableList<in SonClass> = ArrayList<FatherClass>()
list.add(sonClass) // 可以添加
val item = list[0] //报错，不可以读取
```

5、Kotlin允许在声明时控制读写模式, Java不允许声明泛型时控制读写模式
```kotlin
class Test<out T>(private val t:T){
    fun get():T = t
}
```

6、List<?>是什么？
> 等价于 List<? extends Object>，通配符，代表所有类型

7、数组默认支持协变，在运行时才检查，容器集合会在编译时检查，使用泛型时优先用集合
> 数组默认支持协变，运行时才检查，集合在编译时检查，使用泛型时优先使用集合

