1、getter
> 1.vuex允许在'store'实例中定义getter(类似于store的计算属性)；
> 2.就像计算属性一样，getter的返回值会根据它的依赖被缓存起来，且只有当它的依赖值发生改变时，才会被重新计算；
```js
let store = new Vuex.Store({
  state: {
    num: 5,
  },
  getters: {
    changeNum(state) {
      return state.num++;
    }
  },
});
```

2、访问getter——属性访问
> 1.以属性的形式访问值——`store.getters`对象；
```js
export default {
  name: "Page5",

  computed: {
    getterNum(){
      return this.$store.getters.changeNum;
    }
  },
}
```

3、getter中传参——方法访问
> 1.通过让getter返回一个函数，来实现给getter传参；
> 2.在对store中的数组进行查询时非常有用。
```js
let store = new Vuex.Store({
  state: {
    num: 5,
    todos: [
      {
        id: 1, text: 'hello'
      },
      {
        id: 2, text: 'world'
      }
    ]
  },
  getters: {
    changeCount(state) {
      return state.num++;
    },
    getId: state => id => {
      return state.todos.find(todo => todo.id === id)
    }
  }
});
```
```js
computed: {
  count() {
    return this.$store.state.num;
  },
  getterNum(){
    return this.$store.getters.changeCount;
  },
  getId(){
    return this.$store.getters.getId(2);  //'{id: 2, text: 'world'}'
  }
},
```

4、传入第二个参数getters——指的是store中的getter中的所有方法
```js
state: {
  num: 5,
},
getters: {
  changeCount(state) {
    return state.num++;
  },
  getCounts(state, getters) {
    //这里的getters参数指向整个getters对象
    return state.num + getters.changeCount;
  }
},
```
```js
computed: {
  count() {
    return this.$store.state.num;              //6
  },
  getterNum(){
    return this.$store.getters.changeCount;    //5
  },
  getterNums(){
    return this.$store.getters.getCounts;      //11
  },
},
```

5、mapGetters辅助函数
> 1.`mapGetters`辅助函数仅仅是将store中的getter映射到局部计算属性；
```js
import {mapGetters} from 'vuex'

computed: {
  ...mapGetters({
    getMapId:'getId',
  })
},
```
```html
<p>getMapId:{{getMapId(1)}}</p>      //'{ "id": 1, "text": "hello" }'
```
