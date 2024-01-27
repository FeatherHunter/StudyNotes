package LeetCode;

public class P504_七进制数 {
    public String convertToBase7(int num) {
        if(num == 0){
            return "0";
        }
        StringBuilder builder = new StringBuilder();
        int temp = Math.abs(num);
        while (temp / 7 != 0 || temp % 7 != 0){
            builder.append(temp % 7);
            temp = temp / 7;
        }
        if(num < 0){
            builder.append("-");
        }
        return builder.reverse().toString();
    }

}
