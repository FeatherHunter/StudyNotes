package com.derry.kt_coroutines.t94

// TODO RxJava 98.异步事件流
/*
Observable.from(new String[]{"This", "is", "RxJava"})
    .map(new Func1<String, String>() {
        @Override
        public String call(String s) {
            printLog(tvLogs, "Transform Data toUpperCase: ", s);
            return s.toUpperCase();
        }
    })
    // 转成List
    .toList()
    .map(new Func1<List<String>, List<String>>() {
        @Override
        public List<String> call(List<String> strings) {
            printLog(tvLogs, "Transform Data Reverse List: ", strings.toString());
            Collections.reverse(strings);
            return strings;
        }
    })
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(new Action1<List<String>>() {
        @Override
        public void call(List<String> s) {
            printLog(tvLogs, "Consume Data ", s.toString());
        }
    });

    Transform Data toUpperCase: 'This' , Main Thread:false, Thread Name:RxCachedThreadScheduler-1
    Transform Data toUpperCase: 'is' , Main Thread:false, Thread Name:RxCachedThreadScheduler-1
    Transform Data toUpperCase: 'RxJava' , Main Thread:false, Thread Name:RxCachedThreadScheduler-1
    Transform Data Reverse List: '[THIS, IS, RXJAVA]' , Main Thread:false, Thread Name:RxCachedThreadScheduler-1
    Consume Data '[RXJAVA, IS, THIS]' , Main Thread:true, Thread Name:main
*/
