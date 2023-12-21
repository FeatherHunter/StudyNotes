1、注册全局指令
> 1.语法：
```js
Vue.directive(tagName,options)
```
```html
<div id="app">
	<p>页面载入时，input 元素自动获取焦点：</p>
	<input v-focus>
</div>
```
```js
// 注册一个全局自定义指令 v-focus
Vue.directive('focus', {
  // 当绑定元素插入到 DOM 中。
  inserted: function (el) {
    // 聚焦元素
    el.focus()
  }
})
// 创建根实例
new Vue({
  el: '#app'
})
```

2、注册局部指令
> 1.语法：使用directives选项来注册；
```html
<div id="app">
  <p>页面载入时，input 元素自动获取焦点：</p>
  <input v-focus>
</div>
```
```js
// 创建根实例
new Vue({
  el: '#app',
  directives: {
    // 注册一个局部的自定义指令 v-focus
    focus: {
      // 指令的定义
      inserted: function (el) {
        // 聚焦元素
        el.focus()
      }
    }
  }
})
```

3、钩子函数
> 1.bind：只调用一次，指令第一次绑定到元素时调用，用这个钩子函数可以定义一个在绑定时执行一次的初始化动作；
> 2.inserted：被绑定元素插入父节点时嗲用(父节点存在即可调用，不必存在于document中)
> 3.update：被绑定元素所在的模板更新时调用，而不论绑定值是否变化。通过比较更新前后的值，可以忽略不必要的模板更新；
> 4.componentUpdated：被绑定元素所在模板完成一次更新周期时调用；
> 5.unbind：只调用一次，指令与元素解绑时调用；

4、钩子函数参数
> 1.el：指令所绑定的元素，可以用来直接操作DOM
> 2.binding：
```
name：指令名，不包括v-前缀；
value：指令的绑定值
oldValue：指令绑定的前一个值，仅在update和componentUpdated钩子中可用。无论值是否改变都可用；
expression：绑定值得表达式或变量名
arg：传给指令的参数
modifiers：一个包含修饰符的对象
```
> 3.vnode：Vue编译生成的虚拟节点；
> 4.oldVnode：上一个虚拟节点，仅在update和componentUpdated钩子中使用；
```html
<div id="app"  v-runoob:hello.a.b="message">
</div>
```
```js
Vue.directive('runoob', {
  bind: function (el, binding, vnode) {
    var s = JSON.stringify
    el.innerHTML =
      'name: '       + s(binding.name) + '<br>' +
      'value: '      + s(binding.value) + '<br>' +
      'expression: ' + s(binding.expression) + '<br>' +
      'argument: '   + s(binding.arg) + '<br>' +
      'modifiers: '  + s(binding.modifiers) + '<br>' +
      'vnode keys: ' + Object.keys(vnode).join(', ')
  }
})
new Vue({
  el: '#app',
  data: {
    message: '菜鸟教程!'
  }
})
```
