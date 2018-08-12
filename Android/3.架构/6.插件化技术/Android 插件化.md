转载请注明链接：https://blog.csdn.net/feather_wch/article/details/81608967

# Android 插件化

修改版本：2018/8/12-1

---

## 插件化和组件化

1、什么是组件化？
>1. 组件化开发就是将一个app分成多个模块
>2. 每个模块都是一个组件（Module），开发的过程中这些组件相互依赖或者用于单独调试
>3. 最终发布的时候会将这些组件合并统一成一个apk。

2、什么是插件化？
>1. 插件化开发时将整个app拆分成很多模块。
>1. 这些模块包括一个宿主和多个插件，每个模块都是一个apk（组件化的每个模块是个lib）
>2. 最终打包的时候将宿主apk和插件apk分开或者联合打包。

![组件化和插件化](https://raw.githubusercontent.com/halibobo/BlogImage/master/blog/module/module_plug.png)

3、插件化的好处
>1. 宿主和插件分开编译: 各个模块封装成不同的插件APK，不同模块可以单独编译，提高了开发效率。
>1. 并发开发: 不同的团队负责不同的插件APP，这样分工更加明确。
>1. 动态更新插件: 可以通过上线新的插件来解决线上的BUG，达到“热修复”的效果。
>1. 按需下载模块
>1. 解决了方法数超过限制的问题。
>1. 减小了宿主APK的体积。

4、插件化框架
> 1. 360的RePlugin
> 1. 滴滴的VirtualAPK
> 1. 阿里巴巴的Atlas

![插件化和热修复的关系](https://upload-images.jianshu.io/upload_images/1115031-70a55fecac4da43b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/543)


5、热修复插件
> 1. Tinker(腾讯)
> 1. AndFix(阿里巴巴)
> 1. Robust(美团)

## 参考资料

1. [滴滴开源Android插件化框架VirtualAPK原理分析](https://blog.csdn.net/yyh352091626/article/details/74852390)
2. [Android 360开源全面插件化框架RePlugin 实战](https://blog.csdn.net/qiyei2009/article/details/78236520)
