1、使用DOM操作表格
> 1.通过之前学的知识，操作table表格，非常繁琐，代码冗余，且不清晰；
```html
<table border="1px" width="300">
    <caption>表单1</caption>
    <thead>
    <tr>
        <th>姓名</th>
        <th>性别</th>
        <th>年龄</th>
    </tr>
    </thead>
    <tbody>
    <tr>
        <td>张三</td>
        <td>男</td>
        <td>33</td>
    </tr>
    <tr>
        <td>李四</td>
        <td>男</td>
        <td>31</td>
    </tr>
    </tbody>
    <tfoot>
    <tr>
        <td colspan="3">合计：3</td>
    </tr>
    </tfoot>
</table>
```
```js
//使用DOM创建table
var table = document.createElement('table');
table.setAttribute('width', '300');
table.setAttribute('border', '1');

var caption = document.createElement('caption');
table.appendChild(caption);
var captionText = document.createTextNode('人员表');
caption.appendChild(captionText);

var thead = document.createElement('thead');
table.appendChild(thead);

var tr = document.createElement('tr');
thead.appendChild(tr);

var th1 = document.createElement('th');
tr.appendChild(th1);
var thText1 = document.createTextNode('姓名');
th1.appendChild(thText1);
var th2 = document.createElement('th');
tr.appendChild(th2);
var thText2 = document.createTextNode('性别');
th2.appendChild(thText2);
var th3 = document.createElement('th');
tr.appendChild(th3);
var thText3 = document.createTextNode('年龄');
th3.appendChild(thText3);

document.body.appendChild(table);
```
```js
//使用DOM获取表格数据
var table = document.getElementsByTagName('table')[0];
console.log(table.children[0].innerHTML);    //不清晰

console.log(table.children[2].children[0].children[1].innerHTML);

var tbody = table.getElementsByTagName('tbody')[0];
var tr = tbody.getElementsByTagName('tr')[0];
var td = tr.getElementsByTagName('td')[1];
console.log(td.innerHTML);      //比较清晰，但超烦
```

2、使用HTML DOM来操作表格
|属性或方法|说明|
|---|---|
|caption|保存着<caption>元素的引用|
|tBodies|保存着<tbody>元素的HTMLCollection集合|
|tFoot|保存着<tfoot>元素的引用|
|tHead|保存着<thead>元素的引用|
|rows|保存着<tr>元素的HTMLCollection集合|
|createTHead()|创建<thead>元素，并返回引用|
|createTFoot()|创建<tfoot>元素，并返回引用|
|createCaption()|创建<caption>元素，并返回引用|
|deleteTHead()|删除<thead>元素|
|deleteTFoot()|删除<tfoot>元素|
|deleteCaption()|删除<caption>元素|
|deleteRow(pos)|删除指定行|
|insertRow(pos)|向rows集合中的指定位置插入一行|

> 2.<tbody>中的属性和方法

|属性或方法|说明|
|---|---|
|rows|保存着<tbody>元素中行的HTMLCollection集合|
|deleteRow(pos)|删除指定位置的行|
|insertRow(pos)|向rows集合中的指定位置插入一行，并返回引用|

> 3.<tr>中的属性和方法

|属性或方法|说明|
|---|---|
|cells|保存着<tr>元素中单元格的HTMLCollection集合|
|deleteCell(pos)|删除指定位置的单元格|
|insertCell(pos)|向cells集合的指定位置插入一个单元格，并返回引用|
```js
var table = document.getElementsByTagName('table')[0];
console.log(table.caption.innerHTML);
console.log(table.tHead.innerHTML);
console.log(table.tBodies[0].innerHTML);
console.log(table.rows.length);             //表格的所有行数
console.log(table.tBodies[0].rows.length);  //获取表格主体的行数
console.log(table.tBodies[0].rows[0].cells.length);   //获取主体中第一行的单元格数量
console.log(table.tBodies[0].rows[0].cells[0].innerHTML);    //获取主体中第一行的第一个单元格中的内容
```

3、用HTML DOM来创建一个表格
> 1.<table>，<tbody>，<th>没有特定的方法来创建，需要使用document来创建；
```js
var table = document.createElement('table');
table.setAttribute('width', '300');
table.setAttribute('border', '1');
table.createCaption().innerHTML = '人员表';

var thead = table.createTHead();
var tr = thead.insertRow(0);          //插入一行
tr.insertCell(0).innerHTML = '姓名';   //插入三个单元格
tr.insertCell(1).innerHTML = '性别';
tr.insertCell(2).innerHTML = '年龄';

var tbody = table.createTBody();       //这里创建tbody是有效的(为什么？新增的方法？)
var tb = tbody.insertRow(0);
tb.insertCell(0).innerHTML = '张三';
tb.insertCell(1).innerHTML = '张三';
tb.insertCell(2).innerHTML = '张三';

table.createTFoot();
document.body.appendChild(table);
```

4、DOM操作样式
> 1.CSS作为(XHTML的辅助，用来增强页面的显示效果；
> 2.DOM1实现了最基本的文档处理，DOM2增加了CSS编程访问方式和改变CSS样式信息；
> 3.检测浏览器是否支持DOM1级的CSS能力或DOM2级的CSS能力；
> 1. hasFeature()方法，对于IE浏览器的检测不精确，只能检测HTML版本1.0，返回true，其他均返回false，但IE浏览器还是支持最常用的CSS2模块；
```js
console.log('DOM1：'+document.implementation.hasFeature('CSS','2.0'));     //true
console.log('DOM2：'+document.implementation.hasFeature('CSS2','2.0'));    //true
console.log('DOM2：'+document.implementation.hasFeature('HTML','1.0'));   //true(在IE中，其他浏览器也为true)
```

5、访问CSS样式（只能用于内嵌样式）（可以赋值）
> 1.任何的HTML元素标签都会有一个通用的属性：style。会返回CSSStyleDeclaration对象。
> 1.对于float属性，在IE8及以下的版本中，只能通过`style.styleFloat`访问；就兼容性而言，目前的主流浏览器都支持`style.cssFloat`访问；

|CSS属性|JavaScript调用|
|---|---|
|color|style.color|
|font-size|style.fontSize|
|float|非IE：style.cssFloat|
|float|IE：style.styleFloat|
```
<div id="box" style="color:red;font-size:18px;float:left;border:1px solid green;">css样式</div>


var box = document.getElementById('box');
console.log(box.style.color);                  //red
console.log(box.style.fontSize);               //18px
console.log(box.style.border);                 //1px solid green（符合属性也可以获取）
console.log(box.style.cssFloat);               //left
console.log(box.style.styleFloat);             //left(在chrome和firefox中显示undefined)
```

6、操作样式表
> 1.调用样式表；通过id和class来调用；
> 2.通过id调用，会带来灾难性问题，不建议使用（比较混乱）；
```
#test-css{
    font-size:20px;
}
#pox{
    color:black;
}
<div id="test-css">操作样式表</div>

var box = document.getElementById('test-css');
box.id = 'pox';                                //会应用id为pox的样式
box.style.color='green';                       //又能够通过原来的id修改样式    
```
> 3.解决方法，通过className来设置，会清空原先的；
```
<div id="test-css" class="aaa">操作样式表</div>
var box = document.getElementById('test-css');
box.className = 'pox';    //将原来的aaa替换为pox
```
> 4.在class后附加其他的className值，会保留原先的；
> 1. 判断这个附加的class是否存在，如果有，返回true；没有，返回false；
> 2. 如果附加的是'aa'，而原先存在'aaa'，要判断这个正则的首尾；
> 3. 加上'!!'，表示将字符串转换成布尔值；

```
//检查class是否存在
function hasClass(element, cName) {
    return !!element.className.match(new RegExp('(\\s|^)' + cName + '(\\s|$)'));   //获取原来该标签内的class属性后的所有字符串
}

//添加class
function addClass(element, cName) {
    if(!hasClass(element,cName)){
        element.className += ' ' + cName;
    }
}

//移除一个class
function removeClass(element,cName){
    if(hasClass(element,cName)){
        element.calssName = element.className.replace(new RegExp('(\\s|^)' + cName + '(\\s|$)'),' ');
    }
}
```

7、获取<link>和<style>标签中的css样式表
> 1.无法通过style属性，获取这两个里面的样式；
> 2.方法一：通过`link.sheet`来获取样式表；
> 1. 在IE8级以下版本中，通过`link.styleSheet`来获取；
```
var link = document.getElementsByTagName('link')[0];
var sheet = link.sheet||link.styleSheet;      //为了兼容低版本的IE
alert(sheet);
```
> 3.方法二：通过`document.styleSheets`属性，获取StyleSheetList集合；
```
var sheet = document.styleSheets;    //StyleSheetList
alert(sheet[0]);                     //获取第一个样式表(每个浏览器都有这个属性)
```

8、css样式表的属性
> 1.sheet的属性；

|属性或方法|说明|
|---|---|
|disabled|获取或设置样式表是否被禁用|
|href|如果是<link>包含的，则为样式表的URL，否则，为null|
|media|样式表支持的所有媒体类型的集合|
|title|获取<link>标签中的title属性值|
|type|样式表类型字符串|
|cssRules|样式表包含样式规则的集合，IE8以下不支持(rules)|
|deleteRule(index)|删除cssRules集合中指定位置的规则，IE8以下不支持(removeRule)|
|insertRule(rule,index)|向cssRules集合中指定位置插入rule字符串，IE8以下不支持(addRule)|
```
var sheet = document.styleSheets[0];    //StyleSheetList
console.log(sheet.cssRules[0]);         //获取样式表的第一条规则
```

9、cssRules——样式的规则集合CSSRuleList
> 1.表示整个css样式表，一个css样式称为一个规则CSSStyleRule；
```
#test-css{             //第一个规则CSSStyleRule
    font-size:20px;
}
#pox{                  //第二个规则CSSStyleRule
    color:black;       
}
```
> 2.CSSStyleRule的属性

|属性|说明|
|---|---|
|cssText|获取当前规则的所有的文本，IE不支持|
|selectorText|获取当前规则的选择符文本|
|style|返回CSSStyleDeclaration对象，可以获取和设置样式|
```
#test-css{
    font-size:20px;
    color:green;
}
#pox{
    color:black;
}

var sheet = document.styleSheets[0];    //StyleSheetList
rule1 = sheet.cssRules[0];
console.log(rule1.selectorText);        //#test-css
console.log(rule1.style.color);         //green
```
