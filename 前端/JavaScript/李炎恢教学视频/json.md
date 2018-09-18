1、json
> 1.是一种结构化的数据表示方式。

2、json语法
> 1.简单值：可以在json中表示字符串、数值、布尔值和null。不支持特殊值undefined
```
10
"hello"
true
null
```
> 2.对象；
> **注意：字符串使用双引号，避免解析错误**
```
'{"name":"Lee","age":100}'
```
> 3.数组；
```
'[100,"Lee",true]'
```

3、常用的json结构
> 1.数组加对象的结构
```
[
  {
    "title":"a",
    "age":1
  },
  {
    "title":"b",
    "age":2
  }
]
```
> 2.一般将json数据结构保存在文本文件中，通过XMLHttpRequest对象去加载它，得到这个结构数据字符串。

4、JSON字符串的解析
> 1.早期，使用eval()函数，这种方法不安全，可能会执行恶意代码；
```
var Json = '[{"title":"a","age":1},{"title":"b","age":2}]';
var box = eval(Json);
console.log(box);                  //数组
```
> 2.ECMAScript5对解析进行了规范，定义了全局变量JSON；
> 1. JSON对象提供了两个方法；
> 2. stringify()：将原生JavaScript值转换为JSON字符串。提供了两个参数，第一个参数可以是数组或函数，第二个参数表示在JSON字符串中保留缩进
```
//第二个参数为函数
var box1 = [{title: 'a', age: 1, height: 177}, {title: 'b', age: 2, height: 180}];
var test = JSON.stringify(box1, function (key, value) {
    if (key === 'title') {
        return 'Mr.' + value;
    } else {
        return value;
    }
});
console.log('test: ' + test);     //[{"title":"Mr.a","age":1,"height":177},{"title":"Mr.b","age":2,"height":180}]

//接受第三个参数
console.log(JSON.stringify(box1, ['title', 'age'], 4));   //只需要'title', 'age'的值
console.log(JSON.stringify(box1, null, 4));               //全部打印出来，用null表示
```
> 3. 自定义ToJSON()方法，过滤一些数据：
```
var box2 = [{title: 'a', age: 1, height: 177,toJSON:function(){return this.title;}}, {title: 'b', age: 2, height: 180,toJSON:function(){return this.title;}}];
console.log(JSON.stringify(box2, null, 0));             //["a","b"]
```
> **注意：执行顺序，首先执行ToJSON()方法，如果第二个参数应用了函数，则执行这个方法，最后执行序列化过程**
> 4. parse()：将JSON字符串转换为JavaScript原生值。接受两个参数，同上；
```
var Json = '[{"title":"a","age":1},{"title":"b","age":2}]';
console.log(JSON.parse(Json));         //转换为数组

var box1 = [{title:'a',age:1},{title:'b',age:2}];
console.log(JSON.stringify(box1));           //[{"title":"a","age":1},{"title":"b","age":2}]
```
