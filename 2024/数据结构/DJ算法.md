## Dijkstra算法
1、Dijksatr配合链式前向星: 网络延迟路径
```kotlin
    /**=================================
     * Dijkstra算法：
     * 1、从起点开始进入Heap，弹出顶点，标记为visited
     * 2、从点的各个边开始处理，目标点为t
     *  如果distance[c] + w < distance[t] 则更新距离
     *      1. 目标点，已经进入heap，弹出，再加入heap
     *      2. 目标点不在heao，加入heap
     * 3、不断重复直至heap为空
     * 4、如果distance中有无穷大距离的点，代表不可达，无解
     *===================================*/
```
