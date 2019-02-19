1、鼠标事件——单双击事件
> 1.click方法：用于监听用户单击操作；
> 2.dbclick方法：用于监听用户双击操作；
```
<input type="button" value="单击事件"/>

$('input').click(function(){
    console.log('单击事件');
});
```

2、鼠标事件——鼠标按下和弹起事件
> 1.mousedown方法：用于监听用户鼠标按下时的操作；
> 2.mouseup方法：用于监听用户鼠标弹起的操作；
```
<input type="button" value="鼠标弹起按下事件"/>
<p>鼠标事件</p>

$('input:eq(1)').mousedown(function(){
    $('p').css('color','red');          //鼠标按下为红色
});
$('input:eq(1)').mouseup(function(){
    $('p').css('color','orange');       //鼠标弹起为橙色
});
```

3、鼠标事件——鼠标移入移出事件
> 1.mouseover方法：用于监听用户鼠标移入时的操作；
> 2.mouseout方法：用于监听用户鼠标移出时的操作；
```
<input type="button" value="鼠标移入移出事件"/>
<p>鼠标事件</p>

$('input:eq(2)').mouseover(function(){
    $('p').css('background','red');
});
$('input:eq(2)').mouseout(function(){
    $('p').css('background','pink');
});
```

4、鼠标事件——mousemove事件
> 1.定义：当鼠标指针在指定的元素中移动时，会触发该事件；
> 2.用户吧鼠标移动一个像素，就会触发该事件，会非常耗费系统资源，慎用；
```
<style>
    div{
        border:1px solid red;
        height:50px;
        width:50px;
    }
</style>

<p>鼠标事件</p>
<div></div>

$('div').mousemove(function(){
   $('p').css('color','green');          //鼠标移入这个div时，p元素变成绿色；
});
```

5、鼠标事件——mouseenter事件
> 1.定义：当元素指针穿过元素时，触发该事件；（一般与mouseleave事件一起使用）

6、mouseover和mouseenter的区别
> 1.mouseenter事件：只有鼠标穿过被选元素时才会触发；
> 2.mouseover事件：鼠标穿过任何子元素都会触发该方法；
```
div {
    height: 100px;
    width:300px;
}

<div id="over" style="background: gray;padding: 5px;"><h2 style="background:pink;">mouseover事件:<span></span></h2></div>
<br/>
<div id="enter" style="background: gray;padding: 5px;"><h2 style="background:pink;">mouseenter事件:<span></span></h2></div>

$(document).ready(function () {
    x = 0;
    y = 0;
    $('#over').mouseover(function () {
        $('#over span').text(x += 1);       //从上往下划过一次，值为3
    });
    $('#enter').mouseenter(function () {
        $('#enter span').text(y += 1);      //从上往下划过一次，值为1
    });
});
```

7、鼠标事件——mouseleave事件
> 1.定义：当元素指针离开元素时，触发该事件；

8、mouseout和mouseleave的区别
> 1.mouseleave事件：只有鼠标离开被选元素才会触发；
> 2.mouseout事件：鼠标离开任何子元素都会触发该方法，同理第六点；

9、表单事件——focus()和blur()事件
> 1.focus()事件：
> 1. 定义：当元素获得焦点时，触发该事件；
> 2. 当通过鼠标点击选中元素或通过tab键定位带元素时，该元素会获得焦点；
> 3. focus()方法会触发focus事件，或规定当发生focus事件时运行的函数；
```
<p>鼠标事件</p>
请输入：<input id="focus" type="text"/>

$('#focus').focus(function(){
    $('p').css('color','blue');       //获得焦点，p元素变蓝
});
```
> 2.blur()事件：
> 1. 定义：当元素失去焦点时，触发该事件；
> 2. blur()方法触发blur事件，或者设置了function参数，该函数也可规定当发生blur事件时执行的代码；
> 3. **早前，blur事件仅发生于表单元素，现在，可用于任何元素。**
```
<p>鼠标事件</p>
请输入：<input id="focus" type="text"/>

$('#focus').blur(function(){
    $('p').css('color','yellow');       //失去焦点，p元素变黄
});
```

10、表单事件——change()事件
> 1.定义：当元素的值发生改变时，会触发该事件；
> 2.该事件仅用于文本域(text field)，以及textarea和select元素。
> 3.**注意：当用于select元素时，change事件会在选择某个选项时发生；用于text field和textarea时，该事件会在元素失去焦点前发生；**
```
<p>鼠标事件</p>
<select>
    <option>选项1</option>
    <option>选项2</option>
    <option>选项3</option>
</select>

$('select').change(function(){
    $('p').css('color','pink');     //更换选项时，p元素会变粉
});
```

11、表单事件——select()事件
> 1.定义：当textarea或文本类型的input元素中文本被选中时，触发该事件；
> 2.select()方法触发select事件，或规定当发生select事件时运行的函数；
```
<p>鼠标事件</p>
<input id="select" type="text" value="select事件"/>

$('#select').select(function(){
    $('p').css('color','red');           //选中input里的value值时，p元素会变红
});
```

12、表单事件——submit()事件
> 1.当提交表单时，会触发该事件；
> 2.该事件只适用于表单元素；
```
<form action="#" method="post" name="form">
    用户名：<input type="text"/><br/>
    密码：<input type="text"/>
    <input type="submit" value="提交"/>
</form>

$('form').submit(function(){
   alert('error');              //点击提交按钮时，会弹出error
});
```

13、键盘事件——keydown()和keyup()事件
> 1.keydown事件：按钮被按下时触发该事件；
> **注意：如果在文档元素上进行设置，无论元素是否获取焦点，都会触发该事件。**
> 2.keyup事件：按钮被松开时触发该事件，发生在当前获得焦点的元素上。
```
$('body').keydown(function(){
    $('p').html('keydown事件');      //按下按键时触发
});

$('body').keyup(function(){
    $('p').html('keyup事件');       //松开按键时触发
});
```

14、键盘事件——keypress()事件
> 1.定义：当按钮被按下时触发，发生在当前获得焦点的元素上；
> 2.与keydown不同：每插入一个字符，就会发生keypress事件；
```
输入字符验证keypress：<input type="text"/>
<p>keypress：<span>0</span></p>

i = 0;
$('input').keypress(function () {
    $('p span').text(i += 1);            //每插入一个字符，i会加1
});
```
