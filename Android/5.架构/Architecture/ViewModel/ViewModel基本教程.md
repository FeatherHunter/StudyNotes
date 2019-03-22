

转载请注明链接: https://blog.csdn.net/feather_wch/article/details/88667037

#  ViewModel基本教程

版本号:2019-03-22(11:30)

---

[toc]
## 简介

1、ViewModel是什么?
> 1. 用于存储和管理`UI相关的数据`
> 1. 允许ViewModel在`屏幕旋转等Configutation Changes`的场景下依然保存数据


2、Activity在重建时是如何恢复数据的?
> 1. 在`onSaveInstanceState()`中恢复从onCreate()中获取的Bundle
> 1. 这种方法只适合恢复少量的可以序列化/反序列化的数据
> 1. 如果是大量的数据如`用户信息列表`和`Bitmap`就不适合该场景

3、onSaveInstanceState()恢复数据的缺点?

4、如果Activity获取的操作是耗时的异步操作，还需要避免`内存泄露`，此外每次重建都重新请求，非常浪费资源。

5、ViewModel存在的意义
> 1. Activity、Fragment等UI控制器，如果去控制app所有的逻辑，包括数据的请求等，会导致非常难以维护
> 1. ViewModel能将`视图的数据相关的工作`和`UI controller`逻辑分离


## ViewModel的使用

1、ViewModel的使用
> 1. 将数据相关的逻辑都放置到ViewModel中，如`存放一个用户信息列表`
> 1. ViewModel还需要处理加载用户信息列表的逻辑
```java
public class MyViewModel extends ViewModel {
  // 1. 用户信息，列表
    private MutableLiveData<List<User>> users;
  // 2. 获取到用户信息列表
    public LiveData<List<User>> getUsers() {
        if (users == null) {
            users = new MutableLiveData<List<User>>();
            loadUsers();
        }
        return users;
    }
  // 3. 实际加载用户信息列表，可以从数据库或者网络中
    private void loadUsers() {
        // Do an asynchronous operation to fetch users.
    }
}
```

2、ViewModel的构建，以及注册监听用户信息列表的变化，数据发生改变后就自动更新UI
> 1. 一定要在`onCreate()`中进行处理
> 1.
```java
public class MyActivity extends AppCompatActivity {
    public void onCreate(Bundle savedInstanceState) {
        // 1. 第一次执行会创建ViewModel，Activity重建后调用到该方法会获取到之前的ViewModel
        MyViewModel model = ViewModelProviders.of(this).get(MyViewModel.class);

        // 2. 注册对LiveData的观察，将本Activity作为观察者
        model.getUsers().observe(this, users -> {
            // update UI
        });
    }
}
```

3、当Activity销毁时，framework会调用ViewModel的onCleared()方法对资源进行清理

### 特别要点

4、ViewModel绝对不要持有下列引用
> 1. view
> 1. Lifecycle
> 1. 其他任何可能持有Activity Context的类的引用


5、ViewModel绝对不能去观察一个生命周期感知的被观察者，例如LiveData

### AndroidViewModel

1、如果ViewModel需要Applicaiton的Context(为了获取系统服务)，该如何处理?
> 1. 自定义一个类，继承自`AndroidViewModel`
> 1. 构造器一定要有一个`唯一参数-Application'的对象`


## 生命周期

1、ViewModel的生命周期如下：
> 从Activity的`onCreate`到`onDestory`的期间都是存活的
![ViewModel的生命周期](https://developer.android.google.cn/images/topic/libraries/architecture/viewmodel-lifecycle.png)

## Fragment间共享数据

1、Activity中一般有多个Fragment，也都涉及到数据共享的问题。
> 例如: 第一个Fragment选中一个列表中的Item，第二个Fragment展示这个选中的数据
> ViewModel: 共享的数据
```java
public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Item> selected = new MutableLiveData<Item>();

    public void select(Item item) {
        selected.setValue(item);
    }

    public LiveData<Item> getSelected() {
        return selected;
    }
}
```
> FragmentA: 选中某一个Item
```java
public class MasterFragment extends Fragment {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1. 获取到ViewModel
        final SharedViewModel model = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);

        // 2. 点击后，将选中的Item设置到ViewModel中
        itemSelector.setOnClickListener(item -> {
            model.select(item);
        });
    }
}
```
> FragmentB: 展示选中的Item
```java
public class DetailFragment extends Fragment {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1. 获取到同一个ViewModel
        SharedViewModel model = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);

        // 2. 监听“Item”，一旦发生改变(表明选中了一个Item)，展示该选中的数据
        model.getSelected().observe(this, { item ->
           // Update the UI.
        });
    }
}
```

2、利用ViewModel共享Fragment的数据有什么好处?
> 1. Activity不需要关心相关的任何事情
> 1. Fragment不需要知道其他有哪些Fragment也在依赖这些数据
> 1. 当某个Fragment结束的时候，ViewModel能自动处理好生命周期相关的事务

## ViewModel替代Loaders

1、ViewModel替换那些用来保持UI中的数据和数据库同步的Loaders
> 1. 利用ViewModel可以将Loaders和UI的逻辑相分离

2、ViewModel进行UI和数据库数据同步的方案
> 1. `ViewModel`确保在设备配置改变时，数据依然存在
> 1. `Room`在数据库改变时通知`LiveData`
> 1. `LiveData`察觉到改变时，通知UI更新相应的数据

## 知识扩展

### Fragment的setRetainInstance

1、通过Fragment的setRetainInstance(true)实现Configuration Changes时，数据依然存活的特性。
> 1. Fragment调用setRetainInstance(true)后，Fragment实例在配置改变如屏幕旋转后依旧存活
> 1. 不会再触发onCreate
> 1. 其他的如onAttach()、onActivityCreated()依旧会触发

## 参考资料

1. [官方文档: ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)
1. [如何在设备设置改变时，保存、持久化、管理数据?](https://developer.android.com/topic/libraries/architecture/saving-states.html)
1. [官方Android APP 架构指导手册](https://developer.android.com/jetpack/docs/guide#fetching_data)
