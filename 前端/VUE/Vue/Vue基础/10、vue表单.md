1、表单应用
> 1.使用`v-model`实现双向数据绑定；

2、输入框
```html
<div id="app">
  <p>input 元素：</p>
  <input v-model="message" placeholder="编辑我……">
  <p>消息是: {{ message }}</p>

  <p>textarea 元素：</p>
  <p style="white-space: pre">{{ message2 }}</p>
  <textarea v-model="message2" placeholder="多行文本输入……"></textarea>
</div>
```
```js
new Vue({
  el: '#app',
  data: {
    message: 'Runoob',
	  message2: '菜鸟教程\r\nhttp://www.runoob.com'
  }
})
```

3、复选框
```html
<div id="app">
  <p>单个复选框：</p>
  <input type="checkbox" id="checkbox" v-model="checked">
  <label for="checkbox">{{ checked }}</label>

  <p>多个复选框：</p>
  <input type="checkbox" id="runoob" value="Runoob" v-model="checkedNames">
  <label for="runoob">Runoob</label>
  <input type="checkbox" id="google" value="Google" v-model="checkedNames">
  <label for="google">Google</label>
  <input type="checkbox" id="taobao" value="Taobao" v-model="checkedNames">
  <label for="taobao">taobao</label>
  <br>
  <span>选择的值为: {{ checkedNames }}</span>
</div>
```
```js
new Vue({
  el: '#app',
  data: {
	checked : false,
    checkedNames: []
  }
})
```

4、单选按钮
```html
<div id="app">
  <input type="radio" id="runoob" value="Runoob" v-model="picked">
  <label for="runoob">Runoob</label>
  <br>
  <input type="radio" id="google" value="Google" v-model="picked">
  <label for="google">Google</label>
  <br>
  <span>选中值为: {{ picked }}</span>
</div>
```
```js
new Vue({
  el: '#app',
  data: {
	picked : 'Runoob'
  }
})
```

5、select列表
```html
<div id="app">
  <select v-model="selected" name="fruit">
    <option value="">选择一个网站</option>
    <option value="www.runoob.com">Runoob</option>
    <option value="www.google.com">Google</option>
  </select>

  <div id="output">
      选择的网站是: {{selected}}
  </div>
</div>
```
```js
new Vue({
  el: '#app',
  data: {
	selected: ''
  }
})
```

6、`v-model`中的修饰符
> 1.`.lazy`：转变为在change事件中同步；
```html
<input v-model.lazy="msg" >
```
> 2.`.number`：将输入的值Number值（如果原值的转换结果为NaN，则返回原值）
```html
<input v-model.number="age" type="number">
```
> 3.`.trim`：自动过滤用户输入的首尾空格；
```html
<input v-model.trim="msg">
```
