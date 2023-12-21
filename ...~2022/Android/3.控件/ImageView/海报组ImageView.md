转载请注明：http://blog.csdn.net/feather_wch/article/details/79585803

ImageView控件实现效果：设置一组图片或者url能在指定时间间隔内，不断循环切换，达到一组海报自动切换的功能。

具体内容只是初稿，还未完成，勿看！

```java
/**
 * @Tips:
 *      When you don't need this ImageView, must call clean() method.
 *      If you dont't do this, your programe will have many unuseful threads.
 */
public class CarouselImageView extends ImageView{

    private static final String TAG = CarouselImageView.class.getName();

    private static final int WIDTH_HEIGHT_DEFAULT = 500;

    private ArrayList<String> mUrlArray; //存储Url数组用于
    private ImageLoader mImageLoader;
    private int mTimeMs = 0;//毫秒
    private boolean isAlive = true;
    private int mWidth = WIDTH_HEIGHT_DEFAULT;
    private int mHeight = WIDTH_HEIGHT_DEFAULT;

    //cpu核心数
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    //cpu核心线程数
    private static final int CORE_POOL_SIZE = CPU_COUNT +1;
    //cpu最大线程数
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    //线程超时时长
    private static final long KEEP_ALIVE = 10L;
    //线程工厂
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r,"imageLoader#"+mCount.getAndIncrement());
        }
    };
    //线程池
    public static final Executor THREAD_POOL_EXECUTOR
            = new ThreadPoolExecutor(   CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE,
            KEEP_ALIVE,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<Runnable>(),
            sThreadFactory);

    //4-主线程Handler
    private Handler mMainHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            String url = (String) msg.obj;
            mImageLoader.bindBitmap(url, CarouselImageView.this, mWidth, mHeight);
        }
    };
    private static final int MESSAGE_SET_IMAGE = 1;

    public CarouselImageView(Context context) {
        super(context);
        mImageLoader = new ImageLoader(context);
    }
    public CarouselImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mImageLoader = new ImageLoader(context);
    }
    public CarouselImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mImageLoader = new ImageLoader(context);
    }
    public CarouselImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mImageLoader = new ImageLoader(context);
    }
    public CarouselImageView setWidthHeight(int width, int height){
        mWidth = width;
        mHeight = height;
        return this;
    }
    public CarouselImageView setTimeMs(int times){
        mTimeMs = times;
        return this;
    }
    public CarouselImageView setImageUrls(ArrayList<String> urlArray){
        mUrlArray = urlArray;
        return this;
    }
    public void commit(){
        Runnable carousel = new Runnable() {
            @Override
            public void run() {
                if(mTimeMs > 0){
                    while(isAlive){
                        for (String url : mUrlArray) {
                            if(!isAlive){
                                return;
                            }
                            mMainHandler.obtainMessage(MESSAGE_SET_IMAGE, url).sendToTarget();
//                            Log.i(TAG, "thread");
                            try {
                                Thread.sleep(mTimeMs);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        };
        THREAD_POOL_EXECUTOR.execute(carousel);
    }

    public void clean(){
        isAlive = false;
//        Log.i(TAG, "clean");
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        isAlive = false;
//        Log.i(TAG, "finalize");
    }
}

```
