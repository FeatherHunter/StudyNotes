1、onload和ready的写法
```
window.onload = function(){

}

$(function(){

})
$(document).ready(function(){       //等同于第二种写法

})
```

2、onload和ready的执行顺序
> 1.window.onload
> 1. 包括页面的图片加载完成之后才会调用（晚）；
> 2. 只能有一个回调监听；
```
```
> 2.$(document).ready()
> 1. 等同于：`$(function(){})`；
> 2. 页面加载完成就回调，不管图片（早）；
> 3. 可以有多个监听回调；
