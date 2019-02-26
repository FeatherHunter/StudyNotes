1、开发整站的两种方式
> 1.从头往下依次把每个盒子写完；
> 2.先把网站的布局搞定，然后将每个布局的盒子中的元素补齐（模块化）；

2、整站项目的目录结构
> 1.所有的文件放在一个文件夹中；
> 2.所有的图片放在img文件夹；
> 3.所有的css文件放在css文件夹；
> 4.所有的js文件放在js文件夹；
> 5.整站的入口：index.html；

3、CSS Reset(css初始化)
> 1.不同的浏览器对每一个标签都有默认样式，大部分的浏览的默认样式都有区别，所以，要让所有的默认样式清零。
```yui3
html {
    color: #000;
    background: #FFF
}

body, div, dl, dt, dd, ul, ol, li, h1, h2, h3, h4, h5, h6, pre, code, form, fieldset, legend, input, textarea, p, blockquote, th, td {
    margin: 0;
    padding: 0
}

table {
    border-collapse: collapse;
    border-spacing: 0
}

fieldset, img {
    border: 0
}

address, caption, cite, code, dfn, em, strong, th, var {
    font-style: normal;
    font-weight: normal
}

ol, ul {
    list-style: none
}

caption, th {
    text-align: left
}

h1, h2, h3, h4, h5, h6 {
    font-size: 100%;
    font-weight: normal
}

q:before, q:after {
    content: ''
}

abbr, acronym {
    border: 0;
    font-variant: normal
}

sup {
    vertical-align: text-top
}

sub {
    vertical-align: text-bottom
}

input, textarea, select {
    font-family: inherit;
    font-size: inherit;
    font-weight: inherit;
    *font-size: 100%
}

legend {
    color: #000
}

#yui3-css-stamp.cssreset {
    display: none
}

```

4、精灵图
> 1.其他名称：雪碧图，雪碧技术，css sprite。
> 2.作用：用于将网站上的一些小图片管理到一个大的图片中；
> 3.制作：
> 1. 一定是一些小图片（最好不太会发生变化）
> 2. 精灵图在制作的时候宽度一定要大于最大的那张图片宽度；
> 3. 图片与图片之间要有空隙；
> 4. 在精灵图制作完成之后，一定要将精灵图的下方留出足够的位置，用来方便进行扩展；
