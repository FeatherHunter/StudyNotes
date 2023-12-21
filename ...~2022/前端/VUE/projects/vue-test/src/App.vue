<template>
  <div id="app">
    <!--<button @click="push1">login</button>-->
    <router-view/>
    <!--<router-view name="a"/>-->
    <!--<router-view name="b"/>-->
  </div>
</template>

<script>
export default {
  name: 'app',
  components:{

  },
  methods:{
    push1(){
      this.$router.push('/login');
    }
  }
}
</script>

<style>
  body{
    padding: 0;
    margin: 0;
  }
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  /*text-align: center;*/
  color: #2c3e50;
  margin-top: 10px;
}
</style>
