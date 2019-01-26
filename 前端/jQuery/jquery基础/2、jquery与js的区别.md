1、jquery对象与DOM对象
> 1.通过jquery包装后的对象，是一个类数组对象
> 2.这个对象包含了DOM对象的信息，也封装了很多操作方法；
```
<div class="text" id="div"></div>

$(function(){
    var $div = $('#div');            //封装成类数组对象
    $div.html('jquery');             //调用其中的方法
});
```

2、DOM对象转化为jquery对象
> 1.通过`$(dom)`方法——将普通dom对象转化为jquery对象；
> 2.通过ID获得jquery对象——两种方法
> 1. 直接获取对象
>```
><div class="text" id="div"></div>
>
>$(function(){
>    var $div = $('#div');            //封装成类数组对象，直接获取ID
>    $div.html('jquery');             //调用其中的方法
>});
>```
> 2. 通过`$(dom)`方法获取js对象
>```
>$(function(){
>    var div = document.getElementById('div');     //原生js获取dom对象
>    var $div = $(div);                            //dom对象转化为jquery对象
>    $div.html('普通dom转jquery对象');
>});
>```
> 3.通过标签名获取jquery对象——`$(dom)`方法
> 1. 在原生js对象中配置是第几个标签；
>```
><div class="text" id="div"></div><br/>
><div class="text"></div><br/>
>
>var div = document.getElementsByTagName('div')[1];    //设置为第二个div
>var $div = $(div);
>$div.html('在原生js中设置第二个div中的内容');
>```
> 2. 在jquery对象中配置是第几个标签；
>```
>var div = document.getElementsByTagName('div');
>var $first = $(div).first();                          //获取第一个div
>$first.html('在jquery中设置第一个div中的内容');
>```

3、jquery对象转化为DOM对象
> 1.jquery对象本身类似于数组对象，通过`[index]`方式来获取对应的DOM对象；
```
<div class="text" id="div"></div>

var $div = $('#div');      //获取id为div的对象
var div = $div[0];
div.style.width = '50px';
```
> 2.通过`get(index)`方法来得到相应的DOM对象；
```
<div class="text" id="div"></div>
<br/>
<div class="text"></div>
<br/>

var $div = $('div');         //获取元素名为div的所有对象
var div = $div.get(1);       //得到第二个元素名为div的DOM对象
div.style.height = '50px';
```
