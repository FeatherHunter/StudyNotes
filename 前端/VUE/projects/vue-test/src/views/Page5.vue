<template>
  <div>
    <p>state:{{count}}</p>
    <p>getter:{{getterNum}}</p>
    <button @click="eventCount">mutations:{{}}</button>
  </div>
</template>

<script>
  export default {
    name: "Page5",
    // methods: {
    //   increment() {
    //     this.count++;
    //   }
    // },
    computed: {
      count() {
        return this.$store.state.num;
      },
      getterNum(){
        return this.$store.getters.changeCount;
      }
    },
    methods: {
      eventCount() {
        this.$store.commit('eventCount',{num});
        console.log(this.$store.state.num);
      }
    }
  }
</script>

<style scoped>

</style>
