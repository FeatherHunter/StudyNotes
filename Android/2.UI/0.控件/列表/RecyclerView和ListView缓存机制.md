转载请注明链接：https://blog.csdn.net/feather_wch/article/details/81613313

# RecyclerView和ListView原理

最后修改版本：2018/8/20-1(0:52)

----

[TOC]

1、RecyclerView和ListView缓存原理
![RV和ListView](http://mmbiz.qpic.cn/mmbiz_png/tnZGrhTk4desHkFxgnbyfgeoUgmoppufzCkX1dEY7oa7GEXf6kNmDGrGmu7W8wgrTaepT7cibpblRQdI6A1r7Ww/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1)
>1. 离屏的ItemView即被回收至缓存
>2. 入屏的ItemView则会优先从缓存中获取
>3. 只是ListView与RecyclerView的实现细节有差异

## ListView

2、AbsListView
>1. ListView和GridView都继承自AbsListView
> 1. ListView/GridView的缓存原理都处于AbsListView中

3、AbsListView的测量过程
```java
@Override
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    if (mSelector == null) {
        useDefaultSelector();
    }
   //计算padding
    final Rect listPadding = mListPadding;
    listPadding.left = mSelectionLeftPadding + mPaddingLeft;
    listPadding.top = mSelectionTopPadding + mPaddingTop;
    listPadding.right = mSelectionRightPadding + mPaddingRight;
    listPadding.bottom = mSelectionBottomPadding + mPaddingBottom;
    // 如果transcriptMode是TRANSCRIPT_MODE_NORMAL，
    //当Adapter中的数据集改变之后，其子类会自动滚动到底部
    if (mTranscriptMode == TRANSCRIPT_MODE_NORMAL) {
        final int childCount = getChildCount();
        final int listBottom = getHeight() - getPaddingBottom();
        final View lastChild = getChildAt(childCount - 1);
        final int lastBottom = lastChild != null ? lastChild.getBottom() : listBottom;
        mForceTranscriptScroll = mFirstPosition + childCount >= mLastHandledItemCount &&
                lastBottom <= listBottom;
    }
}
```
> 1. padding计算
> 1. 当Adapter中的数据集改变之后，其子类会自动滚动到底部

4、AbsListView的布局过程
```java
@Override
protected void onLayout(boolean changed, int l, int t, int r, int b) {
    super.onLayout(changed, l, t, r, b);

    xxx
    final int childCount = getChildCount();
    if (changed) {
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).forceLayout();
        }
      // 1-重新测量Child,mRecycler其实就是RecycleBin---用于管理回收View的回收类
        mRecycler.markChildrenDirty();
    }
    // 2-给Child布局
    layoutChildren();
    xxx
}
```
> 1. 子类不允许覆盖onLayout方法
> 1. 子类如ListView需要重写layoutChildren()方法
> 1. mRecycler.markChildrenDirty(): 对Child进行重新测量
> 1. layoutChildren(): 给Child布局

### RecycleBin

5、RecycleBin是什么？
> 1. 用于帮助在layout过程中View的复用。
> 1. 包含两种层级的缓存：ActiveView和ScarpViews
> 1. ActiveViews：布局开始时处于屏幕上的View---会在离屏时被降级为ScrapViews
> 1. ScrapViews：Adpater用于避免不必要的分配View而使用的老旧的View
> 1. 缓存的操作一般为4种：初始化、存、取、清空缓存。

6、RecycleBin
```java
    class RecycleBin {

        // 可见的View数组
        private View[] mActiveViews = new View[0];
        // 存储在可见View数组中第一个View的位置
        private int mFirstActivePosition;


        // 不可见的View集合的数组: 每种类型的Item都用一个集合存储、未排序、用于重用
        private ArrayList<View>[] mScrapViews;
        // View的类型(Type)的数量
        private int mViewTypeCount;
        // mScrapViews数组中第一个元素(集合)；或者说View Type = 1的集合
        private ArrayList<View> mCurrentScrap;

        xxx
    }
```
>1. mActiveViews: 可见的View数组
> 1. mScrapViews: 不可见的View集合的数组

7、ViewType和ViewTypeCount是什么？
> 1. 当ListView需要实现复杂列表时，比如根据Type从而显示的样式不同。需要View Type进行区分。
> 1. ViewTypeCount：有几种不同的View
```java
    // View的类型-int值.必须从0开始依次递增.
    private static final int TYPE_TITLE = 0;
    private static final int TYPE_CONTENT = 1;
    // ListView创建View
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        switch (getItemViewType(position)){
            case TYPE_TITLE:
                 // 第一种布局
                break;
            case TYPE_CONTENT:
                 // 第二种布局
                break;
        }
        return convertView;
    }
    // 根据数据列表中的数据返回当前位置所属的Type，由开发者自定义。
    @Override
    public int getItemViewType(int position) {
        if(TextUtils.isEmpty(mData.get(position).getCode())){
            return TYPE_TITLE;
        }else{
            return TYPE_CONTENT;
        }
    }
```

8、RecycleBin-初始化缓存
```java
    //AbsListView.java的内部类：RecycleBin---初始化缓存
    public void setViewTypeCount(int viewTypeCount) {
        if (viewTypeCount < 1) {
            throw new IllegalArgumentException("Can't have a viewTypeCount < 1");
        }
        // 1-根据ViewTypeCount初始化数组： 不可见View集合的数组
        ArrayList<View>[] scrapViews = new ArrayList[viewTypeCount];
        for (int i = 0; i < viewTypeCount; i++) {
            scrapViews[i] = new ArrayList<View>();
        }
        // 2-初始化RecycleBin的数组
        mViewTypeCount = viewTypeCount;
        mCurrentScrap = scrapViews[0];
        mScrapViews = scrapViews;
    }
```
> setViewTypeCount在设置Adpater时进行调用。
```java
    //使用: ListView.java
    @Override
    public void setAdapter(ListAdapter adapter) {
        // 1-清空RecycleBin
        mRecycler.clear();
        // 2-设置新的Adapter
        super.setAdapter(adapter);
        // 3-设置View的类型数量
        mRecycler.setViewTypeCount(mAdapter.getViewTypeCount());
        // 4-请求重新布局
        requestLayout();
    }
```
> 1. 缓存初始化就是创建`ScrapViews`的过程。
> 1. ListView设置Adpater时会对缓存进行清空，并且进行缓存初始化。

9、RecycleBin-存缓存
```java
    /**======================================
     * //AbsListView.java
     * 存储屏幕上可见的View---将所有子View保存到ActiveViews中
     *====================================*/
    void fillActiveViews(int childCount, int firstActivePosition) {
        // 1、扩容检查
        if (mActiveViews.length < childCount) {
            mActiveViews = new View[childCount];
        }
        // 2、存储在ActiveViews中第一个View的position
        mFirstActivePosition = firstActivePosition;

        // 3、遍历子View---将非头部非尾部的View放置到ActiveViews数组中
        final View[] activeViews = mActiveViews;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            // 存储
            if (lp != null && lp.viewType != ITEM_VIEW_TYPE_HEADER_OR_FOOTER) {
                activeViews[i] = child;
            }
        }
    }

    /**======================================
     * //AbsListView.java
     * 将View添加到ScrapViews中---对不在屏幕中的View进行缓存
     *====================================*/
    void addScrapView(View scrap, int position) {
        xxx
        // 不直接缓存具有transient state的View，用transient数组进行缓存
        final boolean scrapHasTransientState = scrap.hasTransientState();
        if (scrapHasTransientState) {
            //
        } else {
            // 根据Type存储到ScrapViews中
            mScrapViews[viewType].add(scrap);
        }
    }
```

10、RecycleBin-取缓存
```java
    // 获取缓存的View(不可见)
    View getScrapView(int position) {
        // 1、拿到当前位置View的type
        final int whichScrap = mAdapter.getItemViewType(position);
        // 2、type的种类为1，直接从第一个数组中取。否则从对应的type数组中取出
        if (mViewTypeCount == 1) {
            //type的种类为1，直接从第一个数组中取
            return retrieveFromScrap(mCurrentScrap, position);
        } else if (whichScrap < mScrapViews.length) {
            //直接从对应的type数组中取
            return retrieveFromScrap(mScrapViews[whichScrap], position);
        }
        return null;
    }

    // 获取可见的View
    View getActiveView(int position) {
        int index = position - mFirstActivePosition;
        final View[] activeViews = mActiveViews;
        if (index >=0 && index < activeViews.length) {
            // 1、返回ActiveView数组中的View
            final View match = activeViews[index];
            //获取之后将数组置空，便于虚拟机回收
            activeViews[index] = null;
            return match;
        }
        return null;
    }
```

11、RecycleBin-清除缓存
```java
    // 清除指定的ScrapView
    private void clearScrap(final ArrayList<View> scrap) {
        final int scrapCount = scrap.size();
        for (int j = 0; j < scrapCount; j++) {
            removeDetachedView(scrap.remove(scrapCount - 1 - j), false);
        }
    }

    // 清空所有缓存数组
    void clear() {
        if (mViewTypeCount == 1) {
            final ArrayList<View> scrap = mCurrentScrap;
            clearScrap(scrap);
        } else {
            final int typeCount = mViewTypeCount;
            for (int i = 0; i < typeCount; i++) {
                final ArrayList<View> scrap = mScrapViews[i];
                clearScrap(scrap);
            }
        }
        clearTransientStateViews();
    }
```

12、RecycleBin-markChildrenDirty
```java
    // 执行所有缓存数组中View的forceLayout
    public void markChildrenDirty() {
        // 1、ScrapView全部执行forceLayout
        for (int i = 0; i < typeCount; i++) {
            final ArrayList<View> scrap = mScrapViews[i];
            final int scrapCount = scrap.size();
            for (int j = 0; j < scrapCount; j++) {
                scrap.get(j).forceLayout();
            }
        }
        // 2、TransientStateViews都执行forceLayout
        for (int i = 0; i < count; i++) {
            mTransientStateViews.valueAt(i).forceLayout();
        }
        // 3、TransientStateViewsById都执行forceLayout
        for (int i = 0; i < count; i++) {
            mTransientStateViewsById.valueAt(i).forceLayout();
        }
    }
```

### 缓存流程

13、ListView有两级缓存
![ListView两级缓存](https://mmbiz.qpic.cn/mmbiz_png/tnZGrhTk4desHkFxgnbyfgeoUgmoppufHz28MwvsyiboPFm87p0KP9X8tXSxfyMa418y5A6W4IM1YHxzIWyegnA/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&retryload=1)
>1. 第一层`ActiveViews`：用于屏幕内ItemView的快速重用
>1. 第二层`ScrapViews`：不可见View
```java
// 1-mActiveViews是View数组
private View[] mActiveViews = new View[0];
// 2-mScrapViews是ArrayList
private ArrayList<View>[] mScrapViews;
```

14、ListView缓存流程
![ListView缓存流程](http://mmbiz.qpic.cn/mmbiz_png/tnZGrhTk4desHkFxgnbyfgeoUgmoppufCXA793Su8XIsICg4MXBhnQZpTcy4mrYicqqz075zF26aXNopowaCArg/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1)
>1. 有新View需要显式的时候，先去ActiveViews中获取。存在就直接取出并且复用。
>1. 不存在，会从ScrapView中去获取。存在就会复用，避免执行createView但是会执行bindView。
>1. ScrapViews中也不存在，就会inflate View，然后bindView。

15、ListView布局流程
```java
    // ListView.java-布局Children
    @Override
    protected void layoutChildren() {
        // 1-从ListView当前顶部往下填充
        fillFromTop(childrenTop);
        xxx
    }
    // ListView.java-从顶至下填充
    private View fillFromTop(int nextTop) {
        //xxx
        return fillDown(mFirstPosition, nextTop);
    }
    // ListView.java-从pos填充到屏幕可见区域的底部
    private View fillDown(int pos, int nextTop) {
        // 1、创建View填充ListView(屏幕可见区域的最上端填充到底部，或者Item已经都创建完毕)
        while (nextTop < end && pos < mItemCount) {
            boolean selected = pos == mSelectedPosition;
            // 2、创建并且获取到ChildView
            View child = makeAndAddView(pos, nextTop, true, mListPadding.left, selected);
            //xxx
        }
        return selectedView;
    }
    // ListView.java-获取到Child View
    private View makeAndAddView(int position, int y, boolean flow, int childrenLeft, boolean selected) {
        // 1、数据集没有变化，从ActiveView中获取
        if (!mDataChanged) {
            final View activeView = mRecycler.getActiveView(position);
            return activeView;
        }else{
            // 2、数据集中有变化
            final View child = obtainView(position, mIsScrap);
            return child;
        }
    }

    // AbsListView.java-从缓存中获取到View
    View obtainView(int position, boolean[] outMetadata) {
        // 1、获取到ScrapView(不可见的View)
        final View scrapView = mRecycler.getScrapView(position);
        // 2、获取到ChildView(createView和bindView)
        final View child = mAdapter.getView(position, scrapView, this);
        if (scrapView != null) {
            if (child != scrapView) {
                // 3、将新View(复用后的View)添加到ScrapView中
                mRecycler.addScrapView(scrapView, position);
            }
        }
        return child;
    }
```
> 1. onLayout(): 调用layoutChildren
> 1. layoutChildren(): fillFromTop
> 1. fillFromTop(): fillDown
> 1. makeAndAddView(): obtainView

## RecyclerView

1、RecyclerView特点
> 1. 多样式：可以对数据的展示进行定制，可以是列表\网格\瀑布流，还可以自定义样式.
> 1. 定向刷新：可以对指定的Item数据进行刷新
> 1. 刷新动画：RecyclerView支持对Item的刷新添加动画
> 1. 添加装饰：相对于ListView以及GridView的单一的分割线，RecyclerView可以自定义添加分割样式

2、RecyclerView的6大组成

|RecyclerView内部类||
|---|---|
|LayoutManager|负责Item的布局和显示|
|ItemDecoration|给Item添加修饰的View|
|Adapter|为Item创建视图|
|ViewHolder|承载Item的布局|
|ItemAnimator|负责处理数据添加或者删除时的动画效果|
|【Cache】|Recycler/RecycledViewPool/ViewCacheExtension|

### 观察者模式

3、RecyclerView的Observer(观察者)
> 1)-RV没有采用系统的Observer，而是用RecyclerViewDataObserver继承自自定义的AdapterDataObserver。
> 2)-AdapterDataObserver如下：
```java
    public abstract static class AdapterDataObserver {
        // 改变：范围内item
        public void onItemRangeChanged(int positionStart, int itemCount) {
        }
        // 插入：范围内item
        public void onItemRangeInserted(int positionStart, int itemCount) {
        }
        // 删除：范围内item
        public void onItemRangeRemoved(int positionStart, int itemCount) {
        }
        // 移动：范围内item
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
        }
    }
```
> 3)-RecyclerViewDataObserver: 继承并实现了AdapterDataObserver的所有方法。

4、RecyclerView的Observable(被观察者)
> 1. RecyclerView实际采用了系统的Observable。
> 2. AdapterDataObservable继承自Observable。
```java
    static class AdapterDataObservable extends Observable<RecyclerView.AdapterDataObserver> {
        // 获取到【观察者】，并且调用其onChanged方法
        public void notifyChanged() {
            for(int i = this.mObservers.size() - 1; i >= 0; --i) {
                ((RecyclerView.AdapterDataObserver)this.mObservers.get(i)).onChanged();
            }

        }
        // 通知观察者，范围刷新Item
        public void notifyItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            for(int i = this.mObservers.size() - 1; i >= 0; --i) {
                ((RecyclerView.AdapterDataObserver)this.mObservers.get(i)).onItemRangeChanged(positionStart, itemCount, payload);
            }

        }
        // 通知观察者，范围插入Item
        public void notifyItemRangeInserted(int positionStart, int itemCount) {
            for(int i = this.mObservers.size() - 1; i >= 0; --i) {
                ((RecyclerView.AdapterDataObserver)this.mObservers.get(i)).onItemRangeInserted(positionStart, itemCount);
            }

        }
        // 通知观察者，范围删除Item
        public void notifyItemRangeRemoved(int positionStart, int itemCount) {
            for(int i = this.mObservers.size() - 1; i >= 0; --i) {
                ((RecyclerView.AdapterDataObserver)this.mObservers.get(i)).onItemRangeRemoved(positionStart, itemCount);
            }

        }
        // 通知观察者，范围移动Item
        public void notifyItemMoved(int fromPosition, int toPosition) {
            for(int i = this.mObservers.size() - 1; i >= 0; --i) {
                ((RecyclerView.AdapterDataObserver)this.mObservers.get(i)).onItemRangeMoved(fromPosition, toPosition, 1);
            }
        }
    }
```

### 缓存类

5、RV的内部类Recycler是什么？有什么作用？
> 1.  Recylcer用于管理"Scrpped View"和"Detached View"
> 1. Scrapped View: 废弃的View，也就是仍然和RV连接着的，但是已经被标记为“删除的”或者“重用的”
> 2. Detached View: 已经移除的View。
```java
    /**
     * Recylcer用于管理"Scrpped View"和"Detached item"
     */
    public final class Recycler {
        // 1、连接着RV的所有的Scraped View
        final ArrayList<RecyclerView.ViewHolder> mAttachedScrap = new ArrayList();
        // 2、数据源更改过的Attached View(连接着RV的View)
        ArrayList<RecyclerView.ViewHolder> mChangedScrap = null;
        // 3、缓存的所有View(包括可见和不可见的ViewHolder)
        final ArrayList<RecyclerView.ViewHolder> mCachedViews = new ArrayList();

        private final List<RecyclerView.ViewHolder> mUnmodifiableAttachedScrap;
        // 4、RecycledViewPool：ViewHolder的缓存池，如果多个RV之间用setRecycledViewPool设置同一个RecycledViewPool，他们就可以共享Item。
        RecyclerView.RecycledViewPool mRecyclerPool;
        /**=======================================
         * 5、缓存的扩展，可以对指定的position跟Type缓存
         *  * 通过实现方法getViewForPositionAndType()来实现自己的缓存。
         *=====================================================*/
        private RecyclerView.ViewCacheExtension mViewCacheExtension;

        static final int DEFAULT_CACHE_SIZE = 2; //默认缓存数量
        private int mRequestedCacheMax = DEFAULT_CACHE_SIZE; //设置的最大缓存数量，默认为2。
        int mViewCacheMax = DEFAULT_CACHE_SIZE; //View的缓存的最大数量，默认为2。
    }
```

6、RV的缓存相对于ListView缓存的创新之处？
> 1. `RV`给ViewHolder增加了一个`UpdateOp`标志。
> 1. 通过`UpdateOp`标志可以进行定向刷新指定的Item，并通过`Payload`参数对`Item`进行局部刷新。
> 1. 如果数据源经常变动，RecyclerView是最好的选择。

#### 初始化

7、RecycledViewPool的作用
> 1. RecycledViewPool类是用来缓存Item用，是一个ViewHolder的缓存池
> 1. 如果多个RecyclerView之间用setRecycledViewPool(RecycledViewPool)设置同一个RecycledViewPool，他们就可以共享Item。
> 1. RecycledViewPool的内部维护了一个Map，里面以不同的viewType为Key存储了各自对应的ViewHolder集合。
> 1. 可以通过提供的方法来修改内部缓存的Viewholder。

8、Recycler会对RecylcedViewPool初始化
```java
    void setRecycledViewPool(RecyclerView.RecycledViewPool pool) {
        if (this.mRecyclerPool != null) {
            this.mRecyclerPool.detach();
        }

        this.mRecyclerPool = pool;
        if (this.mRecyclerPool != null && RecyclerView.this.getAdapter() != null) {
            this.mRecyclerPool.attach();
        }

    }
```
> 1. 如果原来有RecyclerPool则进行detach工作，然后使用新的pool

#### 存缓存

9、Recycler的recycleView()进行缓存
```java
    /**===============================================
     * 回收不可见的View：
     *   1. 采用LFU算法，最少使用策略，会去覆盖掉最少是用的缓存
     *   2. 会添加缓存到mCachedViews中(缓存的所有View---包括可见和不可见的ViewHolder)
     *   3. 特定的View回访金RecyclerViewPool
     *=============================================*/
    public void recycleView(View view) {
        //这边传递过来的是一个View，然后通过View获取ViewHolder
        ViewHolder holder = getChildViewHolderInt(view);
        xxx
        //开始缓存
        recycleViewHolderInternal(holder);
    }

    void recycleViewHolderInternal(ViewHolder holder) {
        // 1. 缓存的时候不能覆盖最近经常被使用到缓存
        if (!mPrefetchRegistry.lastPrefetchIncludedPosition(cachedPos)) {
            break;
        }

        // 2. 添加缓存
        mCachedViews.add(targetCacheIndex, holder);
        cached = true;
        // 3. 如果没有缓存的话就添加进RecycledViewPool
        if (!cached) {
            addViewHolderToRecycledViewPool(holder, true);
            recycled = true;
        }
    }
```

#### 取出缓存

10、RecyclerView的四级缓存
![RV缓存流程图](http://upload-images.jianshu.io/upload_images/1155837-e5365d4a8d217428.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![RecyclerView的四级缓存](http://mmbiz.qpic.cn/mmbiz_png/tnZGrhTk4desHkFxgnbyfgeoUgmoppufC6icrM5bKUMlvibopVTofN5qahSLwvibk3QDL48tib20PmcGB5EgKjjy9g/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1)

11、Recycler取出缓存
```java
    // 1、获取到Position上的View
    View getViewForPosition(int position, boolean dryRun) {
        return this.tryGetViewHolderForPositionByDeadline(position, dryRun, xxx).itemView;
    }

    @Nullable
    RecyclerView.ViewHolder tryGetViewHolderForPositionByDeadline(int position, boolean dryRun) {
        RecyclerView.ViewHolder holder = null;
        /**====================================
         * 1、如果是提前layout，会先从 mChangedScrap集合中取（数据源更改过的Attached View）
         *==================================*/
        if (RecyclerView.this.mState.isPreLayout()) {
            // mChangedScrap.get(offsetPosition)
            holder = this.getChangedScrapViewForPosition(position);
            xxx
        }

        /**====================================
         * 2、会先从 mAttachedScrap集合中取（所有Attached Scrapped View），再从mCachedViews中寻找(缓存的所有View：包括可见和不可见)。
         *==================================*/
        if (holder == null) {
            // mAttachedScrap.get(cacheSize)->mCachedViews.get(i)
            holder = this.getScrapOrHiddenOrCachedHolderForPosition(position, dryRun);
            xxxx
        }
        /**====================================
         * 3、会先从 mAttachedScrap集合中取，再从mCachedViews中寻找。
         *==================================*/
        if (holder == null) {
            // mAttachedScrap.get(cacheSize)->mCachedViews.get(i)
            holder = this.getScrapOrCachedViewForId(RecyclerView.this.mAdapter.getItemId(offsetPosition), type, dryRun);
        }

        /**====================================
         * 4、从mViewCacheExtension中获取
         *==================================*/
        if (holder == null && this.mViewCacheExtension != null) {
            View view = this.mViewCacheExtension.getViewForPositionAndType(this, position, type);
            if (view != null) {
                holder = RecyclerView.this.getChildViewHolder(view);
            }
        }
        /**====================================
         * 5、从RecycledViewPool中获取ViewHolder
         *==================================*/
        if (holder == null) {
            holder = this.getRecycledViewPool().getRecycledView(type);
        }

        /**=======================================================
         * 6、均获取失败，会先create ViewHolder，再bind ViewHolder
         *===================================================*/
        if (holder == null) {
            // 调用RV的Adapter的方法创建ViewHolder
            holder = RecyclerView.this.mAdapter.createViewHolder(RecyclerView.this, type);
            // 绑定ViewHolder： RecyclerView.this.mAdapter.bindViewHolder(holder, offsetPosition);
            this.tryBindViewHolderByDeadline(holder, type, position, deadlineNs);
        }
        // 设置布局参数
        android.view.ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        RecyclerView.LayoutParams rvLayoutParams = generateLayoutParams(lp);

        return holder;
    }
```

#### 清除缓存
12、Recycler清除所有缓存
```java
    // RecyclerView.java内部类：Recycler
    public void clear() {
        // 1、mAttachedScrap(所有Attached Scrapped View)都清除
        this.mAttachedScrap.clear();
        // 2、mCachedViews(缓存的所有可见和不可见View)全部清除
        this.recycleAndClearCachedViews();
    }
    void recycleAndClearCachedViews() {
        // 3、清除mCachedViews
        this.mCachedViews.clear();
        xxx
    }
```

### 定向刷新

13、AdapterHelper的作用
> 1. 用于帮助Adapter更新数组
> 1. 类似于ChildHelper帮助LayoutManager进行布局

14、RecyclerView的定向刷新
> 调用`mAdapter.notifyItemChanged(int position);`进行定向刷新
```java
    // RecyclerView.Adapter
    public final void notifyItemChanged(int position) {
        // 被观察者调用方法
        this.mObservable.notifyItemRangeChanged(position, 1);
    }
    // RecyclerView.AdapterDataObservable
    public void notifyItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
        for(int i = this.mObservers.size() - 1; i >= 0; --i) {
            // 通知观察者进行定向刷新
            ((RecyclerView.AdapterDataObserver)this.mObservers.get(i)).onItemRangeChanged(positionStart, itemCount, payload);
        }
    }
    // RecyclerView.AdapterDataObserver
    public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
        this.onItemRangeChanged(positionStart, itemCount);
    }

    // RecyclerView.RecyclerViewDataObserver
    public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
        RecyclerView.this.assertNotInLayoutOrScroll((String)null);
        if (RecyclerView.this.mAdapterHelper.onItemRangeChanged(positionStart, itemCount, payload)) {
            this.triggerUpdateProcessor();
        }
    }

    // AdapterHelper.java
    boolean onItemRangeChanged(int positionStart, int itemCount, Object payload) {
        if (itemCount < 1) {
            return false;
        } else {
            // 将需要更新的Item的范围记录下来，并且添加更新标识。
            this.mPendingUpdates.add(this.obtainUpdateOp(UpdateOp.UPDATE, positionStart, itemCount, payload));
            this.mExistingUpdateTypes |= UpdateOp.UPDATE;
            return this.mPendingUpdates.size() == 1;
        }
    }
```

1、RecyclerView如何复用Item

1、RecyclerView如何定向刷新Item

1、RecyclerView如何实现局部刷新Item

1、DiffUtil: 如何找到新旧集合的差别

## RecyclerView和ListView对比

1、RecyclerView和ListView的缓存层级不同
>1. ListView是两层
> 1. RecyclerView是四层
> 1. RecyclerView支持多个离屏ItemView缓存
> 1. RecyclerView支持开发者自定义缓存处理逻辑
> 1. RecyclerView支持所有RV公用同一个RecycklerViewPool(缓存池)


## 参考资料
1. [ListView的getItemViewType和getViewTypeCount](https://www.cnblogs.com/RGogoing/p/5872217.html)
1. [Android ListView工作原理完全解析，带你从源码的角度彻底理解](https://blog.csdn.net/guolin_blog/article/details/44996879)
1. [android列表View,ListView源码分析](https://www.jianshu.com/p/1e8fa9260637)
1. [深入理解Android中的缓存机制(二)RecyclerView跟ListView缓存机制对比](https://juejin.im/post/5a7569676fb9a063435eaf4c)
1. [关于Recyclerview的缓存机制的理解](https://zhooker.github.io/2017/08/14/%E5%85%B3%E4%BA%8ERecyclerview%E7%9A%84%E7%BC%93%E5%AD%98%E6%9C%BA%E5%88%B6%E7%9A%84%E7%90%86%E8%A7%A3/)
