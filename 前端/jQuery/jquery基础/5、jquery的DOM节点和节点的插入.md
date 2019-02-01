1、节点的创建
> 1.创建节点(常见的：元素、属性和文本);
> 2.添加节点的一些属性;
> 3.加入到文档中
```
<ul></ul>

$(function(){
    var $li1 = $("<li>php中文网</li>");  //创建第一个li 标签
    var $li2 = $("<li>php.cn</li>");    //创建第二个li 标签

    $("ul").append($li1);              //添加到<ul>节点中，<ul>节点是<li>的父节点
    $($li1).before($li2);
})
```

2、文本节点
> 1.定义：标签中的文字内容;
```
<li>php中文网</li>                  //这里，‘php 中文网’就是文本节点
```

3、属性节点
> 1.定义：整个标签；
```
var $div = $('<div class="shuxing"></div>');     //这是一个属性节点
$('body').append($div);                          //添加到body中
```

4、节点的插入——父子关系的插入（在父元素的后面插入）
|选择器|描述|
|---|---|
|append(content)|向每个匹配的元素内部追加内容|
|appendTo(节点)|把所有匹配的元素追加到另一个、指定的元素集合中|
> **注意：与原生js方法的比较**
>**1、append操作，对应appendChild方法；**
>**2、appendTo操作，对应$(A).append(B)的操作，把A追加到B中；**
```
<div id="append">追加内容</div>

$('#append').append('<em>新内容</em>');         //添加到“追加内容”的后面
$('<em>新内容</em>').appendTo($('#append'));    
```
> 1、append()方法：前面是被插入的对象，后面是要插入的内容；
> 2.appendTo()方法：前面是要插入的内容，后面是被插入的对象；

5、节点的插入——兄弟关系的插入（after()和before(s)）
|选择器|描述|
|---|---|
|after(content)|在匹配元素集合中的每个元素**后面**插入参数所指定的内容，作为兄弟节点|
|before(content)|据参数设定，在匹配元素的**前面**插入内容|
> 1.两个方法都可以接收HTML字符串，DOM元素，元素数组或jQuery对象，用来插入到集合中每个匹配元素的前面或后面；
> 2.两个方法都支持多个参数传递（after(div1,div2...)）;
>**注意：**
>**1、after向元素的后面添加html代码，如果元素后面有元素了，将后面的元素后移，然后将html代码插入；**
>**2、before向元素的前面添加html代码，如果元素前面有元素了，将前面的元素前移，然后将html代码插入；**
```
<div id="append">追加内容</div>

$('#append').before('之前插入<br>','111');
$('#append').before('<em>之前插入</em>','<span>111</span>');    //顺序插入
$('#append').after('<em>之后插入</em>','<span>222</span>');
```

6、节点的插入——父子关系的插入（在父元素的前面插入）
|选择器|描述|
|---|---|
|prepend(content)|向每个匹配的元素内部前置内容|
|prependTo(节点)|把所有匹配的元素前置到另一个指定的元素集合中|
```
<div id="append">追加内容</div>
<div id="prepend">前置内容</div>

$('#prepend').prepend('<em>prepend内容</em>');    //在“前置内容”的前面插入
$('<em>prependTo内容</em>').prependTo($('#prepend'));   

$('#prepend').prependTo($('#append'));           //将整个'prepend'节点插入到'append'节点的最前面
```

7、append、appendTo、prepend、prependTo四个方法的区别
> 1.append()：向每个匹配的元素内部追加内容；
> 2.prepend()：向每个匹配的元素内部前置内容；
> 3.appendTo()：把所有匹配的元素追加到另一个指定元素的集合中；
> 4.prependTo()：把所有匹配的元素前置到另一个指定的元素集合中；

8、节点的插入——兄弟关系的插入（insertAfter()与insertBefore()）
|选择器|描述|
|---|---|
|insertAfter(节点)|内容在前，指定的元素放在后面，在匹配的元素**后面**插入内容|
|insertBefore(节点)|据参数设定，在匹配元素的**前面**插入内容|
>**注意：这两个方法不支持传递多参数；**
```
<div id="append">追加内容</div>

$('<em>insert之前插入</em>','<span>insert111</span>').insertBefore($('#append'));     //第二个参数无用，无法插入进去（不支持）
$('<em>insert之后插入</em>','<span>insert222</span>').insertAfter($('#append'));      //不支持第二个参数
```
