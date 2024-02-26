package LeetCode

class P1688_比赛中的配对次数KT {
    fun numberOfMatches(n: Int): Int {
        var count = 0
        var teams = n
        while(teams> 1){
            if(teams % 2 == 0){
                // 偶数
                count+= teams / 2
                teams /= 2

            }else{
                count += (teams - 1) / 2
                teams = (teams - 1) / 2 + 1
            }
        }
        return count
    }
}