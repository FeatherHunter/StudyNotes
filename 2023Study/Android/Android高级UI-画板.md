本文链接：转载请注明
https://blog.csdn.net/feather_wch/article/details/131487012

云笔记链接：https://note.youdao.com/s/bnKaAKKq


事件分发采用的设计模式：责任链模式

dispatchTouchEvent(MotionEvent ev);
-->if(actionMasked == MotionEvent.ACTION_DOWN) // 触发ACTION_DOWN做好所有重置工作
  -->cancelAndClearTouchTargets(ev);// 重置状态
    -->dispatchTransformedTouchEvent(event, true, target.child, target.pointerIdBits); // 执行取消事件
    -->clearTouchTargets(); //清除TouchTarget
  -->resetTouchState();//
    -->mGroupFlags &= ~FLAG_DISALLOW_INTERCEPT; // 重置GroupFlags
-->intercepted = xxx 检测是否拦截--父容器权利
  -->disallowIntercept // 判断是否允许拦截
    -->onInterceptTouchEvent(ev)
-->intercepted = false 不拦截可以分发
  -->int idBitsToAssign = ev.getPointerId(actionIndex); // 手指ID，后面会给TouchTarget
  -->preorderedList = buildTouchDispatchChildList() // 子View排序，决定处理优先级
    -->nextChild.getZ(); // 遍历判断Z值，xml悬浮按钮有Z值需要设置，默认0
    -->后面的在最上面，放在集合尾部
  -->for(int i = childrenCount - 1; i >= 0; i--) // 倒序遍历，xml写在后面的先处理
    -->是否可以处理点击事件：view可见||Animation不为空||x,y在控件范围内
    -->getTouchTarget(child) 判断是单指操作还是多指操作
    -->if(dispatchTransformedTouchEvent(ev, false, child, idBitsToAssign)) // 询问child是否处理事件
      -->child=null,super.dispatchTouchEvent(transformedEvent);
      -->child!=null,child.dispatchTouchEvent(transformedEvent); //child是容器ViewGroup.dispatchTouchEvent;child是View View.dispatchTouchEvent
      -->已经处理，不再给其他child，退出循环
-->dispatchTransformedTouchEvent(ev, canceled, null,TouchTarget.ALL_POINTER_IDS); // 询问自己是否处理
  -->super.dispatchTouchEvent(event); View#dispatchTouchEvent()

流程总结：
1. 重置工作
2. 拦截判断
3. 事件分发：按处理优先级排序childView，分发dispatchTouchEvent
4. 询问自己是否处理

## 疑问和Tips:
1. 多指还是单指，ACTION_DOWN触发一次-第二个手指ACTION_POINT_DOWN(2~n根)、ACTION_POINT_UP(n~2根)。移动都是ACTION_MOVE
2. 存在残障人士的辅助类，可以实现智能聊天
3. 哪个手指MOVE怎么判断？有id判断
4. ACTION_HOVER_MOVE是什么？鼠标
5. Android手机最多识别多少根手指？> 位运算，1位表示一个手指，int = 32位，32个手指
6. TouchTarget是什么？为什么是链表？保存处理DOWN事件的View，后续不再判断。链表结构是考虑到多指操作的情况
7. ViewGroup如果处理事件，会调用ViewGroup.dispatchTouchEvent+View.dispatchTouchEvent.View处理事件只会调用dispatchTouchEvent
8. 询问子View是否处理事件，在ACTION_DOWN时才询问
9. 为什么 ACTION_DOWN 处理的事件， ACTION_MOVE 自动处理？因为DOWN确定人选后用 TouchTarget 保存了ChildView，下次进入不再判断

## 滑动冲突

场景实战：ViewPager + ListView
1. 父容器onInterceptTouchEvent = true, 左右滑动可以，上下滑动不可以
2. 父容器onInterceptTouchEvent = false，左右滑动不可以，上下滑动可以
3. 父容器onInterceptTouchEvent = false，ListView重写dispatchTouchEvent = false, 左右滑动可以，上下滑动不可以
> ViewPager的所有孩子都不处理事件->因此自己处理事件

如何做到左右可以滑动，上下可以滑动？
1. 两个View德甲在一起，冲突是必然的（不冲突的是google处理了）
2. 左右时ViewPager拦截，上下时不拦截给ListView处理

同向的控件如何处理？
> 根据条件决定给谁处理

### 拦截法

1、内部拦截法
1. 子View根据条件决定事件让谁处理
2. requestDisallowInterceptTouchEvent() DOWN:true 自己不想处理想要父处理时: false
3. requestDisallowInterceptTouchEvent()内部mGroupFlags - 决定 disallowIntercept，= false(可以尝试拦截)才会执行 onInterceptTouchEvent。
```java
子View:
public boolean onTouchEvent(MotionEvent event) {
    switch (event.getAction()){
        case MotionEvent.ACTION_DOWN:
            getParent().requestDisallowInterceptTouchEvent(false);//不要拦截
            break;
        case MotionEvent.ACTION_MOVE:
            getParent().requestDisallowInterceptTouchEvent(true);//根据条件判断，进行拦截
            break;
    }
    return super.onTouchEvent(event);
}

父容器:
public boolean onInterceptTouchEvent(MotionEvent ev) {
    // ACTION_DOWN时有重置操作，并且不能拦截DOWN事件，ChildView还需要接收到DOWN去 requestDisallowInterceptTouchEvent
    if(ev.getAction() == MotionEvent.ACTION_DOWN){
        super.onInterceptTouchEvent(ev);
        return false;
    }
    return true; // 拦截，此时是childView允许的
}
```

2、外部拦截法
1. 父View决定是否拦截 onInterceptTouchEvent
