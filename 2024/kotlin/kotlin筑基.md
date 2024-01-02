

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