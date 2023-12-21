1、Symbol的作用
> 1.确保每个属性名都是独一无二的；

2、Symbol类型
> 1.本质：数据类型，表示独一无二的值；
> 2.其余数据类型：undefined，null，Boolean，String，Number，Object
> 3.Symbol值是通过`Symbol`函数生成的；
> 1. 意味着，对象的属性名现在有两种类型，一种是原来的字符串，另一种是新增的Symbol类型。
> 2. 凡是属性名属于Symbol类型，都是独一无二的值，可以保证与其他属性名不会冲突；
```
let s = Symbol();
console.log(typeof s);          //symbol
```

3、Symbol()函数
> 1.不能使用`new`命令，否则会报错；
> 2.因为函数生成的是一个数值，不是对象。
> 3.可以接受一个**字符串**作为参数，表示对Symbol实例的描述，主要是为了在控制台显示，或者转化为字符串时区分；
```
//如果不加参数，在控制台输出的都是Symbol()，不利于区分
let s1 = Symbol('s1');
let s2 = Symbol('s2');
console.log(s1);              //Symbol(s1)
console.log(s2);              //Symbol(s2)
```
> 4.如果Symbol的参数是一个对象，会先调用该对象的toString()方法，转化为字符串，然后生成一个Symbol值；
```
const obj={
    toString(){
        return 'abc';
    }
};
const sym = Symbol(obj);
console.log(sym);             //Symbol(abc)
```
5、Symbol函数生成的值都是独一无二的，因此相同参数的Symbol函数的返回值是不相等的；
```
let s3 = Symbol();
let s4 = Symbol();
console.log(s3===s4);             //false
```
> 6.Symbol值不能与其他类型的值进行计算，会报错；
```
let sym1 = Symbol('Niko');
console.log('my name is ' + sym1);
//Uncaught TypeError: Cannot convert a Symbol value to a string
```
> 7.symbol值可以显式转化为字符串，调用String()方法或者调用toString()方法；
```
let sym2 = Symbol('my symbol');
console.log(String(sym2));                  //'Symbol(my symbol)'
console.log(sym2.toString());               //'Symbol(my symbol)'
```
> 8.symbol值可以转化为布尔值；
```
let sym3 = Symbol('my boolean');
console.log(Boolean(sym3));                 //true
console.log(!sym2);                         //false
```

4、实例属性description：Symbol.prototype.description
> 1.作用：获取一个描述；
```
let sym3 = Symbol('my boolean');
console.log(sym3.description);            //'my boolean'
```

5、作为属性名的Symbol
