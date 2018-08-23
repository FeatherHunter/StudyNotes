转载请注明链接: https://blog.csdn.net/feather_wch/article/details/51009871

本文介绍OkHttp的介绍、特点、基本使用、进阶使用(拦截器、Cookie管理)、源码解析异步请求、同步请求、复用连接池等内容。

# OkHttp详解

版本：2018/8/23-1(23:59)

---

[TOC]

## 介绍

1、OkHttp是什么?
> 1. 一个处理网络请求的开源项目
> 2. Android上最火热的轻量级框架
> 3. 由移动支付Square公司贡献

## 特点

2、OkHttp的特点
> 1. 支持同步、异步
> 2. 支持GZIP减少数据流量
> 3. 缓存响应数据：从而减少重复的网络请求
> 4. 自动重连：处理了代理服务器问题和SSL握手失败问题
> 5. 支持SPDY：1.共享同个Socket来处理同一个服务器的请求。 2.若SPDY不可用，则通过连接池来减少请求延时
> 6. 请求、处理速度快：基于NIO和Okio。
> 7. API使用方便简单：需要进一步封装。
> 1. 能够从许多连接问题中，自动恢复。如：服务器配置了多个IP，第一个IP连接失败后okhttp会自动尝试下一个IP

3、OkHttp的应用场景
> 数据量大的重量级网络请求

## 基本使用

4、OkHttp集成
>okio是okhttp的io基础，因此也需要集成
```XML
//build.gradle
compile 'com.squareup.okhttp3:okhttp:3.2.0'
compile 'com.squareup.okio:okio:1.7.0'
```

5、Okio是什么？
> square基于IO、NIO的一个高效处理数据流的开源库。

6、NIO是什么？
> 非阻塞性IO

### GET请求

7、OkHttp的GET请求
```java
//1. 通过Builder构建Request
Request.Builder requestBuilder = new Request.Builder().url("https://www.baidu.com/");
//2. 设定方法=GET
requestBuilder.method("GET", null);
//3. 构建Request
Request request = requestBuilder.build();
//4. 创建OKHttpClient客户端
OkHttpClient okHttpClient = new OkHttpClient();
//5. 创建Call请求
Call call = okHttpClient.newCall(request);
//6. 通过call的enqueue将请求入队(enqueue为异步方法， execute为同步方法)
call.enqueue(new Callback() {
    @Override
    public void onFailure(Call call, IOException e) {
    }
    @Override
    public void onResponse(Call call, Response response) throws IOException {
         //TODO 接收返回值并进行处理
        String str = response.body().string();
        //注意！该回调不在UI线程中
        Log.d("HttpActivity", str);
    }
});
```

### POST请求
8、OkHttp的POST请求
```java
//1. 通过FormBody创建RequestBody
        RequestBody requestBody = new FormBody.Builder()
                .add("ip", getIPAddress(this)) //本机IP
                .build();
        //2. 创建Request
        Request request = new Request.Builder()
                .url("http://ip.taobao.com/service/getIpInfo.php")
                .post(requestBody)
                .build();
        //3. 创建OKHttpClient客户端
        OkHttpClient okHttpClient = new OkHttpClient();
        //4. 创建Call请求
        Call call = okHttpClient.newCall(request);
        //5. 通过call的enqueue将请求入队(enqueue为异步方法， execute为同步方法)
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //TODO 接收返回值并进行处理
                String str = response.body().string();
                //注意！该回调不在UI线程中
                Log.d("HttpActivity", str);
            }
        });
```
>获取本机IP的方法（需要网络权限）
```java
    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }
```

### 上传文件
9、OkHttp上传文件
```java
//1. 创建媒体类型
    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
    public void upload(String url, String filename){
        String filepath = "";
        //2. 获取到SD卡根目录中的文件
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            filepath = Environment.getExternalStorageDirectory().getAbsolutePath();
        }else{
            return;
        }
        File file = new File(filepath, filename);
        //3. 创建请求(传入需要上传的File)
        Request request = new Request.Builder()
                .url(url) //"https://www.baidu.com"
                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, file))
                .build();
        //4. 异步上传文件(同步上传需要用execute())
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //TODO 接收返回值并进行处理.注意！该回调不在UI线程中
                String str = response.body().string();
                Log.d("HttpActivity", str);
            }
        });
    }
```

### 异步下载
10、OkHttp异步下载
```java
public void download(String url, final String filename){
    //1. 创建请求(目标的url)
    Request request = new Request.Builder()
            .url(url) //"https://.../xxxx.jpg"
            .build();
    //2. 异步下载文件
    new OkHttpClient().newCall(request).enqueue(new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            //3. 得到Response中的流
            InputStream inputStream = response.body().byteStream();
            //4. 创建保存网络数据的文件
            String filepath = "";
            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                filepath = Environment.getExternalStorageDirectory().getAbsolutePath();
            }else{
                filepath = Environment.getDataDirectory().getAbsolutePath();
            }
            File file = new File(filepath, filename);
            //5. 下载数据到文件中
            if(null != file){
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                byte[] buffer = new byte[2048];
                int len = 0;
                while((len = inputStream.read(buffer)) != -1){
                    fileOutputStream.write(buffer, 0, len);
                }
                fileOutputStream.flush();
            }
            Log.d("HttpActivity", "Downloaded");
        }
    });
}
```

### 多份数据上传
11、OKHttp同时上传多份数据(字符串、图片等等)
```java
//1. 创建媒体类型
public static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
public void multiUpload(String url){
    //2. 请求主体：同时上传字符串数据和图片数据
    RequestBody requestBody = new MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("title", "some jpg") //上传的字符串(key, value)
            .addFormDataPart("image"  //* key值
                    ,"feather.jpg" //* 文件名
                    , RequestBody.create(MEDIA_TYPE_PNG, new File("/sdcard/feather.jpg")) //* 需要上传的文件
                 ) //同时也上传图片
            .build();
    //3. 创建请求
    Request request = new Request.Builder()
            .header("Authorization", "Client-ID" + "...")
            .url(url) //"https://www.baidu.com"
            .post(requestBody)
            .build();
    //4. 异步上传数据和文件(同步上传需要用execute())
    new OkHttpClient().newCall(request).enqueue(new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            //TODO 接收返回值并进行处理.注意！该回调不在UI线程中
            String str = response.body().string();
            Log.d("HttpActivity", str);
        }
    });
}
```

### 超时时间与缓存

12、OkHttp设置连接、读取、写入的超时时间，以及设置缓存
```java
int cacheSize = 10 * 1024 * 1024;
File sdcacheFile = getExternalCacheDir();
//1. 需要通过Builder创建OkHttpClient客户端---才能设置超时和缓存
OkHttpClient.Builder builder = new OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .cache(new Cache(sdcacheFile.getAbsoluteFile(), cacheSize));
OkHttpClient okHttpClient = builder.build();
```

### 取消请求

13、OkHttp如何取消请求
>1. `OkHttp3`的`Callback`的回调方法里面有个参数是`Call` 这个call可以单独取消相应的请求，随便在onFailure或者onResponse方法内部执行`call.cancel()`都可以。
>2. 如果想`取消所有的请求`，则可以`okHttpClient.dispatcher().cancelAll();`进行取消。

## Interceptors

14、什么是Interceptors拦截器？
>1. 是一种强大的机制，可以监视、重写、重试call请求。
>2. 通常情况下，用于添加、移除、转换请求和响应的头部信息。


15、最简单拦截器的实现方法
```java
//会拦截 请求和返回的数据
public class SignInInterceptor implements Interceptor{
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        long t1 = System.nanoTime();
        Logger.getGlobal().info(String.format("Sending request %s on %s%n%s",
                request.url(), chain.connection(), request.headers()));

        Response response = chain.proceed(request);

        long t2 = System.nanoTime();
        Logger.getGlobal().info(String.format("Received response for %s in %.1fms%n%s",
                response.request().url(), (t2 - t1) / 1e6d, response.headers()));

        return response;
    }
}

```
>添加拦截器
```java
OkHttpClient client = new OkHttpClient.Builder()
    .addInterceptor(new SignInInterceptor())
    .build();
```

16、拦截器的分类和在流程上的区别
> 1. 应用拦截器：拦截OkHttp核心和应用间的请求与响应。不需要关心重定向和重试的中间响应。
> 2. 网络拦截器：拦截OkHttp核心和网络之前的请求与响应。
![interceptors](https://raw.githubusercontent.com/wiki/square/okhttp/interceptors@2x.png)

17、拦截器的应用场景
> 1. 可以在拦截器中进行请求体压缩(如果Web服务端支持请求体的压缩)。
> 2. 可以将域名替换为IP地址
> 3. 可以在在请求头中添加host属性
> 4. 可以在请求头中添加应用相关的公共参数：设备ID、版本号等

## Cookie管理

18、如何持久化Cookie？
> 1. 手动保存Cookie，从Response中取出Header里面的Cookie，保存到本地(SharePreference)。发送请求时利用`Interceptor拦截器`添加到头部(保存Cookie也可以采用拦截器拦截，然后本地保存)。为了对应不同域名，可以将域名作为key对Cookie进行保存。
> 2. OkHttp3的Cookie管理。

19、OkHttp3新增的CookieJar
>1. okhttp3中对Cookie管理提供了额外的支持
> 2. 实现CookieJar接口中的两个方法(发送请求/接到响应)，实现Cookie的内存or本地缓存。

20、CookieJar的实现
>1-实现
```java
public class MyCookieJar implements CookieJar{

    private static HashMap<String, List<Cookie>> mCookieStore = new HashMap<>();

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        mCookieStore.put(url.host(), cookies);
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> cookies = mCookieStore.get(url.host());
        return cookies != null ? cookies : new ArrayList<Cookie>();
    }
}
```
>2-使用
```java
OkHttpClient client = new OkHttpClient.Builder()
                            .cookieJar(new MyCookieJar())
                            .build();
```

## 源码解析

### Call
21、Call的创建源码
```java
  //1、创建Call
  Call call = okHttpClient.newCall(request);
  //2、OkHttpClient.java
  @Override public Call newCall(Request request) {
    return new RealCall(this, request);
  }
  //3、RealCall.java
  protected RealCall(OkHttpClient client, Request originalRequest) {
    this.client = client;
    this.originalRequest = originalRequest;
  }
```

22、Call是如何创建的？
> 1. 本质是通过`RealCall`进行创建。
> 2. 在`RealCall`中将OkHttpClient和request进行了保存。

### 异步请求

23、异步请求源码分析
>1-异步请求主体流程
```java
    //RealCall.java
    void enqueue(Callback responseCallback, boolean forWebSocket) {
        //1. 转交给Dispatcher执行enqueue
        client.dispatcher().enqueue(new RealCall.AsyncCall(responseCallback, forWebSocket));
    }
    //Dispatcher.java
    synchronized void enqueue(RealCall.AsyncCall call) {
        //1. 正在运行的异步请求数 < 64 并且 同一个Host的请求数 < 5时
        if (runningAsyncCalls.size() < maxRequests && runningCallsForHost(call) < maxRequestsPerHost) {
            //2. 将call放到正在运行的异步队列中
            runningAsyncCalls.add(call);
            //3. 线程池执行该任务
            executorService().execute(call);
        } else {
            //4. 已满，就添加到待运行的异步任务队列中
            readyAsyncCalls.add(call);
        }
    }
    //Dispatcher.java---将call的host(域名)和运行异步任务队列中的host进行比对，返回相同的任务数
    private int runningCallsForHost(RealCall.AsyncCall call) {
        int result = 0;
        for (RealCall.AsyncCall c : runningAsyncCalls) {
            if (c.host().equals(call.host())) result++;
        }
        return result;
    }
    /**
     * //RealCall.java-内部类AsyncCall
     * 1. AsyncCall的父类NamedRunnable继承自Runnable
     * 2. 在Runnable的run()方法中会执行execute()
     * 3. execute()中完成了异步任务的执行
     */
    final class AsyncCall extends NamedRunnable {
        @Override protected void execute() {
            boolean signalledCallback = false;
            try {
                // 1. 请求网络
                Response response = getResponseWithInterceptorChain(forWebSocket);
                // 2. 成功: 回调Callback的onResponse
                responseCallback.onResponse(RealCall.this, response);
            } catch (IOException e) {
                ...
                // 3. 失败：回调Callback的onFailure
                responseCallback.onFailure(RealCall.this, e);
            } finally {
                // 4. Dispatcher将执行完的任务进行移除，并将待执行任务添加到运行中队列内部，并且开启任务的执行
                client.dispatcher().finished(this);
            }
        }
    }

    //RealCall.java
    private Response getResponseWithInterceptorChain(boolean forWebSocket) throws IOException {
        //1. 创建拦截器链
        Interceptor.Chain chain = new RealCall.ApplicationInterceptorChain(0, originalRequest, forWebSocket);
        //2. 执行拦截器链的proceed
        return chain.proceed(originalRequest);
    }
    //RealCall.java内部类: ApplicationInterceptorChain
    @Override public Response proceed(Request request) throws IOException {
        //1. 从拦截器列表中取出拦截器，迭代执行器拦截前动作。
        if (index < client.interceptors().size()) {
            Interceptor.Chain chain = new RealCall.ApplicationInterceptorChain(index + 1, request, forWebSocket);
            // 取出拦截器
            Interceptor interceptor = client.interceptors().get(index);
            /**========================================================
             * 存在多个拦截器时，会递归调用所有拦截器的intercept方法
             *  1. 调用到自定义拦截器的intercept()---MyInterceptor implements Interceptor
             *  2. 内部执行chain.proceed(request)方法
             *  3. 执行ApplicationInterceptorChain的proceed()，回到该方法，也就是递归调用
             *========================================================*/
            Response interceptedResponse = interceptor.intercept(chain);
            //返回的其实是最后拦截器执行getResponse的返回值
            return interceptedResponse;
        }

        //2. 网络请求---在最后一个拦截器的intercept()->proceed()中执行该处
        return getResponse(request, forWebSocket);
    }

    //RealCall.java---执行request请求，并且返回响应结果
    Response getResponse(Request request, boolean forWebSocket) throws IOException {
        ...
        // 1. 创建HttpEngine
        engine = new HttpEngine(client, request, ...);
        while (true) {
            try {
                // 2. 发送Request请求
                engine.sendRequest();
                // 3. 获取Response响应
                engine.readResponse();
            }  catch (RouteException e / IOException e) {
                // 4. 失败重连，重新获取HttpEngine并且continue重新进行请求
                HttpEngine retryEngine = engine.recover(e, null);
                ...
                continue;
            } finally {
                // 5. 遭遇异常情况下，需要释放掉资源
                if (releaseConnection) {
                    StreamAllocation streamAllocation = engine.close();
                    streamAllocation.release();
                }
            }
            ...
            // 6. 其他异常情况，也是进行失败重连
            engine = new HttpEngine(client, request, ...);
        }
    }

    /**=================================================
     * // HttpEngine.java
     * 1. 获取到旧Engine分配的Stream
     * 2. 用该Stream创建新Engine
     *=========================================*/
    public HttpEngine recover(IOException e, Sink requestBodyOut) {
        ...
        // 1. 获取到旧Engine分配的Stream
        StreamAllocation streamAllocation = close();
        // 2. 用该Stream创建新Engine
        return new HttpEngine(client, ..., streamAllocation, ...);
    }

    //Dispatcher.java
    synchronized void finished(RealCall.AsyncCall call) {
        //1. 将任务从[正在运行的异步任务队列]中移除
        if (!runningAsyncCalls.remove(call)) throw new AssertionError("AsyncCall wasn't running!");
        /**============================================
         * 2. 取出一个[待运行的异步任务]并且添加到[正在运行的异步任务队列中]
         * 3. 通过线程池执行新任务
         *==================================*/
        promoteCalls();
    }
    //Dispatcher.java
    private void promoteCalls() {
        //1. 判断是否超过最大的并发任务数
        if (runningAsyncCalls.size() >= maxRequests) return;
        //2. 判断是否存在待执行的异步任务
        if (readyAsyncCalls.isEmpty()) return;
        //3. 取出待执行的异步任务，执行任务，直到已经达到最大并发任务数
        for (Iterator<RealCall.AsyncCall> i = readyAsyncCalls.iterator(); i.hasNext(); ) {
            RealCall.AsyncCall call = i.next();
            if (runningCallsForHost(call) < maxRequestsPerHost) {
                // 取出待执行的异步任务
                i.remove();
                // 添加到正在执行的队列中
                runningAsyncCalls.add(call);
                // 线程池运行任务
                executorService().execute(call);
            }
            // 判断是否超过最大的并发任务数
            if (runningAsyncCalls.size() >= maxRequests) return; // Reached max capacity.
        }
    }
```
>2-HttpEngine进行请求的发送
```java
    //HttpEngine.java
    public void sendRequest() throws RequestException, RouteException, IOException {
        Request request = networkRequest(userRequest);
        /**===========================================================================
         * 1. 获取Client中的Cache，Cache在初始化时会读取缓存目录中曾经请求过的所有信息。
         *   1- Internal.instance.internalCache实现就是OkHttpClient的internalCache()方法
         *   2- responseCache：就是Cache内部的internalCache
         *===========================================================================*/
        InternalCache responseCache = Internal.instance.internalCache(client);
        // 2. responseCache.get(request)： 获取到上次与服务器交互时缓存的Response。从中可以读取到Header
        Response cacheCandidate = responseCache != null
                ? responseCache.get(request) //
                : null;
        // 3. 获取到缓存策略
        cacheStrategy = new CacheStrategy.Factory(now, request, cacheCandidate).get();
        // 4. 网络Request请求；如果为null则表示这次调用没有使用网络
        networkRequest = cacheStrategy.networkRequest;
        // 5. 缓存的Response或者过期失效；如果为null则表示不使用缓存
        cacheResponse = cacheStrategy.cacheResponse;
        // 6. 如果既没有使用网络，并存缓存不存在或者过期。直接新建并范围报504错误的Response(网关超时)。
        if (networkRequest == null && cacheResponse == null) {
            userResponse = new Response.Builder().request(userRequest)
                    .code(504)
                    .body(EMPTY_BODY)
                    .build();
            return;
        }
        // 7. 网络Request不存在，就直接返回缓存的Response
        if (networkRequest == null) {
            userResponse = cacheResponse.newBuilder().request(userRequest).build();
            // 8. Gzip解压缩
            userResponse = unzip(userResponse);
            return;
        }
        // 9. 网络存在的情况下，都进行网络请求
        httpStream = connect();
        httpStream.setHttpEngine(this);
        ...
    }

    //OkHttpClient.java
    InternalCache internalCache() {
        return cache != null ? cache.internalCache : internalCache;
    }
    //OkHttpClient.java
    final Cache cache;
    /**=================================
     *  //Cache.java
     *  缓存策略：从DiskLruCache中获取上次请求对应的所有信息
     *=================================*/
    private final DiskLruCache cache;
    Response get(Request request) {
        // 1. key就是请求中url的md5：  return Util.md5Hex(request.url().toString())
        String key = urlToKey(request);
        DiskLruCache.Snapshot snapshot;
        // 2. 根据key获取到快照
        snapshot = cache.get(key);
        // 3. 从快照中获取到Entry(Cache.java的内部类)
        Cache.Entry entry = new Cache.Entry(snapshot.getSource(ENTRY_METADATA));
        // 4. 将快照中的数据作为Response的body，以及其他信息，组合成一个Response
        Response response = entry.response(snapshot);
        // 5. 将Response返回
        return response;
    }

    //Cache.java的内部类：Entry---保存了url、响应的头、请求的方法等。
    public Entry(Response response) {
        this.url = response.request().url().toString();
        this.requestMethod = response.request().method();
        this.protocol = response.protocol();
        this.message = response.message();
        this.responseHeaders = response.headers();
        ...
    }
```
>3-HttpEngine进行响应信息的接收
```java
```

24、OkHttp异步请求的流程图和要点
![OkHttp异步请求的流程图和要点](https://github.com/FeatherHunter/StudyNotes/blob/master/assets/OkHttp_AsyncCall.jpg?raw=true)
> 1. 调用到`RealCall`的enqueue
> 2. Dispatcher.enqueue: 会判断当前异步任务数是否<64
> 3. runningCallsForHost：遍历运行中任务，比较和这次请求的Host(域名)一样的有多少，是否 < 5
> 4. (2)(3)任何一个条件不满足，就直接添加到【待运行的异步任务队列】
> 5. runningAsyncCalls.add(call)： 将任务添加到【运行中的异步任务队列】
> 6. executorService().execute(call)： 线程池中执行该任务
> 7. AsyncCall： 本质是Runnable，run()中执行execute()
> 8. AsyncCall.execute(): 进行网络请求、请求成功/失败都会回调对应方法、 进行任务提升
> 9. getResponseWithInterceptorChain(): 进行网络请求
> 10. responseCallback.onResponse(): 请求成功的回调
> 11. responseCallback.onFailure(): 请求失败的回调
> 12. client.dispatcher().finished(this)： 将任务从【正在运行的异步任务队列】中移除
> 13. promoteCalls(): 继续进行(2)(3)的条件判断，满足条件就将待运行任务提升至运行中的任务。
> 14. 1、从【待运行队列】中移除任务 2、将任务添加到【运行中任务队列】 3、线程池执行任务
> 15. RealCall.ApplicationInterceptorChain(): 创建Interceptor.Chain
> 16. chain.proceed(): 调用拦截器链的proceed进行后续请求工作。
> 17. 层层调用interceptor的intercept方法。
> 18. getResponse(): 在最内存拦截器处调用该方法，进行网络请求。
> 19. new HttpEngine(): 创建HttpEngine
> 20. engine.sendRequest()： 发送请求
> 21. engine.readResponse()： 接受响应信息
> 22. 请求成功直接返回；不成功需要进行失败重连。
> 23. engine.recover(): 进行Stream的复用
> 24. 获取到请求失败的旧Engine分配的Stream
> 25. 使用该Stream创建新的HttpEngine
> 26. 重复(20)(21)(22)的任务

#### Dispatcher

25、Dispatcher是什么？
> 1. 用于控制并发的请求。
> 2. 定义了最大并发数：64
> 3. 定义了每个主机的最大请求数：5
> 4. 内部具有消费者线程池，可以构造时指定。默认的线程池类似于CachedThreadPool，适合大量且耗时较少的任务。
> 5. 内部具有3个队列(正在运行的异步请求队列、即将运行的异步请求队列、正在运行的同步请求队列)
```java
public final class Dispatcher {
  private int maxRequests = 64;
  private int maxRequestsPerHost = 5;
  // 线程池
  private ExecutorService executorService;
  // 将要运行的异步请求队列
  private final Deque<AsyncCall> readyAsyncCalls = new ArrayDeque<>();
  // 正在运行的异步请求队列
  private final Deque<AsyncCall> runningAsyncCalls = new ArrayDeque<>();
  // 正在运行的同步请求队列
  private final Deque<RealCall> runningSyncCalls = new ArrayDeque<>();

  public Dispatcher(ExecutorService executorService) {
    this.executorService = executorService;
  }

  public Dispatcher() {
  }
  // 用默认线程池进行构造。
  public synchronized ExecutorService executorService() {
    if (executorService == null) {
      executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
          new SynchronousQueue<Runnable>(), Util.threadFactory("OkHttp Dispatcher", false));
    }
    return executorService;
  }
}
```

#### AsyncCall

26、AsyncCall是什么？
> 1. RealCall的内部类
> 2. 继承自`NamedRunnable`，间接继承`Runnable`
> 3. 在run()中会执行execute()：完成了异步任务的执行
> 4. 用于Dispatcher内部的待运行/运行中的异步任务队列

#### HttpEngine

27、HttpEngine是什么？

### 同步请求

28、同步请求源码分析
> `call.execute(xxx);`
```java
    //RealCall.java
    @Override public Response execute() throws IOException {
        ...
        try {
            // 1. 添加到【运行中的同步队列】
            client.dispatcher().executed(this);
            // 2. 请求网路
            Response result = getResponseWithInterceptorChain(false);
            return result;
        } finally {
            // 3. 从【运行中的同步队列】中移除该任务
            client.dispatcher().finished(this);
        }
    }

    //Dispatcher.java---添加到【运行中的同步队列】
    synchronized void executed(RealCall call) {
        runningSyncCalls.add(call);
    }

    //Dispatcher.java---从【运行中的同步队列】中移除该任务
    synchronized void finished(Call call) {
        if (!runningSyncCalls.remove(call)) throw new AssertionError("Call wasn't in-flight!");
    }
```
>1. 添加到【运行中的同步队列】
>2. getResponseWithInterceptorChain()进行网络请求
>3. 从【运行中的同步队列】中移除该任务

## 补充题

1、如何使用OkHttp进行异步网络请求，并根据请求结果刷新UI
> 1. 通过构造器创建RequestBody
> 2. 创建Request
> 3. 创建OkHttpClient
> 4. 创建Call
> 5. 发起Call的enqueue异步网络请求
> 6. 在response的回调中根据数据改变UI(OKHTTP3.0中不需要切换线程)

2、可否介绍一下OkHttp的整个异步请求流程


3、OkHttp对于网络请求都有哪些优化，如何实现的

4、OkHttp框架中都用到了哪些设计模式

5、OkHttp的缓存策略是什么？

6、OkHttp底层是如何实现缓存的？
> 1. HttpClient的sendRequest完成了实际的请求工作。
> 2. 采用DiskLruCache进行缓存
> 3. key = 请求url中的md5 value = Snapshot(存储了所有响应的信息---包括url、响应头、请求的方法、protocol等)
> 4. 在`HttpClient.readResponse()`会对数据进行缓存。

## 参考资料
1. [OkHttp拦截器-官方github-wiki](https://github.com/square/okhttp/wiki/Interceptors)
