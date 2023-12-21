1、单一状态树
> 1.定义用一个对象就包含了全部的应用层级状态；至此它便作为一个“唯一数据源 (SSOT)”而存在。
> 2.这也意味着，每个应用将仅仅包含一个 store 实例。
> 3.单一状态树让我们能够直接地定位任一特定的状态片段，在调试的过程中也能轻易地取得整个当前应用状态的快照。

2、在 Vue 组件中获得 Vuex 状态
> 1.Vuex 的状态存储是响应式的，
> 2.从 store 实例中读取状态最简单的方法就是在计算属性中返回某个状态
```
// 创建一个 Counter 组件，子组件中返回store实例的state里面的值
const Counter = {
  template: `<div>{{ count }}</div>`,
  computed: {
    count () {
      return store.state.count
    }
  }
}
```
> 3.每当 store.state.count 变化的时候, 都会重新求取计算属性，并且触发更新相关联的 DOM；
> 4.但是，这种模式导致组件依赖全局状态单例。在模块化的构建系统中，在每个需要使用 state 的组件中需要频繁地导入，并且在测试组件时需要模拟状态；
> 5.Vuex 通过 store 选项，提供了一种机制将状态从根组件“注入”到每一个子组件中（需调用 Vue.use(Vuex)）；
```
const app = new Vue({
  el: '#app',
  // 把 store 对象提供给 “store” 选项，这可以把 store 的实例注入所有的子组件，在main.js文件中配置
  store
})
```
> 6.通过在根实例中注册 store 选项，该 store 实例会注入到根组件下的所有子组件中，且子组件能通过 this.$store 访问到。
```
//在子组件中通过this.$store访问store实例
export default {
  computed: {
    count () {
      return this.$store.state.count;
    }
  }
}
```

3、mapState 辅助函数
> 1.当一个组件需要获取多个状态时候，将这些状态都声明为计算属性会有些重复和冗余；
> 2.为了解决这个问题，我们可以使用 mapState 辅助函数帮助我们生成计算属性，让你少按几次键；
```
// 在单独构建的版本中辅助函数为 Vuex.mapState
import { mapState } from 'vuex'

export default {
  // ...
  computed: mapState({
    // 箭头函数可使代码更简练
    count: state => state.count,

    // 传字符串参数 'count' 等同于 `state => state.count`
    countAlias: 'count',

    // 为了能够使用 `this` 获取局部状态，必须使用常规函数
    countPlusLocalState (state) {
      return state.count + this.localCount
    }
  })
}
```
> 3.当映射的计算属性的名称与 state 的子节点名称相同时，我们也可以给 mapState 传一个字符串数组；
```
computed: mapState([
  // 映射 this.count 为 store.state.count
  'count'
])
```

4、对象展开运算符
> 1.mapState 函数返回的是一个对象；
> 2.为了将它与局部计算属性混合使用，需要使用一个工具函数将多个对象合并为一个，以使我们可以将最终对象传给 computed 属性。
> 3.对象展开符：`...`，用三个点号表示；
```
computed: {
  localComputed () { /* ... */ },
  // 使用对象展开运算符将此对象混入到外部对象中
  ...mapState({
    // ...
  })
}
```

5、组件仍然保有局部状态
> 1.使用 Vuex 并不意味着你需要将所有的状态放入 Vuex。
> 2.虽然将所有的状态放到 Vuex 会使状态变化更显式和易调试，但也会使代码变得冗长和不直观；
> 3.如果有些状态严格属于单个组件，最好还是作为组件的局部状态；
> 4.应该根据你的应用开发需要进行权衡和确定。
