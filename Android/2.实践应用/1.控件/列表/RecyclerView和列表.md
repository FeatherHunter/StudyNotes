转载请注明链接：http://blog.csdn.net/feather_wch/article/details/79630459

总结列表和RecylerView的要点。
1. ListView的简略介绍，市面上资料很多
2. RecyclerView给出最基本的步骤，和最简单的实现方法。

#列表和RecyclerView
版本:2018/3/23-1(18:10)

##ListView
比较简单，四步骤：
1. ListView所在的布局和Item的布局
2. 自定义适配器(继承BaseAdapter)
3. 给ListView设置适配器对象

自定义适配器主要四部分工作：
1. getCount()-返回数据集的总数，如List的size等
2. getItem(position)-返回数据集中position位置的数据
3. getItemId(int position)-return position即可
4. getView()-通过Item的布局去自定义每行的效果

convertView+ViewHolder是重写getView()的最好办法，能减少`findViewById`的次数，也减少了`重绘View`

##RecyclerView

1、RecyclerView的特点
>1. 名称`回收循环视图`---只管回收与复用View
>2. 强制使用`ViewHolder`，并将其封装，该控件会自动回收和复用

2、RecyclerView的优点
>1. `Item`复用性高
>2. `灵活、可定制化高、可扩展性高`-提供插拔式体验；高度解耦；
>3. 可控制`显示的方式`-通过布局管理器`LayoutManager`
>4. 可控制`Item间的间隔(可绘制)`-通过`ItemDecoration`
>5. 可控制`Item的增删动画`-通过`ItemAnimator`

3、RecyclerView的缺点
>实现`控制点击`、`长按事件`比较麻烦(要自己写)

4、RecyclerView的实现步骤
>1. 定义包含`RecyclerView`的布局
>2. 实现`RecyclerView`的`Item布局`
>3. 继承并实现`RecyclerView.Adapter`(ViewHolder的实现，`点击事件`的实现，定义`列表等数据结构`用于存储数据)
>4. 给`RecyclerView`设置`Adapter`, 并且设置监听器

5、RecyclerView的最简单实现
>1-Activity的布局和`Item`的布局
```xml
//activity_recler_view.XMML
<android.support.constraint.ConstraintLayout  ...>

    <android.support.v7.widget.RecyclerView
        android:id="@+id/activity_recler_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

    </android.support.v7.widget.RecyclerView>

</android.support.constraint.ConstraintLayout>
```
```xml
//reclerview_item.xml
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical">

    <TextView
        android:id="@+id/item_text"
        android:layout_width="match_parent"
        android:layout_height="50dp"
        android:gravity="center"
        android:text="TextView"
        android:textColor="@color/colorAccent"
        android:textSize="20dp" />

</LinearLayout>
```
>2-自定义Adapter
```java
public class RecyclerViewAdapter extends RecyclerView.Adapter{
    //1. 布局
    private LayoutInflater mInflater;
    //2. 数据
    private ArrayList<String> mDatas;
    public RecyclerViewAdapter(Context context, ArrayList<String> stringArrayList){
        super();
        mInflater = LayoutInflater.from(context);
        mDatas = stringArrayList;
    }
    //3. 自定义ViewHolder
    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mItemClickListener != null){
                        mItemClickListener.onItemClick(v, getPosition());
                    }
                }
            });
        }

        public TextView getName(){
            return name;
        }
    }

    //4. 通过Item的布局创建ViewHolder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.reclerview_item, parent, false)));
    }
    //5. 完成类似于getView里面显示数据的功能
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.name.setText(mDatas.get(position));
    }
    //6. 获取数据size
    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    //7. 点击事件的监听
    private ItemClickListener mItemClickListener;
    public interface ItemClickListener{
        public void onItemClick(View view, int position);
    }
    public void setOnItemClickListener(ItemClickListener listener){
        mItemClickListener = listener;
    }
}
```
>3-Activity.java
```java
public class ReclerViewActivity extends Activity {

    RecyclerView mRecyclerView;
    RecyclerViewAdapter mRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recler_view);
        mRecyclerView = findViewById(R.id.activity_recler_view);

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Wang");
        arrayList.add("Gu");
        arrayList.add("Li");
        arrayList.add("Wen");

        //1. 布局
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //2. 适配器
        mRecyclerViewAdapter = new RecyclerViewAdapter(this, arrayList);
        mRecyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(ReclerViewActivity.this, "onItemClick position=" + position, Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
    }
}
```

6、RecyclerView的基本使用
```java
mRecyclerView = findView(R.id.id_recyclerview);
//1. 设置布局管理器
mRecyclerView.setLayoutManager(layout);
//2. 设置adapter
mRecyclerView.setAdapter(adapter)
//3. 设置Item增加、移除动画
mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//4. 添加分割线
mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL_LIST));

```

###分割线定制：ItemDecoration
7、ItemDecoration的使用和主要方法的要点(可以参考官方提供的`DividerItemDecoration`的内部实现)
>1. `ItemDecoration`是抽象类
>2. 使用方法；`mRecyclerView.addItemDecoration() `
>3. 四个方法：`onDraw()\onDrawOver()\getItemOffsets\getItemOffsets`
>4. `addItemDecoration()`后，绘制顺序`onDraw()->onDrawOver()`-复写其中一个即可。
>5. `getItemOffsets` 可以通过`outRect.set()`为每个Item设置一定的偏移量，主要用于`绘制Decorator`。
```java
mRecyclerView = findViewById(R.id.activity_imageview_recyclerview);
mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
mRecyclerView.setAdapter(mRVAdapter = new RVAdapter(this, cityNames));
//1. 这里设置分割线
mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
```

8、LayoutManager的布局管理器
>系统提供的布局管理器有三种:
```java
//1. LinearLayoutManager
mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//2.GridLayoutManager
mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
//3. StaggeredGridLayoutManager: 瀑布流
mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
```


##参考资料
1. [RecyclerView学习链接列表](http://www.wanandroid.com/article/query?k=recyclerview)
1. [ Android RecyclerView 使用完全解析](https://blog.csdn.net/lmj623565791/article/details/45059587)
1. [clipToPadding解决列表滚动无法触及到Padding的问题](https://blog.csdn.net/litefish/article/details/52471273)
2. [深入理解 RecyclerView 系列之一：ItemDecoration](https://blog.piasy.com/2016/03/26/Insight-Android-RecyclerView-ItemDecoration/#fn:add-together)
