import HelloWorld from '@/components/HelloWorld'
import Login from './views/Login'
import Home from './views/Home'
import Table from './views/nav1/Table'
import Form from './views/nav1/Form'
import Vuex from './views/Page5'

let routes = [
  // {
  //   path: '/',
  //   name: 'Login',
  //   components: {
  //     default: Login,
  //     a: HelloWorld,
  //     b: Home
  //   },
  // },
  {
    path: '/',
    component: Login,
    hidden: true,
  },
  // {
  //   path: '/:id',
  //   component: Home,
  //   //别名，可以用来访问
  //   alias: '/h',
  //   // props: true,
  // },
  {
    path: '/home',
    name: '导航一',
    component: Home,
    children: [
      {
        //'/home'路径下渲染Home组件
        path: '/table',
        name: 'Table',
        component: Table,
      },
      {
        path: '',
        component: HelloWorld,
        hidden: true,
      },
      {
        path: '/form',
        name: 'Form',
        component: Form
      }
      // {
      //   path: 'home',
      //   name: '',
      //   components: {
      //     default: Home,
      //     helper: HelloWorld
      //   },
      //   alias: '/b',
      // }
    ]
  },
  {
    path: '/home',
    name: '导航二',
    component: Home,
    children: [
      {
        //'/home'路径下渲染Home组件
        path: '/page3',
        name: 'Table2',
        component: Table,
      },
      {
        path: '/page4',
        name: 'Form2',
        component: Form
      }
      // {
      //   path: 'home',
      //   name: '',
      //   components: {
      //     default: Home,
      //     helper: HelloWorld
      //   },
      //   alias: '/b',
      // }
    ]
  },
  {
    path: '/home',
    name: '导航三',
    component: Home,
    leaf: true,    //只有一个节点，没有子节点
    children: [
      {
      path: '/page5',
      name:'Page5',
      component: Vuex,
    }
    ]
  },
  {
    path: '/404',
    name: '404',
    component: Home,
    hidden: true,
  },
  // {
  //   path: '/hello',
  //   name: 'HelloWorld',
  //   component: HelloWorld
  // },
  // {
  //   path: '/home-*',
  //   name: '',
  //   component: Home
  // },
  // {
  //   path: '*',
  //   redirect: {path: '/404'}
  // },
  {
    path: '*',
    redirect: {name: '404'},
    hidden: true,
  }

];

export default routes;
