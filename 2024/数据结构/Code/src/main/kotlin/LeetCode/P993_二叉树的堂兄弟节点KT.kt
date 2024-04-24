package LeetCode

import java.util.*
import kotlin.collections.ArrayDeque

class P993_二叉树的堂兄弟节点KT {
    fun isCousins(root: TreeNode?, x: Int, y: Int): Boolean {
        if(x == y) return false
        val queue = ArrayDeque<TreeNode>()
        queue.addFirst(root!!)
        while (queue.isNotEmpty()){
            val size = queue.size
            var fx:TreeNode? = null
            var fy:TreeNode? = null
            for(i in 0 until size){
                val cur = queue.removeLast()
                cur.left?.let{
                    queue.addFirst(it)
                    if(it.`val` == x){
                        fx = cur
                    }else if(it.`val` == y){
                        fy = cur
                    }
                }
                cur.right?.let{
                    queue.addFirst(it)
                    if(it.`val` == x){
                        fx = cur
                    }else if(it.`val` == y){
                        fy = cur
                    }
                }
            }
            if((fx == null && fy != null) || (fx != null && fy == null)){
                return false
            }else if(fx == null && fy == null){
                continue
            }else{
                if(fx != fy){
                    return true
                }else{
                    return false
                }
            }
        }
        // 到死都没找到？或者root为空？
        return false
    }
}