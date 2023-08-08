
# RxJava使用和深入剖析

关键点：
1. 设计模式：线程的选择是策略模式

## 使用场景
RxJava比较好的使用场景：
1. 按键防抖动，一段时间内只触发一次点击【buffer】
> buffer将两秒内的点击事件化为1次
1. 账号、密码、邮箱同时验证通过才允许进行后续的注册行为【combineLatest】
> 当任意一个Observable发射数据之后，会去取其它Observable最近一次发射的数据。
1. 后台下载，前台更新进度
> onNext、onComplete
3. 关键词输入自动搜索【debounce、filter、switchMap】
> 200ms内无输入、过滤空String、只接收新的数据
4. 轮询向服务器发起请求【intervalRange、repeatWhen】
6. 指数退避策略，在请求重试中的使用【retryWhen】
> 指数形重试: 2\4\8\16...
7. 网络请求嵌套（flatMap）注册后自动登录



RxJava



IO = iotask = new ioscheduler
不管用哪个sheduler底层都是线程池。不同的线程池配置。

Observable.just(PATH) // 起点，被观察者
    .map() //变换
    .map() //变换
    .subscribe(xxx); //终点，观察者
起点
订阅事件
变换事件
处理事件
结束

响应式编程和链式思维(起点->终点)

终点：观察者，看着起点，Observer
起点：Observable

U型结构
卡片式编程【卡片式结构】，只管上一层。加水印，加日志。或者去除某功能，可以快速实现。

线程切换可以封装。
UD upstream、downstream
进行抽取：还可以增加其他功能(如下的map, 可以无限加东西)
private static<UD> ObservableTransformer<UD, UD>rxud(){
    return new ObservableTransformer<UD, UD>() {
        @Override
        public @NonNull ObservableSource<UD> apply(@NonNull Observable<UD> upstream) {
            return upstream.subscribeOn(Schedulers.io())// subscribe执行线程
                    .observeOn(AndroidSchedulers.mainThread())// observer的线程
                    .map(new Function<UD, UD>() {
                        @Override
                        public UD apply(UD ud) throws Throwable {
                            // 自定义功能！
                            return ud;
                        }
                    });
        }
    };
}

.xxx
.xxx
.compose(ObservableTransformer)
.xxx

Retrofit是管理者，都是交给OkHttp网络操作。

RxJava只处理回来的数据
Retrofit返回的数据是起点

hook点
应用
防抖


subscribeActual操作的永远是上一层

被压flowable 解决上游数据太多导致oom问题
会将时间放到缓存池，缓存池也满了多余事件直接丢弃
支持背压性能会降低不如observable



disposable要接收并且释放。

