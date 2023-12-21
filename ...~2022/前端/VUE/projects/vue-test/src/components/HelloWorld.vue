<template>
  <div class="hello">
    <h1>{{ msg }}</h1>
    <h2>Essential Links</h2>
    <ul>
      <li>
        <a
          href="https://vuejs.org"
          target="_blank"
        >
          Core Docs
        </a>
      </li>
      <li>
        <a
          href="https://forum.vuejs.org"
          target="_blank"
        >
          Forum
        </a>
      </li>
      <li>
        <a
          href="https://chat.vuejs.org"
          target="_blank"
        >
          Community Chat
        </a>
      </li>
      <li>
        <a
          href="https://twitter.com/vuejs"
          target="_blank"
        >
          Twitter
        </a>
      </li>
      <br>
      <li>
        <a
          href="http://vuejs-templates.github.io/webpack/"
          target="_blank"
        >
          Docs for This Template
        </a>
      </li>
    </ul>
    <h2>Ecosystem</h2>
    <ul>
      <li>
        <a
          href="http://router.vuejs.org/"
          target="_blank"
        >
          vue-router
        </a>
      </li>
      <li>
        <a
          href="http://vuex.vuejs.org/"
          target="_blank"
        >
          vuex
        </a>
      </li>
      <li>
        <a
          href="http://vue-loader.vuejs.org/"
          target="_blank"
        >
          vue-loader
        </a>
      </li>
      <li>
        <a
          href="https://github.com/vuejs/awesome-vue"
          target="_blank"
        >
          awesome-vue
        </a>
      </li>
    </ul>
    <p>hello:$route.params.id：{{$route.params.id}}</p>
    <p>hello:id:{{id}}</p>
    <!--<p>{{$route}}</p>-->
    <button @click="go1">go</button>
    <button @click="push2">home</button>
    <button @click="push3">404</button>
  </div>
</template>

<script>
  export default {
    name: 'HelloWorld',
    props:['id'],
    data() {
      return {
        msg: 'Welcome to Your Vue.js App'
      }
    },
    watch:{
      '$route'(to,from){
        console.log('watch-to',to);
        console.log('watch-from',from);
      }
    },
    beforeRouteUpdate(to,from,next){
      console.log('beforeRouteUpdate-to',to);
      console.log('beforeRouteUpdate-from',from);
      console.log('beforeRouteUpdate-next',next);
    },
    methods:{
      go1(){
        this.$router.go(-1);
      },
      push2() {
        this.$router.push('home');
      },
      push3() {
        this.$router.push('404');
      }
    }
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
  h1, h2 {
    font-weight: normal;
  }

  ul {
    list-style-type: none;
    padding: 0;
  }

  li {
    display: inline-block;
    margin: 0 10px;
  }

  a {
    color: #42b983;
  }
</style>
