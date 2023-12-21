
# Groovy
## Java对比
1、main(args)不需要参数类型
2、可以不需要类，类似kotlin
1. Grovvy脚本写法，自动生成类
## 数据类型
### 强类型
int i = 1
### 弱类型、动态类型
def di = 1
di = "xxx" // 不会报错，允许动态改变类型
## 字符串
和Kotlin一致
### 比较大小
str1 > str2
### 子字符串
str[1..2] ===> Kotlin不允许
### 减字符串
str.minus(subStr)
### 逆序
str.reverse()
### 首字母大写
str.capitalize()
### 字符串是否是数字
str.isNumber()

### multiply
c.multiply(2) 字符重复两遍

## 闭包
匿名内联函数:
===> Kotlin lambda
### 关键变量
this 闭包定义处的对象 innerClosure对象
owner 闭包定义处的外部对象 nestClosure对象
delegate 任意对象，默认是nestClosure指向的。可以修改
#### 委托策略：resolveStrategy
闭包.delegate = xxx
闭包.resolveStrategy = 闭包策略
### call：调用
closure()
closure.call()
#### 内置it
```groovy
def closure = {
    it
    return it // return或者最后一行都可以
}
```
### 闭包：基本数据类型
#### upto
1.upto(n, {num-> result *= num})
1~n,执行闭包{}
#### downto
n.downto(1, {xxxx})
n~1，执行闭包
#### times
5.times{} 
1, 2, 3 ,4 需要小于5
执行{}
### 闭包：字符串
#### each
遍历并且执行
#### find
str.find{
  条件判断
}
符合条件的第一个字符
#### findAll
所有符合条件，返回list
#### any
是否有符合条件的字符
#### every
是否所有字符都符合条件
#### collect
每个字符都执行操作，组成集合
## 数据结构
### List
#### ArrayList
[1,2,3,45]
##### 转为Int数组
[1,2,3,4,5] as int[]
#### 添加
add
<<
list2 = list + 5
#### 删除
remove
removeElement
removeAll
list = list - [2,3] -集合 
#### 查找：find、findAll、any、every、min、max、count
#### 排序：sort

### Map
#### 添加数据
map.yellow = "fff000"
map.map = [key1:1, key2:2] 添加集合对象
#### 遍历
键值对 each key,value->
Entry each teacher
Entry + 索引 eachWithIndex
键值对 + 索引 eachWithIndex key,value->
默认LinkedHashMap
可以创建ConcurrentHashMap
#### 查找 find findAll count
##### 嵌套 findAll+collect
##### groupBy
#### 排序 sort

### Range
清凉list
range = 1..10
range.from to
range[3]
range.contains
#### 遍历 each、i in range、
0..100
0<..<60
## 序列化
### JSON
#### JsonOutput：转为JSON
#### JsonOutpot.prettyPrint 格式化
#### JsonSlurper: 转为对象
### Xml
#### XmlSlurper().parseText(xml)
#### declareNamespace
#### 属性 @‘android:allowBackup’
#### each 遍历
#### 生成 MarkupBuilder(StringWriter).html(){套娃}
## 文件操作
### 遍历 eachLine
### getText 所有文本
### readLines 返回List
### withReader Java流读取
### withWriter Java写入数据
