&emsp;&emsp;做地图社交类APP开发的都知道,一般情况下,为了整体的美观和用户体验度,我们需要定制化Marker的样式。本文中实现的方式都是基于高德地图的,百度地图也类似,大家可以照葫芦画瓢,废话不多说,先来看看最终效果:

![最终自定义Marker效果](http://upload-images.jianshu.io/upload_images/5256969-9eb999f5d84a9611.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

- 实现思路:
  先来看看高德官方提供的设置Marker图标的方法:

![来自高德官方文档](http://upload-images.jianshu.io/upload_images/5256969-9bc0de0f4fc3462f.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

我们可以看到setIcon()方法,里面的参数BitmapDescriptor就是我们最终需要的东西。那么到底该如何得到这个BitmapDescriptor对象呢,其实很简单,该对象可以通过BitmapDescriptorFactory工厂类获取,BitmapDescriptorFactory提供了以下方法来得到BitmapDescriptor对象:
```
/**
 * 1.通过资源id获取
 */
public static BitmapDescriptor fromResource(int var0) {
        ......
}
/**
 * 2.通过自定义的view获取
 */
 public static BitmapDescriptor fromView(View var0) {
    ...... 
}

/**
 * 3.通过具体路径资源获取
 */
 public static BitmapDescriptor fromPath(String var0) {
    ......
}

/**
 * 4.通过具体Assets资源获取
 */
 public static BitmapDescriptor fromAsset(String var0) {
    ...... 
}
/**
 * 5.通过具体文件获取
 */
public static BitmapDescriptor fromFile(String var0) {
   ......
}

/**
 * 6.通过bitmap获取
 */
 public static BitmapDescriptor fromBitmap(Bitmap var0) {
    ......
 
}


```
从以上官方提供的方法可以看到,得到BitmapDescriptor对象的方式有多种,我们可以根据自己需要自行选择。但是,为了应对频繁的需求变化,我们肯定要选择更为动态,灵活的方式来应付我们的需求变化,我们就可以选择fromView()和fromBitmap()两种方式来得到BitmapDescriptor对象了。这里选用fromBitmap()方式来得到BitmapDescriptor对象吧,因为采用fromView()方法获取后有个巨坑,这个后面再说。

&emsp;&emsp;如果我们要根据需求自定义Marker,当然是希望能够让它可大可小,可动可静了,那就非view莫属了。只要我们可以自定义view布局,然后转化成bitmap不就OK了吗,好,说干就干,我们以自定义Makrer样式并加载网络图片为例,开干!

1. 定制化Marker布局,加载网络图片:
>我们就以实现以下Marker的样式为例:

![Marker样例](http://upload-images.jianshu.io/upload_images/5256969-2441fce42ea75edd.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

这个样式比较简单,只要一个CircleImageView外面套一个固定的容器就可以了,直接看布局代码:
>marker_bg.xml:
```
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="40dp"
    android:layout_height="47dp"
    android:layout_gravity="center">
    <RelativeLayout
        android:layout_width="40dp"
        android:layout_height="47dp"
        android:layout_centerHorizontal="true"
        android:layout_alignParentBottom="true"
        android:background="@mipmap/act_map_business_bg">
        <de.hdodenhof.circleimageview.CircleImageView
            android:id="@+id/marker_item_icon"
            android:layout_width="32dp"
            android:layout_height="32dp"
            android:src="@mipmap/userheadholder"
            android:layout_centerHorizontal="true"
            android:layout_marginTop="1dp" />
    </RelativeLayout>

</RelativeLayout>
```
下面看一下代码中如何使用:
```
/**
     * by moos on 2017/11/13
     * func:定制化marker的图标
     * @return
     */
    private void customizeMarkerIcon(String url, final OnMarkerIconLoadListener listener){
        final View markerView = LayoutInflater.from(this).inflate(R.layout.marker_bg,null);
        final CircleImageView icon = (CircleImageView) markerView.findViewById(R.id.marker_item_icon);
        
        Glide.with(this)
                .load(url+"!/format/webp")
                .asBitmap()
                .thumbnail(0.2f)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                .into(new SimpleTarget<Bitmap>(){
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                        //待图片加载完毕后再设置bitmapDes
                        icon.setImageBitmap(bitmap);
                        bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(convertViewToBitmap(markerView));
                        listener.markerIconLoadingFinished(markerView);
                    }
                });

    }

......

/**
     * by moos on 2017/11/15
     * func:自定义监听接口,用来marker的icon加载完毕后回调添加marker属性
     */
    public interface OnMarkerIconLoadListener{
        void markerIconLoadingFinished(View view);
    }




```
这部分代码也不是很难,我就简单讲讲思路。首先加载布局后,拿到里面的控件CircleImageView,并使用常用的Glide图片加载框架来加载网络图片。可能会有人问,为什么关闭Glide的缓存设置呢?关注到这点的人还是比较细心的,我们使用glide加载大量marker布局的时候,如果社会了缓存选项,那么就可能会出现相同图片只显示一张的状况,这个如果不信的话可以自行实验。
&emsp;&emsp;下面来说说另一个问题,也就是刚刚我们为什么选择fromBitmap()而不是fromView()方法,因为之前用fromView()时发现添加的第一个marker并不会加载出图片,而只是显示默认的占位图。即使通过在Glide加载图片的回调方法onResourceReady()设置也依然无效,这个问题大家不信邪也可以试试看看,这就是最终为什么选择fromBitmap()方法的原因。这里还使用了自定义的接口来传入markerView,方便后面的二次开发,然后在回调方法markerIconLoadingFinished()方法中设置marker的图标。

2. 将view转化为bitmap对象:
```
/**
     * by mos on 2017.11.13
     * func:view转bitmap
     */
    public static Bitmap convertViewToBitmap(View view) {

        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.buildDrawingCache();

        Bitmap bitmap = view.getDrawingCache();

        return bitmap;

    }
```
网上相关方法很多,这里就不介绍了,有兴趣可以去百度一下。

3. 批量添加自定义的Marker到地图上:
  先来看看代码:
```
/**
     * by moos on 2017/11/15
     * func:添加marker到地图上显示
     */
    BitmapDescriptor bitmapDescriptor ;
    private void addMarker(final ImageNearBean.DataBean.ExhibitionListBean bean) {
        double lat;
        double lon;
        lat = bean.getLatitude();
        lon = bean.getLongitude();
        LatLng latLng = new LatLng(lat, lon, false);
        loge("添加maker前的类型为==="+bean.getUserType());

        String url = bean.getUserLogo();
        final MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.setFlat(true);
        markerOptions.anchor(0.5f, 0.5f);
        markerOptions.position(new LatLng(lat, lon));
        customizeMarkerIcon(url, bean.getTeamName(),new OnMarkerIconLoadListener() {
            @Override
            public void markerIconLoadingFinished(View view) {
                //bitmapDescriptor = BitmapDescriptorFactory.fromView(view);
                markerOptions.icon(bitmapDescriptor);
                Marker marker;
                marker = mAMap.addMarker(markerOptions);
                //marker.setObject(cluster);
            }
        });

    }

    /**
     * by moos on 2017/11/15
     * func:批量添加marker到地图上
     */
    private void addMarkersToMap(){

        runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for(int i = 0;i<allBussinessBean.size();i++){
                          addMarker(allBussinessBean.get(i));
                        }
                    }
                });
    
    }
    
    
```
既然marker样式定制完毕,BitmapDescriptor也拿到了,那么下面就简单了,应该就不需要我多说了,只要注意一点,批量添加marker一般比较耗时,尽量放在子线程处理。

&emsp;&emsp;到这里,自定义marker就实现了,这应该可以适用于多数情况了,一路看下来,是不是也挺简单的,只要用心思考研究就好了,而且搞定后有很强的工作动力有木有!

&emsp;&emsp;最后,大家有任何问题或者建议,欢迎留言或者加群讨论,谢谢.
代码地址 ：<https://github.com/Moosphan/AMapMarker-master>
![Android集中营](http://upload-images.jianshu.io/upload_images/5256969-aa35133f07872669.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)