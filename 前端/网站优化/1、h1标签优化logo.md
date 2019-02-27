1、网站的logo使用h1标签进行优化
> 1.通常，网站的logo是一张图片，使用div来进行包裹；
> 2.但是，为了加重logo的权重，使用h1标签来包裹，并添加隐藏的文字说明，以便搜索引擎能够识别；
```html
<div class="logo">
    <h1 class="logo-l fl"><a href="#">梅兰商贸</a></h1>
    <div class="logo-r fr"></div>
</div>
```
```
.fl {
    float: left;
}

.fr {
    float: right;
}

.logo {
    width: 970px;
    height: 90px;
    margin: 0 auto;
}
.logo-l {
    width: 135px;
    height: 45px;
    margin: 20px 0 0 20px;
}

.logo-l a {
    display: inline-block;
    width: 100%;
    height: 100%;
    background: url("../img/logo.png") no-repeat;
    /*首行缩进，隐藏文字*/
    text-indent: -9999em;
}

.logo-r {
    width: 530px;
    height: 42px;
}
```
