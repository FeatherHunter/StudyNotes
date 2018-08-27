1、什么是DOM
> 1.是文档对象模型，针对HTML和XML文档的API；
> 2.DOM描绘了一个层次化的节点树，运行人员添加、移除和修改页面的某一部分。
> 3.三个字母：
> 1. D（文档）：整个Web加载的网页文档；
> 2. O（对象）：类似window对象之类的东西，可以调用属性和方法，这里指的是document对象；
> 3. M（模型）：网页文档的树型结构；

2、DOM的等级
> 1.有三个等级，分别是DOM1，DOM2，DOM3；
> 2.DOM1在1998年10月份，成为W3C标准；
> 3.DOM1支持的浏览器：IE6+，Firefox，Safari，Chrome和Opera1.7+；
> **注意：IE中的所有DOM对象，是以COM对象实现的，意味着IE中的DOM可能会与其他浏览器有一定的差异**

3、什么是节点
> 1.加载HTML页面时，Web浏览器生成一个树型结构，用来表示页面内部结构；
> 2.DOM将这个树型结构理解为节点。
```
html：head(meta,title)，body(各种标签)
```
> 3.html标签：没有父辈和兄弟，称为根标签(根节点)；
> 4.head标签：是html的子标签(子节点)，meta和title是兄弟关系(兄弟节点)；

4、节点的种类
> 1.分为三类：元素节点，文本节点，属性节点；
> 2.元素节点：其实就是标签，`<div></div>`；
> 3.文本节点：其实就是纯文本，`测试`；
> 4.属性节点：其实就是标签的属性，`class属性`
```
<div class='div'>测试</div>
```

5、查找元素——getElementById()方法
> 1.作用：获取特定ID的元素节点；
> 1.接受一个参数，是id值
> 2.如果找到相应的元素则返回该元素的HTMLDivElement对象；
> 3.如果未找到，则返回null；
> **注意：DOM操作必须等待HTML文档加载完毕，才可以获取，可以将DOM的代码放到后面或者使用延迟脚本属性defer(浏览器不兼容)或者用onload事件来加载HTML文档**
```
<script src="js/dom.js" defer></script>
window.onload = function(){
    alert(document.getElementById('div'));
};

<div id='div' class='class' title='标题' style='color:red;'>测试</div>
alert(document.getElementById('div'));
```
> 4.元素节点属性：
> 1. tagName：获取元素节点的标签名;
> 2. innerHTML：获取元素节点里的内容（包含HTML）（可赋值，包含标签），非W3C标准，兼容性很好；
```
alert(document.getElementById('div').tagName);     //DIV
alert(document.getElementById('div').innerHTML);   //测试
alert(document.getElementById('div').innerHTML='你<strong>好</strong>');   //你好，'好'会加粗
```
> 5.HTML标签属性(可赋值)：
> 1. id：元素节点的id名称；
> 2. title：元素节点的title属性值；
> 3. style：CSS内联样式属性对象；
> 4. className：CSS元素的类名；
```
alert(document.getElementById('div').id);          //id
alert(document.getElementById('div').title);       //标题
alert(document.getElementById('div').style);       //CSSStyleDeclaration对象
alert(document.getElementById('div').className);   //class
```
> 6.自定义属性，只有IE8及以下版本的，可以使用；
```
<div id="id" aaa ='123'>ceshi</div>
console.log(document.getElementById('id').aaa);    //IE8显示123
```

6、查找元素——getElementsByTagName()方法
> 1.作用：获取相同元素的节点列表，返回HTMLCollection(NodeList)数组；
> 2.参数：接受一个参数，是元素标签；
```
<div id='div' class='class' title='标题' style='color:red;'>测试</div>
<div id="id">ceshi</div>

console.log(document.getElementsByTagName('div'));  //获取所有的div标签
console.log(document.getElementsByTagName('div').length);    //2
console.log(document.getElementsByTagName('div').item(0));;  //<div id='div' class='class' title='标题' style='color:red;'>测试</div>，第一个div标签（节点对象HTMLDivElement）
console.log(document.getElementsByTagName('div')[1]);    //<div id="id">ceshi</div>，第二个div标签（节点对象HTMLDivElement）
```
```
//获取body节点
console.log(document.getElementsByTagName('body')[0]);
```
```
console.log(document.getElementsByTagName('*').length);    //获取所有标签
//在IE8及以下的版本中，比其他的浏览器多打印出一条，原因是，IE会把文档申明算进去
```

7、查找元素——getElementsByName()方法
> 1.作用：获取相同名称的节点列表，返回一个对象集合HTMLCollection(NodeList);
```
<div id='div' class='class' name="123">测试</div>
<div id="id" name ='123'>ceshi</div>
<div name="1234"></div>

console.log(document.getElementsByName('123'));  //NodeList(2) [div#div.class, div#id]
console.log(document.getElementsByName('123')[0]);    //<div id="div" class="class" name="123">测试</div>
console.log(document.getElementsByName('123').item(0));    //<div id="div" class="class" name="123">测试</div>
console.log(document.getElementsByName('123').item(0).tagName);   //DIV
```
> 2.input标签的属性：name,value属性等，也都可以获取；
```
//在IE9及以下版本中，对于不合法的标签里的name属性，浏览器无法识别
<div name="1234"></div>
console.log(document.getElementsByName('1234').length);      //0，无法识别div里面的name属性(低版本)

<input type='button' name='test' value='aaa'/>
console.log(document.getElementsByName('test').length);      //1
console.log(document.getElementsByName('test')[0].value);    //aaa
```

8、查找元素——getAttribute()方法
> 1.作用：获取特定元素节点的属性值；
> 2.存在的问题：
> 1. style属性：在IE7及以下的版本中，显示`object`，但是在其他浏览器中，显示style里面的字符串`color:red;`；
> 2. class属性：在IE7及以下的版本中，无法获取类名，显示为`null`；
> 3. className属性：在IE7及以下的版本中，可以获取类名，在其他浏览器及更高的IE版本中，显示为`null`；
> 4. onclick()方法：浏览器不兼容；
```
<div id='div' class='class' aaa="bbb" style="color:red;">测试</div>

var box = document.getElementById('div');
console.log(box.getAttribute('style'));

//跨浏览器获取类名
if(box.getAttribute('className')){
  console.log(box.getAttribute('className'));
}else{
  console.log(box.getAttribute('class'));
}
```
> 3.这个方法与直接调用`.属性`的区别：在于自定义属性上；
> 1. `.属性`的方法：只有IE8及以下版本的，可以使用；
> 2. getAttribute()方法：在任何的浏览器中，都可以使用；
> 3. 解决了自定义属性的兼容性问题；
```
<div id='div' class='class' aaa="bbb" style="color:red;">测试</div>

var box = document.getElementById('div');
console.log(box.getAttribute('aaa'));         //bbb
console.log(box.aaa);                         //undefined
```

9、查找元素——setAttribute()方法
> 1.setAttribute()：设置特定元素节点的属性值
> 2.两个参数：属性名和属性值；
```
box.setAttribute('title','标题');    //创建一个属性(可以创建自定义属性)
```
> 3.存在的问题：style和class属性，onclick()方法(同上)；

10、查找元素——removeAttribute()方法
> 1.removeAttribute()：移除特定元素节点的属性值；
> 2.IE6及更低版本不支持该方法；
```
box.removeAttribute('style');
```

11、node节点属性
> 1.元素节点，属性节点和文本节点，都有三个属性；

|节点类型|nodeName|nodeType|nodeValue|
|---|---|---|---|
|元素|元素标签名(tagName属性)|1|null|
|属性|属性名称|2|属性值|
|文本|#text|3|文本内容(不包含html)|
