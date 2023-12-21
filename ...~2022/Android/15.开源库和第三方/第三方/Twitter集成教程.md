转载请注明连接：http://blog.csdn.net/feather_wch/article/details/79612355

根据公司需求需要在`Android`集成`twitter`登录功能。本文会记录集成过程中的每个细节和遇到的问题。

主要会包括三部分内容：
1. 注册开发者账号
2. 集成Android的twitter功能
3. 登陆功能的流程和代码

#Android Twitter集成教程
版本:2018/3/19-1(15:25)

[TOC]
---

##1-注册账号并创建自己的App
>Tips: 需要翻墙哦！
###1、创建Twitter账号
1. [Twitter官网](https://twitter.com)
2. 选择`sign up`进行登录
3. 按提示输入账号、密码等，并进行手机验证。比较简单不过多介绍了。

###2、登陆开发者网站
1. [Twitter创建App的网站](https://apps.twitter.com)
2. 登陆后选择`Create New App`
![Create New App](https://images2017.cnblogs.com/blog/1019373/201801/1019373-20180106191528284-1574761264.png)
3. 创建一个应用
![Create an application](https://images2017.cnblogs.com/blog/1019373/201801/1019373-20180106191619503-1336980226.png)
    >1. `Name`: 输入应用名
    >2. `Description`: 应用的描述
    >3. `Website`：app的官网，用户可以下载、使用或查找app的更多信息。该URL会用于源应用程序创建的`tweetes`并显示在面向用户的授权界面汇总。(`如果没有URL可以直接放一个占位符，以后再修改---我是直接填写的twiiter官网...`)
    >4. `Callback Url`一定要填写！！不填写会导致`登录功能无法使用`，我是直接填写`https://www.baidu.com`。

4. 创建好后会进入应用相关页面
![Application Info](https://images2017.cnblogs.com/blog/1019373/201801/1019373-20180106192254706-2136107722.png)

5. 进入`Keys and Access Tokens`,其中`Consumer Key(API Key)`和`Consumer Secret(API Secret)`是需要用到的！

##2-在Android项目中集成Twitter功能
1. 在build.gradle中添加twitter相关依赖
```xml
//project的build.gradle添加jcenter
repositories {
    jcenter()
}
//app的build.gradle添加twitter的依赖
dependencies {
    ...
    compile 'com.twitter.sdk.android:twitter-core:3.1.1'
}
```

2.资源文件`strings.xml`中添加`API KEY`(第一部分第五点获取的两个内容)
```xml
<resources>
  <string name="com.twitter.sdk.android.CONSUMER_KEY">XXXXXXXXXXX</string>
  <string name="com.twitter.sdk.android.CONSUMER_SECRET">XXXXXXXXXXX</string>
</resources>
```

3. 在自定义的Application的onCreate()中进行初始化
>需要在`TwitterAuthConfig`中设定`API KEY和API Secret`
```java
Twitter.initialize(this);
TwitterConfig config = new TwitterConfig.Builder(this)
        .logger(new DefaultLogger(Log.DEBUG))
        .twitterAuthConfig(new TwitterAuthConfig("CONSUMER_KEY的字符串", "CONSUMER_SECRET的字符串"))
        .debug(true)
        .build();
Twitter.initialize(config);
```

##3-Twitter登陆功能

1. TwitterLoginButton的布局
```xml
<com.twitter.sdk.android.core.identity.TwitterLoginButton
        android:id="@+id/twitter_login_button"
        android:layout_width="match_parent"
        android:layout_height="40dp"
        />
```

2. TwitterLoginButton设置监听
```java
TwitterLoginButton mTwitterLoginButton = findViewById(R.id.twitter_login_button);
mTwitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                /**====================================================
                 * 1. result包含了用户的信息(token，tokenSecret等)
                 * 2. 如果有自己的后台服务器，发送这两个到后台，后台再去验证
                 *=====================================================*/
                TwitterAuthToken authToken = result.data.getAuthToken();

                String token = authToken.token;
                String tokenSecret = authToken.secret;

                Toast.makeText(Main2Activity.this, "Twitter登陆成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(TwitterException exception) {

            }
        });
```

3. 在Activity或者Fragment中设置`onActivityResult`
```java
//Activity中使用Button时
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    mCallbancManager.onActivityResult(requestCode, resultCode, data);
    mTwitterLoginButton.onActivityResult(requestCode, resultCode, data);
    super.onActivityResult(requestCode, resultCode, data);
}
//Fragment中使用Button时，需要在其所属的Activity添加:
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    // Pass the activity result to the fragment, which will then pass the result to the login
    // button.
    Fragment fragment = getFragmentManager().findFragmentById(R.id.your_fragment_id);
    if (fragment != null) {
        fragment.onActivityResult(requestCode, resultCode, data);
    }
}
```

###自定义登陆Button: 将点击事件转交给Twitter
布局
```xml
<FrameLayout
        ...>

        <com.twitter.sdk.android.core.identity.TwitterLoginButton
            android:id="@+id/login_button"
            ...
            android:visibility="gone"/>

        <ImageView
            android:id="@+id/login_image"
            .../>
    </FrameLayout>
```
给自己的Button设置点击事件，最终交给TwitterButton去执行登陆操作：
```java
public void onClick(View view) {
    switch (view.getId()){case R.id.login_image:
            LoginButton.performClick();
            break;
        default:
            break;
    }
}
```
##参考资料
1. [android集成twitter登录](http://www.cnblogs.com/tangZH/p/8206569.html)
