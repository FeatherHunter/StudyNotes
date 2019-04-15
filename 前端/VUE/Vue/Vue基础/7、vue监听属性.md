1、vue监听属性——watch
> 1.通过watch来响应数据的变化；
```html
<div id = "app">
 <p style = "font-size:25px;">计数器: {{ counter }}</p>
 <button @click = "counter++" style = "font-size:25px;">点我</button>
</div>
```
```js
var vm = new Vue({
   el: '#app',
   data: {
      counter: 1
   }
});
vm.$watch('counter', function(nval, oval) {
   alert('计数器值的变化 :' + oval + ' 变为 ' + nval + '!');
});
```
