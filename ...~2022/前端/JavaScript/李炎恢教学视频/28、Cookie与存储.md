1、cookie
> 1.定义：在本地的客户端的磁盘上以很小的文件形式保存数据；
> 2.作用：在会员登录，下次回访无需登录了；

2、cookie的组成
> 1.由名/值对形式的文本组成：name=value。完整格式：中括号是可选的(值，失效时间，路径访问，域名限制访问，安全的https限制通信)
`name=value;[expires=date];[path=path];[domain=somewhere.com];[secure]`
```
document.cookie = 'user=顾文慧';     //向本地磁盘写入cookie
console.log(document.cookie);       //user=顾文慧


//解决编码问题
document.cookie = 'user='+encodeURIComponent('顾文慧');     
console.log(decodeURIComponent(document.cookie));
```

3、cookie的过期时间
> 1.过期时间：是到了这个时间点，就会自动清理cookie。
> 2.如果没有设置过期时间，在浏览器关闭时，会自动清理cookie。
> 3.设置了过期时间，则时间到期后才会失效。
```
var date = new Date();
date.setDate(date.getDate() + 7);      //获取当前日期，延长一周
document.cookie = 'user=' + encodeURIComponent('顾文慧') + ';expires=' + date;     //失效时间是当前日期往后延迟7天(失效后如果日期调整到过期时间之前，cookie仍然存在，不能真正删除)
```
> 4.手工清理cookie：设置过期时间为当前时间之前的时间(真正删除)
```
var date = new Date();
date.setDate(date.getDate() - 1);      //获取当前日期，延长一周
document.cookie = 'user=' + encodeURIComponent('顾文慧') + ';expires=' + date;     //失效时间是当前日期之前
```
> 5.清理cookie：更简单的写法
```
document.cookie = 'user=' + encodeURIComponent('顾文慧') + ';expires=' + new Date(0);     //失效时间是之前的时间
```

4、cookie的路径限制访问——path
> 1.设置了路径后，只有设置的那个路径才可以访问cookie。

5、cookie的域名限制访问——domain
> 1.设置的域名才可以访问，没有设置，默认限制为创建cookie的域名。
> 2.设置域名，必须在当前服务器上设置，如果在当前服务器上设置不属于这个服务器的域名，无效；
> 3。设置二级域名，则只能在二级域名商访问，主域名和其他子域名则不能访问；

6、cookie的安全设置——secure
> 1.指定必须通过https来通信访问；

7、封装设置和获取cookie的方法
```
//设置cookie的方法
function setCookie(name, value, expires, path, domain, secure) {

    var cookieName = encodeURIComponent(name) + '=' + encodeURIComponent(value);
    if (expires instanceof Date) {
        cookieName += ';expires=' + expires;
    }
    if (path) {
        cookieName += ';path=' + path;
    }
    if (domain) {
        cookieName += ';domain=' + domain;
    }
    if (secure) {
        cookieName += ';secure';
    }
    document.cookie = cookieName;
}

//获取cookie
function getCookie(name) {
    var cookieValue = null;
    var cookieName = encodeURIComponent(name) + '=';              //得到'name='
    var cookieStart = document.cookie.indexOf(cookieName);        //得到name的初始位置
    if (cookieStart > -1) {
        var cookieEnd = document.cookie.indexOf(';', cookieStart); //得到name的结尾位置
        if (cookieEnd === -1) {
            cookieEnd = document.cookie.length;                   //有多个cookie，最后一个name没有分号，直接获取整个字符串的长度
        }
        cookieValue = document.cookie.substring(cookieStart+cookieName.length,cookieEnd);
    }
    return decodeURIComponent(cookieValue);
}

//设置过期时间
function setExpires(day) {
    var date = null;
    if (typeof day === 'number' && day > 0) {
        date = new Date();
        date.setDate(date.getDate() + day);
    } else {
        throw new Error('您传递的参数不合法');
    }
    return date;
}

setCookie('user', 'qwe', setExpires(7));
setCookie('email', 'qwe@qq.com', setExpires(7));
console.log(getCookie('email'));
```

8、cookie的局限性
> 1.在每个特定的域名下最多生成20个cookie(根据不同的浏览器有所区别,数字不准确)；
> 1. Microsoft指出InternetExplorer8增加cookie限制为每个域名50个，但IE7似乎也允许每个域名50个cookie。
> 2. Firefox每个域名cookie限制为50个。
> 3. Opera每个域名cookie限制为30个。
> 4. Safari/WebKit貌似没有cookie限制。但是如果cookie很多，则会使header大小超过服务器的处理的限制，会导致错误发生。
> **注意：当Cookie已达到限额，自动踢除最老的Cookie，以使给最新的Cookie一些空间，InternetExplorer和Opera使用此方法。Firefox很独特：虽然最后的设置的Cookie始终保留，但似乎随机决定哪些cookie被保留。**
> 2.cookie的最大大约为4096字节(4k)，为了更好的兼容性，一般不超过4095字节；
> 3.cookie存储在客户端的文本文件，所以特别重要的数据不建议保存在cookie中。例如银行卡号等。

9、其他存储
> 1.IE浏览器，提供了一种持久化用户数据，叫做userData。从IE5开始支持，IE9以上不支持。
> 2.每个数据最多128k，每个域名下最多1k。
> 3.这个持久化数据放在缓存中，如果缓存没有清理，就会一直存在。
> **注意：如果不设置过期时间，就是永久保存**
```
//设置cookie
window.onload=function(){
    var box = document.getElementById('box');
    box.setAttribute('name','aaa');      //名/值对
    //box.expires = setExpires(7).toGMTString();    //设置过期时间，要转换为字符串
    box.save('user');                    //相当于cookie名，保存
};
//获取cookie
window.onload=function(){
    var box = document.getElementById('box');
    box.load('user');                   //加载cookie
    alert(box.getAttribute('name'));
};
//删除cookie
window.onload=function(){
    var box = document.getElementById('box');
    box.removeAttribute('name');         //删除cookie
    box.save('user');
    box.load('user');
    alert(box.getAttribute('name'));
};
```

10、web存储
> 1.在比较高版本的浏览器中，JavaScript提供了sessionStorage和globalStorage。
> 2.在HTML5中，使用localStorage代替globalStorage。
```
//通过方法存储和获取
sessionStorage.setItem('name','Lee');
console.log(sessionStorage.getItem('name'));

//通过属性存储和获取
sessionStorage.book = 'Lee1';
console.log(sessionStorage.book);

//删除存储
sessionStorage.removeItem('name');
```
