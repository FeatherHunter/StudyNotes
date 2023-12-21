1、let命令
> 1.声明变量，用法类似于var；
> 2.作用域：声明的变量仅在let命令所在的代码块有效；
```
var a = [];
for (var i = 0; i < 10; i++) {
    a[i] = function () {
        console.log(i);
    }
}
a[6]();                //6
```
>**注意：可以解决js中的循环存在的问题——闭包**

2、不存在变量提升
> 1.var会出现变量提升，即变量可以在声明之前使用，值为undefined
```
// var 的情况
console.log(foo);        // 输出undefined
var foo = 2;
```
> 2.let命令，一定要声明后使用，否则报错。
```
// let 的情况
console.log(bar); // 报错ReferenceError
let bar = 2;
```

3、暂时性死区(TDZ)
> 1.在代码块内，存在let命令和const命令，这个区块对这些命令声明的变量，从一开始就形成了封闭作用域。凡是在声明之前使用这些变量，就会报错。
> 2.在声明这些变量之前的区域称为死区。
```
var tmp = 123;    //全局变量

if (true) {
  tmp = 'abc';    // ReferenceError，报错（死区）
  let tmp;        //存在let命令
}
```
```
function bar(x = y, y = 2) {     //y还未声明
  return [x, y];
}

bar();                           // 报错   
```
```
function bar(x = 2, y = x) {     //x声明了
  return [x, y];
}
bar();                           // [2, 2]
```

4、不允许重复声明
> 1.let不允许在相同的作用域内，重复声明同一个变量；
```
// 报错
function func() {
  let a = 10;
  var a = 1;
}

// 报错
function func() {
  let a = 10;
  let a = 1;
}
```

5、ES6块级作用域
> 1.外层作用域无法获取内层作用于的变量
> 2.块级作用域的出现，实际上使得获得广泛应用的立即执行函数表达式（IIFE）不再必要了
```
function f1() {
  let n = 5;
  if (true) {
    let n = 10;
  }
  console.log(n); // 5
}
```

6、const命令
> 1.声明一个只读的常量。一旦声明，该值就不可以改变。
> 2.声明常量必须立即初始化，不能留到以后赋值；
```
const PI = 3.1415;
```
