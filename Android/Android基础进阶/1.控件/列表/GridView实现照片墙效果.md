转载请注明：http://blog.csdn.net/feather_wch/article/details/79568078

本文介绍照片墙功能的实现思路和源码。照片墙效果也和相册类似。

整体思路是：
1. 使用GridView控件(设置好其布局，以及Item的布局)
2. 自定义GridView的适配器(重点是getView方法的重写)
3. 使用GridView并设置适配器


如果有问题请指出，万分感谢！

##源码

GridView的布局
```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/colorPrimary">

    <GridView
        android:id="@+id/gridView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:gravity="center"
        android:horizontalSpacing="5dp"
        android:verticalSpacing="5dp"
        android:listSelector="@android:color/transparent"
        android:numColumns="3"
        android:stretchMode="columnWidth">
    </GridView>
</LinearLayout>
```

GridView Item的布局
```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <com.example.a6005001819.androiddeveloper.Drawable.SquareImageView
        android:id="@+id/image"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:scaleType="centerCrop"
        android:src="@drawable/ic_launcher"/>

</LinearLayout>
```

GrirdView的Item使用自定义的正方形ImageView
```java
public class SquareImageView extends ImageView{
    //...
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
      //重点：将高度的也设置为宽度的widthMeasureSpec
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
```

GridView的Adapter
```java
public class ImageAdapter extends BaseAdapter{
    ArrayList<String> mUrlList;
    LayoutInflater mInflater;
    ImageLoader mImageLoader;
    Boolean mIsStopScroll = true;

    public ImageAdapter(Context context, ArrayList<String> urls){
        mImageLoader = new ImageLoader(context);
        mInflater = LayoutInflater.from(context);
        mUrlList = urls;
    }
    @Override
    public int getCount() {
        return mUrlList.size();
    }
    @Override
    public Object getItem(int position) {
        return mUrlList.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.gridview_item, parent, false);
            holder = new ViewHolder();
            holder.imageView = (ImageView)convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        ImageView imageView = holder.imageView;
        final String tag = (String) imageView.getTag();
        final String url = (String) getItem(position);
        //1、图片已经发生了变化，使用默认图片
        if(!url.equals(tag)){
            imageView.setImageResource(R.drawable.ic_launcher);
        }
        //2、停止滚动时加载图片
        if(mIsStopScroll){
            imageView.setTag(url);
            mImageLoader.bindBitmap((String)getItem(position), holder.imageView, 200, 200);
        }
        return convertView;
    }

    class ViewHolder{
        ImageView imageView;
    }

    public void setmIsStopScroll(Boolean mIsStopScroll) {
        this.mIsStopScroll = mIsStopScroll;
    }
}
```

GrdiView的使用
```java
//1. 获得控件并设置监听器(解决快速滑动时列表卡顿问题)
mGridView = view.findViewById(R.id.gridView);
mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
            mImageAdapter.setmIsStopScroll(true);
            mImageAdapter.notifyDataSetChanged();
        }else{
            mImageAdapter.setmIsStopScroll(false);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }
});
//2. 设置适配器(提供图片的Url列表)
mImageAdapter = new ImageAdapter(getActivity(), mUrlList);
mGridView.setAdapter(mImageAdapter);
```
