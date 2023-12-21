1、循环指令`v-for`
> 1.`v-for`需要以site in sites形式的特殊语法；
> 2.site是数组元素迭代的别名；
> 3.sites是源数据数组。
> 4.`v-for`可以绑定数据来渲染一个列表；
```html
<div id="app">
  <ul>
    <template v-for="site in sites">
      <li>{{ site.name }}</li>
      <li>--------------</li>
    </template>
  </ul>
</div>
```
```js
new Vue({
  el: '#app',
  data: {
    sites: [
      { name: 'Runoob' },
      { name: 'Google' },
      { name: 'Taobao' }
    ]
  }
})
```


2、`v-for`迭代对象
> 1.通过对象的属性来迭代数据；
```html
<div id="app">
  <ul>
    <li v-for="value in object">
    {{ value }}
    </li>
  </ul>
</div>
```
```js
new Vue({
  el: '#app',
  data: {
    object: {
      name: '菜鸟教程',
      url: 'http://www.runoob.com',
      slogan: '学的不仅是技术，更是梦想！'
    }
  }
})
```
> 2.提供第二个参数为键名
```html
<div id="app">
  <ul>
    <li v-for="(value, key) in object">    //这个顺序要注意
    {{ key }} : {{ value }}
    </li>
  </ul>
</div>
```
> 3.提供第三个参数为索引
```html
<div id="app">
  <ul>
    <li v-for="(value, key, index) in object">
     {{ index }}. {{ key }} : {{ value }}
    </li>
  </ul>
</div>
```

3、`v-for`迭代整数
```html
<div id="app">
  <ul>
    <li v-for="n in 10">
     {{ n }}
    </li>
  </ul>
</div>
```
```js
new Vue({
  el: '#app'
})
```
