
# Kotlin筑基

[TOC]

1、const val PI = 45 编译时常量不可以用于局部变量,为什么？
>函数之内必须在运行时赋值，不符合编译时常量
2、编译时常量（compile-time constants）有什么用？
1. 提高性能：编译时进行常量折叠（constant folding），避免在运行时进行重复计算。
2. 减少错误和提高可读性：减少人为错误的可能性，会在编译时验证。可以提高代码的可读性和可维护性。
3. 编译时配置：定义应用程序的版本号、构建类型或其他配置参数，从而在编译期间对应用程序进行不同的处理。
4. 优化资源使用：可以在代码中指定资源的路径，避免在运行时进行资源查找和加载的开销。

3、Kotlin基本类型也是引用类型，会不会耗费性能？不会，都转为基本类型了

4、range表达式
n in 0..10
n !in 0..10

5、函数默认public，其他修饰符
1. private、protected
1. internal：同一module可见
1. protected internal：同一模块或者子类可见
1. internal可以提高代码封装性

6、默认返回Unit可以不写，kt中Unit是单例类

7、Nothing类型是什么？
1. TODO("抛出异常，结束当前程序")，这不是注释
1. TODO()返回的类型是Nothing

8、反引号是什么？
```
private fun `登录功能20230727环境测试功能`(name:String){
println(name)
}
```

9、kt中in和is是关键字，想要函数名为in，is怎么办？反引号
```
fun `in`(){}
`in`() // 调用
```

10、反引号可以用于函数加密,公司内部有文档
```
private fun `9867693746293234`(name:String){
// xxx
}
```
