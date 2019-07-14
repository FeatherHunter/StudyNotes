1、Function类型
> 1.函数实际上是对象；
> 2.每个函数都是Function类型的实例，与其他引用类型具有相同的属性和方法；
> 3.函数名实际上是一个指向函数对象的指针，不会与某个函数绑定

2、函数的创建
> 1.使用函数名
>```
>function sum(num1,num2){
>  return num1+num2;
>}
>```
> 2.不使用函数名——变量声明：
> 1. 使用变量sum可以引用函数；
> 2. 函数末尾有一个分号；
>```
>var sum = function(num1, num2){
>  return num1+num2;
>}；
>```
> 3.使用Function构造函数：不推荐使用
> 1. 会解析两次代码，影响性能：
> 2. 第一次是解析常规的ECMAScript代码；
> 3. 第二次解析传入构造函数的字符串；
>```
>var sum = new Function("num1","num2","return num1+num2");
>```

3、函数可以作为值来使用
> 1.函数名本质是变量，因此可以作为值来使用；
> 2.可以将函数作为参数传递给另一个函数；
>```
>funtion callSomeFunction(someFunction, someArgument){   //这个函数是通用的
>  return someFunction(someArgument);
>}
>
>function add(num){
>  return num+10;
>}
>var result = callSomeFunction(add,10);    //20
>```
> 3.从一个函数中返回另一个函数。极为有用
>```
>function createComparisonFunction(propertyName){   //指定对象数组的某个属性排序
>  return function(object1,object2){                //对象数组排序
>    var value1=object1[propertyName];
>    var value2=object2[propertyName];
>    if(value1<value2){
>      return -1;
>    }else if(value1>value2){
>      return 1;
>    }else{
>      return 0;
>    }
>  }
>}
>
>var data = [{name:"Wello",age:10},{name:"John",age:21}];
>data.sort(createComparisonFunction("name"));   //按照name属性进行排序
>alert(data[0].name);                         //John
>```

4、函数内部属性
> 1.arguments：
> 1. 是一个类数组对象，包含传入函数的所有参数；
> 2. 这个对象有一个名叫callee的属性，是一个指针，指向arguments对象的函数；
>```
>function factorial(num){        //阶乘函数，函数名不能改变
>  if(num<=1){
>    return 1;
>  }else{
>    return num*factorial(num-1);
>  }
>}
>```
>```
>function factorial(num){       //修改函数名不会影响代码
>  if(num<=1){
>    return 1;
>  }else{
>    return num*arguments.callee(num-1);
>  }
>}
>```
> 2.this：
> 1. this引用的是函数执行的环境对象；
> 2. 当在网页的全局作用域中调用函数时，this对象引用的是window。
>```
>window.color = "red";
>var o = {color:"blue"};
>function sayColor(){
>  alert(this.color);
>}
>
>sayColor();              //在全局作用域中调用  "red"
>o.say = sayColor;        //将函数赋值给对象o
>o.say();                 //调用函数，this引用的对象是o  "blue"
>```

5、函数的属性有哪些
> 1.length：表示函数希望接收的命名参数的个数
>```
>function sayName(name){}
>function sum(num1,num2){}
>alert(sayName.length);            //1
>alert(sum.length);                //2
>```
> 2.prototype：原型：
> 1. 对于ECMAScript中的引用类型，prototype是保存它们所有实例方法的真正所在；
> 2. 在创建自定义引用类型以及实现继承时，prototype属性极为重要，第六节详细讲解；
> 3. prototype的属性是不可枚举的，因此，使用for-in无法实现。

6、函数的方法
> 1.apply()：接收两个参数：冒充另外一个函数
> 1. 一个是在其中运行函数的作用域(this)；
> 2. 另一个是参数数组，可以是Array实例，也可以是arguments对象。
>```
>function sum(num1,num2){
>  return num1+num2;
>}
>function callSum(num1,num2){
>  return sum.apply(this,arguments);     //this是window对象
>}
>alert(callSum(10,10));                 //20
>```
> 2.call()：接收两个参数：冒充另外一个函数
> 1. 第一个参数同上，this值；
> 2. 其他的参数都直接传递给函数；
>```
>function sum(num1,num2){
>  return num1+num2;
>}
>function callSum(num1,num2){
>  return sum.call(this,num1,num2);     
>}
>alert(callSum(10,10));                 //20
>```
> 3.这两个方法的作用：在特定的作用域中调用函数，等于设置函数体内this对象的值。
> 4.真正的用途：扩充函数运行的作用域；
>```
>window.color = "red";
>var o = {color:"blue"};
>function sayColor(){
>  alert(this.color);
>}
>
>sayColor();              //在全局作用域中调用  "red"
>sayColor.call(this);      //red
>sayColor.call(window);   //red
>sayColor.call(o);         //blue
>```
