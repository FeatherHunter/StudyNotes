1、border制作倒三角
> 1.将宽和高都设置为0，四个边框都变成了三角形；
> 2.将左右和下边框颜色设置为透明或与背景颜色相同的颜色，推荐透明；
> **注：两种写法：**
```
border-width:3px;
border-style:solid dashed dashed;
border-color:#333 transparent transparent;
```
```
border: 3px dashed transparent;
border-top: solid #333;
```
```html
<div class="test">
    <ul>
        <li><a href="#">建材网首页</a></li>
        <li><a href="#">我的商务室<i></i></a></li>
        <li><a href="#">我的收藏<i></i></a></li>
        <li><a href="#">建材服务<i></i></a></li>
        <li><a href="#">客服中心</a></li>
        <li><a href="#">网站导航<i></i></a></li>
    </ul>
</div>
```
```css
.test {
    height: 20px;
    border: 1px solid red;
}

.test ul li {
    float: left;
    padding-right: 10px;
    line-height: 20px;
}

.test ul li a i {
    height: 0;
    width: 0;
    /*制作倒三角*/
    border: 3px dashed transparent;
    border-top: solid #333;
    /* 行内块元素*/
    display: inline-block;
}
```

制作倒三角详解：https://blog.csdn.net/hl_java/article/details/70148328
