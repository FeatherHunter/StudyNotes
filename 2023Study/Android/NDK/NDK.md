
# NDK
## C
1、C调用Java
### Java签名类型
2、Java签名类型
1. boolean z
1. long j
1. 对象 Lxx/xx/类名
1. 数组int[] `[I`
3、javap -s -p:反编译class文件并且输出类的详细信息
4、C++深拷贝原理
5、C++区域划分
1. 堆
2. 栈
3. 全局区域（静态区、常量区、字符串区）
4. 代码区域
6、realloc为什么要传入原始指针和总大小？
1. 如果后面内存被其他东西占用，需要到另一块区域分配，需要copy原始数据到新内存
2. 分配的空间可以连续
3. 不连续会分配新空间
7、函数本身存放在哪里？
1. 不存放在栈区
### 数组和指针
8、数值作为参数传递，会优化成指针。
1. 为了高效
2. 传递指针比传递数值本身更高效（浮点数等会大于 4 or 8 byte）
3. 方法中：size arr > 4byte只会当做指针大小了
9、数组指针
```
arr[1] = *(arr + 1) = 1[arr]
arr[4] = *(4 + arr) = 4[arr]
```
10、数组如何退化成指针？
1. 表达式中由c编译器自动退化
2. 函数传递数组也会退化
```
int *p = arr;
void test(int arr[5]){
    // sizeof arr, 数值为4或者8(64位)
    // 正常情况，sizeof aar应该是5
}
```
11、aar为数组时，是数组类型，不是指针类型
12、sizeof(arr+1) = 4 or 8，退化为指针
13、什么情况下数组不会退化为指针？
1. 对数组sizeof aar不会退化
2. 对数组取地址 &aar不会退化
### 指针
14、指针的重要信息
1. 数据对象的首地址
2. 数据对象的存储空间大小（步长）
15、void*会丢失存储空间大小/指针类型丢失/不知道步长 => void*不能取值和加减
16、指针数组？
> 元素1 = arr1的首元素指针
17、static描述的方法里面的变量，可以用指针返回，指向全局区域
```
int * getValue(){
    static int a = 100;
    a++;
    return &a;
}
static只有一份，多次调用后，a = 101,102,103
```
如何释放该数据？
```
r = null
```
## C++
1、ImageView设置Class类为友元类，Class类可以获取到ImageView的私有变量。===> 反射是这么做的吗？不是
## 变音器
fmod
1. C/C++库
2. 游戏、开门、关门，等音效都是fmod做的
## JNI
1、JNI组成区域有哪些？
1. 不分堆区、栈区
2. 只考虑全局引用，局部引用
3. 方法结束后所有局部引用都会释放
```
JNI DeleteLocalRef() // 可以手动释放
```
2、从JNI拷贝数组到Java层
1. 只支持基本数据类型
3、局部引用要手动释放，提高效率
1. DeleteLocalRef，避免jni方法运行时间过长，消耗过多资源
1. kotlin会转为Java再给NDK，多了一个步骤
2. 局部引用不可以在线程和函数之间传递
3. 全局引用可以传递，不会回收，一直占据内存
4、JNI的GC
1. JNI属于JDK
2. 会打上标记，等待GC
### 弱全局引用
1、WeakGlobalRef是什么？
1. 唯一可以在运行时被回收的全局引用
2. 内存不足时可能会回收
3. 长期不用，会打上标记等待回收
2、任何引用释放后还调用，会出错
### 方法调用
1、通过子类对象调用父类方法
```
JNI.invoke(子类对象，父类class，父类方法MethodId)
```
### 内存分配
1、堆区操作
```
//非数组赋值，会启动JavaVM堆区操作，有较强依赖性
jint * arrC = env->GetIntArrayElements(arr, nullptr);
//堆区开辟，必须手动释放
env->ReleaseIntArrayElements(arr, arrC, JNI_OK)
```
2、数组从Java复制到C
```
env->GetIntArrayRegion(javaArr, 0, size, cArr);
```
3、数组从C复制到Java
```
jintArray jniArr = env->NewIntArray(8);
or
jintArray jnitArr = javaArr; // 参数传入的
env->SetIntArrayRegion(jniArr, 0, size, cArr);//cArr->jniArr
```
## Java中增加native方法 ===> 文档中补全
1、定义native方法
```
native int getIntFromJni()
```
2、生成头文件
3、cpp实现native方法
4、加载so库
```
static{
    System.loadLibrary(native-lib)
}
```
## 动态注册
1、静态注册的缺点
1. 性能低，运行时才会去匹配JNI函数
2. 名称绑定，包名+类名
3. JNI函数名太长
## QUESTION
1、局部引用在native方法调用的持续时间内有效，在native方法返回后，自动释放，每个引用会占用一部分nJava虚拟机资源
2、不能分配过多的局部引用
1. 虽然会自动释放，但是会占用大量资源，JVM没办法回收
2. 占据大量native内存，可能会OOM
3、
# Native Hook
1、native hook是什么？
1. 拦截某个native方法的调用，使得执行流程转向自定义的代码中
2、有哪些场景？
1. 内存监控
2. 线程监控
3. GC监控
3、Native Hook的方案有哪些？
1. GOT\PLT hook
2. inline hook
3. trap hook
## GOT\PLT
1、GOT是什么？Global Offset Table 全局偏移表
2、PLT是什么？Procudure Linkage Table 过程链接表
3、ELF是什么？
1. 可重定向文件 a.o
2. 可自行文件 a.out
3. so
4. core dump文件
4、GOT\PLT hook方案思路
1. 修改GOT表中目标函数地址
2. 跳转到我们的函数执行
3. 函数执行完后，执行原函数
### xHook
### bHook
## inline hook
1、inline hook原理
1. 将目标函数的开头几条指令替换为跳转指令，跳转到我们的函数
2. 在hook函数中执行被替换的指令
3. 再跳回原函数执行剩余部分
## trap hook
1、trap hook原理
1. 在目标跳转指令前，增加trap指令，会触发SIGTRAP信号
2. 信号处理函数里面修改PC值
3. 执行我们的函数，执行完成后，修改PC值到原函数
# AI问题
C/C++相关问题：
1. 什么是C语言和C++语言？C语言是一种通用的编程语言，而C++是在C语言基础上发展起来的一种面向对象的编程语言
2. C语言和C++语言之间有什么区别？C语言主要注重过程式编程，而C++在C语言的基础上增加了面向对象编程的特性
3. C++中的面向对象编程和C语言有什么关系？C++中的面向对象编程是通过类、封装、继承和多态等概念实现的，可以看作是对C语言的补充和扩展。
4. C语言和C++语言的内存管理方式有什么区别？C语言使用手动内存管理，而C++引入了构造函数和析构函数的概念，使得资源的申请和释放更加方便和安全。
5. 请解释什么是指针和引用，并描述它们之间的区别。指针是存储变量地址的变量，可以通过指针来操作和访问内存中的数据；而引用是已存在对象的别名，使用引用可以直接访问原始对象，不需要解引用操作。
JNI相关问题：
1. 什么是JNI（Java Native Interface）？是一种编程框架，用于实现Java代码与本地代码（如C/C++）之间的互操作。
2. 在Android中为什么需要使用JNI？Java需要利用强大的C++库，需要具有调用native代码的能力, 此外可以调用底层系统库或者实现高性能的计算。
3. JNI的数据类型与Java的数据类型有何区别？存在对应关系
4. JNI中的JNIEnv是什么，它的作用是什么？JNIEnv是JNI的环境接口，它提供了一组函数用于与Java进行交互，包括获取Java对象、调用Java方法、处理异常等。
Native Hook相关问题：
1. 什么是Native Hook？为什么在Android开发中常用？Native Hook是指在应用程序的底层代码中注入自定义代码，用于修改或扩展应用程序的行为。在Android开发中，常用于实现逆向工程、安全分析和性能优化等。
2. Native Hook的实现原理是什么？GOT\PLT 全局偏移表和过程连接表，原理是修改GOT中函数地址，跳转到xx，在执行。inline hook，修改开头的指令，执行我们的，再执行开头指令，再跳转到源函数执行剩下内容
3. 请解释动态链接库（Shared Library）和静态链接库（Static Library）的区别。SO可以运行时加载，可以多个应用共享。a编译时嵌入，每个应用是独立副本。
4. 有哪些常见的Native Hook技术和工具？Xposed Framework、Frida、Substrate等
5. Native Hook在Android中有什么应用场景？逆向工程、应用程序行为分析、漏洞挖掘和性能优化
