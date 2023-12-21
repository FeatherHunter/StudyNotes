1、Vue实例化
> 1.每个vue应用，都需要实例化Vue来实现
> 2.语法格式：
```
var vm = new Vue({
    //选项
})
```

2、Vue构造器中的参数
> 1.el参数：它是DOM元素中的id值；意味着只有id内部的元素会受影响；
```
<div id="vue-det"></div>
```
```
var vm = new Vue({
    el:'#vue-det'
})
```
> 2.data参数：用于定义属性；
> 3.methods参数：用于定义函数，可以通过return来返回函数值；
> 4.`{{ }}`：用于输出对象属性和函数返回值；
```html
<div id="vue-det">
	<h1>site:{{site}}</h1>
	<h1>url:{{url}}</h1>
	<h1>{{details}}</h1>
</div>
```
```js
var data = {site:"菜鸟教程",url:'www.baidu.com'};
	var vm = new Vue({
		el:'#vue-det',
		data:data
	})
```

3、Vue的数据属性
> 1.Vue实例和data属性石互相影响的；
```js
// 它们引用相同的对象！
document.write(vm.site === data.site) // true
document.write("<br>")
// 设置属性也会影响到原始数据
vm.site = "Runoob"
document.write(data.site + "<br>") // Runoob

// ……反之亦然
data.url = '1234'
document.write(vm.url) // '1234'
```

4、Vue的实例属性和方法
> 1.他们有前缀`$`，便于与用于定义的属性区分开来；
```js
document.write(vm.$data === data) // true
document.write("<br>")
document.write(vm.$el === document.getElementById('vue_det')) // true
```
