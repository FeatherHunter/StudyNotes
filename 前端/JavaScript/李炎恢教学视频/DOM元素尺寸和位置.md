1、获取元素的实际大小
> 1.在<DOM操作表格及样式表>一节中，讲的获取css样式的两种方法(内联style属性和styleSheets的cssRules属性)，得到的是我们自己写的样式的大小(不包括padding和marging的值)；
> 2.clientWidth和clientHeight属性（在BOM一节中讲过另一种用法）
> 1. 没有单位，默认是px；
> 2. 数据类型是number；
> 3. 如果设置了其他的单位，返回的结果会转换为px;
```
<div id="test-css">操作样式表</div>
#test-css {
    width: 200pt;                     //单位会进行转换
    height: 200px;
}
var box = document.getElementById('test-css');
console.log(box.clientWidth);         //266或267
console.log(box.clientHeight);        //200
```
> 4. 增加边框，无变化(border)；
> 5. 增加外边距，无变化(margin)；
> 6. 增加内边距，实际大小会变大(padding)；
> 7. 增加滚动条，实际大小会变小(会空出位置存放滚动条)；
```
#test-css {
    width: 200px;
    height: 200px;
    background: green;
    overflow: scroll;
}
console.log(box.clientWidth);         //183
console.log(box.clientHeight);        //183
```

2、获取滚动内容的元素实际大小
> 1.scrollWidth和scrollHeight属性
> 1. 注意点：在IE7级以下版本中，scrollHeight获取的有效内容的高度，而不是你设置的高度；
```
<div id="test-css">操作样式表</div>
#test-css {
    width: 200px;
    height: 200px;
    background: green;
}
console.log(box.clientWidth);         //200
console.log(box.clientHeight);        //18（表示的是div中文本的高度）
```
> 2.规则：
> 1. 增加边框，无变化(针对IE8以上，IE7以下高度显示内容高度)；
> 2. 增加外边距，无变化(margin)；
> 3. 增加内边距，实际大小会变大(padding)；
> 4. 增加滚动条，实际大小会变小(会空出位置存放滚动条，同上)；
> 5. 增加内容溢出，chrome，Firefox和IE显示的高度都不一样(不理解原因)；

3、获取元素实际大小(边框，内边距和滚动条)
> 1.offsetWidth和offsetHeight属性；
> 2.规则：
> 1. 增加边框，实际大小会变大(border)；
> 2. 增加外边距，无变化(margin)；
> 3. 增加内边距，实际大小会变大(padding)；
> 4. 增加滚动条，无变化(会空出位置存放滚动条，但会计算进去)；
> 5. 增加内容溢出，chrome，Firefox和IE显示的高度都不一样(不理解原因)；
> **注意：如果是内联元素或者是没有设置大小的元素比较麻烦，一般获取块级元素或者行内块元素**
```
console.log(box.offsetWidth);
console.log(box.offsetHeight);
```

4、获取元素周边大小——clientLeft和clientTop
> 1.作用：获取元素设置了左边框和上边框的大小；
```
<div id="test-css">操作样式表</div>
#test-css {
    width: 200px;
    height: 200px;
    border-left:10px solid red;
    border-top:20px solid black;
}
var box = document.getElementById('test-css');
console.log(box.clientLeft);            //10
console.log(box.clientTop);             //20
```

5、获取元素周边大小——offsetLeft和offsetTop
> 1.作用：获取当前元素相对于父元素的位置；
> 2.一般与定位结合使用，否则容易出现浏览器不兼容的问题；
```
<div id="test-css">操作样式表</div>
#test-css {
    width: 200px;
    height: 200px;
    background: green;
    position:absolute;
    top:50px;
    left:50px;
}
console.log(box.offsetLeft);       //50
console.log(box.offsetTop);        //50
```
> 3.加入边框和内边距，不会影响它的位置，加上外边距(margin)会影响位置；
> 4.获取父元素 `offsetParent`属性；
> 1. IE7及以下版本中，获取的父元素为根节点，则显示为HTML标签；
> 2. 其他浏览器及高版本IE，获取的父元素是根节点，则显示BODY标签；
```
console.log(box.offsetParent.tagName);      //BODY
```
```
//多层嵌套，获取子元素到最顶部的距离
function offsetTop(element) {  
    var top = element.offsetTop;         //获取第一层高度
    var parent = element.offsetParent;   //获取父元素
    while (parent !== null) {
        top += parent.offsetTop;
        parent = parent.offsetTop;
    }
}
```

6、获取元素周边大小——scrollTop和scrollLeft
> 1.作用：获取滚动条被隐藏的区域的大小，也可以设置定位到该区域；
```
//判断滚动条是否在最上面，不在则设置到最上面（这里有单位的问题，每个浏览器都不一样）
function scrollStart(element){
    if(element.scrollTop!=0){
        element.scrollTop=0;
    }
}
```
