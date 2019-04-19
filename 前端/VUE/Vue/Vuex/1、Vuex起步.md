1、什么是Vuex
> 1.Vuex是一个专门为Vue.js应用程序开发的状态管理模式。
> 2.它采用集中式存储管理应用的所有组件的状态，并以相应的规则保证状态以一种可预测的方式发生变化。

2、什么是"状态管理模式"
> 1.包括三个部分:
> 1. `state`：驱动应用的数据源；
> 2. `view`：以声明方式将state映射到视图；
> 3. `actions`：响应在view上的用户输入导致的状态变化；
> 4. 这三个部分组成了单向数据流：
```
actions--->state--->view--->action
```

3、当应用遇到多个组件共享状态时，单向数据流很容易破坏
> 1.原因：
> 1. 多个视图依赖同一个状态；
> 2. 来自不同视图的行为需要变更同一状态；

4、什么时候使用Vuex
> 1.Vuex可以帮助我们管理共享状态，并附带了更多的概念和框架。
> 2.如果不打算开发大型单页应用，使用Vuex可能是冗余的；
> 3.如果应用够简单，最好不好使用Vuex。

5、Vuex应用核心
> 1.核心：store(仓库)。"store基本是一个容器，它包含着你的应用中大部分的状态(state)"。
> 2.Vuex和单纯的全局对象的不同点：
> 1. Vuex的状态存储是响应式的。当Vue组件从store中读取状态的时候，若store中的状态发生变化，那么相应的组件也会得到高效的更新；
> 2. 不能直接的改变store中的状态。改变store状态的唯一途径就是显示的提交(commit)mutation。这样，使得我们可以方便的跟踪每一个状态的变化。

6、项目中使用Vuex的具体步骤
> 1.在项目中使用`cnpm install vuex --save`安装vuex模块；
> 1.在项目的，src文件夹中，新建一个文件夹（命名为store），用来存放store的操作；
> 2.在store文件夹下新建一个store.js文件，主要进行store的初始配置；
```
import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex);

let store = new Vuex.Store({
    state: {
    num: 5,
  },
});

export default store;
```
> 3.在项目起始配置文件main.js中，新建的router实例中引入之前创建的store实例
```
import Vue from 'vue'
import App from './App'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import VueRouter from 'vue-router'
import routes from './routes'
import Vuex from 'vuex'
import store from'./store/store'     //引入store实例

Vue.use(ElementUI);
Vue.use(VueRouter);
Vue.use(Vuex);

//创建router实例
const router = new VueRouter({
  mode: 'history',
  routes,
  store,                  //引入store实例，以便于在以后的各个子组件中，都能使用this.$store来访问store实例
});
```
> 4.在子组件中访问store实例中的值，要在`computed`属性中；
```
<template>
  <div>
    //这里会输出"5"
    <p>state:{{count}}</p>
  </div>
</template>
```
```
export default {
  name: "Page5",
  computed: {
    count() {
      return this.$store.state.num;
    }
}
```
