1、jquery元素的属性
> 1.每个元素都有一个或多个特性，用来展示元素的附加信息。
> 2.attr()和removeAttr()方法

2、attr()方法
> 1.作用：获取和设置元素属性；
> 2.attr(传入属性名)：获取属性的值；
> 3.attr(属性名，属性值)：设置属性值；
> 4.attr(属性名.函数值)：设置属性的函数值；
> 5.attr(attributes)：给定元素设置多个属性值（例如：{属性名一:"属性值",属性名二:"属性值",...}）
```
<input type="text" value="php 中文网" id="ipt">

alert($("#ipt").attr('value'));
$("#ipt").attr('value','你好')
```

3、removeAttr()方法
> 1.作用：删除
> 2.removeAttr(attributeName)：为匹配的元素集合中的每个元素移除一项属性；

4、这两个方法的优点
> 1.都是封装的方法;；
> 2.直接调用方法即可；

5、html()方法
> 1.html()方法：获取集合中第一个匹配元素的HTML内容或设置每一个匹配元素的html内容；
> 1. html()：不传入值，就是获取集合中第一个匹配元素的HTML内容；
> 2. html(htmlString)：设置每一个匹配元素的HTML内容；
> 3. html(function(index,oldhtml))：用来返回设置HTML内容的一个函数；
```
<div id="dv">php 中文网</div>

$('#dv').html();
$('#dv').html("www.php.cn");
```

6、追加内容——append(htmlString)
> 1.作用：在指定的位置加入内容(可以加入新的元素节点)
> 2.注意：与指定的元素是父子关系；
```
<div id="append">追加内容</div>

$('#append').append('<em>新内容</em>')                  //追加内容新内容(斜体)
```

7、after和before方法
> 1.after：在所有匹配的元素之后加入新的HTML内容；
> 2.before：在所有匹配元素之前加入新的HTML内容；
> 3.共同点：与指定的元素是兄弟关系
```
<div id="append">追加内容</div>

$('#append').before('<em>之前插入</em>');
$('#append').after('<em>之后插入</em>');
//之前插入(斜体)
//追加内容
//之后插入(斜体)
```

8、text()方法
> 1.作用：
> 1. 得到匹配元素集合中每个元素的文本内容结合，包括他们的后代；
> 2. 设置匹配元素集合中每个元素的文本内容为指定的文本内容；
```
```
> 2.用法
> 1. text()：得到匹配元素集合中每个元素的合并文本，包括他们的后代；
> 2. text(textString)：设置匹配元素内容的文本；
> 3. text(function(index,text))：返回设置文本内容的一个函数；
```
<div id="text">匹配集合</div>

console.log($('#text').text());               //匹配集合

$('#text').text('匹配集合22');
console.log($('#text').text());               //匹配集合22
```

9、val()方法
> 1.作用：用于处理表单元素的值，比如input，select和textarea。
> 2.val()：无参数，获取匹配的元素集合中第一个元素的值（多用来设置表单的字段的值）；
> 3.val(value)：设置匹配集合中每个元素的值；
> 4.val(function)：用来返回设置值得函数；
> **注意：val()在处理select元素时，当没有选项被选中时，它返回null**
```
<select id="see">
    <option>php 中文网</option>
    <option>php.cn</option>
    <option>小猪 CMS</option>
</select>
<p></p>

$("p").text($('#see').val());            //将p标签内的文本内容设置为‘php 中文网’
```

10、html(),text()和val()的差异
>1..html(),.text(),.val()三种方法都是用来读取选定元素的内容；
>2.只不过.html()是用来读取元素的html内容（包括html标签），
>3.text()用来读取元素的纯文本内容，包括其后代元素，
>4.val()是用来读取表单元素的"value"值。
>5.其中html()和text()方法不能使用在表单元素上,
>6.而val()只能使用在表单元素上；
>7.另外html()方法使用在多个元素上时，只读取第一个元素；
>8.val()方法和.html()相同，如果其应用在多个元素上时，只能读取第一个表单元素的"value"值，
>9.但是.text()和他们不一样，如果.text()应用在多个元素上时，将会读取所有选中元素的文本内容。
>10.html(htmlString),text(textString)和val(value)三种方法都是用来替换选中元素的内容，如果三个方法同时运用在多个元素上时，那么将会替换所有选中元素的内容。
>11.html(),text(),val()都可以使用回调函数的返回值来动态的改变多个元素的内容。
