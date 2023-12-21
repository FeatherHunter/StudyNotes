1、apply和call的用法和区别
> 1.用法: 都能继承另一个对象的方法和属性,区别在于参数列表不一样;
> 2.区别
> 1. Function.apply(obj, args) args是一个数组,作为参数传给Function
> 2. Function.call(obj, arg1, arg2,...)  arg*是参数列表
> 3. apply一个妙用: 可以将一个数组默认的转化为一个参数列表（举个例子: 有一个数组arr要push进一个新的数组中去, 如果用call的话需要把数组中的元素一个个取出来再push, 而用apply只有Array.prototype.push.apply(this, arr)）

2、bind函数的兼容性
> 1.bind()函数会创建一个新函数, 为绑定函数。当调用这个绑定函数时,绑定函数会以创建它时传入bind方法的第一个参数作为this,传入bind方法的第二个以及以后的参数加上绑定函数运行时本身的参数按照顺序作为原函数的参数来调用原函数
> 2.一个绑定函数也能使用new操作符创建对象：这种行为就像把原函数当成构造器。提供的 this 值被忽略，同时调用时的参数被提供给模拟函数;

3、事件代理
> 1.事件委托利用了事件冒泡, 只指定一个事件处理程序, 就可以管理某一类型的所有事件
```html
<ul id="list">
    <li id="li-1">Li 2</li>
    <li id="li-2">Li 3</li>
    <li id="li-3">Li 4</li>
    <li id="li-4">Li 5</li>
    <li id="li-5">Li 6</li>
    <li id="li-6">Li 7</li>
</ul>
```
```js
//js部分
document.getElementById("list").addHandler("click", function(e){
    var e = e || window.event;
    var target = e.target || e.srcElement;
    if(target.nodeName.toUpperCase == "LI"){
        console.log("List item", e,target.id, "was clicked!");
    }
});
```

4、 解释下js中this是怎么工作的?
> 1.this 在 JavaScript 中主要由以下五种使用场景
> 1. 作为函数调用，this 绑定全局对象，浏览器环境全局对象为 window
> 2. 内部函数的 this 也绑定全局对象，应该绑定到其外层函数对应的对象上，这是 JavaScript的缺陷，用that替换
> 3. 作为构造函数使用，this 绑定到新创建的对象
> 4. 作为对象方法使用，this 绑定到该对象
> 5. 使用apply或call调用 this 将会被显式设置为函数调用的第一个参数

5、什么是闭包? 闭包有什么作用?
> 1.闭包是指有权访问另一个函数作用域中的变量的函数. 创建闭包常见方式,就是在一个函数内部创建另一个函数
> 2.作用
> 1. 匿名自执行函数  (function (){ ... })();   创建了一个匿名的函数，并立即执行它，由于外部无法引用它内部的变量，因此在执行完后很快就会被释放，关键是这种机制不会污染全局对象
> 2. 缓存, 可保留函数内部的值
> 3. 实现封装
> 4. 实现模板

6、 undefined和null的区别
> 1.null表示没有对象, 即此处不该有此值. 典型用法
> 1. 作为函数的参数，表示该函数的参数不是对象
> 2. 作为对象原型链的终点
> 3. null可以作为空指针. 只要意在保存对象的值还没有真正保存对象,就应该明确地让该对象保存null值
```
```
> 2.undefined表示缺少值, 即此处应该有值, 但还未定义
> 1. 变量被声明了，但没有赋值时，就等于undefined
> 2. 调用函数时，应该提供的参数没有提供，该参数等于undefined
> 3. 对象没有赋值的属性，该属性的值为undefined
> 4. 函数没有返回值时，默认返回undefined

7、IIFE(立即调用的函数表达式)：function foo(){ }();
> 1.函数定义（语句以function关键字开始）是不能被立即执行的，这无疑会导致语法的错误（SyntaxError）
> 2.当函数定义代码段包裹在括号内，使解析器可以将之识别为函数表达式，然后调用。IIFE:  (function foo(){})()　

8、"attribute" 和 "property" 的区别是什么
> 1.DOM元素的attribute和property两者是不同的东西。attribute翻译为“特性”，property翻译为“属性”
> 2.attribute是一个特性节点，每个DOM元素都有一个对应的attributes属性来存放所有的attribute节点，attributes是一个类数组的容器，说得准确点就是NameNodeMap，不继承于Array.prototype，不能直接调用Array的方法。attributes的每个数字索引以名值对(name=”value”)的形式存放了一个attribute节点。
> 3.property就是一个属性，如果把DOM元素看成是一个普通的Object对象，那么property就是一个以名值对(name=”value”)的形式存放在Object中的属性。要添加和删除property和普通的对象类似。
> 4.很多attribute节点还有一个相对应的property属性，比如上面的div元素的id和class既是attribute，也有对应的property，不管使用哪种方法都可以访问和修改
> 5.总之，attribute节点都是在HTML代码中可见的，而property只是一个普通的名值对属性

9、请指出 document load 和 document ready 两个事件的区别
> 1.JavaScript文档加载完成事件。页面加载完成有两种事件
> 1. 一是ready，表示文档结构已经加载完成（不包含图片等非文字媒体文件）
> 2. 二是onload，指示页面包含图片等文件在内的所有元素都加载完成
```
```
> 2.jQuery中$(function(){/* do something*/});他的作用或者意义就是:在DOM加载完成后就可以可以对DOM进行操作。一般情况先一个页面响应加载的顺序是，域名解析-加载html-加载js和css-加载图片等其他信息

10、什么是use strict? 其好处坏处分别是什么?
> 1.在所有的函数 (或者所有最外层函数) 的开始处加入 "use strict"; 指令启动严格模式
> 2."严格模式"有两种调用方法
> 1. 将"use strict"放在脚本文件的第一行，则整个脚本都将以"严格模式"运行。如果这行语句不在第一行，则无效，整个脚本以"正常模式"运行。如果不同模式的代码文件合并成一个文件，这一点需要特别注意
> 2. 将整个脚本文件放在一个立即执行的匿名函数之中
```
```
> 3.好处
> 1. 消除Javascript语法的一些不合理、不严谨之处，减少一些怪异行为
> 2. 消除代码运行的一些不安全之处，保证代码运行的安全；
> 3. 提高编译器效率，增加运行速度；
> 4. 为未来新版本的Javascript做好铺垫。
```
```
4.坏处
> 1. 同样的代码，在"严格模式"中，可能会有不一样的运行结果；一些在"正常模式"下可以运行的语句，在"严格模式"下将不能运行

11、浏览器端的js包括哪几个部分?
> 1.核心( ECMAScript) , 文档对象模型(DOM), 浏览器对象模型(BOM)

12、DOM包括哪些对象?
> 1.DOM是针对HTML和XML文档的一个API(应用程序编程接口). DOM描绘了一个层次化的节点树, 允许开发人员添加, 移除和修改页面的某一部分;
> 2.常用的DOM方法
```
getElementById(id)
getElementsByTagName()
appendChild(node)
removeChild(node)
replaceChild()
insertChild()
createElement()
createTextNode()
getAttribute()
setAttribute()
```
> 3.常用的DOM属性
```
innerHTML  节点(元素)的文本值
parentNode  节点(元素)的父节点
childNodes
attributes   节点(元素)的属性节点
```

13、js有哪些基本类型?
> 1. Undefined, Null, Boolean, Number, String
> 2.Object是复杂数据类型, 其本质是由一组无序的名值对组成的

14、基本类型与引用类型有什么区别?
> 1.基本类型如上题所示. 引用类型则有: Object, Array, Date, RegExp, Function
> 2.存储
> 1. 基本类型值在内存中占据固定大小的空间,因此被保存在栈内存中
> 2. 引用类型的值是对象, 保存在堆内存中. 包含引用类型的变量实际上包含的并不是对象本身, 而是一个指向改对象的指针s
```
```
> 3.复制
> 1. 从一个变量向另一个变量复制基本类型的值, 会创建这个值的一个副本
> 2. 从一个变量向另一个变量复制引用类型的值, 复制的其实是指针,　因此两个变量最终都指向同一个对象
```
```
> 3.检测类型
> 1. 确定一个值是哪种基本类型可以用typeof操作符
> 2. 而确定一个值是哪种引用类型可以使用instanceof操作符　

15、关于js的垃圾收集例程
> 1.js是一门具有自动垃圾回收机制的编程语言,开发人员不必关心内存分配和回收问题
> 2.离开作用域的值将被自动标记为可以回收, 因此将在垃圾收集期间被删除
> 3."标记清除"是目前主流的垃圾收集算法, 这种算法的思路是给当前不使用的值加上标记, 然后再回收其内存
> 4.另一种垃圾收集算法是"引用计数", 这种算法的思想是跟踪记录所有值被引用的次数. js引擎目前都不再使用这种算法, 但在IE中访问非原生JS对象(如DOM元素)时, 这种算法仍然可能会导致问题
> 5.当代码中存在循环引用现象时, "引用计数" 算法就会导致问题
> 6.解除变量的引用不仅有助于消除循环引用现象, 而且对垃圾收集也有好处. 为了确保有效地回收内存, 应该及时解除不再使用的全局对象, 全局对象属性以及循环引用变量的引用

16、js有几种函数调用方式?
> 1.方法调用模型    var obj = { func : function(){};}    obj.func()
> 2.函数调用模式　　var func = function(){}    func();
> 3.构造器调用模式
> 4.apply/ call调用模式

17、描述事件模型?IE的事件模型是怎样的？事件代理是什么？事件代理中怎么定位实际事件产生的目标？
> 1.捕获->处于目标->冒泡，IE应该是只有冒泡没有捕获
> 2.事件代理就是在父元素上绑定事件来处理，通过event对象的target来定位

18、js动画有哪些实现方法?
> 1.用定时器 setTimeout和setInterval

19、还有什么实现动画的方法
> 1.js动画: 使用定时器
> 2.CSS : transition , animation
> 1. transition 包含4种属性：transition-delaytransition-durationtransition-propertytransition-timing-function，对应动画的4种属性： 延迟、持续时间、对应css属性和缓动函数，
> 2. transform 包含7种属性：animation-nameanimation-durationanimation-timing-functionanimation-delayanimation-directionanimation-iteration-countanimation-fill-modeanimation-play-state，它们可以定义动画名称，持续时间，缓动函数，动画延迟，动画方向，重复次数，填充模式
```
```
> 3.HTML5 动画
> 1. canvas
> 2. svg
> 3. webgl

20、面向对象有哪几个特点?
> 1.封装, 继承, 多态

21、如何判断属性来自自身对象还是原型链?
> 1.hasOwnPrototype

22、图片预加载的实现
> 1.使用jQuery图片预加载插件Lazy Load
> 1. 加载jQuery, 与jquery.lazyload.js
> 2. 设置图片的占位符为data-original, 给图片一个特别的标签,比如class=".lazy"
> 3. 然后延迟加载: $('img.lazy').lazyload();这个函数可以选择一些参数:
```
图片预先加载距离：threshold，通过设置这个值，在图片未出现在可视区域的顶部距离这个值时加载。
事件绑定加载的方式：event
图片限定在某个容器内：container
```
> 2.使用js实现图片加载: 就是new一个图片对象, 绑定onload函数, 赋值url
> 3.用CSS实现图片的预加载
> 1. 写一个CSS样式设置一批背景图片，然后将其隐藏
> 2. 改进: 使用js来推迟预加载时间, 防止与页面其他内容一起加载
```
```
> 4.用Ajax实现预加载：其实就是通过ajax请求请求图片地址. 还可以用这种方式加载css,js文件等

23、如果在同一个元素上绑定了两个click事件, 一个在捕获阶段执行, 一个在冒泡阶段执行. 那么当触发click条件时, 会执行几个事件? 执行顺序是什么?
> 1.绑定在目标元素上的事件是按照绑定的顺序执行的
> 1. 绑定在被点击元素的事件是按照代码顺序发生，
> 2. 其他元素通过冒泡或者捕获“感知”的事件，
> 3. 按照W3C的标准，先发生捕获事件，后发生冒泡事件。所有事件的顺序是：其他元素捕获阶段事件 -> 本元素代码顺序事件 -> 其他元素冒泡阶段事件
```html
<div class="div1" id="one">
    <div class="div2" id="two"></div>
</div>
```
```js
//点击div2，触发的事件顺序是：首先是找到父元素div1的捕获事件，其次是目标元素div2的事件（按照代码顺序执行），最后执行div1的冒泡事件
//捕获 one
//冒泡 two
//捕获 two
//冒泡 one
let div1 = document.getElementById('one');
let div2 = document.getElementById('two');

div2.addEventListener('click', function () {
    console.log('冒泡', 'two');
}, false);
div2.addEventListener('click', function () {
    console.log('捕获', 'two');
}, true);
div1.addEventListener('click', function () {
    console.log('冒泡', 'one');
}, false);
div1.addEventListener('click', function () {
    console.log('捕获', 'one');
}, true);
```

24、js中怎么实现块级作用域?
> 1.使用匿名函数, (立即执行函数)
```
(function(){...})()
```
> 2.es6：块级作用域引入了两种新的声明形式,可以用它们定义一个只存在于某个语句块中的变量或常量。
> 1. let: 语法上非常类似于var, 但定义的变量只存在于当前的语句块中
> 2. const: 和let类似,但声明的是一个只读的常量;
> **注意：使用let代替var可以更容易的定义一个只在某个语句块中存在的局部变量,而不用担心它和函数体中其他部分的同名变量有冲突.在let语句内部用var声明的变量和在let语句外部用var声明的变量没什么差别,它们都拥有函数作用域,而不是块级作用域**

25、构造函数里定义function和使用prototype.func的区别？
> 1.直接调用function，每一个类的实例都会拷贝这个函数，弊端就是浪费内存;
> 2.prototype方式定义的方式，函数不会拷贝到每一个实例中，所有的实例共享prototype中的定义，节省了内存;
> 3.但是如果prototype的属性是对象的话，所有实例也会共享一个对象（这里问的是函数应该不会出现这个情况）,
> 4.如果其中一个实例改变了对象的值，则所有实例的值都会被改变。同理的话，如果使用prototype调用的函数，一旦改变，所有实例的方法都会改变

26、js实现对象的深克隆
> 1.js中数据类型分为基本数据类型(number, string, boolean, null, undefined)和引用类型值(对象, 数组, 函数). 这两类对象在复制克隆的时候是有很大区别的;
> 2.原始类型存储的是对象的实际数据, 而对象类型存储的是对象的引用地址(对象的实际内容单独存放, 为了减少数据开销通常放在内存中)
> 3.此外, 对象的原型也是引用对象, 它把原型的属性和方法放在内存中, 通过原型链的方式来指向这个内存地址.
> 4.于是克隆也会分为两类:
> 1. 浅度克隆: 原始类型为值传递, 对象类型仍为引用传递
> 2. 深度克隆: 所有元素或属性均完全复制, 与原对象完全脱离, 也就是说所有对于新对象的修改都不会反映到原对象中.
