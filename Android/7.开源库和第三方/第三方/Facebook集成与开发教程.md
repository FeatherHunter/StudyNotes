关键字：Facebook | Android studio 3.0

Facebook离线包如何集成？公司的环境决定了无法通过仓库的方法进行集成，因此只能下载Facebook SDK进行集成。

本文讲解了Android Studio 3.0中离线集成Facebook SDK遇到的问题，并按步骤讲解Facebook登陆功能的使用方法。

转载请注明链接：http://blog.csdn.net/feather_wch/article/details/79585728

###Facebook SDK离线集成
####1、下载Facebook SDK包
1. 下载地址：[facebook-android-sdk-4.31.0.zip(密码:9pr2)](https://pan.baidu.com/s/1oEeyK7jiDJss8fgTkNcFVA)

2. 解压缩, 并将所有arr复制到`libs`目录下

####2、项目中集成需要功能的aar

1. 在项目的`build.gradle`中进行配置
```xml
compile(name: 'facebook', ext: 'aar')
compile(name: 'facebook-common', ext: 'aar')
compile(name: 'facebook-core', ext: 'aar')
compile(name: 'facebook-login', ext: 'aar')
```

2. 在项目中添加依赖`implementation 'com.android.support:cardview-v7:26.1.0'`-AS3.0之前的版本用`complie`即可。
>项目进行同步后，可能会出现找不到资源的问题`style/com_facebook_button`等等。这是因为缺少`cardview`的包。

###Facebook登录功能
[Facebook Login官方教程(需要翻墙)](https://developers.facebook.com/docs/facebook-login/android?sdk=fbsdk)
####1-注册Facebook账号并创建一个App应用
会获取到对应的`App Id`(官方教程中第二步)

####2-编辑`strings.xml`和`AndroidManifest.xml`
1、strings.xml中增加如下内容：
```xml
<string name="facebook_app_id">xxx你自己的APP IDxxx</string>
<string name="fb_login_protocol_scheme">fbxxx你自己的APP IDxxx</string>
```

2、`AndroidManifest.xml`中增加网络权限并添加`meta-data`和`FacebookActivity`等内容
```xml
<uses-permission android:name="android.permission.INTERNET"/>

<meta-data android:name="com.facebook.sdk.AppplicationId"
    android:value="@string/facebook_app_id"/>

<activity android:name="com.facebook.FacebookActivity"
    android:configChanges="keyboard|keyboardHidden|screenLayout|screenSize|orientation"
    android:label="@string/app_name"
    tools:replace="android:theme"
    android:theme="@android:style/Theme.Translucent.NoTitleBar"/>
```
>给FacebookActivity更换Theme可能会出现冲突，这是因为facebook中也有该标签，可以通过给`Activity`添加`tools:replace="android:theme"`就可以解决冲突。[冲突分析链接](http://blog.csdn.net/feather_wch/article/details/79581743)

3、提供环境和Realease的Key Hashs用于Facebook和自己app的身份认证(官方教程第六步)
Windows中生成秘钥教程：

4、自己Activity中添加LogiButton并进行处理
布局：
```xml
<com.facebook.login.widget.LoginButton
    android:id="@+id/facebook_login_button"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content" />
```
Activity:(一定要设置onActivityResult不然无法接收登陆后的返回信息)
```java
public class Main2Activity extends SupportActivity {

    LoginButton mLoginButton;
    //1. 注册回调用于处理登陆返回信息
    CallbackManager mCallbancManager = CallbackManager.Factory.create();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //2. SDK初始化
        FacebookSdk.setApplicationId("548759495495950");
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_main2);

        //3. 获取登陆按钮
        mLoginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        //4. 设置回调接口
        mLoginButton.registerCallback(mCallbancManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("Main2Activity", "onSuccess");
            }
            @Override
            public void onCancel() {
                Log.i("Main2Activity", "onCancel");
            }
            @Override
            public void onError(FacebookException e) {
                Log.i("Main2Activity", "onError");
            }
        });

    }

    //5. 需要处理FacebookActivity的返回信息，才能触发登陆成功的回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbancManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
```

##参考资料
1. [ Facebook Android SDK 4.14使用详解(2016.8)](http://blog.csdn.net/lvshaorong/article/details/52243171)
