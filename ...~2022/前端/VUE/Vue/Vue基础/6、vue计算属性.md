1、vue计算属性`computed`
> 1.计算属性在处理一些复杂逻辑时很有用；
```html
<div id="app">
  <p>原始字符串: {{ message }}</p>
  <p>计算后反转字符串: {{ reversedMessage }}</p>
</div>`
```
```
var vm = new Vue({
  el: '#app',
  data: {
    message: 'Runoob!'
  },
  computed: {
    // 计算属性的 getter
    reversedMessage: function () {
      // `this` 指向 vm 实例
      return this.message.split('').reverse().join('')
    }
  }
})
```

2、`methods`与`computed`
> 1.我们可以使用methods来替代computed，效果是一样的
> 2.但是，computed是基于它的依赖缓存，只有相关缓存发生改变时才会重新获取值；
> 3.而，methods。在重新渲染时，函数总会执行调用。


3、`computed`的getter和setter
> 1.computed属性，默认只有getter，不过可以自己提供一个setter；
```html
<div id="app">
  <p>{{ site }}</p>
</div>
```
```js
var vm = new Vue({
  el: '#app',
  data: {
	name: 'Google',
	url: 'http://www.google.com'
  },
  computed: {
    site: {
      // getter
      get: function () {
        return this.name + ' ' + this.url
      },
      // setter
      set: function (newValue) {
        var names = newValue.split(' ')
        this.name = names[0]
        this.url = names[names.length - 1]
      }
    }
  }
})
// 调用 setter， vm.name 和 vm.url 也会被对应更新
vm.site = '菜鸟教程 http://www.runoob.com';
document.write('name: ' + vm.name);
document.write('<br>');
document.write('url: ' + vm.url);
```
