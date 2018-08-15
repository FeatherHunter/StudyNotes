1、`<script>`元素中的各个属性及其作用
> 1.async:可选。
> 1. 表示异步执行，应立即下载脚本，而不妨碍页面中的其他操作；
> 2. 只对外部脚本文件有效（只有在使用src属性时才能使用）。
>```
><script type="text/javascript" src="demo.js" async="async"></script>   //异步执行
>```
> 2.charset：可选。表示通过src属性指定的代码的字符集。
>```
><script type="text/javascript" src="demo.js" charset="UTF-8"></script>    //定义外部脚本文件中所使用的字符编码
>```
> 3.defer：可选。表示脚本可以延迟到文档完全被解析和显示之后执行（只对外部脚本文件有效）
> 4.language：已废弃。原来用于表示编写代码使用的脚本语言。
> 5.src：可选。表示包含要执行代码的外部文件。
> 6.type：可选，必需？。
> 1. 可以看成是language的替代属性：表示编写代码使用的脚本语言的内容类型（也成为MIME类型）；
> 2. 设置type为"text/javascript"（约定俗成和兼容性），实际上，服务器传的是application/x-javascript。在type中设置会导致脚本被忽略。

2、使用`<script>`元素的方法
> 1.直接在页面中嵌入JavaScript
> 1. 指定type属性；
> 2. `<script>`是结束标签，如果要表示"</script>"字符串，可以写成`<\/script>`或`'</scr'+'ipt>'`；
>```
><script type="text/javascipt">
>function sayScript()
>{
>  alert("<\/script>");
>  alert('</scr'+'ipt>');    //本质是将两个字符串组合起来
>}
>```
> 2.包含外部JavaScript文件，src属性是必须的（此时，不存在"</script>"字符串表示的问题，可以直接在外部文件中使用`alert("</script>")`）。
> > 1. src属性可以包含本地的js文件；
> > 2. src属性也可以包含某个域中的URL；
>```
><script type="text/javascript" src="http://www.somewhere.com/afile.js></script>
>```
> **注意：**
> 带有src属性的`<script>`元素，不应该嵌入额外的JavaScript代码，如包含了，只会执行外部脚本文件，嵌入的会忽略。

3、<script>标签的位置
> 1. 按照惯性，所有的`<script>`元素都应该放在页面的<head>元素中。
>- 页面的内容会等待js文件执行完成之后才加载出来；
>- 页面会出现空白，解决方法是将js引用放在`<body>`元素中页面的后面。
>```
><!doctype html>
><html>
>  <head>
>  @*<script type="text/javascript" src="afile.js></script>*@    //注释
>  @*<script type="text/javascript" src="bfile.js></script>*@
>  </head>
>  <body>
>    <!-- 这里放内容 -->
>    <script type="text/javascript" src="afile.js></script>
>    <script type="text/javascript" src="bfile.js></script>
>  </body>
></html>
>```

4、延迟脚本：defer属性
> 1.defer属性：脚本会延迟到整个页面都解析完成后再执行(先下载，延迟执行，按照原先的顺序执行)。
>- 浏览器不适配，有的浏览器会忽略这个属性，因此，建议js引用放在页面底部。

5、异步脚本
> 1.async属性：立即下载文件；
> 2.不保证按照原先的顺序执行，要确定文件互不依赖。
> 3.目的：不让页面等待脚本下载和执行，异步加载其他内容。
> 4.执行顺序：在页面load事件前，可能在DOMContentLoaded事件触发之后。
> 5.支持异步的浏览器：firefox3.6，Safari5和Chrome。
>```
><script type="text/javascript" async src="demo1.js"></script>
>```

6、在XHTML中的用法(XHTML：可扩展超文本标记语言)
> 1.有特殊的规则确定`<script>`元素中哪些内容可以被解析；
> 例如：比较运算符"<"会在XHTML中当做一个新的标签(标签后跟空格会出现语法错误)，
> 解决方法：
> 1. 用`&lt`来代替；
> 2. 用CData片段来包含JavaScript代码；
>```
><script type="text/javascript"><![CData[    //浏览器不兼容XHTML，注释掉CData片段
>  function compare(a, b){
>    if(a>b)
>    {
>      alert("A is more than B");
>    }
>  }
>]]></script>
>```

7、嵌入外部js文件的优点：
> 1.可维护性。能够在不触及HTML标记的情况下编辑JavaScript代码；
> 2.可缓存。浏览器能够根据具体的设置缓存链接的所有外部js文件（两个页面使用同一个文件，该文件只需下载一次）
> 3.适应未来。无需使用XHTML或注释hack。HTML和XHTML包含外部文件的语法是相同的。

8、文档模式
> 1.最初两种模式：混杂模式(quirks mode)和标准模式(standards mode)，之后，IE提出了准标准模式(almost standards mode)。
**注：最初的两种模式主要影响CSS内容的呈现，准标准模式主要体现在处理图片间隙的时候。**
> 2.文档开头没有发现任何文档类型的声明，浏览器会默认开启混杂模式；
> 3.开启标准模式：
>```
><!DOCTYPE html>    //HTML5
>```
> 4.准标准模式，可以通过过渡型(transitional)和框架型(frameset)文档类型来触发；
>```
><!DOCTYPE HTML PUBLIC       //html 4.01过渡型
>"-//W3C//DTD HTML 4.01 Transitional//EN"
>"http://www.w3.org/TR/html4/loose.dtd">
><!DOCTYPE HTML PUBLIC       //html 4.01框架集型
>"-//W3C//DTD HTML 4.01 Frameset//EN"
>"http://www.w3.org/TR/html4/frameset.dtd">
>```

9、<noscript>元素
> 1.作用：当浏览器不支持JavaScript时，显示其替代的内容。
> 2.作用域：可以包含能够出现在<body>中的任何元素（除了`<script>`元素）
>```
><body>
>  <noscript>
>    <p>本页面需要浏览器支持</p>
>  </noscript>
></body>
>```
> 3.触发条件：
> 1. 浏览器不支持脚本；
> 2. 浏览器支持脚本，但脚本被禁用；
