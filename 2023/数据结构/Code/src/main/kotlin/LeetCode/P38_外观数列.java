package LeetCode;

public class P38_外观数列 {
    /**
     * 1ms 击败 96.42%使用 Java 的用户
     */
    public String countAndSay(int n) {
        if (n == 1) return "1";
        String res = "1";
        for (int i = 2; i <= n; i++) {
            res = convert(res.toCharArray());
        }
        return res;
    }

    public String convert(char[] src) {
        int count = 1;
        char temp = src[0];
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < src.length; i++) {
            if (src[i] != temp) {
                builder.append(count);
                builder.append(temp);
                temp = src[i];
                count = 1;
            } else {
                count++;
            }
        }
        builder.append(count);
        builder.append(temp);
        return builder.toString();
    }

}
