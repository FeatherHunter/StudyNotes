1、children()方法——父子关系
> 1.作用：查找集合里的第一级子元素；
> 2.children()：返回匹配元素集合中每个元素的所有子元素(仅儿子辈)；
```
<div>
    <ul>
        <li>children方法11</li>
        <li>children方法22</li>
    </ul>
    <p>children段落</p>
</div>

$('div').children().css('color','red');    //所有的都变红（第一级子元素有效）
```
> 3.children(selector)：选择性的接受同一类型选择器表达式；
```
$('div').children(':first').css('color','blue');    //只有ul标签内的元素变蓝
```

2、find()方法——后代关系
> 1.作用：查找DOM树中的元素的所有后代元素；
> 2.find遍历当前元素集合中每个元素的后代，只要符合，不管是儿子，还是孙子等，都可以。
```
<div>
    <ul>
        <li>children<span>方法11</span></li>
        <li>children方法22</li>
    </ul>
</div>

$('div').find('span').css('color','orange');
```

3、parent()方法——父子关系
> 1.作用：查找集合里面的每一个元素的父元素，有序的向上匹配元素，并根据匹配的元素创建一个新的jQuery对象；
> 2.选择性的接受同一类型选择器表达式;
```
<div><p>Hello</p></div>
<div class="selected"><p>Hello Again</p></div>

$('p').parent('.selected').css('color','gray');     //只有第二个变灰，选择类名符合的父元素，设置样式
```

4、parents()方法——祖先关系
> 1.作用：查找合集里面每个元素的所有祖先元素，有序的向上匹配，返回的元素顺序是从离他们最近的父级元素开始的；
>**注意：**
>**1.jQuery是一个合集对象，通过parents匹配的是合集中所有元素的祖先元素；**
>**2.parents()方法，选择性的接受同一类型选择器表达式，进行筛选。**
```
<body>
<div>div
    <ul>ul
        <li>children<span>方法11</span></li>
        <li>children方法22</li>
    </ul>
    <p>children段落</p>
</div>
</body>

$('li').parents().css('color','pink');      //html中的所有字体颜色，均被设置；
```

5、parent()和parents()比较
> 1.前者只是进行了单级的DOM树查找；
> 2.`$('html').parent();`返回一个包含document的集合；
> 3.`$('html').parents();`返回空集合；


6、closest()方法
> 1.作用：从元素本身开始，在DOM树上逐级向上级元素匹配，并返回最先匹配的祖先元素；
> 2.接受一个匹配元素的选择器字符串；


7、parents()和closest()比较
> 1.起始位置不同：parents开始于父元素，closest开始于当前元素；
> 2.遍历目标不同：closest要找到指定的目标，找到一个匹配的目标就停止查找；parents遍历到文档根元素，将匹配的元素加入集合中；
> 3.结果不同：closest返回包含零个或一个元素的jquery对象；parents返回包含零个或一个或多个的jquery对象；

8、next()方法——兄弟关系
> 1.作用：查找指定元素集合中每一个元素紧邻的后面同辈元素的元素集合；
> 2.选择性的接受同一类型选择器表达式；
```
<ul>ul
    <li>children<span>方法11</span></li>
    <li>children方法22</li>
    <li>children方法33</li>
</ul>

$('li').next().css('background','red');             //第二个变红
$('li').next(':last').css('background','green');    //第三个变绿
```

9、prev()方法——兄弟关系
> 1.作用：查找指定集合中每一个元素紧邻的前面同辈元素的元素集合；
> 2.选择性的接受同一类型选择器表达式；
```
<div>div
    <ul>ul
        <li>children<span>方法11</span></li>
        <li>children方法22</li>
        <li class="item">children方法33</li>
    </ul>
    <p>children段落</p>
</div>
<div>div2
    <ul>ul2
        <li>children<span>方法1111</span></li>
        <li>children方法2222</li>
        <li class="item">children方法3333</li>
    </ul>
    <p>children段落11</p>
</div>

$('.item').prev().css('color','red');              //两个全部变红
$('.item').prev(':first').css('color','blue');     //只有第一个变蓝
```

10、siblings()方法——兄弟关系
> 1.作用：查找指定元素集合中每一个元素的所有同辈元素
> 2.选择性的接受同一类型选择器表达式；
```
<div>div
    <ul>ul
        <li>children<span>方法11</span></li>
        <li>children方法22</li>
        <li class="item">children方法33</li>
        <li>children方法44</li>
    </ul>
</div>
<div>div2
    <ul>ul2
        <li>children<span>方法1111</span></li>
        <li>children方法2222</li>
        <li class="item">children方法3333</li>
        <li>children方法4444</li>
    </ul>
</div>

$('.item').siblings().css('color','red');             //所有的变红
$('.item').siblings(':first').css('color','blue');    //只有第一个变蓝
```

11、add()方法
> 1.作用：向合集中添加新的元素；
> 2.接受参数：jquery选择器表达式，DOM元素，或HTML片段；
```
<div>传递元素标签
    <ul>ul
        <li>children<span>方法11</span></li>
        <li>children方法22</li>
        <li class="item">children方法33</li>
        <li>children方法44</li>
    </ul>
    <p>children段落</p>
</div>

$('li').add('p').css('color','red');   //将p元素加入到li元素合集中，全部设置css样式（li元素和p元素的字体颜色都变红）
```
```
<div>传递html结构
    <ul>ul
        <li>children<span>方法11</span></li>
        <li>children方法22</li>
        <li class="item">children方法33</li>
        <li>children方法44</li>
    </ul>
    <p>children段落</p>
</div>
<div id="li2"></div>

$('li').add('<p>新元素</p>').appendTo($('#li2'));     //在id为li2中插入一个HTML结构（在li合集中插入了p元素，原来位置的会消失）
```

12、each()方法
> 1.作用：类似for循环迭代器，迭代jquery对象合集中的每一个DOM元素；
> 2.通过回调的方式处理，有两个固定的实参，索引与元素；
> 3.回调方法中的this指向当前迭代的dom元素。
> 4.如果提前退出，返回false在回调函数内终止循环；
```
$("li").each(function(index, element) {
    index 索引 0,1
    element是对应的li节点 li,li
    this 指向的是li
})
```
```
<div>
    <ul>
        <li>children<span>方法11</span></li>
        <li>children方法22</li>
        <li>children方法33</li>
        <li>children方法44</li>
    </ul>
</div>
<div>
    <ul>
        <li>children<span>方法11</span></li>
        <li>children方法22</li>
        <li>children方法33</li>
        <li>children方法44</li>
    </ul>
</div>

$('li').each(function(index,element){
   $(this).css('color','green');         //给所有的li元素设置样式
});

$('li').each(function (index, element) {
    if (index % 2) {
        $(this).css('color', 'blue');    //只有下标为基数的li元素设置样式
    }
});
```
