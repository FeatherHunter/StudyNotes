

转载请注明链接: https://blog.csdn.net/feather_wch/article/details/88199536

# RxJava 2.x使用详解

版本号:2019-03-06(20:00)

---

[toc]
## 基本概念

1、什么是响应式编程？
> 1. 响应式编程是一种基于异步数据流概念的编程模式。
> 1. 只要接收到事件，就进行响应并执行一定操作。

2、什么是函数式编程？


### 新特性

3、RxJava 2.x支持新特性，依赖于四个基础接口
> 1. Publisher
> 1. Subscriber
> 1. Subscription
> 1. Processor

4、Publisher的作用
> 1. Publisher 可以发出一系列的事件

5、Subscriber的作用
> 1. Subscriber 负责和处理这些事件。

6、Publisher的Flowable：支持背压
> 背压是指在异步场景中，被观察者发送事件速度远快于观察者的处理事件速度的情况下，告诉被观察者降低发送速度的策略。

### 两种观察者模式


7、RxJava 2.x有两种观察者模式
> 1. Observable/Observer
> 1. Flowable/Subscriber

8、Observable/Observer不支持背压

9、Flowable/Subscriber支持背压

## Observable

### doOnNext

1、doOnNext是用于在订阅者接收到数据前进行一些操作
```java
Observable.just(1, 2, 3, 4)
.doOnNext(new Consumer<Integer>() {
    @Override
    public void accept(@NonNull Integer integer) throws Exception {
        // 1、发送给观察者前进行额外操作：保存数据等等。
    }
}).subscribe(new Consumer<Integer>() {
    @Override
    public void accept(@NonNull Integer integer) throws Exception {

        // 2、观察者接收到数据
    }
});
```

## Observer

### Consumer

1、简化的订阅方式
```java
Observable.just("hello").subscribe(new Consumer<String>()
{
    @Override
    public void accept(String s) throws Exception
    {

    }
});
```

### SingleObserver

2、SingleObserver的作用
> 1、SingleObserver只会调用`onError()`和`onSuccess()`
```java
Single.just(new Random().nextInt())
      .subscribe(new SingleObserver<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull Integer integer) {
                // 成功接收到
            }

            @Override
            public void onError(@NonNull Throwable e) {
                // 出现异常
            }
      });
```

## 线程调度

### subscribeOn

1、subscribeOn用于指定发射事件的线程
> 1. 指定`订阅时-subscribe()`在哪个线程中调用
```java
Observable.just("hello")
    //在一个新线程中调用
        .subscribeOn(Schedulers.newThread())
        .subscribe(new Consumer<String>()
        {
            @Override
            public void accept(String s) throws Exception
            {
                Log.e("feather", s);
            }
        });
```

2、多次调用subscribeOn只有第一次有效

### observeOn

2、observeOn用于指定订阅者接收事件的线程
> 1. 指定`观察者处理事件`是在哪个线程中---这就是处理事件的回调方法在哪个线程调用。

3、多次调用observeOn是可以的，每调用一次，下游的线程就会切换一次。

### 线程切换

```java
Observable.create(new ObservableOnSubscribe<Integer>() {
    // 1、在IO线程中订阅
    public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
        Log.e(TAG, "Observable thread is : " + Thread.currentThread().getName());
        e.onNext(1);
        e.onComplete();
    }
}).subscribeOn(Schedulers.newThread())
    // 1、在IO线程中订阅
        .subscribeOn(Schedulers.io())
          // 2、在主线程中处理
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(new Consumer<Integer>() {
            // 2、在主线程中处理
            public void accept(@NonNull Integer integer) throws Exception {
                Log.e(TAG, "After observeOn(mainThread)，Current thread is " + Thread.currentThread().getName());
            }
        })
          // 3、在IO线程中处理
        .observeOn(Schedulers.io())
        .subscribe(new Consumer<Integer>() {
            // 3、在IO线程中处理
            public void accept(@NonNull Integer integer) throws Exception {
                Log.e(TAG, "After observeOn(io)，Current thread is " + Thread.currentThread().getName());
            }
        });
```
#### AndroidSchedulers.mainThread()

1、AndroidSchedulers.mainThread()就是安卓的主线程

## Scheduler

1、Scheduler是什么?
> 1. 调度器
> 1. 用于RxJava进行线程切换

2、Schedulers的作用
> 用于生成用户所需要的`Scheduler`

3、Schedulers的方法有哪些?

|方法|作用|
|---|---|
|io()|子线程，使用线程池。io操作如网络请求、读写文件等。|
|newThred()|子线程，新创建一个线程|
|computation()|用于需要大量计算任务，不能用于IO操作，默认线程数 = 处理器的数量|
|from(executor)|使用指定的Executor作为调度器, 自定义线程池|
|single()|RxJava有一个单例线程，所有任务都在该线程中执行，后续任务按顺序排队|
|trampoline()|当前线程立即执行任务。当前线程正在执行的任务，会暂停，在插入的任务执行完毕后，继续执行。|
|shutdown()/start()|所有调度器终止和开始|


4、Schedulers.newThread() vs Schedulers.io()
> 1. newThread()会为每个实例创建一个新线程，而 io() 使用的是线程池来管理
> 1. 大量并发工作采用`Schedulers.io()`，可能会遇到IO线程，比如打开文件的最大数量，tcp连接的最大数量，也可能会用完RAM

## Subject

### AsyncSubject

1、AsyncSubject的作用
> 1. `Observer`会接收到`AsyncSubject.onComplete()`之前的最后一个数据。
> 1. 下例中发送了`1、2、3、onComplete、4`只接收到`3`
```java
Subject<String> subject = AsyncSubject.create();
        subject.onNext("1");
        subject.onNext("2");
        subject.onNext("3");
        subject.onComplete();
        subject.onNext("4");

        subject.subscribe(new Observer<String>()
        {
            @Override
            public void onSubscribe(@NonNull Disposable disposable)
            {
                Log.d("feather", "onSubscribe");
            }

            @Override
            public void onNext(@NonNull String s)
            {
                Log.d("feather", "onNext：" + s);
            }

            @Override
            public void onError(@NonNull Throwable throwable)
            {
                Log.d("feather", "onError");
            }

            @Override
            public void onComplete()
            {
                Log.d("feather", "onComplete");
            }
        });
```
> 输出结果
```
onSubscribe
onNext：3
onComplete
```

### BehaviorSubject

1、BehaviorSubject的作用
> 1. 接收`订阅前的最后一个数据`，并且`继续接收后续的数据`
```java
Subject<String> subject = BehaviorSubject.create();
        subject.onNext("1");
        subject.onNext("2");

        subject.subscribe(new Observer<String>()
        {
            @Override
            public void onSubscribe(@NonNull Disposable disposable)
            {
                Log.d("feather", "onSubscribe");
            }

            @Override
            public void onNext(@NonNull String s)
            {
                Log.d("feather", "onNext：" + s);
            }

            @Override
            public void onError(@NonNull Throwable throwable)
            {
                Log.d("feather", "onError");
            }

            @Override
            public void onComplete()
            {
                Log.d("feather", "onComplete");
            }
        });

        subject.onNext("3");
        subject.onNext("4");
```
> 结果
```
onSubscribe
onNext：2
onNext：3
onNext：4
```

### PublishSubject

1、PublishSubject的作用
> 1. `只接收订阅之后发送的数据`
> 1. 下例中只接收到`3、4`
```java
Subject<String> subject = PublishSubject.create();
subject.onNext("1");
subject.onNext("2");

subject.subscribe(new Observer<String>(){...});

subject.onNext("3");
subject.onNext("4");
```

### ReplaySubject

1、ReplaySubject的作用和使用
> 1. `发射一切数据`
> 1. 四种创建方式:
>     1. `ReplaySubject.create()`创建默认初始缓存容量大小为16，当数据条目超过16会重新分配内存空间。
>     1. `ReplaySubject.create(100)`创建指定初始缓存容量的ReplaySubject
>     1. `ReplaySubject.createWithSize(2)`只缓存订阅前最后发送的2条数据
>     1. `ReplaySubject.createWithTime(xxx,TimeUnit.SECONDS,Schedulers.computation())`被订阅前的前`xxx`秒内发送的数据才能被接收
```java
Subject<String> subject = ReplaySubject.create();
subject.onNext("1");
subject.onNext("2");

subject.subscribe(new Observer<String>(){...});

subject.onNext("3");
subject.onNext("4");
```

## 操作符

### just

1、just的作用就是简单发射器，并依次调用`onNext()`方法

### Map

1、Map的作用?
> 1. 将一个`Observable对象`转换为`另一个Observable对象`

2、可以使用Map的场景
> 1-`传入本地图片路径`，根据路径获取到`图片的Bitmap`
```java
Observable.just(filepath)
.map(new Function<String, Bitmap>()
{
    @Override
    public Bitmap apply(@NonNull String path) throws Exception
    {
        // 1、path转换为Bitmap
        Bitmap bitmap = getBitmapByPath(path);
        return bitmap;
    }
}).subscribe(new Consumer<Bitmap>()
{
    @Override
    public void accept(Bitmap bitmap) throws Exception
    {
        // 2、获取到Bitmap进行展示
    }
});
```

3、利用map进行网络数据解析
```java
Observable.create(new ObservableOnSubscribe<String>()
{
    @Override
    public void subscribe(@NonNull ObservableEmitter<String> observableEmitter) throws Exception
    {
        // 1、发起网络请求
        // Request
        // 2、获取返回的数据-JSon数据
        String json = xxx;
        // 3、将数据发送出去
        observableEmitter.onNext(json);
    }
}).map(new Function<String, UserDetailBean>()
{
    @Override
    public UserDetailBean apply(@NonNull String json) throws Exception
    {
        // 4、将网络数据转换为需要的实体
        return new Gson().fromJson(json, UserDetailBean.class);
    }
}).observeOn(AndroidSchedulers.mainThread()) // 5、在主线程处进行临时保存
.doOnNext(new Consumer<UserDetailBean>()
{
    @Override
    public void accept(UserDetailBean bean) throws Exception
    {
        // 5、主线程保存数据
    }
}).subscribeOn(Schedulers.io()) // 6、在io线程池中发送事件，处理数据的请求，返回和处理
.observeOn(AndroidSchedulers.mainThread()) // 7、主线程处理返回的结果
.subscribe(new Consumer<UserDetailBean>()
{
    // 7、成功接收到数据
    @Override
    public void accept(UserDetailBean bean) throws Exception
    {

    }
}, new Consumer<Throwable>()
{
    // 8、接收过程中出现了异常
    @Override
    public void accept(Throwable throwable) throws Exception
    {

    }
});
```

#### FlatMap
1、FlatMap是什么？和Map有什么区别？
> 1. 可以`将一个Observable对象转换为多个Observable对象`
> 1. `FlatMap`不会保证`发送事件的顺序`

2、FlatMap获取到一个Student列表，差分为各个Student，依次打印其详细信息。
```java
// 1-一个Observable，查询到多个学生的信息列表
Observable.create(new ObservableOnSubscribe<Student>()
{
    @Override
    public void subscribe(@NonNull ObservableEmitter<Student> observableEmitter) throws Exception
    {

        // 1、网络请求到一个学生的列表
        ArrayList<Student> students = studentNames;
        // 2、遍历发出去
        for (Student student : students)
        {
            observableEmitter.onNext(student);
        }
    }
    // 2-拆分为多个Student的Observable，发出去，每个接受到后进行相应的处理
}).flatMap(new Function<Student, ObservableSource<String>>()
{
    @Override
    public ObservableSource<String> apply(@NonNull Student student) throws Exception
    {
        // 3、每个Student，都额外调用接口去查询数据
        return Observable.just(student.name);
    }
})
.subscribe(new Consumer<String>()
{
    @Override
    public void accept(String s) throws Exception
    {
        Log.d("feather", s);
    }
});
```

#### concatMap

1、concatMap属于有顺序的flatMap
> 1. concatMap使用方法和flatMap完全一致
> 1. 数据的发送符合顺序

### Concat

1、Concat的作用
> 1. 可以做到`多个Observable的订阅事件按顺序前后发生`
> 1. 例如`ObservableA终止后(onComplete)`, 才会去`订阅第二个Observable`

2、利用concat操作符先读取缓存再通过网络请求新数据
> 1. 对于`操作不敏感的数据时`，可以先读取缓存，再通过网络获取
> 1. 技巧在于`是否调用onComplete()`方法。
```java
Observable<UserDetailBean> cache = Observable.create(new ObservableOnSubscribe<UserDetailBean>()
{
    @Override
    public void subscribe(@NonNull ObservableEmitter<UserDetailBean> observableEmitter) throws Exception
    {
        // 1、读取缓存数据
        UserDetailBean cache = getCache();

        if(cache != null){
            // 2、具有缓存数据，直接返回
            observableEmitter.onNext(cache);
        }else{
            // 3、不具有返回数据，通过网络请求
            observableEmitter.onComplete();
        }

    }
});

Observable<UserDetailBean> network = Observable.create(new ObservableOnSubscribe<UserDetailBean>()
{
    @Override
    public void subscribe(@NonNull ObservableEmitter<UserDetailBean> observableEmitter) throws Exception
    {
        // 1、读取网络数据
        UserDetailBean cache = fromInternet();

        if(cache != null){
            // 2、获取到数据，直接返回
            observableEmitter.onNext(cache);
        }else{
            // 3、不具有数据，出现网络错误。
            observableEmitter.onError(new NetworkErrorException());
        }
    }
});

// 1、合并缓存和网络请求
Observable.concat(cache, network)
        .subscribeOn(Schedulers.io()) // 2、IO读取缓存和网络数据
        .observeOn(AndroidSchedulers.mainThread()) // 3、主线程处理返回的结果
        .subscribe(new Consumer<UserDetailBean>()
        {
            // 4、获取到数据
            @Override
            public void accept(UserDetailBean bean) throws Exception
            {

            }
        }, new Consumer<Throwable>()
        {
            // 5、出现网络异常
            @Override
            public void accept(Throwable throwable) throws Exception
            {

            }
        });
```

### Zip

1、Zip的作用和使用场景
> 1. Zip用于`将多个Observable结合成一个数据发送出去`
> 1. 适用于`将多个接口的数据共同返回后一起发送出去`
```java
Observable<String> observable1 = Observable.create(new ObservableOnSubscribe<String>()
{
    @Override
    public void subscribe(@NonNull ObservableEmitter<String> observableEmitter) throws Exception
    {
        Log.d("feather", "第一个接口正在请求数据");
        Thread.sleep(5000);
        observableEmitter.onNext("1");
        Log.d("feather", "第一个接口请求到数据");
    }
});

Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>()
{
    @Override
    public void subscribe(@NonNull ObservableEmitter<String> observableEmitter) throws Exception
    {
        Log.d("feather", "第二个接口正在请求数据");
        observableEmitter.onNext("2");
        Log.d("feather", "第二个接口请求到数据");
    }
});

Observable.zip(observable1, observable2, new BiFunction<String, String, String>()
{
    @Override
    public String apply(@NonNull String s, @NonNull String s2) throws Exception
    {
        // 一起返回
        Log.d("feather", "两个接口的数据都获取到");
        return s + " " + s2;
    }
}).subscribeOn(Schedulers.io())
.subscribe(new Consumer<String>()
{
    @Override
    public void accept(String result) throws Exception
    {
        Log.d("feather", result);
    }
});
```

### 数据操作

#### skip

1、skip的作用
> 1. `跳过count数目的数据才开始接收`

#### buffer

1、buffer的作用
> 1. 对数据进行`缓存`，可以设置缓存大小。`缓存满之后以List的形式发送出去`
```java
final int count = 0;

// 1、每两个数据打包成一组，最后多余出来的数据也会发送出来。
Observable.just(1, 2, 3, 4, 5, 6, 7).buffer(2).subscribe(new Consumer<List<Integer>>()
{
    // 2、分组为 (1, 2)(3, 4)(5, 6)(7)
    @Override
    public void accept(List<Integer> integers) throws Exception
    {
        Log.d("feather", count + " : " + integers.size());
    }
});
```

2、利用buffer实现网络数据获取后，对列表中数据进行预处理，然后打包成List发送出去。

##### buffer(count, skip)

3、buffer具有两个参数count和skip
> 1. 作用是将`Observable`中的数据按`skip(步长)`分成最大不超过`count`的buffer，然后生成一个`Observable`
> 2. 实例: 当前有数据`1、2、3、4、5、6`
> 3. `buffer(3, 1)`
```
1,2,3
2,3,4
3,4,5
4,5,6
```
> 4. `buffer(3, 2)`
```
1,2,3
3,4,5
5,6
```
> 5. `buffer(3, 3)`
```
1,2,3
4,5,6
```
> 5. `buffer(3, 4)`
```
1,2,3
5,6
```

#### take

2、take的作用是什么?
> 1. `发射前n项数据`
> 1. 下例中`会只保留前3个数据`，然后走`buffer的流程`
```java
final int count = 0;

// 1、每两个数据打包成一组，最后多余出来的数据也会发送出来。
Observable.just(1, 2, 3, 4, 5, 6, 7).take(3).buffer(2).subscribe(new Consumer<List<Integer>>()
{
    // 2、分组为 (1, 2)(3, 4)(5, 6)(7)
    @Override
    public void accept(List<Integer> integers) throws Exception
    {
        Log.d("feather", count + " : " + integers.size());
    }
});
```
> 结果
```
D/feather: 0 : 2
D/feather: 0 : 1
```

#### Distinct

1、Distinct的作用是去除重复的项
> 基本数据类型、String常量都是能过滤的

2、Distinct如何处理对象的？
> 1. 例如：StudentA, name = "wang", StudentB, name = "wang"
> 1. 根据测试结果，是无法过滤的
```java
Observable.just(new Student("wang"), new Student("chen"), new Student("hao"), new Student("wang"), new Student("wang"))
.distinct().subscribe(new Consumer<Student>()
{
    @Override
    public void accept(Student student) throws Exception
    {
        Log.d("feather", "" + student.name);
    }
});
```
> 结果
```
D/feather: wang
D/feather: chen
D/feather: hao
D/feather: wang
D/feather: wang

```

#### Filter

1、Filter的作用是过滤，通过判断的才发射。
```java
Observable.just(new Student("wang"), new Student("chen"), new Student("hao"))
        // 过滤数据，自定义过滤的条件，返回true表示发射，false表示不会发射
.filter(new Predicate<Student>()
{
    @Override
    public boolean test(@NonNull Student student) throws Exception
    {
        return "wang".equals(student.name);
    }
})
.subscribe(new Consumer<Student>()
{
    @Override
    public void accept(Student student) throws Exception
    {
        Log.d("feather", "" + student.name);
    }
});
```

#### debounce

1、debounce的作用和使用方法
> 1. 去除掉发送频率过快的数据
> 1. 下例中移除掉发送间隔时间，低于500ms的数据。
```java
// 1、间隔发送数据
Observable.create(new ObservableOnSubscribe<Integer>() {
    @Override
    public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
        // send events with simulated time wait
        emitter.onNext(1); // skip
        Thread.sleep(400);
        emitter.onNext(2); // deliver
        Thread.sleep(505);
        emitter.onNext(3); // skip
        Thread.sleep(100);
        emitter.onNext(4); // deliver
        Thread.sleep(605);
        emitter.onNext(5); // deliver
        Thread.sleep(510);
        emitter.onComplete();
    }
    // 2. 去除掉发送间隔时间 <= 500ms都剔除掉，只发送了 2、4、5
}).debounce(500, TimeUnit.MILLISECONDS)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer integer) throws Exception {
                    // 接收到数据
            }
        });
```

### 定时器

#### interval

1、interval的作用和使用场景
> 1. `间隔一定时间就发送一次数据`
> 1. 默认在新线程
> 1. 适用场景: 定时轮询
```java
    private Disposable mDisposable;

    @Override
    protected void doSomething() {
    // 1、1秒发送一次事件
        mDisposable = Flowable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        // 2、定时周期性接收到事件，进行操作。
                    }
                });
    }

    /**
     * 2、销毁时停止心跳
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null){
            mDisposable.dispose();
        }
    }
```

#### timer

1、timer的作用和使用
> 1. `timer具有定时器的功能`
```java

Observable.timer(2, TimeUnit.SECONDS)
.subscribeOn(Schedulers.io())
// 1. timer 默认在新线程，所以需要切换回主线程
.observeOn(AndroidSchedulers.mainThread())
.subscribe(new Consumer<Long>() {
            @Override
            public void accept(@NonNull Long aLong) throws Exception {
                // 2. 接收到数据
            }
        });
```


### defer

1、defer的作用
> 1. 每次`Subscribe订阅时`会创建一个新`被观察者Observable`
```java
// 1、def
Observable<Integer> observable = Observable.defer(new Callable<ObservableSource<? extends Integer>>()
{
    @Override
    public ObservableSource<? extends Integer> call() throws Exception
    {
        // 2、每订阅一次，创建一个被观察者
        return Observable.just(1, 2, 3);
    }
});

// 订阅一次
observable.subscribe(xxx);
// 订阅一次
observable.subscribe(xxx);
```

### last

1、last是取出可观察到的最后一个值
```java
Observable.just(1, 2, 3)
          .last(4)
          .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        mRxOperatorsText.append("last : " + integer + "\n");
                        Log.e(TAG, "last : " + integer + "\n");
                    }
          });
```

### merge

2、merge的作用？
> 1. 能将多个`Observable`结合起来
> 1. `merge`和`concat`区别在于:
>      * 不需要等待`Observable 1`事件全部发送完成，就可以发送`发射器2的事件`

3、merge的使用
```java
Observable.merge(Observable.just(1, 2), Observable.just(3, 4, 5))
                .subscribe(xxx);
                // 接收到1、2、3、4、5
```


### reduce


1、reduce的作用
> 1. 被观察者发出的每一个item都调用function进行处理，然后得到一个最终值，并且发射出去。
> 1. 例如1、2、3、4、5，funvtion是相加，结果=1+2+3+4+5
```java
Observable.just(1, 2, 3)
.reduce(new BiFunction<Integer, Integer, Integer>() {
          // 1、两个item相加
          public Integer apply(@NonNull Integer integer, @NonNull Integer integer2) throws Exception {
              return integer + integer2;
          }
}).subscribe(new Consumer<Integer>() {
           @Override
           public void accept(@NonNull Integer integer) throws Exception {
               // 输出结果 = 6
           }
});
```

### scan

1、scan的作用？和reduce的区别？
> 1. 功能和reduce类似
> 1. 区别在于`每一个步骤的结果都会发射出去`
```java
Observable.just(1, 2, 3)
.scan(new BiFunction<Integer, Integer, Integer>() {
      @Override
      public Integer apply(@NonNull Integer integer, @NonNull Integer integer2) throws Exception {
            return integer + integer2;
}})
.subscribe(xxx);
```

### window

1、window的作用
> 1. 和Buffer类似，但是是发送出`每组item的Observable`，`每个Observable会依次发出这组数据中的数据`
```java
Observable.just(1, 2, 3)
.window(3, TimeUnit.SECONDS)
.subscribe(new Consumer<Observable<Integer>>() {
        @Override
        public void accept(@NonNull Observable<Integer> observable) throws Exception {
                // 1、接收到一组Item的Observable
                observable.subscribe(new Consumer<Integer>() {
                      @Override
                      public void accept(@NonNull Integer aLong) throws Exception {
                              // 2、依次接收到一组数据中的每个数据
                      }
                });
      }
});
```

## Function

### BiFunction

## 问题汇总

## 参考资料

1. [操作符-官方文档](http://reactivex.io/documentation/operators.html)
1. [RxJava 2.x 入门教程（五）](https://www.jianshu.com/p/81fac37430dd)
