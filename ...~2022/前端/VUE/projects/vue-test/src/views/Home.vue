<template>
  <div class="container">
    <!--<p>$router.options.routes:{{$router.options.routes}}</p>-->
    <el-row class="header">
      <!--<div class="grid-content bg-purple-dark" style="height:100px;background: yellow;">header</div>-->
      <el-col :span="10" class="logo" :class="collapsed?'logo-collapse-width':'logo-width'">
        {{collapsed?'':sysName}}
      </el-col>
      <el-col :span="10">
        <div class="tools" @click.prevent="collapse">
          <i class="fa fa-align-justify"></i>
        </div>
      </el-col>
      <el-col :span="4" class="userinfo">
        <el-dropdown>
            <span class="el-dropdown-link userinfo-inner">
              顾某某<img src="../assets/userlogo.jpg"/></span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item>我的消息</el-dropdown-item>
            <el-dropdown-item>设置</el-dropdown-item>
            <el-dropdown-item divided @click.native="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </el-col>
    </el-row>
    <el-row :span="24" class="main">
      <el-col :span="4">
        <aside :class="collapsed?'menu-collapsed':'menu-expanded'">
          <!--<div class="grid-content bg-purple-dark" style="height:100px;background:gray;"></div>-->
          <el-menu
            default-active="1"
            class="el-menu-vertical-demo"
            @open="handleOpen"
            @close="handleClose"
            style="text-align: left;">
            <!--路由里面，找到所有导航栏的路由，过滤其他的路由设置-->
            <template v-for="(item,index) in $router.options.routes" v-if="!item.hidden">
              <!--过滤导航下面有子栏目的，显示一级和二级导航-->
              <el-submenu :index="index+''" v-if="!item.leaf">
                <template slot="title">
                  <i class="el-icon-location"></i>
                  <span>{{item.name}}</span>
                </template>
                <el-menu-item :index="child.path" :key="child.path" v-for="child in item.children" v-if="!child.hidden"
                              @click="$router.push(child.path)">{{child.name}}
                </el-menu-item>
              </el-submenu>
              <!--导航下没有子栏目，只有一级导航-->
              <el-menu-item :index="index" v-else>
                <i class="el-icon-setting"></i>
                <span slot="title" @click="$router.push(item.children[0].path)">{{item.children[0].name}}</span>
              </el-menu-item>
            </template>
          </el-menu>
        </aside>
      </el-col>
      <!--<div class="grid-content bg-purple-dark" style="height:100px;background:pink;"></div>-->
      <el-col :span="20"><section class="content-container">
        <div class="grid-content bg-purple-light">
          <el-col :span="24" class="breadcrumb-container">
            <strong class="title">{{$route.name}}</strong>
            <el-breadcrumb separator="/" class="breadcrumb-inner">
              <el-breadcrumb-item v-for="item in $route.matched" :key="item.path">
                {{item.name}}
              </el-breadcrumb-item>
            </el-breadcrumb>
          </el-col>
          <el-col :span="24" class="content-wrapper">
            <router-view></router-view>
          </el-col>
        </div>
      </section></el-col>


    </el-row>
  </div>
</template>

<script>
  export default {
    name: "Home",
    data() {
      return {
        sysName: 'VUEADMIN',
        collapsed: false,
      };
    },
    props: ['id'],
    methods: {
      go1() {
        this.$router.go(-1);
      },
      handleOpen(key, keyPath) {
        console.log(key, keyPath);
      },
      handleClose(key, keyPath) {
        console.log(key, keyPath);
      },
      //折叠导航栏
      collapse: function () {
        this.collapsed = !this.collapsed;
      },
      //退出登录
      logout: {}
    }
  }
</script>

<style scoped>
  .container {
    width: 100%;
  }

  /*header*/
  .header {
    height: 60px;
    line-height: 60px;
    color: white;
    background: #20a0ff;
    /*padding-left: 20px;*/
  }

  .logo {
    height: 60px;
    font-size: 22px;
    border-right: 1px solid rgba(238, 241, 146, 0.3);
  }

  .logo-collapse-width {
    width: 60px;
  }

  .logo-width {
    width: 230px;
  }

  .tools {
    width: 16px;
    height: 60px;
    line-height: 60px;
    cursor: pointer;
    /*margin-left: 14px;*/
    padding: 0 23px;

  }

  .tools:hover {
    transform: scale(1.2);

  }

  .fa {
    display: inline-block;
    width: 16px;
    height: 16px;
    background: url('../assets/tool.png');
  }

  .userinfo {
    float: right;
    text-align: right;
    padding-right: 35px;
    height: 60px;
    line-height: 60px;
  }

  .userinfo-inner {
    cursor: pointer;
    color: #fff;
  }

  img {
    width: 40px;
    height: 40px;
    border-radius: 20px;
    float: right;
    margin: 10px 0 10px 10px;
  }

  /*main*/
  .content-container {
    padding: 20px;
    /*float: left;*/
  }

  .title {
    color: #475669;
    float: left;
  }

  .breadcrumb-inner {
    float: right;
  }

  .bg-purple-light {
    background: #e5e9f2;
  }
</style>
