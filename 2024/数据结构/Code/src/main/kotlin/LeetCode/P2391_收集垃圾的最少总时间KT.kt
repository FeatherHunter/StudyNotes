package LeetCode

class P2391_收集垃圾的最少总时间KT {
    fun garbageCollection(garbage: Array<String>, travel: IntArray): Int {
        var m = false
        var p = false
        var g = false
        var res = 0
        for(i in (garbage.size-1) downTo  0){

            if(garbage[i].contains('M')){
                m = true
                garbage[i].forEach {
                    if(it == 'M'){
                        res++
                    }
                }
                res += if(i > 0)travel[i - 1] else 0
            }else if(m){
                res += if(i > 0)travel[i - 1] else 0
            }

            if(garbage[i].contains('P')){
                p = true
                garbage[i].forEach {
                    if(it == 'P'){
                        res++
                    }
                }
                res += if(i > 0)travel[i - 1] else 0
            }else if(p){
                res += if(i > 0)travel[i - 1] else 0
            }

            if(garbage[i].contains('G')){
                g = true
                garbage[i].forEach {
                    if(it == 'G'){
                        res++
                    }
                }
                res += if(i > 0)travel[i - 1] else 0
            }else if(g){
                res += if(i > 0)travel[i - 1] else 0
            }
        }
        return res
    }
}