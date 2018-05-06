Twitter登陆功能时遇到故障：
>Desktop applications only support the oauth_callback value
...
>E/Twitter: Failed to get request token  com.twitter.sdk.android.core.TwitterApiException: HTTP request failed, Status: 400
>...
>E/Twitter: Authorization completed with an error com.twitter.sdk.android.core.TwitterAuthException: Failed to get request token

转载请注明链接：http://blog.csdn.net/feather_wch/article/details/79612471

###原因分析
可能1： 没有在`Twitter`自己的应用中设置`Callback Url`，这是用于登陆成功后的回调的，没有的话设置个合法的URL即可，我设置为`https://www.baidu.com/`

可能2：没有正确设置API KEY和API Secret
解决办法：[Twitter集成教程](http://blog.csdn.net/feather_wch/article/details/79612355)中第1-2的第三点：需要设置`Callback Url`

###解决
1. 登陆[https://apps.twitter.com](https://apps.twitter.com)
2. 进入自己的APP并进入`setting`选项。
3. 设置`Callback Url`
