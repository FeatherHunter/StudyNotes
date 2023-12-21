1、组件Component
> 1.作用：可以扩展HTML元素，封装可重用的代码；
> 2.组件系统让我们可以独立可复用的小组件来构建大型应用，几乎任意类型的应用的界面都可以抽象为一个组件树；
> 3.注册全局组件的语法：
> 1. tagName为组件名；
> 2. options为配置选项；
```js
Vue.component(tagName,options)
```
> 3. 注册完成后调用组件的方法：
```html
<tagName><tagName>
```

2、全局组件实例
> 1.创建一个全局组件runoob；
```html
<div id="app">
	<runoob></runoob>
</div>
```
```js
// 注册
Vue.component('runoob', {
  template: '<h1>自定义组件!</h1>'
})
// 创建根实例
new Vue({
  el: '#app'
})
```

3、局部组件
> 1.组件只能在这个实例中使用
```html
<div id="app">
	<runoob></runoob>
</div>
```
```js
var Child = {
  template: '<h1>自定义组件!</h1>'
}

// 创建根实例
new Vue({
  el: '#app',
  components: {
    // <runoob> 将只在父模板可用
    'runoob': Child
  }
})
```

4、prop属性
> 1.prop是父组件用来传递数据的一个自定义属性；
> 2.父组件通过props把数据传递给子组件，子组件需要显式地用props选项声明"prop"；
```html
<div id="app">
	<child message="hello!"></child>
</div>
```
```js
// 注册
Vue.component('child', {
  // 声明 props
  props: ['message'],
  // 同样也可以在 vm 实例中像 “this.message” 这样使用
  template: '<span>{{ message }}</span>'
})
// 创建根实例
new Vue({
  el: '#app'
})
```

5、动态prop
> 1.类似用`v-bind`绑定HTML特性到一个表达式，
> 2.也可以用`v-bind`动态绑定props的值到父组件的数据中；
> 3.每当父组件的数据变化时，该变化也会传导给子组件；
```html
<div id="app">
	<div>
	  <input v-model="parentMsg">
	  <br>
	  <child v-bind:message="parentMsg"></child>
	</div>
</div>
```
```js
// 注册
Vue.component('child', {
  // 声明 props
  props: ['message'],
  // 同样也可以在 vm 实例中像 “this.message” 这样使用
  template: '<span>{{ message }}</span>'
})
// 创建根实例
new Vue({
  el: '#app',
  data: {
	parentMsg: '父组件内容'
  }
})
```

6、自定义事件
> 1.父组件是使用props传递数据给子组件；
> 2.但是，如果子组件要把数据传递回去，就需要使用自定义事件；
> 3.可以使用`v-on`绑定自定义事件，每个Vue实例都实现了事件接口(Events interface)，即：
> 1. 使用`$on(eventName)`监听事件；
> 2. 使用`$emit(eventName)`触发事件；
```html
<div id="app">
	<div id="counter-event-example">
	  <p>{{ total }}</p>
	  <button-counter v-on:increment="incrementTotal"></button-counter>
	  <button-counter v-on:increment="incrementTotal"></button-counter>
	</div>
</div>
```
```js
Vue.component('button-counter', {
  template: '<button v-on:click="incrementHandler">{{ counter }}</button>',
  data: function () {
    return {
      counter: 0
    }
  },
  methods: {
    incrementHandler: function () {
      this.counter += 1
      this.$emit('increment')
    }
  },
})


new Vue({
  el: '#counter-event-example',
  data: {
    total: 0
  },
  methods: {
    incrementTotal: function () {
      this.total += 1
    }
  }
})
```

7、`.native`修饰符
> 1.作用：在某个组件的根元素上监听原生事件；
