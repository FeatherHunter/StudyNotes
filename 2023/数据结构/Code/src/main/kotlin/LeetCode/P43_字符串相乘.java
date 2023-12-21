package LeetCode;

public class P43_字符串相乘 {
    /**
     * 59 x 59
     *  9 * 9 = 81 = 1 个位数1
     *
     */
    public String multiply(String num1, String num2) {
        char[] s1 = num1.toCharArray();
        char[] s2 = num2.toCharArray();

        String result = "";
        for (int i = s2.length - 1; i >= 0; i--) {
            int num = s2[i] - '0';
            int last = 0;
            String st = "";
            System.out.println("num1:"+num1+"x s2[i]:"+num);
            for (int j = s1.length - 1; j >= 0; j--) {
                int a = (s1[j] - '0') * num + last;
                st = a % 10 + st;
                last = a / 10;
            }
            if(last > 0){
                st = last + st;
            }
            // 补0
            for (int k = 0; k < s2.length - 1 - i; k++) {
                st += '0';
            }
            result = add(st, result);
            System.out.println("added result:"+result);
        }
        return trim0(result);
    }


    public String add(String a, String b){
        int an = a.length();
        int bn = b.length();
        if(an > bn){
            for (int i = 0; i < an - bn; i++) {
                b = "0" + b;
            }
        }else{
            for (int i = 0; i < bn - an; i++) {
                a = "0" + a;
            }
        }// 一样长了

        System.out.println("a:"+a+" b:"+b);
        int rest = 0; // 进位
        String res = "";
        for (int i = a.length() - 1; i >= 0; i--) {
            int num = (a.charAt(i) - '0') + (b.charAt(i) - '0') + rest;
            res = num % 10 + res;
            rest = num / 10;
        }
        if(rest > 0){
            res = rest + res; // 剩下的数据
        }
        return res;
    }

    public String trim0(String num){
        for (int i = 0; i < num.length(); i++) {
            if(num.charAt(i) != '0'){
                return num.substring(i);
            }
        }
        return "0";
    }
}
