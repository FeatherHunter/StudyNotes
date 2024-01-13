

1、Delegates.vetoable的作用
```kotlin
// 返回值决定是否修改为
NewValuevar age by Delegates.vetoable(123){ property, oldValue, newValue 
->    println("property=$property old=$oldValue new=$newValue")

newValue % 2 ==
1}


```

2、Delegates.notNull的作用
```kotlin

var name by Delegates.notNull<String>()

// Kotlin
的空类型安全，要求属性必须初始化，但是可能一开始不知道值，就有三种方案// 方案A
：开始时随意给个默认值（基本数据类型还行，引用类型感觉很奇怪）// 方案B：Delegates.notNull
，适用于基本类型和引用类型。要求：使用前必须先初始化// 方案C：lateinit修饰，只适用于引用类型。要求：使用前必须先初始化

```