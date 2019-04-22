<template>
  <div>
    <h1>{{msg}}</h1>
    <!--<p>id:{{id}}</p>-->
    <p>$route.params.id:{{$route.params.id}}</p>
    <!--<p>$route.path：{{$route.path}}</p>-->
    <!--<p>$route.params：{{$route.params}}</p>-->
    <!--<p>$route.query：{{$route.query}}</p>-->
    <!--<p>$route.hash：{{$route.hash}}</p>-->
    <!--<p>$route.fullPath：{{$route.fullPath}}</p>-->
    <!--<p>$route.matched：{{$route.matched}}</p>-->
    <!--<p>$route.name：{{$route.name}}</p>-->
    <!--<p>$route.meta：{{$route.meta}}</p>-->

    <p>$router.options.routes：{{$router.options.routes}}</p>
    <button @click="replace1">replace</button>
    <button @click="push1">helloWorld</button>
    <button @click="push2">home</button>
    <button @click="push3">aliasHome</button>
    <button @click="push4">aliasHello</button>
    <!--<button @click="push2">Home</button>-->
    <!--<router-link :to="home">homeLinkBind</router-link>-->
    <!--<router-link :to="{path:'home'}">homeLinkPath</router-link>-->
    <router-link to="home" tag="li">
      <a>homeLinkLiName</a>
    </router-link>
    <router-view class="default"/>
    <router-view name="helper" class="helper"/>
  </div>
</template>


<script>
  export default {
    name: "Login",
    data() {
      return {
        msg: '你好',
        home: 'home'
      }
    },
    methods: {
      replace1() {
        this.$router.replace('home');
      },
      push1() {
        this.$router.push('hello');
      },
      push2() {
        this.$router.push('home');
      },
      push3(){
        this.$router.push('/h');
      },
      push4(){
        this.$router.push('/b');
      }
    }
  }
</script>

<style scoped>
  .default {
    width: 300px;
    background: pink;
  }

  .helper {
    width: 200px;
    background: gray;
  }
</style>
