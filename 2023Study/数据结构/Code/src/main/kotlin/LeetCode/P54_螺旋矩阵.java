package LeetCode;

import javax.rmi.CORBA.Util;
import java.util.ArrayList;
import java.util.List;

/**
 * 0ms 击败 100.00%使用 Java 的用户
 */
public class P54_螺旋矩阵 {

    int jadd = 0; // 1-向右 -1向左 0不变
    int iadd = 0; // 1-向下 -1向上 0不变

    public List<Integer> spiralOrder(int[][] matrix) {
        jadd = 1; //
        iadd = 0;// default 向右
        List<Integer> ans = new ArrayList<>();
        int i = 0;
        int j = 0;
        int R = matrix.length;
        int C = matrix[0].length;
        while (true){
            ans.add(matrix[i][j]);
            matrix[i][j] = Integer.MIN_VALUE; // 非法
            int ti = i+iadd;
            int tj = j+jadd;
            int count = 0;
            // 碰壁或者遇到非法位置
            while (ti < 0 || ti >= R || tj < 0 || tj >= C || (matrix[ti][tj] == Integer.MIN_VALUE)){
                switchStatus();
                ti = i+iadd;
                tj = j+jadd;
                count++;
                //四处碰壁 走投无路
                if(count > 4){
                    return ans;
                }
            }
            i = ti;
            j = tj;
        }
    }

    public void switchStatus(){
        // 向右
        if(jadd == 1 && iadd == 0){
            jadd = 0;
            iadd = 1;//向下
        }
        // 向下
        else if(jadd == 0 && iadd == 1){
            jadd = -1;
            iadd = 0;//向左
        }
        // 向左
        else if(jadd == -1 && iadd == 0){
            jadd = 0;
            iadd = -1;//向上
        }
        //向上
        else if(jadd == 0 && iadd == -1){
            jadd = 1;
            iadd = 0;//向右
        }
    }
}
