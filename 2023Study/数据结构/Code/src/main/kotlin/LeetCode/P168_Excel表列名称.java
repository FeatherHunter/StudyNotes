package LeetCode;

public class P168_Excel表列名称 {
    /**
     * 0ms击败 100.00%使用 Java 的用户
     * StringBuilder提高了很多性能
     *
     * 关联题目：171. Excel 表列序号
     */
    public String convertToTitle(int columnNumber) {
        StringBuilder builder = new StringBuilder();
        while (columnNumber != 0){
            columnNumber--; // A~Z对应1~26，但是需要对齐0~25，所以需要-1
            int rest = columnNumber % 26;
            builder.insert(0, (char)((rest) + 'A'));
            columnNumber = columnNumber / 26;
        }
        return builder.toString(); // 提高性能
    }
}
