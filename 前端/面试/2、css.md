1、display:none和visibility:hidden的区别？
> 1.display:none  隐藏对应的元素，在文档布局中不再给它分配空间，它各边的元素会合拢，就当他从来不存在;
> 2.visibility:hidden  隐藏对应的元素，但是在文档布局中仍保留原来的空间;

2、CSS中 link 和@import 的区别是？
> 1.link属于HTML标签，而@import是CSS提供的;
> 2.页面被加载的时，link会同时被加载，而@import引用的CSS会等到页面被加载完再加载;
> 3.import只在IE5以上才能识别，而link是HTML标签，无兼容问题;
> 4.link方式的样式的权重 高于@import的权重。

3、position的absolute与fixed共同点与不同点
> 1.共同点：
> 1. 改变行内元素的呈现方式，display被置为block；
> 2. 让元素脱离普通流，不占据空间；
> 3. 默认会覆盖到非定位元素上;
```
```
> 2.不同点：
> 1. absolute的”根元素“是可以设置的，
> 2. 而fixed的”根元素“固定为浏览器窗口。
> 3. 当你滚动网页，fixed元素与浏览器窗口之间的距离是不变的


4、介绍一下CSS的盒子模型？
> 1.有两种， IE 盒子模型、标准 W3C 盒子模型；IE的content部分包含了 border 和 padding;
> 2.盒模型： 内容(content)、填充(padding)、边界(margin)、 边框(border);

5、CSS 选择符有哪些？
> 1.id选择器（ # myid）
> 2.类选择器（.myclassname）
> 3.标签选择器（div, h1, p）
> 4.相邻选择器（h1 + p）
> 5.子选择器（ul > li）
> 6.后代选择器（li a）
> 7.通配符选择器（ * ）
> 8.属性选择器（a[rel = "external"]）
> 9.伪类选择器（a: hover, li:nth-child）
> 10.伪元素选择器（span:after）

6、哪些属性可以继承？
> 1.可继承：font-size ,font-family, color, text-indent;
> 2.不可继承：border padding margin width height；

7、选择器的优先级算法如何计算？
> 1.优先级就近原则，同权重情况下样式定义最近者为准;
> 2.优先级：
```   
!important > 内联样式 >  id > class > tag  
important 比 内联优先级高,但内联比 id 要高
```

8、CSS3新增伪类有那些？
```
p:first-of-type 选择属于其父元素的首个 <p> 元素的每个 <p> 元素。
p:last-of-type  选择属于其父元素的最后 <p> 元素的每个 <p> 元素。
p:only-of-type  选择属于其父元素唯一的 <p> 元素的每个 <p> 元素。
p:only-child    选择属于其父元素的唯一子元素的每个 <p> 元素。
p:nth-child(2)  选择属于其父元素的第二个子元素的每个 <p> 元素。
:enabled  :disabled 控制表单控件的禁用状态。
:checked        单选框或复选框被选中。
```

9、列出display的值，说明他们的作用
> 1.block 像块类型元素一样显示;
> 2.inline 缺省值。像行内元素类型一样显示
> 3.inline-block 像行内元素一样显示，但其内容象块类型元素一样显示
> 4.list-item 象块类型元素一样显示，并添加样式列表标记

10、block, inline和inline-block的区别
> 1.起新行
> 1. block元素会独占一行, 多个block元素会各自新起一行. 默认情况下, block元素宽度自动填满其父元素宽度;
> 2. inline元素不会独占一行, 多个相邻的行内元素会排列在同一行里, 直到一行排列不下, 才会新换一行, 其宽度随元素的内容而变化;
```
```
> 2.设置宽高
> 1. block元素可以设置width, height属性. 块级元素即使设置了宽度, 仍然独占一行;
> 2. inline元素设置width, height无效
```
```
> 3.内外边距
> 1. block元素可以设置margin和padding属性
> 2. inline元素的margin和padding属性,水平方向的padding-left, padding-right, margin-left, margin-right都会产生边距效果. 但是数值方向的 margin/padding-top/bottom不会产生边距效果
```
```
> 4.包含
> 1. block可以包含inline和block元素,
> 2. inline元只能包含inline元素;
```
```
> 5.display: inline-block
> 1. 将对象呈现为inline对象, 但是对象的内容作为block对象呈现,之后的内联对象会被排列到一行内;
> 2. 比如我们可以给一个link(a元素)inline-block的属性, 使其既有block的高宽特性又有inline的同行特性

11、position的值， relative和absolute分别是相对于谁进行定位的？
> 1.absolute：生成绝对定位的元素，相对于除 static 定位以外的第一个祖先元素进行定位；
> 2.fixed （老IE不支持）：生成绝对定位的元素，相对于浏览器窗口进行定位；
> 3.relative：生成相对定位的元素，相对于其在普通流中的位置进行定位;
> 4.static  默认值。没有定位，元素出现在正常的流中（忽略 top, bottom, left, right z-index 声明）；
> 5.inherit 规定从父元素继承 position 属性的值；

12、CSS3有哪些新特性？
```
CSS3实现圆角（border-radius），阴影（box-shadow），
对文字加特效（text-shadow、），线性渐变（gradient），旋转（transform）
transform:rotate(9deg) scale(0.85,0.90) translate(0px,-30px) skew(-9deg,0deg);//旋转,缩放,定位,倾斜
增加了更多的CSS选择器  多背景 rgba
在CSS3中唯一引入的伪元素是::selection.
媒体查询，多栏布局
border-image
```

13、为什么要初始化CSS样式
> 1. 因为浏览器的兼容问题，不同浏览器对有些标签的默认值是不同的，如果没对CSS初始化往往会出现浏览器之间的页面显示差异；
> 2.当然，初始化样式会对SEO有一定的影响，但鱼和熊掌不可兼得，但力求影响最小的情况下初始化;
> 3.最简单的初始化方法就是： * {padding: 0; margin: 0;} （不建议）;

14、对BFC规范的理解？
> 1.BFC，块级格式化上下文，一个创建了新的BFC的盒子是独立布局的，盒子里面的子元素的样式不会影响到外面的元素。在同一个BFC中的两个毗邻的块级盒在垂直方向（和布局方向有关系）的margin会发生折叠;

15、解释下 CSS sprites，以及你要如何在页面或网站中使用它
> 1.CSS Sprites其实就是把网页中一些背景图片整合到一张图片文件中，再利用CSS的“background-image”，“background- repeat”，“background-position”的组合进行背景定位;
> 2.background-position可以用数字能精确的定位出背景图片的位置.
> 3.可以减少很多图片请求的开销，因为请求耗时比较长；请求虽然可以并发，但是也有限制，一般浏览器都是6个;

16、box-sizing属性
> 1.box-sizing属性主要用来控制元素的盒模型的解析模式。默认值是content-box；
> 2.content-box：让元素维持W3C的标准盒模型。元素的宽度/高度由border + padding + content的宽度/高度决定，设置width/height属性指的是content部分的宽/高;
> 3.border-box：让元素维持IE传统盒模型（IE6以下版本和IE6~7的怪异模式）。设置width/height属性指的是border + padding + content;
> 4.标准浏览器下，按照W3C规范对盒模型解析，一旦修改了元素的边框或内距，就会影响元素的盒子尺寸，就不得不重新计算元素的盒子尺寸，从而影响整个页面的布局

17、解释浮动和工作原理
> 1.浮动可以理解为让某个div元素脱离标准流，漂浮在标准流之上，和标准流不是一个层次;
> 2.假如某个div元素A是浮动的，如果A元素上一个元素也是浮动的，那么A元素会跟随在上一个元素的后边(如果一行放不下这两个元素，那么A元素会被挤到下一行)；如果A元素上一个元素是标准流中的元素，那么A的相对垂直位置不会改变，也就是说A的顶部总是和上一个元素的底部对齐;
> 3.清除浮动是为了清除使用浮动元素产生的影响。浮动的元素，高度会塌陷，而高度的塌陷使我们页面后面的布局不能正常显示;

18、清除浮动的方法
> 1.父级div定义伪类：after和zoom
> 2.在结尾处添加空div标签clear:both
> 3.父级div定义height
> 4.父级div定义overflow:hidden
> 5.父级div定义overflow:auto
> 6.父级div也一起浮动
> 7.父级div定义display:table
> 8.结尾处加br标签clear:both
