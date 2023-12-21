package LeetCode;

public class P278_第一个错误的版本 {
    class VersionControl{
        boolean isBadVersion(int version){
            return true;
        }
    }
    public class Solution extends VersionControl {
        public int firstBadVersion(int n) { // 二分查找
            int l = 1;
            int r = n;
            while (l < r){
                int m = l + (r-l)/2;
                if(isBadVersion(m)){
                    r = m;
                }else{
                    l = m + 1;
                }
            }
            return r;
        }
    }
}
