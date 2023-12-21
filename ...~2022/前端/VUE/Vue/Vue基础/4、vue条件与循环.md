1、条件判断`v-if`
> 1.指令：`v-if`；
> 2.作用：根据条件判断的真假，来决定是否插入元素；
```html
<div id="app">
    <p v-if="seen">现在你看到我了</p>
    <template v-if="ok">
      <h1>菜鸟教程</h1>
      <p>学的不仅是技术，更是梦想！</p>
      <p>哈哈哈，打字辛苦啊！！！</p>
    </template>
</div>
```
```js
new Vue({
  el: '#app',
  data: {
    seen: true,
    ok: true
  }
})
```

2、条件判断`v-else`
> 1.`v-if`指令的一个else模块；
```html
<div id="app">
    <div v-if="Math.random() > 0.5">
      Sorry
    </div>
    <div v-else>
      Not sorry
    </div>
</div>
```
```js
new Vue({
  el: '#app'
})
```

3、条件判断`v-else-if`
> 1.在2.1.0新增，用作`v-if`的`v-else`块，可以多次使用；
```html
<div id="app">
    <div v-if="type === 'A'">
      A
    </div>
    <div v-else-if="type === 'B'">
      B
    </div>
    <div v-else-if="type === 'C'">
      C
    </div>
    <div v-else>
      Not A/B/C
    </div>
</div>
```
```js
new Vue({
  el: '#app',
  data: {
    type: 'C'
  }
})
```

4、`v-show`指令
> 1.根据条件展示元素
```html
<div id="app">
    <h1 v-show="ok">Hello!</h1>
</div>
```
```js
new Vue({
  el: '#app',
  data: {
    ok: true
  }
})
```
