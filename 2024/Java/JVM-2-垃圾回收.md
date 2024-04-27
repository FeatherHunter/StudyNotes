
# 垃圾回收

## 分带划分

堆中分为：Eden（T2）、from、to、tenured（T1）【终生的，长期的】
新生代：Eden（T2）、from、to
老年代：tenured 存放多次垃圾回收没有回收的对象

15次`System.gc()`垃圾回收，会放入老年代

JHSDB工具：