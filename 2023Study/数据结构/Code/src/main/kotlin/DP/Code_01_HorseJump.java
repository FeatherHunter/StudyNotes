package DP;

import java.util.Arrays;

public class Code_01_HorseJump {
    /**
     * 中国象棋：x = 9(宽度)，y = 10（宽度）
     * 马🐎从（0，0）出发经过k步到（a,b）的方案有多少？
     * */
    // 经过k步骤到x,y点的方案，最少有多少？
    public static int process(int x, int y, int step){
        if(x < 0 || x > 8 || y < 0 || y > 9){
            return 0;
        }
        if(step == 0){
            if(x == 0 && y == 0){
                return 1;
            }else{
                return 0;
            }
        }

        return process(x - 2, y - 1, step - 1)
                + process(x + 2, y - 1, step - 1)
                + process(x - 1, y - 2, step - 1)
                + process(x + 1, y - 2, step - 1)
                + process(x - 2, y + 1, step - 1)
                + process(x + 2, y + 1, step - 1)
                + process(x - 1, y + 2, step - 1)
                + process(x + 1, y + 2, step - 1);

    }
    public static int processDp(int x, int y, int step){

        if(x < 0 || x > 8 || y < 0 || y > 9 || step < 0){
            return 0;
        }

        int[][][] help = new int[9][10][step + 1];

        help[0][0][0] = 1;

        for(int s = 1; s <= step; s++){
            for(int i = 0; i < 9; i++){
                for(int j = 0; j < 10; j++){
                        help[i][j][s] =  getValue(help,i - 2, j - 1, s - 1)
                                + getValue(help,i + 2, j - 1, s - 1)
                                + getValue(help,i - 1, j - 2, s - 1)
                                + getValue(help,i + 1, j - 2, s - 1)
                                + getValue(help,i - 2, j + 1, s - 1)
                                + getValue(help,i + 2, j + 1, s - 1)
                                + getValue(help,i - 1, j + 2, s - 1)
                                + getValue(help,i + 1, j + 2, s - 1);
                }
            }
        }

        return help[x][y][step];
    }

    public static int getValue(int[][][] help, int x, int y, int s){
        if(x < 0 || x > 8 || y < 0 || y > 9){
            return 0;
        }
        return help[x][y][s];
    }
}
