**注意点**
**1、父子元素必须都是块元素，才会发生这些现象；**
**2、如果父子元素中只有一个是块元素，不会发生这些现象，会正常显示；**


1、第一个子元素（块元素）margin-top值应用到父元素（块元素）中
> 1.原因：**外边距合并**；
> 1. 外边距合并，指的是，当两个垂直的外边距相遇时，会合并成一个外边距；
> 2. 合并后的外边距的高度等于两个发生合并的外边距中较高的；
> 3. 当一个元素出现在另一个元素上面时，第一个元素的下边距会和第二个元素的上边距合并；
> 4. 当一个元素包含另一个元素时(假如没有内边距或边框把外边距分隔开)，他们的上和/或下外边距也会发生合并。
```html
<div class="g-container">
    <div class="margin">11</div>
</div>
```
```css
.g-container {
    height: 100px;
    width: 100px;
    background: #6f42c1;
}

.margin {
    width: 40px;
    height: 50px;
    border: 1px solid red;
    margin-top: 10px;           //这个值会用到父元素中，所以父元素也会有10px的上外边距
}
```

2、解决方法
> 1.给父元素设置`border`属性；
> 2.给父元素设置`overflow: hidden;`，推荐使用；
> 3.给父元素设置内边距`padding`；
> 4.给父元素或子元素设置`float`；
> 5.给父元素或子元素设置`position:absolute;`；
```css
.g-container {
    height: 100px;
    width: 100px;
    background: #6f42c1;
    /*position: absolute;*/
    /*padding-top: 1px;*/
    /*float:left;*/
    /*border: 1px solid red;*/
    /*overflow: hidden;         //必须加上，否则子元素外边距与父元素外边距重合，会导致子元素的margin-top应用到父元素身上*/
}

.margin {
    width: 40px;
    height: 50px;
    border: 1px solid red;
    /*float:left;*/
    /*position: absolute;*/
    margin-top: 10px;
}
```
