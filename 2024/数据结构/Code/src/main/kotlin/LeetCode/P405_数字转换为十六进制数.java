package LeetCode;

public class P405_数字转换为十六进制数 {
    public static String toHex(int nums) {
        long num = nums;
        if(num == 0) return "0";
        if(num > 0){
            StringBuilder builder = new StringBuilder();
            while ((num) != 0){
                long left = num % 16;
                builder.insert(0, (char)((left <= 9) ? left+'0': (left - 10)+'a'));
                num = num / 16;
            }
            return builder.toString();
        }else{
            char[] neg1 = {'f','f','f','f','f','f','f','f'};
            num = (-num) - 1;
            long[] numa = new long[8];
            int index = 7;
            while (num != 0){
                long left = num % 16;
                numa[index--] = left;
                num = num / 16;
            }
            for (int i = 0; i < 8; i++) {
                long temp = 15 - numa[i];
                neg1[i] = (char) (temp <= 9 ? (temp + '0'): ((temp - 10) + 'a'));
            }

            return new String(neg1);
        }
    }
}
