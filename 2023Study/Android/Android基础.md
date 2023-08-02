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