package LeetCode;

/**
 * 0ms
 * 击败 100.00%使用 Java 的用户
 */
public class P171_Excel表列序号 {
    // 不涉及到数组操作，能提高1ms性能
    public int titleToNumber(String columnTitle) {
        int res = 0;
        for (int i = 0; i < columnTitle.length(); i++) {
            res = res * 26 + (columnTitle.charAt(i) - 'A' + 1);
        }
        return res;
    }
}
