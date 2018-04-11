1、html分为head和body标签

2、head标签存放标题以及meta数据,meta可以存放关键字、作者等数据

3、body存放网页主体内容

4、换行的几种方法？
>1. `<br/>`标签
>2. `<p>hello world<p/>`,`段落标签`-上下都会空一行。
>3. `<h1>hello<h1/>`等`标题标签`也会`换行`

5、标题标签(6级)
```html
<h1>hello<h1/>
  <h2>hello<h2/>
    <h3>hello<h3/>
      <h4>hello<h4/>
        <h5>hello<h5/>
          <h6>hello<h6/>
```

6、有序列表
```html
<ul>
    <li>剧集</li>
    <li>电影</li>
    <li>综艺</li>
    <li>动漫</li>
    <li>娱乐</li>
</ul>
```

7、无序列表
```html
<ol>
    <li>剧集</li>
    <li>电影</li>
    <li>综艺</li>
    <li>动漫</li>
    <li>娱乐</li>
</ol>
```

8、列表的子标签li可以作为布局(内部嵌套标题、列表等等)
```html
<ul>
    <li>
        <h1>剧集</h1>
        <ul>
            <li>动作片</li>
            <li>剧情片</li>
            <li>动画片</li>
        </ul>
    </li>
    <li>
        <h1>电影</h1>
        <ul>
            <li>战狼</li>
            <li>头号玩家</li>
        </ul>
    </li>
</ul>
```

9、xhtml中的列表
```xhtml
<dl>
    <dt>我是XHTML中dt的title</dt>
    <dd>纪实</dd>
    <dd>公益</dd>
    <dd>体育</dd>
</dl>
```
>html中用下列代码可以实现相同效果：
```html
<h1>
    我是HTML中的title
</h1>
<ul>
    <li>纪实</li>
    <li>公益</li>
    <li>体育</li>
</ul>
```

10、html中的注释
```html
<!-- 我是注释 -->
```

11、html中`<pre></pre>`标签显示代码段
```html
<pre>
    public class Person{
        String name;
        int age;
        boolean sex;
    }
</pre>
```

12、html中特殊的符号需要转义字符
|字符|转义字符|
|---|---|
|<   |`&lt; `  |
|>   |`&gt; `  |
|空格   | `&nbsp;`   |
|拷贝权限符号： © | `&copy`  |
| &   |`&amp;`   |

13、超链接`<a></a>`标签
```html
<a href="https://www.baidu.com/"
   target="_blank"
   title="鼠标悬浮在文字上时的提示语">这是“百度一下”的超链接</a>
```
>超链接标签也可以作为`父布局`去放置`img`等标签

14、跑马灯标签: `<marquee></marquee>`
```html
<marquee
        direction="right"
        scrollamount="5"
        onmouseover="this.stop()"
        onmouseout="this.start()">
    这是一个弹弹弹的垃圾控件
</marquee>
```
>`marquee`标签也可以作为`父布局`去放置`img`等标签

15、图片标签
```html
<img src="https://img3.doubanio.com/view/photo/s_ratio_poster/public/p2510956726.webp"
     width="300"
     height="400"
     alt="图片没有加载时显示的内容"
     title="鼠标悬浮在图片上时显示的标题">
```

16、表格`table、tr、td标签`
```html
<table>
    <tr>
        <td>A</td>
        <td>B</td>
        <td>C</td>
    </tr>
    <tr>
        <td>D</td>
        <td>E</td>
        <td>F</td>
    </tr>
    <tr>
        <td>G</td>
        <td colspan="2">H</td>
    </tr>
</table>
```
>复杂页面用table扩展性太差，最好的办法是使用`div标签+css`

17、布局标签`div`的作用
>1. 容器, 类似于`Android中的父布局`
>2. `span标签`是作为文本的容器，可以对文本进行定位。
```html
<div>
    <ul>
        <li>
            <h1>剧集</h1>
            <ul>
                <li>动作片</li>
                <li>剧情片</li>
                <li>动画片</li>
            </ul>
        </li>
        <li>
            <h1>电影</h1>
            <ul>
                <li>战狼</li>
                <li>头号玩家</li>
            </ul>
        </li>
    </ul>
    <span>
          span标签
    </span>
</div>
```

18、html中表单是什么意思？
>1. `表单`的英文为`form`
>2. `html表单`是用于`收集`不同类型的用户输入。
