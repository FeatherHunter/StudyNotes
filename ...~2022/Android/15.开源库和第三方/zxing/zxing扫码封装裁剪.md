转载请注明链接:

# zxing扫码功能封装

版本号:2018/

---

[TOC]

---

## zxing基本介绍

1、为什么要选择zxing？
> 1. google的开源项目，高度可定制库
> 1. 可以识别多种码，不仅仅是二维码
> 1. 不依赖其他第三方库，使用简单。

## zxing集成

2、zxing集成步骤
> 1. 进入github：https://github.com/zxing/zxing
> 1. 将项目clone到本地，将android文件夹作为module加载入项目中。(android这个文件夹就是demo)
> 1. 在该module中的build.gradle中添加zxing的依赖项：
```xml
dependencies{
    api 'com.google.zxing:android-core:3.3.0'
    api 'com.google.zxing:core:3.3.2'
}
```
> 1. 需要添加Camera权限(Android 6.0)的动态权限: [动态权限教程](https://blog.csdn.net/feather_wch/article/details/81483960)


## 功能开发
### zxing重要类

3、zxing android demo中有哪些重要的类？
> 1. CaptureActivity: 扫码页面
> 1. CameraManager
>      1. 包含所有预览、闪光灯的功能
>      1. 内部会调用FlashLighManager的功能
> 1. DecodeThread
>      1. 解码是耗时操作, 需要到工作线程中进行。
> 1. DecideHandler
>      1. 解码完成后，需要转发到UI线程中
> 1. ViewfinderView
>      1. 自定义扫码框的UI

### 自定义扫码页面

4、自定义扫码页面，就是自定义ViewfinderView的onDraw()方法
```java
  public void onDraw(Canvas canvas) {
    if (cameraManager == null) {
      return; // not ready yet, early draw before done configuring
    }
    Rect frame = cameraManager.getFramingRect();
    Rect previewFrame = cameraManager.getFramingRectInPreview();
    if (frame == null || previewFrame == null) {
      return;
    }
    int width = canvas.getWidth();
    int height = canvas.getHeight();

    // Draw the exterior (i.e. outside the framing rect) darkened
    paint.setColor(resultBitmap != null ? resultColor : maskColor);
    canvas.drawRect(0, 0, width, frame.top, paint);
    canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
    canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
    canvas.drawRect(0, frame.bottom + 1, width, height, paint);

    //xxx
  }
```


### 闪光灯开启

5、利用CameraManager的API进行开关
```java
              if(isFlashOpen){
                  // 开启状态，进行关闭。
                  isFlashOpen = !isFlashOpen;
                  cameraManager.setTorch(!isFlashOpen);
              }else{
                  // 关闭状态，需要开启
                  isFlashOpen = !isFlashOpen;
                  cameraManager.setTorch(!isFlashOpen);
              }
```

### 扫描相册中二维码图片


6、扫描相册中的二维码图片
> 1-打开手机相册
```java
              // 扫描手机中二维码图片
              Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
              intent.setType("image/*");
              Intent wrapIntent = Intent.createChooser(intent, "选择二维码图片");
              startActivityForResult(wrapIntent, ALBUM_REQUEST_CODE);
```
> 2-onActivityResult处理返回的图片
```java
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent intent) {
      if(resultCode == RESULT_OK){
          if(requestCode == ALBUM_REQUEST_CODE){
              // 处理相册中的图片
              handleAlbumPic(intent);
          }
      }
  }
```
> 3-处理图片的逻辑
```java
    /**======================================
     * 处理选择的图片
     * @param data 包含图片Uri的Intent
     *======================================*/
    private void handleAlbumPic(Intent data) {
        //获取选中图片的路径
        final String photoPath = UriUtils.getRealPathFromUri(CaptureActivity.this, data.getData());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Result result = scanningImage(photoPath);
                if (result != null) {
                    // 处理扫码结果
                    String resultCode = result.getText();
                    Intent intent = new Intent();
                    // 扫描结果
                    intent.putExtra("result", resultCode);
                    //
                    CaptureActivity.this.setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(CaptureActivity.this, "识别失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**======================================
     * 扫描二维码图片中的二维码
     * @param path 图片路径
     * @return Result结果
     *==========================================*/
    public Result scanningImage(String path) {
        if(TextUtils.isEmpty(path)){
            return null;
        }
        Hashtable<DecodeHintType, String> hints = new Hashtable<>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8"); //设置二维码内容的编码

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        mScanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小
        int sampleSize = (int) (options.outHeight / (float) 200);
        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        // 扫描到的图片
        mScanBitmap = BitmapFactory.decodeFile(path, options);

        /**
         *  bitmap对象转换成二进制图片
         */
        // 1、获取到pixels数组
        int[] data = new int[mScanBitmap.getWidth() * mScanBitmap.getHeight()];
        mScanBitmap.getPixels(data, 0, mScanBitmap.getWidth(), 0, 0, mScanBitmap.getWidth(), mScanBitmap.getHeight());
        // 2、获取到RGBLuminanceSource
        RGBLuminanceSource source = new RGBLuminanceSource(mScanBitmap.getWidth(),mScanBitmap.getHeight(),data);

        // 二进制图片转换成bitmap对象(说明:创建HybridBinarizer对象,需要传入LuminanceSource,所以传入source(二进制的图片),并且通过BinaryBitmap转换成bitmap对象)
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            return reader.decode(bitmap1, hints);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return null;
    }
```
> 4-工具类
```java
/**========================================
 * Uri路径工具
 *========================================*/
public class UriUtils {
    /**
     * 根据图片的Uri获取图片的绝对路径(适配多种API)
     *
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    public static String getRealPathFromUri(Context context, Uri uri) {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion < 11) return getRealPathFromUri_BelowApi11(context, uri);
        if (sdkVersion < 19) return getRealPathFromUri_Api11To18(context, uri);
        else return getRealPathFromUri_AboveApi19(context, uri);
    }

    /**
     * 适配api19以上,根据uri获取图片的绝对路径
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String getRealPathFromUri_AboveApi19(Context context, Uri uri) {
        String filePath = null;
        String wholeID = DocumentsContract.getDocumentId(uri);

        // 使用':'分割
        String[] ids = wholeID.split(":");
        String id = null;
        if (ids == null) {
            return null;
        }
        if (ids.length > 1) {
            id = ids[1];
        } else {
            id = ids[0];
        }

        String[] projection = {MediaStore.Images.Media.DATA};
        String selection = MediaStore.Images.Media._ID + "=?";
        String[] selectionArgs = {id};

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,//
                projection, selection, selectionArgs, null);
        int columnIndex = cursor.getColumnIndex(projection[0]);
        if (cursor.moveToFirst()) filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }

    /**
     * 适配api11-api18,根据uri获取图片的绝对路径
     */
    private static String getRealPathFromUri_Api11To18(Context context, Uri uri) {
        String filePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(context, uri, projection, null, null, null);
        Cursor cursor = loader.loadInBackground();

        if (cursor != null) {
            cursor.moveToFirst();
            filePath = cursor.getString(cursor.getColumnIndex(projection[0]));
            cursor.close();
        }
        return filePath;
    }

    /**
     * 适配api11以下(不包括api11),根据uri获取图片的绝对路径
     */
    private static String getRealPathFromUri_BelowApi11(Context context, Uri uri) {
        String filePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            filePath = cursor.getString(cursor.getColumnIndex(projection[0]));
            cursor.close();
        }
        return filePath;
    }
}
```

## 错误

### Manifest merger failed with multiple errors, see logs

**错误提示**:
```
Manifest merger failed with multiple errors, see logs
```

**原因分析**:
需要打印详细日志，在AS的终端上输入：
```
gradlew processDebugManifest --stacktrace
```
实际错误原因: module的version版本和整个app的版本不符合。
```
AndroidManifest.xml:19:5-30 Error:
        Attribute manifest@versionCode value=(108) from AndroidManifest.xml:19:5-30
        is also present at AndroidManifest.xml:19:5-30 value=(1).
        Attributes of <manifest> elements are not merged.
```

**解决办法**:
修改versionCode和versionName

## 知识储备

1. Intent.createChooser(xxx)的作用是什么?

## 问题汇总

## 参考资料
1. [android zxing简单集成](https://www.jianshu.com/p/a4ba10da4231)
