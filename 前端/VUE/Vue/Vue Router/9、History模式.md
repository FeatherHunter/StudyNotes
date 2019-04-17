1、hash模式
> 1.`vue-router`默认模式是hash模式；
> 2.hash模式，使用URL的hash来模拟一个完整的URL，当URL改变时，页面不会重新加载；
```
hash模式下的URL——"http://localhost:8080/#/"
```

2、History模式
> 1.URL比较正常，也好看
```
history模式下的URL——"http://localhost:8080/"
```
> 2.方法：
```
//创建router实例
const router = new Router({
  mode: 'history',               //定义history模式
  routes
});
```
