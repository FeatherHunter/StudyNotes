
转载请注明链接：https://blog.csdn.net/feather_wch/article/details/87376462

>详细分析Router的源码。

# ARouter源码详解

版本：2019/2/18-19:38

---

[toc]

1、ARouter源码的四个组成部分
> 1. arouter-register
> 1. arouter-annotation
> 1. arouter-compiler
> 1. arouter-api


2、arouter-register的作用?
> 1. 1.3.0版本新添加的gradle插件.
> 2. 用于路由表的自动注册
> 3. 可以缩短初始化时间，解决应用加固导致无法直接访问dex文件初始化失败的问题。

3、arouter-annotation的作用?
> 1. 注解类以及携带数据的bean

4、 arouter-compiler的作用?
> 1. 注解处理类
> 1. 通过java的`APT`(Annotation Processor Tool)按照定义的Processor生成所需要的类。
> 1. 生成的类位于文件夹`build/generated/source/apt/debug/com.alibaba.android.arouter/routes`中。

5、arouter-api的作用?
> 1. 提供了实现路由功能所需要的api

## 初始化

1、ARouter的初始化
```java
// 1.必须在init之前调用
if (isDebugARouter) {
    ARouter.openLog();     // 开启Log
    ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
}
// 2.初始化
ARouter.init(BaseApp.this);
```

### 开启日志

2、ARouter.openLog()源码分析
> 1. 设置DefaultLogger的标志位(isShowLog)为true
> 2. 后续打印的日志，在isShowLog=true时，调用`Log的相关方法`打印出日志。
```java
/**===================
 * 1、打开Log
 * // ARouter.java
 *===================*/
public static synchronized void openLog() {
    _ARouter.openLog();
}
/**==================================
 * 2、交给默认日志工具:DefaultLogger
 *     1. 设置logger的标志位(isShowLog)为true
 *     2. 打印出日志
 * // _ARouter.java
 *===================================*/
static ILogger logger = new DefaultLogger(Consts.TAG); // 日志工具
static synchronized void openLog() {
    logger.showLog(true); // 1. 设置logger的标志位(isShowLog)为true
    logger.info(Consts.TAG, "ARouter openLog"); // 2. 打印出日志
}
// DefaultLogger.java
public void showLog(boolean showLog) {
    isShowLog = showLog;
}

/**==================================
 * 3、日志的打印内部都是通过Log来实现。例如info就是Log.i
 * // DefaultLogger.java
 *===================================*/
public void info(String tag, String message) {
    if (isShowLog) {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        // Log打印出日志
        Log.i(TextUtils.isEmpty(tag) ? getDefaultTag() : tag, message + getExtInfo(stackTraceElement));
    }
}
```

3、ARouter.openDebug()源码分析
```java
/**===================
 * // ARouter.java
 * 1、打开Debug模式
 *===================*/
public static synchronized void openDebug() {
    _ARouter.openDebug();
}
/**==================================
 * // _ARouter.java
 * 2、设置debuggable = true
 *   1. debuggable的作用:
 *      在navigation等路由操作遇到问题的时候,如果debuggable=true，
 *      就会弹出相关的toast。如"There's no route matched!"。
 *===================================*/
static synchronized void openDebug() {
    debuggable = true;
    logger.info(Consts.TAG, "ARouter openDebug");
}
```

### _ARouter

4、`_ARouter`的作用?
> 1. ARouter的相关操作，内部都是通过`_ARouter`实现。
> 1. Arouter是对外暴露api的类，`_ARouter`是真正的实现类
> 1. 好处: 解耦，可以有选择的去暴露想要给用户使用的方法，并且将其他方法隐藏在内部。比使用`private`的灵活性更强。

### 初始化源码

5、ARouter.init()源码分析
> 1. 内部通过`_ARouter.init()`转交给`LogisticsCenter`(后勤中心)进行初始化工作。
> 1. `LogisticsCenter.init()`需要得到ARouter框架生成的所有中间类的类名集合。如果有本地缓存直接读取，没有缓存会找到app的所有Dex路径，然后遍历出其中的属于com.alibaba.android.arouter.routes包下的所有类名，将这些ARouter框架生成的中间类打包成集合返回。
> 1. 获取到ARouter生成的所有中间类类名集合后，会遍历该集合并且对其中的Root、Interceptors拦截器、Providors服务进行初始化，并且加入到Map中。
> 1. `LogisticsCenter.init()`还会实现检测是否已经通过插件完成了注册和初始化，如果是，则跳过上面这些操作。
> 1. 初始化完成后，会调用`_ARouter.afterInit()`触发所有拦截器的初始化，会遍历拦截器的Map，实例化所有拦截器并且调用其init方法，然后将拦截器对象加入到烂机器列表中。
```java
/**===================
 * // ARouter.java
 * 1、初始化
 *===================*/
public static void init(Application application) {
    // 1. hasInit保证初始化代码只执行一次
    if (!hasInit) {
        // 2. 使用_ARouter的日至工具
        logger = _ARouter.logger;
        _ARouter.logger.info(Consts.TAG, "ARouter init start.");
        // 3. 通过_ARouter进行初始化
        hasInit = _ARouter.init(application);

        if (hasInit) {
            // 4. 触发拦截器的初始化(内部通过interceptorService实现)。实例化所有拦截器，调用其init方法，并将对象存储到列表Warehouse.interceptors中.
            _ARouter.afterInit();
        }

        _ARouter.logger.info(Consts.TAG, "ARouter init over.");
    }
}
/**=======================
 * // _ARouter.java
 * 2、_ARouter实际进行初始化工作
 *=======================*/
private volatile static ThreadPoolExecutor executor = DefaultPoolExecutor.getInstance();
protected static synchronized boolean init(Application application) {
    // 1. 保存application为成员变量
    mContext = application;
    // 2. 重要的初始化工作
    LogisticsCenter.init(mContext, executor);
    // 3. 打印日志
    logger.info(Consts.TAG, "ARouter init success!");
    // 4. hasInit = true;
    hasInit = true;
    // 5. 创建主线程的Handler
    mHandler = new Handler(Looper.getMainLooper());
    return true;
}

/**================================
 * // LogisticsCenter.java
 * 3、LogisticsCenter初始化，在内存中加载所有的元信息。请求初始化.
 *==================================*/
public synchronized static void init(Context context, ThreadPoolExecutor tpe) throws HandlerException {
    mContext = context;
    // 1. 该线程池是_ARouter中获取的DefaultPoolExecutor对象
    executor = tpe;

    try {
        /**--------------------------------------
         * 2. 由插件加载(该方法默认是空方法)
         *   1)arouter-auto-register插件会在该方法内生成代码
         *   2)调用该方法去注册所有的Routers、Interceptors、Providers
         *-------------------------------------*/
        loadRouterMap();
        if (registerByPlugin) {
            // 3. 如果由插件进行了注册，完成初始化
            logger.info(TAG, "Load router map by arouter-auto-register plugin.");
        } else {
            // 4. 插件没有成功进行注册。该集合存放生成类的类名集合.
            Set<String> routerMap;

            /**=================================================
             * 5. 如果是debug模式或者App是新版本，从apt生成的包中加载类
             *================================================*/
            if (ARouter.debuggable()
                    || PackageUtils.isNewVersion(context)) {  // 对比App的新旧versioncode和versionname是否相同

                // 6. 找到app的dex，然后遍历出其中的属于com.alibaba.android.arouter.routes包下的所有类名，打包成集合返回。
                //    public static final String ROUTE_ROOT_PAKCAGE = "com.alibaba.android.arouter.routes";
                routerMap = ClassUtils.getFileNameByPackageName(mContext, ROUTE_ROOT_PAKCAGE);
                // 7. 通过sp将类名集合存储到本地
                if (!routerMap.isEmpty()) {
                    context.getSharedPreferences(AROUTER_SP_CACHE_KEY, Context.MODE_PRIVATE).edit().putStringSet(AROUTER_SP_KEY_MAP, routerMap).apply();
                }
                // 8. 更新版本(保存versionname和versioncode)
                PackageUtils.updateVersion(context);
            } else {
                /**===============================================================
                 * 9. 不是debug模式，也不是新版本的app，直接从本地缓存中读取RouterMap(apt生成类的类名集合)
                 *============================================================*/
                routerMap = new HashSet<>(context.getSharedPreferences(AROUTER_SP_CACHE_KEY, Context.MODE_PRIVATE).getStringSet(AROUTER_SP_KEY_MAP, new HashSet<String>()));
            }

            // 10. 成功找到Router Map打印RouterMap的尺寸
            logger.info(TAG, "Find router map finished, map size = " + routerMap.size());

            /**===============================================================
             * 11. 不是debug模式，也不是新版本的app，直接从本地缓存中读取RouterMap(apt生成类的类名集合)
             *============================================================*/
            for (String className : routerMap) {
                // 12. 加载Root元素。className如:com.alibaba.android.arouter.routes.ARouter$$Root + $$app。反射并实例化对象，调用其loadInto()方法，加载root元素.
                if (className.startsWith(ROUTE_ROOT_PAKCAGE + DOT + SDK_NAME + SEPARATOR + SUFFIX_ROOT)) {
                    // 将分组生成的类，例如: ARouter$$Group$$app.class和ARouter$$Group$$fragment.class添加到Map中
                    ((IRouteRoot) (Class.forName(className).getConstructor().newInstance())).loadInto(Warehouse.groupsIndex);
                }
                // 13. 加载拦截器interceptors。className如: com.alibaba.android.arouter.routes.ARouter$$Interceptors$$app
                else if (className.startsWith(ROUTE_ROOT_PAKCAGE + DOT + SDK_NAME + SEPARATOR + SUFFIX_INTERCEPTORS)) {
                    ((IInterceptorGroup) (Class.forName(className).getConstructor().newInstance())).loadInto(Warehouse.interceptorsIndex);
                }
                // 14. 加载Providers, 将元数据(RouteMeta)存储到Map中。(ARouter提供服务管理，用于将一部分功能和组件封装成接口，以接口的形式对外提供能力)。className如: com.alibaba.android.arouter.routes.ARouter$$Providers$$arouterapi
                else if (className.startsWith(ROUTE_ROOT_PAKCAGE + DOT + SDK_NAME + SEPARATOR + SUFFIX_PROVIDERS)) {
                    ((IProviderGroup) (Class.forName(className).getConstructor().newInstance())).loadInto(Warehouse.providersIndex);
                }
            }
        }

        logger.info(TAG, "Load root element finished, cost " + (System.currentTimeMillis() - startInit) + " ms.");

        // 不包含任何Group，需要检查是否配置有问题.
        if (Warehouse.groupsIndex.size() == 0) {
            logger.error(TAG, "No mapping files were found, check your configuration please!");
        }

        if (ARouter.debuggable()) {
            logger.debug(TAG, String.format(Locale.getDefault(), "LogisticsCenter has already been loaded, GroupIndex[%d], InterceptorIndex[%d], ProviderIndex[%d]", Warehouse.groupsIndex.size(), Warehouse.interceptorsIndex.size(), Warehouse.providersIndex.size()));
        }
    } catch (Exception e) {
        throw new HandlerException(TAG + "ARouter init logistics center exception! [" + e.getMessage() + "]");
    }
}


/**================================
 * // ClassUtils.java
 * 4、获取到所有的dex路径
 *==================================*/
public static List<String> getSourcePaths(Context context) throws PackageManager.NameNotFoundException, IOException {
    ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
    // 1. 获取到APK所在文件
    File sourceApk = new File(applicationInfo.sourceDir);

    List<String> sourcePaths = new ArrayList<>();
    // 2. 添加默认的apk路径到Dex路径集合中。例如:/data/app/com.feather.imageview-1/base.apk
    sourcePaths.add(applicationInfo.sourceDir);

    // 3. APK文件名 + EXTRACTED_NAME_EX(= .class)。例如: base.apk.classes
    String extractedFilePrefix = sourceApk.getName() + EXTRACTED_NAME_EXT;

    /**========================================================
     * 4. 如果VM不支持MultiDex,进一步处理。
     *    目前手上的Phone和Pad都已经支持MultiDex，不会进入该分支，不仔细研究其中的细节。
     *    // 如果VM已经支持了MultiDex，就不要去Secondary Folder加载 Classesx.zip了，那里已经么有了
     *    // 通过是否存在sp中的multidex.version是不准确的，因为从低版本升级上来的用户，是包含这个sp配置的
     *=======================================================*/
    if (!isVMMultidexCapable()) {
        //the total dex numbers
        int totalDexNumber = getMultiDexPreferences(context).getInt(KEY_DEX_NUMBER, 1);
        File dexDir = new File(applicationInfo.dataDir, SECONDARY_FOLDER_NAME);

        for (int secondaryNumber = 2; secondaryNumber <= totalDexNumber; secondaryNumber++) {
            //for each dex file, ie: test.classes2.zip, test.classes3.zip...
            String fileName = extractedFilePrefix + secondaryNumber + EXTRACTED_SUFFIX;
            File extractedFile = new File(dexDir, fileName);
            if (extractedFile.isFile()) {
                sourcePaths.add(extractedFile.getAbsolutePath());
                //we ignore the verify zip part
            } else {
                throw new IOException("Missing extracted secondary dex file '" + extractedFile.getPath() + "'");
            }
        }
    }

    // 5. ARouter处于Debug模式,搜寻IntentRun的Dex文件
    if (ARouter.debuggable()) { // Search instant run support only debuggable
        sourcePaths.addAll(tryLoadInstantRunDexFile(applicationInfo));
    }
    // 6. 最终返回Dex路径集合。本例中只返回了路径: /data/app/com.feather.imageview-1/base.apk
    return sourcePaths;
}

/**================================
 * // ARouter$$Root$$app.java
 * 5、加载Root根元素。将group-app、group-fragment, 都添加到Map中(Warehouse.groupsIndex)
 *==================================*/
public class ARouter$$Root$$app implements IRouteRoot {
    @Override
    public void loadInto(Map<String, Class<? extends IRouteGroup>> routes) {
        // 1. 分组名app和fragment都是自定义的
        routes.put("app", ARouter$$Group$$app.class);
        routes.put("fragment", ARouter$$Group$$fragment.class);
    }
}

/**========================================
 * // ARouter$$Interceptors$$app.java
 * 6、将拦截器加载到Map中(Warehouse.interceptorsIndex)
 *     1. key=priority优先级，例如: 8
 *     2. value = 拦截器的class对象
 *=========================================*/
public class ARouter$$Interceptors$$app implements IInterceptorGroup {
    @Override
    public void loadInto(Map<Integer, Class<? extends IInterceptor>> interceptors) {
        interceptors.put(8, MyInterceptor.class);
    }
}

/**========================================
 * // ARouter$$Providers$$arouterapi.java
 * 7、将Provider服务加载到privoders Map中(Warehouse.providersIndex)
 *=========================================*/
public class ARouter$$Providers$$arouterapi implements IProviderGroup {
    public ARouter$$Providers$$arouterapi() {
    }

    public void loadInto(Map<String, RouteMeta> providers) {
        providers.put("com.alibaba.android.arouter.facade.service.AutowiredService", RouteMeta.build(RouteType.PROVIDER, AutowiredServiceImpl.class, "/arouter/service/autowired", "arouter", (Map)null, -1, -2147483648));
        providers.put("com.alibaba.android.arouter.facade.service.InterceptorService", RouteMeta.build(RouteType.PROVIDER, InterceptorServiceImpl.class, "/arouter/service/interceptor", "arouter", (Map)null, -1, -2147483648));
    }
}

/**=======================
 * // _ARouter.java
 * 8、触发拦截器的初始化。实例化所有拦截器，调用其init方法，并将对象存储到列表Warehouse.interceptors中.
 *=======================*/
static void afterInit() {
    // Trigger interceptor init, use byName.
    interceptorService = (InterceptorService) ARouter.getInstance().build("/arouter/service/interceptor").navigation();
}

/**==========================================================
 * // InterceptorServiceImpl.java
 * 9、管理拦截器的服务。InterceptorService实现了IProvider接口。
 *==========================================================*/
@Route(path = "/arouter/service/interceptor")
public class InterceptorServiceImpl implements InterceptorService {
    private static boolean interceptorHasInit;
    private static final Object interceptorInitLock = new Object();

    /**==========================================================
     * 1. 初始化所有拦截器，调用其init方法，并且实例化拦截器对象并且存储到拦截器列表中(List<IInterceptor> interceptors)
     *==========================================================*/
    public void init(final Context context) {
        LogisticsCenter.executor.execute(new Runnable() {
            public void run() {
                if (MapUtils.isNotEmpty(Warehouse.interceptorsIndex)) {
                    // 2. 遍历Map: Warehouse.interceptorsIndex
                    for (Map.Entry<Integer, Class<? extends IInterceptor>> entry : Warehouse.interceptorsIndex.entrySet()) {
                        Class<? extends IInterceptor> interceptorClass = entry.getValue();
                        try {
                            // 3. 构建拦截器对象，调用其init方法
                            IInterceptor iInterceptor = interceptorClass.getConstructor().newInstance();
                            iInterceptor.init(context);
                            // 4. 将拦截器添加到List中
                            Warehouse.interceptors.add(iInterceptor);
                        } catch (Exception ex) {
                            throw new HandlerException(TAG + "ARouter init interceptor error! name = [" + interceptorClass.getName() + "], reason = [" + ex.getMessage() + "]");
                        }
                    }

                    interceptorHasInit = true;

                    synchronized (interceptorInitLock) {
                        interceptorInitLock.notifyAll();
                    }
                }
            }
        });
    }
    public void doInterceptions(final Postcard postcard, final InterceptorCallback callback) {...}
    private static void _excute(final int index, final CancelableCountDownLatch counter, final Postcard postcard) {...}
    private static void checkInterceptorsInitStatus() {...}
}
```

6、为什么不同module使用了相同的group名导致出现错误`There is no route match the path`?
> 1. 不同的Module都会生成不同的`IRouteGroup实现(如: ARouter$$Group$$fragment.class)`
> 1. 在加载`Root元素`的时候，会先后执行两次`put(xxx)`方法，但是因为key相同，因此前一个会被覆盖，导致前一个定义的路由无法找到。
> 1. 官方建议不同Module的group名不能相同。
```java
public class ARouter$$Root$$app implements IRouteRoot {
    @Override
    public void loadInto(Map<String, Class<? extends IRouteGroup>> routes) {
        routes.put("fragment", ARouter$$Group$$fragmentA.class);
    }
}
public class ARouter$$Root$$home implements IRouteRoot {
    @Override
    public void loadInto(Map<String, Class<? extends IRouteGroup>> routes) {
        routes.put("fragment", ARouter$$Group$$fragmentB.class);
    }
}
```

#### Warehouse

7、Warehouse的源码和作用分析
> 1. 存储了Providers、Interceptors以及Group相关的RouteMeta(路由元数据)
```java
class Warehouse {
    // 1、存储Group的类对象和Group行管的RouteMeta
    static Map<String, Class<? extends IRouteGroup>> groupsIndex = new HashMap<>();
    static Map<String, RouteMeta> routes = new HashMap<>();

    // 2、存储Provider(服务)
    static Map<String, RouteMeta> providersIndex = new HashMap<>();
    static Map<Class, IProvider> providers = new HashMap<>();

    // 3、存储拦截器，使用UniqueKeyTreeMap在key值相同时报错(key = 优先级)。
    static Map<Integer, Class<? extends IInterceptor>> interceptorsIndex = new UniqueKeyTreeMap<>("More than one interceptors use same priority [%s]");
    static List<IInterceptor> interceptors = new ArrayList<>();

    static void clear() {
        routes.clear();
        groupsIndex.clear();
        providers.clear();
        providersIndex.clear();
        interceptors.clear();
        interceptorsIndex.clear();
    }
}
```

8、IRouteGroup、IProvider、IInterceptor接口的作用?
```java
/**==========================================================
 * 1、路由分组(Group)需要实现IRouteGroup接口。
 *  用于将ActvityA、ActivityB、FragmentA、FragmentB的RouteMeta(路由元数据)
 *   以路径path为key值，存储到Map atlas中
 *==========================================================*/
public interface IRouteGroup {
    void loadInto(Map<String, RouteMeta> atlas);
}
/**==========================================================
 * 2、服务接口需要实现IProvider接口。
 *  init方法用于处理初始化工作。
 *==========================================================*/
public interface IProvider {
    void init(Context context);
}
/**==========================================================
 * 3、拦截器需要实现IInterceptor接口。该接口实现了IProvider接口。
 *  主要进行两个操作.
 *  1. init方法用于处理初始化工作。
 *  2. process方法进行拦截和处理工作。
 *==========================================================*/
public interface IInterceptor extends IProvider {
    void process(Postcard postcard, InterceptorCallback callback);
}
```

9、RouteMeta分析
> RouteMeta是一个数据bean，封装了被注解类的一些信息
```java
public class RouteMeta {
    /**=========================================
     * 1、路由类型。是一个枚举，表示被注解类的路由类型
     *   1. Activity
     *   2. Service // 目前不支持
     *   3. Provider
     *   4. Content Provider // 目前不支持
     *   5. Broastcast // 目前不支持
     *   6. Method
     *   7. Fragment
     *   8. UNKNOWN
     *========================================*/
    private RouteType type;         // Type of route
    private Element rawType;        // Raw type of route
    private Class<?> destination;   // Destination
    private String path;            // Path of route
    private String group;           // Group of route
    private int priority = -1;      // The smaller the number, the higher the priority
    private int extra;              // Extra data
    private String name;
    private Map<String, Autowired> injectConfig;  // Cache inject config.
    /**=========================================
     * 2、属性类型。包含了所有注解了Autowired的属性的信息。
     *   key为属性名，value为属性类型，ARouter将可被intent传递的数据类型定义了对应的int类型：
     *   BOOLEAN,BYTE,SHORT,INT,LONG,CHAR,FLOAT,DOUBLE,STRING,PARCELABLE,OBJECT分别对应0，1，2，3...
     *========================================*/
    private Map<String, Integer> paramsType;  // Param type
}

/**=========================================
 * 1、路由类型。枚举，表示被注解类的路由类型。
 *========================================*/
public enum RouteType {
    ACTIVITY(0, "android.app.Activity"),
    SERVICE(1, "android.app.Service"),
    PROVIDER(2, "com.alibaba.android.arouter.facade.template.IProvider"),
    CONTENT_PROVIDER(-1, "android.app.ContentProvider"),
    BOARDCAST(-1, ""),
    METHOD(-1, ""),
    FRAGMENT(-1, "android.app.Fragment"),
    UNKNOWN(-1, "Unknown route type");

    int id;
    String className;
}
```

#### ClassUtils.getFileNameByPackageName

6、ClassUtils.getFileNameByPackageName()源码分析
> 1. 找到app的dex，然后遍历出其中的属于com.alibaba.android.arouter.routes包下的所有类名.
> 1. 这些类都是编译期间生成的中间类。()
```java
public static Set<String> getFileNameByPackageName(Context context, final String packageName) throws PackageManager.NameNotFoundException, IOException, InterruptedException {
    final Set<String> classNames = new HashSet<>();

    // 1. 获取到所有dex的路径。
    List<String> paths = getSourcePaths(context);
    final CountDownLatch parserCtl = new CountDownLatch(paths.size());
    // 2. 遍历所有dex路径
    for (final String path : paths) {
        DefaultPoolExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                DexFile dexfile = null;
                try {
                    // 3. 加载Dex文件
                    if (path.endsWith(EXTRACTED_SUFFIX)) {
                        //NOT use new DexFile(path), because it will throw "permission error in /data/dalvik-cache"
                        dexfile = DexFile.loadDex(path, path + ".tmp", 0);
                    } else {
                        dexfile = new DexFile(path);
                    }

                    // 4. 找到Dex文件中所有以"com.alibaba.android.arouter.routes"开头的类，并将其类名存储到集合中
                    Enumeration<String> dexEntries = dexfile.entries();
                    while (dexEntries.hasMoreElements()) {
                        String className = dexEntries.nextElement();
                        if (className.startsWith(packageName)) {
                            classNames.add(className);
                        }
                    }
                } catch (Throwable ignore) {
                    Log.e("ARouter", "Scan map file in dex files made error.", ignore);
                } finally {
                    if (null != dexfile) {
                        try {
                            dexfile.close();
                        } catch (Throwable ignore) {
                        }
                    }
                    parserCtl.countDown();
                }
            }
        });
    }
    parserCtl.await();
    // 5. 返回"com.alibaba.android.arouter.routes"包下的所有类名的集合
    return classNames;
}
```
> 返回的`classNames`例如:
```
0 = "com.alibaba.android.arouter.routes.ARouter$$Root$$app"
1 = "com.alibaba.android.arouter.routes.ARouter$$Group$$app$1"
2 = "com.alibaba.android.arouter.routes.ARouter$$Group$$fragment"
3 = "com.alibaba.android.arouter.routes.ARouter$$Providers$$arouterapi"
4 = "com.alibaba.android.arouter.routes.ARouter$$Group$$app"
6 = "com.alibaba.android.arouter.routes.ARouter$$Group$$arouter"
5 = "com.alibaba.android.arouter.routes.ARouter$$Interceptors$$app"
7 = "com.alibaba.android.arouter.routes.ARouter$$Root$$arouterapi"
8 = "com.alibaba.android.arouter.routes.ARouter$$Providers$$app"
```

7、CountDownLatch的作用?
>1. CountDownLatch这个类能够使一个线程等待其他线程完成各自的工作后再执行。
>1. `getFileNameByPackageName`需要等所有的Dex路径都扫描好后，才返回类名的集合。

8、ClassUtils.getFileNameByPackageName()的效率问题和改进方法
> 1. 遍历所有Dex路径寻找指定包名下所有类的操作工作量过大，从而会导致效率问题。
> 1. arouter-register就是用来解决这个问题。

### 问题补充

1、为什么会出现错误: There is no route match the path
> 1. 需要调用`ARouter.openDebug()`方法将标志位`debuggable`设置为`true`。不进入Debug模式不会弹出Toast，只会打印日志。
> 1. 不同module的一级路径相同，导致moudle中的一级路径失效，因此跳转到第二个module的某个页面时出现该错误。

## 参考资料
1. [可能是最详细的ARouter源码分析](https://www.jianshu.com/p/bc4c34c6a06c)
1. [Java注解处理器](https://race604.com/annotation-processing/)
1. [路由方案之ARouter源码分析](https://blog.csdn.net/byhook/article/details/79945460)
1. [什么时候使用CountDownLatch](http://www.importnew.com/15731.html)
