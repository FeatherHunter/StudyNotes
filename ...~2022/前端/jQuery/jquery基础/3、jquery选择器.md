1、简单选择器
|选择器|css模式|jquery模式|描述|
|---|---|---|---|
|元素名|div{}|$('div')|获取所有div元素的DOM对象|
|ID|#box{}|$('#box')|获取一个ID为box的DOM对象|
|类(class)|.box{}|$('.box')|获取所有class为box的DOM对象|

2、基本选择器
|选择器|css模式|jquery模式|描述|
|---|---|---|---|
|群组选择器|span,em,box{}|$('span,em,box')|获取多个选择器的DOM对象|
|后代选择器|ul li a{}|$('ul li a')|获取追溯到多个DOM对象，包括a标签之内的标签|
|通配选择器|*{}|$('*')|获取所元素标签的DOM对象|
```
<ul>
    <li><a href="#"><span>链接1</span></a></li>
    <li><a href="#"></a></li>
    <li><a href="#"><p>这是一个段落</p></a></li>
</ul>

$('ul li a').css('color','green');            //包括span标签和p标签，全部被设置为绿色
```

3、层次选择器
|选择器|描述|
|---|---|
|$('parent > child')|子选择器：选定所有指定‘parent’元素中指定的‘child’的直接子元素以及子元素中的内容（下一层及）|
|$('ancestor descendant')|后代选择器：选择给定的祖先元素的所有后代元素，一个元素的后代可能是该元素的一个孩子，孙子，曾孙等（所有层级的）|
|$('prev + next')|相邻兄弟选择器：选择所有紧接在‘prev’元素后的‘next’元素|
|$('prev ~ siblings')|一般兄弟选择器：匹配‘prev’元素之后的所有兄弟元素。具有相同的父元素，并且过滤‘siblings’选择器|
```子选择器
<div>
    <a href="#">选择器<span>测试1</span></a>
</div>

$('div>a').css('color','blue');      //“选择器”和“测试1”全部变蓝
```
```子选择器与后代选择器的区别
$('div span');           //div标签下的所有的span标签，不管什么层级的（后代选择器）
$('div>span');           //div标签的下一层级的span标签，不是所有的（子选择器）
```
```相邻兄弟选择器
<p>jquery</p>
<p id="p">jquery</p>
<p>jquery</p>

$('#p + p').css('color', 'yellow');      //只有第三个变黄
```
```一般兄弟选择器
<p>jquery</p>
<p>jquery</p>
<p id="p">jquery</p>
<p>jquery</p>
<p>jquery</p>

$('#p ~ p').css('color', 'orange');     //第四个和第五个变橙（过滤到第三个p）
```

4、层次选择器的相同点与不同点
> 1.相同点：都有一个参考节点；
> 2.不同点：
> 1. 后代选择器包含子选择器的内容；
> 2. 一般兄弟选择器包含相邻兄弟选择的内容（一般兄弟选择器和相邻兄弟选择器所选择的的元素，必须在同一个父元素下）；

5、过滤选择器
|选择器|返回|实例|
|---|---|---|
|:first|单个元素|$('div:first')：选取所有div元素中的第一个div元素|
|:last|单个元素|$('div:last')：选取所有div元素中的最后一个div元素|
|:not(selector)|集合元素|$('input:not(.myClass)')：选取类名不是myClass的input元素|
|:even|集合元素|$('input:even')：选取索引是偶数的input元素（从0开始计数）|
|:odd|集合元素|$('input:odd')：选取索引是基数的input元素|
|:eq(index)|单个元素|$('input:eq(1)')：选取索引为1的input元素|
|:gt(index)|集合元素|$('input:gt(1)')：选取索引大于1的input元素|
|:lt(index)|集合元素|$('input:lt(1)')：选取索引小于1的input元素，不包括1|
|:header|集合元素|$(':header')：选取网页中所有别的h1,h2,h3...|
|:animated|集合元素|$('div:animated')：选取正在执行动画的div元素|
```
<p>jquery</p>
<h1>你好1</h1>
<p>jquery</p>
<p id="p">jquery</p>
<p class="pp">jquery</p>
<p>jquery</p>
<h1>你好1</h1>
<h6>你好1</h6>

$('p:first').css('background','red');
$('p:last').css('background','blue');
$('p:not(.pp)').css('background','orange');
$('p:even').css('background','green');
$(':header').css('background','yellow');
```

6、属性选择器
> 1.使用XPath表达式来选择带有给定属性的元素；

|选择器|jquery模式|描述|
|---|---|---|
|[attribute]|`$('[href]')`|获取带有herf属性的元素|
|[attribute=value]|`$('[href='#']')`|获取href值为'#'的元素|
|[attribute!=value]|`$('[href!='#']')`|获取href值不为'#'的元素|
|[attribute$=value]|`$('[href$='.jpg']')`|获取herf属性值包含以'.jpg'结尾的元素|
