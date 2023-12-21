import Vue from 'vue'
import Vuex from 'vuex'


Vue.use(Vuex);

let store = new Vuex.Store({
  state: {
    num: 5,
  },
  getters: {
    changeCount(state) {
      return state.num++;
    }
  },
  mutations: {
    eventCount(state) {
      state.num -= 1;
    }
  },
  // actions:{
  //   addCount(context){
  //     context.commit('increment');
  //   }
  // }
});


// store.commit('increment');

export default store;
