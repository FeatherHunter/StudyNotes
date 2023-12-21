转载请注明链接: https://blog.csdn.net/feather_wch/article/details/88670381

# gradle compile project 中使用exclude去除重复的依赖

版本号:2019-03-19(18:30)

---

@[toc]
错误的使用方式: 会报错
```
compile project(':uisdk:Library:facebook') {
    exclude group: 'com.android.support', module: 'appcompat-v7'
}
```


## 正确的使用: 加上括号
```
// project用括号包裹住
compile (project(':uisdk:Library:facebook')) {
    exclude group: 'com.android.support', module: 'appcompat-v7'
}
```

## exclude失效问题

遇到了exclude无效的问题，使用全局移除就正常了:
```
// 排除掉V4包中引入的android.arch.lifecycle:runtime导致构建失败
configurations.all {
    exclude group: 'android.arch.lifecycle', module: 'runtime'
}
```
