1、如果我们想要通过Matrix去对图片进行缩放，要注意Matrix的所有操作都是在原来基础上叠加的：
```java
//循环count次，并且每次放大everyScale的倍数---画面渐渐放大
for(int i = 0; i < count; i++){
    mScaleX += everyScale;
    mScaleY += everyScale;
    //1. 如果不进行“reset”，会导致= Matrix * 1.1 * 1.1...最终放大角度越来越大。
    mCurrentMatrix.reset();
    mCurrentMatrix.postScale(mScaleX, mScaleY, getWidth() / 2f, getHeight() / 2f);
    //2. 放大图片等操作，一定要reset才能保持每次放大到第"mScaleX倍"，而不是成倍放大
    try {
        Thread.sleep(20);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}
```
