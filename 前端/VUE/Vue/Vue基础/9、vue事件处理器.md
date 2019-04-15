1、事件监听——直接绑定事件
> 1.使用`v-on`指令；
> 2.接受一个定义的方法来调用，直接绑定；
```html
<div id="app">
   <!-- `greet` 是在下面定义的方法名 -->
  <button v-on:click="greet">Greet</button>
</div>
```
```js
var app = new Vue({
  el: '#app',
  data: {
    name: 'Vue.js'
  },
  // 在 `methods` 对象中定义方法
  methods: {
    greet: function (event) {
      // `this` 在方法里指当前 Vue 实例
      alert('Hello ' + this.name + '!')
      // `event` 是原生 DOM 事件
	  if (event) {
		  alert(event.target.tagName)
	  }
    }
  }
})
```

2、内联javascript语句绑定方法
```html
<div id="app">
  <button v-on:click="say('hi')">Say hi</button>
  <button v-on:click="say('what')">Say what</button>
</div>
```
```js
new Vue({
  el: '#app',
  methods: {
    say: function (message) {
      alert(message)
    }
  }
})
```

3、事件修饰符
> 1.描述：由(.)表示的指令后缀来调用修饰符

|值|描述|
|---|---|
|.stop|阻止单击事件冒泡|
|.prevent|提交事件不再重载页面|
|.capture|添加事件监听器时使用捕获模式|
|.self|只当事件在该元素本身(而不是子元素)触发时回调|
|.once|click事件只能点击一次|

4、按键修饰符
> 1.允许为 v-on 在监听键盘事件时添加按键修饰符
```
<!-- 只有在 keyCode 是 13 时调用 vm.submit() -->
<input v-on:keyup.13="submit">
```
> 2.记住所有的 keyCode 比较困难，所以 Vue 为最常用的按键提供了别名：
```
<!-- 同上 -->
<input v-on:keyup.enter="submit">
<!-- 缩写语法 -->
<input @keyup.enter="submit">
```

|值|描述|
|---|---|
|.enter||
|.tab||
|.delete||
|.esc||
|.space||
|.up||
|.down||
|.left||
|.right||
|.ctrl||
|.alt||
|.shift||
|.meta||
