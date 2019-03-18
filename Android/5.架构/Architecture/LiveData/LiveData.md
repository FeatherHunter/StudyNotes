转载请注明链接: https://blog.csdn.net/feather_wch/article/details/88648559

# LiveData基本教程

版本号:2019-03-18(22:30)

---

[toc]

## 简介

1、LiveData的简介
> 1. `LiveData`是一种类，持有`可被观察的数据`。
> 1. `LiveData`是一种`可感知生命周期的组件`，意味着该组件重视其他`app组件的生命周期`，如`Activity、Fragment、Service`
>    * 该组件能确保，仅仅在`Activity\Fragment\Service等组件`都处于活跃的生命周期状态的时候，才去更新app组件。

2、LiveData只有当观察者的生命周期处于`活跃状态`时才会去通知观察者。
> 1. 实现了`Observer类`的观察者，可以注册监听`LiveData`
> 1. 活跃状态就是指处于`STARTED或者RESUMED`状态
> 1. 处于非活跃的观察者，LiveData不会去通知这些观察者


3、可以注册一种观察者, 该观察者与`LifecycleOwner`对象(如：Activity、Fragment)相关联。
> 1. 在对应的`Lifecycle Object`处于`DESTORYED`状态时，会自动解除`LiveData和该观察者的注册关系`

4、在Activity、Fragment中这种自动解除注册的特性非常有用
> 1. Activity、Fragment不用担心会出现`内存泄露`
> 1. 在`Activity、Fragment`销毁时，`LiveData会自动解除其注册关系`

### 优势

5、LiveData能确保UI和数据状态相符
> 1. 因为是观察者模式，LiveData会在生命周期状态改变时，通知观察者
> 1. 可以在`观察者对象`中进行UI的更新操作

6、LiveData没有内存泄露
> 1. 观察者和`Lifecycle对象`绑定，能在销毁时自动解除注册

7、LiveData不会给已经停止的Activity发送事件
> 1. 如果观察者处于非活跃状态，`LiveData`不会再发送任何事件给这些`Observer对象`

8、LiveData能确保不再需要手工对生命周期进行处理
> 1. UI组件仅仅需要对相关数据进行观察
> 1. LiveData自动处理生命周期状态改变后，需要处理的代码。

9、LiveData能保证数据最新
> 1. 一个`非活跃的组件`进入到`活跃状态`后，会立即获取到最新的数据
> 1. 不用担心数据问题

10、LiveData在横竖屏切换等Configuration改变时，也能保证获取到最新数据
> 1. 例如`Acitivty、Fragment`因为`屏幕选装`导致`重建`, 能立即`接收到最新的数据`

11、LiveData能资源共享
> 1. 如果将`LiveData对象`扩充，用`单例模式`将`系统服务进行包裹`。这些服务就可以在app中共享。
> 1. 只需要`LiveData和系统服务`connect，其他`观察者`只需要`监视LiveData`就能获取到这些资源


## 使用LiveData

1、LiveData的使用遵循下面三个步骤
> 1. 创建`LiveData的实例`，并持有具有类型的数据
> 1. 创建`Observer对象`，该对象具有`onChanged()`方法，在`LiveData的数据改变时`，会调用`onChanged()方法，进行相应的处理工作`。可以将`Observer`放置到`activity、fragment`中
> 1. 利用`observe()方法`将`Observer`和`LiveData`联系起来。
>        * observe()接收一个`LifecycleOwner对象`
>        * 可以使用`observeForeve(Observer)`注册一个没使用`LifecycleOwner`的`Observer`，这种场景中，该Observer会认为是一直存活的。
>        * 使用`removeObserver(Observer)`可以移除这些观察者

### 创建LiveData对象

1、创建LiveData对象
> 1. `LiveData`能用来包裹所有数据，包括实现了`Collections`的对象，例如List
> 1. `LiveData`通常存储在`ViewModel`之中, 并通过`get方法`来获取
```java
public class UserViewModel extends ViewModel {
    private MutableLiveData<String> mName;
    private MutableLiveData<Integer> mAge;

    public MutableLiveData<String> getName() {
        if(mName == null){
            mName = new MutableLiveData<>();
        }
        return mName;
    }

    public MutableLiveData<Integer> getAge() {
        if(mAge == null){
            mAge = new MutableLiveData<>();
        }
        return mAge;
    }
}

```

2、为什么将LiveData放置到ViewModel中，而不放到activity或者fragment中？
> 1. 避免fragment和activity的代码臃肿
> 1. 将`LiveData`和特定的activity/fragment解耦，能够在configuration改变的时候，LiveData依然存活。

### 观察LiveData对象

1、在App组件的哪个生命周期适合观察LiveData对象？为什么？
> 1. app组件的`onCreate()`方法
> 1. 不适合在`onResume()`等方法中，可能会调用多次
> 1. 能确保组件能尽可能快的展示出数据。只要app组件处于启动状态(STARTED)就会立即接收到`LiveData对象`中的数据---前提是已经监听了LiveData

## 参考资料
1. [LiveData](https://developer.android.google.cn/topic/libraries/architecture/livedata)
