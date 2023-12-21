// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import VueRouter from 'vue-router'
import Vuex from 'vuex'
import routes from './routes'
import store from'./vuex/store'


// Vue.config.productionTip = false
Vue.use(ElementUI);
Vue.use(VueRouter);
Vue.use(Vuex);

//创建router实例
const router = new VueRouter({
  mode: 'history',
  routes,
  store,
  scrollBehavior(to, from, savePosition) {
    console.log(to);
    console.log(from);
    console.log(savePosition);
    return {x: 10, y: 10};
  }
});




// router.beforeEach((to, from, next) => {
//   //NProgress.start();
//   if (to.path === '/login') {
//     // sessionStorage.removeItem('user');
//     console.log('path不是login');
//   }
//   if (to.path !== '/login') {
//     next({ path: '/login' });
//     console.log('path是login');
//   } else {
//     next()
//   }
// });

/* eslint-disable no-new */
new Vue({
  // el: '#app',
  router,
  store,
  // components: { App },
  // template: '<App/>'
  render: h => h(App)
}).$mount("#app");

