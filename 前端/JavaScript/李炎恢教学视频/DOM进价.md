1、DOM类型的种类
> 1. Node：表示所有类型值得统一接口，IE8及以下不支持；
> 2.Document：表示文档类型；
> 3.Element：表示元素节点类型；
> 4.Text：表示文本节点类型；
> 5.Comment：表示文档中的注释类型；
> 6.CDATASection：表示CDATA区域类型；
> 7.DocumentType：表示文档申明类型；
> 8.DocumentFragment：表示文档片段类型；
> 9.Attr：表示属性节点类型；

2、Node类型
> 1.Node接口是DOM1定义的，
> 2.有12个数值常量表示每个节点的类型值；

|常量|说明|nodeType|
|---|---|---|
|ELEMENT_NODE|元素节点|1|
|ATTRIBUTE_NODE|属性节点|2|
|TEXT_NODE|文本节点|3|
> 3.使用英文常量代替阿拉伯数字，使判断更加清晰；
```
if(box1.nodeType === 1)               //判断元素节点
if(box1.nodeType === Node.ELEMENT_NODE)    //等价

//解决IE低版本不兼容
if (typeof Node === 'undefined') {
    //创建一个全局Node
    window.Node = {
        ELEMENT_NODE:1,
        TEXT_NODE:3,
        ATTRIBUTE_NODE:2
    }
}
```

3、Document类型
> 1.Document类型表示文档，或者根节点；这个节点是隐藏的，没有具体的元素标签；
```
console.log(document.nodeType);                 //9
console.log(document.childNodes[0]);            //DocumentType，文档申明
console.log(document.childNodes[0].nodeType);   //10(在IE8及以下，会将<!理解为注释，显示值为8)
console.log(document.childNodes[0].nodeName);   //html(在火狐低版本显示HTML)
console.log(document.childNodes[1]);            //HTMLHtmlElement
```
> 2.直接获取<html>标签的元素节点HTMLHtmlElment；
```
alert(document.documentElement);              //HTMLHtmlElement     
```
> 3.获取<body>标签的元素节点的方法；
```
document.getElementByTagName('body'[0]);
document.body;
```
> 4.获取文档申明的方法：
> **注意：在IE8及以下的版本中，会显示为null，把文档申明的开头<!当成了注释**
```
document.doctype;
```

4、document属性
> 1.document.title：获取和设置<title>标签的值；
> 2.document.URL：获取URL路径；
> 3.document.domain：获取域名，服务器端；
> 4.document.referrer：获取上一个URL，服务器端；

5、document对象集合
> 1.document.anchors：获取文档中带name属性的<a>元素集合
> 2.document.links：获取文档中带href属性的<a>元素集合
> 3.document.applets：获取文档中<applet>元素集合，已不用
> 4.document.forms：获取文档中<form>元素集合
> 5.document.images：获取文档中<img>元素集合

6、Element类型
> 1.Element类型用于表现HTMl中的元素节点；

|元素名|类型|
|---|---|
|HTML|HTMLHtmlElement|
|DIV|HTMLDivElement|
|BODY|HTMLBodyElement|

7、Text类型
> 1.Text类型用于表现文本节点类型；文本不包含html；
> 2.在同时创建两个同级别的文本节点的时候，会产生分离的两个节点；
> 1. 解决方法：合并成一个节点：normalize()方法
```
<div id="test"></div>

var box1 = document.getElementById('test');
var p1 = document.createTextNode('<测试p1');
var p2 = document.createTextNode('<测试p2');
console.log('未添加'+box1.childNodes.length);      //未合并0，合并后0
box1.appendChild(p1);
box1.appendChild(p2);
//box1.normalize();                               //合并两个节点
console.log('添加后'+box1.childNodes.length);      //未合并2，合并后1
```
> 3.分离一个文本节点中的内容：splitText(num)方法
```
<div id="test">Mr.Lee</div>
console.log(box1.childNodes[0].splitText(3));    //把前三个字符分离成新节点Lee
console.log(box1.childNodes[0].nodeValue);       //Mr.
```
> 4.其他方法：
> 1. deleteData(x,y)：删除从x位置开始的y个字符；
> 2. insertData(x,str)：从x位置添加指定字符串；
> 3. replaceData(x,y,str)：从x位置替换到y个指定的字符,替换为str字符；
> 4. sustringData(x,y)：从x位置获取y个字符，直接输出
```
<div id="test">Mr.Lee</div>

var box1 = document.getElementById('test');
box1.firstChild.deleteData(0,3);             //Lee
box1.firstChild.insertData(0,'Hello ');      //Hello Mr.Lee
box1.firstChild.replaceData(0,3,'Miss.');    //Miss.Lee
alert(box1.firstChild.substringData(0,3));   //Mr.
console.log('其他方法'+box1.firstChild.nodeValue);
```

8、Comment类型
> 1.Comment类型表示文档中的注释；
> 1. nodeType:8
> 2. nodeName:#comment
> 3. nodeValue:注释的内容
```
<div id="test"><!--Mr.Lee--></div>

var box1 = document.getElementById('test');
console.log(box1.firstChild.nodeType);      //8
console.log(box1.firstChild.nodeName);      //#comment
console.log(box1.firstChild.nodeValue);     //Mr.Lee
```

9、Attr类型
> 1.Attr类型用来表示文档元素中的属性；
> 1. nodeType:2
> 2. nodeName:属性名
> 3. nodeValue:属性值

10、DOM扩展——呈现模式
> 1.从IE6开始区分标准模式和混杂模式(怪异模式)，主要看文档申明；
> 2.IE为document对象添加了一个compatMode属性，用来识别模式；
> 3.标准模式，返回CSS1Compat；混杂模式，返回BackCompat。
> 4.应用：设置窗口页面大小（详见BOM.md文章）
```
//判断IE6的模式
function judgeMode() {
    if (document.compatMode === 'CSS1Compat') {
        console.log('标准模式' + document.compatMode);
        console.log('浏览器模式：' + '标准模式');
    } else {
        console.log('混杂模式' + document.compatMode);
        console.log('浏览器模式：' + '混杂模式');
    }
}
```
> 5.在IE8后，引入了documentMode属性，判断三种模式
> 1. 标准模式：8
> 2. 仿真模式：7
> 3. 混杂模式：5
```
function judgeIEMode() {
    if (document.documentMode > 7) {
        console.log('标准模式' + document.documentMode);
    } else if(document.documentMode < 7) {
        console.log('混杂模式' + document.documentMode);
    }else{
        console.log('仿真模式' + document.documentMode);
    }
}
```

11、DOM扩展——滚动scrollIntoView()方法
> 1.设置可见区域——将指定的节点滚动到可见范围内；
```
box1.scrollIntoView();
```

12、DOM扩展——children属性
> 1.因为空白节点的问题，可以使用children属性获取有效的节点，这个属性是非标准的。
```
<div id="test">
    <p>p1</p>
    <p>p2</p>
    <p>p3</p>
</div>

var box1 = document.getElementById('test');
console.log(box1.children.length);           //3(忽略空白节点)
console.log(box1.children[0]);               //<p>p1</>(获取第一个非空白节点)
console.log(box1.childNodes.length);         //7
```

13、DOM扩展——contains()方法
> 1.判断一个节点是不是另一个节点的后代；
> 2.IE9及以上的版本不支持这个方法；
```
console.log(box1.contains(box1.firstChild));     //true
```
> 3.解决：compareDocumentPosition()方法
> 1. 这个方法不支持IE8及以下版本的，其他都支持；
> 2. 这个方法，返回一个掩码值；
> 3. 20这个值，因为满足了4和16两项，最后相加了。
> 4. 10这个值，因为满足了2和8两项，最后相加了。

|掩码|节点关系|
|---|---|
|1|无关(节点不存在)|
|2|居前(节点在参考点之前)|
|4|居后(节点在参考点之后)|
|8|包含(节点是参考点的祖先)|
|16|被包含(节点是参考点的后代)|

```
console.log(box1.compareDocumentPosition(box1.firstChild));     //20
console.log(box1.firstChild.compareDocumentPosition(box1));     //10
//跨浏览器兼容
function refAndChildContains(ref, child) {
    var relation = null;
    if (typeof ref.compareDocumentPosition === 'function') {
        relation = ref.compareDocumentPosition(child);
        if (relation === 20) {
            console.log('compareDocumentPosition是后代节点');
        } else if (relation === 10) {
            console.log('compareDocumentPosition是祖先节点')
        } else if (relation === 1) {
            console.log('compareDocumentPosition是两者无关');
        }
    } else {
        relation = ref.contains(child);
        if (relation) {
            console.log('contains是后代节点');
        } else {
            console.log('contains两者无关');
        }
    }
}
```

14、DOM操作方法（了解）
> 1.innerHTML：获取元素节点里的内容（包含HTML）（可赋值，包含标签），非W3C标准，兼容性很好；
> 1. 限制：作用域元素离开这个作用域就无效了；
```
box1.innerHTML = "<script>alert('Lee');</script>";   
console.log(box1.innerHTML);    //只能纯粹的打印出节点里的内容，而不能执行alert方法
```
> 2.innerText：获取文本内容,如果有html，直接删除内部的标签；赋值，包含标签直接转义；
```
<div id="test">
    <p>p1</p>
    <p>p2</p>
    <p>p3</p>
</div>
var box1 = document.getElementById('test');
console.log(box1.innerText);                  //获取
//p1
//p2
//p3
box1.innerText = '<b>123</b>';               //赋值，<b>123</b>
```
> 3.outerHTML：取值比innerHTML多加一层外面的div，输出整个div，赋值操作会将元素抹去，危险；
> 4.outerText：取值与innerText一样，但火狐不支持，赋值操作会将元素抹去(chrome会)，危险；
```
var box1 = document.getElementById('test');
box1.outerText = 'nihao';
console.log(document.getElementById('test'));    //null

```
