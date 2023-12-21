package LeetCode;


/**
 * I             1
 * V             5
 * X             10
 * L             50
 * C             100
 * D             500
 * M             1000
 * I 可以放在 V (5) 和 X (10) 的左边，来表示 4 和 9。 IV 4 IX 9
 * X 可以放在 L (50) 和 C (100) 的左边，来表示 40 和 90。 XL 40 XC 90
 * C 可以放在 D (500) 和 M (1000) 的左边，来表示 400 和 900。CD 400 CM 900
 */
public class P12_整数转罗马数字{
    public String intToRoman(int num) {

        int k = num/1000;
        num = num%1000;
        int h = num/100;
        num = num%100;
        int t = num/10;
        num = num%10;
        int n = num;

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < k; i++) {
            builder.append(convert(1000));
        }
        if(h == 4){
            builder.append(convert(400));
        }else if (h == 9){
            builder.append(convert(900));
        }else{

            int t5 = h / 5;
            int tt = h % 5;
            for (int i = 0; i < t5; i++) {
                builder.append(convert(500));
            }
            for (int i = 0; i < tt; i++) {
                builder.append(convert(100));
            }
        }

        if(t == 4){
            builder.append(convert(40));
        }else if (t == 9){
            builder.append(convert(90));
        }else{
            int t5 = t / 5;
            int tt = t % 5;
            for (int i = 0; i < t5; i++) {
                builder.append(convert(50));
            }
            for (int i = 0; i < tt; i++) {
                builder.append(convert(10));
            }
        }

        if(n == 4){
            builder.append(convert(4));
        }else if (n == 9){
            builder.append(convert(9));
        }else{

            int t5 = n / 5;
            int tt = n % 5;
            for (int i = 0; i < t5; i++) {
                builder.append(convert(5));
            }
            for (int i = 0; i < tt; i++) {
                builder.append(convert(1));
            }
        }

        return builder.toString();
    }

    public static String convert(int num){
        switch (num){
            case 1:return "I";
            case 4:return "IV";
            case 5:return "V";
            case 9:return "IX";
            case 10:return "X";
            case 40:return "XL";
            case 50:return "L";
            case 90:return "XC";
            case 100:return "C";
            case 500:return "D";
            case 1000:return "M";
            case 400:return "CD";
            case 900:return "CM";
        }
        return "";
    }
}
