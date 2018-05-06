&emsp;&emsp;目前,网上提供基于高德marker聚合的思路大致差不多,处于雏形阶段。高德官方也提供了关于聚合的解决方案,对于缓存和加载效率都做了一些处理,为我们后面的定制奠定了基础,本文就在高德官方提供的方案基础上做一些定制化。笔者经过思考后,还是觉得将篇幅分为上下两部分,前篇主要涉及聚合的基本使用以及针对定制过程中出现的坑进行填补(如多种聚合标签的冲突,聚合标签与普通标签的冲突问题等),下篇则讲述如何定制化marker并加载网络图片(为了方便描述,marker聚合在下文都称之为聚合标签)。看完两篇文章,大家还可以尝试将聚合和网络图片样式的marker结合起来,达到更加炫酷的效果。
话不多说,先来看看最终的效果吧:

![效果图](http://ovl7kcyr4.bkt.clouddn.com/18-5-6/19951549.jpg)


&emsp;&emsp;先来放一下官网提供的聚合点的demo:https://github.com/amap-demo/android-cluster-marker
,如果只是实现一种聚合点,可以复制官方的demo代码,稍微改改就可以用了。如果你们产品有其他需求,比如多种聚合标签点,或者实现普通标签和聚合标签的混排使用,那么可以参考下本文提供的改进思路。

- 发现问题:

首先,我们发现高德官方提供的demo存在的问题:
1. 当存在多种类型聚合标签点时,聚合点的点击事件冲突问题.
>这个问题是什么意思呢?举个例子:当地图上我们先后初始化两种聚合标签点A和B的时候,不管点击A还是B类型的聚合点,都会只响应B类的点击事件,即先声明的聚合标签的点击事件会被后声明的聚合标签的点击事件"抢占".
2. 当地图上存在普通标签(Marker)和聚合标签(Cluster)时,同样会产生点击事件的冲突问题.
>这个意思就是,点击聚合标签仍然会响应普通标签的点击事件.

- 分析问题:
>这两个问题其实归根结底是一个问题,都是因为marker的点击事件被抢占,从官方的提供的ClusterOverlay类中代码我们就可以看出来:
```
......
amap.setOnCameraChangeListener(this);
amap.setOnMarkerClickListener(this);
......
 //点击事件
    @Override
    public boolean onMarkerClick(Marker arg0) {
        if (mClusterClickListener == null) {
            return true;
        }
       Cluster cluster= (Cluster) arg0.getObject();
        if(cluster!=null){
            mClusterClickListener.onClick(arg0,cluster.getClusterItems());
            return true;
        }
        return false;
    }

```
&emsp;&emsp;从以上代码我们可以看出来,聚合处理类ClusterOverlay实现了AMap.OnMarkerClickListener接口,而当我们使用两个或者两个以上样式的聚合标签时,我们就会发现明明是两种类型的聚合标签,但是却触发相同的点击事件。这样显然是不符合我们预期需求的,那么我们该如何让不同样式的聚合标签A,B,C...各司其职呢?你肯定会说,废话,不然你现在在讲啥!咳咳,请允许我静静~ 

- 解决问题:

&emsp;&emsp;显然,如果我们想让各种聚合标签互不干扰,需要集中管理它们的点击事件,即OnMarkerClickListener。假设我们写了两个不同的聚合标签类ClusterOverlayA和ClusterOverlayB,我们可以将两个类中OnMarkerClick()方法中的逻辑拿出来,单独封装成一个方法,然后放在我们Activity的onMarkerClick()中执行.这里我贴一下修改后的ClusterOverlay类,前方高能,一大波代码来袭:
>ClusterOverlay:
```
/**
 * Created by yiyi.qi on 16/10/10.
 * 整体设计采用了两个线程,一个线程用于计算组织聚合数据,一个线程负责处理Marker相关操作
 */
public class ClusterOverlay {
    private AMap mAMap;
    private Context mContext;
    private List<ClusterItem> mClusterItems;
    private List<Cluster> mClusters;
    private int mClusterSize;
    private ClusterClickListener mClusterClickListener;
    private ClusterRender mClusterRender;
    private ClusterRenderB bClusterRender;
    private AMap.OnMarkerClickListener markerClickListener=new AMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker arg0) {
            if (mClusterClickListener == null) {
                return true;
            }
            Cluster cluster = (Cluster) arg0.getObject();
            //将marker的位置移动到地图中心
            LatLng latLng=new LatLng(arg0.getPosition().latitude,arg0.getPosition().longitude);
            mAMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
            if (cluster != null) {
                arg0.showInfoWindow();
                Log.e("timeory", "普通标签infowindow出现了吗?  " +  arg0.isInfoWindowShown()+ "   "+ arg0.isInfoWindowEnable(), null);
                mClusterClickListener.onClick(arg0, cluster.getClusterItems());
                return true;
            }

            return false;
        }
    };




    private List<Marker> mAddMarkers = new ArrayList<Marker>();
    private double mClusterDistance;
    private LruCache<Integer, BitmapDescriptor> mLruCache;
    private HandlerThread mMarkerHandlerThread = new HandlerThread("addMarker");
    private HandlerThread mSignClusterThread = new HandlerThread("calculateCluster");
    private Handler mMarkerhandler;
    private Handler mSignClusterHandler;
    private float mPXInMeters;
    private boolean mIsCanceled = false;
    private MarkerOptions markerOptions;
    private Cluster mcluster;
    private LatLng latlng1;

    /**
     * 构造函数
     *
     * @param amap
     * @param clusterSize 聚合范围的大小（指点像素单位距离内的点会聚合到一个点显示）
     * @param context
     */
    public ClusterOverlay(AMap amap, int clusterSize, Context context) {
        this(amap, null, clusterSize, context);


    }

    /**
     * 构造函数,批量添加聚合元素时,调用此构造函数
     *  默认最多会缓存80张图片作为聚合显示元素图片,根据自己显示需求和app使用内存情况,可以修改数量
     * @param amap
     * @param clusterItems 聚合元素
     * @param clusterSize
     * @param context
     */
    public ClusterOverlay(AMap amap, List<ClusterItem> clusterItems, int clusterSize, Context context) {
        mLruCache = new LruCache<Integer, BitmapDescriptor>(80) {
            protected void entryRemoved(boolean evicted, Integer key, BitmapDescriptor oldValue, BitmapDescriptor newValue) {
                oldValue.getBitmap().recycle();
            }
        };
        if (clusterItems != null) {
            mClusterItems = clusterItems;
        } else {
            mClusterItems = new ArrayList<ClusterItem>();
        }
        mContext = context;
        mClusters = new ArrayList<Cluster>();
        this.mAMap = amap;
        mClusterSize = clusterSize;
        mPXInMeters = mAMap.getScalePerPixel();
        mClusterDistance = mPXInMeters * mClusterSize;
        initThreadHandler();
        assignClusters();
    }

    /**
     * by moos on 2017/11/13
     * function:聚合刷新,在activity的onCameraChangeFinish()方法中
     */
    public void updateClusters(){
        mPXInMeters = mAMap.getScalePerPixel();
        mClusterDistance = mPXInMeters * mClusterSize;
        assignClusters();
    }

    /**
     * by moos on 2017/11/13
     * func:通过activity中的onMarkerClick()响应聚合点的点击事件
     * @param arg0
     */
    public void respondClusterClickEvent(Marker arg0){

        Cluster cluster = (Cluster) arg0.getObject();
        //将marker的位置移动到地图中心
        LatLng latLng=new LatLng(arg0.getPosition().latitude,arg0.getPosition().longitude);
        mAMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
        if (cluster != null) {
            arg0.showInfoWindow();
            Log.e("timeory", "普通标签infowindow出现了吗?  " +  arg0.isInfoWindowShown()+ "   "+ arg0.isInfoWindowEnable(), null);
            mClusterClickListener.onClick(arg0, cluster.getClusterItems());
        }

    }

    /**
     * 设置聚合点的点击事件
     *
     * @param clusterClickListener
     */
    public void setOnClusterClickListener(ClusterClickListener clusterClickListener) {
        mClusterClickListener = clusterClickListener;
    }

    /**
     * 添加一个聚合点
     *
     * @param item
     */
    public void addClusterItem(ClusterItem item) {
        Message message = Message.obtain();
        message.what = SignClusterHandler.CALCULATE_SINGLE_CLUSTER;
        message.obj = item;
        mSignClusterHandler.sendMessage(message);
    }

    /**
     * 共外部调用的接口,设置聚合元素的渲染样式，不设置则默认为气泡加数字形式进行渲染
     *
     * @param render
     */
    public void setClusterRenderer(ClusterRender render) {
        mClusterRender = render;
    }
    /**
     * 设置聚合元素的渲染样式，不设置则默认为气泡加数字形式进行渲染
     *
     * @param render
     */
    public void setBClusterRenderer(ClusterRenderB render) {
        bClusterRender = render;
    }

    public void onDestroy() {
        mIsCanceled = true;
        mSignClusterHandler.removeCallbacksAndMessages(null);
        mMarkerhandler.removeCallbacksAndMessages(null);
        mSignClusterThread.quit();
        mMarkerHandlerThread.quit();
        for (Marker marker : mAddMarkers) {
            marker.remove();

        }
        if(markerClickListener!=null){
            markerClickListener = null;
        }
        mAddMarkers.clear();
        mLruCache.evictAll();
    }

    /**
     * 清除maker点击事件,防止抢占
     */
    public void cleanListener(){
        if(markerClickListener!=null){
            markerClickListener = null;
        }
    }

    //初始化Handler
    private void initThreadHandler() {
        mMarkerHandlerThread.start();
        mSignClusterThread.start();
        mMarkerhandler = new MarkerHandler(mMarkerHandlerThread.getLooper());
        mSignClusterHandler = new SignClusterHandler(mSignClusterThread.getLooper());
    }



    /**
     * 将聚合元素添加至地图上
     */
    private void addClusterToMap(List<Cluster> clusters) {

        ArrayList<Marker> removeMarkers = new ArrayList<>();
        removeMarkers.addAll(mAddMarkers);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        MyAnimationListener myAnimationListener = new MyAnimationListener(removeMarkers);
        for (Marker marker : removeMarkers) {
            marker.setAnimation(alphaAnimation);
            marker.setAnimationListener(myAnimationListener);
            marker.startAnimation();
        }

        for (Cluster cluster : clusters) {
            addSingleClusterToMap(cluster);
        }
    }

    private AlphaAnimation mADDAnimation = new AlphaAnimation(0, 1);

    /**
     * 将单个聚合元素添加至地图显示
     *
     * @param cluster
     */
    private void addSingleClusterToMap(Cluster cluster) {
        latlng1 = cluster.getCenterLatLng();
        markerOptions = new MarkerOptions();
        markerOptions
                .anchor(0.5f, 0.5f)
                .icon(getBitmapDes(cluster.getClusterCount()))
                .position(latlng1);
        Marker marker = mAMap.addMarker(markerOptions);
        marker.setAnimation(mADDAnimation);
        marker.setObject(cluster);
        marker.startAnimation();

//        marker.setTitle("你好啊");
//        marker.setSnippet("真的不好");
        cluster.setMarker(marker);
        mcluster = cluster;
        mAddMarkers.add(marker);

    }

    public void newIcon() {
        markerOptions
                .anchor(0.5f, 0.5f)
                .icon(getBitmapDes(mcluster.getClusterCount()))
                .position(latlng1);
    }


    private void calculateClusters() {
        mIsCanceled = false;
        mClusters.clear();
        //地图获取转换器, 获取可视区域, 获取可视区域的四个点形成的经纬度范围, 得到一个经纬度范围
        LatLngBounds visibleBounds = mAMap.getProjection().getVisibleRegion().latLngBounds;
        for (ClusterItem clusterItem : mClusterItems) {
            if (mIsCanceled) {
                return;
            }
            LatLng latlng = clusterItem.getPosition();//聚合元素的地理位置
            if (visibleBounds.contains(latlng)) {//如果这个范围包含这个聚合元素的地理位置
                Cluster cluster = getCluster(latlng, mClusters);//根据这个位置和聚合物的集合, 获得一个聚合器
                if (cluster != null) {
                    cluster.addClusterItem(clusterItem);//往聚合器中添加聚合点
                } else {
                    cluster = new Cluster(latlng);
                    mClusters.add(cluster);
                    cluster.addClusterItem(clusterItem);
                }

            }
        }

        //复制一份数据，规避同步
        List<Cluster> clusters = new ArrayList<Cluster>();
        clusters.addAll(mClusters);
        Message message = Message.obtain();
        message.what = MarkerHandler.ADD_CLUSTER_LIST;
        message.obj = clusters;
        if (mIsCanceled) {
            return;
        }
        mMarkerhandler.sendMessage(message);
    }

    /**
     * 对点进行聚合
     */
    private void assignClusters() {
        mIsCanceled = true;
        mSignClusterHandler.removeMessages(SignClusterHandler.CALCULATE_CLUSTER);
        mSignClusterHandler.sendEmptyMessage(SignClusterHandler.CALCULATE_CLUSTER);
    }

    /**
     * 在已有的聚合基础上，对添加的单个元素进行聚合
     *
     * @param clusterItem
     */
    private void calculateSingleCluster(ClusterItem clusterItem) {
        LatLngBounds visibleBounds = mAMap.getProjection().getVisibleRegion().latLngBounds;
        LatLng latlng = clusterItem.getPosition();
        if (!visibleBounds.contains(latlng)) {
            return;
        }
        Cluster cluster = getCluster(latlng, mClusters);
        if (cluster != null) {
            cluster.addClusterItem(clusterItem);
            Message message = Message.obtain();
            message.what = MarkerHandler.UPDATE_SINGLE_CLUSTER;

            message.obj = cluster;
            mMarkerhandler.removeMessages(MarkerHandler.UPDATE_SINGLE_CLUSTER);
            mMarkerhandler.sendMessageDelayed(message, 5);


        } else {

            cluster = new Cluster(latlng);
            mClusters.add(cluster);
            cluster.addClusterItem(clusterItem);
            Message message = Message.obtain();
            message.what = MarkerHandler.ADD_SINGLE_CLUSTER;
            message.obj = cluster;
            mMarkerhandler.sendMessage(message);

        }
    }

    /**
     * 根据一个点获取是否可以依附的聚合点，没有则返回null
     *
     * @param latLng
     * @return
     */
    private Cluster getCluster(LatLng latLng, List<Cluster> clusters) {
        for (Cluster cluster : clusters) {
            LatLng clusterCenterPoint = cluster.getCenterLatLng();
            double distance = AMapUtils.calculateLineDistance(latLng, clusterCenterPoint);
            if (distance < mClusterDistance) {
                return cluster;
            }
        }

        return null;
    }


    /**
     * 获取每个聚合点的绘制样式
     */
    private BitmapDescriptor getBitmapDes(int num) {

        //加载字体
        Typeface typeFace = Typeface.createFromAsset(mContext.getAssets(), "fonts/A-OTF-ShinGoPr6N-Ultra.otf");

        BitmapDescriptor bitmapDescriptor = mLruCache.get(num);
        if (bitmapDescriptor == null) {
            TextView textView = new TextView(mContext);
            // 应用字体
            textView.setTypeface(typeFace);
            if (num < 10) {
                String tile = String.valueOf(num);

                textView.setText(tile);
            }else if(num==1){
                textView.setText("");
            }else {
                float zoom = mAMap.getCameraPosition().zoom;
                //loge("当前缩放级别是==" + zoom);
                if (zoom == 19.0 || zoom == 20.0) {
                    String tile = String.valueOf(num);
                    textView.setText(tile);
                }
            }
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(Color.parseColor("#ffffff"));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            if (mClusterRender != null && mClusterRender.getDrawAble(num) != null) {
                textView.setBackgroundDrawable(mClusterRender.getDrawAble(num));   //根据数量设置背景样式
            } else {
                //textView.setBackgroundResource(R.mipmap.act_map_evelopetwo);
                //2017/09/25修改 换图标
                textView.setBackgroundResource(R.mipmap.act_map_blue_icon_bg);
            }
            bitmapDescriptor = BitmapDescriptorFactory.fromView(textView);
            mLruCache.put(num, bitmapDescriptor);    //把聚合点数量,样式键值对形式存入集合中

        }
        return bitmapDescriptor;
    }

    /**
     * 更新已加入地图聚合点的样式
     */
    private void updateCluster(Cluster cluster) {

        Marker marker = cluster.getMarker();
        marker.setIcon(getBitmapDes(cluster.getClusterCount()));


    }




//-----------------------辅助内部类用---------------------------------------------

    /**
     * marker渐变动画，动画结束后将Marker删除
     */
    class MyAnimationListener implements Animation.AnimationListener {
        private List<Marker> mRemoveMarkers;

        MyAnimationListener(List<Marker> removeMarkers) {
            mRemoveMarkers = removeMarkers;
        }

        @Override
        public void onAnimationStart() {

        }

        @Override
        public void onAnimationEnd() {
            for (Marker marker : mRemoveMarkers) {
                marker.remove();
            }
            mRemoveMarkers.clear();
        }
    }

    /**
     * 处理market添加，更新等操作
     */
    class MarkerHandler extends Handler {

        static final int ADD_CLUSTER_LIST = 0;

        static final int ADD_SINGLE_CLUSTER = 1;

        static final int UPDATE_SINGLE_CLUSTER = 2;

        MarkerHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {

            switch (message.what) {
                case ADD_CLUSTER_LIST:
                    List<Cluster> clusters = (List<Cluster>) message.obj;
                    addClusterToMap(clusters);
                    break;
                case ADD_SINGLE_CLUSTER:
                    Cluster cluster = (Cluster) message.obj;
                    addSingleClusterToMap(cluster);
                    break;
                case UPDATE_SINGLE_CLUSTER:
                    Cluster updateCluster = (Cluster) message.obj;
                    updateCluster(updateCluster);
                    break;
            }
        }
    }

    /**
     * 处理聚合点算法线程
     */
    class SignClusterHandler extends Handler {
        static final int CALCULATE_CLUSTER = 0;
        static final int CALCULATE_SINGLE_CLUSTER = 1;

        SignClusterHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            switch (message.what) {
                case CALCULATE_CLUSTER:
                    calculateClusters();
                    break;
                case CALCULATE_SINGLE_CLUSTER:
                    ClusterItem item = (ClusterItem) message.obj;
                    mClusterItems.add(item);
                    Log.e("cluster_overlay", "calculate single cluster");
                    calculateSingleCluster(item);
                    break;
            }
        }
    }
}
```
以上代码大家可以根据自己具体需要修改,接下来是聚合标签类Cluster代码,几乎没改(滑稽):
>Cluster:
```
public class Cluster {


    private LatLng mLatLng;
    private List<ClusterItem> mClusterItems;
    private Marker mMarker;


    public Cluster(LatLng latLng) {

        mLatLng = latLng;
        mClusterItems = new ArrayList<ClusterItem>();
    }

    public Cluster(List<ClusterItem> clusterItems){
        mClusterItems = new ArrayList<>() ;
        mClusterItems = clusterItems;
    }

    public void addClusterItem(ClusterItem clusterItem) {
        mClusterItems.add(clusterItem);
    }

    public int getClusterCount() {
        return mClusterItems.size();
    }


    public LatLng getCenterLatLng() {
        return mLatLng;
    }

    public void setMarker(Marker marker) {
        mMarker = marker;
    }

    public Marker getMarker() {
        return mMarker;
    }

    public List<ClusterItem> getClusterItems() {
        return mClusterItems;
    }
}

```

然后就是ClusterItem接口了,用来获取聚合标签的相关信息:
>ClusterItem:
```

public interface ClusterItem {
    /**
     * 返回聚合元素的地理位置
     *
     * @return
     */
    LatLng getPosition();
    String getUserType();

}
```
有人可能会问:getUserType()方法干啥用的?客官,别急,先接着往下看,很快就知道它的用处了.

接着,就是我们的RegionItem类了,实现了ClusterItem接口:
>RegionItem:

```
public class RegionItem implements ClusterItem {
    private LatLng mLatLng;
    private String userType;



    public String getUserType() {
        return userType;
    }

    public RegionItem(LatLng latLng, String userType) {
        this.mLatLng=latLng;
        this.userType = userType;
    }

    @Override
    public LatLng getPosition() {
        // TODO Auto-generated method stub
        return mLatLng;
    }

}

```

最后,就是要在Activity中根据需求添加我们的聚合标签了:
```
private ClusterOverlay mClusterOverlay;//普通用户聚合覆盖物的对象
private BClusterOverlay bClusterOverlay;//企业用户聚合覆盖物的对象
/**
     * by moos on 2017/11/15
     * func:添加并显示聚合点,增加点击和绘制事件
     */
    
    private void addItems() {
        new Thread() {
            public void run() {

                List<ClusterItem> items = new ArrayList<ClusterItem>();//普通用户
               
                List<ClusterItem> bItems = new ArrayList<ClusterItem>();//企业用户
            
                 //loge("附近的点有===" + imageNearBean.getData().getExhibitionList().size());
                for (int i = 0; i < imageNearBean.getData().getExhibitionList().size(); i++) {

                    double lat = imageNearBean.getData().getExhibitionList().get(i).getLatitude();
                    double lon = imageNearBean.getData().getExhibitionList().get(i).getLongitude();

                    if (imageNearBean.getData().getExhibitionList().get(i).getUserType() != null &&
                            imageNearBean.getData().getExhibitionList().get(i).getUserType().equals("03")) {//是企业用户的数据
                        LatLng latLng = new LatLng(lat, lon, false);
                        RegionItem regionItem = new RegionItem(latLng,  imageNearBean.getData().getExhibitionList().get(i).getUserType());//添加元素
                        bItems.add(regionItem);
                        allBussinessBean.add(imageNearBean.getData().getExhibitionList().get(i));//放入所有企业用户数据
                    }else { //是普通用户
                        LatLng latLng = new LatLng(lat, lon, false);
                        RegionItem regionItem = new RegionItem(latLng, imageNearBean.getData().getExhibitionList().get(i).getOriginalId(), imageNearBean.getData().getExhibitionList().get(i).getUserType());//添加元素
                        items.add(regionItem);
                    }

                }

                log.e("普通用户有==" + items.size());
                log.e("企业用户有==" + bItems.size());

                if ( items.size() > 0) {
    
                    if (mClusterOverlay == null) {
                        //覆盖物的集合,
                        mClusterOverlay = new ClusterOverlay(mAMap, items, dp2px(getApplicationContext(), clusterRadius), getApplicationContext());   //地图对象,集合,聚合范围,上下文传到指定方法实现
                    } else {
                        mClusterOverlay.onDestroy();   //销毁之前的覆盖物集合
                        mClusterOverlay = null;
                        mClusterOverlay = new ClusterOverlay(mAMap, items, dp2px(getApplicationContext(), clusterRadius), getApplicationContext());
                    }
                    //渲染和聚合点点击事件监听
                    mClusterOverlay.setClusterRenderer(MapActivity.this);      //getdrawable 方法实现
                    mClusterOverlay.setOnClusterClickListener(MapActivity.this);    //onclick方法实现
                }
                
                if (  bItems.size() > 0) {
                    //商家用户数据
                    loge("设置企业标签点击事件");
                    if (bClusterOverlay == null) {
                        //覆盖物的集合
                        bClusterOverlay = new BClusterOverlay(mAMap, bItems, dp2px(getApplicationContext(), clusterRadius), getApplicationContext());
                    } else {
                        bClusterOverlay.onDestroy();
                        bClusterOverlay = null;
                        bClusterOverlay = new BClusterOverlay(mAMap, bItems, dp2px(getApplicationContext(), clusterRadius), getApplicationContext());
                    }
                    //渲染和聚合点点击事件监听
                    bClusterOverlay.setClusterRenderer(this);
                    bClusterOverlay.setOnClusterClickListener(MapActivity.this);    //onBclick 方法实现
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addMarkersToMap();//添加普通marker到地图
                    }
                });

            }
        }.start();


    }

```
这里大家可能看得很烦,没错,看得很烦就对了😆!因为这一部分完全没必要看...只是为了筛选后台不同的用户数据而已。记得之前我提到的ClusterItem类中getUserType()方法以及实现这个接口的类RegionItem中定义的userType变量吗?它的作用就是用来区分不同的聚合标签,以上代码的03代表商家用户,其余类型默认普通用户。

最后,就是我们的重头戏了,集中处理多种聚合标签的点击事件:
```
mAMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    //LatLng latLng = cameraPosition.target;
                    
                }

                @Override
                public void onCameraChangeFinish(CameraPosition cameraPosition) {

                    mClusterOverlay.updateClusters();//刷新
                    bClusterOverlay.updateClusters();
                }
            });
            
            
            mAMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    //mClusterOverlay.cleanListener();
                    Cluster cluster = (Cluster) marker.getObject();
                    if(cluster!=null && cluster.getClusterCount()>0){
                        if(cluster.getClusterItems().get(0).getUserType().equals("03")){
                            loge("是普通用户聚合标签");
                            mClusterOverlay.respondClusterClickEvent(marker);//响应聚合点的点击事件    
                        }else {
                            loge("是商家用户聚合标签");
                            bClusterOverlay.respondClusterClickEvent(marker);//响应聚合点的点击事件
                        }
                    }
                    return true;
                }
            });
            
            ......
            
    //聚合标签的点击事件
    @Override
    public void onClick(Marker marker, List<ClusterItem> clusterItems) {
        loge("点击了普通用户");
        
    }
    
    @Override
    public void onBClick(Marker marker, List<ClusterItem> clusterItems) {
        loge("点击了商家用户");
        
    }

```
代码有点多,具体业务逻辑代码就不贴了,估计看了也会比较的烦,大家只需要看onMarkerClick()方法中的逻辑就OK了。一般情况下,我们需要点击不同的marker后,获取该marker应该对应的数据,那么该如何获取呢?为了快点结束战斗,这里我就不卖关子了,高德marker类中有一方法getObject(),官方是这样描述的:

![获取marker附加信息](http://upload-images.jianshu.io/upload_images/5256969-0d434a7340359c3f.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

看到这,很多人一定豁然开朗,没错!还有个对应的方法marker.setObject(Object obj)。在ClusterOverlay中,我们可以找到如下代码:
```
/**
     * 将单个聚合元素添加至地图显示
     *
     * @param cluster
     */
    private void addSingleClusterToMap(Cluster cluster) {
        latlng1 = cluster.getCenterLatLng();
        markerOptions = new MarkerOptions();
        markerOptions
                .anchor(0.5f, 0.5f)
                .icon(getBitmapDes(cluster.getClusterCount()))
                .position(latlng1);
        Marker marker = mAMap.addMarker(markerOptions);
        marker.setAnimation(mADDAnimation);
        marker.setObject(cluster);
        marker.startAnimation();
        cluster.setMarker(marker);
        mcluster = cluster;
        mAddMarkers.add(marker);

    }
```
看看上面这块代码,我们的确发现了marker.setObject(cluster)方法,cluster里面有什么?有ClusterItem呀!看到这,我们就发现,前面的getUsertype()方法不是正好派上用场了嘛!添加聚合标签的时候,我们给每个marker贴上了"名牌",当我们点击聚合标签的时候,拿到附加的信息并找到这个名牌(userType),这样不就可以轻而易举"撕掉名牌"了吗!😆

&emsp;&emsp;这样我们就解决多种聚合标签点击冲突的问题了,至于如何解决聚合标签与普通Marker点击事件冲突,其实大同小异,这里就不献丑了,相信对于大家来说没什么问题。下一篇文章将为大家提供marker加载网络图片的完美解决方案,请拭目以待!

&emsp;&emsp;最后,大家有任何问题或者建议,欢迎留言或者加群讨论,谢谢.

代码地址：<https://github.com/Moosphan/AMapMarker-master>

![Android集中营](http://upload-images.jianshu.io/upload_images/5256969-2bb978e570e25ebe.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

>个人博客传送门:www.moos.club