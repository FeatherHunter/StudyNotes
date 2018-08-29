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

5、访问CSS样式（可以赋值）
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
