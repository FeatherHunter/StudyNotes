1、BOM是什么
> 1.叫浏览器对象模型，用于访问浏览器的功能；
> 2.每个浏览器都按自己的想法扩展它，所以浏览器共有对象成了事实的标准；
> 3.BOM本身没有标准；

2、window对象
> 1.BOM的核心对象：window，表示浏览器的一个实例；
> 2.window对象处于JavaScript结构的最顶层，对于每个打开的窗口，系统都会定义为window对象；

3、window对象的属性
> 1.document：anchors,forms,images,links,location;
> 2.frames：
> 3.history：
> 4.location：
> 5.navigator：
> 6.screen：
> **注意：window对象的属性，也是对象；document对象的属性，也是对象**

4、window下的属性和方法如何调用
> 1.`window.属性`；
> 2.`window.方法()`；
> 3.直接属性，方法()的方式调用；
> **注意：如果是某个浏览器特有的属性或方法，那么在其他浏览器中可能不会识别，当做普通变量或普通函数，此时，可以使用window.属性或window.方法()**

5、系统警告框
> 1.alert()：弹出警告框；
> 2.confirm()：确定和取消按钮，本身方法可以返回布尔值（确定true，取消false）；
```
if(confirm('请点击')){
    alert('确定');
}else{
    alert('取消');
}
```
> 3.prompt()方法：输入提示框；
> 1. 传两个参数，第一个提示信息，第二个默认值；
> 2. 返回输入的值；
```
var box = prompt('请输入。。。',0);
alert(box);
```

6、调出打印和查找对话框
> 1.print()：打印；
> 2.find()：查找；

7、新建窗口
> 1.window.open()：可以导航到一个特定的URL，也可以打开一个新的浏览器窗口；
> 2.参数：第一个参数，要加载的URL；
> 3.第二个参数，窗口的名称或窗口目标；
> 1. 不命名每次打开新窗口，命名的第一次打开新窗口，之后在这个窗口中加载;
> 2. `_blank` ：表示新建一个窗口；
> 3. `_parent`：表示在本窗口内加载；
```
open('http://www.baidu.com','baidu');
open('http://www.baidu.com','_blank');
```
> 4.第三个参数，一个特性字符串，表示各种窗口配置的属性；
```
open('http://www.baidu.com','baidu','width=400,height=400,top=100,left=100');
```
> 5.第四个参数，表示新页面是否取代浏览器记录中当前加载页面的布尔值；
> **注意：open方法本身会返回子窗口的window对象，表示是子窗口弹出的**
```
var box = open('http://www.baidu.com','baidu','width=400,height=400,top=100,left=100');
alert('Lee');             //表示的是父窗口
box.alert('Lee');         //表示的是子窗口
```

8、窗口的位置（属性）
> 1.IE,Safari,Opera和Chrome支持：
> 1. screenLeft：表示窗口相对于屏幕左边的位置；
> 2. screenTop：表示窗口相对于屏幕上边的位置；
```
alert(window.screenLeft);           //每个浏览器的值不同
alert(typeof window.screenLeft);    //number类型（有这个属性）  
                                    //undefined类型（无这个属性）
```
> 2.Firefox,Safari和Chrome支持：
> 1. screenX和screenY属性；
```
//跨浏览器的方法
var leftX = (typeof window.screenLeft == 'number')?window.screenLeft:window.screenX;
var topY = (typeof window.screenTop == 'number'):window.screenTop:window.screenY;
```

9、窗口的页面大小（属性）
> 1.Firefox,Chrome,Safari和Opera支持(IE不支持)：
> 2.innerWidth和innerHeight：返回浏览器窗口本身的尺寸；
> 3.outerWidth和outerHeight：返回浏览器窗口本身和框的尺寸之和；
> 4.在Chrome浏览器中，innerWidth=outerWidth，innerHeight=outerHeight；
> 5.IE和其他4个浏览器都支持，DOM中的方法，性能较差：
> 1. document.documentElement.clientWidth
> 2. document.documentElement.clientHeight
```
//跨浏览器的方法
var width = window.innerWidth;     //必须有window，因为IE不支持
var height = window.innerHeight;
if(typeof width != 'number'){
  if(document.compatMode == 'CSS1Compat'){    //IE6标准模式
    width = document.documentElement.clientWidth;
    height = document.documentElement.clientHeight;
  }else{                                      //IE6怪异模式
    width = document.body.clientWidth;
    height = document.body.clientHeight;
  }
}

alert(width);
alert(height);
```

10、超时调用和间歇调用的作用
> 1.JavaScript是单线程语言，允许设置超时值和间歇时间来调度代码在特定的时刻执行。

11、超时调用
> 1.指定window对象的setTimeout()方法；
> 2.接受两个参数：要执行的代码和毫秒数的超时时间；
> 3.作用：在指定的时间过后执行代码；
```
setTimeout("alert('Lee')",3000);    //这种写法不推荐（容易出错，不易扩展）

function box(){
  alert('Lee');
}
setTimeout(box,3000);

setTimeout(function(){           //推荐（扩展性好，封装性好）
  alert('Lee');
},3000);
```
> 4.超时调用会返回一个数值ID，这个调用ID是计划执行代码的唯一标识符，可以通过这个ID取消尚未执行的超时调用计划；
> 5.clearTimeout()方法：传入相应的超时调用ID；
```
var box = setTimeout(function(){           //返回超时调用的ID
  alert('Lee');
},3000);
clearTimeout(box);                         //取消超时调用
```

12、间歇调用
> 1.setInterval()方法：
> 2.作用：重复不断的执行；
> 3.返回一个数值ID，这个调用ID是计划执行代码的唯一标识符；
> 4.clearInterval()方法取消间歇调用；
```
var box = setInterval(function(){           //返回间歇调用的ID
  alert('Lee');
},2000);
clearInterval(box);                         //取消间歇调用
```
> 5.用途：制作定时器（不常用）
```
var num = 0;
var max = 5;
setInterval(function(){            //5秒定时器
  num++;
  if(num == max){
    clearInterval(this);           //this代表本身，不能代表ID，无效，不能取消
    alert('5秒到了！')；
  }
},1000);

//解决方法
var num = 0;
var max = 5;
var id = null;

function box(){            
  num++;
  if(num == max){
    clearInterval(id);           
    alert('5秒到了！')；
  }
}
id = setInterval(box,1000);
```

13、使用超时调用来模拟间歇调用
> 1.原因：需要根据情况来取消ID，可能会造成同步的问题；
```
var num = 0;
var max = 5;
function box(){            
  num++;
  if(num == max){           
    alert('5秒到了！')；
  }else{
    setTimeout(box,1000);
  }
}
setTimeout(box,1000);       //只能执行一次
```

14、location对象
> 1.location是BOM对象之一，
> 2.提供了与当前窗口中加载的文档有关信息（URL）；
> 3.还提供了一些导航功能。
> 4.location对象是window对象的属性，也是document对象的属性
```
window.location和document.location等效
```

15、location的属性
> 1.hash：获取锚点（设置该属性会跳转）；
```
location.hash = '#21';         //会跳转到#21这个页面
```
> 2.port：获取端口（设置该属性会跳转）；
> 3.hostname：获取主机名（设置该属性会跳转）；
> 4.pathname：获取当前路径（设置该属性会跳转）；
> 5.protocal：获取协议（设置该属性不会跳转）；
```
location.protocal = 'ftp';
```
> 6.search：获取？后的字符串（设置该属性会跳转）；
```
location.search = '?id=2';
```
> 7.href：获取当前URL（设置该属性会跳转）；
```
location.href = 'http://www.baidu.com';
```
```
//获取URL中的指定字符（http://localhost:63342/html/jsdemo.html?id=5&search=ok）
function getArgs(){
  var args = [];
  var qs = locatio.search.length > 0 ? location.subString(1) : '';
  var items = qs.split('&');              //items得到['id=5','search=ok']
  var item = null,name = null, value = null;
  for(var i = 0; i < items.length; i++){
    item = items[i].split('=');           //item得到['id','5'] 和['search','ok']   
    name = item[0];
    value = item[1];
    args[name] = value;                 //数组保存，按照键值对
  }
  return args;
}
var args = getArgs();
alert(args['id']);              //5
alert(args['search']);          //ok
```

16、location跳转的方法
> 1.assign()：跳转到指定的URL；
```
location.assign('http://www.baidu.com');     //跳转到百度
```
> 2.reload()：重新加载，有可能从缓存中加载（最有效的加载）；
> 3.reload(true)：强制加载，从服务器源头加载；
> 4.replace()：避免跳转前的历史记录，无法返回到跳转之前的页面；

17、history对象
> 1.history对象是window对象的属性，保存着用户上网的记录，从窗口被打开的那一刻算起；

18、history对象的属性
> 1.length：history对象中的历史记录数；

19、history对象的方法
> 1.back()：前往浏览器历史条目前一个URL，类似后退；
> 2.forward()：前往浏览器历史条目下一个URL，诶死前进；
> 3.go(num)：浏览器在history对象中向前或向后；
```
function back(){
  history.back();        //后退
}
function forward(){
  history.forward();     //前进
}
history.go(1);           //前进一页
history.go(-1);          //后退一页
history.go(2);           //前进两页
history.go(-2);          //后退两页
```
