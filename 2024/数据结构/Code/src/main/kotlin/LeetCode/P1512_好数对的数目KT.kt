package LeetCode

class P1512_好数对的数目KT {
    
    fun numIdenticalPairs(nums: IntArray): Int {
        var count = 0
        for (i in nums.indices){
            for (j in i+1 until nums.size){
                if(nums[i] == nums[j]){
                    count++
                }
            }
        }
        return count
    }
}