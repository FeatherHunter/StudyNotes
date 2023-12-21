1、关于`margin`和`padding`的百分数形式的参照对象
> 1.margin的4个值得参考对象都是父元素的`width`;
> 2.padding的4个值得参考对象都是父元素的`width`;
```html
<div class="g-container">
    <div class="margin"></div>
</div>
```
```css
.g-container {
    height: 100px;
    width: 100px;
    background: #6f42c1;
    overflow: hidden;         //必须加上，否则子元素外边距与父元素外边距重合，会导致子元素的margin-top应用到父元素身上
}

.margin {
    width: 200px;
    height: 200px;
    border: 1px solid red;
    margin-left: 50%;       //相对于父元素的宽，实际为50px
    margin-top: 20%;        //相对于父元素的宽，实际为20px
}
```
```css
.g-container {
    height: 100px;
    width: 100px;
    background: #6f42c1;
}

.padding {
    width: 40px;
    height: 50px;
    border: 1px solid red;
    padding-left: 50%;      //相对于父元素的宽，实际为50px
    padding-top: 20%;       //相对于父元素的宽，实际为20px
}
```
