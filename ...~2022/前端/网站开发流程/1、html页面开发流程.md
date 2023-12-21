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

5、IconFont
> 1.定义：指用字体文件代替图片文件，来展示图标，特殊字体等元素；
> 2.优点:
> 1. 加载文件体积小；
> 2. 可以直接通过css的font-size，和color修改它的大小和颜色，对于需要做多个尺寸的图标，是个很好的方法；
> 3. 支持一些css3对文字的效果，如：阴影、旋转、透明度。
> 4. 兼容低版本浏览器；
```
```
> 3.缺点：
> 1. 矢量图只能是纯色的；
> 2. 制作门槛高，耗时长，维护成本很高。

6、生成IconFont字体文件
> 1.设计矢量图，然后通过工具直接转换为相应的字体；
> 2.使用第三方IconFont在线服务（阿里巴巴IconFont平台），直接上传设计的图标矢量图生成字体文件；

7、使用IconFont字体文件
> 1.font-face字体声明：不同的浏览器调用不同的字体文件；
```
@font-face {font-family: "iconfont";
  src: url('iconfont.eot'); /* IE9*/
  src: url('iconfont.eot?#iefix') format('embedded-opentype'), /* IE6-IE8 */
  url('iconfont.woff') format('woff'), /* chrome, firefox */
  url('iconfont.ttf') format('truetype'), /* chrome, firefox, opera, Safari, Android, iOS 4.2+*/
  url('iconfont.svg#iconfont') format('svg'); /* iOS 4.1- */
}
```
> 2.定义IconFont样式
```
.iconfont {
   font-family:"iconfont" !important;
   font-size:16px;
}
```
> 3.挑选图标对应的字体编码，应用于页面中
```
<i class="icon iconfont">&#xe60e;</i>
```

8、IconFont存在的问题(实践)
> 1.字体图标在一些浏览器下会遇到被加粗的问题，设置-webkit-font-smoothing: antialiased; -moz-osx-font-smoothing: grayscale;解决。
> 2.跨域访问不到字体，由于怕字体版权得不到保护，默认跨域的字体文件是访问不到的，一般通过服务器设置 Access-Control-Allow-Origin指定自己需要的网站和设置同域来解决这个问题。
> 3.不要包含没有使用的@font-face，IE将不分他是否使用，统统加载下来。万恶的IE。
> 4.@font-face声明之前，如果有script标签的话，直到字体文件完成下载之前，IE将都不会渲染任何东西。

IconFont三种使用方式，推荐第二种：https://blog.csdn.net/qq_39176732/article/details/81390423
