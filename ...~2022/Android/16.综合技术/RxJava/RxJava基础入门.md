转载请注明链接: https://blog.csdn.net/feather_wch/article/details/88090332

# RxJava基础入门

版本号:2019-03-03(21:01)

---

[toc]
## 依赖添加

1、在线添加依赖
```java
compile 'io.reactivex.rxjava2:rxandroid:2.1.1'
compile 'io.reactivex.rxjava2:rxjava:2.2.7'
```


2、离线依赖
```java
compile files('libs/rxjava-2.2.0.jar')
compile files('libs/RxAndroid.jar')
```

## RxJava的定义

1、RxJava的定义
> `RxJava 是一个在 Java VM 上使用可观测的序列来组成异步的、基于事件的程序的库`


2、RxJava 是一个 `基于事件流`、`实现异步操作`的库

3、Android中RxJava类似于`AsyncTask、Handler`的作用

## RxJava的原理

1、RxJava的原理基于`一种扩展的观察者模式`

2、流程总结: `被观察者(Observable)`通过`订阅(Subscribe)`，`按顺序发送事件` 给`观察者(Observer)`，`观察者（Observer）`按顺序接收事件，并且作出对应的响应动作。

### 四个角色

#### 被观察者(Observable)

3、被观察者(Observable)用于：产生事件

#### 观察者(Observer)

4、观察者(Observer)用于：接收事件，并给出响应动作

#### 订阅(Subscribe)

5、订阅(Subscribe)用于：连接被观察者和观察者

#### 事件(Event)

6、事件(Event)用于：被观察者和观察者沟通的载体


## 使用步骤

### 创建被观察者并产生事件

1、create()创建被观察者
```java
// 1、创建被观察者Obervavle对象
// 2、create()方法用于创造事件序列
Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
    // 3、定义需要发送的事件
    @Override
    public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
        e.onNext(1);
        e.onNext(2);
        e.onNext(3);
        e.onComplete();
        e.onNext(4);
    }
});
```
> 1. `onNext(1)`用于发送事件
> 1. `onComplete()`会让`观察者`不再接收到`事件`。但是事件会继续发送，比如`onNext(4)`还是会发送出去。

2、just()创建被观察者
> 1. 不仅会构建被观察者，还会依次将参数的事件发送出去。
```java
/**=====================================
 * 方法1：just(T...)：直接将传入的参数依次发送出来
 *         // 将会依次调用：
 *         // onNext("A");
 *         // onNext("B");
 *         // onNext("C");
 *         // onCompleted();
 *======================================*/
Observable justObservable = Observable.just("A", "B", "C");
```

3、from()创造被观察者
> `from()方法`在`RxJava2中没找到，不知道是不是移除了。`
```java
/**==============================================
 * 方法2：from(T[]) / from(Iterable<? extends T>) : 将传入的数组 / Iterable 拆分成具体对象后，依次发送出来
 *         // 将会依次调用：
 *         // onNext("A");
 *         // onNext("B");
 *         // onNext("C");
 *         // onCompleted();
 *===========================================*/
String[] words = {"A", "B", "C"};
Observable fromObservable = Observable.from(words);
```

#### ObservableEmitter

4、ObservableEmitter是什么?
```java
/**================================================
 * 1. ObservableEmitter继承自Emitter, 是事件发射器
 * 2. 用于定义需要发送的事件
 * 3. 并且用于向观察者发送事件
 * // ObservableEmitter.java
 *===============================================*/
public interface ObservableEmitter<T> extends Emitter<T> {
    void setDisposable(@Nullable Disposable var1);

    void setCancellable(@Nullable Cancellable var1);

    boolean isDisposed();

    @NonNull
    ObservableEmitter<T> serialize();

    boolean tryOnError(@NonNull Throwable var1);
}
```

### 创建观察者并定义响应事件的行为

#### 事件(Next、Complete、Error)

5、Next事件
> 1. `被观察者`可以`发送无限个Next事件`
> 1. `观察者`可以`接收无限个Next事件`
> 1. 接收到Next事件后，回调`onNext()方法`

6、Complete事件
> 1. 该事件表示：事件队列完结。
> 1. `被观察者`在`onComplete()`后发送的事件依然会继续发送
> 1. 在事件队列完结后，`观察者`不再继续接收任何事件`(不再回调onNext)`

7、Error事件
> 1. 该事件表示：事件队列异常。
> 1. `被观察者`在`onError()`后发送的事件依然会继续发送。
> 1. 出现异常事件后，`观察者`不再继续接收任何事件`(不再回调onNext)`

8、在事件序列中Complete事件和Next事件两者互斥

#### Observer接口
9、通过Observer接口，创建观察者。
```java
/**===========================
 * 创建观察者 （Observer ）对象
 *   方式1：采用Observer 接口.
 *===========================*/
Observer<Integer> observer = new Observer<Integer>()
{
    // 1. 订阅事件
    @Override
    public void onSubscribe(Disposable d)
    {
        Log.d(TAG, "开始采用subscribe连接");
    }

    // 2. 普通事件
    @Override
    public void onNext(Integer value)
    {
        Log.d(TAG, "对Next事件作出响应" + value);
    }

    // 3. 事件队列异常
    @Override
    public void onError(Throwable e)
    {
        Log.d(TAG, "对Error事件作出响应");
    }

    // 4. 事件队列完结
    @Override
    public void onComplete()
    {
        Log.d(TAG, "对Complete事件作出响应");
    }
};
```

#### Subscriber抽象类

10、通过Subscriber抽象类，创建观察者。
```java
/**===========================
 * 创建观察者 （Observer ）对象
 *   方式2：采用Subscriber 抽象类.
 *===========================*/
Subscriber<Integer> subscriber = new Subscriber<Integer>()
{
    // 1. 普通事件
    @Override
    public void onNext(Integer value)
    {
        Log.d(TAG, "对Next事件作出响应" + value);
    }

    // 2. 事件队列异常
    @Override
    public void onError(Throwable e)
    {
        Log.d(TAG, "对Error事件作出响应");
    }

    // 3. 事件队列完结
    @Override
    public void onCompleted()
    {
        Log.d(TAG, "对Complete事件作出响应");
    }
};
```

#### 两者的区别

11、Subscriber抽象类与Observer接口的区别
> 1. 相同点：使用方式完全一致
>     * 实质上，订阅过程中，Observer总是会先被转换成Subscriber再使用
> 1. 不同点：Subscriber抽象类对 Observer 接口进行了扩展，新增了两个方法：
>     1. onStart()：在还未响应事件前调用，用于做一些初始化工作
>     1. unsubscribe()：用于取消订阅。在该方法被调用后，观察者将不再接收事件
>             * 调用该方法前，先使用 isUnsubscribed() 判断状态

12、被观察者Observable会持有观察者Subscriber的引用，如果引用不能及时释放，就会出现内存泄露

### 通过订阅连接观察者和被观察者


13、通过订阅连接观察者和被观察者
```java
observable.subscribe(observer);
```

14、实际上事件的发送时subscribe后发送。

## 函数式接口

1、RxJava2.0提供了多个函数式接口，用于便捷实现观察者模式
> 例如Consumber:
```java
Observable.just("hello").subscribe(new Consumer<String>()
{
    @Override
    public void accept(String s) throws Exception
    {
        Log.d("accept", s);
    }
});
```

## Disposable

1、Disposable的作用
> 1. 可用`Disposable.dispose() `切断观察者与被观察者之间的连接.
> 1. `切断之后`，观察者不再接收事件，但`被观察者还会继续发送Event`
> 1. 当`isDisposed()`返回为`false`的时候，接收器能正常接收事件，但当其为`true`的时候，接收器停止了接收。
```java
/**===========================
   * 创建观察者 （Observer ）对象
   *   方式1：采用Observer 接口.
   *===========================*/
  Observer<Integer> observer = new Observer<Integer>()
  {

      private Disposable mDisposable;

      // 1. 订阅事件
      @Override
      public void onSubscribe(Disposable d)
      {
          mDisposable = d;
      }

      // 2. 普通事件
      public void onNext(Integer value)
      {
          if(value == 10){
              mDisposable.dispose();
          }
      }
  };
```

## 参考资料
1. [Android Rxjava：这是一篇 清晰 & 易懂的Rxjava 入门教程](https://www.jianshu.com/p/a406b94f3188)
1. [这可能是最好的RxJava 2.x 入门教程（二）](https://www.jianshu.com/p/b39afa92807e)
