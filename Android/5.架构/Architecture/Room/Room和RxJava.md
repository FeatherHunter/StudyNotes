转载请注明链接: https://blog.csdn.net/feather_wch/article/details/88762443

#  Room和RxJava

版本号:2019-03-23(16:30)

---

[toc]

## RxJava

1、Google的ORM库 ROOM支持和RxJava配合使用
> 需要添加依赖
```
    // Room + RxJava2
    implementation 'android.arch.persistence.room:rxjava2:1.1.1'
```
> 仓库路径
```
maven {
    // For Room Persistence Library
    url "https://maven.google.com"
}
```


### Insert插入操作

2、Room的Insert操作能返回三种类型的数据
> 1. Completable
>      * 在插入完成后调用`onComplete`
> 1. Single<Long> or Maybe<Long>
>      * 插入完成后，将插入的id，发射到`onSuccess`
> 1. Single<List<Long>> or Maybe<List<Long>>
>      * 发射插入Item的id列表到onSuccess中


3、Insert示例
```java
@Insert
Completable insert(User user);
// or
@Insert
Maybe<Long> insert(User user);
// or
@Insert
Single<Long> insert(User[] user);
// or
@Insert
Maybe<List<Long>> insert(User[] user);
// or
@Insert
Single<List<Long>> insert(User[] user);
```
> `Completable insert(User user);`为例
```java
// 1. 获取到Completable
        Completable completable = userDao.insert(user);
        completable.subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {
                Log.d("wch", "数据插入成功");
            }

            @Override
            public void onError(Throwable e) {
                Log.d("wch", "数据插入失败:" + e.getCause().getMessage());
            }
        });
```

### Update/Delete操作


4、集成了RxJava的Room针对 update/delete支持如下返回类型：
> 1. Completable — 当update/delete完成时，马上调用onComplete
> 1. Single<Long>/ Maybe<Long> — onSuccess发射的值，是update/delete影响的行数
```java
@Update
Completable update(User user);
// or
@Update
Single<Integer> update(User user);
// or
@Update
Single<Integer> updateAll(User[] user);
// or
@Delete
Single<Integer> deleteAll(User[] user);
// or
@Delete
Single<Integer> deleteAll(User[] user);
```

### Query查询操作

5、传统的插入操作有两个缺点
```java
@Query(“SELECT * FROM Users WHERE id = :userId”)
User getUserById(String userId);
```
> 1. 这是一个阻塞操作
> 1. 数据被修改后，都要手动再查询一次

6、借助RxJava中的` Maybe`, `Single` 和`Flowable` 对象，能观察数据库中数据和执行异步查询

### Maybe

7、Maybe的使用
```java
@Query(“SELECT * FROM Users WHERE id = :userId”)
Maybe<User> getUserById(String userId);
```
> 1. 当数据库中没有用户，查询将不返回行，Maybe会完成。
> 1. 当数据库中有一个用户，Maybe会触发onSuccess，然后将完成。
> 1. Maybe完成后，如果这个用户有更新，什么都不会发生。

### Single

8、Single的使用
```java
    @Query("select * from user where account = :account")
    Maybe<User> findUserByAccount(String account);
```
> 1. 数据库没有`User`，Single会触发`onError(EmptyResultSetException.class)`
> 1. 数据库中有`User`，Single会触发`onSuccess`
> 1. Single完成后，User的数据更新了，不会发生任何事

9、Single用于查询User信息的实例
```java
        Maybe<User> userMaybe = userDao.findUserByAccount(account);
        userMaybe.subscribe(new MaybeObserver<User>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(User user) {
                Log.d("wch", "查询到User");
                getUserLiveData().postValue(user);
            }

            @Override
            public void onError(Throwable e) {
                Log.d("wch", "查询失败:" + e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
```

### Flowable/Observable


10、Flowable/Observable的使用
```java
    @Query("select * from user where account = :account")
    Flowable<User> findUserByAccount(String account);
```
> 1. 数据库没有`User`，查询不会返回数据，不会调用`onNext`也不会调用`onError`
> 1. 数据库中有`User`，会调用`onNext`
> 1. 每次User的数据更新后，`Flowable`会自动发送出去


11、Flowable/Observable的使用实例
> 查询用户信息
```java
        Flowable<User> userFlowable = userDao.findUserByAccount(account);
        userFlowable.subscribe(new Subscriber<User>() {
            @Override
            public void onSubscribe(Subscription s) {
                Logger.d(s);
                /**===========================
                 *  1. 上游发送65535个数据, 非常重要(不写或者数值低，都会导致后面接不到数据)
                 *  2. 调用该方法才会触发下游的onNext()
                 *===============================*/
                s.request(Integer.MAX_VALUE);
            }

            // 后续每次数据更新都会触发该onNext
            @Override
            public void onNext(User user) {
                Logger.d(user);
                getUserLiveData().postValue(user);
            }

            @Override
            public void onError(Throwable t) {
                Logger.d(t.getMessage());
            }

            @Override
            public void onComplete() {
                Logger.d("onComplete()");
            }
        });
```

## 参考资料

1. [Room 🔗 RxJava](https://medium.com/androiddevelopers/room-rxjava-acb0cd4f3757)
