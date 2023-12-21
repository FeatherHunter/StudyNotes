1、Mutation
> 1.用途：更新vuex中的store里面的state；
> 2.Mutation非常类似于事件：每个mutation都有一个字符串的事件类型(type)和一个回调函数(handler)。
> 3.这个回调函数就是进行状态更改的地方，并且会接受state作为第一个参数。
```
let store = new Vuex.Store({
  state: {
    num: 5,
  mutations: {
    eventCount(state) {
      //变更状态
      state.num -= 1;
    }
  },
});
```
> 4.不能直接调用`mutation.handler`。这个选项更像是一个事件注册。
> 5.当触发一个`eventCount`的mutation时，调用此函数；
> 6.唤醒mutation handler，需要以相应的type调用`store.commit()`方法；
```
//在组件的methods方法中，唤醒handler
methods: {
  eventCount() {
    this.$store.commit('eventCount');
  }
}
```

2、提交载荷(Playload)
> 1.定义：可以向`store.commit`传入额外的参数，即为mutation的载荷(playload)；
```js
mutations: {
  eventCount(state, temp) {
    state.num += temp;
  }
},
```
```js
methods: {
  eventCount() {
    this.$store.commit('eventCount',5);
  }
}
```
```html
<button @click="eventCount">eventCount mutation:</button>
```
> 2.在大多数情况下，载荷应该是一个对象，这样可以包含多个字段并且记录的mutation会更易读
```js
mutations: {
  eventCount(state, playload) {
    state.num += playload.temp;
  }
},
```
```js
methods: {
  eventCount() {
    this.$store.commit('eventCount',{temp:10});
  }
}
```

3、对象风格的提交方式
> 1.提交mutation的另一种方式，直接使用包含type属性的对象；
```js
methods: {
  eventCount() {
    this.$store.commit({
      type:'eventCount',
      temp:5
    })
  }
}
```

4、Mutation需要遵循Vue的响应规则
> 1.Vuex中的状态是响应性的，当我们变更状态时，监视状态的Vue组件也会自动更新。
> 2.这就意味着，Vuex中的mutation需要遵循一些规则：
> 3.最好提前在你的store中初始化好所有所需属性。
> 4.当需要在对象上添加新属性时，使用：
> 1. `Vue.set(obj,'newProp',123)`，或者
> 2. 以新对象替换老对象。例如，
```
state.obj = { ...state.obj, newProp:123 }
```

5、使用常量代替Mutation事件类型
> 1.使用常量替代mutation事件类型在各种Flux实现中是很常见的模式。
> 2.这样，可以使得linter之类的工具发挥作用；
> 3.同时，把这些常量放在单独的文件中可以让你的代码合作者对整个app包含的mutation一目了然。
```js
// mutation-types.js
export const SOME_MUTATION = 'SOME_MUTATION'
```
```js
// store.js
import Vuex from 'vuex'
import { SOME_MUTATION } from './mutation-types'

const store = new Vuex.Store({
  state: { ... },
  mutations: {
    // 我们可以使用 ES2015 风格的计算属性命名功能来使用一个常量作为函数名
    [SOME_MUTATION] (state) {
      // mutate state
    }
  }
})
```

6、Mutation必须是同步函数
>

7、提交Mutation
> 1.在组件中提交`this.$store.commit('')`；
> 2.使用`mapMutations`辅助函数将组件中的methods映射为`store.commit`调用
```js
import { mapMutations } from 'vuex'

export default {
  // ...
  methods: {
    ...mapMutations([
      'increment', // 将 `this.increment()` 映射为 `this.$store.commit('increment')`

      // `mapMutations` 也支持载荷：
      'incrementBy' // 将 `this.incrementBy(amount)` 映射为 `this.$store.commit('incrementBy', amount)`
    ]),
    ...mapMutations({
      add: 'increment' // 将 `this.add()` 映射为 `this.$store.commit('increment')`
    })
  }
}
```
> 3.
