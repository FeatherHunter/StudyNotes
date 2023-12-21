# 执行引擎

### 字节码

1、下面字节码指令导致的效果是怎么样的？
|地址|指令|
|---|---|
|0|ICONST_1|
|1|ISTORE 0|
|2|ICONST_2|
|3|ISTORE 1|
|4|ILOAD 0|
|5|ILOAD 1|
|6|ADD|
|7|ISTORE 2|
|8|RETURN|

> 阐述在PC、操作数栈、局部变量表中是如何变换的