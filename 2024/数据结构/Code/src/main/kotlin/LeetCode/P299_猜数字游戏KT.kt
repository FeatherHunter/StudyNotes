package LeetCode

class P299_猜数字游戏KT {
    fun getHint(secret: String, guess: String): String {
        val map = hashMapOf<Char,Int>()
        secret.forEach {
            map[it] = map[it]?.plus(1) ?: 1
        }
        var a = 0
        var b = 0
        // 找A
        for (i in secret.indices){
            if(secret[i] == guess[i]){
                a++
                map[secret[i]] = map[secret[i]]?.minus(1) ?: 0
            }
        }
        for (i in secret.indices){
            if(secret[i] != guess[i]){
                if(map.contains(guess[i]) && map.getOrDefault(guess[i], 0) > 0){
                    b++
                    map[guess[i]] = map.getOrDefault(guess[i], 0) - 1
                }
            }
        }
        return "${a}A${b}B"
    }
}