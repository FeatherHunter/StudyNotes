1、语义化的理解
> 1.去掉或者丢失样式的时候能够让页面呈现出清晰的结构;
> 2.有利于SEO：和搜索引擎建立良好沟通，有助于爬虫抓取更多的有效信息：爬虫依赖于标签来确定上下文和各个关键字的权重；
> 3.方便其他设备解析（如屏幕阅读器、盲人阅读器、移动设备）以意义的方式来渲染网页；
> 4.便于团队开发和维护，语义化更具可读性，是下一步吧网页的重要动向，遵循W3C标准的团队都遵循这个标准，可以减少差异化;

2、Doctype作用? 严格模式与混杂模式如何区分？它们有何意义?
> 1.`<!DOCTYPE>` 声明位于文档中的最前面，处于`<html>`标签之前。告知浏览器以何种模式来渲染文档;
> 2.严格模式的排版和JS运作模式是以该浏览器支持的最高标准运行;
> 3.在混杂模式中，页面以宽松的向后兼容的方式显示。模拟老式浏览器的行为以防止站点无法工作;
> 4.DOCTYPE不存在或格式不正确会导致文档以混杂模式呈现;

3、Doctype文档类型有哪些
> 1.该标签可声明三种 DTD 类型，分别表示严格版本、过渡版本以及基于框架的 HTML 文档;
> 2.HTML 4.01 规定了三种文档类型：Strict、Transitional 以及 Frameset;
> 3.XHTML 1.0 规定了三种 XML 文档类型：Strict、Transitional 以及 Frameset;
> 4.Standards （标准）模式（也就是严格呈现模式）用于呈现遵循最新标准的网页，而 Quirks（包容）模式（也就是松散呈现模式或者兼容模式）用于呈现为传统浏览器而设计的网页


4、HTML与XHTML——二者有什么区别
>1.所有的标记都必须要有一个相应的结束标记
2.所有标签的元素和属性的名字都必须使用小写
3.所有的XML标记都必须合理嵌套
4.所有的属性必须用引号""括起来
5.把所有<和&特殊符号用编码表示
6.给所有属性赋一个值
7.不要在注释内容中使“--”
8.图片必须有说明文字

5、html5有哪些新特性、移除了那些元素？
> 1.* HTML5 现在已经不是 SGML 的子集，主要是关于图像，位置，存储，多任务等功能的增加
>
> 拖拽释放(Drag and drop) API
  语义化更好的内容标签（header,nav,footer,aside,article,section）
  音频、视频API(audio,video)
  画布(Canvas) API
  地理(Geolocation) API
  本地离线存储 localStorage 长期存储数据，浏览器关闭后数据不丢失；
  sessionStorage 的数据在浏览器关闭后自动删除
  表单控件，calendar、date、time、email、url、search  
  新的技术webworker, websocket, Geolocation

> 2.移除的元素

>纯表现的元素：basefont，big，center，font, s，strike，tt，u；
>对可用性产生负面影响的元素：frame，frameset，noframes；

6、如何处理HTML5新标签的浏览器兼容问题？如何区分 HTML 和 HTML5？
> 1.IE8/IE7/IE6支持通过document.createElement方法产生的标签，可以利用这一特性让这些浏览器支持HTML5新标签;
> 2.浏览器支持新标签后，还需要添加标签默认的样式，最好的方式是直接使用成熟的框架、使用最多的是html5shim框架
```
<!--[if lt IE 9]>
<script> src="http://html5shim.googlecode.com/svn/trunk/html5.js"</script>
<![endif]-->
```
> 3.如何区分： DOCTYPE声明，新增的结构元素，功能元素。

7、iframe有那些缺点？
> 1.优点：解决加载缓慢的第三方内容如图标和广告等的加载问题
> 1.
  Security sandbox
  并行加载脚本
> 2.`<iframe>`的缺点：
    iframe会阻塞主页面的Onload事件；
    即时内容为空，加载也需要时间
    没有语意
    解决加载缓慢的第三方内容如图标和广告等的加载问题
Security sandbox
并行加载脚本
