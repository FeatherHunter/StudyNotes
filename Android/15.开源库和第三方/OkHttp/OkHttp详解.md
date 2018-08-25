转载请注明链接: https://blog.csdn.net/feather_wch/article/details/51009871

本文介绍OkHttp的介绍、特点、基本使用、进阶使用(拦截器、Cookie管理)、源码解析异步请求、同步请求、复用连接池等内容。

如果有帮助的话，请点个赞！万分感谢！

# OkHttp详解

版本：2018/8/26-1(3:20)

---

[TOC]

## 介绍(3)

1、OkHttp是什么?
> 1. 一个处理网络请求的开源项目
> 2. Android上最火热的轻量级框架
> 3. 由移动支付Square公司贡献

### 特点

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

## 基本使用(10)

1、OkHttp集成
>okio是okhttp的io基础，因此也需要集成
```XML
//build.gradle
compile 'com.squareup.okhttp3:okhttp:3.2.0'
compile 'com.squareup.okio:okio:1.7.0'
```

2、Okio是什么？
> square基于IO、NIO的一个高效处理数据流的开源库。

3、NIO是什么？
> 非阻塞性IO

### GET请求

4、OkHttp的GET请求
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
5、OkHttp的POST请求
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
6、OkHttp上传文件
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
7、OkHttp异步下载
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
8、OKHttp同时上传多份数据(字符串、图片等等)
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

9、OkHttp设置连接、读取、写入的超时时间，以及设置缓存
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

10、OkHttp如何取消请求
>1. `OkHttp3`的`Callback`的回调方法里面有个参数是`Call` 这个call可以单独取消相应的请求，随便在onFailure或者onResponse方法内部执行`call.cancel()`都可以。
>2. 如果想`取消所有的请求`，则可以`okHttpClient.dispatcher().cancelAll();`进行取消。

## 进阶使用(11)

### Interceptors

1、什么是Interceptors拦截器？
>1. 是一种强大的机制，可以监视、重写、重试call请求。
>2. 通常情况下，用于添加、移除、转换请求和响应的头部信息。


2、最简单拦截器的实现方法
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

3、拦截器的分类和在流程上的区别
> 1. 应用拦截器：拦截OkHttp核心和应用间的请求与响应。不需要关心重定向和重试的中间响应。
> 2. 网络拦截器：拦截OkHttp核心和网络之前的请求与响应。
![interceptors](https://raw.githubusercontent.com/wiki/square/okhttp/interceptors@2x.png)

4、添加应用拦截器: addInterceptor
```java
OkHttpClient client = new OkHttpClient.Builder()
    .addInterceptor(new SignInInterceptor())
    .build();
```

5、添加网络拦截器: addNetworkInterceptor
```java
OkHttpClient client = new OkHttpClient.Builder()
    .addNetworkInterceptor(new SignInInterceptor())
    .build();
```

6、拦截器的应用场景
> 1. 可以在拦截器中进行请求体压缩(如果Web服务端支持请求体的压缩)。
> 2. 可以将域名替换为IP地址
> 3. 可以在在请求头中添加host属性
> 4. 可以在请求头中添加应用相关的公共参数：设备ID、版本号等

7、应用拦截器是在哪里进行处理的？
> 1. RealCall的getResponseWithInterceptorCahin()中
> 1. 大致流程：从拦截器链中取出拦截器，依次进行递归调用。
```java
 //RealCall.java
private Response getResponseWithInterceptorChain(){...}
```

### Cookie管理

8、如何持久化Cookie？
> 1. 手动保存Cookie，从Response中取出Header里面的Cookie，保存到本地(SharePreference)。发送请求时利用`Interceptor拦截器`添加到头部(保存Cookie也可以采用拦截器拦截，然后本地保存)。为了对应不同域名，可以将域名作为key对Cookie进行保存。
> 2. OkHttp3的Cookie管理。

9、OkHttp3新增的CookieJar
>1. okhttp3中对Cookie管理提供了额外的支持
> 2. 实现CookieJar接口中的两个方法(发送请求/接到响应)，实现Cookie的内存or本地缓存。

10、CookieJar的实现
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

11、CookieJar的saveFromeResponse()在哪里被调用？
> 1. HttpClient.readResponse()->receiveHeaders(networkResponse.headers())
> 1. receiveHeaders()中从response的headers中解析出Cookie

## 源码解析(21)

### Call
1、Call的创建源码
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

2、Call是如何创建的？
> 1. 本质是通过`RealCall`进行创建。
> 2. 在`RealCall`中将OkHttpClient和request进行了保存。

### 异步请求

3、异步请求源码分析
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
    @Override
    public Response proceed(Request request) throws IOException {
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
            // 返回Response
            Response response = engine.getResponse();
            Request followUp = engine.followUpRequest();
            if (followUp == null) {
              if (!forWebSocket) {
                engine.releaseStreamAllocation();
              }
              return response;
            }
            ...
            // 6. 其他异常情况，也是进行失败重连
            engine = new HttpEngine(client, request, ...);
        }
    }
```

4、OkHttp发起异步请求时，调用的call.enqueue方法做了哪些事情？
> 1. 在Call创建的时候，在内部创建了RealCall，并且将OkHttpClient和request保存到了内部。
> 2. 执行Call.enqueue()，内部是执行的RealCall.enqueue()
> 3. RealCall.enqueue()内部直接转交给Dispatcher执行enqueue()方法
> 4. Dsipatcher内部做了会去判断是否达到了最大并发任务数64，以及同一个主机的请求数是否达到了5。
> 5. 都没有达到：加入到正在执行的异步请求队列。然后调用线程池去执行这个任务。
> 6. 达到：加入到待执行的异步请求队列

5、OkHttp是如何发起实际的网络请求的？OkHttp是如何处理拦截器/拦截器链的？
> 1. RealCall的getResponseWithInterceptorCahin()发起了实际的网络请求。
> 1. 会创建ApplicationInterceptorChain对象，并执行其proceed()方法
> 1. proceed方法会从request对应的拦截器列表中取出第一个拦截器，执行其intercept方法
> 1. intercept()内部会去执行ApplicationInterceptorChain.proceed()方法，然后取出第二个拦截器。依次层层递归调用。
> 1. 最终在最后一个拦截器之后，执行了ApplicationInterceptorChain.proceed()中的最后一行代码：`getResponse(request, forWebSocket)`
> 1. getResponse真正发起了请求，并且获取到了Reponse。

6、RealCall的getResponse是如何进行网络请求的？
> 1. 通过HttpEngine进行网络请求
> 1. 内部有一个While循环
> 1. engine.sendRequest()：发送Request请求
> 1. engine.readResponse()： 获取Response响应
> 1. 如果出现异常，会通过engine.recover()进行失败重连。

7、OkHttp异步请求的流程图和要点
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

8、Dispatcher是什么？
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

9、Dispatcher在请求任务完成后，如何进行的清理工作？
> 1. getResponseWithInterceptorChain()进行网络请求后，会调用Dispatcher.finished()进行清理工作。
> 1. 会将任务从正在运行的异步任务队列中移除
> 1. 在满足最大并发数和主机最大请求数的情况下，将待执行的异步任务进行提升到正在运行的异步任务队列，并且通过线程池执行该任务。
```java
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

#### AsyncCall

10、AsyncCall是什么？
> 1. RealCall的内部类
> 2. 继承自`NamedRunnable`，间接继承`Runnable`
> 3. 在run()中会执行execute()：完成了异步任务的执行
> 4. 用于Dispatcher内部的待运行/运行中的异步任务队列

11、AysncCall的execute方法中做了哪些工作？(4个)
> 1-请求网络
```java
Response response = getResponseWithInterceptorChain(forWebSocket);
```
> 2-请求成功: 回调Callback的onResponse
```java
responseCallback.onResponse(RealCall.this, response);
```
> 3-请求失败: 回调Callback的onFailure
```java
responseCallback.onFailure(RealCall.this, e);
```
> 4-Dispatcher将执行完的任务进行移除，并将待执行任务添加到运行中队列内部，并且开启任务的执行。(finally中一定执行)
```java
client.dispatcher().finished(this);
```

#### HttpEngine

12、HttpEngine是什么？
> 1. 处理单个Http的请求和响应

13、HttpEngine.sendRequest()发送请求的源码分析
> 1. sendRequest(): 并不会真正的发送请求，而是找到合适的Socket封装到了HttpStream中。
> 1. Internal.instance.internalCache(client)：获取到客户端中的Cache，Cache在初始化时会读取缓存目录中曾经请求过的所有信息。
> 1. responseCache.get(request)：获取到上次与服务器交互时缓存的Response。
> 1. new CacheStrategy.Factory： 获取到缓存策略
> 1. if (networkRequest == null && cacheResponse == null)： 如果既没有使用网络，也没有缓存(或过期)。直接新建并且返回报504错误的Response
> 1. 如果仅仅是没有网络，会获取到缓存的Response，进行Gzip解压后返回。
> 1. 如果有网络，就会调用connect进行连接，并且获取到httpstream。
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
        // 6. 如果既没有使用网络，并且缓存不存在或者过期。直接新建并返回报504错误的Response(网关超时)。
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
        // 9. 网络存在的情况下，会进行连接(找到合适的Socket封装到了HttpStream中)
        httpStream = connect();
        httpStream.setHttpEngine(this);
        // 10. 返回并且保存request Body可以写入的output stream
        requestBodyOut = httpStream.createRequestBody(networkRequest, contentLength);
        ...
    }
```

14、HttpEngine.readResponse()获取响应信息的源码分析
```java
    /**====================================================
     * // HttpEngine.java
     * 1. 刷新剩下的request header和request body
     * 2. 解析Http response header
     * 3. 开始读取Http response body
     *==============================================*/
    public void readResponse() throws IOException {
        // 1、第一次请求是null，不会执行。
        if (userResponse != null) {
            return; // Already ready.
        }
        // 2、没有网络，但是有缓存的Response。会要求需要先调用sendRequest
        if (networkRequest == null && cacheResponse == null) {
            throw new IllegalStateException("call sendRequest() first!");
        }
        // 3、没有网络
        if (networkRequest == null) {
            return; // No network response to read.
        }
        Response networkResponse;
        // 4、从Call.enqueue->RealCall.enqueue中，设置forWebSocket = false
        if (forWebSocket) {
            httpStream.writeRequestHeaders(networkRequest);
            networkResponse = readNetworkResponse();
        }
        // 5、默认callerWritesRequestBody = false，一定会进入该代码块
        else if (!callerWritesRequestBody) {
            // 6、调用NetworkInterceptorChain的proceed()进行网络请求，并且返回Response
            networkResponse = new HttpEngine.NetworkInterceptorChain(0, networkRequest).proceed(networkRequest);
        } else {
            //xxx
        }
        // 7、回调cookieJar.saveFromResponse()方法
        receiveHeaders(networkResponse.headers());

        // 8、存在缓存的Response
        if (cacheResponse != null) {
            // 9、判断是使用缓存Response还是网络Response
            if (validate(cacheResponse, networkResponse)) {
                // 10、继续使用缓存的Response
                userResponse = cacheResponse.newBuilder()
                        .request(userRequest)
                        .priorResponse(stripBody(priorResponse))
                        .headers(combine(cacheResponse.headers(), networkResponse.headers()))
                        .cacheResponse(stripBody(cacheResponse))
                        .networkResponse(stripBody(networkResponse))
                        .build();
                // 11、关闭网络Response的连接
                networkResponse.body().close();
                releaseStreamAllocation();
                // 12、更新缓存
                InternalCache responseCache = Internal.instance.internalCache(client);
                responseCache.update(cacheResponse, stripBody(userResponse));
                // 13、Gzip解压后，返回该Response
                userResponse = unzip(userResponse);
                return;
            }
        }
        // 14、没有缓存或者缓存已经过期
        userResponse = networkResponse.newBuilder()
                .request(userRequest)
                .priorResponse(stripBody(priorResponse))
                .cacheResponse(stripBody(cacheResponse))
                .networkResponse(stripBody(networkResponse))
                .build();
        // 15、Response有Body，缓存，并且读取到userResponse
        if (hasBody(userResponse)) {
            maybeCache();
            userResponse = unzip(cacheWritingResponse(storeRequest, userResponse));
        }
    }

    // 网络拦截器链
    class NetworkInterceptorChain implements Interceptor.Chain {
        private final Request request;
        NetworkInterceptorChain(int index, Request request) {
            this.index = index;
            this.request = request;
        }
        @Override public Response proceed(Request request) throws IOException {
            // 1、层层递归调用拦截器的intercept
            if (index < client.networkInterceptors().size()) {
                HttpEngine.NetworkInterceptorChain chain = new HttpEngine.NetworkInterceptorChain(index + 1, request);
                Interceptor interceptor = client.networkInterceptors().get(index);
                Response interceptedResponse = interceptor.intercept(chain);
                return interceptedResponse;
            }
            /**===============================
             * 2、最后一个拦截器才会执行到这部分
             *   1. 会向请求中写入Header
             *   2. 并将Stream中写入Request Body
             *===================================*/
            // 1. 向请求中写入Header
            httpStream.writeRequestHeaders(request);
            // 2. 获取到可以写入request Body的outputstream，并通过Okio进行转换
            Sink requestBodyOut = httpStream.createRequestBody(request, request.body().contentLength());
            BufferedSink bufferedRequestBody = Okio.buffer(requestBodyOut);
            // 3. 将request body写入到Stream中
            request.body().writeTo(bufferedRequestBody);
            // 4. 关闭Stream
            bufferedRequestBody.close();

            /**===============================
             * 3、获取到Response，并且返回
             *   1. 获取到Response
             *   2. 处理返回码为204/205的情况
             *===================================*/
            // 1. 获取到Response
            Response response = readNetworkResponse();
            // 2. 处理返回码为204/205的情况
            int code = response.code();
            if ((code == 204 || code == 205) && response.body().contentLength() > 0) {
                throw new ProtocolException("HTTP " + code + " had non-zero Content-Length: " + response.body().contentLength());
            }
            // 3. 返回response
            return response;
        }
    }

    // HttpEngine.java
    private Response readNetworkResponse() throws IOException {
        // 1、真正完成请求：将请求刷新到底层的Scoket中
        httpStream.finishRequest();
        // 2、通过HttpStream(Http1xStream)获取到Reponse的Headers
        Response networkResponse = httpStream.readResponseHeaders()
                .request(networkRequest)
                .handshake(streamAllocation.connection().handshake())
                .header(OkHeaders.SENT_MILLIS, Long.toString(sentRequestMillis))
                .header(OkHeaders.RECEIVED_MILLIS, Long.toString(System.currentTimeMillis()))
                .build();
        // 3、openResponseBody：返回能读取Response Body的stream
        if (!forWebSocket) {
            networkResponse = networkResponse.newBuilder()
                    .body(httpStream.openResponseBody(networkResponse)) // openResponseBody：
                    .build();
        }
        if ("close".equalsIgnoreCase(networkResponse.request().header("Connection"))
                || "close".equalsIgnoreCase(networkResponse.header("Connection"))) {
            streamAllocation.noNewStreams();
        }
        // 4、返回
        return networkResponse;
    }

    // HttpEngine.java-回调CookieJar的saveFromResponse方法
    public void receiveHeaders(Headers headers) throws IOException {
        List<Cookie> cookies = Cookie.parseAll(userRequest.url(), headers);
        if (cookies.isEmpty()) return;
        client.cookieJar().saveFromResponse(userRequest.url(), cookies);
    }

    // HttpEngine.java-如果缓存可用，return true；缓存不可用，需要采用网络，return false；
    private static boolean validate(Response cached, Response network) {
        // 1、304：表示没有更改过，缓存的数据可以继续使用
        if (network.code() == HTTP_NOT_MODIFIED) {
            return true;
        }

        /**=======================================
         * 2、比较缓存和网络的Last-Modified的时间
         *  1. 网络获取的最后修改时间 < 缓存的最后修改时间，return true ： 继续用缓存
         *  2. 否则，return false： 会使用网络Response
         * Last-Modified：用于标记资源在服务端最后被修改的时间
         *=====================================*/
        Date lastModified = cached.headers().getDate("Last-Modified");
        if (lastModified != null) {
            Date networkLastModified = network.headers().getDate("Last-Modified");
            if (networkLastModified != null
                    && networkLastModified.getTime() < lastModified.getTime()) {
                return true;
            }
        }
        return false;
    }
```

15、HttpEngine如何进行失败重连？
> 1. 通过HttpEngine.recover()方法
```java
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
```

16、返回码504
> 1. 获得具有该返回码的Response表示：网关超时
> 1. HttpEngine的sendRequest()中，如果既没有网络，有没有缓存，就会返回具有504的Response。

17、返回码204/205
> 1. 204: 响应报文中包含若干首部和一个状态行，但是没有实体的主体内容。使用场景：对于一些提交到服务器处理的数据,只需要返回是否成功,此时不需要返回数据。可以使用204。
> 1. 205: 告知浏览器清除当前页面中的所有html表单元素，也就是表单重置。
> 1. NetworkInterceptorChain.proceed()中获取到最终的Response时，会处理返回码为204/205的情况。
> 1. 当code=204/205时，Body的Content长度 > 0, 会抛出ProtocolException。

18、返回码304
> 1. 表示资源没有更改过。
> 1. HttpEngine的invalidate()方法用于判断是采用缓存还是网络的Response
> 1. 如果netWork.code = 304，则直接使用缓存数据。
> 1. 如果netWork.code != 304, 会继续去判断缓存和网络的`Last-Modified`。
> 1. 缓存的最后修改时间更大，就采用缓存。
> 1. 网络的最后修改时间更大，就采用网络数据。

##### CacheStrategy

19、CacheStrategy是什么？
> 1. 缓存策略
> 1. 返回和request对应的Cached Response。
> 1. 决定了是否使用网络、缓存，还是两者都使用。
> 1.

20、OkHttp如何缓存的Reponse？
> 1. OkHttpClient中保存了缓存：Cache cache
> 1. 缓存实现于`Cache.java`
> 1. 采用DiskLruCache进行缓存。
```java
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

### 同步请求

21、同步请求源码分析
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

### 复用连接池

22、OkHttp的复用连接池
> 1. TCP的三次握手和四次挥手，会导致效率低下。
> 1. HTTP有一种keepalive connection机制
> 1. OkHttp支持5个并发socket连接
> 1. OkHttp默认keppAlive时间为5分钟

#### ConnectionPool

23、OkHttp的ConnectionPool
> 具有五种主要变量：
> 1. 空闲的最大连接数：默认5
> 1. keepAlive时间：默认5分钟
> 1. 线程池：后台用于清理需要清理的线程
> 1. 双向队列：维护者RealConnections(socket物理连接的包装)
> 1. 连接失败的路线名单：连接失败时，会将失败的路线添加进去
> 1. cleanupRunning：表明是否正在进行清理工作
> 1. cleanupRunnable：清理任务，每隔一定时间间隔就进行下次清理工作。
```java
/**=============================================
 * //ConnectionPool.java-管理HTTP和SPDY连接的复用，用于减少网络延迟。
 * 1. 共享相同Address的Http请求可能会共享同一个Connection。
 * 2. 实现了复用策略：决定哪个连接能为复用而保持open
 *===============================================*/
public final class ConnectionPool {
    // 1、每个address的空闲最大连接数(socket)
    private final int maxIdleConnections;
    // 2、keepAlive时间
    private final long keepAliveDurationNs;
    // 构造方法-最大连接数：5；keepAlive：5分钟
    public ConnectionPool() {
        this(5, 5, TimeUnit.MINUTES);
    }
    public ConnectionPool(int maxIdleConnections, long keepAliveDuration, TimeUnit timeUnit) {
        this.maxIdleConnections = maxIdleConnections;
        this.keepAliveDurationNs = timeUnit.toNanos(keepAliveDuration);
    }

    /**=================================================
     * 3、线程池: 后台线程用于清理需要清理的连接
     *   类似于CachedThreadPool，并且阻塞队列采用没有容量的SynchronousQueue
     *=================================================*/
    private static final Executor executor = new ThreadPoolExecutor(0 /* corePoolSize */,
            Integer.MAX_VALUE /* maximumPoolSize */, 60L /* keepAliveTime */, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(), Util.threadFactory("OkHttp ConnectionPool", true));

    // 4、双向队列，同时具有队列和栈的性质，经常在缓存中使用。内部存储着RealConnection，也就是对socket物理连接的包装。
    private final Deque<RealConnection> connections = new ArrayDeque<>();
    // 5、记录连接失败的路线名单。连接失败时就会把失败的路线加进去。
    final RouteDatabase routeDatabase = new RouteDatabase();

    // 6、表明是否正在进行清理工作
    boolean cleanupRunning;
    // 7、清理任务：循环调用cleanup进行清理工作，并且wait一定时间间隔，然后继续进行清理工作
    private final Runnable cleanupRunnable = new Runnable() {
        @Override public void run() {
            //清理工作
        }
    };
}
```

24、RealConnection的作用？
> 是对socket物理连接的包装

25、ConnectionPool是什么时候创建的？
> 在OkHttpClient构造时，创建的ConnectionPool

26、Deque是什么？
> 1. Deque是Queue的子接口
> 1. 既具有stack栈的性质，也具有queue队列的性质。

#### 缓存操作

27、ConnectionPool关于缓存的操作有哪些？
> 1. 也就是对`Deque<RealConnection>`双向队列的操作。
> 1. 提供了四种操作：放入连接-put；获取连接-get；移除连接-connectionBecameIdle；移除所有连接-evictAll
>
> put: 存放缓存
```java
// ConnectionPool.java
void put(RealConnection connection) {
      // 1、第一次进入时，cleanupRunning = false，会通过executor执行cleanupRunnable进行清理工作。
      if (!cleanupRunning) {
            cleanupRunning = true;
            // 2、 执行完ConnectionPool的cleanup()，并且会继续将cleanupRunning设置为false
            executor.execute(cleanupRunnable);
      }
      // 3、将RealConnection添加到双向队列中
      connections.add(connection);
}
```
> get：获取缓存
```java
    // ConnectionPool.java-返回复用的连接到address的connection，如果不存在连接返回null
    RealConnection get(Address address, StreamAllocation streamAllocation) {
        // 1、遍历双向队列中的RealConnection
        for (RealConnection connection : connections) {
            // 2、连接的allocations的次数小于限制的大小，并且request的address和该连接的地址完全匹配
            if (connection.allocations.size() < connection.allocationLimit
                    && address.equals(connection.route().address)
                    && !connection.noNewStreams) {
                streamAllocation.acquire(connection);
                // 3、直接返回connection，用于复用
                return connection;
            }
        }
        return null;
    }
```
> connectionBecameIdle: 移除连接
```java
    // ConnectionPool.java-移除连接
    boolean connectionBecameIdle(RealConnection connection) {
        // 1、connection进入空闲状态或者最大的空闲连接数=0
        if (connection.noNewStreams || maxIdleConnections == 0) {
            // 2、立即从连接队列中，将该连接移除
            connections.remove(connection);
            return true;
        } else {
            // 3、否则，去通知cleanup线程进行可能的清理工作
            notifyAll();  // 去唤醒cleanupRunnable中对ConnectionPool对象的wait，继续去进行清理任务
            return false;
        }
    }
```
> evictAll: 移除所有连接
```java
    // ConnectionPool.java-移除所有连接
    public void evictAll() {
        List<RealConnection> evictedConnections = new ArrayList<>();
        synchronized (this) {
            // 1、将connection从连接队列中移除，并且加入到待移除的连接队列中
            for (Iterator<RealConnection> i = connections.iterator(); i.hasNext(); ) {
                RealConnection connection = i.next();
                if (connection.allocations.isEmpty()) {
                    connection.noNewStreams = true;
                    // 添加到待移除连接队列中
                    evictedConnections.add(connection);
                    // 从连接队列中移除
                    i.remove();
                }
            }
        }
        // 2、遍历待移除的连接队列，将connection中的socket进行关闭
        for (RealConnection connection : evictedConnections) {
            closeQuietly(connection.socket());
        }
    }
```

#### 自动回收连接

28、ConnectionPool的自动回收连接
> 1. OkHttp是根据StreamAllocation的引用计数是否为0来实现自动回收连接.
> 1. ConnectionPool具有一个cleanup线程
> 1. ConnectionPool.put()方法缓存connection时，会开启cleanup线程进行清理工作。
```java
    // ConnectionPool.java-清理任务
    private final Runnable cleanupRunnable = new Runnable() {
        @Override public void run() {
            while (true) {
                // 1、cleanup进行清理工作
                long waitNanos = cleanup(System.nanoTime());
                // 2、因为没有使用中、idle中的connection，直接退出cleanup线程
                if (waitNanos == -1) return;
                if (waitNanos > 0) {
                    long waitMillis = waitNanos / 1000000L;
                    waitNanos -= (waitMillis * 1000000L);
                    // 3、多线程同步，等待一定时间
                    synchronized (okhttp3.ConnectionPool.this) {
                        try {
                            okhttp3.ConnectionPool.this.wait(waitMillis, (int) waitNanos);
                        } catch (InterruptedException ignored) {
                        }
                    }
                }
            }
        }
    };

    /**===============================================================
     * // ConnectionPool.java---清理工作
     * 1. 在连接池中进行维护工作，将idle时间最长并且已经超过了keep alive限制，或者idle连接上限的connection进行清除
     * 2. 返回时间：直至下一次执行cleanup需要sleep的时间
     * 3. 返回-1：已经没有进一步的清理工作。
     *==================================================================*/
    long cleanup(long now) {
        // 使用中的connection数据
        int inUseConnectionCount = 0;
        // 空闲的connection数据
        int idleConnectionCount = 0;
        // 空闲时间最长的connection
        RealConnection longestIdleConnection = null;
        // 空闲时间最长的connection的空闲时间
        long longestIdleDurationNs = Long.MIN_VALUE;
        synchronized (this) {
            /**========================================================
             * 1、遍历连接队列connections，获取到idle时间最长的connection
             *==========================================================*/
            for (Iterator<RealConnection> i = connections.iterator(); i.hasNext(); ) {
                RealConnection connection = i.next();
                // 1. 如果该connection正在使用中，继续搜索
                if (pruneAndGetAllocationCount(connection, now) > 0) {
                    // 2. 使用中的连接数 + 1
                    inUseConnectionCount++;
                    continue;
                }
                // 3. 该connection处于idle状态，idle的连接数 + 1
                idleConnectionCount++;

                /**======================================
                 * 2、通过算法获取到idle时间最长的connection
                 *=====================================*/
                // 1. 计算得到该connection空闲了多少时间
                long idleDurationNs = now - connection.idleAtNanos;
                if (idleDurationNs > longestIdleDurationNs) {
                    // 2. 保存空闲最长的时间
                    longestIdleDurationNs = idleDurationNs;
                    // 3. 保存空闲最长的connection
                    longestIdleConnection = connection;
                }
            }

            /**=============================================
             * 3、idle时间超过了keepAlive时间，或者，idle的connection数量想超过最大idle连接数。立即清理。
             *=============================================*/
            if (longestIdleDurationNs >= this.keepAliveDurationNs
                    || idleConnectionCount > this.maxIdleConnections) {
                // 1. 从连接队列中移除该connection
                connections.remove(longestIdleConnection);
                // 2. 立即关闭该connection的socket连接
                closeQuietly(longestIdleConnection.socket());
                // 3. 返回0，表示立即再次进行cleanup
                return 0;
            } else if (idleConnectionCount > 0) {
                /**=============================================
                 * 4、具有idle的connection，计算距离keepAlive还有多少时间，return后进行sleep
                 *=============================================*/
                return keepAliveDurationNs - longestIdleDurationNs;
            } else if (inUseConnectionCount > 0) {
                /**=============================================
                 * 5、所有的connection都处于使用中。return keepAlive。默认是睡眠5分钟。
                 *=============================================*/
                return keepAliveDurationNs;
            } else {
                /**=============================================
                 * 6、没有空闲的connection，也没有使用中的connection。-1会退出cleanup线程。等待之后通过线程池开启。
                 *=============================================*/
                cleanupRunning = false;
                return -1;
            }
        }
    }
```

29、清理线程的工作流程？以及四种情况的处理办法？

30、ConnectionPool.pruneAndGetAllocationCount()的源码分析
> 1. 用于判断connection是空闲连接还是使用中的连接。
> 1. return 0: idle connection
> 1. return >0: connection处于使用中
```java
    /**=======================================================
     * //ConnectionPool.java
     * 1. 去除任何泄露的allocations(分配)，并且返回在connection上存活的allocations数量
     * 2. Allocations会被泄露：如果connection正在追踪它们，但是app代码已经遗弃了它们。
     * 3. 泄露检测是不准确的并且依赖于GC
     *=========================================================*/
    private int pruneAndGetAllocationCount(RealConnection connection, long now) {
        // 1、获取到RealConnection中的StreamAllocation列表
        List<Reference<StreamAllocation>> references = connection.allocations;
        for (int i = 0; i < references.size(); ) {
            // 2、获取到StreamAllocation
            Reference<StreamAllocation> reference = references.get(i);
            // 3、如果已经被应用或者GC清理，会返回null
            if (reference.get() != null) {
                i++;
                continue;
            }
            /**=============================================
             * 4、找到泄露的allocation，并从StreamAllocation列表中移除该allocation
             *  1. 移除该allocation
             *  2. 设置noNewStreams标志
             *=========================================*/
            Internal.logger.warning("A connection to " + connection.route().address().url() + " was leaked. Did you forget to close a response body?");
            // 1. 从StreamAllocation列表中移除该allocation
            references.remove(i);
            // 2. connection没有新的stream。该标志可以用于connectionBecameIdle、evictAll中移除连接。
            connection.noNewStreams = true;

            // 5、当前是最后一个allocation，该connection符合立即释放的条件。
            if (references.isEmpty()) {
                // 设置idle的时间
                connection.idleAtNanos = now - keepAliveDurationNs;
                // 表明该connection是空闲连接
                return 0;
            }
        }
        // 6、返回Allocation的数量
        return references.size();
    }
```

#### StreamAllocation
31、StreamAllocation是什么?
> 1. OkHttp中使用了类似于引用计数的方式追踪socket流的调用。
> 1. 该计数对象就是StreamAllocation
> 1. 具有两个重要方法：acquire()、release()---本质是改变RealConnection中StreamAllocation的List的大小。

32、StreamAllocation的acquire()和release()源码
```java
    /**======================================================
     * // StreamAllocation.java
     * 1. 使用该allocation去持有connection。
     * 2. 每次调用acquire()方法，都必须要配套的调用release()方法在同一个connection上。
     *===============================================================*/
    public void acquire(RealConnection connection) {
        // 将该StreamAllocation添加到RealConnection内部的allocation列表中
        connection.allocations.add(new WeakReference<>(this));
    }

    // StreamAllocation.java---从RealConnection的allocation列表中删除当前allocation
    private void release(RealConnection connection) {
        // 1、遍历allocations
        for (int i = 0, size = connection.allocations.size(); i < size; i++) {
            Reference<StreamAllocation> reference = connection.allocations.get(i);
            // 2、查找到当前StreamAllocation
            if (reference.get() == this) {
                // 3、从allocations列表中删除这个allocation
                connection.allocations.remove(i);
                return;
            }
        }
        throw new IllegalStateException();
    }
```

#### RealConnection
33、RealConnection是什么？有什么用？
> 1. 是socket物理连接的包装
> 1. 维护了`List<Reference<StreamAllocation>> allocations`
> 1. StreamAllocation的数量也就是socket被引用的次数
> 1. 如果计数  = 0，表明该连接处于idle状态，需要经过算法进行回收。
> 1. 如果计数 != 0, 表明该连接处于使用中，无需关闭。

## 总结题

1、如何使用OkHttp进行异步网络请求，并根据请求结果刷新UI
> 1. 通过构造器创建RequestBody
> 2. 创建Request
> 3. 创建OkHttpClient
> 4. 创建Call
> 5. 发起Call的enqueue异步网络请求
> 6. 在response的回调中根据数据改变UI(OKHTTP3.0中不需要切换线程)

2、可否介绍一下OkHttp的整个异步请求流程

3、OkHttp发起异步请求时，调用的call.enqueue方法做了哪些事情？
> 1. 在Call创建的时候，在内部创建了RealCall，并且将OkHttpClient和request保存到了内部。
> 2. 执行Call.enqueue()，内部是执行的RealCall.enqueue()
> 3. RealCall.enqueue()内部直接转交给Dispatcher执行enqueue()方法
> 4. Dsipatcher内部做了会去判断是否达到了最大并发任务数64，以及同一个主机的请求数是否达到了5。
> 5. 都没有达到：加入到正在执行的异步请求队列。然后调用线程池去执行这个任务。
> 6. 达到：加入到待执行的异步请求队列

4、异步请求队列中的元素AsyncCall是什么？

5、OkHttp对于网络请求都有哪些优化，如何实现的

6、OkHttp框架中都用到了哪些设计模式

7、OkHttp的缓存策略是什么？

8、OkHttp底层是如何实现缓存的？
> 1. HttpClient的sendRequest完成了实际的请求工作。
> 2. 采用DiskLruCache进行缓存
> 3. key = 请求url中的md5 value = Snapshot(存储了所有响应的信息---包括url、响应头、请求的方法、protocol等)
> 4. 在`HttpClient.readResponse()`会对数据进行缓存。

9、OkHttp中涉及到的Http返回码
> 1. 504: 网关超时
> 1. 204/205: 在获取到返回Response后，如果code=204/205, 但是Body的Content长度>0，会抛出异常：ProtocolException
> 1. 304：数据没有更改过。比如请求图片，如果图片在上次访问后没有更新过，就不用重新下载，直接返回304，告诉客户端可以直接使用缓存。

## 参考资料
1. [OkHttp拦截器-官方github-wiki](https://github.com/square/okhttp/wiki/Interceptors)
1. [OKHTTP结合官网示例分析两种自定义拦截器的区别](https://www.jianshu.com/p/d04b463806c8)
1. [http中的204和205](https://blog.csdn.net/mevicky/article/details/50483178)
