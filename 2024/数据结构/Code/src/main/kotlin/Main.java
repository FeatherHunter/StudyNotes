import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
//        StreamTokenizer in = new StreamTokenizer(new BufferedReader(new InputStreamReader(System.in))); // 输入结果
//        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(System.out)); // 输出结果
//        while (in.nextToken() != StreamTokenizer.TT_EOF){
//            int m = (int) in.nval;
//            in.nextToken();
//            int n = (int) in.nval;
//            int[][] arr = new int[m][n];
//            for (int i = 0; i < m; i++) {
//                for (int j = 0; j < n; j++) {
//                    in.nextToken();
//                    arr[i][j] = (int) in.nval;
//                }
//            }
//            printWriter.println("result=xxx"); // 输出结果。哈哈
//        }
//        printWriter.flush(); // 刷新
//        printWriter.close(); // 关闭


        BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); // 输入结果
        String line;
        while ((line = in.readLine()) != null){
            String[] arr = line.split(" ");
            // 切分
        }
    }
}
