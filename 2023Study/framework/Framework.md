
# Framework

## AMS
```
ActivityStackSupervisor
-ActivityStack:栈 内部有ArrayList<ActivityRecord>
--Task
---ActivityRecord
---ActivityRecord
---ActivityRecord
ProcessRecord
-ActivityRecord?
-ActivityRecord?
-...
```
AMS中：
1. Activity 对应于 ActivityRecord
2. Application 对应于 ProcessRecord
## WMS
1、WindowState是什么？内部有什么？
WindowState
-mBaseLayer
-mSubLayer
2、Window内部的层级关系
Window
-Surface
--Canvas
3、ViewRootImpl
-mWindow：用于给WMS取调用自己
4、mWIndowSession.addToDisplayAsUser做哪些事情？
1. mWindow加入到Display中
2. mInputChannel：准备好接收事件
3. 但没有relayout，还没有有效的Surface

## SurfaceFlinger
### 三线程
```
SurfaceFlinger 主线程：---acquireBuffer---acquireBuffer---releaseBuffer
SurfaceFlinger Binder线程：---dequeueBuffer---queueBuffer---
应用渲染线程RenderThread：---dequeue---queue---
```
### Surface
Q：Surface方法JNI注册在哪里？
> app.main()->Runtime.start()->Reg注册JNI
#### SurfaceView
1、什么是SurfaceView？
1. 单独拥有画布的控件
2. 图像、摄影，在异步线程中处理，并于SurfaceView展示
2、如何使用
```java
GameUI extends SurfaceView import SurfaceView.callback{
    开一个线程：
    while(isDraw){
        drawUI()
    }
    public void drawUI(){
        Canvas canvas = holder.lock();
        // xxx
        holder.unlock(canvas)
    }
}
```
3、SurfaceView的帧数是多少？和Window的区别？
1. View Window 主线程 60hz
2. SurfaceView Surface 任务线程 控制帧数
4、SurfaceView为什么会有透明问题？
1. 创建时会像WMS请求创建，并且告诉Z-order来决定位置
2. 如SurfaceView在主Window之下，主Window会透明
```
setZOrderOnTop(true)置顶，多个时按照布局次序
setZOrderMediaOverlay(true)不会遮盖其他普通View
```
### DisplayID
1. SF中设置displayid可以决定在哪个Display对应的屏幕上显示
### HWC
1、HWC是什么？
1. hardware composer
2. Android中layer合成和显示的HAL模块
2、为什么要有Buffer？
1. 直接显示避免并发问题，需要同步等待
3、3 buffer避免janky frame
1. 避免cpu/gpu空转
4、硬件和软件之间有hence 同步信号，同频
