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
> 1.节点分为：元素节点，属性节点和文本节点，都有三个属性；

|节点类型|nodeName|nodeType|nodeValue|
|---|---|---|---|
|元素|元素标签名(tagName属性)|1|null|
|属性|属性名称|2|属性值|
|文本|#text|3|文本内容(不包含html)|

12、层次节点属性
> 1.节点的层次结构可划分为：父节点与子节点，兄弟结点这两种；

|属性|说明|
|---|---|
|childNodes|获取当前元素节点的所有子节点|
|firstChild|获取当前元素节点的第一个子节点|
|lastChild|获取当前元素节点的最后一个子节点|
|ownerDocument|获取该节点的文档根节点，相当于document|
|parentNode|获取当前节点的父节点|
|previousSibling|获取当前节点的前一个同级节点|
|netSibling|获取当前节点的后一个同级节点|
|attributes|获取当前元素节点的所有属性节点集合|

13、childNodes属性
> 1.作用，获取当前元素节点的所有子节点，子节点包括元素子节点和文本子节点；
> 2.文本子节点可以使用nodeValue，输出文本内容（无法使用innerHTML属性获取文本内容）；
> 3.元素子节点可以使用nodeName，输出标签名称；
```
<div id='div' class='class' style="color:red;">测试<strong>div</strong>结尾</div>

var box = document.getElementById('div');
console.log(box.childNodes);               //NodeList(3) [text, strong, text]
console.log(box.childNodes[0]);            //'测试'（文本子节点）
```
```
//小例子，通过判断节点类型来输出值
for(var i = 0; i < box.childNodes.length; i++){
    if(box.childNodes[i].nodeType === 1){
        console.log('元素节点：'+box.childNodes[i].nodeName);
    }else{
        console.log('文本节点：'+box.childNodes[i].nodeValue);
    }
}
//文本节点：测试
//元素节点：STRONG
//文本节点：结尾
```

14、firstChild和lastChild属性
> 1.firstChild：获取当前元素节点的第一个子节点;
> 2.lastChild：获取当前元素节点的最后一个子节点；
```
console.log(box.firstChild.nodeValue);     //测试
console.log(box.lastChild.nodeValue);      //结尾
```

15、attributes属性
> 1.获取当前元素节点的所有属性节点集合；
> 2.返回一个集合数组NamedNodeMap，保存着这个元素节点的属性列表；
```
<div id='div' class='class' style="color:red;">测试<strong>div</strong>结尾</div>
console.log(box.attributes);                //NamedNodeMap
console.log(box.attributes.length);         //3
console.log(box.attributes[0]);             //id='div'
console.log(box.attributes[0].nodeName);    //id(属性名)

console.log(box.attributes['style'].nodeValue);    //color:red;（遍历的时候从后往前）
```

16、空白文本节点
> 1.在IE8及以下的版本中，DOM忽略了空白字符；
> 2.在其他浏览器，和IE9以上的版本中，标准的DOM具有是哦空白文本节点的功能；
```
<div id="test">
    <p>p1</p>
    <p>p2</p>
    <p>p3</p>
</div>

console.log(box1.childNodes.length);    //7(IE8及以下，输出3)
console.log(filterWhiteNode(box1.childNodes).length);   //所有浏览器都输出3
console.log(filterSpaceNode(box1.childNodes).length);

//忽略空白字符
function filterWhiteNode(node){
    var ret = [];
    for(var i = 0; i < node.length; i++){
        if(node[i].nodeType === 3 && /^\s+$/.test(node[i].nodeValue)){
            continue;
        }else{
            ret.push(node[i]);
        }
    }
    return ret;
}

//删除空白节点
function filterSpaceNode(node){
    for(var i = 0; i < node.length; i++){
        if(node[i].nodeType === 3 && /^\s+$/.test(node[i].nodeValue)){
            node[i].parentNode.removeChild(node[i]);
        }
    }
    return node;
}
```
> 3.如果firstChild，lastChild，previousSibling和nextSibling在获取节点的过程中遇到空白节点，解决办法如下:
```
console.log(removeSpaceNode(box1).firstChild.nodeName);   //p

function removeSpaceNode(node){
    for(var i = 0; i < node.childNodes.length; i++){
        if(node.childNodes[i].nodeType === 3 && /^\s+$/.test(node.childNodes[i].nodeValue)){
            node.childNodes[i].parentNode.removeChild(node.childNodes[i]);
        }
    }
    return node;
}
```

17、节点操作
> 1.定义：动态的创建，赋值，插入，删除和替换节点；

|方法|说明|
|---|---|
|write()|把任意字符串插入到文档中|
|createElement()|创建一个元素节点|
|appendChild()|将节点追加到子节点列表的末尾|
|createTextNode()|创建一个文本节点|
|insertBefore()|将新节点插入在前面|
|replaceChild()|将新节点替换旧节点|
|cloneNode()|复制节点|
|removeChild()|移除节点|
```
console.log(document.write('<p>你好</p>'));
```

18、createElement()方法
> 1.创建一个元素节点；
```
document.createElement('p');   //创建了一个p节点，但还未添加到文档中
```

19、appendChild()方法
> 1.将节点追加到某个子节点列表的末尾；
```
<div id="test">
    <p>p1</p>
    <p>p2</p>
    <p>p3</p>
</div>

var box1 = document.getElementById('test');
var p = document.createElement('p');
box1.appendChild(p);
```

20、createTextNode()方法
> 1.创建一个文本节点；
```
var box1 = document.getElementById('test');
var p = document.createElement('p');
box1.appendChild(p);
var text = document.createTextNode('测试DOM方法');
p.appendChild(text);
```

21、insertBefore()方法
> 1.将新节点插入在指定节点的前面；
> 2.两个参数：新节点和指定节点；
```
var box1 = document.getElementById('test');
var p = document.createElement('p');
box1.parentNode.insertBefore(p,box1);     //首先要获取当前节点的父节点，才可以插入
```
```
<span>开头</span>
<div id="test">
    <p>p1</p>
    <p>p2</p>
    <p>p3</p>
</div>
<span>结尾</span>

//将新节点插入到指定节点的后面(在结尾那个span标签之后添加)
function insertAfter(newElement,targetElement){
    var parent = targetElement.parentNode;    //获取指定节点的父节点
    console.log(parent.lastChild.nodeName);
    if(parent.lastChild === targetElement){   //如果指定节点是同级里的最后一个节点(去除空白节点的问题)
        alert('你');
        parent.appendChild(newElement);       //直接使用appendChild方法添加到末尾
    }else{
        parent.insertBefore(newElement,targetElement.nextSibling);
    }
}
```

22、replaceChild()方法
> 1.将新节点替换旧节点；
> 2.两个参数：新节点和旧节点；
```
<span>开头</span>     //第一个span标签在执行该方法后替换为p标签
<span>结尾</span>

var box2 = document.getElementsByTagName('span')[0];
var p = document.createElement('p');
box2.parentNode.replaceChild(p,box2);     //首先获取父节点再进行操作
```

23、cloneNode()方法
> 1.复制节点；
> 2参数：true表示复制标签(包括标签内的内容)；false表示只复制标签(不复制标签内的内容)；
```
var box2 = document.getElementsByTagName('span')[0];
var p = document.createElement('p');
var clone = p.cloneNode(true);
box2.append(clone);
```

24、removeChild()方法
> 1.删除节点；
```
<div id="test">
    <p>p1</p>
    <p>p2</p>
    <p>p3</p>
</div>

var box1 = document.getElementById('test');
box1.removeChild(removeSpaceNode(box1).firstChild);   //删除box1的第一个子节点
box1.parentNode.removeChild(box1);     //删除整个box1节点
```
