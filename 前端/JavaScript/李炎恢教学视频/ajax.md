1、Ajax——异步方式
> 1.能够从服务器请求额外的数据而无需卸载页面(即刷新)。

2、Ajax的核心技术——XMLHttpRequest对象
> 1.简称为XHR对象。
> 2.创建XHR对象，使用new方法；
> 3.调用open()方法，启动一个请求以备发送。接受三个参数：请求类型(get,post)、请求的URL、是否异步（false同步);
> 4.调用send()方法，发送请求。接受一个参数：作为请求主体发送的数据，如果不需要，必须填null。
```
var xhr = new XMLHttpRequest();              //创建XHR对象
xhr.open('get','demo.php',false);
xhr.send(null);
```

3、XHR对象的属性
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

4、HTTP状态码
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

5、异步调用Ajax
> 1.异步调用，需要出发readystatechange事件，然后检测readyState属性
> 2.使用abort()方法，取消异步请求，放在send方法之前会出错，应该放在send方法的后面。

|readyState的属性值|状态|说明|
|---|---|---|
|0|未初始化|尚未调用open方法|
|1|启动|已调用open方法，但未调用send方法|
|2|发送|已调用send方法，但尚未接受响应|
|3|接受|已接受部分响应数据|
|4|完成|接受全部响应数据，而且可以使用|
```
//异步
var xhr = new XMLHttpRequest();
xhr.onreadystatechange = function () {
    if(xhr.readyState === 4){
        if (xhr.status === 200) {
            console.log(xhr.responseText);
        } else {
            throw new Error('出错：状态码：' + xhr.status);
        }
    }
};
xhr.open('get', 'demo.php', true);        //改为true
xhr.send(null);
xhr.abort();                              //取消异步请求
```

6、HTTP头部信息
> 1.响应头信息：服务器返回的信息，客户端可以获取，不可以设置；
> 2.请求头信息：客户端发送出去的信息，客户端可以设置，不可以获取；
> 3.获取单个或全部响应头信息；
```
console.log(xhr.getResponseHeader('Content-Type'));    //单个
console.log(xhr.getAllResponseHeaders());              //全部
```
> 4.设置单个请求头信息：在open方法之后，send方法之前
```
console.log(xhr.setRequestHeader('MyHeader','Lee'));   //在POST提交时有用
```

6、GET和POST请求的区别：在Web程序上
> 1.GET一般是URL提交请求；
> 2.POST一般是表单提交；

7、GET请求
> 1.最常见的请求类型，用于向服务器查询某些信息。必要时，可以将查询字符串参数追加到URL的末尾，以便提交给服务器。
```
xhr.open('get', 'demo.php ? rand = ' + Math.random() + ' & name = Koo', true);
```
> 2.中文乱码问题，要全部设置为UTF-8编码；
> 3.特殊字符，通过encodeURIComponent()方法来编码；
```
var url = 'demo.php ? rand = ' + Math.random();
url = params(url,'name','L&ee');                        //特殊字符&是在值里面的
url = params(url,'age',23);
console.log(decodeURIComponent(url));             //demo.php ? rand = 0.9864410807572037&name=L&ee&age=23

function params(url, name, value) {
    url += url.indexOf('?') === -1 ? '?' : '&';      //首先判断有没有'?'号，有就加'&'，没有就加'?'
    url += encodeURIComponent(name) + '=' + encodeURIComponent(value);    //进行编码
    return url;
}
```

8、POST请求
> 1.可以包含非常多的数据，在提交表单的时候，常用POST请求；
> 2.通过send方法向服务器提交数据，要模拟表单提交；
```
var xhr = new XMLHttpRequest();
var url = 'demo.php ? rand = ' + Math.random();
xhr.onreadystatechange = function () {
    if (xhr.readyState === 4) {
        if (xhr.status === 200) {
            console.log(xhr.responseText);
        } else {
            throw new Error('出错：状态码：' + xhr.status);
        }
    }
};
xhr.open('post', 'demo.php', true);
xhr.setRequestHeader('Content-Type','application/x-www-form-urlencoded');    //模拟表单提交
xhr.send('name=Lee&age=30');
```

9、封装ajax
```
//名值对转换字符串
function params(data) {
    var arr = [];
    for (var i in data) {
        arr.push(encodeURIComponent(i) + '=' + encodeURIComponent(data[i]));
    }
    return arr.join('&');
}

//封装AJAX
function ajax(obj) {
    var xhr = new XMLHttpRequest();
    //obj.url = obj.url + '? rand = ' + Math.random();
    obj.data = params(obj.data);                 //转换为字符串
    if (obj.method === 'get') {
        obj.url += obj.url.indexOf('?') === -1 ? '?' + obj.data : '&' + obj.data;    //解决url中是否存在’？‘号
    }
    if (obj.async === true) {                  //表示异步调用
        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4) {
                callback();
            }
        };
    }

    xhr.open(obj.method, obj.url, obj.async);
    if (obj.method === 'post') {
        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        xhr.send(obj.data);
    } else {
        xhr.send(null);
    }
    if (obj.async === false) {         //表示同步调用
        callback();
    }

    function callback() {       
        if (xhr.status === 200) {
            obj.success(xhr.responseText);    //回调传递参数
        } else {
            throw new Error('出错：状态码：' + xhr.status);
        }
    }
}

//调用AJAX
document.onclick = function () {
    ajax({
        method: 'get',
        url: 'demo.php',
        data: {
            'name': 'Lee',
            'age': 30
        },
        success: function (text) {
            console.log(text);              //获取到成功的数据
        },
        async: true
    });
};
```
