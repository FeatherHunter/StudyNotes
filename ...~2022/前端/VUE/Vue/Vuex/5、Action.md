1、Action
> 1.类似于mutation，不同之处在于：
> 2.Action提交的是mutation，而不是直接变更状态；
> 3.Action可以包含任意异步操作。
> 4.Action函数接受一个与store实例具有相同方法和属性的context对象，可以调用`context.commit()`提交一个mutation，或者
> 5.通过`context.state`和`context.getters`啦获取state和getters。
```js
mutations: {
  eventCount(state, playload) {
    state.num += playload.temp;
  }
},
actions: {
  eventAction (context) {
    context.commit('eventCount',{temp:2});
  }
}
```
```js
//需要调用多次commit方法时，用ES的参数解构，简化为
actions: {
  eventAction ({ commit }) {
    commit('eventCount')
  }
}
```

2、分发Action
> 1.Action通过`store.dispatch()`方法触发
```
store.dispatch('eventAction');
```
```js
mutations: {
  eventCount(state, playload) {
    state.num += playload.temp;
  }
},
actions: {
  eventAction (context) {
    context.commit('eventCount',{temp:2});
  }
}
```
```js
//在子组件中调用该方法
methods: {
  eventAction(){
    this.$store.dispatch('eventAction');
  }
}
```
```html
<button @click="eventAction">eventAction 同步Action:</button>
```
> 2.因为mutation必须同步执行，而action不受约束，可以在action内部执行异步操作
```js
mutations: {
  eventCount(state, playload) {
    state.num += playload.temp;
  }
},
actions: {
  //同步action
  eventAction(context) {
    context.commit('eventCount', {temp: 2});
  },
  //异步action,延迟一秒之后进行计算
  eventAsync({commit}) {
    setTimeout(() => {
      commit('eventCount', {temp: 5});
    },1000)
  }
}
```
```js
methods: {
  eventAction(){
    this.$store.dispatch('eventAction');
  },
  eventAsync(){
    this.$store.dispatch('eventAsync');
  }
},
```
```html
<button @click="eventAsync">eventAsync 异步Action:</button>
```

3、载荷分发和对象分发(未用到)
```
// 以载荷形式分发
store.dispatch('incrementAsync', {
  amount: 10
})

// 以对象形式分发
store.dispatch({
  type: 'incrementAsync',
  amount: 10
})
```

4、在组件中分发Action
> 1.方法一：`this.$store.dispatch('xxx')`分发action;
> 2.方法二：使用`mapActions`辅助函数将组件的methods映射为`store.dispatch`调用（需要在根节点注入store实例）
```js
import { mapActions } from 'vuex'

export default {
  // ...
  methods: {
    ...mapActions([
      'increment', // 将 `this.increment()` 映射为 `this.$store.dispatch('increment')`

      // `mapActions` 也支持载荷：
      'incrementBy' // 将 `this.incrementBy(amount)` 映射为 `this.$store.dispatch('incrementBy', amount)`
    ]),
    ...mapActions({
      add: 'increment' // 将 `this.add()` 映射为 `this.$store.dispatch('increment')`
    })
  }
}
```

5、组合Action
> 1.Action通常是异步的，通过组合Action来解决多个异步action何时结束的问题；
> 2.`store.dispatch`可以处理被触发的action的处理函数返回的Promise，并且`store.dispatch`仍旧返回Promise；
