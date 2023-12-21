1、vue模板语法
> 1.基于HTML的模板语法，允许开发者声明式的将DOM绑定至底层Vue实例的数据；
> 2.核心：允许采用简洁的模板语法来声明式的将数据渲染进DOM系统；
> 3.结合响应式系统，在引用状态改变时，能够只能计算出重新渲染组件的最小代价并应用到DOM操作上。

2、文本值
> 1.常见形式：使用双大括号`{{...}}`；
```
<div id="app">
	<p>{{message}}</p>
</div>
```

3、HTML值
> 1.使用`v-html`指令：输出html代码；
```html
<div id="app">
	<p v-html="message"></p>
</div>
```
```js
var vm = new Vue({
  el:'#app',
  data:{
      message:'<h1>HTML插值</h1>'
  }
});
```

4、属性值
> 1.使用`v-bind`指令，绑定属性值；
```html
//如果use为true，则使用class1类的样式
<div id="app">
	<p v-bind:class="{'class1':use}"></p>
</div>
//如果use为true，则使用class1类的样式
```

5、表达式
> 1.Vue.js提供了完全的JavaScript表达式支持；
```html
<div id="app">
	<p v-bind:id="'list-'+id"></p>
	{{5+5}}}<br>
	{{ok?'YES':'NO'}}<br>
	{{message.split('').reverse().join('')}}
</div>
```
```js
var vm = new Vue({
  el:'#app',
  data:{
    id:1,
    ok:true,
    message:'RUNOOB',
  }
});
```

6、指令
> 1.定义：带有`v-`前缀的特殊属性；
> 2.作用：用于在表达式值改变时，某些行为应用到DOM上

7、参数
> 1.参数在指令后用冒号指明

8、修饰符
> 1.以半角句号`.`指明的特殊后缀，用于一个指令应该以特殊方式绑定；
```html
//.prevent修饰符告诉v-on指令对于触发的事件调用event.preventDefault()方法
<form v-on:submit.prevent="onSubmit"></form>
```

9、用户输入
> 1.在input框中使用`v-model`指令来实行双向数据绑定；
> 2.`v-model`用来在input，select，text，checkbox，radio等表单控件上创建双向数据。根据表单上的值，自动更新绑定的元素的值
```html
<div id="app">
    <p>{{ message }}</p>
    <input v-model="message">
</div>
```
```js
new Vue({
  el: '#app',
  data: {
    message: 'Runoob!'
  }
})
```

10、过滤器
> 1.允许自动以过滤器，用于一些常见的文本格式化。
> 2.过滤器函数，接受表达式的第一个值作为第一个参数；
```html
<div id="app">
  {{ message | capitalize }}
</div>
```
```js
var vm = new Vue({
    el: '#app',
    data: {
        message: 'runoob',
    },
    filters: {
        capitalize: function (value) {
            if (!value) return '';
            value = value.toString();
            return value.charAt(0).toUpperCase() + value.slice(1);
        }
    }
});
```
> 3.格式（由管道符指示）：
> 1. 在两个大括号中：
```
{{message|capitalize}}
```
> 2. 在`v-bind`指令中
```
<div v-bind:id="rawId|formatId"></div>
```
