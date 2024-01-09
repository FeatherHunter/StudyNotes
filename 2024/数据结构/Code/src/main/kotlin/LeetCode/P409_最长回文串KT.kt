package LeetCode

class P409_最长回文串KT {
    fun longestPalindrome(s: String): Int {
        val map = HashMap<Char, Int>()
        var ans = 0
        for(c in s){
           var count = map[c] ?: 0
           when(count){
               0 -> count++
               1 -> {
                   ans += 2
                   count = 0
               }
           }
            map[c] = count
        }

        return if(s.length > ans) ans + 1 else ans
    }
}