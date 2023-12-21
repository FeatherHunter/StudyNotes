1、什么是AJAX
> 1.全名： Asynchronous JavaScript And XML，异步JavaScript和XML；
> 2.是一种创建交互式网页应用的网页开发技术。
> 3.是一种用于创建快速动态网页的技术；
> 4.是一种在无需加载整个页面的情况下，能够更新部分网页的技术；
> 5.实现了异步更新，即按需更新；

2、AJAX对象
> 1.基于XMLHTTPRequest对象，简称XHR对象；

3、ajax()函数实现AJAX操作
> 1.常用参数：
```
<input type="button" value="个人信息"/>
<div>
    <h3>姓名：<span id="name"></span></h3>
    <h3>工资：<span id="salary"></span></h3>
    <h3>课程：<span id="lesson"></span></h3>
    <h3>域名：<span id="siteName"></span></h3>
</div>

$('input').click(function () {
    $.ajax({                        //jquery函数
        type:"GET",                 //http请求方法
        url:"demo.json",            //要获取数据的url
        data:null,                  //不给url添加任何数据
        dataType:"json",            //获取到的数据当做json类型处理
        success:function(data){     //完成时调用这个回调函数
            $("#name").text(data.name);
            $("#salary").text(data.salary);
            $("#lesson").text(data.lesson[1]);
            $("#siteName").text(data.website.siteName);
        }
    });
});
```
```json
{
  "name": "顾文慧",
  "salary": "2222",
  "lesson": ["JQuery","javaScript"],
  "website":{
    "siteName":"百度一下",
    "domain":"www.baidu.com"
  },
  "version": "1.0.0",
  "dependencies": {

  }
}
```

4、load()方法
> 1.语法：`$(选择器).load(url,args.function(data){回调函数})`；
> 2.访问纯文本文件：
```txt文件
纯文本文件1111
```
```
<input id="txt" type="button" value="纯文本文件"/>
<div id="text"></div>

$('#txt').click(function(){
    $('#text').load("demo.txt");                  //纯文本文件
});
```
> 3.访问html文件（XML文件类似）：
```
$('#txt').click(function(){
    $('#text').load("demo.html");                    //html文件全部内容
    $('#text').load('demo.html li:first', '?' + (new Date()));          //获取html文件的第一个li标签
});
```
```html
<ul>
    <li>课程列表1</li>
    <li>课程列表1</li>
    <li>课程列表1</li>
    <li>课程列表1</li>
    <li>课程列表1</li>
</ul>
```
 > 4.访问json数据
 ```json
 {
  "name": "顾文慧",
  "salary": "2222",
  "lesson": ["JQuery","javaScript"],
  "website":{
    "siteName":"百度一下",
    "domain":"www.baidu.com"
  },
  "version": "1.0.0",
  "dependencies": {

  }
}
 ```
 ```
 $('#txt').click(function () {
     $('#text').load('demo.json',function(data){
         var jsonObj = JSON.parse(data);                             //将获取的json字符串解析为js对象
         $(this).empty()                                             //清空当前显示的json字符串内容
             .append('<p>姓名：<span>'+jsonObj.name+'</span>')
             .append('<p>工资：<span>'+jsonObj.salary+'</span>')
             .append('<p>课程：<span>'+jsonObj.lesson[0]+'</span>')
             .append('<p>域名：<span>'+jsonObj.website.siteName+'</span>')
     })
 });
 ```


 5、Ajax的核心技术——XMLHttpRequest对象
 > 1.简称为XHR对象。
 > 2.创建XHR对象，使用new方法；
 > 3.调用open()方法，启动一个请求以备发送。接受三个参数：请求类型(get,post)、请求的URL、是否异步（false同步);
 > 4.调用send()方法，发送请求。接受一个参数：作为请求主体发送的数据，如果不需要，必须填null。
 ```
 var xhr = new XMLHttpRequest();              //创建XHR对象
 xhr.open('get','demo.php',false);
 xhr.send(null);
 ```

 6、XHR对象的属性
 > 1.IE低版本浏览器，第一次向服务器端请求时，获取最新数据，而第二次就会获取缓存数据，不会获取最新的数据，解决方法，使用JS随机字符串。
 ```
 xhr.open('get', 'demo.php?rand=' + Math.random(), false);
 xhr.send(null);
 alert(xhr.responseText);
 ```

 |属性名|说明|
 |---|---|
 |responseText|作为响应主体被返回的文本|
 |responseXML|如果响应主体中的内容是'text/xml'或'application/xml'，则返回包含响应数据的XML DOM文档|
 |status|响应的HTTP状态|
 |statusText|HTTP状态的说明|

 7、HTTP状态码
 > 1.接受响应后，第一步检查status属性，以确定响应已经成功返回。
 > 2.HTTP状态码为200，表示成功。
 ```
 var xhr = new XMLHttpRequest();
 xhr.open('get', 'demo.php', false);
 xhr.send(null);
 if (xhr.status === 200) {
     console.log(xhr.responseText);
 } else {
     throw new Error('出错：状态码：' + xhr.status);
 }
 ```

 |HTTP码|状态字符串|说明|
 |---|---|---|
 |200|OK|服务器成功返回了页面|
 |400|Bad Request|语法错误导致服务器不识别|
 |401|Unauthorized|请求需要用户认证|
 |404|Not found|指定的URL在服务器上找不到|
 |500|Internal Server Error|服务器遇到意外错误，无法完成请求|
 |503|ServiceUnavailable|由于服务器过载或维护导致无法完成请求|

8、get()函数
> 1.作用：从服务器上获取数据；
> 2.用法与ajax()函数类似；

9、getJSON()函数
> 1.作用：专门处理json数据；
> 2.与get()函数的区别：不需要再解析json字符串；
```
var jsonObj = JSON.parse(data);            //不需要这个解析过程
```

10、post()函数
> 1.作用：向服务器发送数据；
