package LeetCode

class P802_找到最终的安全状态KT {
    /**
     * graph = [[1,2],[2,3],[5],[0],[5],[],[]]
     * 0 入1 出2
     * 1 入1 出2
     * 2 入2 出1
     * 3 入1 出1
     * 4 入0 出1
     * 5 入1 出0
     * 6 入0 出0
     *
     *
     * 6 入0出0
     * 5 入0出2
     * 4 入1出0
     * 3 入1出1
     * 2 入1出2
     * 1 入2出1
     * 0 入2出1
     * 构造反向有向无环图，进入queue的就是答案
     *
     * next=1,i=0
     * next=2,i=0
     * next=2,i=1
     * next=3,i=1
     * next=5,i=2
     * next=0,i=3
     * next=5,i=4
     * [2, 2, 1, 1, 1, 0, 0]
     * [5, 6, 0, 0, 0, 0, 0]
     * [5, 6, 0, 0, 0, 0, 0]
     */
    fun eventualSafeNodes(graph: Array<IntArray>): List<Int> {
        val indegree = IntArray(graph.size)
        val regraph = MutableList<MutableList<Int>>(graph.size){
            mutableListOf()
        }
        for(i in graph.indices){
            for(j in graph[i].indices){
                val next = graph[i][j]
                regraph[next].add(i)
                indegree[i]++
            }
        }
        val queue = IntArray(graph.size)
        var l = 0
        var r = 0
        indegree.forEachIndexed { index, degree ->
            if(degree == 0){
                queue[r++] = index
            }
        }

        while (l < r){
            val cur = queue[l++]
            for (i in regraph[cur]) {
                if(--indegree[i] == 0){
                    queue[r++] = i
                }
            }
        }
        println(queue.sliceArray(l .. r-1).contentToString())
        return queue.sliceArray(l .. r-1).sortedArray().toMutableList()
    }
}