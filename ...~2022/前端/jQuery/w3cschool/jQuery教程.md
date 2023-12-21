1、jQuery库的特性
> 1.HTML元素选取
> 2.HTML元素操作
> 3.CSS操作
> 4.HTML事件函数
> 5.JavaScript特效和动画
> 6.HTML DOM遍历和修改
> 7.AJAX
> 8.Utilities

2、jQuery语法
> 1.基本语法：`$(selector).action()`
> 1. 美元符号：定义jQuery；
> 2. selector：选择符，“查找和查询”HTML元素；
> 3. action：执行对元素的操作；
```
$(this).hide();                  //隐藏当前的HTML元素
$("p").hide();                   //隐藏所有的<p>元素
$(".class").hide();              //隐藏所有class="class"的元素
$("#id").hide();                 //隐藏所有id="id"的元素
```

3、jQuery选择器有哪些
> 1.元素选择器：使用CSS选择器来选取HTML元素；
```
$("p").hide();              //选取所有的<p>元素
$("p.class").hide();        //选取所有class="class"的<p>元素
$("p#id").hide();           //选取所有id="id"的<p>元素
```
> 2.属性选择器：使用XPath表达式来选择带有给定属性的元素；
```
$("[href]");               //选取所有带href属性的元素
$("[href='#']")            //选取所有带href值等于"#"的元素
$("[href!='#']")           //选取所有带href值不等于"#"的元素
$("[href$='.jpg']")        //选取所有href值以".jpg"结尾的元素
```
> 3.CSS选择器：用于改变HTML元素的CSS属性
```
$("p").css("background-color","red");       //将所有p元素的背景颜色改为红色
```

4、jQuery事件
> 1.事件处理程序指的是：当HTML中发生某些事件时所调用的方法，术语由事件"触发"(或"激发")经常被使用。

5、jQuery处理事件遵循的原则
> 1.把所有的jQuery代码置于事件处理函数中；
> 2.把所有事件处理函数置于文档就绪事件处理器中；
> 3.把jQuery代码置于单独的.js文件中；
> 4.如果存在命名冲突，则重命名jQuery库；

6、jQuery命名冲突
> 1.jQuery使用`$`符号作为jQuery的简介方式；
> 2.某些其他的JavaScript库中的函数(比如Prototype)同样使用`$`符号；
> 3.jQuery使用名为noConflict()方法来解决该问题；
> 4.`var jq = jQuery.noConflict()`，帮助使用自己的名称(使用jq来代替`$`符号)；
