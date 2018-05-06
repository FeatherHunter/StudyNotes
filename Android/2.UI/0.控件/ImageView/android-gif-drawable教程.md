本文介绍如何将`android-gif-drawable`集成到项目中，并且如何使用`android-gif-drawable`加载离线和网络Gif动图。

转载请注明链接：http://blog.csdn.net/feather_wch/article/details/79558240

#android-gif-drawable教程
版本：2018/3/14-1(17:56)

[TOC]

---

* [Github:android-gif-drawable](https://github.com/koral--/android-gif-drawable)

##android-gif-drawable的集成
###在线集成
Github上相关教程，也比较简单，将依赖添加到项目的`build.gradle`文件即可：
```xml
dependencies {
    compile 'pl.droidsonroids.gif:android-gif-drawable:1.2.11'
}
```
###离线集成
Android Studio 3.0中有效

1. 进入Github上的realease页面-[realease点我](https://github.com/koral--/android-gif-drawable/releases)

2. 下载其中的`android-gif-drawable-1.2.11.aar`

3. 将`android-gif-drawable-1.2.11.aar`添加到项目的`libs目录中`

4. 在项目的`build.gradle`中添加该`arr文件`
```xml
compile(name:'android-gif-drawable-1.2.11', ext:'aar')
```

5. 集成完毕，可以进行测试。

##android-gif-drawable的使用
`android-gif-drawable`有四种控件：`GifImageView`、`GifImageButton`、`GifTextView`、`GifTextureView`。这里以`ImageView`为例进行介绍。
###加载本地图片
1. 直接在布局中选定资源文件
```xml
<pl.droidsonroids.gif.GifImageView
    android:id="@+id/fragment_gif_local"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:src="@drawable/dog"/>
```

2. 通过代码进行动态添加gif动图
```java
//1. 构建GifDrawable
GifDrawable gifFromResDrawable = new GifDrawable( getResources(), R.drawable.dog );
//2. 设置给GifImageView控件
gifImageView.setImageDrawable(gifFromResDrawable);
```
####GifDrawable
`GifDrawable`是用于该开源库的Drawable类。构造方法大致有9种：
```java
//1. asset file
GifDrawable gifFromAssets = new GifDrawable( getAssets(), "anim.gif" );

//2. resource (drawable or raw)
GifDrawable gifFromResource = new GifDrawable( getResources(), R.drawable.anim );

//3. byte array
byte[] rawGifBytes = ...
GifDrawable gifFromBytes = new GifDrawable( rawGifBytes );

//4. FileDescriptor
FileDescriptor fd = new RandomAccessFile( "/path/anim.gif", "r" ).getFD();
GifDrawable gifFromFd = new GifDrawable( fd );

//5. file path
GifDrawable gifFromPath = new GifDrawable( "/path/anim.gif" );

//6. file
File gifFile = new File(getFilesDir(),"anim.gif");
GifDrawable gifFromFile = new GifDrawable(gifFile);

//7. AssetFileDescriptor
AssetFileDescriptor afd = getAssets().openFd( "anim.gif" );
GifDrawable gifFromAfd = new GifDrawable( afd );

//8. InputStream (it must support marking)
InputStream sourceIs = ...
BufferedInputStream bis = new BufferedInputStream( sourceIs, GIF_LENGTH );
GifDrawable gifFromStream = new GifDrawable( bis );

//9. direct ByteBuffer
ByteBuffer rawGifBytes = ...
GifDrawable gifFromBytes = new GifDrawable( rawGifBytes );
```

###加载网络Gif
我们解决的办法是将`Gif图片`下载到`缓存目录`中，然后从`磁盘缓存`中获取该`Gif动图`进行显示。

1、下载工具DownloadUtils.java
```java
public class DownloadUtils {
    private final int DOWN_START = 1; // Handler消息类型(开始下载)
    private final int DOWN_POSITION = 2; // Handler消息类型(下载位置)
    private final int DOWN_COMPLETE = 3; // Handler消息类型(下载完成)
    private final int DOWN_ERROR = 4; // Handler消息类型(下载失败)
    private OnDownloadListener onDownloadListener;

    public void setOnDownloadListener(OnDownloadListener onDownloadListener) {
        this.onDownloadListener = onDownloadListener;
    }

    /**
     * 下载文件
     *
     * @param url      文件路径
     * @param filepath 保存地址
     */
    public void download(String url, String filepath) {
        MyRunnable mr = new MyRunnable();
        mr.url = url;
        mr.filepath = filepath;
        new Thread(mr).start();
    }

    @SuppressWarnings("unused")
    private void sendMsg(int what) {
        sendMsg(what, null);
    }

    private void sendMsg(int what, Object mess) {
        Message m = myHandler.obtainMessage();
        m.what = what;
        m.obj = mess;
        m.sendToTarget();
    }

    Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_START: // 开始下载
                    int filesize = (Integer) msg.obj;
                    onDownloadListener.onDownloadConnect(filesize);
                    break;
                case DOWN_POSITION: // 下载位置
                    int pos = (Integer) msg.obj;
                    onDownloadListener.onDownloadUpdate(pos);
                    break;
                case DOWN_COMPLETE: // 下载完成
                    String url = (String) msg.obj;
                    onDownloadListener.onDownloadComplete(url);
                    break;
                case DOWN_ERROR: // 下载失败
                    Exception e = (Exception) msg.obj;
                    e.printStackTrace();
                    onDownloadListener.onDownloadError(e);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    class MyRunnable implements Runnable {
        private String url = "";
        private String filepath = "";

        @Override
        public void run() {
            try {
                doDownloadTheFile(url, filepath);
            } catch (Exception e) {
                sendMsg(DOWN_ERROR, e);
            }
        }
    }

    /**
     * 下载文件
     *
     * @param url      下载路劲
     * @param filepath 保存路径
     * @throws Exception
     */
    private void doDownloadTheFile(String url, String filepath) throws Exception {
        if (!URLUtil.isNetworkUrl(url)) {
            sendMsg(DOWN_ERROR, new Exception("不是有效的下载地址：" + url));
            return;
        }
        URL myUrl = new URL(url);
        URLConnection conn = myUrl.openConnection();
        conn.connect();
        InputStream is = null;
        int filesize = 0;
        try {
            is = conn.getInputStream();
            filesize = conn.getContentLength();// 根据响应获取文件大小
            sendMsg(DOWN_START, filesize);
        } catch (Exception e) {
            sendMsg(DOWN_ERROR, new Exception(new Exception("无法获取文件")));
            return;
        }
        FileOutputStream fos = new FileOutputStream(filepath); // 创建写入文件内存流，
        // 通过此流向目标写文件
        byte buf[] = new byte[1024];
        int numread = 0;
        int temp = 0;
        while ((numread = is.read(buf)) != -1) {
            fos.write(buf, 0, numread);
            fos.flush();
            temp += numread;
            sendMsg(DOWN_POSITION, temp);
        }
        is.close();
        fos.close();
        sendMsg(DOWN_COMPLETE, filepath);
    }

    interface OnDownloadListener{
        public void onDownloadUpdate(int percent);

        public void onDownloadError(Exception e);

        public void onDownloadConnect(int filesize);

        public void onDownloadComplete(Object result);
    }
}
```

2、调用DonwloadUtils进行下载，下载完成后加载本地图片
```java
//1. 下载gif图片(文件名自定义可以通过Hash值作为key)
DownloadUtils downloadUtils = new DownloadUtils();
downloadUtils.download(gifUrlArray[0],
                getDiskCacheDir(getContext())+"/0.gif");
//2. 下载完毕后通过“GifDrawable”进行显示
downloadUtils.setOnDownloadListener(new DownloadUtils.OnDownloadListener() {
            @Override
            public void onDownloadUpdate(int percent) {
            }
            @Override
            public void onDownloadError(Exception e) {
            }
            @Override
            public void onDownloadConnect(int filesize) {
            }
            //下载完毕后进行显示
            @Override
            public void onDownloadComplete(Object result) {
                try {
                    GifDrawable gifDrawable = new GifDrawable(getDiskCacheDir(getContext())+"/0.gif");
                    mGifOnlineImageView.setImageDrawable(gifDrawable);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
```
```java
//获取缓存的路径
public String getDiskCacheDir(Context context) {
    String cachePath = null;
    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
            || !Environment.isExternalStorageRemovable()) {
        // 路径：/storage/emulated/0/Android/data/<application package>/cache
        cachePath = context.getExternalCacheDir().getPath();
    } else {
        // 路径：/data/data/<application package>/cache
        cachePath = context.getCacheDir().getPath();
    }
    return cachePath;
}
```


##参考文献
1. [android-gif-drawable移植](http://blog.csdn.net/lvshaorong/article/details/51721537)
2. [android-gif-drawable的使用](https://www.jianshu.com/p/46dd1ee82141)
3. [网络加载gif图片](https://www.cnblogs.com/3A87/p/5076090.html)
