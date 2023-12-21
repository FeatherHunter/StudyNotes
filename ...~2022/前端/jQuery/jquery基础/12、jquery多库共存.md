1、多库共存
> 1.存在两个库都使用了`$`符号，就存在冲突；
> 2.解决：jquery库可以释放`$`的使用，让另一个库正常使用，此时的jquery库只能使用jQuery了；
> 3.jquery库释放`$`符：`jQuery.noConflict();`
