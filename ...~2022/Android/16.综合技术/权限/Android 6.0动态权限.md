Android 6.0以上动态权限申请的简单实例。
转载请注明链接：https://blog.csdn.net/feather_wch/article/details/81483960

# Android 6.0 动态权限

最后修改: 2018/8/7-1(23:00)

---

[TOC]

## 简介
自从Android 6.0开始，一些重要权限不仅要保留原来的静态注册，还需要进行额外的动态注册。

## 动态注册方法

### 在AndroidManifest中申请权限
```xml
<!-- 添加可监听电话状态的权限 -->
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
```

### 代码中进行动态申请
>必须要在Activity中，`Application`中无法处理`弹窗的需求`
```java
/**
 * 检查和申请权限
 */
public void requestPermission(){
    //判断是否已经赋予权限
    if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED
        || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED){

        //多个权限一起进行申请
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            }, 1);//RequestCode = 1，用于处理弹窗结果
    }else{
        Log.d(TAG, "Has Permission: READ_PHONE_STATE");
        /*============================================
         * 如果已经具有权限，就直接走接下来的流程。
         * * 该方法根据自己的业务逻辑去实现。
         *===================================*/
        doNextWork();
    }
}
```

### 处理动态申请权限的结果
> 1. 处理动态申请权限的结果.
> 2. 用户点击了拒绝或者同意后触发.
```java
@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    //requestCode 对应于`requestPermissions`的requestCode
    switch(requestCode){
        case 1:
            //获取到了权限（按顺序，对三个权限进行验证）
            if(grantResults.length > 2
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED
                    ){
                doNextWork();
            }else{
                //没有获取到权限，直接退出app。不然也没办法继续使用。
                Toast.makeText(this, "App must has permission!", Toast.LENGTH_SHORT).show();
                finish();
            }
            break;
        default:
            break;
    }
}
```
