转载请注明链接: https://blog.csdn.net/feather_wch/article/details/88744255

> Android paging library是一种分页库。
> 1. 按需加载数据进行展示，避免网络流量和系统资源的损耗

#  Paging分页库的基本使用

版本号:2019-03-22(13:30)

---

[toc]
## 简介

1、分页加载的前世今生
> 1. 分页加载共有两种模式
> 1. 一种是传统的上拉加载更多的分页效果
> 1. 一种是无限滚动的分页效果

2、无限滚动的这种无感知的分页效果无疑是最好的
> Paging library就是这种分页库

1、Paging library 的核心组件是`PagedList`
> 1. 能分页加载app需要的数据(先加载一部分)
> 1. 如果有任何加载的数据变化，一个新的`Pagedlist对象`会更新到`LiveData或者RxJava2依赖的对象`中


## 参考资料

1. [Android官方架构组件Paging：分页库的设计美学](https://blog.csdn.net/mq2553299/article/details/80788692)
