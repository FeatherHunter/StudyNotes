## Intent
### Intent传递数据为什么要序列化？
启动Activity等涉及到AMS的操作，需要跨进程通信，只能序列化。
=> AMS流程
=> Binder
=> 进程隔离 => 零拷贝 => mmap
## 主线程
判断当前是否是主线程 => Handler
```
Looper.getMainLooper().getThread() == Thread.currentThread()
Looper.getMainLooper() == Looper.myLooper()
```

## Lifecycle
=> AtomicRefrence

## onTrimMemory
1、Activity得到内存情况的通知：
```
onTrimMemory(int level){
    // level 
}
// 
```

2、运行时回调：App在前台
1. 内存低、系统已经在杀死App

3、缓存时回调：App在后台
1. 不同回调标识在LRU缓存中的位置，越靠前越不会被杀死。

## 依赖注入

1. 将实例化的权利交给外界去做。也就是将创建依赖的权利交给外部。外部注入到内部。
1. dagger2是最常见的注入框架。

## Bundle
1、Bundle内部为什么不用hashmap？
1. 内部使用了ArrayMap，适合小数据量。
1. 符合bundle场景。
1. bundle用parcelable序列化。

## Bitmap优化

1、BitmapFactoryOptions
1. 像素格式
1. 采样率。设置2。宽高为1/2，总共1/4。必须是2 4 8

2、Bitmap的三级缓存
> 分配了图片a，a不需要用了。需要图片b，会去和a比较size，小于等于就用a的内存空间。

3、大图加载
> gradle的缓存缩放后的图片。

