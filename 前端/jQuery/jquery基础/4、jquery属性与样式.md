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

5、读取、修改元素的html结构或元素的文本内容
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
> 1.作用：得到匹配元素集合中
