1、样式绑定
> 1.使用`v-bind`来设置样式属性；
> 2.Vue在使用`v-bind`处理class和style时，增强了它。
> 3.表达式的结果类型除了字符串之外，还可以是对象或数组；
```html
<div id="app">
  <div v-bind:class="{ active: isActive }"></div>
</div>
```
```css
.active {
	width: 100px;
	height: 100px;
	background: green;
}
```
```js
new Vue({
  el: '#app',
  data: {
    isActive: true
  }
})
```
> 4.传入更过属性用来动态切换多个class
```html
<div id="app">
  <div v-bind:class="{ 'active': isActive, 'text-danger': hasError }">
  </div>
</div>
```
```js
new Vue({
  el: '#app',
  data: {
    isActive: true,
	  hasError: true
  }
})
```

2、数组绑定
> 1.给`v-bind:class`传入一个数组；
```html
<div id="app">
	<div v-bind:class="[activeClass, errorClass]"></div>
</div>
```
```css
.active {
	width: 100px;
	height: 100px;
	background: green;
}
.text-danger {
	background: red;
}
```
```js
new Vue({
  el: '#app',
  data: {
    activeClass: 'active',
    errorClass: 'text-danger'
  }
})
```

3、内联样式绑定
> 1.使用`v-bind:style`，直接设置样式；
```html
<div id="app">
	<div v-bind:style="{ color: activeColor, fontSize: fontSize + 'px' }">菜鸟教程</div>
</div>
```
```js
new Vue({
  el: '#app',
  data: {
    activeColor: 'green',
	  fontSize: 30
  }
})
```
