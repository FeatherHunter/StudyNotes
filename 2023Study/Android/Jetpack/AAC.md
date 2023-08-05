
# Jetpack深度探索

[TOC]

本文链接：https://blog.csdn.net/feather_wch/article/details/132094862


# Lifeycle
1、是一种利用空白Fragment的技巧，采用了观察者模式、状态机和装饰者模式，并且用Map针对反射进行性能优化的，生命周期组件。
2、注册：装饰者模式
> Observer->ReflectiveGenericLifecycleObserver->LifecycleEventObserver->ObserverWithState
> 反射->onStageChanged->State
3、事件派发：Activity->ReportFragment(派发六大事件)->LifecycleRegisry(五大状态处理)->拆包装(state和mState同步->onStageChange->反射调用Map(key=Event,Value=Method))
4、为什么Activity能够被感知？
```java
ComponentActivity: 成员变量
// 1、【implements：LifecycleOwner】 // 标记具有生命周期的组件
    public Lifecycle getLifecycle() {
        return mLifecycleRegistry; // 生命周期组件
    }
// 2、
    private final LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);
// 3、
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 绑定ReportFragment
        ReportFragment.injectIfNeededIn(this);  // add 
    }
// 使用者：注册到LifecycleRegistry(Registry 注册表的意思)
getLifecycle().addObserver(xxx);
```
1. 实现LifecycleOwner接口：需要实现getLifecycle()方法
2. 成员变量LifecycleRegistry：返回给上层，进行注册
3. onCreate中add ReportFragment：回调onActivityCreate等生命周期时，会disptach分发，
5、ReportFragment如何绑定到Activity？如何事件上报？
```java
public class ReportFragment extends android.app.Fragment {
    public static void injectIfNeededIn(Activity activity) {
        // add
        manager.beginTransaction().add(new ReportFragment(), REPORT_FRAGMENT_TAG).commit();
        manager.executePendingTransactions();
    }
    static void dispatch(@NonNull Activity activity, @NonNull Lifecycle.Event event) {
        // LifecycleRegistry#
        activity.getLifecycle().handleLifecycleEvent(event);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dispatchCreate(mProcessListener);
        dispatch(Lifecycle.Event.ON_CREATE);
    }
    @Override
    public void onStart() {
        super.onStart();
        dispatchStart(mProcessListener);
        dispatch(Lifecycle.Event.ON_START);
    }
}
```
6、状态同步是什么意思？
1. LifecycleRegistry，生命周期组件，会先根据EVENT决定自己的状态STATE
2. 同步状态,while()，对比ObserverWithState的state，dispatchTouchEvent

7、涉及到的类：
1. LifecycleOwner、Lifecycle、LifecycleRegistry
2. LifecycleObserver、ObserverWithState(LifecycleEventObserver(ReflectiveGenericLifecycleObserver))

8、为什么需要状态机？=> 给一切框架使用

# LiveData
核心点：
1. 正常数据分发（遍历观察者，分发数据）
2. 状态变化（活跃状态时，接收到活跃状态，遍历数据分发）
3. DESTORY时，remove观察者
4. LifecycleBoundObserver
5. ==> 模板方法模式、外观模式
6. MutableLiveData ==> 开闭原则

1、LiveData相关类
1. LiveData(LifecycleBoundObserver、ObserverWrapper、AlwaysActiveObserver)
2. MutableLiveData extends LiveData
3. Observer ========================================================> SAM接口
2、LiveData为什么只能在主线程使用？威慑呢这么设计？
1. 要确保数据的一致性和安全性，需要强制主线程。还能简化其用法。
```java
    protected void setValue(T value) {
        assertMainThread("setValue"); // 检测 ===========> Looper
        mVersion++;
        mData = value;
        dispatchingValue(null);
    }
```
3、数据粘性 ====================================================> Hook、反射、封装
1. LiveData存有mVersion：每次数据更新时++
2. LifecycleBoundObserver（ObserverWrapper）：mLastVersion
3. 本质为了避免处理重复数据，并且接收到最新一次数据。

4、observe: 注册流程
5、Transformations：工具类可对LiveData，进行数据变换
1. map
2. switchMap
3. distinctUntilChanged

6、ComputableLiveData：支持在后台计算和更新LiveData的值。
1. 来处理需要进行耗时计算的LiveData，以及避免在主线程中执行这些计算。
2. 场景：重写compute，从网络or数据库获取数据并且处理，最后才返回
```java
public class MyComputableLiveData extends ComputableLiveData<String> {
    @Override
    protected String compute() {
        // 执行耗时计算，例如从数据库或网络获取数据
        String data = fetchDataFromDatabaseOrNetwork();
        // 对数据进行处理
        String result = processData(data);
        return result;
    }
}
// 使用
MyComputableLiveData myLiveData = new MyComputableLiveData();
myLiveData.observe(this, new Observer<String>() {
    @Override
    public void onChanged(String result) {
        // 处理计算结果
        updateUI(result);
    }
});
```
7、MediatorLiveData
1. 作用是组合多个LiveData，并根据它们的变化情况来更新自己的值。
1. 有时候我们需要根据多个LiveData的变化来更新另一个LiveData的值
1. 调用addSource()方法来添加其他LiveData作为源

8、LifecycleBoundObserver
```java
//LiveData内部类：
//  ObserverWrapper.activeStateChanged()
//  LifecycleEventObserver.onStateChanged()
    class LifecycleBoundObserver extends ObserverWrapper implements LifecycleEventObserver {
        final LifecycleOwner mOwner;
        LifecycleBoundObserver(@NonNull LifecycleOwner owner, Observer<? super T> observer) {
            super(observer);
            mOwner = owner;
        }
        public void onStateChanged(@NonNull LifecycleOwner source,
                @NonNull Lifecycle.Event event) {
            if (mOwner.getLifecycle().getCurrentState() == DESTROYED) {
                removeObserver(mObserver);
                return;
            }
            activeStateChanged(shouldBeActive());
        }
    }
```

9、粘性事件最简单处理方法
1. 自定义UnPeekLiveData
2. mLastVersion
3. 自己实现ObserverWrapper：mVersion，注册非粘性事件时，用mLastVersion作为mVersion
4. onChanged()中判断version

# ViewModel
===> 记得补充恢复机制的图片
1、ViewModel是通过ViewModelStoreOwner持有者提供并且掌控ViewModelStore的生命长度，结合Lifycle在真正销毁时对数据进行销毁，在重建时维持数据的组件。

2、ViewModel包中的核心元素
```kotlin
ViewModel // 1、抽象类，需要我们用Provier构建出实例
--AndroidViewModel // 系统预置App层面ViewModel
ViewModelProvider // 2、构建ViewModel
--Factory // 工厂
----NewInstanceFactory
------AndroidViewModelFactory
ViewModelStoreOwner // 3、ViewModel商店的持有者
--getViewModelStore()
ViewModelStore // 4、ViewModel商店
```
1. 构造
2. 数据恢复原理和机制
3. +Lifecycle释放数据

### ViewModelStoreOwner
1、ViewModelStoreOwner是什么？
1. 实现者需要提供ViewModelStore
1. 提供ViewModelStore的组件，需要保证其生命周期的长度

### ViewModelStore

1、用于存储和管理ViewModel
1. Activity横竖屏切换，需要通过NonConfigurationInstance恢复ViewModelStore
2. HashMap存储ViewModel，提供get和put
3. clear()：会在ComponentActivity构造的时候，监听lifecycle的ON_DESTORY事件，判断是否是重建，清空ViewModelStore
```kotlin
public class ViewModelStore {
    private final HashMap<String, ViewModel> mMap = new HashMap<>();
    final void put(String key, ViewModel viewModel) {
        ViewModel oldViewModel = mMap.put(key, viewModel);
    }
    final ViewModel get(String key) {
        return mMap.get(key);
    }
    public final void clear() {
        for (ViewModel vm : mMap.values()) {
            vm.clear();
        }
        mMap.clear();
    }
}
```
```kotlin
ComponentActivity():
        getLifecycle().addObserver(new LifecycleEventObserver() {
            public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    if (!isChangingConfigurations()) { // 非横竖屏、语言、深色模式等重建导致的销毁
                        getViewModelStore().clear();
                    }
                }
            }
        });
```
#### NonConfigurationInstance

2、CompoentActivity中的ViewModelStore是什么时候新建的？Activity如何实现ViewModelStore的生命长度？
```kotlin
    public ViewModelStore getViewModelStore() {
        // 1、存在就返回
        if (mViewModelStore == null) {
            // 2、尝试从重建中恢复
            NonConfigurationInstances nc = (NonConfigurationInstances) getLastNonConfigurationInstance();
            if (nc != null) mViewModelStore = nc.viewModelStore;
            // 3、没有需要回复的，就新建
            if (mViewModelStore == null) mViewModelStore = new ViewModelStore();
        }
        // 4、有就直接使用
        return mViewModelStore;
    }
```

3、为什么可以做到重建时保存数据？
1. handleRelaunchActivity->handleDestoryActivity->存储非配置变量
2. handleLaunchActivity->attach()中传入并且恢复

4、利用了Actibity.retainNonConfigurationInstances(机制)

5、NonConfigurationInstance会由ActivityThread存储
### ViewModelProvider
1、ViewModelProvider如何通过get()构造出ViewModel
```kotlin
    private static final String DEFAULT_KEY = "androidx.lifecycle.ViewModelProvider.DefaultKey";
// 1、ViewModel标签+自定义ViewModel类的全限定名 = key， value = ViewModel
    public <T extends ViewModel> T get(Class<T> modelClass) {
        String canonicalName = modelClass.getCanonicalName();
        return get(DEFAULT_KEY + ":" + canonicalName, modelClass);
    }
    public <T extends ViewModel> T get(String key, Class<T> modelClass) {
        // 2、ViewModelStore中查询ViewModel，查询到就返回
        // 一个Owner维护唯一个ViewModelStore
        ViewModel viewModel = mViewModelStore.get(key);
        if (modelClass.isInstance(viewModel)) return (T) viewModel;
        // 3、用工厂构造实例
        viewModel = (mFactory).create(modelClass);
        // 4、存入
        mViewModelStore.put(key, viewModel);
        return (T) viewModel;
    }
```
2、Factory会决定调用有参构造函数，还是无参的。
```kotlin
NewInstanceFactory:
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return modelClass.newInstance();
    }
AndroidViewModelFactory:
    modelClass.getConstructor(Application.class).newInstance(mApplication);
```
#### getCanonicalName
1、getCanonicalName作用是什么
1. modelClass.getCanonicalName()返回给定类的全限定名 =======> 反射
1. 将返回类似"com.example.MyClass"
```java
String className = MyClass.class.getCanonicalName();
```
2、Canonical => `/kəˈnɒnɪkl/` 规范化的

# Databinding

1、ObserveField和LiveData哪个更好？ ===>性能优化点
1. 数据刷新很频繁，比如大量进度条，适合ObserveField：编译时耗时，运行时OK
1. 正常UI用LiveData

