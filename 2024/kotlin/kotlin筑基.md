

## split

Kotlin关于""空字符串和split和Java不同，会默认在列表中生成一个空字符串（size=1）

```kotlin
var name = 
"a,b,c,d,e"var list = name.split(",")

println(list.size)
// size=5name = "" // 空字符串分割后还是空字符串，
size=1list = name.split(",")

println(list.size) // size=1

```

## Kotlin ?: 作用探究 

下面两种代码的区别是什么？
```kotlin
// 此种，当node.right为null时，执行run的代码，并且将run代码块返回值作为该行代码的值
    node.right?.let{
        //
    }?: run{
        println("run")
        node.`val` += father
    }
// 该行代码的是，node.right为null时，该行的值为{xxx}这行代码。可以将返回值调用invoke代码
    node.right?.let{
        //
    }?:{
        println("run")
        node.`val` += father
    }
```
