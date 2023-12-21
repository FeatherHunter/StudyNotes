转载请注明链接：http://blog.csdn.net/feather_wch/article/details/78713144

本文详细介绍Android Studio的使用技巧和快捷方式，目标是提高开发效率。
1. 第一部分介绍使用技巧
2. 第二部分介绍快捷键

#Android Studio使用技巧和快捷方式汇总
版本：2018/4/11-1

##Andorid Studio使用技巧

###1-AS内置预览布局功能强化
开发中通过布局的预览是无法预览`ListView等列表`和`include标签`等各方面的效果。本质上`Android Studio`不仅可以进行预览，还能提供测试数据。

核心部分是使用`tools工具`：`xmlns:tools="http://schemas.android.com/tools"`。

还可以提供随机数据，例如随机名字和随机图片
```xml
<ImageView
    android:id="@+id/image"
    ...
    tools:src="@tools:sample/avatars"/>

<TextView
    ...
    tools:text="@tools:sample/full_names"/>
```



#### 参考资料
1. [Github-教程和实例](https://github.com/xiaweizi/AndroidToolsExample)

###2-AS自动生成给内部变量加上m
Google代码规范中，类的内部变量需要加上`m`，这可以让`AS`自动添加：

>1. As中选择`Setting`
>2. 选择`Editor`->`Code Style`->`Java`
>3. 选择`Code Generation`，给`Field`加上`m`
![AS自动补全内部变量](http://img.blog.csdn.net/20170607111158528?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveWVjaGFvYQ==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)

###3-Getter模板：自动进行null保护
作用: 在Getter中进行判空，防止接口等数据中返回null，避免因为疏忽没有判断null而导致崩溃。
####步骤
1. Alt+Insert 或者 代码区域->右击->Generate->Getter
2. “Getter”选择框中点击右上角“...”进入模板文件的选择
3. 新建模板文件
4. 输入新模板
```c
#if($field.modifierStatic)
static ##
#end
$field.type ##
#set($name = $StringUtil.capitalizeWithJavaBeanConvention($StringUtil.sanitizeJavaIdentifier($helper.getPropertyName($field, $project))))
#if ($field.boolean && $field.primitive)
  #if ($StringUtil.startsWithIgnoreCase($name, 'is'))
    #set($name = $StringUtil.decapitalize($name))
  #else
    is##
#end
#else
  get##
#end
${name}() {
  #if($field.string)
    return $field.name == null ? "" : $field.name;
  #elseif($field.list)
    if($field.name == null){
        return new ArrayList<>();
    }else{
        return $field.name;
    }
  #else
    return $field.name;
  #end
}

```

###4-格式化代码(Ctrl+Alt+L)：自动归类方法
作用: 将方法按照广度或者深度的形式进行归纳排序

####步骤
1. Ctrl+shfit+alt+l 进入配置页面
2. 勾选`Rearrange code`
3. File->Settings->Editor->Code Style->Java->Arrangement 进入代码排列配置页面
4. 勾选“Keep override methods together”：将重载方法放到一起(keep order-保持原有顺序)
5. 勾选“Keep dependent methods together”: 将一般方法放到一起(breadth-first order---广度排序)

使用： 全选代码(Ctrl+A)->Ctrl+Alt+L 完成代码排序


##Android Studio 快捷键
###1-最强快捷键技巧+gif配图(转载)
[最强Android Studio快捷键技巧+gif配图](http://www.open-open.com/lib/view/open1458715872710.html)
> 该链接内容翻译自 Android Studio Tips by Philippe Breault，一共收集了62个 Android Studio 使用小技巧和快捷键。 根据这些小技巧的使用场景，本文将这62个小技巧分为常用技巧（1 – 28）、编码技巧（29 – 49）和调试技巧（50 – 62），分成三个部分。

###2-《Android 神兵利器》第三章总结

|快捷键|作用|备注|
|---|---|---|
|Alt+Shift+C|工程最近改变的文件||
|Ctrl+Alt+Shift+N|打开任何特定的方法和成员变量(搜索关键词并且打开)||
|Shift+Shift|快速查找||
|Ctrl+Shift+A|查询指令/快捷键|
|Ctrl+空格|在HTML等文件，补全image file|这个快捷键没试过|
|Ctrl+E/Ctrl+Shift+E|最近的文件操作|
|Ctrl+Tab|各种文件、功能切换||
|Ctrl+Alt+左/右|最近浏览位置||
|行操作:||
|Ctrl+Shift+上/下|整行移动|
|Ctrl+Backspace|删除整行|
|Ctrl+X|剪切一行|
|Ctrl+D|复制当前行到下一行|
|查找调用：|右击方法>Find Usage|查找到该方法或ID哪里被调用|
|Ctrl+左击/B|进入方法|
|Ctrl+P|查找参数定义|
|断点：||
|如何进入断点|1.run(normal) 2.attach debugger to android process|
|临时断点|Ctrl+Alt+Shift+F8|只执行一次|
|条件断点|普通断点+右击+condition+Enable(开关断点)||
|异常断点|Run+View breakpotins+选Java Exception BreakPoints||
|日志断点|普通断点+右击+suspend为"false"+log evaluate expression|对完成的工程添加调试信息，会在断点处打印信息，不需要重新编译整个项目|
|Alt+右击(debug mode)|计算表达式信息|调试模式中|
|Alt+Ctrl+O|清楚无效包引用||
|Alt+拖动|多重选择|
|Alt+Shift+左击|多个光标|
|Alt+Shift+左击key|多选多个单词|
|Ctrl+Alt+Enter|快速完成|
|Ctrl+空格+TAB\ENTER|代码提示|Tab会清除后面的内容，Enter不会|
|Ctrl+Shift+空格|更丰富的代码提示|
|Ctrl+F12|打开大纲|便于了解结构|
|Ctrl+Alt+Shift+T|重构|Rename/Extrac+Method等等|
|Ctrl+Alt+T|surround with|给目标加上if、try catch等surround语句|
|多国语言|strings.xml+open editor(右上角)+add Locale(左上角地球)|可以建立多个国家的strings文件|
|重构的Extract|能够从xml中提取一些属性作为style给其他view复用，也可以抽取布局、变量、常量、参数等等|
|search structurally(Ctrl+Alt+A)|允许在不同文件中找到不同变量的方法等各种功能|需要在快捷指令中搜索|
|Ctrl+J|内置模板|配置方法：设置中的Live Templates有所有模板，也可以自己设置|
|如：list.for|后缀模板|会在后面提供模板，选择即可。list.cast是提供转换模板|
|文件、类注释|File and Code templates|可以在files、include中自定义模板|
|Ctrl+Alt+H|方法调用的栈|可以清晰看到方法被调用的地方和顺序|


 *  Activity左侧图标可以打开布局
