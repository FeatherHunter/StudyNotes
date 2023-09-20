package LeetCode;

public class P374_猜数字大小 {
    public int guessNumber(int n) {

        int l = 1;
        int r = n;
        int m;
        while (l < r){
            m = l + (r-l)/2;
            if(guess(m) <= 0){
                // 说明在左侧, 或者正好是r
                r = m; // 当l靠近r的时候，就是
            }else{
                // l不断靠近r
                l = m + 1;
            }
        }

        return l;
    }

    int guess(int num){
        return 1;
    }
}
