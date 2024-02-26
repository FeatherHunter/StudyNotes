package LeetCode

class TreeNode(var `val`: Int) {
    var left: TreeNode? = null
    var right: TreeNode? = null
}

/**
 * [3,
 * 3,   2,
 * 0,0, 3,       2,
 *      1,3,    1,1,
 *        2, 1
 *        1,1
 */
class P2331_计算布尔二叉树的值KT {
    fun evaluateTree(root: TreeNode?): Boolean {
        return dfs(root).state
    }

    fun dfs(node: TreeNode?):Info{
        if(node?.`val` == 0){
            return Info(false)
        }
        if(node?.`val` == 1){
            return Info(true)
        }
        val l = dfs(node?.left)
        val r = dfs(node?.right)
        if(node?.`val` == 2){
            return Info(l.state or r.state)
        }
        println(node?.`val`)
        return Info(l.state and r.state)
    }

    class Info(val state: Boolean)
}