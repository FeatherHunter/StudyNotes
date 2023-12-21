转载请注明链接: https://blog.csdn.net/feather_wch/article/details/88670948

# gradlew 查看依赖的层级

版本号:2019-03-19(19:30)

---

@[toc]
## gradlew
使用gradlew在Android Studio的Terminal中输入指令
```
gradlew mobile:iptvclient:dependencies --configuration compile
```
> 1. `--configuration compile`只显示出编译期间的依赖


显示的层级如下:
```
+--- com.android.support:recyclerview-v7:26.1.0
|    +--- com.android.support:support-annotations:26.1.0
|    +--- com.android.support:support-compat:26.1.0 (*)
|    \--- com.android.support:support-core-ui:26.1.0 (*)
+--- com.android.support:multidex:1.0.1
+--- com.android.support.constraint:constraint-layout:1.0.2
|    \--- com.android.support.constraint:constraint-layout-solver:1.0.2
+--- com.google.firebase:firebase-core:9.0.2
|    \--- com.google.firebase:firebase-analytics:9.0.2 (*)
+--- com.android.databinding:library:1.3.1
|    +--- com.android.support:support-v4:21.0.3 -> 26.1.0 (*)
|    \--- com.android.databinding:baseLibrary:2.3.0-dev -> 2.3.0
+--- com.android.databinding:baseLibrary:2.3.0
\--- com.android.databinding:adapters:1.3.1
     +--- com.android.databinding:library:1.3 -> 1.3.1 (*)
     \--- com.android.databinding:baseLibrary:2.3.0-dev -> 2.3.0

```
