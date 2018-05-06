在Android的日常开发中，评论与回复功能是我们经常遇到的需求之一，其中评论与回复列表的展示一般在功能模块中占比较大。对于需求改动和迭代较频繁的公司来说，如何快速开发一个二级界面来适应我们的功能需求无疑优先级更高一些。首先我们来看看其他社交类app的评论与回复列表如何展示的：

![](http://ovl7kcyr4.bkt.clouddn.com/18-4-11/21410582.jpg)                          ![](http://ovl7kcyr4.bkt.clouddn.com/18-4-11/25414551.jpg)



Twitter不用说了，全球知名社交平台，上亿用户量，他们的评论回复都只展示一级数据(评论数据)，其他更多内容（回复内容），是需要页面跳转去查看，知乎也类似。第一张图是我们设计给我找的，他说要按照这个风格来，尽量将评论和回复内容在一个页面展示。好吧，没办法，毕竟我们做前端的，UI要看设计脸色，数据要看后台脸色😂。看到设计图，我们脑海肯定第一时间联想一下解决方案：用recyclerview？listview？不对，分析一下它的层级发现，评论是一个列表，里面的回复又是一个列表，难道用recyclerview或者listview的嵌套？抱着不确定的态度，立马去网上查一下，果不其然，搜到的实现方式大多都是用嵌套实现的，来公司之前，其中一个项目里的评论回复功能就是用的嵌套listview，虽然处理了滑动冲突问题，但效果不佳，而且时常卡顿，所以，这里我肯定要换个思路。



网上还有说用自定义view实现的，但我发现大多没有处理view的复用，而且开发成本大，暂时不予考虑。那怎么办？无意中看到expandable这个关键词，我突然想到谷歌很早之前出过一个扩展列表的控件 - ExpandableListView，但听说比较老，存在一些问题。算了，试试再说，顺便熟悉一下以前基础控件的用法。

先来看一下最终的效果图吧：

![](http://ovl7kcyr4.bkt.clouddn.com/18-4-24/18985123.jpg)



这只是一个简单的效果图，你可以在此基础上来完善它。好了，废话不多说，下面让我们来看看效果具体如何实现的吧。大家应该不难看出来，页面整体采用了CoordinatorLayout来实现详情页的顶部视差效。同时，这里我采用ExpandableListView来实现多级列表，然后再解决它们的嵌套滑动问题。OK，我们先从ExpandableListView开始动手。

## ExpandableListView

官方对于ExpandableListView给出这样的解释：A view that shows items in a vertically scrolling two-level list. This differs from the `ListView` by allowing two levels: groups which can individually be expanded to show its children. The items come from the `ExpandableListAdapter` associated with this view.

简单来说，ExpandableListView是一个用于垂直方向滚动的二级列表视图，ExpandableListView与listview不同之处在于，它可以实现二级分组，并通过ExpandableListAdapter来绑定数据和视图。下面我们来一起实现上图的效果。

### 布局中定义

首先，我们需要在xml的布局文件中声明ExpandableListView：

```
<ExpandableListView
    android:id="@+id/detail_page_lv_comment"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:divider="@null"
    android:layout_marginBottom="64dp"
    android:listSelector="@android:color/transparent"
    android:scrollbars="none"/>
```

这里需要说明两个问题：

1. ExpandableListView默认为它的item加上了点击效果，由于item里面还包含了childItem，所以，点击后，整个item里面的内容都会有点击效果。我们可以取消其点击特效，避免其影响用户体验，只需要设置如上代码中的listSelector即可。
2. ExpandableListView具有默认的分割线，可以通过divider属性将其隐藏。



### 设置Adapter

正如使用listView那样，我们需要为ExpandableListView设置一个适配器Adapter，为其绑定数据和视图。ExpandableListView的adapter需要继承自ExpandableListAdapter，具体代码如下：

```
/**
 * Author: Moos
 * E-mail: moosphon@gmail.com
 * Date:  18/4/20.
 * Desc: 评论与回复列表的适配器
 */

public class CommentExpandAdapter extends BaseExpandableListAdapter {
    private static final String TAG = "CommentExpandAdapter";
    private List<CommentDetailBean> commentBeanList;
    private Context context;

    public CommentExpandAdapter(Context context, List<CommentDetailBean> commentBeanList)               {
        this.context = context;
        this.commentBeanList = commentBeanList;
    }

    @Override
    public int getGroupCount() {
        return commentBeanList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        if(commentBeanList.get(i).getReplyList() == null){
            return 0;
        }else {
            return commentBeanList.get(i).getReplyList().size()>0 ? commentBeanList.get(i).getReplyList().size():0;
        }

    }

    @Override
    public Object getGroup(int i) {
        return commentBeanList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return commentBeanList.get(i).getReplyList().get(i1);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return getCombinedChildId(groupPosition, childPosition);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
    boolean isLike = false;

    @Override
    public View getGroupView(final int groupPosition, boolean isExpand, View convertView, ViewGroup viewGroup) {
        final GroupHolder groupHolder;

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.comment_item_layout, viewGroup, false);
            groupHolder = new GroupHolder(convertView);
            convertView.setTag(groupHolder);
        }else {
            groupHolder = (GroupHolder) convertView.getTag();
        }
        Glide.with(context).load(R.drawable.user_other)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .error(R.mipmap.ic_launcher)
                .centerCrop()
                .into(groupHolder.logo);
        groupHolder.tv_name.setText(commentBeanList.get(groupPosition).getNickName());
        groupHolder.tv_time.setText(commentBeanList.get(groupPosition).getCreateDate());
        groupHolder.tv_content.setText(commentBeanList.get(groupPosition).getContent());
        groupHolder.iv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLike){
                    isLike = false;
                    groupHolder.iv_like.setColorFilter(Color.parseColor("#aaaaaa"));
                }else {
                    isLike = true;
                    groupHolder.iv_like.setColorFilter(Color.parseColor("#FF5C5C"));
                }
            }
        });

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean b, View convertView, ViewGroup viewGroup) {
        final ChildHolder childHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.comment_reply_item_layout,viewGroup, false);
            childHolder = new ChildHolder(convertView);
            convertView.setTag(childHolder);
        }
        else {
            childHolder = (ChildHolder) convertView.getTag();
        }

        String replyUser = commentBeanList.get(groupPosition).getReplyList().get(childPosition).getNickName();
        if(!TextUtils.isEmpty(replyUser)){
            childHolder.tv_name.setText(replyUser + ":");
        }

        childHolder.tv_content.setText(commentBeanList.get(groupPosition).getReplyList().get(childPosition).getContent());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    private class GroupHolder{
        private CircleImageView logo;
        private TextView tv_name, tv_content, tv_time;
        private ImageView iv_like;
        public GroupHolder(View view) {
            logo =  view.findViewById(R.id.comment_item_logo);
            tv_content = view.findViewById(R.id.comment_item_content);
            tv_name = view.findViewById(R.id.comment_item_userName);
            tv_time = view.findViewById(R.id.comment_item_time);
            iv_like = view.findViewById(R.id.comment_item_like);
        }
    }

    private class ChildHolder{
        private TextView tv_name, tv_content;
        public ChildHolder(View view) {
            tv_name = (TextView) view.findViewById(R.id.reply_item_user);
            tv_content = (TextView) view.findViewById(R.id.reply_item_content);
        }
    }

}
```

一般情况下，我们自定义自己的ExpandableListAdapter后，需要实现以下几个方法：

- 构造方法，这个应该无需多说了，一般用来初始化数据等操作。
- getGroupCount，返回group分组的数量，在当前需求中指代评论的数量。
- getChildrenCount，返回所在group中child的数量，这里指代当前评论对应的回复数目。
- getGroup，返回group的实际数据，这里指的是当前评论数据。
- getChild，返回group中某个child的实际数据，这里指的是当前评论的某个回复数据。
- getGroupId，返回分组的id，一般将当前group的位置传给它。
- getChildId，返回分组中某个child的id，一般也将child当前位置传给它，不过为了避免重复，可以使用`getCombinedChildId(groupPosition, childPosition);`来获取id并返回。
- hasStableIds，表示分组和子选项是否持有稳定的id，这里返回true即可。
- isChildSelectable，表示分组中的child是否可以选中，这里返回true。
- getGroupView，即返回group的视图，一般在这里进行一些数据和视图绑定的工作，一般为了复用和高效，可以自定义ViewHolder，用法与listview一样，这里就不多说了。
- getChildView，返回分组中child子项的视图，比较容易理解，第一个参数是当前group所在的位置，第二个参数是当前child所在位置。



这里的数据是我自己做的模拟数据，不过应该算是较为通用的格式了，大体格式如下：

![数据格式](http://ovl7kcyr4.bkt.clouddn.com/18-4-24/70716804.jpg)

一般情况下，我们后台会通过接口返回给我们一部分数据，如果想要查看更多评论，需要跳转到“更多页面”去查看，这里为了方便，我们只考虑加载部分数据。



### Activity中使用

接下来，我们就需要在activity中显示评论和回复的二级列表了：

```
private ExpandableListView expandableListView;
private CommentExpandAdapter adapter;
private CommentBean commentBean;
private List<CommentDetailBean> commentsList;

...

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
       
        expandableListView = findViewById(R.id.detail_page_lv_comment);
        initExpandableListView(commentsList);
    }

    /**
     * 初始化评论和回复列表
     */
    private void initExpandableListView(final List<CommentDetailBean> commentList){
        expandableListView.setGroupIndicator(null);
        //默认展开所有回复
        adapter = new CommentExpandAdapter(this, commentList);
        expandableListView.setAdapter(adapter);
        for(int i = 0; i<commentList.size(); i++){
            expandableListView.expandGroup(i);
        }
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                boolean isExpanded = expandableListView.isGroupExpanded(groupPosition);
                Log.e(TAG, "onGroupClick: 当前的评论id>>>"+commentList.get(groupPosition).getId());
                
//                if(isExpanded){
//                    expandableListView.collapseGroup(groupPosition);
//                }else {
//                    expandableListView.expandGroup(groupPosition, true);
//                }
              
                return true;
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                Toast.makeText(MainActivity.this,"点击了回复",Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                //toast("展开第"+groupPosition+"个分组");

            }
        });

    }

    /**
     * by moos on 2018/04/20
     * func:生成测试数据
     * @return 评论数据
     */
    private List<CommentDetailBean> generateTestData(){
        Gson gson = new Gson();
        commentBean = gson.fromJson(testJson, CommentBean.class);
        List<CommentDetailBean> commentList = commentBean.getData().getList();
        return commentList;
    }

```

就以上代码作一下简单说明：

1. ExpandableListView在默认情况下会为我们自带分组的icon（▶️），当前需求下，我们根本不需要展示，可以通过`expandableListView.setGroupIndicator(null)`来隐藏。

2. 一般情况下，我们可能需要默认展开所有的分组，我就可以通过循环来调用`expandableListView.expandGroup(i);`方法。

3. ExpandableListView为我们提供了group和child的点击事件，分别通过`setOnGroupClickListener`和`setOnChildClickListener`来设置。值得注意的是，group的点击事件里如果我们返回的是false，那么我们点击group就会自动展开，但我这里碰到一个问题，当我返回false时，第一条评论数据会多出一条。通过百度查找方法，虽然很多类似问题，但终究没有解决，最后我返回了ture，并通过以下代码手动展开和收缩就可以了：

   ```
   if(isExpanded){
       expandableListView.collapseGroup(groupPosition);
   }else {
       expandableListView.expandGroup(groupPosition, true);
   }
   ```

4. 此外，我们还可以通过`setOnGroupExpandListener`和`setOnGroupCollapseListener`来监听ExpandableListView的分组展开和收缩的状态。

### 评论和回复功能

为了模拟整个评论和回复功能，我们还需要手动插入收据并刷新数据列表。这里我就简单做一下模拟，请忽略一些UI上的细节。

#### 插入评论数据

插入评论数据比较简单，只需要在list中插入一条数据并刷新即可：

```
String commentContent = commentText.getText().toString().trim();
if(!TextUtils.isEmpty(commentContent)){

    //commentOnWork(commentContent);
    dialog.dismiss();
    CommentDetailBean detailBean = new CommentDetailBean("小明", commentContent,"刚刚");
    adapter.addTheCommentData(detailBean);
    Toast.makeText(MainActivity.this,"评论成功",Toast.LENGTH_SHORT).show();

}else {
    Toast.makeText(MainActivity.this,"评论内容不能为空",Toast.LENGTH_SHORT).show();
}
```

adapter中的addTheCommentData方法如下：

```
/**
 * by moos on 2018/04/20
 * func:评论成功后插入一条数据
 * @param commentDetailBean 新的评论数据
 */
public void addTheCommentData(CommentDetailBean commentDetailBean){
    if(commentDetailBean!=null){

        commentBeanList.add(commentDetailBean);
        notifyDataSetChanged();
    }else {
        throw new IllegalArgumentException("评论数据为空!");
    }

}
```

代码比较容易理解，就不多做说明了。

#### 插入回复数据

首先，我们需要实现点击某一条评论，然后`@ta`,那么我们需要在group的点击事件里弹起回复框：

```
expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
   
                showReplyDialog(groupPosition);
                return true;
            }
        });
        
......

/**
     * by moos on 2018/04/20
     * func:弹出回复框
     */
    private void showReplyDialog(final int position){
        dialog = new BottomSheetDialog(this);
        View commentView = LayoutInflater.from(this).inflate(R.layout.comment_dialog_layout,null);
        final EditText commentText = (EditText) commentView.findViewById(R.id.dialog_comment_et);
        final Button bt_comment = (Button) commentView.findViewById(R.id.dialog_comment_bt);
        commentText.setHint("回复 " + commentsList.get(position).getNickName() + " 的评论:");
        dialog.setContentView(commentView);
        bt_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String replyContent = commentText.getText().toString().trim();
                if(!TextUtils.isEmpty(replyContent)){

                    dialog.dismiss();
                    ReplyDetailBean detailBean = new ReplyDetailBean("小红",replyContent);
                    adapter.addTheReplyData(detailBean, position);
                    Toast.makeText(MainActivity.this,"回复成功",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this,"回复内容不能为空",Toast.LENGTH_SHORT).show();
                }
            }
        });
        commentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(charSequence) && charSequence.length()>2){
                    bt_comment.setBackgroundColor(Color.parseColor("#FFB568"));
                }else {
                    bt_comment.setBackgroundColor(Color.parseColor("#D8D8D8"));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dialog.show();
    }

```

插入回复的数据与上面插入评论类似，这里贴一下adapter中的代码：

```
/**
 * by moos on 2018/04/20
 * func:回复成功后插入一条数据
 * @param replyDetailBean 新的回复数据
 */
public void addTheReplyData(ReplyDetailBean replyDetailBean, int groupPosition){
    if(replyDetailBean!=null){
        Log.e(TAG, "addTheReplyData: >>>>该刷新回复列表了:"+replyDetailBean.toString() );
        if(commentBeanList.get(groupPosition).getReplyList() != null ){
            commentBeanList.get(groupPosition).getReplyList().add(replyDetailBean);
        }else {
            List<ReplyDetailBean> replyList = new ArrayList<>();
            replyList.add(replyDetailBean);
            commentBeanList.get(groupPosition).setReplyList(replyList);
        }
        notifyDataSetChanged();
    }else {
        throw new IllegalArgumentException("回复数据为空!");
    }

}
```

需要注意一点，由于不一定所有的评论都有回复数据，所以在插入数据前我们要判断ReplyList是否为空，如果不为空，直接获取当前评论的回复列表，并插入数据；如果为空，需要new一个ReplyList，插入数据后还要为评论set一下ReplyList。

## 解决CoordinatorLayout与ExpandableListView嵌套问题

如果你不需要使用CoordinatorLayout或者NestedScrollView，可以跳过本小节。一般情况下，我们产品为了更好的用户体验，还需要我们加上类似的顶部视差效果或者下拉刷新等，这就要我们处理一些常见的嵌套滑动问题了。

由于CoordinatorLayout实现NestedScrollingParent接口,RecycleView实现了NestedScrollingChild接口,所以就可以在NestedScrollingChildHelper的帮助下实现嵌套滑动，那么我们也可以通过自定义的ExpandableListView实现NestedScrollingChild接口来达到同样的效果：

```
/**
 * Author: Moos
 * E-mail: moosphon@gmail.com
 * Date:  18/4/20.
 * Desc: 自定义ExpandableListView,解决与CoordinatorLayout滑动冲突问题
 */

public class CommentExpandableListView extends ExpandableListView implements NestedScrollingChild{
    private NestedScrollingChildHelper mScrollingChildHelper;


    public CommentExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScrollingChildHelper = new NestedScrollingChildHelper(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setNestedScrollingEnabled(true);
        }

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mScrollingChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mScrollingChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mScrollingChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mScrollingChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                        int dyUnconsumed, int[] offsetInWindow) {
        return mScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }
}
```

代码就不解释了，毕竟这不是本篇文章等重点，大家可以去网上查阅NestedScrollView相关文章或者源码去对照理解。

完整的布局代码比较多，这里就不贴了，大家可以去github上面查看：<https://github.com/Moosphan/CommentWithReplyView-master>